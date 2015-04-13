lazy val root = (project in file(".")).
  settings(
    organization := "de.tu_darmstadt.stg.reclipse",
    name := "rescala-logger",
	version := "0.0.0",
	
    scalaVersion := "2.11.2",
	
	resolvers += Resolver.mavenLocal,
	
    libraryDependencies ++= Seq(
	  "de.tuda.stg" % "rescala_2.11" % "0.0.0",
	  "de.tu_darmstadt.stg.reclipse" % "logger-interface" % "0.1-SNAPSHOT"
	)
  )