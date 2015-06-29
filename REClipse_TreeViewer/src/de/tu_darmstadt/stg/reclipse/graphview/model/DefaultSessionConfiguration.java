package de.tu_darmstadt.stg.reclipse.graphview.model;

import de.tu_darmstadt.stg.reclipse.graphview.Activator;
import de.tu_darmstadt.stg.reclipse.graphview.Properties;
import de.tu_darmstadt.stg.reclipse.graphview.preferences.PreferenceConstants;

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
    final String propertyValue = Activator.getDefault().getProperty(Properties.SUSPEND_ON_SESSION_START);

    if (propertyValue == null) {
      return false;
    }

    return Boolean.parseBoolean(propertyValue);
  }
}
