scalaVersion := "2.12.1"

scalacOptions := Seq("-unchecked", "-deprecation"
  ,
//  "-Xlint",
  "-Ywarn-unused",
  "-Ywarn-dead-code",
  "-Xfatal-warnings",
  "-feature",
  "-language:_"
)
