package de.tu_darmstadt.stg.reclipse.graphview.model.querylanguage;

import org.antlr.v4.runtime.tree.ParseTree;

public class ReclipseQuery {

  private final String queryText;
  private final ParseTree parseTree;

  public ReclipseQuery(final String queryText, final ParseTree parseTree) {
    super();
    this.queryText = queryText;
    this.parseTree = parseTree;
  }

  public String getQueryText() {
    return queryText;
  }

  public ParseTree getParseTree() {
    return parseTree;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((queryText == null) ? 0 : queryText.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final ReclipseQuery other = (ReclipseQuery) obj;
    if (queryText == null) {
      if (other.queryText != null) {
        return false;
      }
    }
    else if (!queryText.equals(other.queryText)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "ReclipseQuery [queryText=" + queryText + "]"; //$NON-NLS-1$//$NON-NLS-2$
  }
}
