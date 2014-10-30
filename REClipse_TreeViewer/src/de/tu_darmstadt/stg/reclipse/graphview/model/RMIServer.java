package de.tu_darmstadt.stg.reclipse.graphview.model;

import de.tu_darmstadt.stg.reclipse.graphview.Activator;
import de.tu_darmstadt.stg.reclipse.logger.RMIConstants;

import java.io.IOException;
import java.net.URL;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

/**
 * Sets up the RMI server by creating an instance of the
 * {@link RemoteLoggerImpl} and providing them to clients via Java RMI with the
 * name {@value RMIConstants#REMOTE_REFERENCE_NAME}.
 */
public class RMIServer implements Runnable {

  @Override
  public void run() {
    try {
      if (System.getSecurityManager() == null) {
        try {
          final URL url = FileLocator.find(Platform.getBundle(Activator.PLUGIN_ID), new Path("etc/server.policy"), null); //$NON-NLS-1$
          final String policyFileName = FileLocator.resolve(url).getFile();
          System.setProperty("java.security.policy", policyFileName); //$NON-NLS-1$
        }
        catch (final IOException e) {
        }
        System.setProperty("java.rmi.server.hostname", "127.0.0.1"); //$NON-NLS-1$ //$NON-NLS-2$
        System.setSecurityManager(new RMISecurityManager());
      }
      final RemoteLoggerImpl remoteLoggerImpl = new RemoteLoggerImpl();
      Registry registry;
      try {
        registry = LocateRegistry.createRegistry(RMIConstants.REGISTRY_PORT);
      }
      catch (final ExportException e) {
        registry = LocateRegistry.getRegistry(RMIConstants.REGISTRY_PORT);
      }
      registry.rebind(RMIConstants.REMOTE_REFERENCE_NAME, remoteLoggerImpl);
    }
    catch (final RemoteException e) {
      Activator.log(e);
    }
  }
}
