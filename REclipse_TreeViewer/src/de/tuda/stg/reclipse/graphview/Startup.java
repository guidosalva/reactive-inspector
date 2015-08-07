package de.tuda.stg.reclipse.graphview;

import de.tuda.stg.reclipse.graphview.model.RMIServer;

import org.eclipse.ui.IStartup;

/**
 * This class is used in the
 * 
 * <pre>
 * org.eclipse.ui.startup
 * </pre>
 * 
 * extension point. It sets up the RMI server as soon as possible, so that it is
 * ready when a debugging session is started.
 */
public class Startup implements IStartup {

  @Override
  public void earlyStartup() {
    // directly start RMI server, so that client connection works
    new Thread(new RMIServer()).start();
  }

}
