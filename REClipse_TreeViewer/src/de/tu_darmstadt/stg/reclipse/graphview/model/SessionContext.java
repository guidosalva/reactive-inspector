package de.tu_darmstadt.stg.reclipse.graphview.model;

import java.util.Date;
import java.util.UUID;

public class SessionContext {

  // TODO remove global instance
  public static SessionContext INSTANCE = null;

  private final UUID id;
  private final Date created;
  private final DatabaseHelper dbHelper;

  public SessionContext() {
    this.id = UUID.randomUUID();
    this.created = new Date();
    this.dbHelper = new DatabaseHelper(id.toString());
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
