package de.tuda.stg.reclipse.graphview.view.statistics;

import de.tuda.stg.reclipse.graphview.model.ISessionSelectionListener;
import de.tuda.stg.reclipse.graphview.model.SessionContext;
import de.tuda.stg.reclipse.graphview.model.SessionManager;
import de.tuda.stg.reclipse.graphview.model.persistence.IDependencyGraphListener;
import de.tuda.stg.reclipse.logger.DependencyGraphHistoryType;

import java.awt.Frame;
import java.util.Optional;

import javax.swing.JPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.ChartPanel;

public class StatisticsView extends ViewPart implements IDependencyGraphListener, ISessionSelectionListener {

  // ID of the view
  public static final String ID = "de.tuda.stg.reclipse.graphview.StatisticsView"; //$NON-NLS-1$

  private Charts pieCharts;

  private Frame frame;

  @Override
  public void createPartControl(final Composite parent) {
    parent.setLayout(new GridLayout(1, true));

    final SessionManager sessionManager = SessionManager.getInstance();

    final Composite frameComposite = new Composite(parent, SWT.EMBEDDED | SWT.BACKGROUND);
    frameComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    frame = SWT_AWT.new_Frame(frameComposite);

    final Optional<SessionContext> ctx = sessionManager.getSelectedSession();

    if (ctx.isPresent()) {
      onSessionSelected(ctx.get());
    }

    sessionManager.addSessionSelectionListener(this);
  }

  @Override
  public void onSessionSelected(final SessionContext ctx) {
    pieCharts = new Charts(ctx);
    final ChartPanel typeChartPanel = new ChartPanel(pieCharts.getTypeChart());
    final ChartPanel changeChartPanel = new ChartPanel(pieCharts.getChangeChart());

    final JPanel chartsPanel = new JPanel();
    chartsPanel.setLayout(new java.awt.GridLayout(1, 2));
    chartsPanel.add(typeChartPanel);
    chartsPanel.add(changeChartPanel);

    frame.add(chartsPanel);

    ctx.getDbHelper().addDependencyGraphListener(this);
  }

  @Override
  public void onSessionDeselected(final SessionContext ctx) {
    ctx.getDbHelper().removeDependencyGraphListener(this);
    frame.removeAll();
    pieCharts = null;
  }

  @Override
  public void setFocus() {
  }

  @Override
  public void onDependencyGraphChanged(final DependencyGraphHistoryType type, final int pointInTime) {
    if (pieCharts != null) {
      pieCharts.refresh();
    }
  }
}
