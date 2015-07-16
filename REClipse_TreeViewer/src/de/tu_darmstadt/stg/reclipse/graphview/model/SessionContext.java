package de.tu_darmstadt.stg.reclipse.graphview.model;

import de.tu_darmstadt.stg.reclipse.graphview.model.persistence.DatabaseHelper;
import de.tu_darmstadt.stg.reclipse.graphview.model.persistence.HistoryEsperAdapter;
import de.tu_darmstadt.stg.reclipse.graphview.model.persistence.PersistenceFacade;
import de.tu_darmstadt.stg.reclipse.logger.BreakpointInformation;
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
  private final Map<UUID, BreakpointInformation> variableLocations = new HashMap<>();
  private final Map<ReactiveVariable, BreakpointInformation> breakpointInformation = new HashMap<>();

  protected SessionContext(final ISessionConfiguration configuration) {
    this.configuration = configuration;
    this.id = UUID.randomUUID();
    this.created = new Date();
    this.persistence = new PersistenceFacade(id, configuration);
  }

  public void close() {
    persistence.close();
  }

  public void putVariableLocation(final UUID idVariable, final BreakpointInformation location) {
    variableLocations.put(idVariable, location);
  }

  public BreakpointInformation getVariableLocation(final UUID idVariable) {
    return variableLocations.get(idVariable);
  }

  public void putBreakpointInformation(final ReactiveVariable variable, final BreakpointInformation information) {
    breakpointInformation.put(variable, information);
  }

  public BreakpointInformation getBreakpointInformation(final ReactiveVariable variable) {
    return breakpointInformation.get(variable);
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

  public HistoryEsperAdapter getHistoryEsperAdapter() {
    return persistence.getHistoryEsperAdapter();
  }

  public ISessionConfiguration getConfiguration() {
    return configuration;
  }
}
