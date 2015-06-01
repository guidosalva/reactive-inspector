package de.tu_darmstadt.stg.reclipse.graphview.model.persistence;

import de.tu_darmstadt.stg.reclipse.logger.DependencyGraphHistoryType;

/**
 * Interface which has to be implemented by classes which want to be notified on
 * changes of the dependency graph history.
 */
public interface IDependencyGraphListener {

  public void onDependencyGraphChanged(DependencyGraphHistoryType type, int pointInTime);

}
