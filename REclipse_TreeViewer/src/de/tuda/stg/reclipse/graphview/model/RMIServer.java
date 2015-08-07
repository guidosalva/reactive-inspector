package de.tuda.stg.reclipse.graphview.model;

import de.tuda.stg.reclipse.graphview.Activator;
import de.tuda.stg.reclipse.logger.RMIConstants;

import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;

/**
 * Sets up the RMI server by creating an instance of the
 * {@link RemoteLoggerImpl} and providing them to clients via Java RMI with the
 * name {@value RMIConstants#REMOTE_REFERENCE_NAME}.
 */
public class RMIServer implements Runnable {

  @Override
  public void run() {
    try {
      final RemoteSessionImpl remoteSession = new RemoteSessionImpl();
      bind(remoteSession);
    }
    catch (final RemoteException e) {
      Activator.log(e);
    }
  }

  private void bind(final RemoteSessionImpl remoteSession) throws RemoteException, AccessException {
    Registry registry;
    try {
      registry = LocateRegistry.createRegistry(RMIConstants.REGISTRY_PORT);
    }
    catch (final ExportException e) {
      registry = LocateRegistry.getRegistry(RMIConstants.REGISTRY_PORT);
    }
    registry.rebind(RMIConstants.REMOTE_REFERENCE_NAME, remoteSession);
  }
}
