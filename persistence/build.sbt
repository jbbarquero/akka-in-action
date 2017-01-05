name := "Akka in action, Persistence"

version := "0.0.1"

organization := "com.malsolo"

parallelExecution in Test := false

fork := true

libraryDependencies ++= {
  val akkaVersion = "2.4.16"
  val akkaHttpVersion = "10.0.1"
  val commonsioVersion = "2.5"
  val logbackVersion = "1.1.8"
  Seq(
    "com.typesafe.akka"         %%  "akka-actor"                          % akkaVersion,
    "com.typesafe.akka"         %%  "akka-slf4j"                          % akkaVersion,
    "ch.qos.logback"            %   "logback-classic"                     % logbackVersion,


    "com.typesafe.akka"         %%  "akka-persistence"                    % akkaVersion,
    "com.typesafe.akka"         %%  "akka-persistence-query-experimental" % akkaVersion,
    "org.iq80.leveldb"          %   "leveldb"                             % "0.7",
    "org.fusesource.leveldbjni" %   "leveldbjni-all"                      % "1.8",

    "com.typesafe.akka"         %%  "akka-http-core"                      % akkaHttpVersion,
    "com.typesafe.akka"         %%  "akka-http"                           % akkaHttpVersion,
    "com.typesafe.akka"         %%  "akka-http-spray-json"                % akkaHttpVersion,
    "com.typesafe.akka"         %%  "akka-http-xml"                       % akkaHttpVersion,

    "org.scalatest"             %%  "scalatest"                           % "3.0.0"           % "test",
    "com.typesafe.akka"         %%  "akka-testkit"                        % akkaVersion       % "test",
    "com.typesafe.akka"         %%  "akka-multi-node-testkit"             % akkaVersion       % "test",

    "commons-io"                %   "commons-io"                          % commonsioVersion

  )
}
