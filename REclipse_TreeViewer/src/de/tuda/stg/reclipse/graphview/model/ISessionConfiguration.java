package de.tuda.stg.reclipse.graphview.model;

import org.eclipse.core.runtime.IPath;

public interface ISessionConfiguration {

  public IPath getDatabaseFilesDir();

  public boolean isEventLogging();

  public boolean isSuspendOnSessionStart();
}
