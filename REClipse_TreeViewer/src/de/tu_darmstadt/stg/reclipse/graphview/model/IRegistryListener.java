package de.tu_darmstadt.stg.reclipse.graphview.model;

import de.tu_darmstadt.stg.reclipse.graphview.model.querylanguage.ReclipseQuery;

public interface IRegistryListener {

  public void onQueryAdded(ReclipseQuery query);

  public void onQueryRemoved(ReclipseQuery query);
}
