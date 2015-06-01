package de.tu_darmstadt.stg.reclipse.graphview.model;

import de.tu_darmstadt.stg.reclipse.graphview.Activator;

import org.eclipse.core.runtime.IPath;

public class DefaultSessionConfiguration implements ISessionConfiguration {

  @Override
  public IPath getDatabaseFilesDir() {
    return Activator.getDefault().getStateLocation().append("sessions"); //$NON-NLS-1$
  }

  @Override
  public boolean isLogging() {
    return false; // TODO add plug-in property for logging
  }
}
