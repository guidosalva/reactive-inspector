package de.tu_darmstadt.stg.reclipse.rescala

import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter
import rescala.Reactive
import rescala.Signal
import rescala.Stamp
import rescala.log.Logging
import java.rmi.registry.LocateRegistry
import java.rmi.registry.Registry
import de.tu_darmstadt.stg.reclipse.logger.RMIConstants
import de.tu_darmstadt.stg.reclipse.logger.RemoteLoggerInterface
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable
import java.rmi.RMISecurityManager
import de.tu_darmstadt.stg.reclipse.logger.BreakpointInformation
import de.tu_darmstadt.stg.reclipse.logger.DependencyGraphHistoryType
import rescala.DepHolder
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariableType
import rescala.Var
import de.tu_darmstadt.stg.reclipse.logger.RemoteSessionInterface

/**
 * Provides some static helper methods for the {@link REScalaLogger} class.
 */
object REScalaLogger {

  val defaultSourceFolder = "./src/"

  private def createReactiveVariable(r: Reactive, historyType: DependencyGraphHistoryType): ReactiveVariable = {
    val id = r.id

    var reactiveVariableType = ReactiveVariableType.SIGNAL
    if (classOf[Var[_]].isAssignableFrom(r.getClass)) {
      reactiveVariableType = ReactiveVariableType.VAR
    }

    var innerTypeSimple: String = null
    var innerTypeFull: String = null
    var valueString: String = null
    if (classOf[Signal[_]].isAssignableFrom(r.getClass)) {
      val s = r.asInstanceOf[Signal[_]]
      if (s.get != null) {
        innerTypeSimple = s.get.getClass.getSimpleName
        innerTypeFull = s.get.getClass.getName
        valueString = s.get.toString
      } else {
        valueString = "null"
      }
    }

    var varTypeSimple = r.getClass.getSimpleName.replace("VarSynt", "Var").replace("SignalSynt", "Signal")
    if (innerTypeSimple != null) {
      varTypeSimple += "[" + innerTypeSimple + "]" //$NON-NLS-1$ //$NON-NLS-2$
    }

    var varTypeFull = r.getClass.getName
    if (innerTypeFull != null) {
      varTypeFull += "[" + innerTypeFull + "]" //$NON-NLS-1$ //$NON-NLS-2$
    }

    val varName = SrcReader.getVarName(r)

    val reVar = new ReactiveVariable(id, reactiveVariableType, -1, historyType, null, true, varTypeSimple, varTypeFull, varName, valueString)

    if (classOf[DepHolder].isAssignableFrom(r.getClass)) {
      val d = r.asInstanceOf[DepHolder]
      if (d.dependents != null) {
        d.dependents.foreach(d => reVar.setConnectedWith(d.id))
      }
    }

    reVar.setAdditionalKeyValue("Level", r.level)

    reVar
  }

}

/**
 * Logs all events of a project to the generic
 *
 * <pre>
 * REClipse_ZestViewer
 * </pre>
 *
 * project via Java RMI.
 *
 * An instance of this class has to be plugged into the logging facility of
 * REScala in the project you want to debug like that:
 *
 * <pre>
 * rescala.ReactiveEngine.log = new REScalaLogger
 * </pre>
 */
class REScalaLogger extends Logging {

  private lazy val remoteLogger: RemoteLoggerInterface = {
    checkSecurityManager()
    
    val registry = LocateRegistry.getRegistry()
    val session = registry.lookup(RMIConstants.REMOTE_REFERENCE_NAME).asInstanceOf[RemoteSessionInterface]
    session.startSession()
  }
  
  private def checkSecurityManager() {
    if (System.getSecurityManager() == null) {
      val policyFileName = getClass().getProtectionDomain().getCodeSource().getLocation().getFile() + "../etc/client.policy"
      System.setProperty("java.security.policy", policyFileName)
      System.setProperty("java.rmi.server.hostname", "127.0.0.1")
      System.setSecurityManager(new RMISecurityManager())
    }
  }

  private def getBreakpointInformation(): BreakpointInformation = {
    val stackTraceElement = Thread.currentThread().getStackTrace().filterNot(s => {
      val c = s.getClassName
      c.startsWith("java") || c.startsWith("scala") || c.startsWith("rescala") || c.startsWith("de.tu_darmstadt.stg.reclipse.rescala")
    }).head
    val className = stackTraceElement.getClassName()
    val fileName = stackTraceElement.getFileName()
    val sourceFiles = SrcReader.sourceFiles.filter(f => f.getPath().endsWith(fileName))
    var sourceFile: String = null
    if (sourceFiles.length > 0) {
      sourceFile = sourceFiles.head.getPath()
    }
    // breakpoint should be set one line after the current line
    val lineNumber = stackTraceElement.getLineNumber() + 1
    new BreakpointInformation(sourceFile, className, lineNumber)
  }

  override def nodeCreated(r: Reactive) {
    val breakpointInformation = getBreakpointInformation()
    val reVar = REScalaLogger.createReactiveVariable(r, DependencyGraphHistoryType.NODE_CREATED)
    remoteLogger.logNodeCreated(reVar, breakpointInformation)
  }

  override def nodeAttached(dependent: Reactive, r: Reactive) {
    val breakpointInformation = getBreakpointInformation()
    val reVar = REScalaLogger.createReactiveVariable(r, DependencyGraphHistoryType.NODE_ATTACHED)
    remoteLogger.logNodeAttached(reVar, dependent.id, breakpointInformation)
  }

  override def nodePulsed(r: Reactive) {
  }

  override def nodeScheduled(r: Reactive) {
    // do nothing for the time being
  }

  override def nodeEvaluationStarted(r: Reactive) {
    val breakpointInformation = getBreakpointInformation()
    val reVar = REScalaLogger.createReactiveVariable(r, DependencyGraphHistoryType.NODE_EVALUATION_STARTED)
    remoteLogger.logNodeEvaluationStarted(reVar, breakpointInformation)
  }

  override def nodeEvaluationEnded(r: Reactive) {
    val breakpointInformation = getBreakpointInformation()
    val reVar = REScalaLogger.createReactiveVariable(r, DependencyGraphHistoryType.NODE_EVALUATION_ENDED)
    remoteLogger.logNodeEvaluationEnded(reVar, breakpointInformation)
  }

  override def nodeEvaluationEndedWithException(r: Reactive, e: Exception) {
    val breakpointInformation = getBreakpointInformation()
    val reVar = REScalaLogger.createReactiveVariable(r, DependencyGraphHistoryType.NODE_EVALUATION_ENDED_WITH_EXCEPTION)
    remoteLogger.logNodeEvaluationEndedWithException(reVar, e, breakpointInformation)
  }

  override def nodeValueSet(r: Reactive) {
    val breakpointInformation = getBreakpointInformation()
    val reVar = REScalaLogger.createReactiveVariable(r, DependencyGraphHistoryType.NODE_VALUE_SET)
    remoteLogger.logNodeValueSet(reVar, breakpointInformation)
  }

  override def nodePropagationStopped(r: Reactive) {
    // do nothing for the time being
  }

  override def logRound(ts: Stamp) {
    // do nothing for the time being
  }

  override def logMessage(s: String) {
    // do nothing for the time being
  }

}