package de.tu_darmstadt.stg.reclipse.graphview.view;

import de.tu_darmstadt.stg.reclipse.graphview.Texts;
import de.tu_darmstadt.stg.reclipse.graphview.action.ChooseLayoutAlgorithm;
import de.tu_darmstadt.stg.reclipse.graphview.action.Relayout;
import de.tu_darmstadt.stg.reclipse.graphview.action.SaveGraphAsImage;
import de.tu_darmstadt.stg.reclipse.graphview.action.ZoomIn;
import de.tu_darmstadt.stg.reclipse.graphview.action.ZoomOut;
import de.tu_darmstadt.stg.reclipse.graphview.controller.QueryController;
import de.tu_darmstadt.stg.reclipse.graphview.model.DatabaseHelper;
import de.tu_darmstadt.stg.reclipse.graphview.model.DependencyGraphHistoryChangedListener;
import de.tu_darmstadt.stg.reclipse.graphview.model.RemoteLoggerImpl;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
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
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.core.widgets.Graph;

/**
 * Base view class containing all the elements which are shown in the
 * "Reactive Tree" view / tab.
 */
// IZoomableWorkbenchPart,
public class ReactiveTreeView extends ViewPart implements IDebugEventSetListener, DependencyGraphHistoryChangedListener {

  /**
   * The ID of the view as specified by the extension.
   */
  public static final String ID = "de.tu-darmstadt.stg.reclipse.graphview.ReactiveTreeView"; //$NON-NLS-1$

  protected CustomGraphViewer viewer;
  protected CustomGraph graph;

  protected Slider slider;
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

    // viewer = new CustomGraphViewer(parent, SWT.NONE);
    // viewer.getControl().setLayoutData(new GridData(GridData.FILL, SWT.FILL,
    // true, true));
    // viewer.getControl().addMouseListener(new MoveGraphMouseAdapter());
    // viewer.getControl().addMouseMoveListener(new MoveGraphMoveListener());
    graph = new CustomGraph(parent);

    // be careful: you have to set environment variable LIBOVERLAY_SCROLLBAR=0
    // under Ubuntu / OpenJDK, so that the slider works - see
    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=368929
    slider = new Slider(parent, SWT.HORIZONTAL);
    slider.setMinimum(0);
    slider.setIncrement(1);
    slider.setPageIncrement(1);
    slider.setLayoutData(new GridData(SWT.FILL, SWT.END, true, false));
    slider.addListener(SWT.Selection, new Listener() {

      @Override
      public void handleEvent(final Event event) {
        rebuildGraph();
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

    DatabaseHelper.getInstance().addDepGraphHistoryChangedListener(this);

    // update slider values, because the reactive tree view tab could be opened
    // after the dependency graph history changed events have been fired
    updateSliderValues();
  }

  private void createActions() {
    // creating the toolbar entries
    getViewSite().getActionBars().getToolBarManager().add(new SaveGraphAsImage(getSite(), viewer));
    getViewSite().getActionBars().getToolBarManager().add(new ZoomIn(viewer));
    getViewSite().getActionBars().getToolBarManager().add(new ZoomOut(viewer));
    // getViewSite().getActionBars().getToolBarManager().add(new
    // ZoomContributionViewItem(this));

    // create the context menu
    final MenuManager menuMgr = new MenuManager();
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(new IMenuListener() {

      @Override
      public void menuAboutToShow(final IMenuManager manager) {
        manager.add(new Relayout(viewer));
        // append layout algorithm submenu
        final MenuManager subMenu = new MenuManager(Texts.Menu_Layouts);
        ChooseLayoutAlgorithm.addActions(subMenu, viewer);
        manager.add(subMenu);

        manager.add(new Separator());
        manager.add(new ZoomIn(viewer));
        manager.add(new ZoomOut(viewer));
        manager.add(new Separator());
        manager.add(new SaveGraphAsImage(getSite(), viewer));
        manager.add(new Separator());
        // Other plug-ins can contribute there actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
      }
    });
    // final Menu menu = menuMgr.createContextMenu(viewer.getControl());
    // viewer.getControl().setMenu(menu);
    // getSite().registerContextMenu(menuMgr, viewer);
  }

  // @Override
  // public AbstractZoomableViewer getZoomableViewer() {
  // return viewer;
  // }

  @Override
  public void setFocus() {
    rebuildGraph();
    // viewer.getControl().setFocus();
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

    if (terminated) {
      RemoteLoggerImpl.debuggingTerminated();
      rebuildGraph();
    }
    else if (suspended) {
      rebuildGraph();
    }
  }

  protected void rebuildGraph() {
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
        graph.setPointInTime(getCurrentSliderValue());
        if (slider != null && !slider.isDisposed()) {
          slider.redraw();
        }
      }
    });
  }

  private void updateSliderValues() {
    // syncExec so that maximum is correctly set if jumpToLastSliderValue is
    // called directly afterwards
    Display.getDefault().syncExec(new Runnable() {

      @Override
      public void run() {
        if (slider == null || slider.isDisposed()) {
          return;
        }
        slider.setMaximum(DatabaseHelper.getLastPointInTime() + slider.getThumb());
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

  private class MoveGraphMouseAdapter extends MouseAdapter {

    public MoveGraphMouseAdapter() {
      // just to avoid the "emulated by a synthetic accessor method" warning
    }

    @Override
    public void mouseDown(final MouseEvent e) {
      // do nothing if clicked on node
      if (e.getSource() instanceof Graph && ((Graph) e.getSource()).getSelection().size() == 0) {
        moveGraphActive = e.button == 1;
        if (moveGraphActive) {
          moveStartPos.x = e.x;
          moveStartPos.y = e.y;
          viewLocationStartPos = new Point(viewer.getGraphControl().getViewport().getViewLocation().x, viewer.getGraphControl().getViewport().getViewLocation().y);
        }
      }
    }

    @Override
    public void mouseUp(final MouseEvent e) {
      moveGraphActive = false;
    }
  }

  private class MoveGraphMoveListener implements MouseMoveListener {

    public MoveGraphMoveListener() {
      // just to avoid the "emulated by a synthetic accessor method" warning
    }

    @Override
    public void mouseMove(final MouseEvent e) {
      if (moveGraphActive) {
        final int newX = viewLocationStartPos.x + moveStartPos.x - e.x;
        final int newY = viewLocationStartPos.y + moveStartPos.y - e.y;
        viewer.getGraphControl().scrollTo(newX, newY);
      }
    }

  }

  @Override
  public void dependencyGraphHistoryChanged() {
    // only update the slider if the graph is shown
    if (showGraph) {
      updateSliderValues();
    }
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
