## REclipse Plugin

### Installation

* Install and open Eclipse (the [Scala IDE](http://scala-ide.org) is recommended).
* *Help* -> *Install New Software...*
* Add the REclipse update site:
`https://dl.bintray.com/m1c3/generic`
* Select the just added REclipse update site from the drow-down.
* Install the **REclipse** plugin from the **Reactive Programming** category.

### Create a new project

If you want to create a new project with REclipse support you can use the [REclipse Template](https://github.com/m1c3/reclipse-template) project as a starting point:

`git clone https://github.com/m1c3/reclipse-template.git`

### Use an existing REScala project with REclipse

Add the **REScala Logger** to the dependencies of the *build.sbt*:

`"de.tuda.stg.reclipse" %% "rescala-logger" % "0.1"`

Add a resolver for the REclipse maven repository in the *build.sbt*:

`resolvers += Resolver.bintrayRepo("m1c3", "maven")`

Attach the **REScala Logger** to REScala's logging in the *main* method of your application (or at a place **before** any Var, Signal or Event is created):

`rescala.ReactiveEngine.log = new REScalaLogger`

### How to extend the plugin

#### Prerequisites

* Scala IDE for Eclipse
* Plug-in Development Environment (PDE) (should be already included in Scala IDE)
* Eclipse Test Framework (only for test project)

#### Installation

* Set up the Scala IDE for Eclipse.
* Install the *Eclipse Test Framework* from the Eclipse update site if you want to execute or create tests.
* Clone this project.
* Import the **REClipse_TreeViewer** project into the workspace.
* Import the **REClipse_TreeViewer-tests project** if you want to execute or create tests.
* Done!
