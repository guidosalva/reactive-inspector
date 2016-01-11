package de.tuda.stg.reclipse.graphview.javaextensions;

@FunctionalInterface
public interface ThrowingRunnable<E extends Exception>  {

  void run() throws E;

}
