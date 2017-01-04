package com.malsolo.akkainaction.streams.flow

import akka.stream.{ActorMaterializer, IOResult}
import akka.util.ByteString
import com.malsolo.akkainaction.streams.basic.FileArg
import com.typesafe.config.ConfigFactory

import scala.concurrent.Future
import java.nio.file.StandardOpenOption._

import akka.NotUsed
import akka.actor.ActorSystem
import com.malsolo.akkainaction.streams.flow.LogStreamProcessor.LogParseException
import spray.json._


object EventFilter extends App with EventMarshalling {
  val config = ConfigFactory.load()
  val maxLine = config.getInt("log-stream-processor.max-line")

  if (args.length != 3) {
    System.err.println("Provide args: input-file output-file state")
    System.exit(1)
  }

  val inputFile = FileArg.shellExpanded(args(0))
  val outputFile = FileArg.shellExpanded(args(1))

  val filterState = args(2) match {
    case State(state) => state
    case unknown =>
      System.err.println(s"Unknow state $unknown, exiting.")
      System.exit(1)
  }

  import akka.stream.scaladsl._

  val source: Source[ByteString, Future[IOResult]] = FileIO.fromPath(inputFile)
  val sink: Sink[ByteString, Future[IOResult]] = FileIO.toPath(outputFile, Set(CREATE, WRITE, APPEND))

  val frame: Flow[ByteString, String, NotUsed] = Framing.delimiter(ByteString(System.lineSeparator), maxLine).map(_.decodeString("UTF8"))

//  import akka.stream.ActorAttributes
//  import akka.stream.Supervision
//
//  val decider: Supervision.Decider = {
//    case e: LogParseException =>
//      System.err.println(s"Trata de arrancarlo, Carlos, ¡por Dios!: ${e.getMessage}")
//      Supervision.resume
//    case _ =>
//      System.err.println("La jodimos")
//      Supervision.stop
//  }

  val parse: Flow[String, Event, NotUsed] = Flow[String].map(LogStreamProcessor.parseLineEx).collect { case Some(e) => e}
//    .withAttributes(ActorAttributes.supervisionStrategy(decider))


  val filter: Flow[Event, Event, NotUsed] = Flow[Event].filter(_.state == filterState)

  val serialize: Flow[Event, ByteString, NotUsed] = Flow[Event].map(event => ByteString(event.toJson.compactPrint + System.lineSeparator))

  implicit val system = ActorSystem()
  implicit val ec = system.dispatcher

  import akka.stream.Supervision
  val graphDecider: Supervision.Decider = {
    case e: LogParseException =>
      System.err.println(s"Error parsing log: ${e.getMessage}. Let's continue.")
      Supervision.resume
    case _ =>
      System.err.println(s"Unexpected error processing log. Let's stop.")
      Supervision.stop
  }

  import akka.stream.ActorMaterializerSettings
  implicit val materializer = ActorMaterializer(
    ActorMaterializerSettings(system)
      .withSupervisionStrategy(graphDecider)
  )

  val composedFlow: Flow[ByteString, ByteString, NotUsed] = frame.via(parse).via(filter).via(serialize)

  val runnableGraph: RunnableGraph[Future[IOResult]] = source.via(composedFlow).toMat(sink)(Keep.right)

  runnableGraph.run().foreach { result =>
    println(s"Wrote ${result.count} bytes to '$outputFile'.")
    system.terminate()
  }

}
