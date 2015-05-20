package de.tu_darmstadt.stg.reclipse.graphview.model.querylanguage;

import de.tu_darmstadt.stg.reclipse.graphview.model.querylanguage.ReclipseParser.DependencyCreatedContext;
import de.tu_darmstadt.stg.reclipse.graphview.model.querylanguage.ReclipseParser.EvaluationExceptionContext;
import de.tu_darmstadt.stg.reclipse.graphview.model.querylanguage.ReclipseParser.EvaluationYieldedContext;
import de.tu_darmstadt.stg.reclipse.graphview.model.querylanguage.ReclipseParser.NodeValueSetContext;

import java.util.UUID;

import org.antlr.v4.runtime.misc.NotNull;

/**
 * Translates the queries of the REClipse query language to MySQL conditions.
 */
public class ReclipseVisitorEsperImpl extends ReclipseBaseVisitor<String> {

  public ReclipseVisitorEsperImpl() {
  }

  @Override
  public String visitNodeCreatedQuery(@NotNull final ReclipseParser.NodeCreatedQueryContext ctx) {
    final String nodeName = ctx.NODE_NAME().getText();
    // TODO escape nodeName, see
    // https://stackoverflow.com/questions/25268583/how-can-i-sanitize-mysql-query-parameters-in-esper
    return "dependencyGraphHistoryType = de.tu_darmstadt.stg.reclipse.logger.DependencyGraphHistoryType.NODE_CREATED and name = '" + nodeName + "' and active = true"; //$NON-NLS-1$ //$NON-NLS-2$
  }

  @Override
  public String visitNodeEvaluatedQuery(@NotNull final ReclipseParser.NodeEvaluatedQueryContext ctx) {
    final String nodeName = ctx.NODE_NAME().getText();
    // TODO escape nodeName, see
    // https://stackoverflow.com/questions/25268583/how-can-i-sanitize-mysql-query-parameters-in-esper
    return "dependencyGraphHistoryType = de.tu_darmstadt.stg.reclipse.logger.DependencyGraphHistoryType.NODE_EVALUATION_STARTED and name = '" + nodeName + "' and active = true"; //$NON-NLS-1$ //$NON-NLS-2$
  }

  @Override
  public String visitNodeValueSet(final NodeValueSetContext ctx) {
    final String nodeName = ctx.NODE_NAME().getText();
    // TODO escape nodeName, see
    // https://stackoverflow.com/questions/25268583/how-can-i-sanitize-mysql-query-parameters-in-esper
    return "dependencyGraphHistoryType = de.tu_darmstadt.stg.reclipse.logger.DependencyGraphHistoryType.NODE_VALUE_SET and name = '" + nodeName + "' and active = true"; //$NON-NLS-1$ //$NON-NLS-2$
  }

  @Override
  public String visitDependencyCreated(final DependencyCreatedContext ctx) {
    final String nodeName1 = ctx.NODE_NAME(0).getText();
    final String nodeName2 = ctx.NODE_NAME(1).getText();
    // TODO implement
    return "false";
  }

  @Override
  public String visitEvaluationYielded(final EvaluationYieldedContext ctx) {
    final String nodeName = ctx.NODE_NAME().getText();
    String value = ctx.VALUE().getText();
    value = value.substring(1, value.length() - 1);
    // TODO escape nodeName, see
    // https://stackoverflow.com/questions/25268583/how-can-i-sanitize-mysql-query-parameters-in-esper
    return "dependencyGraphHistoryType = de.tu_darmstadt.stg.reclipse.logger.DependencyGraphHistoryType.NODE_EVALUATION_ENDED and name = '" + nodeName + "' and active = true and valueString = '" + value + "'"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  }

  @Override
  public String visitEvaluationException(final EvaluationExceptionContext ctx) {
    final String nodeName = ctx.NODE_NAME().getText();
    // TODO escape nodeName, see
    // https://stackoverflow.com/questions/25268583/how-can-i-sanitize-mysql-query-parameters-in-esper
    return "dependencyGraphHistoryType = de.tu_darmstadt.stg.reclipse.logger.DependencyGraphHistoryType.NODE_EVALUATION_ENDED_WITH_EXCEPTION and name = '" + nodeName + "' and active = true"; //$NON-NLS-1$ //$NON-NLS-2$
  }

}
