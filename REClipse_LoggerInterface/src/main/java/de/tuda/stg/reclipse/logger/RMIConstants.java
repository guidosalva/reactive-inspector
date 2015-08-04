package de.tuda.stg.reclipse.logger;

import java.rmi.registry.Registry;

/**
 * Very simple class which just defines some common constants used by all
 * parties which participate in the Java RMI communication.
 */
public class RMIConstants {

  public static final String REMOTE_REFERENCE_NAME = "RECLIPSE_LOGGER"; //$NON-NLS-1$
  public static final int REGISTRY_PORT = Registry.REGISTRY_PORT;

}
