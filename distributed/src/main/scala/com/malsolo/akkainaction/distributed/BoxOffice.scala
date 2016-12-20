package com.malsolo.akkainaction.distributed

import akka.actor.{Actor, ActorRef, Props}
import akka.util.Timeout

object BoxOffice {
  def props(implicit timeout: Timeout) = Props(new BoxOffice)
  def name = "boxOffice"

  case class CreateEvent(name: String, tickets: Int)
  case class GetEvent(name: String)
  case object GetEvents
  case class GetTickets(event: String, tickets: Int)
  case class CancelEvent(name: String)

  case class Event(name: String, ticket: Int)
  case class Events(events: Vector[Event])

  sealed trait EventResponse
  case class EventCreated(event: Event) extends EventResponse
  case object EventExists extends EventResponse

}

class BoxOffice(implicit timeout: Timeout) extends Actor {
  import BoxOffice._

  def createTicketSeller(name: String): ActorRef = {
    context.actorOf(TicketSeller.props(name), name)
  }

  override def receive: Receive = {
    case CreateEvent(name, tickets) =>
      def create() = {
        val eventTickets = createTicketSeller(name)
        val newTickets = (1 to tickets).map { ticketId =>
          TicketSeller.Ticket(ticketId)
        }.toVector
        eventTickets ! TicketSeller.Add(newTickets)
        sender() ! EventCreated(Event(name, tickets))
      }
      context.child(name).fold(create())(_ => sender() ! EventExists)

    case GetTickets(event, tickets)=>
      def notFound() = sender() ! TicketSeller.Tickets(event)
      def buy(child: ActorRef) = child.forward(TicketSeller.Buy(tickets))
      context.child(event).fold(notFound())(buy)

  }
}
