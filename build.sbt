name := "Akka in action, manual clone"

version := "0.0.1"

scalaVersion := "2.12.0"

lazy val distributed    = project.in(file("distributed"))

lazy val channels	    = project.in(file("channels"))

lazy val fsm		    = project.in(file("fsm"))

lazy val http		    = project.in(file("http"))
