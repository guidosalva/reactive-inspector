package de.tu_darmstadt.stg.reclipse.logger;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Defines a common Java RMI interface which is used by the language-specific
 * logger as well as by the generic Eclipse plugin. This interface is used to
 * create individual instances of the {@link RemoteLoggerInterface} per logging
 * session.
 */
public interface RemoteSessionInterface extends Remote {

  public RemoteLoggerInterface startSession() throws RemoteException;

}
