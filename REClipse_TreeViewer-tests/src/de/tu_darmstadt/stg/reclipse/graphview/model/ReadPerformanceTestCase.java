package de.tu_darmstadt.stg.reclipse.graphview.model;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Random;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.test.performance.PerformanceTestCase;

import de.tu_darmstadt.stg.reclipse.graphview.model.SerializationEventLogger.NodeAttached;
import de.tu_darmstadt.stg.reclipse.graphview.model.SerializationEventLogger.NodeCreated;
import de.tu_darmstadt.stg.reclipse.graphview.model.SerializationEventLogger.NodeEvaluationEnded;
import de.tu_darmstadt.stg.reclipse.graphview.model.SerializationEventLogger.NodeEvaluationEndedWithException;
import de.tu_darmstadt.stg.reclipse.graphview.model.SerializationEventLogger.NodeEvaluationStarted;
import de.tu_darmstadt.stg.reclipse.graphview.model.SerializationEventLogger.NodeEvent;
import de.tu_darmstadt.stg.reclipse.graphview.model.SerializationEventLogger.NodeValueSet;


public class ReadPerformanceTestCase extends PerformanceTestCase {
  
  private static final String SESSION = "profiling1k";
  private static final int ITERATIONS = 10;
  private static final int READS = 1000;
  
  public void testReadPerformance() throws Exception {
    SessionManager.getInstance().setConfiguration(new ISessionConfiguration() {

      @Override
      public IPath getDatabaseFilesDir() {
        return new Path(System.getProperty("java.io.tmpdir"));
      }

      @Override
      public boolean isEventLogging() {
        return false;
      }
    });
    
    SessionContext ctx = prepareSession();

    for (int i = 0; i < ITERATIONS; i++) {
      startMeasuring();
      emulateReads(i + 1, ctx);
      stopMeasuring();
    }
    commitMeasurements();
    assertPerformance();
  }

  private SessionContext prepareSession() throws IOException {
    System.out.println("load data...");
    
    NodeEventIterator iter = new NodeEventIterator(SESSION);

    SessionContext context = SessionManager.getInstance().createSession();

    while (iter.hasNext()) {
      NodeEvent event = iter.next();
      dispatchEvent(event, context.getPersistence());
    }

    iter.close();
    
    System.out.println("loading data finished");
    
    return context;
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
  
  private void emulateReads(int run, SessionContext ctx) {
    System.out.println("Run " + run);
    
    Random rand = new Random();
    int last = ctx.getPersistence().getLastPointInTime();
    
    for(int i = 0; i < READS; i++) {
      int point = rand.nextInt(last) + 1;
      ctx.getPersistence().getDependencyGraph(point);
    }
    
  }

}
