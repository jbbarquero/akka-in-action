package com.malsolo.akkainaction.routing

import java.util.Date

import akka.actor.{ActorSystem, PoisonPill, Props}
import akka.routing.{Broadcast, RoundRobinGroup}
import akka.testkit.{TestKit, TestProbe}
import com.malsolo.akkainaction.filters.ImageProcessing
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}

import scala.concurrent.duration._

class GroupRouterTest extends TestKit(ActorSystem("GroupRouterTest")) with MustMatchers with WordSpecLike with BeforeAndAfterAll {

  override def afterAll() = {
    system terminate
  }

  "The router group" must {
    "manage routees" in {
      val endProbe = TestProbe()

      //val creator = system.actorOf(Props(new GetLicenseCreator(2, endProbe.ref)), "Creator")
      val paths = List(
        "/user/Creator/GeLicense0",
        "/user/Creator/GeLicense1"
      )
      val router = system.actorOf(RoundRobinGroup(paths).props(), "groupRouter")

      router ! Broadcast(PoisonPill)
      Thread.sleep(100)

      val msg = PerformanceRoutingMessage(ImageProcessing.createPhotoString(new Date(), 60, "1234xyz"), None, None)

      router ! msg

      endProbe.expectMsgType[PerformanceRoutingMessage](1 second)
    }
  }

}
