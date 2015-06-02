package de.tu_darmstadt.stg.reclipse.graphview.view;

import de.tu_darmstadt.stg.reclipse.graphview.Activator;
import de.tu_darmstadt.stg.reclipse.graphview.Texts;
import de.tu_darmstadt.stg.reclipse.graphview.action.Relayout;
import de.tu_darmstadt.stg.reclipse.graphview.action.SaveGraphAsImage;
import de.tu_darmstadt.stg.reclipse.graphview.action.SessionSelect;
import de.tu_darmstadt.stg.reclipse.graphview.action.ShowHeatmap;
import de.tu_darmstadt.stg.reclipse.graphview.action.ZoomIn;
import de.tu_darmstadt.stg.reclipse.graphview.action.ZoomOut;
import de.tu_darmstadt.stg.reclipse.graphview.controller.QueryController;
import de.tu_darmstadt.stg.reclipse.graphview.model.ISessionSelectionListener;
import de.tu_darmstadt.stg.reclipse.graphview.model.SessionContext;
import de.tu_darmstadt.stg.reclipse.graphview.model.SessionManager;
import de.tu_darmstadt.stg.reclipse.graphview.model.persistence.IDependencyGraphListener;
import de.tu_darmstadt.stg.reclipse.graphview.preferences.PreferenceConstants;
import de.tu_darmstadt.stg.reclipse.graphview.view.graph.CustomGraph;
import de.tu_darmstadt.stg.reclipse.graphview.view.graph.GraphContainer;
import de.tu_darmstadt.stg.reclipse.logger.DependencyGraphHistoryType;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;
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

  protected final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
  protected final GraphContainer graphContainer = new GraphContainer();

  protected Composite graphParent;
  protected Scale slider;
  protected Text queryTextField;

  protected long lastUpdate = 0;
  protected ScheduledFuture<?> delayedUpdateTask;
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

    slider = new Scale(parent, SWT.HORIZONTAL);
    slider.setMinimum(0);
    slider.setIncrement(1);
    slider.setPageIncrement(1);
    slider.setLayoutData(new GridData(SWT.FILL, SWT.END, true, false));
    slider.addListener(SWT.Selection, new Listener() {

      @Override
      public void handleEvent(final Event event) {
        if (event.detail == SWT.NONE) {
          manualMode = true;
          rebuildGraph(false);
        }
      }
    });

    final QueryController queryController = new QueryController(this);
    final Composite queryComposite = new Composite(parent, SWT.NONE);
    queryComposite.setLayout(new GridLayout(4, false));
    queryComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

    queryTextField = new Text(queryComposite, SWT.BORDER);
    queryTextField.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

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
    final Button prevButton = new Button(queryComposite, SWT.ARROW | SWT.LEFT);
    prevButton.addSelectionListener(queryController.new PrevQueryResultButtonHandler());

    final Button nextButton = new Button(queryComposite, SWT.ARROW | SWT.RIGHT);
    nextButton.addSelectionListener(queryController.new NextQueryResultButtonHandler());

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
    getViewSite().getActionBars().getToolBarManager().add(new Relayout(graphContainer));
    getViewSite().getActionBars().getToolBarManager().add(new SaveGraphAsImage(getSite(), graphContainer));
    getViewSite().getActionBars().getToolBarManager().add(new ZoomIn(graphContainer));
    getViewSite().getActionBars().getToolBarManager().add(new ZoomOut(graphContainer));
    getViewSite().getActionBars().getToolBarManager().add(new ShowHeatmap(graphContainer));
  }

  @Override
  public void setFocus() {
    queryTextField.setFocus();
  }

  @Override
  public void onSessionSelected(final SessionContext ctx) {
    manualMode = false;

    ctx.getDbHelper().addDependencyGraphListener(ReactiveTreeView.this);

    Display.getDefault().syncExec(new Runnable() {

      @Override
      public void run() {
        final CustomGraph graph = new CustomGraph(graphParent, ctx);
        graphContainer.setGraph(graph);
      }
    });

    updateSliderMaximum();
  }

  @Override
  public void onSessionDeselected(final SessionContext ctx) {
    ctx.getDbHelper().removeDependencyGraphListener(this);

    if (graphContainer.containsGraph()) {
      Display.getDefault().syncExec(new Runnable() {

        @Override
        public void run() {
          graphContainer.getGraph().dispose();
          graphContainer.setGraph(null);
        }
      });
    }
  }

  @Override
  public void onDependencyGraphChanged(final DependencyGraphHistoryType type, final int pointInTime) {
    // only update if the graph is visible
    if (isVisible()) {
      updateSliderMaximum();
    }
  }

  protected void rebuildGraph(final boolean highlightChange) {
    if (!isVisible()) {
      return;
    }

    Display.getDefault().asyncExec(new Runnable() {

      @Override
      public void run() {
        if (!graphContainer.containsGraph()) {
          return;
        }

        final int pointInTime = getCurrentSliderValue();
        graphContainer.getGraph().setPointInTime(pointInTime, highlightChange);

        if (graphParent != null && !graphParent.isDisposed()) {
          graphParent.layout();
        }
      }
    });
  }

  public void updateSliderMaximum() {
    if (delayedUpdateTask != null && !delayedUpdateTask.isDone()) {
      // update is already delayed
      return;
    }
    else if (isInUpdateInterval()) {
      deleayUpdate();
    }
    else {
      doUpdateSliderMaximum();
    }
  }

  protected void doUpdateSliderMaximum() {
    lastUpdate = System.currentTimeMillis();

    final Optional<SessionContext> ctx = SessionManager.getInstance().getSelectedSession();

    if (!ctx.isPresent()) {
      return;
    }

    final int lastPoint = ctx.get().getPersistence().getLastPointInTime();

    Display.getDefault().syncExec(new Runnable() {

      @Override
      public void run() {
        if (slider == null || slider.isDisposed()) {
          return;
        }

        slider.setMaximum(lastPoint);

        if (!manualMode) {
          slider.setSelection(lastPoint);
          rebuildGraph(true);
        }
      }
    });
  }

  private void deleayUpdate() {
    final long delay = updateInterval - (System.currentTimeMillis() - lastUpdate);

    delayedUpdateTask = executorService.schedule(new Runnable() {

      @Override
      public void run() {
        doUpdateSliderMaximum();

      }
    }, delay, TimeUnit.MILLISECONDS);
  }

  private boolean isInUpdateInterval() {
    return System.currentTimeMillis() - lastUpdate < updateInterval;
  }

  public void jumpToPointInTime(final int pointInTime) {
    manualMode = true;

    Display.getDefault().syncExec(new Runnable() {

      @Override
      public void run() {
        if (slider.getMaximum() < pointInTime) {
          slider.setMaximum(pointInTime);
        }

        slider.setSelection(pointInTime);

        rebuildGraph(false);
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
      updateSliderMaximum();
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
