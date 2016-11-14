name := "crypto"

version := "1.0"

scalaVersion := "2.12.0"


//resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

//libraryDependencies += "com.typesafe.akka" % "akka-actor" % "2.0.2"

// https://mvnrepository.com/artifact/commons-codec/commons-codec
libraryDependencies += "commons-codec" % "commons-codec" % "1.9"

// for debugging sbt problems
logLevel := Level.Debug

scalacOptions += "-deprecation"