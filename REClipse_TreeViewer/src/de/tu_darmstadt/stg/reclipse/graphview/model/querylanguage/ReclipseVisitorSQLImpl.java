package de.tu_darmstadt.stg.reclipse.graphview.model.querylanguage;

import de.tu_darmstadt.stg.reclipse.graphview.model.querylanguage.ReclipseParser.DependencyCreatedContext;
import de.tu_darmstadt.stg.reclipse.graphview.model.querylanguage.ReclipseParser.EvaluationExceptionContext;
import de.tu_darmstadt.stg.reclipse.graphview.model.querylanguage.ReclipseParser.EvaluationYieldedContext;
import de.tu_darmstadt.stg.reclipse.graphview.model.querylanguage.ReclipseParser.NodeValueSetContext;
import de.tu_darmstadt.stg.reclipse.logger.DependencyGraphHistoryType;

/**
 * Translates the queries of the REClipse query language to MySQL conditions.
 */
public class ReclipseVisitorSQLImpl extends ReclipseBaseVisitor<String> {

  @Override
  public String visitNodeCreatedQuery(final ReclipseParser.NodeCreatedQueryContext ctx) {
    final String nodeName = ctx.NODE_NAME().getText();
    return createSimpleQuery(DependencyGraphHistoryType.NODE_CREATED, nodeName);
  }

  @Override
  public String visitNodeEvaluatedQuery(final ReclipseParser.NodeEvaluatedQueryContext ctx) {
    final String nodeName = ctx.NODE_NAME().getText();
    final int endedId = DependencyGraphHistoryType.NODE_EVALUATION_ENDED.ordinal();
    final int endedWithExceptionId = DependencyGraphHistoryType.NODE_EVALUATION_ENDED_WITH_EXCEPTION.ordinal();
    return "SELECT pointInTime FROM event JOIN variable ON event.idVariable = variable.idVariable WHERE (event.type = " + endedId + " OR event.type = " + endedWithExceptionId + ") AND variable.variableName = '" + nodeName + "'"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
  }

  @Override
  public String visitNodeValueSet(final NodeValueSetContext ctx) {
    final String nodeName = ctx.NODE_NAME().getText();
    return createSimpleQuery(DependencyGraphHistoryType.NODE_VALUE_SET, nodeName);
  }

  @Override
  public String visitDependencyCreated(final DependencyCreatedContext ctx) {
    final String nodeName1 = ctx.NODE_NAME(0).getText();
    final String nodeName2 = ctx.NODE_NAME(1).getText();
    return "SELECT pointInTime FROM event JOIN variable ON event.idVariable = variable.idVariable JOIN variable AS dependent ON event.dependentVariable = dependent.idVariable WHERE event.type = " + DependencyGraphHistoryType.NODE_ATTACHED.ordinal() + " AND variable.variableName = '" + nodeName1 + "' AND dependent.variableName = '" + nodeName2 + "'"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
  }

  @Override
  public String visitEvaluationYielded(final EvaluationYieldedContext ctx) {
    final String nodeName = ctx.NODE_NAME().getText();
    String value = ctx.VALUE().getText();
    value = value.substring(1, value.length() - 1);

    final DependencyGraphHistoryType type = DependencyGraphHistoryType.NODE_EVALUATION_ENDED;

    return "SELECT pointInTime FROM event JOIN variable ON event.idVariable = variable.idVariable JOIN variable_status ON (event.idVariable = variable_status.idVariable AND event.pointInTime = variable_status.timeFrom) WHERE event.type = " //$NON-NLS-1$
            + type.ordinal() + " AND variable.variableName = '" + nodeName + "' AND variable_status.valueString = '" + value + "'"; //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
  }

  @Override
  public String visitEvaluationException(final EvaluationExceptionContext ctx) {
    if (ctx.NODE_NAME() != null) {
      final String nodeName = ctx.NODE_NAME().getText();
      return createSimpleQuery(DependencyGraphHistoryType.NODE_EVALUATION_ENDED_WITH_EXCEPTION, nodeName);
    }
    else {
      return "SELECT pointInTime FROM event WHERE event.type = " + DependencyGraphHistoryType.NODE_EVALUATION_ENDED_WITH_EXCEPTION.ordinal(); //$NON-NLS-1$
    }
  }

  private String createSimpleQuery(final DependencyGraphHistoryType type, final String variableName) {
    return "SELECT pointInTime FROM event JOIN variable ON event.idVariable = variable.idVariable WHERE event.type = " + type.ordinal() + " AND variable.variableName = '" + variableName + "'"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  }
}
