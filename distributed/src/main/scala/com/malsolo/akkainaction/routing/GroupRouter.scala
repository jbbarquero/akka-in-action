package com.malsolo.akkainaction.routing

import akka.actor.{Actor, ActorRef, Props, Terminated}
import akka.actor.Actor.Receive

class GetLicenseCreator(numberOfActors: Int, nextStep: ActorRef) extends Actor {

  override def preStart() = {
    super.preStart()
    (0 until numberOfActors).map{ number =>
      val child = context.actorOf(Props(new GetLicense(nextStep)), "GetLicense" + number)
      context.watch(child)
    }
  }

  def receive = {
    case Terminated(child) => {
      val newChild = context.actorOf(Props(new GetLicense(nextStep)), child.path.name)
      context.watch(newChild)
    }

  }
}

class GroupRouter {

}