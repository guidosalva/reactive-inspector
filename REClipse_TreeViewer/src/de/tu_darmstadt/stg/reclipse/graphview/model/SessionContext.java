package de.tu_darmstadt.stg.reclipse.graphview.model;

import java.util.Date;
import java.util.UUID;

/**
 * A context for a logging session. It contains session details and the database
 * connection of the session.
 *
 */
public class SessionContext {

  private final UUID id;
  private final Date created;
  private final DatabaseHelper dbHelper;

  protected SessionContext() {
    this.id = UUID.randomUUID();
    this.created = new Date();
    this.dbHelper = new DatabaseHelper(id.toString());
  }

  public void close() {
    dbHelper.close();
  }

  public UUID getId() {
    return id;
  }

  public Date getCreated() {
    return created;
  }

  public DatabaseHelper getDbHelper() {
    return dbHelper;
  }
}
