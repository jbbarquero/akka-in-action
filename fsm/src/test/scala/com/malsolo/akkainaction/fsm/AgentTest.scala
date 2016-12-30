package com.malsolo.akkainaction.fsm

import akka.actor.ActorSystem
import akka.agent.Agent
import akka.testkit.TestKit
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}

import scala.concurrent.Await
import scala.concurrent.duration._

class AgentTest extends TestKit(ActorSystem("AgentTest")) with WordSpecLike with BeforeAndAfterAll with MustMatchers {

  implicit val ec = system.dispatcher
  implicit val timeout = Timeout(3 seconds)

  override def afterAll(): Unit = {
    system.terminate()
  }

  "AgentMgr" must {
    "test" in {
      val bookName = "Akka in Action"
      val mgr = new BookStatisticsMgr(system)
      mgr.addBookSold(bookName, 1)
      mgr.addBookSold(bookName, 1)
      Await.result(mgr.stateAgent.future(), 1 second)
      val book = new BookStatistics(bookName, 2)
      mgr.getStateBookStatistics() must be(new StateBookStatistics(2, Map(bookName -> book)))
    }
    "test alter" in {
      val bookName = "Akka in Action"
      val mgr = new BookStatisticsMgr(system)
      mgr.addBookSold(bookName, 1)
      val state = mgr.addBooksSoldAndReturnNewState(bookName, 1)
      val book = new BookStatistics(bookName, 2)
      state must be(new StateBookStatistics(2, Map(bookName -> book)))
    }
  }

}
