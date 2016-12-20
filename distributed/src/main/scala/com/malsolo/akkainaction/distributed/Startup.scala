package com.malsolo.akkainaction.distributed

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer

import scala.concurrent.Future
import scala.util.{Failure, Success}

trait Startup extends RequestTimeout {
  def startup(api: Route)(implicit system: ActorSystem) = {
    val host = system.settings.config.getString("http.host")
    val port = system.settings.config.getInt("http.port")
    startHttpServer(api, host, port)
  }

  def startHttpServer(api: Route, host: String, port: Int)(implicit system: ActorSystem) = {
    implicit val ec = system.dispatcher
    implicit val materializer = ActorMaterializer()
    val bindingFuture: Future[ServerBinding] = Http().bindAndHandle(api, host, port)

    val log = Logging(system.eventStream, "go-ticks")
    bindingFuture.map { serverBinding =>
      log.info(s"RestApi bound to ${serverBinding.localAddress} ")
    }.onComplete {
      case Failure(e) =>
        log.error(e, "Failed to bind to {}:{}!", host, port)
        system.terminate()
      case Success(_) =>
        log.warning("Bound to {}:{}!", host, port)
    }
  }

}
