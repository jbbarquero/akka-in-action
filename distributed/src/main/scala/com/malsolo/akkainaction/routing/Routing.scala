package com.malsolo.akkainaction.routing

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorRef}
import com.malsolo.akkainaction.filters.ImageProcessing

import scala.concurrent.duration._

case class SetService(id: String, serviceTime: FiniteDuration)

case class PerformanceRoutingMessage(photo: String, license: Option[String], processedBy: Option[String])

class GetLicense(pipe: ActorRef, initialServiceTime: FiniteDuration = 0 millis) extends Actor {
  var id = self.path.name
  var serviceTime = initialServiceTime

  def receive = {
    case init: SetService => {
      id = init.id
      serviceTime = init.serviceTime
      Thread.sleep(100)
    }
    case msg: PerformanceRoutingMessage => {
      Thread.sleep(serviceTime.toMillis)
      pipe ! msg.copy(license = ImageProcessing.getLicense(msg.photo), processedBy = Some(id))
    }
  }
}


