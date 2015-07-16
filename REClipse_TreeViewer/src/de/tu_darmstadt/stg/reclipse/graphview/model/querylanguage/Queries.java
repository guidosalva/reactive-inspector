package de.tu_darmstadt.stg.reclipse.graphview.model.querylanguage;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;

/**
 * Utility class for parsing and creating REclipse queries.
 *
 */
public class Queries {

  private Queries() {
  }

  public static ReclipseQuery parse(final String queryText) {
    final ReclipseParser parser = createParser(queryText);

    final ParserRuleContext ctx = parser.query();

    return ctx.exception == null ? new ReclipseQuery(queryText, ctx) : null;
  }

  public static ReclipseQuery parse(final String queryText, final BaseErrorListener errorListener) {
    final ReclipseParser parser = createParser(queryText);

    parser.removeErrorListeners();
    parser.addErrorListener(errorListener);

    final ParserRuleContext ctx = parser.query();

    return ctx.exception == null ? new ReclipseQuery(queryText, ctx) : null;
  }

  private static ReclipseParser createParser(final String queryText) {
    final ReclipseLexer lexer = new ReclipseLexer(new ANTLRInputStream(queryText));
    final CommonTokenStream tokens = new CommonTokenStream(lexer);
    return new ReclipseParser(tokens);
  }

}
