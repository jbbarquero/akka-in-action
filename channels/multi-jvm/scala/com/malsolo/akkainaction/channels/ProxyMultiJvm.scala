package com.malsolo.akkainaction.routing

import akka.remote.testkit.{MultiNodeConfig, MultiNodeSpecCallbacks}

trait STMultiNodeSpec extends MultiNodeSpecCallbacks with WordSpecLike with MustMatchers with BeforeAndAfterAll {

  override def beforeAll() = multiNodeSpecBeforeAll()

  override def afterAll() = multiNodeSpecAfterAll()
}

object ReliableProxySampleConfig extends MultiNodeConfig {
  val client = role("Client")
  val server = role("Server")
  testTransport(on = true)
}

class ReliableProxySampleSpecMultiJvmNode1 extends ReliableProxySample
class ReliableProxySampleSpecMultiJvmNode2 extends ReliableProxySample

import akka.remote.transport.ThrottlerTransportAdapter.Direction
import scala.concurrent.duration._
import concurrent.Await
import akka.contrib.pattern.ReliableProxy

class ReliableProxySample extends MultiNodeSpec(ReliableProxySampleConfig) with STMultiNodeSpec with ImplicitSender {

  import ReliableProxySampleConfig._

  def initialParticipants = roles.size

  "A MultiNodeSample" must {

    "wait for all nodes to enter a barrier" in {
      enterBarrier("startup")
    }

    "send to and receive from a remote node" in {

      runOn(client) {
        enterBarrier("deployed")
        val pathToEcho = node(server) / "user" / "echo"
        val echo = system.actorSelection(pathToEcho)
        val proxy = system.actorOf(ReliableProxy.props(pathToEcho, 500.millis), "proxy")

        //Actual test

        //Tests proxy under normal conditions
        proxy ! "message1"
        expectMsg("message1")
        //Turns off communication between the two nodes
        Await.ready(testConductor.blackhole( client, server, Direction.Both),1 second)

        //Sends message using both references
        echo ! "DirectMessage"
        proxy ! "ProxyMessage"
        expectNoMsg(3 seconds)

        //Restores communication
        Await.ready(testConductor.passThrough( client, server, Direction.Both), 1 second)

        //Message sent using the proxy is received
        expectMsg("ProxyMessage")

        //Testing messages sent directly to echo actor are received when communication is restored
        echo ! "DirectMessage2"
        expectMsg("DirectMessage2")
      }

      runOn(server) {
        system.actorOf(Props(new Actor {
          def receive = {
            case msg: AnyRef => {
              sender() ! msg
            }
          }
        }), "echo")
        enterBarrier("deployed")
      }

      enterBarrier("finished")
    }

  }

}