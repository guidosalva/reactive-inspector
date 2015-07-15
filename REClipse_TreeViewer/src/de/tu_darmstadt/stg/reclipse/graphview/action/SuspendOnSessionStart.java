package de.tu_darmstadt.stg.reclipse.graphview.action;

import de.tu_darmstadt.stg.reclipse.graphview.Activator;
import de.tu_darmstadt.stg.reclipse.graphview.Images;
import de.tu_darmstadt.stg.reclipse.graphview.Properties;
import de.tu_darmstadt.stg.reclipse.graphview.Texts;

import org.eclipse.jface.action.Action;

public class SuspendOnSessionStart extends Action {

  public SuspendOnSessionStart() {
    super(Texts.SuspendOnSessionStart_Text, AS_CHECK_BOX);

    setToolTipText(Texts.SuspendOnSessionStart_Tooltip);
    setImageDescriptor(Activator.getImageDescriptor(Images.SUSPEND));
    setChecked(Properties.getBoolean(Properties.SUSPEND_ON_SESSION_START));
  }

  @Override
  public void run() {
    Properties.setBoolean(Properties.SUSPEND_ON_SESSION_START, this.isChecked());
  }
}
