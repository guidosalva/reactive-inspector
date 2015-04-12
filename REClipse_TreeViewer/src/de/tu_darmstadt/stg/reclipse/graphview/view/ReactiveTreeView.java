package de.tu_darmstadt.stg.reclipse.graphview.view;

import de.tu_darmstadt.stg.reclipse.graphview.Texts;
import de.tu_darmstadt.stg.reclipse.graphview.action.Relayout;
import de.tu_darmstadt.stg.reclipse.graphview.action.SaveGraphAsImage;
import de.tu_darmstadt.stg.reclipse.graphview.action.SessionSelect;
import de.tu_darmstadt.stg.reclipse.graphview.action.ShowHeatmap;
import de.tu_darmstadt.stg.reclipse.graphview.action.ZoomIn;
import de.tu_darmstadt.stg.reclipse.graphview.action.ZoomOut;
import de.tu_darmstadt.stg.reclipse.graphview.controller.QueryController;
import de.tu_darmstadt.stg.reclipse.graphview.model.DependencyGraphHistoryChangedListener;
import de.tu_darmstadt.stg.reclipse.graphview.model.ISessionSelectionListener;
import de.tu_darmstadt.stg.reclipse.graphview.model.SessionContext;
import de.tu_darmstadt.stg.reclipse.graphview.model.SessionManager;
import de.tu_darmstadt.stg.reclipse.graphview.view.graph.CustomGraph;

import java.util.Optional;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

/**
 * Base view class containing all the elements which are shown in the
 * "Reactive Tree" view / tab.
 */
public class ReactiveTreeView extends ViewPart implements IDebugEventSetListener, DependencyGraphHistoryChangedListener, ISessionSelectionListener {

  /**
   * The ID of the view as specified by the extension.
   */
  public static final String ID = "de.tu-darmstadt.stg.reclipse.graphview.ReactiveTreeView"; //$NON-NLS-1$

  protected CustomGraph graph;

  protected Composite graphParent;
  protected Scale slider;
  private boolean showGraph = false;
  protected boolean manualMode = false;

  protected boolean moveGraphActive = false;
  protected Point moveStartPos = new Point(0, 0);
  protected Point viewLocationStartPos = new Point(0, 0);

  private Text queryTextField;

  public ReactiveTreeView() {
    DebugPlugin.getDefault().addDebugEventListener(this);
  }

  @Override
  public void createPartControl(final Composite parent) {
    parent.setLayout(new GridLayout(1, true));

    graphParent = new Composite(parent, SWT.NONE);
    graphParent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    graphParent.setLayout(new GridLayout());

    // be careful: you have to set environment variable LIBOVERLAY_SCROLLBAR=0
    // under Ubuntu / OpenJDK, so that the slider works - see
    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=368929
    slider = new Scale(parent, SWT.HORIZONTAL);
    slider.setMinimum(0);
    slider.setIncrement(1);
    slider.setPageIncrement(1);
    slider.setLayoutData(new GridData(SWT.FILL, SWT.END, true, false));
    slider.addListener(SWT.Selection, new Listener() {

      @Override
      public void handleEvent(final Event event) {
        if (event.detail == SWT.NONE) {
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
    getViewSite().getActionBars().getToolBarManager().add(new Relayout(graph));
    getViewSite().getActionBars().getToolBarManager().add(new SaveGraphAsImage(getSite(), graph));
    getViewSite().getActionBars().getToolBarManager().add(new ZoomIn(graph));
    getViewSite().getActionBars().getToolBarManager().add(new ZoomOut(graph));
    getViewSite().getActionBars().getToolBarManager().add(new ShowHeatmap(graph));
  }

  @Override
  public void onSessionSelected(final SessionContext ctx) {
    showGraph = true;

    ctx.getDbHelper().addDepGraphHistoryChangedListener(ReactiveTreeView.this);

    Display.getDefault().syncExec(new Runnable() {

      @Override
      public void run() {
        graph = new CustomGraph(graphParent, ctx);
      }
    });

    // update slider values, because the reactive tree view tab could be
    // opened
    // after the dependency graph history changed events have been fired
    updateSliderValues();
  }

  @Override
  public void onSessionDeselected(final SessionContext ctx) {
    ctx.getDbHelper().removeDepGraphHistoryChangedListener(this);

    if (graph != null) {
      Display.getDefault().syncExec(new Runnable() {

        @Override
        public void run() {
          graph.dispose();
          graph = null;
        }
      });
    }
  }

  @Override
  public void setFocus() {
    rebuildGraph();
  }

  @Override
  public void handleDebugEvents(final DebugEvent[] events) {
    // react only when debugging is suspended
    boolean suspended = false;
    boolean terminated = false;
    for (final DebugEvent e : events) {
      switch (e.getKind()) {
        case DebugEvent.SUSPEND:
          suspended = true;
          break;
        case DebugEvent.TERMINATE:
          terminated = true;
          break;
        default:
          // do nothing
          break;
      }
    }

    if (terminated || suspended) {
      rebuildGraph();
    }
  }

  public void rebuildGraph(final boolean highlightChange) {
    if (!showGraph) {
      return;
    }
    Display.getDefault().asyncExec(new Runnable() {

      @Override
      public void run() {
        if (graph == null) {
          return;
        }
        // just give the point in time to the graph at which the user wants to
        // see the dependency graph
        graph.setPointInTime(getCurrentSliderValue(), highlightChange);
        if (slider != null && !slider.isDisposed()) {
          slider.redraw();
        }
      }
    });
  }

  public void rebuildGraph() {
    rebuildGraph(true);
  }

  private void updateSliderValues() {
    // syncExec so that maximum is correctly set if jumpToLastSliderValue is
    // called directly afterwards
    Display.getDefault().syncExec(new Runnable() {

      @Override
      public void run() {
        final Optional<SessionContext> ctx = SessionManager.getInstance().getSelectedSession();

        if (slider == null || slider.isDisposed() || !ctx.isPresent()) {
          return;
        }

        slider.setMaximum(ctx.get().getDbHelper().getLastPointInTime());
        if (!manualMode) {
          slider.setSelection(slider.getMaximum());
          // notify the listeners, because this is not done automatically when
          // the selection is changed programmatically via setSelecion
          slider.notifyListeners(SWT.Selection, new Event());
        }
      }
    });
  }

  public int getCurrentSliderValue() {
    if (slider == null || slider.isDisposed()) {
      return -1;
    }
    return slider.getSelection();
  }

  public void setShowGraph(final boolean show) {
    showGraph = show;
    if (showGraph) {
      updateSliderValues();
    }
    rebuildGraph();
  }

  @Override
  public void dependencyGraphHistoryChanged() {
    // only update the slider if the graph is shown
    if (showGraph) {
      updateSliderValues();
    }

    // update the graph to reflect newest changes
    rebuildGraph();
  }

  public String getQueryText() {
    if (queryTextField == null || queryTextField.isDisposed()) {
      return ""; //$NON-NLS-1$
    }
    return queryTextField.getText();
  }

  public void jumpToPointInTime(final int pointInTime) {
    manualMode = true;
    slider.setSelection(pointInTime);
    rebuildGraph();
  }

  public void showInformation(final String title, final String message) {
    MessageDialog.openInformation(getSite().getShell(), title, message);
  }

  public void showError(final String title, final String message) {
    MessageDialog.openError(getSite().getShell(), title, message);
  }
}
