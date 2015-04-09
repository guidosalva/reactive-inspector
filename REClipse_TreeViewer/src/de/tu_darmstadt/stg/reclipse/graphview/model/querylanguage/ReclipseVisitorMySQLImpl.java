package de.tu_darmstadt.stg.reclipse.graphview.model.querylanguage;

import de.tu_darmstadt.stg.reclipse.graphview.model.SessionContext;
import de.tu_darmstadt.stg.reclipse.graphview.model.querylanguage.ReclipseParser.DependencyCreatedContext;
import de.tu_darmstadt.stg.reclipse.graphview.model.querylanguage.ReclipseParser.EvaluationExceptionContext;
import de.tu_darmstadt.stg.reclipse.graphview.model.querylanguage.ReclipseParser.EvaluationYieldedContext;
import de.tu_darmstadt.stg.reclipse.graphview.model.querylanguage.ReclipseParser.NodeValueSetContext;
import de.tu_darmstadt.stg.reclipse.logger.DependencyGraphHistoryType;

import java.util.UUID;

import org.antlr.v4.runtime.misc.NotNull;

/**
 * Translates the queries of the REClipse query language to MySQL conditions.
 */
public class ReclipseVisitorMySQLImpl extends ReclipseBaseVisitor<String> {

  private final SessionContext sessionContext;

  public ReclipseVisitorMySQLImpl(final SessionContext sessionContext) {
    this.sessionContext = sessionContext;
  }

  @Override
  public String visitNodeCreatedQuery(@NotNull final ReclipseParser.NodeCreatedQueryContext ctx) {
    final String nodeName = ctx.NODE_NAME().getText();
    // TODO escape nodeName, see
    // https://stackoverflow.com/questions/25268583/how-can-i-sanitize-mysql-query-parameters-in-esper
    return "dependencyGraphHistoryType = " + DependencyGraphHistoryType.NODE_CREATED.ordinal() + " AND name = '" + nodeName + "' AND active = 1"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  }

  @Override
  public String visitNodeEvaluatedQuery(@NotNull final ReclipseParser.NodeEvaluatedQueryContext ctx) {
    final String nodeName = ctx.NODE_NAME().getText();
    // TODO escape nodeName, see
    // https://stackoverflow.com/questions/25268583/how-can-i-sanitize-mysql-query-parameters-in-esper
    return "dependencyGraphHistoryType = " + DependencyGraphHistoryType.NODE_EVALUATION_STARTED.ordinal() + " AND name = '" + nodeName + "' AND active = 1"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  }

  @Override
  public String visitNodeValueSet(final NodeValueSetContext ctx) {
    final String nodeName = ctx.NODE_NAME().getText();
    // TODO escape nodeName, see
    // https://stackoverflow.com/questions/25268583/how-can-i-sanitize-mysql-query-parameters-in-esper
    return "dependencyGraphHistoryType = " + DependencyGraphHistoryType.NODE_VALUE_SET.ordinal() + " AND name = '" + nodeName + "' AND active = 1"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  }

  @Override
  public String visitDependencyCreated(final DependencyCreatedContext ctx) {
    final String nodeName1 = ctx.NODE_NAME(0).getText();
    final String nodeName2 = ctx.NODE_NAME(1).getText();
    final UUID nodeId1 = sessionContext.getDbHelper().getIdFromName(nodeName1);
    final UUID nodeId2 = sessionContext.getDbHelper().getIdFromName(nodeName2);
    if (nodeId1 == null || nodeId2 == null) {
      return "false"; //$NON-NLS-1$
    }
    // TODO escape nodeName, see
    // https://stackoverflow.com/questions/25268583/how-can-i-sanitize-mysql-query-parameters-in-esper
    return "dependencyGraphHistoryType = " + DependencyGraphHistoryType.NODE_ATTACHED.ordinal() + " AND additionalInformation = '" + nodeId1.toString() + "->" + nodeId2.toString() + "'"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
  }

  @Override
  public String visitEvaluationYielded(final EvaluationYieldedContext ctx) {
    final String nodeName = ctx.NODE_NAME().getText();
    String value = ctx.VALUE().getText();
    value = value.substring(1, value.length() - 1);
    // TODO escape nodeName, see
    // https://stackoverflow.com/questions/25268583/how-can-i-sanitize-mysql-query-parameters-in-esper
    return "dependencyGraphHistoryType = " + DependencyGraphHistoryType.NODE_EVALUATION_ENDED.ordinal() + " AND name = '" + nodeName + "' AND active = 1 AND valueString = '" + value + "'"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
  }

  @Override
  public String visitEvaluationException(final EvaluationExceptionContext ctx) {
    final String nodeName = ctx.NODE_NAME().getText();
    // TODO escape nodeName, see
    // https://stackoverflow.com/questions/25268583/how-can-i-sanitize-mysql-query-parameters-in-esper
    return "dependencyGraphHistoryType = " + DependencyGraphHistoryType.NODE_EVALUATION_ENDED_WITH_EXCEPTION.ordinal() + " AND name = '" + nodeName + "' AND active = 1"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  }

}
