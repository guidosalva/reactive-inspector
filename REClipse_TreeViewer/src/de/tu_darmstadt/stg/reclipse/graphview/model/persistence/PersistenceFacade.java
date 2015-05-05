package de.tu_darmstadt.stg.reclipse.graphview.model.persistence;

import de.tu_darmstadt.stg.reclipse.graphview.Activator;
import de.tu_darmstadt.stg.reclipse.graphview.model.ILoggerInterface;
import de.tu_darmstadt.stg.reclipse.graphview.model.ISessionConfiguration;
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable;

import java.sql.SQLException;
import java.util.UUID;

public class PersistenceFacade implements ILoggerInterface {

  private final UUID sessionId;
  private final ISessionConfiguration configuration;
  private final DatabaseHelper dbHelper;
  private final EsperAdapter esperAdapter;

  public PersistenceFacade(final UUID sessionId, final ISessionConfiguration configuration) {
    this.sessionId = sessionId;
    this.configuration = configuration;
    this.dbHelper = new DatabaseHelper(sessionId.toString(), configuration);
    this.esperAdapter = new EsperAdapter(dbHelper);
  }

  @Override
  public void logNodeCreated(final ReactiveVariable r) {
    try {
      final int idVariable = dbHelper.createVariable(r);
      final int idVariableStatus = dbHelper.createVariableStatus(r, idVariable);
      final int pointInTime = dbHelper.createEvent(r, idVariableStatus, null);

      dbHelper.nextPointInTime(pointInTime, idVariableStatus, null);

      r.setPointInTime(pointInTime);
    }
    catch (final SQLException e) {
      Activator.log(e);
    }
  }

  @Override
  public void logNodeAttached(final ReactiveVariable r, final UUID dependentId) {
    try {
      final int idVariable = dbHelper.findVariableById(r.getId());
      final int dependentVariable = dbHelper.findVariableById(dependentId);

      final int oldVariableStatus = dbHelper.findActiveVariableStatus(idVariable);

      final int idVariableStatus = dbHelper.createVariableStatus(r, idVariable, oldVariableStatus, dependentVariable);
      final int pointInTime = dbHelper.createEvent(r, idVariable, dependentVariable);

      dbHelper.nextPointInTime(pointInTime, idVariableStatus, oldVariableStatus);

      final String additionalInformation = r.getId() + "->" + dependentId; //$NON-NLS-1$
      r.setPointInTime(pointInTime);
      r.setAdditionalInformation(additionalInformation);
      r.setConnectedWith(dependentId);
    }
    catch (final SQLException e) {
      Activator.log(e);
    }

  }

  @Override
  public void logNodeEvaluationEnded(final ReactiveVariable r) {
    try {
      final int idVariable = dbHelper.findVariableById(r.getId());
      final int oldVariableStatus = dbHelper.findActiveVariableStatus(idVariable);
      final int idVariableStatus = dbHelper.createVariableStatus(r, idVariable, oldVariableStatus);
      final int pointInTime = dbHelper.createEvent(r, idVariable, null);

      dbHelper.nextPointInTime(pointInTime, idVariableStatus, oldVariableStatus);

      r.setPointInTime(pointInTime);
    }
    catch (final SQLException e) {
      Activator.log(e);
    }
  }

  @Override
  public void logNodeEvaluationEndedWithException(final ReactiveVariable r, final Exception exception) {
    try {
      final int idVariable = dbHelper.findVariableById(r.getId());
      final int oldVariableStatus = dbHelper.findActiveVariableStatus(idVariable);
      // TODO store exception
      final int idVariableStatus = dbHelper.createVariableStatus(r, idVariable, oldVariableStatus);
      final int pointInTime = dbHelper.createEvent(r, idVariable, null);

      dbHelper.nextPointInTime(pointInTime, idVariableStatus, oldVariableStatus);

      r.setPointInTime(pointInTime);
    }
    catch (final SQLException e) {
      Activator.log(e);
    }
  }

  @Override
  public void logNodeEvaluationStarted(final ReactiveVariable r) {
    try {
      final int idVariable = dbHelper.findVariableById(r.getId());
      final int oldVariableStatus = dbHelper.findActiveVariableStatus(idVariable);
      final int idVariableStatus = dbHelper.createVariableStatus(r, idVariable, oldVariableStatus);
      final int pointInTime = dbHelper.createEvent(r, idVariable, null);

      dbHelper.nextPointInTime(pointInTime, idVariableStatus, oldVariableStatus);

      r.setPointInTime(pointInTime);
    }
    catch (final SQLException e) {
      Activator.log(e);
    }
  }

  @Override
  public void logNodeValueSet(final ReactiveVariable r) {
    try {
      final int idVariable = dbHelper.findVariableById(r.getId());
      final int oldVariableStatus = dbHelper.findActiveVariableStatus(idVariable);
      final int idVariableStatus = dbHelper.createVariableStatus(r, idVariable, oldVariableStatus);
      final int pointInTime = dbHelper.createEvent(r, idVariable, null);

      dbHelper.nextPointInTime(pointInTime, idVariableStatus, oldVariableStatus);

      r.setPointInTime(pointInTime);
    }
    catch (final SQLException e) {
      Activator.log(e);
    }
  }

  public void close() {
    dbHelper.close();
  }

  public DatabaseHelper getDbHelper() {
    return dbHelper;
  }

  public EsperAdapter getEsperAdapter() {
    return esperAdapter;
  }
}
