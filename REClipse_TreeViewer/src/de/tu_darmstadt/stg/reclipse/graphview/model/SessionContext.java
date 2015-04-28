package de.tu_darmstadt.stg.reclipse.graphview.model;

import de.tu_darmstadt.stg.reclipse.graphview.model.persistence.DatabaseHelper;
import de.tu_darmstadt.stg.reclipse.graphview.model.persistence.EsperAdapter;
import de.tu_darmstadt.stg.reclipse.graphview.model.persistence.PersistenceFacade;

import java.util.Date;
import java.util.UUID;

/**
 * A context for a logging session. It contains session details and the database
 * connection of the session.
 *
 */
public class SessionContext {

  private final ISessionConfiguration configuration;
  private final UUID id;
  private final Date created;
  private final PersistenceFacade persistence;

  protected SessionContext(final ISessionConfiguration configuration) {
    this.configuration = configuration;
    this.id = UUID.randomUUID();
    this.created = new Date();
    this.persistence = new PersistenceFacade(id, configuration);
  }

  public void close() {
    persistence.close();
  }

  public UUID getId() {
    return id;
  }

  public Date getCreated() {
    return created;
  }

  public DatabaseHelper getDbHelper() {
    return persistence.getDbHelper();
  }

  public PersistenceFacade getPersistence() {
    return persistence;
  }

  public EsperAdapter getEsperAdapter() {
    return persistence.getEsperAdapter();
  }

  public ISessionConfiguration getConfiguration() {
    return configuration;
  }
}
