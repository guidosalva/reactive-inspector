package de.tu_darmstadt.stg.reclipse.graphview.view.statistics;

import de.tu_darmstadt.stg.reclipse.graphview.model.DependencyGraphHistoryChangedListener;
import de.tu_darmstadt.stg.reclipse.graphview.model.SessionContext;

import java.awt.Frame;

import javax.swing.JPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.ChartPanel;

public class StatisticsView extends ViewPart implements DependencyGraphHistoryChangedListener {

  // ID of the view
  public static final String ID = "de.tu-darmstadt.stg.reclipse.graphview.StatisticsView"; //$NON-NLS-1$

  private Charts pieCharts;

  private Frame frame;

  @Override
  public void createPartControl(final Composite parent) {
    parent.setLayout(new GridLayout(1, true));

    final Composite frameComposite = new Composite(parent, SWT.EMBEDDED | SWT.BACKGROUND);
    frameComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    frame = SWT_AWT.new_Frame(frameComposite);

    pieCharts = new Charts();
    final ChartPanel typeChartPanel = new ChartPanel(pieCharts.getTypeChart());
    final ChartPanel changeChartPanel = new ChartPanel(pieCharts.getChangeChart());

    final JPanel chartsPanel = new JPanel();
    chartsPanel.setLayout(new java.awt.GridLayout(1, 2));
    chartsPanel.add(typeChartPanel);
    chartsPanel.add(changeChartPanel);

    frame.add(chartsPanel);

    SessionContext.INSTANCE.getDbHelper().addDepGraphHistoryChangedListener(this);
  }

  @Override
  public void setFocus() {
  }

  @Override
  public void dependencyGraphHistoryChanged() {
    pieCharts.refresh();
  }
}
