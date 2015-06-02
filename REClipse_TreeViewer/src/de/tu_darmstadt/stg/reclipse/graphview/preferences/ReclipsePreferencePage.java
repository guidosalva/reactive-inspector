package de.tu_darmstadt.stg.reclipse.graphview.preferences;

import de.tu_darmstadt.stg.reclipse.graphview.Activator;
import de.tu_darmstadt.stg.reclipse.graphview.Texts;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ReclipsePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

  public ReclipsePreferencePage() {
    super(GRID);
    setPreferenceStore(Activator.getDefault().getPreferenceStore());
    setDescription(Texts.Pref_Description);
  }

  @Override
  public void init(final IWorkbench arg0) {
    // nothing to do
  }

  /**
   * Creates the field editors. Field editors are abstractions of the common GUI
   * blocks needed to manipulate various types of preferences. Each field editor
   * knows how to save and restore itself.
   */
  @Override
  public void createFieldEditors() {
    addField(new IntegerFieldEditor(PreferenceConstants.UPDATE_INTERVAL, Texts.Pref_UpdateInterval, getFieldEditorParent()));
    addField(new BooleanFieldEditor(PreferenceConstants.EVENT_LOGGING, Texts.Pref_EventLogging, getFieldEditorParent()));
  }
}