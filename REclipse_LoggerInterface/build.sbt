name := "logger-interface"
organization := "de.tuda.stg.reclipse"
version := "0.1"

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

resolvers += Resolver.bintrayRepo("m1c3", "maven")

// Do not append Scala versions to the generated artifacts
crossPaths := false

// This forbids including Scala related libraries into the dependency
autoScalaLibrary := false

EclipseKeys.projectFlavor := EclipseProjectFlavor.Java