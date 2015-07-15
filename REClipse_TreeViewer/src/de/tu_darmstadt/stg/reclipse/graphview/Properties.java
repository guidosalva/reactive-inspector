package de.tu_darmstadt.stg.reclipse.graphview;

public class Properties {

  public static final String SUSPEND_ON_SESSION_START = "suspendOnSessionStart"; //$NON-NLS-1$
  public static final String SHOW_CLASS_NAME = "showClassName"; //$NON-NLS-1$

  private Properties() {
  }

  public static boolean getBoolean(final String propertyName) {
    final String propertyValue = Activator.getDefault().getProperty(propertyName);

    if (propertyValue == null) {
      return false;
    }

    return Boolean.parseBoolean(propertyValue);
  }

  public static void setBoolean(final String propertyName, final boolean value) {
    final String stringValue = Boolean.toString(value);
    Activator.getDefault().setProperty(propertyName, stringValue);
  }

}
