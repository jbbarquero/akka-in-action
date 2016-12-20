name := "Akka in action, manual clone"

version := "0.0.1"

scalaVersion := "2.12.0"

libraryDependencies ++= {
  val akkaVersion = "2.4.12"
  val akkaHTTPVersion = "10.0.0"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-remote" % akkaVersion,
    "com.typesafe.akka" %% "akka-multi-node-testkit" % akkaVersion,

    "com.typesafe.akka" %% "akka-http-core" % akkaHTTPVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHTTPVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHTTPVersion,

    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "ch.qos.logback" % "logback-classic" % "1.1.3",

    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
    "org.scalatest" %% "scalatest" % "3.0.0" % "test"
  )
}

scalacOptions := Seq("-unchecked", "-deprecation")

