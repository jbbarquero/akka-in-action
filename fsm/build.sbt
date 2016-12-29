name := "Akka in action, FSM"

version := "0.0.1"

libraryDependencies ++= {
	val akkaVersion = "2.4.16"
	val akkaHttpVersion = "10.0.0"
	Seq(
	    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
	    "com.typesafe.akka" %% "akka-remote" % akkaVersion,
	    "com.typesafe.akka" %% "akka-multi-node-testkit" % akkaVersion,

	    "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
	    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
	    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,

	    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
	    "ch.qos.logback" % "logback-classic" % "1.1.3",

	    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
	    "org.scalatest" %% "scalatest" % "3.0.0" % "test"
	)
}
