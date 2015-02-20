package de.tu_darmstadt.stg.reclipse.logger;

import java.io.Serializable;

/**
 * Simple class which includes the information where a breakpoint has to be set.
 */
public class BreakpointInformation implements Serializable {

  private static final long serialVersionUID = 2189737304729216736L;

  /**
   * The full path to the respective source file.
   *
   * Example: ./src/de/tu_darmstadt/stg/reclipse/examples/FibonacciExample.scala
   */
  private final String sourcePath;

  /**
   * The full type name of the respective class.
   *
   * Example: de.tu_darmstadt.stg.reclipse.examples.FibonacciExample$
   */
  private final String className;

  /**
   * The line number in which the breakpoint has to be set.
   *
   * Example: 123
   */
  private final int lineNumber;

  public BreakpointInformation(final String theSourcePath, final String theClassName, final int theLineNumber) {
    sourcePath = theSourcePath;
    className = theClassName;
    lineNumber = theLineNumber;
  }

  public String getSourcePath() {
    return sourcePath;
  }

  public String getClassName() {
    return className;
  }

  public int getLineNumber() {
    return lineNumber;
  }

}
