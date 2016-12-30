package com.malsolo.akkainaction.fsm

import akka.actor.{ActorSystem, Props}
import akka.actor.FSM.{CurrentState, SubscribeTransitionCallBack, Transition}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}
import concurrent.duration._


class InventoryWithTimerTest extends TestKit(ActorSystem("InventoryTest"))
  with WordSpecLike with BeforeAndAfterAll with MustMatchers with ImplicitSender {

  override def afterAll(): Unit = {
    system terminate
  }

  "Inventory" must {
    "follow the flow" in {

      val publisher = TestProbe()
      val inventory = system.actorOf(
        Props(new InventoryWithTimer(publisher.ref)))
      val stateProbe = TestProbe()
      val replyProbe = TestProbe()

      inventory ! new SubscribeTransitionCallBack(stateProbe.ref)
      stateProbe.expectMsg(
        new CurrentState(inventory, WaitForRequests))

      //start test
      inventory ! new BookRequest("context1", replyProbe.ref)
      stateProbe.expectMsg(
        new Transition(inventory, WaitForRequests, WaitForPublisher))
      publisher.expectMsg(PublisherRequest)
      stateProbe.expectMsg(6 seconds,
        new Transition(inventory, WaitForPublisher, WaitForRequests))
      stateProbe.expectMsg(
        new Transition(inventory, WaitForRequests, WaitForPublisher))

      system.stop(inventory)
    }

  }


}
