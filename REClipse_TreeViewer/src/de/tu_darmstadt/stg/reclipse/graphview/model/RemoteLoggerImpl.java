package de.tu_darmstadt.stg.reclipse.graphview.model;

import de.tu_darmstadt.stg.reclipse.graphview.Activator;
import de.tu_darmstadt.stg.reclipse.graphview.model.persistence.EsperAdapter;
import de.tu_darmstadt.stg.reclipse.graphview.model.persistence.PersistenceFacade;
import de.tu_darmstadt.stg.reclipse.graphview.view.ReactiveTreeView;
import de.tu_darmstadt.stg.reclipse.logger.BreakpointInformation;
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable;
import de.tu_darmstadt.stg.reclipse.logger.RemoteLoggerInterface;

import java.io.IOException;
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

  private static final IEventLogger DUMMY_LOGGER = new IEventLogger() {

    @Override
    public void logNodeValueSet(final ReactiveVariable r) {
    }

    @Override
    public void logNodeEvaluationStarted(final ReactiveVariable r) {
    }

    @Override
    public void logNodeEvaluationEndedWithException(final ReactiveVariable r, final Exception e) {
    }

    @Override
    public void logNodeEvaluationEnded(final ReactiveVariable r) {
    }

    @Override
    public void logNodeCreated(final ReactiveVariable r) {
    }

    @Override
    public void logNodeAttached(final ReactiveVariable r, final UUID dependentId) {
    }

    @Override
    public void close() {
    }
  };

  private final SessionContext ctx;
  private final PersistenceFacade persistence;
  private final EsperAdapter esperAdapter;
  private final IEventLogger logger;
  private final HashSet<IJavaLineBreakpoint> breakpoints = new HashSet<>();

  private final BreakpointInformationStore store;

  protected RemoteLoggerImpl(final SessionContext ctx) throws RemoteException {
    super();

    this.ctx = ctx;
    this.persistence = ctx.getPersistence();
    this.esperAdapter = ctx.getEsperAdapter();
    this.logger = createLogger(ctx);

    store = BreakpointInformationStore.getInstance();
  }

  private IEventLogger createLogger(final SessionContext ctx) {
    if (!ctx.getConfiguration().isEventLogging()) {
      return DUMMY_LOGGER;
    }

    try {
      return new SerializationEventLogger(ctx);
    }
    catch (final IOException e) {
      Activator.log(e);
      return DUMMY_LOGGER;
    }
  }

  private void createBreakpoint(final BreakpointInformation breakpointInformation) {
    try {
      final IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
      IFile resource = null;
      for (final IProject project : projects) {
        final IFile file = project.getFile(breakpointInformation.getSourcePath());
        if (file != null && file.exists()) {
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

    logger.logNodeCreated(r);
    persistence.logNodeCreated(r);

    sendEventToEsper(r, breakpointInformation);
  }

  @Override
  public void logNodeAttached(final ReactiveVariable r, final UUID dependentId, final BreakpointInformation breakpointInformation) throws RemoteException {
    store.put(r, breakpointInformation);

    logger.logNodeAttached(r, dependentId);
    persistence.logNodeAttached(r, dependentId);

    sendEventToEsper(r, breakpointInformation);
  }

  @Override
  public void logNodeEvaluationEnded(final ReactiveVariable r, final BreakpointInformation breakpointInformation) throws RemoteException {
    store.put(r, breakpointInformation);

    logger.logNodeEvaluationEnded(r);
    persistence.logNodeEvaluationEnded(r);

    sendEventToEsper(r, breakpointInformation);
  }

  @Override
  public void logNodeEvaluationEndedWithException(final ReactiveVariable r, final Exception e, final BreakpointInformation breakpointInformation) throws RemoteException {
    store.put(r, breakpointInformation);

    logger.logNodeEvaluationEndedWithException(r, e);
    persistence.logNodeEvaluationEndedWithException(r, e);

    sendEventToEsper(r, breakpointInformation);
  }

  @Override
  public void logNodeEvaluationStarted(final ReactiveVariable r, final BreakpointInformation breakpointInformation) throws RemoteException {
    store.put(r, breakpointInformation);

    logger.logNodeEvaluationStarted(r);
    persistence.logNodeEvaluationStarted(r);

    sendEventToEsper(r, breakpointInformation);
  }

  @Override
  public void logNodeValueSet(final ReactiveVariable r, final BreakpointInformation breakpointInformation) throws RemoteException {
    store.put(r, breakpointInformation);

    logger.logNodeValueSet(r);
    persistence.logNodeValueSet(r);

    sendEventToEsper(r, breakpointInformation);
  }

  @Override
  public void endSession() throws RemoteException {
    debuggingTerminated();
  }

  private void sendEventToEsper(final ReactiveVariable r, final BreakpointInformation breakpointInformation) {
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

  /**
   * Clears the the automatically created breakpoints for the next debugging
   * session.
   */
  public void debuggingTerminated() {
    for (final IJavaLineBreakpoint breakpoint : breakpoints) {
      try {
        breakpoint.delete();
      }
      catch (final CoreException e) {
        Activator.log(e);
      }
    }

    logger.close();
  }
}
