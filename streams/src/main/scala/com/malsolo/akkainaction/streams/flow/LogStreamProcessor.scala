package com.malsolo.akkainaction.streams.flow

import java.time.ZonedDateTime

object LogStreamProcessor extends EventMarshalling {

  def parseLineEx(line: String): Option[Event] = {
    if (!line.isEmpty) {
      line.split("\\|") match {
        case Array(host, service, state, time, desc, tag, metric) =>
          val t = tag.trim
          val m = metric.trim
          Some(
            Event(host.trim, service.trim,
              state.trim match {
                  case State(s) => s
                    case _ => throw new LogParseException(s"Unexpected state: $line")
              }, ZonedDateTime.parse(time.trim), desc.trim,
              if (t.nonEmpty) Some(t) else None,
              if (m.nonEmpty) Some(m.toDouble) else None
            )
          )
        case Array(host, service, state, time, desc) =>
          Some(
            Event(host.trim, service.trim,
              state.trim match {
                case State(s) => s
                case _ => throw new LogParseException(s"Unexpected state: $line")
              }, ZonedDateTime.parse(time.trim), desc.trim
            )
          )
        case x =>
          throw new LogParseException(s"Failed on line: $line")
      }
    }
    else {
      None
    }
  }

  case class LogParseException(msg: String) extends Exception(msg)

}
