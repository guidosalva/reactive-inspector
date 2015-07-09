## Eclipse Tree Viewer Plugin

### Prerequisites

* Scala IDE for Eclipse
* Plug-in Development Environment (PDE) (should be already included in Scala IDE)

### Installation

1. Set up the Scala IDE for Eclipse.
2. Import the REClipse_TreeViewer project into workspace.
3. Done!

### How to use the Tree Viewer Plugin with existing projects

Once the project is set up, the plugin can be used by starting an Eclipse application. In this Eclipse application, perform the following steps to use the tree viewer.

* Make sure the projects include "REClipse_REScala" as a dependency.
* Add `rescala.ReactiveEngine.log = new REScalaLogger` to the main method of an application in order to allow the plugin to communicate with the debugger.

SBT dependency for REClipse_REScala:
`"de.tu_darmstadt.stg.reclipse" % "rescala-logger_2.11" % "0.0.0"`
