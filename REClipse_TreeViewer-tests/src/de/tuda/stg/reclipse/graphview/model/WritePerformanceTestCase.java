package de.tuda.stg.reclipse.graphview.model;

import java.rmi.RemoteException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.test.performance.PerformanceTestCase;

import de.tuda.stg.reclipse.graphview.model.SerializationEventLogger.NodeAttached;
import de.tuda.stg.reclipse.graphview.model.SerializationEventLogger.NodeCreated;
import de.tuda.stg.reclipse.graphview.model.SerializationEventLogger.NodeEvaluationEnded;
import de.tuda.stg.reclipse.graphview.model.SerializationEventLogger.NodeEvaluationEndedWithException;
import de.tuda.stg.reclipse.graphview.model.SerializationEventLogger.NodeEvaluationStarted;
import de.tuda.stg.reclipse.graphview.model.SerializationEventLogger.NodeEvent;
import de.tuda.stg.reclipse.graphview.model.SerializationEventLogger.NodeValueSet;

public class WritePerformanceTestCase extends PerformanceTestCase {
  
  private static final String SESSION = "profiling1k";
  private static final int ITERATIONS = 10;

  public void testWritePerformance() throws Exception {
    SessionManager.getInstance().setConfiguration(new ISessionConfiguration() {

      @Override
      public IPath getDatabaseFilesDir() {
        return new Path(System.getProperty("java.io.tmpdir"));
      }

      @Override
      public boolean isEventLogging() {
        return false;
      }

      @Override
      public boolean isSuspendOnSessionStart() {
        return false;
      }
    });

    for (int i = 0; i < ITERATIONS; i++) {
      startMeasuring();
      emulateDebuggingSession(i + 1);
      stopMeasuring();
    }
    commitMeasurements();
    assertPerformance();
  }

  private void emulateDebuggingSession(int i) throws Exception {
    System.out.println("Run " + i);

    NodeEventIterator iter = new NodeEventIterator(SESSION);

    SessionContext context = SessionManager.getInstance().createSession();

    while (iter.hasNext()) {
      NodeEvent event = iter.next();
      dispatchEvent(event, context.getPersistence());
    }

    context.close();
    iter.close();
  }

  private void dispatchEvent(NodeEvent nodeEvent, ILoggerInterface logger) throws RemoteException {
    if (nodeEvent instanceof NodeCreated) {
      NodeCreated event = (NodeCreated) nodeEvent;
      logger.logNodeCreated(event.getReactiveVariable());
    }
    else if (nodeEvent instanceof NodeAttached) {
      NodeAttached event = (NodeAttached) nodeEvent;
      logger.logNodeAttached(event.getReactiveVariable(), event.getDependentId());
    }
    else if (nodeEvent instanceof NodeEvaluationEnded) {
      NodeEvaluationEnded event = (NodeEvaluationEnded) nodeEvent;
      logger.logNodeEvaluationEnded(event.getReactiveVariable());
    }
    else if (nodeEvent instanceof NodeEvaluationEndedWithException) {
      NodeEvaluationEndedWithException event = (NodeEvaluationEndedWithException) nodeEvent;
      logger.logNodeEvaluationEndedWithException(event.getReactiveVariable(), event.getException());
    }
    else if (nodeEvent instanceof NodeEvaluationStarted) {
      NodeEvaluationStarted event = (NodeEvaluationStarted) nodeEvent;
      logger.logNodeEvaluationStarted(event.getReactiveVariable());
    }
    else if (nodeEvent instanceof NodeValueSet) {
      NodeValueSet event = (NodeValueSet) nodeEvent;
      logger.logNodeValueSet(event.getReactiveVariable());
    }
  }

}
