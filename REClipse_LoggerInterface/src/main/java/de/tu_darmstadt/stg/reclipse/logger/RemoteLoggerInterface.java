package de.tu_darmstadt.stg.reclipse.logger;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

/**
 * Defines a common Java RMI interface which is used by the language-specific
 * logger as well as by the generic Eclipse plugin. The methods correspond
 * one-to-one to the {@link DependencyGraphHistoryType}.
 */
public interface RemoteLoggerInterface extends Remote {

  public void logNodeAttached(final ReactiveVariable r, final UUID dependentId, final BreakpointInformation breakpointInformation) throws RemoteException;

  public void logNodeCreated(final ReactiveVariable r, final BreakpointInformation breakpointInformation) throws RemoteException;

  public void logNodeEvaluationEnded(final ReactiveVariable r, final BreakpointInformation breakpointInformation) throws RemoteException;

  public void logNodeEvaluationEndedWithException(final ReactiveVariable r, final Exception e, final BreakpointInformation breakpointInformation) throws RemoteException;

  public void logNodeEvaluationStarted(final ReactiveVariable r, final BreakpointInformation breakpointInformation) throws RemoteException;

  public void logNodeValueSet(final ReactiveVariable r, final BreakpointInformation breakpointInformation) throws RemoteException;
  
  public void endSession() throws RemoteException;
}
