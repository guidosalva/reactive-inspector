package de.tuda.stg.reclipse.graphview.model.persistence;

import de.tuda.stg.reclipse.graphview.model.ILoggerInterface;
import de.tuda.stg.reclipse.logger.ReactiveVariable;

import java.util.HashMap;
import java.util.UUID;

public class TimeProfiler implements ILoggerInterface {

  private final HashMap<UUID, Long> evaluationStartTimes = new HashMap<>();

  final HashMap<UUID, Long> evaluationTimes = new HashMap<>();

  @Override
  public void logNodeEvaluationStarted(final ReactiveVariable variable) {
    final long time = System.nanoTime();
    final UUID id = variable.getId();
    evaluationStartTimes.put(id, time);
  }

  @Override
  public void logNodeEvaluationEnded(final ReactiveVariable variable) {
    logEvaluationTime(variable);
  }

  @Override
  public void logNodeEvaluationEndedWithException(final ReactiveVariable variable, final Exception exception) {
    logEvaluationTime(variable);
  }

  private void logEvaluationTime(final ReactiveVariable variable) {
    final long time = System.nanoTime();
    final UUID id = variable.getId();

    if (evaluationStartTimes.get(id) == null) {
      return;
    }

    final long startTime = evaluationStartTimes.get(id);
    final long evaluationTime = time - startTime;

    evaluationTimes.put(id, evaluationTime);
  }

  @Override
  public void logNodeCreated(final ReactiveVariable variable) {}

  @Override
  public void logNodeAttached(final ReactiveVariable variable, final UUID dependentId) {}

  @Override
  public void logNodeValueSet(final ReactiveVariable variable) {}



}
