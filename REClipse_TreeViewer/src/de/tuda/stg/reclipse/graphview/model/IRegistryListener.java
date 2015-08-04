package de.tuda.stg.reclipse.graphview.model;

import de.tuda.stg.reclipse.graphview.model.querylanguage.ReclipseQuery;

public interface IRegistryListener {

  public void onQueryAdded(ReclipseQuery query);

  public void onQueryRemoved(ReclipseQuery query);
}
