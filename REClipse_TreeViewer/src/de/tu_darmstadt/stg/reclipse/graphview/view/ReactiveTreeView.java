package de.tu_darmstadt.stg.reclipse.graphview.view;

import de.tu_darmstadt.stg.reclipse.graphview.Activator;
import de.tu_darmstadt.stg.reclipse.graphview.Texts;
import de.tu_darmstadt.stg.reclipse.graphview.action.Relayout;
import de.tu_darmstadt.stg.reclipse.graphview.action.SaveGraphAsImage;
import de.tu_darmstadt.stg.reclipse.graphview.action.SessionSelect;
import de.tu_darmstadt.stg.reclipse.graphview.action.ShowHeatmap;
import de.tu_darmstadt.stg.reclipse.graphview.action.SuspendOnSessionStart;
import de.tu_darmstadt.stg.reclipse.graphview.action.ZoomIn;
import de.tu_darmstadt.stg.reclipse.graphview.action.ZoomOut;
import de.tu_darmstadt.stg.reclipse.graphview.controller.QueryController;
import de.tu_darmstadt.stg.reclipse.graphview.model.ISessionSelectionListener;
import de.tu_darmstadt.stg.reclipse.graphview.model.SessionContext;
import de.tu_darmstadt.stg.reclipse.graphview.model.SessionManager;
import de.tu_darmstadt.stg.reclipse.graphview.model.persistence.IDependencyGraphListener;
import de.tu_darmstadt.stg.reclipse.graphview.preferences.PreferenceConstants;
import de.tu_darmstadt.stg.reclipse.graphview.view.graph.GraphComponent;
import de.tu_darmstadt.stg.reclipse.graphview.view.graph.TreeViewGraph;
import de.tu_darmstadt.stg.reclipse.logger.DependencyGraphHistoryType;

import java.awt.Frame;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * Base view class containing all the elements which are shown in the
 * "Reactive Tree" view / tab.
 */
public class ReactiveTreeView extends ViewPart implements IDependencyGraphListener, ISessionSelectionListener, IPartListener2 {

  /**
   * The ID of the view as specified by the extension.
   */
  public static final String ID = "de.tu-darmstadt.stg.reclipse.graphview.ReactiveTreeView"; //$NON-NLS-1$

  private static final String[] QUERY_TEMPLATES = new String[] {
    "nodeCreated(<name>)", //$NON-NLS-1$
    "nodeEvaluated(<name>)", //$NON-NLS-1$
    "nodeValueSet(<name>)", //$NON-NLS-1$
    "dependencyCreated(<name>, <name>)", //$NON-NLS-1$
    "evaluationYielded(<name>, <value>)", //$NON-NLS-1$
    "evaluationException(<name>)" //$NON-NLS-1$
  };

  protected final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

  protected Composite graphParent;
  protected Scale slider;
  protected Button autoNavButton;
  protected Button nextPointButton;
  protected Button prevPointButton;
  protected Combo queryTextField;
  protected TreeViewGraph graph;
  protected GraphComponent graphComponent;

  protected long lastUpdate = 0;
  protected ScheduledFuture<?> delayedUpdateTask;
  protected int lastPointInTime = -1;

  protected boolean manualMode = false;

  private final int updateInterval;

  public ReactiveTreeView() {
    updateInterval = Activator.getDefault().getPreferenceStore().getInt(PreferenceConstants.UPDATE_INTERVAL);
  }

  @Override
  public void init(final IViewSite site) throws PartInitException {
    super.init(site);
    site.getPage().addPartListener(this);
  }

  @Override
  public void createPartControl(final Composite parent) {
    parent.setLayout(new GridLayout(1, true));

    graphParent = new Composite(parent, SWT.NONE);
    graphParent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    graphParent.setLayout(new GridLayout());

    final Composite composite = new Composite(graphParent, SWT.EMBEDDED | SWT.BACKGROUND);
    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    graph = new TreeViewGraph();

    // initialize graph component and add it to frame
    graphComponent = new GraphComponent(graph);
    graphComponent.setEnabled(false);
    final Frame graphFrame = SWT_AWT.new_Frame(composite);
    graphFrame.add(graphComponent);

    final Composite navComposite = new Composite(parent, SWT.NONE);
    navComposite.setLayout(new GridLayout(4, false));
    navComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

    slider = new Scale(navComposite, SWT.HORIZONTAL);
    slider.setMinimum(0);
    slider.setIncrement(1);
    slider.setPageIncrement(1);
    slider.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    slider.addListener(SWT.Selection, new Listener() {

      @Override
      public void handleEvent(final Event event) {
        if (event.detail == SWT.NONE) {
          enableManualMode();
          rebuildGraph(slider.getSelection(), false);
        }
      }
    });

    autoNavButton = new Button(navComposite, SWT.TOGGLE);
    autoNavButton.setText("Auto"); //$NON-NLS-1$
    autoNavButton.setSelection(true);
    autoNavButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent e) {
        if (autoNavButton.getSelection()) {
          disableManualMode();
        }
        else {
          enableManualMode();
        }
      }
    });

    prevPointButton = new Button(navComposite, SWT.ARROW | SWT.LEFT);
    prevPointButton.setEnabled(false);
    prevPointButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent e) {
        prevPointInTime();
      }
    });

    nextPointButton = new Button(navComposite, SWT.ARROW | SWT.RIGHT);
    nextPointButton.setEnabled(false);
    nextPointButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent e) {
        nextPointInTime();
      }
    });

    final QueryController queryController = new QueryController(this);
    final Composite queryComposite = new Composite(parent, SWT.NONE);
    queryComposite.setLayout(new GridLayout(4, false));
    queryComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

    queryTextField = new Combo(queryComposite, SWT.DROP_DOWN);
    queryTextField.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    queryTextField.setItems(QUERY_TEMPLATES);

    final Button submitButton = new Button(queryComposite, SWT.PUSH);
    submitButton.addSelectionListener(queryController.new SubmitQueryButtonHandler());
    submitButton.setText(Texts.Query_Submit);
    queryTextField.addTraverseListener(new TraverseListener() {

      @Override
      public void keyTraversed(final TraverseEvent e) {
        if (e.detail == SWT.TRAVERSE_RETURN) {
          submitButton.notifyListeners(SWT.Selection, new Event());
        }
      }
    });

    final Button prevResultButton = new Button(queryComposite, SWT.ARROW | SWT.LEFT);
    prevResultButton.addSelectionListener(queryController.new PrevQueryResultButtonHandler());

    final Button nextResultButton = new Button(queryComposite, SWT.ARROW | SWT.RIGHT);
    nextResultButton.addSelectionListener(queryController.new NextQueryResultButtonHandler());

    createActions();

    final SessionManager sessionManager = SessionManager.getInstance();
    final Optional<SessionContext> ctx = sessionManager.getSelectedSession();

    if (ctx.isPresent()) {
      onSessionSelected(ctx.get());
    }

    sessionManager.addSessionSelectionListener(this);
  }

  private void createActions() {
    // creating the toolbar entries
    getViewSite().getActionBars().getToolBarManager().add(new SessionSelect());
    getViewSite().getActionBars().getToolBarManager().add(new SuspendOnSessionStart());
    getViewSite().getActionBars().getToolBarManager().add(new Relayout(graph));
    getViewSite().getActionBars().getToolBarManager().add(new SaveGraphAsImage(getSite(), graph));
    getViewSite().getActionBars().getToolBarManager().add(new ZoomIn(graphComponent));
    getViewSite().getActionBars().getToolBarManager().add(new ZoomOut(graphComponent));
    getViewSite().getActionBars().getToolBarManager().add(new ShowHeatmap(graph));
  }

  @Override
  public void setFocus() {
    queryTextField.setFocus();
  }

  @Override
  public void onSessionSelected(final SessionContext ctx) {
    manualMode = false;
    lastPointInTime = ctx.getPersistence().getLastPointInTime();

    graph.setSessionContext(ctx);
    ctx.getDbHelper().addDependencyGraphListener(ReactiveTreeView.this);

    rebuildGraph(lastPointInTime, false);
  }

  @Override
  public void onSessionDeselected(final SessionContext ctx) {
    ctx.getDbHelper().removeDependencyGraphListener(this);
    graph.removeSessionContext();

    delayedUpdateTask.cancel(false);
    lastUpdate = 0;
  }

  @Override
  public void onDependencyGraphChanged(final DependencyGraphHistoryType type, final int pointInTime) {
    lastPointInTime = pointInTime;

    // only update if the graph is visible
    if (isVisible()) {
      updateGraph();
    }
  }

  protected void rebuildGraph(final int pointInTime, final boolean highlightChange) {
    if (!isVisible()) {
      return;
    }

    graph.setPointInTime(pointInTime, highlightChange);
  }

  public void updateGraph() {
    if (delayedUpdateTask != null && !delayedUpdateTask.isDone()) {
      // update is already delayed
      return;
    }
    else if (isInUpdateInterval()) {
      deleayUpdate();
    }
    else {
      doUpdateGraph();
    }
  }

  protected void doUpdateGraph() {
    lastUpdate = System.currentTimeMillis();

    Display.getDefault().syncExec(new Runnable() {

      @Override
      public void run() {
        if (slider == null || slider.isDisposed()) {
          return;
        }

        slider.setMaximum(lastPointInTime);

        if (!manualMode) {
          slider.setSelection(lastPointInTime);
          rebuildGraph(lastPointInTime, true);
        }
      }
    });
  }

  private void deleayUpdate() {
    final long delay = updateInterval - (System.currentTimeMillis() - lastUpdate);

    delayedUpdateTask = executorService.schedule(new Runnable() {

      @Override
      public void run() {
        doUpdateGraph();

      }
    }, delay, TimeUnit.MILLISECONDS);
  }

  private boolean isInUpdateInterval() {
    return System.currentTimeMillis() - lastUpdate < updateInterval;
  }

  public void jumpToPointInTime(final int pointInTime) {
    enableManualMode();

    Display.getDefault().syncExec(new Runnable() {

      @Override
      public void run() {
        if (slider.getMaximum() < pointInTime) {
          slider.setMaximum(pointInTime);
        }

        slider.setSelection(pointInTime);

        rebuildGraph(pointInTime, false);
      }
    });
  }

  public int getCurrentSliderValue() {
    if (slider == null || slider.isDisposed()) {
      return -1;
    }
    return slider.getSelection();
  }

  private boolean isVisible() {
    return getSite().getPage().isPartVisible(this);
  }

  protected void enableManualMode() {
    manualMode = true;

    autoNavButton.setSelection(false);
    prevPointButton.setEnabled(true);
    nextPointButton.setEnabled(true);
  }

  protected void disableManualMode() {
    manualMode = false;

    autoNavButton.setSelection(true);
    prevPointButton.setEnabled(false);
    nextPointButton.setEnabled(false);

    updateGraph();
  }

  protected void nextPointInTime() {
    final int pointInTime = slider.getSelection() + 1;

    if (pointInTime <= slider.getMaximum()) {
      slider.setSelection(pointInTime);
      rebuildGraph(pointInTime, true);
    }
  }

  protected void prevPointInTime() {
    final int pointInTime = slider.getSelection() - 1;

    if (pointInTime >= 0) {
      slider.setSelection(pointInTime);
      rebuildGraph(pointInTime, true);
    }
  }

  public String getQueryText() {
    if (queryTextField == null || queryTextField.isDisposed()) {
      return ""; //$NON-NLS-1$
    }
    return queryTextField.getText();
  }

  public void showInformation(final String title, final String message) {
    MessageDialog.openInformation(getSite().getShell(), title, message);
  }

  public void showError(final String title, final String message) {
    MessageDialog.openError(getSite().getShell(), title, message);
  }

  @Override
  public void dispose() {
    final SessionManager sessionManager = SessionManager.getInstance();
    final Optional<SessionContext> ctx = sessionManager.getSelectedSession();

    sessionManager.removeSessionSelectionListener(this);

    if (ctx.isPresent()) {
      ctx.get().getDbHelper().removeDependencyGraphListener(this);
    }

    super.dispose();
  }

  @Override
  public void partVisible(final IWorkbenchPartReference ref) {
    final IWorkbenchPart part = ref.getPart(false);

    if (part != null && part == this) {
      updateGraph();
    }
  }

  @Override
  public void partActivated(final IWorkbenchPartReference ref) {
    // nothing to do
  }

  @Override
  public void partBroughtToTop(final IWorkbenchPartReference ref) {
    // nothing to do
  }

  @Override
  public void partClosed(final IWorkbenchPartReference ref) {
    // nothing to do
  }

  @Override
  public void partDeactivated(final IWorkbenchPartReference ref) {
    // nothing to do
  }

  @Override
  public void partHidden(final IWorkbenchPartReference ref) {
    // nothing to do
  }

  @Override
  public void partInputChanged(final IWorkbenchPartReference ref) {
    // nothing to do
  }

  @Override
  public void partOpened(final IWorkbenchPartReference ref) {
    // nothing to do
  }
}
