package de.tu_darmstadt.stg.reclipse.graphview.model.persistence;

/**
 * Interface which has to be implemented by classes which want to be notified on
 * changes of the dependency graph history.
 */
public interface DependencyGraphHistoryChangedListener {

  public void dependencyGraphHistoryChanged();

}
