package com.malsolo.akkainaction.filters

import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

import scala.concurrent.duration._

class PipeAndFilterTest extends TestKit(ActorSystem("PipeAndFilterTest")) with WordSpecLike with BeforeAndAfterAll {

  val timeout = 2 seconds

  override def afterAll(): Unit = {
    system terminate
  }

  "The pipe and filter" must {
    "filter messages inconfiguration 1" in {
      val endProbe = TestProbe()
      val speedFilterRef = system.actorOf(Props(new SpeedFilter(50, endProbe.ref)))
      val licenseFilterRef = system.actorOf(Props(new LicenseFilter(speedFilterRef)))
      val msg = new Photo("1234xyz", 60)

      licenseFilterRef ! msg
      endProbe.expectMsg(msg)

      licenseFilterRef ! new Photo("", 60)
      endProbe.expectNoMsg(timeout)

      licenseFilterRef ! new Photo("1234xyz", 49)
      endProbe.expectNoMsg(timeout)
    }

    "filter messages inconfiguration 2" in {
      val endProbe = TestProbe()
      val licenseFilterRef = system.actorOf(Props(new LicenseFilter(endProbe.ref)))
      val speedFilterRef = system.actorOf(Props(new SpeedFilter(50, licenseFilterRef)))
      val msg = new Photo("1234xyz", 60)

      speedFilterRef ! msg
      endProbe.expectMsg(msg)

      speedFilterRef ! new Photo("", 60)
      endProbe.expectNoMsg(timeout)

      speedFilterRef ! new Photo("1234xyz", 49)
      endProbe.expectNoMsg(timeout)
    }
  }

}
