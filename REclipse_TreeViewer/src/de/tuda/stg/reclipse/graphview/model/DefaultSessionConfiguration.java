package de.tuda.stg.reclipse.graphview.model;

import de.tuda.stg.reclipse.graphview.Activator;
import de.tuda.stg.reclipse.graphview.Properties;
import de.tuda.stg.reclipse.graphview.preferences.PreferenceConstants;

import org.eclipse.core.runtime.IPath;

public class DefaultSessionConfiguration implements ISessionConfiguration {

  @Override
  public IPath getDatabaseFilesDir() {
    return Activator.getDefault().getStateLocation().append("sessions"); //$NON-NLS-1$
  }

  @Override
  public boolean isEventLogging() {
    return Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.EVENT_LOGGING);
  }

  @Override
  public boolean isSuspendOnSessionStart() {
    return Properties.getBoolean(Properties.SUSPEND_ON_SESSION_START);
  }
}
