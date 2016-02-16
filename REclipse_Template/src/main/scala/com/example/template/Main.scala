package com.example.template

import rescala._
import makro.SignalMacro.{ SignalM => Signal }
import de.tuda.stg.reclipse.rescala.REScalaLogger

object Main extends App {
  
  rescala.ReactiveEngine.log = new REScalaLogger
  
  val say = Var("");
  val shout = Signal { say().toUpperCase() }
  
  shout.changed += { println(_) }
  
  say() = "Hello World!"
}