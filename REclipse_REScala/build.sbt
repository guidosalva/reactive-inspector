name := "rescala-logger"

organization := "de.tuda.stg.reclipse"

version := "0.0.0"

scalaVersion := "2.11.2"

libraryDependencies ++= Seq(
	"de.tuda.stg" % "rescala_2.11" % "0.0.0",
	"de.tuda.stg.reclipse" % "logger-interface" % "0.1-SNAPSHOT"
	)