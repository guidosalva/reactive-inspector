package de.tuda.stg.reclipse.graphview.model.persistence;

import de.tuda.stg.reclipse.graphview.Activator;
import de.tuda.stg.reclipse.graphview.model.ILoggerInterface;
import de.tuda.stg.reclipse.logger.ReactiveVariable;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class PersistenceFacade implements ILoggerInterface {

  private final DatabaseHelper dbHelper;
  private final HistoryEsperAdapter historyEsperAdapter;
  private final LiveEsperAdapter liveEsperAdapter;
  private final TimeProfiler timeProfiler;

  public PersistenceFacade(final UUID sessionId) {
    this.dbHelper = new DatabaseHelper(sessionId.toString());
    this.historyEsperAdapter = new HistoryEsperAdapter(dbHelper);
    this.liveEsperAdapter = new LiveEsperAdapter(sessionId.toString());
    timeProfiler = new TimeProfiler();
  }

  @Override
  public void logNodeCreated(final ReactiveVariable r) {
    try {
      dbHelper.logNodeCreated(r);
    }
    catch (final PersistenceException e) {
      Activator.log(e);
    }
    timeProfiler.logNodeCreated(r);
  }

  @Override
  public void logNodeAttached(final ReactiveVariable r, final UUID dependentId) {
    try {
      dbHelper.logNodeAttached(r, dependentId);
    }
    catch (final PersistenceException e) {
      Activator.log(e);
    }
    timeProfiler.logNodeAttached(r, dependentId);
  }

  @Override
  public void logNodeEvaluationEnded(final ReactiveVariable r) {
    try {
      dbHelper.logNodeEvaluationEnded(r);
    }
    catch (final PersistenceException e) {
      Activator.log(e);
    }
    timeProfiler.logNodeEvaluationEnded(r);
  }

  @Override
  public void logNodeEvaluationEndedWithException(final ReactiveVariable r, final Exception exception) {
    try {
      dbHelper.logNodeEvaluationEndedWithException(r, exception);
    }
    catch (final PersistenceException e) {
      Activator.log(e);
    }
    timeProfiler.logNodeEvaluationEndedWithException(r, exception);
  }

  @Override
  public void logNodeEvaluationStarted(final ReactiveVariable r) {
    try {
      dbHelper.logNodeEvaluationStarted(r);
    }
    catch (final PersistenceException e) {
      Activator.log(e);
    }
    timeProfiler.logNodeEvaluationStarted(r);
  }

  @Override
  public void logNodeValueSet(final ReactiveVariable r) {
    try {
      dbHelper.logNodeValueSet(r);
    }
    catch (final PersistenceException e) {
      Activator.log(e);
    }
    timeProfiler.logNodeValueSet(r);
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

  public HistoryEsperAdapter getHistoryEsperAdapter() {
    return historyEsperAdapter;
  }

  public LiveEsperAdapter getLiveEsperAdapter() {
    return liveEsperAdapter;
  }
}
