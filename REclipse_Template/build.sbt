name := "reclipse-template"
organization := "com.example"
version := "0.0.0"

scalaVersion := "2.11.2"

libraryDependencies ++= Seq(
  "de.tuda.stg" %% "rescala" % "0.0.0",
  "de.tuda.stg" %% "reswing" % "0.0.0",
  "de.tuda.stg.reclipse" %% "rescala-logger" % "0.1"
)

resolvers += Resolver.bintrayRepo("m1c3", "maven")