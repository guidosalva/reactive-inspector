package de.tu_darmstadt.stg.reclipse.graphview.view;

import de.tu_darmstadt.stg.reclipse.graphview.Activator;
import de.tu_darmstadt.stg.reclipse.graphview.Texts;
import de.tu_darmstadt.stg.reclipse.graphview.action.Relayout;
import de.tu_darmstadt.stg.reclipse.graphview.action.SaveGraphAsImage;
import de.tu_darmstadt.stg.reclipse.graphview.action.SessionSelect;
import de.tu_darmstadt.stg.reclipse.graphview.action.ShowClassName;
import de.tu_darmstadt.stg.reclipse.graphview.action.ShowHeatmap;
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
import org.eclipse.swt.widgets.Label;
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

  protected Composite graphParent;
  protected Scale slider;
  protected Button autoNavButton;
  protected Button nextPointButton;
  protected Button prevPointButton;
  protected Combo queryTextField;
  protected Label queryResultsLabel;
  protected Text searchTextField;
  protected Label searchResultsLabel;
  protected TreeViewGraph graph;
  protected GraphComponent graphComponent;

  protected QueryController queryController;

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

    final Composite searchQueryComposite = new Composite(parent, SWT.NONE);
    searchQueryComposite.setLayout(new GridLayout(2, false));
    searchQueryComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

    final Composite searchComposite = new Composite(searchQueryComposite, SWT.BORDER);
    searchComposite.setLayout(new GridLayout(5, false));
    searchComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

    final Composite queryComposite = new Composite(searchQueryComposite, SWT.BORDER);
    queryComposite.setLayout(new GridLayout(5, false));
    queryComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

    searchTextField = new Text(searchComposite, SWT.SEARCH | SWT.ICON_CANCEL);
    searchTextField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    searchTextField.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetDefaultSelected(final SelectionEvent e) {
        if (e.detail == SWT.ICON_CANCEL) {
          clearSearch();
        }
        else {
          searchNode();
        }
      }

    });

    final Button searchButton = new Button(searchComposite, SWT.PUSH);
    searchButton.setText(Texts.Search_Submit);
    searchButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
    searchButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent e) {
        searchNode();
      }
    });

    final Button prevSearchResultButton = new Button(searchComposite, SWT.ARROW | SWT.LEFT);
    prevSearchResultButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
    prevSearchResultButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent e) {
        graphComponent.prevSearchResult();
        updateSearchResultsLabel();
      }
    });

    final Button nextSearchResultButton = new Button(searchComposite, SWT.ARROW | SWT.RIGHT);
    nextSearchResultButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
    nextSearchResultButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent e) {
        graphComponent.nextSearchResult();
        updateSearchResultsLabel();
      }
    });

    searchResultsLabel = new Label(searchComposite, SWT.NONE);
    final GridData searchResultGridData = new GridData(GridData.VERTICAL_ALIGN_CENTER);
    searchResultGridData.widthHint = 40;
    searchResultsLabel.setLayoutData(searchResultGridData);
    updateSearchResultsLabel();

    queryController = new QueryController(this);
    queryTextField = new Combo(queryComposite, SWT.DROP_DOWN);
    queryTextField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    queryTextField.setItems(QueryController.QUERY_TEMPLATES);

    final Button submitButton = new Button(queryComposite, SWT.PUSH);
    submitButton.addSelectionListener(queryController.new SubmitQueryButtonHandler());
    submitButton.setText(Texts.Query_Submit);
    submitButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
    queryTextField.addTraverseListener(new TraverseListener() {

      @Override
      public void keyTraversed(final TraverseEvent e) {
        if (e.detail == SWT.TRAVERSE_RETURN) {
          submitButton.notifyListeners(SWT.Selection, new Event());
        }
      }
    });

    final Button prevResultButton = new Button(queryComposite, SWT.ARROW | SWT.LEFT);
    prevResultButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
    prevResultButton.addSelectionListener(queryController.new PrevQueryResultButtonHandler());

    final Button nextResultButton = new Button(queryComposite, SWT.ARROW | SWT.RIGHT);
    nextResultButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
    nextResultButton.addSelectionListener(queryController.new NextQueryResultButtonHandler());

    queryResultsLabel = new Label(queryComposite, SWT.NONE);
    final GridData queryResultGridData = new GridData(GridData.VERTICAL_ALIGN_CENTER);
    queryResultGridData.widthHint = 40;
    queryResultsLabel.setLayoutData(queryResultGridData);
    updateQueryResultsLabel();

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
    getViewSite().getActionBars().getToolBarManager().add(new ShowClassName(graph));
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
    lastPointInTime = ctx.getPersistence().getLastPointInTime();

    queryController.reset();

    graph.setSessionContext(ctx);
    ctx.getDbHelper().addDependencyGraphListener(ReactiveTreeView.this);

    Display.getDefault().syncExec(new Runnable() {

      @Override
      public void run() {
        disableManualMode();
        updateQueryResultsLabel();
      }
    });
  }

  @Override
  public void onSessionDeselected(final SessionContext ctx) {
    ctx.getDbHelper().removeDependencyGraphListener(this);
    graph.removeSessionContext();

    if (delayedUpdateTask != null) {
      delayedUpdateTask.cancel(false);
    }

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

    if (graphComponent.clearSearch()) {
      updateSearchResultsLabel();
    }
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

  protected void searchNode() {
    final String name = searchTextField.getText();

    if (name != null && name.trim().length() > 0) {
      graphComponent.searchNodes(name);

      if (graphComponent.getSearchResultCount() == 0) {
        showInformation(Texts.Search_NoResults_Title, Texts.Search_NoResults_Message);
      }
    }
    else {
      if (graphComponent.clearSearch()) {
        graph.resetNodes();
        graph.refresh();
      }
    }

    updateSearchResultsLabel();
  }

  protected void clearSearch() {
    searchTextField.setText(""); //$NON-NLS-1$

    if (graphComponent.clearSearch()) {
      graph.resetNodes();
      graph.refresh();
    }

    updateSearchResultsLabel();
  }

  protected void updateSearchResultsLabel() {
    final int current = graphComponent.getCurrentSearchResult();
    final int count = graphComponent.getSearchResultCount();

    searchResultsLabel.setText(current + " / " + count); //$NON-NLS-1$
  }

  public void updateQueryResultsLabel() {
    final int current = queryController.getCurrentResultSelection();
    final int count = queryController.getResultCount();

    queryResultsLabel.setText(current + " / " + count); //$NON-NLS-1$
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
