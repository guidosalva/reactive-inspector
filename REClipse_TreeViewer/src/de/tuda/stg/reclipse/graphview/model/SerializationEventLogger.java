package de.tuda.stg.reclipse.graphview.model;

import de.tuda.stg.reclipse.logger.ReactiveVariable;

import de.tuda.stg.reclipse.graphview.Activator;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.UUID;

/**
 * Writes every event to a log file. The file can then be used to replay the
 * logging session. As serialization the standard Java object serialization is
 * used.
 *
 */
public class SerializationEventLogger implements IEventLogger {

  private final ObjectOutputStream out;
  private int eventCount = 0;

  public SerializationEventLogger(final SessionContext ctx) throws IOException {
    this.out = createOutputStream(ctx);
  }

  private ObjectOutputStream createOutputStream(final SessionContext ctx) throws IOException {
    final File file = ctx.getConfiguration().getDatabaseFilesDir().append(ctx.getId() + ".ser").toFile(); //$NON-NLS-1$
    file.getParentFile().mkdirs();

    return new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
  }

  private void log(final NodeEvent event) {
    try {
      out.writeObject(event);
    }
    catch (final IOException e) {
      Activator.log(e);
    }
  }

  @Override
  public void logNodeCreated(final ReactiveVariable r) {
    log(new NodeCreated(r));
    eventCount++;
  }

  @Override
  public void logNodeAttached(final ReactiveVariable r, final UUID dependentId) {
    log(new NodeAttached(r, dependentId));
    eventCount++;
  }

  @Override
  public void logNodeEvaluationEnded(final ReactiveVariable r) {
    log(new NodeEvaluationEnded(r));
    eventCount++;
  }

  @Override
  public void logNodeEvaluationEndedWithException(final ReactiveVariable r, final Exception e) {
    log(new NodeEvaluationEndedWithException(r, e));
    eventCount++;
  }

  @Override
  public void logNodeEvaluationStarted(final ReactiveVariable r) {
    log(new NodeEvaluationStarted(r));
    eventCount++;
  }

  @Override
  public void logNodeValueSet(final ReactiveVariable r) {
    log(new NodeValueSet(r));
    eventCount++;
  }

  @Override
  public void close() {
    Activator.logInfo(eventCount + " events written"); //$NON-NLS-1$

    if (out != null) {
      try {
        out.close();
      }
      catch (final IOException e) {
        Activator.log(e);
      }
    }
  }

  public static class NodeEvent implements Serializable {

    private static final long serialVersionUID = 4284968222686234320L;

    private ReactiveVariable reactiveVariable;

    public NodeEvent() {
    }

    public NodeEvent(final ReactiveVariable reactiveVariable) {
      this.reactiveVariable = reactiveVariable;
    }

    public ReactiveVariable getReactiveVariable() {
      return reactiveVariable;
    }

    public void setReactiveVariable(final ReactiveVariable reactiveVariable) {
      this.reactiveVariable = reactiveVariable;
    }
  }

  public static class NodeAttached extends NodeEvent {

    private static final long serialVersionUID = -4612129576314547382L;

    private UUID dependentId;

    public NodeAttached() {
      super();
    }

    public NodeAttached(final ReactiveVariable reactiveVariable, final UUID dependentId) {
      super(reactiveVariable);
      this.dependentId = dependentId;
    }

    public UUID getDependentId() {
      return dependentId;
    }

    public void setDependentId(final UUID dependentId) {
      this.dependentId = dependentId;
    }
  }

  public static class NodeCreated extends NodeEvent {

    private static final long serialVersionUID = 2951355989254968936L;

    public NodeCreated() {
      super();
    }

    public NodeCreated(final ReactiveVariable reactiveVariable) {
      super(reactiveVariable);
    }
  }

  public static class NodeEvaluationEnded extends NodeEvent {

    private static final long serialVersionUID = 4787380010184517265L;

    public NodeEvaluationEnded() {
      super();
    }

    public NodeEvaluationEnded(final ReactiveVariable reactiveVariable) {
      super(reactiveVariable);
    }
  }

  public static class NodeEvaluationEndedWithException extends NodeEvent {

    private static final long serialVersionUID = 425054201443151028L;

    private Exception exception;

    public NodeEvaluationEndedWithException() {
    }

    public NodeEvaluationEndedWithException(final ReactiveVariable reactiveVariable, final Exception exception) {
      super(reactiveVariable);
      this.exception = exception;
    }

    public Exception getException() {
      return exception;
    }

    public void setException(final Exception exception) {
      this.exception = exception;
    }
  }

  public static class NodeEvaluationStarted extends NodeEvent {

    private static final long serialVersionUID = 3575081194345180721L;

    public NodeEvaluationStarted() {
      super();
    }

    public NodeEvaluationStarted(final ReactiveVariable reactiveVariable) {
      super(reactiveVariable);
    }
  }

  public static class NodeValueSet extends NodeEvent {

    private static final long serialVersionUID = -5774397767494577886L;

    public NodeValueSet() {
      super();
    }

    public NodeValueSet(final ReactiveVariable reactiveVariable) {
      super(reactiveVariable);
    }

  }
}
