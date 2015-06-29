package de.tu_darmstadt.stg.reclipse.graphview.model;

import de.tu_darmstadt.stg.reclipse.logger.BreakpointInformation;
import de.tu_darmstadt.stg.reclipse.logger.RemoteLoggerInterface;
import de.tu_darmstadt.stg.reclipse.logger.RemoteSessionInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Implements the {@link RemoteSessionInterface}. It provides the possibility to
 * start a logging session wit a {@link RemoteLoggerInterface}.
 */
public class RemoteSessionImpl extends UnicastRemoteObject implements RemoteSessionInterface {

  private static final long serialVersionUID = -5052947613187963697L;

  private final SessionManager sessionManager = SessionManager.getInstance();

  protected RemoteSessionImpl() throws RemoteException {
    super();
  }

  @Override
  public RemoteLoggerInterface startSession(final BreakpointInformation breakpointInformation) throws RemoteException {
    final SessionContext ctx = sessionManager.createSession();
    return new RemoteLoggerImpl(ctx, breakpointInformation);
  }

}
