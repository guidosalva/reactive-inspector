package de.tuda.stg.reclipse.graphview.model;

import de.tuda.stg.reclipse.graphview.Activator;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchListener;

/**
 * This singleton class manages all logging sessions. Consumers can add
 * {@link ISessionSelectionListener}, to be notified if a session is
 * (de)selected.
 *
 */
public class SessionManager {

  private static final SessionManager INSTANCE = new SessionManager();

  private final Map<UUID, SessionContext> sessions = new ConcurrentHashMap<>();
  private final List<ISessionSelectionListener> listeners = new CopyOnWriteArrayList<>();

  private ISessionConfiguration configuration = new DefaultSessionConfiguration();

  private Optional<SessionContext> selectedSession = Optional.empty();

  protected ILaunch latestLaunch;

  private SessionManager() {
    DebugPlugin.getDefault().getLaunchManager().addLaunchListener(new ILaunchListener() {

      @Override
      public void launchAdded(final ILaunch launch) {
        latestLaunch = launch;
      }

      @Override
      public void launchRemoved(final ILaunch arg0) {
      }

      @Override
      public void launchChanged(final ILaunch arg0) {
      }
    });
  }

  public synchronized SessionContext createSession() {
    final SessionContext ctx = new SessionContext(configuration, latestLaunch);
    sessions.put(ctx.getId(), ctx);
    selectSession(ctx);
    return ctx;
  }

  private void selectSession(final SessionContext ctx) {
    if (selectedSession.isPresent()) {
      final SessionContext ctxOld = selectedSession.get();

      if (ctx == ctxOld) {
        return;
      }

      fireSessionDeselected(ctxOld);
    }

    selectedSession = Optional.of(ctx);

    fireSessionSelected(ctx);
  }

  public synchronized void selectSession(final UUID sessionId) {
    if (!sessions.containsKey(sessionId)) {
      Activator.logMessage("unable to select session: no session wit id " + sessionId); //$NON-NLS-1$
      return;
    }

    final SessionContext ctx = sessions.get(sessionId);
    selectSession(ctx);
  }

  public Collection<SessionContext> getSessions() {
    return sessions.values();
  }

  public Optional<SessionContext> getSelectedSession() {
    return selectedSession;
  }

  public void addSessionSelectionListener(final ISessionSelectionListener listener) {
    if (!listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

  public void removeSessionSelectionListener(final ISessionSelectionListener listener) {
    listeners.remove(listener);
  }

  private void fireSessionSelected(final SessionContext ctx) {
    for (final ISessionSelectionListener listener : listeners) {
      listener.onSessionSelected(ctx);
    }
  }

  private void fireSessionDeselected(final SessionContext ctx) {
    for (final ISessionSelectionListener listener : listeners) {
      listener.onSessionDeselected(ctx);
    }
  }

  public synchronized void stop() {
    for (final SessionContext ctx : sessions.values()) {
      ctx.close();
    }
  }

  public ISessionConfiguration getConfiguration() {
    return configuration;
  }

  public void setConfiguration(final ISessionConfiguration configuration) {
    this.configuration = configuration;
  }

  public static SessionManager getInstance() {
    return INSTANCE;
  }
}
