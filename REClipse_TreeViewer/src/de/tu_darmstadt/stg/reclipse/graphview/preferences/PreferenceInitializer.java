package de.tu_darmstadt.stg.reclipse.graphview.preferences;

import de.tu_darmstadt.stg.reclipse.graphview.Activator;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

  @Override
  public void initializeDefaultPreferences() {
    final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
    store.setDefault(PreferenceConstants.UPDATE_INTERVAL, 500);
    store.setDefault(PreferenceConstants.EVENT_LOGGING, false);
  }
}
