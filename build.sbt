lazy val root = Project("root", file(".")).aggregate(interface, rescalaLogger)

lazy val interface = Project("logger-interface", file("REClipse_LoggerInterface"))

lazy val rescalaLogger = Project("rescala-logger", file("REClipse_REScala")).dependsOn(interface)