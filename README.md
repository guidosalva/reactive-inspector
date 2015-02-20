## Eclipse Tree Viewer Plugin

### Prerequisites

* Scala IDE for Eclipse
* Plug-in Development Environment (PDE)
* Zest

### Installation

1. Set up the Scala IDE for Eclipse.
2. Install both the PDE and Zest modules in Eclipse.
3. Import this plugin into the workspace.
4. Add the local "org.eclipse.jdt.core_XX.jar" library from the "plugins/" directory of Eclipse to the build path.
5. Done!

### How to use the Tree Viewer Plugin with existing projects

Once the project is set up, it can be used to start an Eclipse application in which the plugin is active. In this Eclipse application, perform the following steps to use the tree viewer.

* Make sure the projects "REClipse_LoggerInterface" and "REClipse_REScala" are imported and work without conflicts.
* Add `rescala.ReactiveEngine.log = new REScalaLogger` to every project in order to allow the tree viewer plugin to communicate with the debugger.
