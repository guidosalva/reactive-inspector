package de.tu_darmstadt.stg.reclipse.logger;

/**
 * Type of an entry in the dependency graph history.
 */
public enum DependencyGraphHistoryType {

  // new dependency is established
  NODE_ATTACHED,
  // new node is created
  NODE_CREATED,
  // node evaluation has been completed
  NODE_EVALUATION_ENDED,
  // node evaluation has been completed with an exception
  NODE_EVALUATION_ENDED_WITH_EXCEPTION,
  // node evaluation has been started
  NODE_EVALUATION_STARTED,
  // node value has been set
  NODE_VALUE_SET

}
