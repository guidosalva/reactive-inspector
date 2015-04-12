package de.tu_darmstadt.stg.reclipse.graphview;

import de.tu_darmstadt.stg.reclipse.graphview.model.SessionManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

  // The plug-in ID
  public static final String PLUGIN_ID = "de.tu-darmstadt.stg.reclipse.Viewer"; //$NON-NLS-1$

  // The shared instance
  private static Activator plugin;

  // plugin specific error code for log
  private static final int ERROR = 99;

  private final String propertiesFileName;
  private final Properties prop;

  public Activator() {
    propertiesFileName = getStateLocation().toOSString() + File.separator + "state.properties"; //$NON-NLS-1$
    prop = new Properties();
    final File file = new File(propertiesFileName);
    // If no file, nothing to do
    if (file.exists()) {
      try (final InputStream stream = new BufferedInputStream(new FileInputStream(file))) {
        prop.load(stream);
      }
      catch (final IOException e) {
        // do nothing
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
   */
  @Override
  public void start(final BundleContext context) throws Exception {
    super.start(context);
    plugin = this;
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
   */
  @Override
  public void stop(final BundleContext context) throws Exception {
    SessionManager.getInstance().stop();

    plugin = null;

    // save the properties
    final File file = new File(propertiesFileName);
    try (final OutputStream stream = new BufferedOutputStream(new FileOutputStream(file))) {
      prop.store(stream, null);
    }
    catch (final IOException e) {
      // do nothing
    }
    super.stop(context);
  }

  /**
   * Returns the shared instance
   *
   * @return the shared instance
   */
  public static Activator getDefault() {
    return plugin;
  }

  /**
   * Returns an image descriptor for the image file at the given plug-in
   * relative path
   *
   * @param path
   *          the path
   * @return the image descriptor
   */
  public static ImageDescriptor getImageDescriptor(final Images img) {
    return imageDescriptorFromPlugin(PLUGIN_ID, img.getPath());
  }

  /**
   * Logs the given message.
   *
   * @param message
   *          the message to log
   */
  public static void logMessage(final String message) {
    logMessage(message, null);
  }

  /**
   * Logs the given message and the given throwable or <code>null</code> if
   * none.
   *
   * @param message
   *          the message to log
   * @param throwable
   *          the exception that occurred or <code>null</code> if none
   */
  public static void logMessage(final String message, final Throwable throwable) {
    log(new Status(IStatus.ERROR, PLUGIN_ID, ERROR, message, throwable));
  }

  /**
   * Logs the specified status.
   *
   * @param status
   *          status to log
   */
  public static void log(final IStatus status) {
    getDefault().getLog().log(status);
  }

  /**
   * Logs the specified throwable.
   *
   * @param t
   *          throwable to log
   */
  public static void log(final Throwable t) {
    log(new Status(IStatus.ERROR, PLUGIN_ID, ERROR, Texts.Log_Error, t));
  }

  /**
   * Read a property from the plugin specific state
   *
   * @param key
   *          the property key
   * @return value of the specified property key
   */
  public String getProperty(final String key) {
    return prop.getProperty(key);
  }

  /**
   * Set a plugin specific property
   *
   * @param key
   *          the property key
   * @param value
   *          value of the specified property key
   */
  public void setProperty(final String key, final String value) {
    prop.setProperty(key, value);
  }
}
