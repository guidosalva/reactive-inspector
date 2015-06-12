name := "logger-interface"

organization := "de.tu_darmstadt.stg.reclipse"

version := "0.1-SNAPSHOT"

// Do not append Scala versions to the generated artifacts
crossPaths := false

// This forbids including Scala related libraries into the dependency
autoScalaLibrary := false

EclipseKeys.projectFlavor := EclipseProjectFlavor.Java