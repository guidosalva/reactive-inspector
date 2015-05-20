package de.tu_darmstadt.stg.reclipse.graphview.model.persistence;

import de.tu_darmstadt.stg.reclipse.graphview.model.ILoggerInterface;
import de.tu_darmstadt.stg.reclipse.graphview.model.ISessionConfiguration;
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class PersistenceFacade implements ILoggerInterface {

  private final UUID sessionId;
  private final ISessionConfiguration configuration;
  private final Neo4jHelper helper;
  private final EsperAdapter esperAdapter;

  public PersistenceFacade(final UUID sessionId, final ISessionConfiguration configuration) {
    this.sessionId = sessionId;
    this.configuration = configuration;
    this.helper = new Neo4jHelper(sessionId.toString(), configuration);
    this.esperAdapter = new EsperAdapter();
  }

  @Override
  public void logNodeCreated(final ReactiveVariable r) {
    helper.createNode(r);
  }

  @Override
  public void logNodeAttached(final ReactiveVariable r, final UUID dependentId) {
    helper.attachNode(r, dependentId);
  }

  @Override
  public void logNodeEvaluationEnded(final ReactiveVariable r) {
    helper.evaluationEnded(r);
  }

  @Override
  public void logNodeEvaluationEndedWithException(final ReactiveVariable r, final Exception exception) {

  }

  @Override
  public void logNodeEvaluationStarted(final ReactiveVariable r) {

  }

  @Override
  public void logNodeValueSet(final ReactiveVariable r) {

  }

  public List<ReactiveVariable> getReVars(final int pointInTime) {
    return Collections.emptyList();
  }

  public EsperAdapter getEsperAdapter() {
    return esperAdapter;
  }

  public DependencyGraph getDependencyGraph(final int pointInTime) {
    return null; // TODO create dependency graph
  }

  public int getLastPointInTime() {
    return helper.getLastPointInTime();
  }

  public void close() {
    helper.close();
  }
}
