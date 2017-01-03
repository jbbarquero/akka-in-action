package com.malsolo.akkainaction.streams.basic

import java.nio.file.StandardOpenOption._

import akka.actor.ActorSystem
import akka.stream.scaladsl.{FileIO, RunnableGraph, Sink, Source}
import akka.stream.{ActorMaterializer, IOResult}
import akka.util.ByteString
import com.typesafe.config.ConfigFactory

import scala.concurrent.Future

object StreamingCopy extends App {
  val config = ConfigFactory load
  val maxLine = config getInt("log-stream-processor.max-line")

  if (args.length != 2) {
    System.err.println("Provide args: input-file output-file")
    System.exit(1)
  }

  val inputFile = FileArg.shellExpanded(args(0))
  val outputFile = FileArg.shellExpanded(args(1))

  val source: Source[ByteString, Future[IOResult]] = FileIO.fromPath(inputFile)
  val sink: Sink[ByteString, Future[IOResult]] = FileIO.toPath(outputFile, Set(CREATE, WRITE, APPEND))

  val runnableGraph: RunnableGraph[Future[IOResult]] = source.to(sink)

  implicit val system = ActorSystem()
  implicit val ec = system.dispatcher
  implicit val materializer = ActorMaterializer()

  runnableGraph.run().foreach{ result =>
    println(s"${result.status}, ${result.count} bytes read")
    system.terminate()
  }





}
