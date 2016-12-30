package com.malsolo.akkainaction.fsm

import akka.actor.ActorSystem
import akka.agent.Agent

import scala.concurrent.Await
import scala.concurrent.duration._


//import concurrent.ExecutionContext.Implicits.global

case class BookStatistics(val nameBook: String, nrSold: Int)
case class StateBookStatistics(val sequence: Long, books: Map[String, BookStatistics])

class BookStatisticsMgr(system: ActorSystem) {
  implicit val ex = system.dispatcher
  val stateAgent = Agent(new StateBookStatistics(0, Map()))

  def addBookSold(book: String, nrSold: Int): Unit = {
    stateAgent send (oldState => {
      val bookStat = oldState.books.get(book) match {
        case Some(bookState) =>
          bookState.copy(nrSold = bookState.nrSold + nrSold)
        case None =>
          new BookStatistics(book, nrSold)
      }
      oldState.copy(oldState.sequence + 1, oldState.books + (book -> bookStat))
    })
  }

  def addBooksSoldAndReturnNewState(book: String, nrSold: Int): StateBookStatistics = {
    val future = stateAgent alter (oldState => {
      val bookStat = oldState.books.get(book) match {
        case Some(bookState) =>
          bookState.copy(nrSold = bookState.nrSold + nrSold)
        case None =>
          new BookStatistics(book, nrSold)
      }
      oldState.copy(oldState.sequence + 1, oldState.books + (book -> bookStat))
    })
    Await.result(future, 1 second)
  }

  def getStateBookStatistics(): StateBookStatistics = {
    stateAgent.get()
  }

}