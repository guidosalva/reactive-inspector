package de.tu_darmstadt.stg.reclipse.rescala

import rescala._
import scala.reflect.runtime.universe._
import java.io.File
import scala.Option.option2Iterable

/**
 * Reads the source files in order to extract the variable names, because it is not possible to get variable names via reflection
 * (see e.g. https://stackoverflow.com/questions/744226/java-reflection-how-to-get-the-name-of-a-variable).
 *
 * The author of the original code of this file is Gerold Hintz. See https://github.com/allprojects/tools-for-RP
 */
object SrcReader {

  /**
   * This method should be called EXACTLY when a reactive is created.
   *  By inspecting the stack trace and source files, it determines the variable name of the current object
   */
  def getVarName(reactive: Reactive) = varNames.getOrElseUpdate(reactive, createVarName)
  val varNames = new scala.collection.mutable.HashMap[Reactive, String]

  private def createVarName: String = {
    Thread.currentThread().getStackTrace().filterNot { s =>
      val c = s.getClassName
      c.startsWith("java") || c.startsWith("scala") || c.startsWith("rescala") || c.startsWith("de.tu_darmstadt.stg.reclipse.rescala")
    }.headOption match {
      case Some(trace) =>
        getVarName(trace.getFileName(), trace.getClassName(), trace.getLineNumber())
      case None => "?"
    }
  }

  var sourceFolder = REScalaLogger.defaultSourceFolder
  def setSourceFolder(s: String) { sourceFolder = s }

  lazy val sourceFiles = findSourceFiles(new File(sourceFolder))
  private def findSourceFiles(path: File): List[File] = {
    val files = path.listFiles.toList
    val recursive = (files.filter { _.isDirectory }.flatMap { findSourceFiles(_) })
    val here = files.filterNot { _.isDirectory }.filter(_.getName().endsWith(".scala"))
    here ::: recursive
  }

  private def getVarName(filename: String, pathhint: String, linenum: Int): String = {
    val file = getFile(filename, pathhint)
    if (file.isEmpty) return "?"
    val varnames = getFileVarnames(file.get)
    varnames.getOrElse(linenum, "?")
  }

  private def getFile(filename: String, pathhint: String): Option[File] = {
    val candidates = sourceFiles.filter(_.getName().endsWith(filename))
    candidates.size match {
      case 0 => None
      case 1 => Some(candidates.head)
      case _ =>
        val refset = pathhint.split('.').toSet
        val best = candidates.maxBy(
          _.getPath().stripSuffix(filename).split("[/\\\\]").toSet.intersect(refset).size)
        Some(best)
    }
  }

  val fileVarnames = new scala.collection.mutable.HashMap[String, Map[Int, String]]
  private def getFileVarnames(file: File): Map[Int, String] = {
    fileVarnames.getOrElseUpdate(file.getPath(), parseFile(file))
  }

  val varRegex = new scala.util.matching.Regex("^\\s*(?:val|var|def)\\s+(\\w+)(?::.*)?\\s+=", "varname")
  private def parseFile(file: File): Map[Int, String] = {
    val lines = scala.io.Source.fromFile(file).getLines
    val vardefs = for {
      (line: String, i: Int) <- lines.zipWithIndex
      vardef <- varRegex.findFirstMatchIn(line)
    } yield (i + 1, vardef.group("varname"))
    vardefs.toMap
  }

}
