package de.tu_darmstadt.stg.reclipse.graphview.view;

import de.tu_darmstadt.stg.reclipse.graphview.model.DatabaseHelper;
import de.tu_darmstadt.stg.reclipse.graphview.model.DependencyGraphHistoryChangedListener;

import java.awt.Frame;

import javax.swing.JScrollPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class StatisticsView extends ViewPart implements DependencyGraphHistoryChangedListener {

  // ID of the view
  public static final String ID = "de.tu-darmstadt.stg.reclipse.graphview.StatisticsView"; //$NON-NLS-1$

  private StatisticsTable table;

  @Override
  public void createPartControl(final Composite parent) {
    parent.setLayout(new GridLayout(1, true));

    table = new StatisticsTable();
    final JScrollPane scrollPane = new JScrollPane(table);
    table.setFillsViewportHeight(true);

    final Composite frameComposite = new Composite(parent, SWT.EMBEDDED | SWT.BACKGROUND);
    frameComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    final Frame frame = SWT_AWT.new_Frame(frameComposite);

    frame.add(scrollPane);

    DatabaseHelper.getInstance().addDepGraphHistoryChangedListener(this);
  }

  @Override
  public void setFocus() {
  }

  @Override
  public void dependencyGraphHistoryChanged() {
    table.refresh();
  }
}
