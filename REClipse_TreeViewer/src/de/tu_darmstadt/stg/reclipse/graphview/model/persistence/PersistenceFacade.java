package de.tu_darmstadt.stg.reclipse.graphview.model.persistence;

import de.tu_darmstadt.stg.reclipse.graphview.Activator;
import de.tu_darmstadt.stg.reclipse.graphview.model.ILoggerInterface;
import de.tu_darmstadt.stg.reclipse.graphview.model.ISessionConfiguration;
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable;

import java.util.Collections;
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
    try {
      dbHelper.logNodeCreated(r);
    }
    catch (final PersistenceException e) {
      Activator.log(e);
    }
  }

  @Override
  public void logNodeAttached(final ReactiveVariable r, final UUID dependentId) {
    try {
      dbHelper.logNodeAttached(r, dependentId);
    }
    catch (final PersistenceException e) {
      Activator.log(e);
    }

  }

  @Override
  public void logNodeEvaluationEnded(final ReactiveVariable r) {
    try {
      dbHelper.logNodeEvaluationEnded(r);
    }
    catch (final PersistenceException e) {
      Activator.log(e);
    }
  }

  @Override
  public void logNodeEvaluationEndedWithException(final ReactiveVariable r, final Exception exception) {
    try {
      dbHelper.logNodeEvaluationEndedWithException(r, exception);
    }
    catch (final PersistenceException e) {
      Activator.log(e);
    }
  }

  @Override
  public void logNodeEvaluationStarted(final ReactiveVariable r) {
    try {
      dbHelper.logNodeEvaluationStarted(r);
    }
    catch (final PersistenceException e) {
      Activator.log(e);
    }
  }

  @Override
  public void logNodeValueSet(final ReactiveVariable r) {
    try {
      dbHelper.logNodeValueSet(r);
    }
    catch (final PersistenceException e) {
      Activator.log(e);
    }
  }

  public List<ReactiveVariable> getReVars(final int pointInTime) {
    try {
      return dbHelper.getReVarsWithDependencies(pointInTime);
    }
    catch (final PersistenceException e) {
      Activator.log(e);
      return Collections.emptyList();
    }
  }

  public DependencyGraph getDependencyGraph(final int pointInTime) {
    try {
      return dbHelper.getDependencyGraph(pointInTime);
    }
    catch (final PersistenceException e) {
      Activator.log(e);
      return DependencyGraph.emptyGraph();
    }
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
