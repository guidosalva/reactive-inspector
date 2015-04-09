package de.tu_darmstadt.stg.reclipse.graphview.model;

import de.tu_darmstadt.stg.reclipse.graphview.Activator;
import de.tu_darmstadt.stg.reclipse.graphview.view.ReactiveTreeView;
import de.tu_darmstadt.stg.reclipse.logger.BreakpointInformation;
import de.tu_darmstadt.stg.reclipse.logger.DependencyGraphHistoryType;
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable;
import de.tu_darmstadt.stg.reclipse.logger.RemoteLoggerInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.UUID;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.debug.core.IJavaLineBreakpoint;
import org.eclipse.jdt.debug.core.JDIDebugModel;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * Implements the {@link RemoteLoggerInterface}, so that it retrieves all the
 * events from the language-specific library and will process them accordingly.
 */
public class RemoteLoggerImpl extends UnicastRemoteObject implements RemoteLoggerInterface {

  private static final long serialVersionUID = -3766741205877371369L;

  private final static SessionContext ctx;
  private final static DatabaseHelper dbHelper;
  private final static EsperAdapter esperAdapter;
  protected static int currentPointInTime = 0;
  private static HashSet<IJavaLineBreakpoint> breakpoints = new HashSet<>();

  private final BreakpointInformationStore store;

  static {
    ctx = new SessionContext();
    dbHelper = ctx.getDbHelper();
    esperAdapter = new EsperAdapter(ctx);
    SessionContext.INSTANCE = ctx;
  }

  protected RemoteLoggerImpl() throws RemoteException {
    super();

    store = BreakpointInformationStore.getInstance();
  }

  private static void createBreakpoint(final BreakpointInformation breakpointInformation) {
    try {
      final IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
      IFile resource = null;
      for (final IProject project : projects) {
        final IFile file = project.getFile(breakpointInformation.getSourcePath());
        if (file != null) {
          resource = file;
          break;
        }
      }
      if (resource != null) {
        final IJavaLineBreakpoint breakpoint = JDIDebugModel.createLineBreakpoint(resource, breakpointInformation.getClassName(), breakpointInformation.getLineNumber(), -1, -1, 0,
                true, null);
        breakpoints.add(breakpoint);
      }
    }
    catch (final CoreException e) {
      Activator.log(e);
    }
  }

  @Override
  public void logNodeCreated(final ReactiveVariable r, final BreakpointInformation breakpointInformation) throws RemoteException {
    store.put(r, breakpointInformation);

    dbHelper.copyLastReVars(r.getDependencyGraphHistoryType());
    final int lastPointInTime = dbHelper.getLastPointInTime();
    r.setPointInTime(lastPointInTime);
    dbHelper.addReVar(r);
    sendEventToEsper(r, breakpointInformation);
  }

  @Override
  public void logNodeAttached(final ReactiveVariable r, final UUID dependentId, final BreakpointInformation breakpointInformation) throws RemoteException {
    store.put(r, breakpointInformation);

    dbHelper.copyLastReVars(r.getDependencyGraphHistoryType());
    final int lastPointInTime = dbHelper.getLastPointInTime();
    r.setPointInTime(lastPointInTime);
    final String additionalInformation = r.getId() + "->" + dependentId; //$NON-NLS-1$
    r.setAdditionalInformation(additionalInformation);
    r.setConnectedWith(dependentId);
    dbHelper.deleteReVar(r.getId(), lastPointInTime);
    dbHelper.addReVar(r);
    sendEventToEsper(r, breakpointInformation);
  }

  @Override
  public void logNodeEvaluationEnded(final ReactiveVariable r, final BreakpointInformation breakpointInformation) throws RemoteException {
    store.put(r, breakpointInformation);

    dbHelper.copyLastReVars(r.getDependencyGraphHistoryType());
    final int lastPointInTime = dbHelper.getLastPointInTime();
    r.setPointInTime(lastPointInTime);
    dbHelper.deleteReVar(r.getId(), lastPointInTime);
    dbHelper.addReVar(r);
    sendEventToEsper(r, breakpointInformation);
  }

  @Override
  public void logNodeEvaluationEndedWithException(final ReactiveVariable r, final Exception e, final BreakpointInformation breakpointInformation) throws RemoteException {
    store.put(r, breakpointInformation);

    dbHelper.copyLastReVars(r.getDependencyGraphHistoryType());
    final int lastPointInTime = dbHelper.getLastPointInTime();
    r.setPointInTime(lastPointInTime);
    r.setAdditionalInformation(e.getMessage());
    dbHelper.deleteReVar(r.getId(), lastPointInTime);
    dbHelper.addReVar(r);
    sendEventToEsper(r, breakpointInformation);
  }

  @Override
  public void logNodeEvaluationStarted(final ReactiveVariable r, final BreakpointInformation breakpointInformation) throws RemoteException {
    store.put(r, breakpointInformation);

    dbHelper.copyLastReVars(r.getDependencyGraphHistoryType());
    final int lastPointInTime = dbHelper.getLastPointInTime();
    r.setPointInTime(lastPointInTime);
    dbHelper.deleteReVar(r.getId(), lastPointInTime);
    dbHelper.addReVar(r);
    sendEventToEsper(r, breakpointInformation);
  }

  @Override
  public void logNodeValueSet(final ReactiveVariable r, final BreakpointInformation breakpointInformation) throws RemoteException {
    store.put(r, breakpointInformation);

    dbHelper.copyLastReVars(r.getDependencyGraphHistoryType());
    final int lastPointInTime = dbHelper.getLastPointInTime();
    r.setPointInTime(lastPointInTime);
    dbHelper.deleteReVar(r.getId(), lastPointInTime);
    dbHelper.addReVar(r);
    sendEventToEsper(r, breakpointInformation);
  }

  private static void sendEventToEsper(final ReactiveVariable r, final BreakpointInformation breakpointInformation) {
    esperAdapter.sendEvent(r);
    final int pointInTime = esperAdapter.getPointInTime();
    if (pointInTime != -1) {
      createBreakpoint(breakpointInformation);
      jumpToPointInTime(pointInTime);
    }
  }

  private static void jumpToPointInTime(final int pointInTime) {
    Display.getDefault().syncExec(new Runnable() {

      @Override
      public void run() {
        final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        final IViewPart view = page.findView(ReactiveTreeView.ID);
        if (!(view instanceof ReactiveTreeView)) {
          return;
        }
        final ReactiveTreeView rtv = (ReactiveTreeView) view;
        rtv.jumpToPointInTime(pointInTime);
      }
    });
  }

  private static int getCurrentPointInTime() {
    Display.getDefault().syncExec(new Runnable() {

      @Override
      public void run() {
        final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        final IViewPart view = page.findView(ReactiveTreeView.ID);
        if (!(view instanceof ReactiveTreeView)) {
          return;
        }
        final ReactiveTreeView rtv = (ReactiveTreeView) view;
        currentPointInTime = rtv.getCurrentSliderValue();
      }
    });
    return currentPointInTime;
  }

  public static boolean isNodeConnectionCurrentlyActive(final UUID srcId, final UUID destId) {
    final int pointInTime = getCurrentPointInTime();
    return dbHelper.isNodeConnectionActive(pointInTime, srcId, destId);
  }

  public static DependencyGraphHistoryType getCurrentDependencyGraphHistoryType() {
    final int pointInTime = getCurrentPointInTime();
    return dbHelper.getDependencyGraphHistoryType(pointInTime);
  }

  /**
   * Clears the database and the automatically created breakpoints for the next
   * debugging session.
   */
  public static void debuggingTerminated() {
    dbHelper.truncateTable(DatabaseHelper.REACTIVE_VARIABLES_TABLE_NAME);
    dbHelper.resetLastPointInTime();
    for (final IJavaLineBreakpoint breakpoint : breakpoints) {
      try {
        breakpoint.delete();
      }
      catch (final CoreException e) {
        Activator.log(e);
      }
    }
  }

}
