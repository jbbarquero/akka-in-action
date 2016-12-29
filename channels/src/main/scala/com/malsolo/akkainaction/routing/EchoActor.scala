package com.malsolo.akkainaction.routing

import akka.actor.Actor

class EchoActor extends Actor {
  def receive = {
    case msg: AnyRef =>
      sender() ! msg
  }
}
