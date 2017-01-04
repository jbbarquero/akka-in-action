package com.malsolo.akkainaction.streams.flow

import akka.NotUsed
import akka.stream.{ActorMaterializer, IOResult}
import akka.stream.scaladsl.{BidiFlow, FileIO, Flow, Framing, JsonFraming, Keep, RunnableGraph, Sink, Source}
import akka.util.ByteString
import com.malsolo.akkainaction.streams.basic.FileArg
import com.typesafe.config.ConfigFactory
import spray.json._

import scala.concurrent.Future
import java.nio.file.StandardOpenOption._

import akka.actor.ActorSystem
import com.malsolo.akkainaction.streams.flow.LogStreamProcessor.LogParseException

object BidiEventFilter extends App with EventMarshalling {
  val config = ConfigFactory load
  val maxLine = config getInt("log-stream-processor.max-line")
  val maxJsonObject = config getInt("log-stream-processor.max-json-object")

  if (args.length != 5) {
    System.err.println("")
    System.exit(1)
  }

  val inputFile = FileArg.shellExpanded(args(2))
  val outputFile = FileArg.shellExpanded(args(3))
  val filterState = args(4) match {
    case State(state) => state
    case unknown =>
      System.err.println(s"Unknown state ${unknown}, exiting.")
      System.exit(2)
  }

  val inFlow: Flow[ByteString, Event, NotUsed] =
    if (args(0).toLowerCase == "json") {
      JsonFraming.objectScanner(maxJsonObject).map(_.decodeString("UTF8").parseJson.convertTo[Event])
    }
    else {
      Framing.delimiter(ByteString(System.lineSeparator), maxLine)
        .map(_.decodeString("UTF8"))
        .map(LogStreamProcessor.parseLineEx)
        .collect { case Some(event) => event }
    }

  val outFlow: Flow[Event, ByteString, NotUsed] =
    if (args(1).toLowerCase == "json") {
      Flow[Event].map(event => ByteString(event.toJson.compactPrint + System.lineSeparator))
    }
    else {
      Flow[Event].map { event =>
        ByteString(LogStreamProcessor.logLine(event))
      }
    }

  val bidiFlow = BidiFlow.fromFlows(inFlow, outFlow)

  val source: Source[ByteString, Future[IOResult]] = FileIO.fromPath(inputFile)

  val sink: Sink[ByteString, Future[IOResult]] = FileIO.toPath(outputFile, Set(CREATE, WRITE, APPEND))

  val filter: Flow[Event, Event, NotUsed] = Flow[Event].filter(_.state == filterState)

  val flow = bidiFlow.join(filter)

  val runnableGraph: RunnableGraph[Future[IOResult]] = source.via(flow).toMat(sink)(Keep.right)

  implicit val system = ActorSystem()
  implicit val ec = system.dispatcher

  import akka.stream.Supervision
  val graphDecider: Supervision.Decider = {
    case _: LogParseException => Supervision.resume
    case _ => Supervision.stop
  }
  import akka.stream.ActorMaterializerSettings
  implicit val materializer = ActorMaterializer(
    ActorMaterializerSettings(system).withSupervisionStrategy(graphDecider)
  )

  runnableGraph.run().foreach { result =>
    println(s"Wrote ${result.count} bytes to ${outputFile}")
    system.terminate()
  }

}
