package de.tu_darmstadt.stg.reclipse.graphview.model;

import de.tu_darmstadt.stg.reclipse.graphview.Activator;
import de.tu_darmstadt.stg.reclipse.graphview.model.persistence.LiveEsperAdapter;
import de.tu_darmstadt.stg.reclipse.graphview.model.persistence.PersistenceFacade;
import de.tu_darmstadt.stg.reclipse.graphview.util.BreakpointUtils;
import de.tu_darmstadt.stg.reclipse.graphview.view.ReactiveTreeView;
import de.tu_darmstadt.stg.reclipse.logger.BreakpointInformation;
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable;
import de.tu_darmstadt.stg.reclipse.logger.RemoteLoggerInterface;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.UUID;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.jdt.debug.core.IJavaLineBreakpoint;
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
  private final LiveEsperAdapter esperAdapter;
  private final IEventLogger logger;
  private final HashSet<IJavaLineBreakpoint> breakpoints = new HashSet<>();

  protected RemoteLoggerImpl(final SessionContext ctx, final BreakpointInformation breakpointInformation) throws RemoteException {
    super();

    this.ctx = ctx;
    this.persistence = ctx.getPersistence();
    this.esperAdapter = persistence.getLiveEsperAdapter();
    this.logger = createLogger();

    if (ctx.getConfiguration().isSuspendOnSessionStart()) {
      suspendDebugTarget(breakpointInformation);
    }
  }

  private IEventLogger createLogger() {
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
    final IJavaLineBreakpoint breakpoint = BreakpointUtils.createBreakoint(breakpointInformation);

    if (breakpoint != null) {
      breakpoints.add(breakpoint);
    }
  }

  @Override
  public void logNodeCreated(final ReactiveVariable r, final BreakpointInformation breakpointInformation) throws RemoteException {
    logger.logNodeCreated(r);
    persistence.logNodeCreated(r);

    ctx.putVariableLocation(r.getId(), breakpointInformation);
    ctx.putBreakpointInformation(r, breakpointInformation);

    sendEventToEsper(r, breakpointInformation);
  }

  @Override
  public void logNodeAttached(final ReactiveVariable r, final UUID dependentId, final BreakpointInformation breakpointInformation) throws RemoteException {
    logger.logNodeAttached(r, dependentId);
    persistence.logNodeAttached(r, dependentId);

    ctx.putBreakpointInformation(r, breakpointInformation);

    sendEventToEsper(r, breakpointInformation);
  }

  @Override
  public void logNodeEvaluationEnded(final ReactiveVariable r, final BreakpointInformation breakpointInformation) throws RemoteException {
    logger.logNodeEvaluationEnded(r);
    persistence.logNodeEvaluationEnded(r);

    ctx.putBreakpointInformation(r, breakpointInformation);

    sendEventToEsper(r, breakpointInformation);
  }

  @Override
  public void logNodeEvaluationEndedWithException(final ReactiveVariable r, final Exception e, final BreakpointInformation breakpointInformation) throws RemoteException {
    logger.logNodeEvaluationEndedWithException(r, e);
    persistence.logNodeEvaluationEndedWithException(r, e);

    ctx.putBreakpointInformation(r, breakpointInformation);

    sendEventToEsper(r, breakpointInformation);
  }

  @Override
  public void logNodeEvaluationStarted(final ReactiveVariable r, final BreakpointInformation breakpointInformation) throws RemoteException {
    logger.logNodeEvaluationStarted(r);
    persistence.logNodeEvaluationStarted(r);

    ctx.putBreakpointInformation(r, breakpointInformation);

    sendEventToEsper(r, breakpointInformation);
  }

  @Override
  public void logNodeValueSet(final ReactiveVariable r, final BreakpointInformation breakpointInformation) throws RemoteException {
    logger.logNodeValueSet(r);
    persistence.logNodeValueSet(r);

    ctx.putBreakpointInformation(r, breakpointInformation);

    sendEventToEsper(r, breakpointInformation);
  }

  @Override
  public void endSession() throws RemoteException {
    debuggingTerminated();
  }

  private void sendEventToEsper(final ReactiveVariable r, final BreakpointInformation breakpointInformation) {
    if (esperAdapter.sendEvent(r)) {
      suspendDebugTarget(breakpointInformation);
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

  private void suspendDebugTarget(final BreakpointInformation breakpointInformation) {
    final ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
    final IDebugTarget[] targets = launchManager.getDebugTargets();

    if (targets.length > 0) {
      final IDebugTarget target = targets[0];

      try {
        if (target.hasThreads()) {
          final IThread[] threads = target.getThreads();

          for (final IThread thread : threads) {
            if (thread.getName().equals(breakpointInformation.getThreadName())) {
              thread.suspend();
              return;
            }
          }
        }
      }
      catch (final DebugException e) {
        Activator.log(e);
      }
    }
  }
}
