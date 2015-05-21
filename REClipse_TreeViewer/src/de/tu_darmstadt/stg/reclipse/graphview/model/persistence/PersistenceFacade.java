package de.tu_darmstadt.stg.reclipse.graphview.model.persistence;

import de.tu_darmstadt.stg.reclipse.graphview.model.ILoggerInterface;
import de.tu_darmstadt.stg.reclipse.graphview.model.ISessionConfiguration;
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable;

import java.util.List;
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
    dbHelper.copyLastReVars(r.getDependencyGraphHistoryType());
    final int lastPointInTime = dbHelper.getLastPointInTime();
    r.setPointInTime(lastPointInTime);
    dbHelper.addReVar(r);
  }

  @Override
  public void logNodeAttached(final ReactiveVariable r, final UUID dependentId) {
    dbHelper.copyLastReVars(r.getDependencyGraphHistoryType());
    final int lastPointInTime = dbHelper.getLastPointInTime();
    r.setPointInTime(lastPointInTime);
    final String additionalInformation = r.getId() + "->" + dependentId; //$NON-NLS-1$
    r.setAdditionalInformation(additionalInformation);
    r.setConnectedWith(dependentId);
    dbHelper.deleteReVar(r.getId(), lastPointInTime);
    dbHelper.addReVar(r);
  }

  @Override
  public void logNodeEvaluationEnded(final ReactiveVariable r) {
    dbHelper.copyLastReVars(r.getDependencyGraphHistoryType());
    final int lastPointInTime = dbHelper.getLastPointInTime();
    r.setPointInTime(lastPointInTime);
    dbHelper.deleteReVar(r.getId(), lastPointInTime);
    dbHelper.addReVar(r);
  }

  @Override
  public void logNodeEvaluationEndedWithException(final ReactiveVariable r, final Exception e) {
    dbHelper.copyLastReVars(r.getDependencyGraphHistoryType());
    final int lastPointInTime = dbHelper.getLastPointInTime();
    r.setPointInTime(lastPointInTime);
    r.setAdditionalInformation(e.getMessage());
    dbHelper.deleteReVar(r.getId(), lastPointInTime);
    dbHelper.addReVar(r);
  }

  @Override
  public void logNodeEvaluationStarted(final ReactiveVariable r) {
    dbHelper.copyLastReVars(r.getDependencyGraphHistoryType());
    final int lastPointInTime = dbHelper.getLastPointInTime();
    r.setPointInTime(lastPointInTime);
    dbHelper.deleteReVar(r.getId(), lastPointInTime);
    dbHelper.addReVar(r);
  }

  @Override
  public void logNodeValueSet(final ReactiveVariable r) {
    dbHelper.copyLastReVars(r.getDependencyGraphHistoryType());
    final int lastPointInTime = dbHelper.getLastPointInTime();
    r.setPointInTime(lastPointInTime);
    dbHelper.deleteReVar(r.getId(), lastPointInTime);
    dbHelper.addReVar(r);
  }

  public List<ReactiveVariable> getReVars(final int pointInTime) {
    return dbHelper.getReVars(pointInTime);
  }

  public DependencyGraph getDependencyGraph(final int pointInTime) {
    return dbHelper.getDependencyGraph(pointInTime);
  }

  public int getLastPointInTime() {
    return dbHelper.getLastPointInTime();
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
