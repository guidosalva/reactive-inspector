package de.tu_darmstadt.stg.reclipse.graphview.model;

import org.eclipse.core.runtime.IPath;

public interface ISessionConfiguration {

  public IPath getDatabaseFilesDir();

  public boolean isLogging();
}
