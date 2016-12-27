package com.malsolo.akkainaction.routing

import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}
import scala.concurrent.duration._

class StateRoutingTest extends TestKit(ActorSystem("StateRountingTest")) with WordSpecLike with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    system terminate
  }

  "The Router" must {
    "routes depending on state" in {
      val normalFlowProbe = TestProbe()
      val cleanupProbe = TestProbe()
      val router = system.actorOf(Props(new SwitchRouter(normalFlowProbe.ref, cleanupProbe.ref)))

      val msg = "message"
      router ! msg

      cleanupProbe.expectMsg(msg)
      normalFlowProbe.expectNoMsg(1 second)

      router ! RouteStateOn

      router ! msg

      cleanupProbe.expectNoMsg(1 second)
      normalFlowProbe.expectMsg(msg)

      router ! RouteStateOff

      router ! msg

      cleanupProbe.expectMsg(msg)
      normalFlowProbe.expectNoMsg(1 second)
    }
  }

}
