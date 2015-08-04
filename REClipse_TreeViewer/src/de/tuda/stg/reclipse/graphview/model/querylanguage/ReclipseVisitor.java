// Generated from Reclipse.g4 by ANTLR 4.4
package de.tuda.stg.reclipse.graphview.model.querylanguage;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ReclipseParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ReclipseVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ReclipseParser#dependencyCreated}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDependencyCreated(@NotNull ReclipseParser.DependencyCreatedContext ctx);
	/**
	 * Visit a parse tree produced by {@link ReclipseParser#nodeCreatedQuery}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNodeCreatedQuery(@NotNull ReclipseParser.NodeCreatedQueryContext ctx);
	/**
	 * Visit a parse tree produced by {@link ReclipseParser#query}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQuery(@NotNull ReclipseParser.QueryContext ctx);
	/**
	 * Visit a parse tree produced by {@link ReclipseParser#evaluationException}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEvaluationException(@NotNull ReclipseParser.EvaluationExceptionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ReclipseParser#nodeValueSet}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNodeValueSet(@NotNull ReclipseParser.NodeValueSetContext ctx);
	/**
	 * Visit a parse tree produced by {@link ReclipseParser#nodeEvaluatedQuery}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNodeEvaluatedQuery(@NotNull ReclipseParser.NodeEvaluatedQueryContext ctx);
	/**
	 * Visit a parse tree produced by {@link ReclipseParser#evaluationYielded}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEvaluationYielded(@NotNull ReclipseParser.EvaluationYieldedContext ctx);
}