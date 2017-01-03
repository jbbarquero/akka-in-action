name := "Akka in action HTTP"

version := "0.0.1"

organization := "com.malsolo"

libraryDependencies ++= {
  val akkaVersion = "2.4.16"
  val akkaHttpVersion = "10.0.1"
  Seq(
    "com.typesafe.akka" %%  "akka-actor"   % akkaVersion,
    "com.typesafe.akka" %%  "akka-slf4j"   % akkaVersion,

    "com.typesafe.akka"       %% "akka-http-core"                    % akkaHttpVersion,
    "com.typesafe.akka"       %% "akka-http"                         % akkaHttpVersion,
    "com.typesafe.akka"       %% "akka-http-spray-json"              % akkaHttpVersion,
    "com.typesafe.akka"       %% "akka-http-xml"                     % akkaHttpVersion,

    "org.scalatest"     %%  "scalatest"    % "3.0.0"      % "test"
  )
}
