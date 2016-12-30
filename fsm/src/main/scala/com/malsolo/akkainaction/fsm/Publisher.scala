package com.malsolo.akkainaction.fsm

import akka.actor.Actor
import Math.min

class Publisher(totalNrBooks: Int, nrBooksPerRequest: Int) extends Actor {
  var nrLeft = totalNrBooks
  def receive = {
    case PublisherRequest => {
      if (nrLeft == 0) {
        sender ! BookSupplySoldOut
      }
      else {
        val supply = min(nrBooksPerRequest, nrLeft)
        nrLeft -= supply
        sender ! new BookSupply(supply)
      }
    }


  }
}
