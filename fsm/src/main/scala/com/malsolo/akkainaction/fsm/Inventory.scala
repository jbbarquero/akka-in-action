package com.malsolo.akkainaction.fsm

import akka.actor.{Actor, ActorRef, FSM}

//Events
case class BookRequest(context: AnyRef, target: ActorRef)
case class BookSupply(nrBooks: Int)
case object BookSupplySoldOut
case object Done
case object PendingRequests

//States
sealed trait State
case object WaitForRequests extends State
case object ProcessRequest extends State
case object WaitForPublisher extends State
case object SoldOut extends State
case object ProcessSoldOut extends State

case class StateData(numberOfBooksInStore: Int, pendingRequests: Seq[BookRequest])


class Inventory extends Actor with FSM[State, StateData] {

  startWith(WaitForRequests, new StateData(0, Seq()))

  when(WaitForRequests) {
    case Event(request: BookRequest, data: StateData) => {
      val newStateData = data.copy(pendingRequests = data.pendingRequests :+ request)
      if (newStateData.numberOfBooksInStore > 0) {
        goto(ProcessRequest) using newStateData
      }
      else {
        goto(WaitForPublisher) using newStateData
      }
    }
    case Event(PendingRequests, data: StateData) => {
      if (data.pendingRequests.isEmpty) {
        stay
      }
      else if (data.numberOfBooksInStore > 0) {
        goto(ProcessRequest)
      }
      else {
        goto(WaitForPublisher)
      }
    }
  }

  when(WaitForPublisher) {
    case Event(supply: BookSupply, data: StateData) => {
      goto(ProcessRequest) using data.copy(numberOfBooksInStore = supply.nrBooks)
    }
    case Event(BookSupplySoldOut, _) => {
      goto(ProcessSoldOut)
    }
  }

  when(ProcessRequest) {
    case Event(Done, data: StateData) => {
      goto(WaitForRequests) using data.copy(numberOfBooksInStore = -1, pendingRequests = data.pendingRequests.tail)
    }
  }

  when(SoldOut) {
    case Event(request: BookRequest, data: StateData) => {
      goto(ProcessSoldOut) using new StateData(0, Seq(request))
    }
  }

  when(ProcessSoldOut) {
    case Event(Done, data: StateData) => {
      goto(SoldOut) using new StateData(0, Seq())
    }

  }

  whenUnhandled {
    // common code for all states
    case Event(request: BookRequest, data: StateData) => {
      stay using data.copy(pendingRequests = data.pendingRequests :+ request)
    }
    case Event(e, s) => {
      log.warning("received unhandled request {} in state {}/{}", e, stateName, s)
      stay
    }
  }

}
