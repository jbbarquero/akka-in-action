package com.malsolo.akkainaction.http

import akka.util.Timeout
import com.typesafe.config.Config


object OrderServiceApp extends App with RequestTimeout {


}

trait RequestTimeout {
  import scala.concurrent.duration._

  def requestTimeout(config: Config): Timeout = {
    val t = config.getString("akka.http.server.request-timeout")
    val d = Duration(t)
    FiniteDuration(d.length, d.unit)
  }


}

