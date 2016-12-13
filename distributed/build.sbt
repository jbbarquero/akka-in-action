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

    "org.scalatest" %% "scalatest" % "3.0.0" % "test"
  )
}

scalacOptions := Seq("-unchecked", "-deprecation")

