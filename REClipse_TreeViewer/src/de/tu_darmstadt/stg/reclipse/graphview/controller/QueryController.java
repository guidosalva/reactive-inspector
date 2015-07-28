package de.tu_darmstadt.stg.reclipse.graphview.controller;

import de.tu_darmstadt.stg.reclipse.graphview.Texts;
import de.tu_darmstadt.stg.reclipse.graphview.model.SessionContext;
import de.tu_darmstadt.stg.reclipse.graphview.model.SessionManager;
import de.tu_darmstadt.stg.reclipse.graphview.model.querylanguage.Queries;
import de.tu_darmstadt.stg.reclipse.graphview.model.querylanguage.ReclipseErrorListener;
import de.tu_darmstadt.stg.reclipse.graphview.model.querylanguage.ReclipseQuery;
import de.tu_darmstadt.stg.reclipse.graphview.view.ReactiveTreeView;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

/**
 * Controller which handles all events related to the querying feature (e.g.
 * submit/previous/next actions).
 */
public class QueryController {

  public static final String[] QUERY_TEMPLATES = new String[] {
      "nodeCreated(<nodeName>)", //$NON-NLS-1$
      "nodeEvaluated(<signalName>)", //$NON-NLS-1$
      "nodeValueSet(<varName>)", //$NON-NLS-1$
      "dependencyCreated(<nodeName>, <nodeName>)", //$NON-NLS-1$
      "evaluationYielded(<nodeName>, \"<value>\")", //$NON-NLS-1$
      "evaluationException(<nodeName>?)" //$NON-NLS-1$
  };

  protected final ReactiveTreeView rtv;
  protected List<Integer> matches;
  protected int selection = 0;

  public QueryController(final ReactiveTreeView reactiveTreeView) {
    rtv = reactiveTreeView;
  }

  public class SubmitQueryButtonHandler extends SelectionAdapter {

    @Override
    public void widgetSelected(final SelectionEvent e) {
      final Optional<SessionContext> ctx = SessionManager.getInstance().getSelectedSession();

      if (!ctx.isPresent()) {
        return;
      }

      final String queryText = rtv.getQueryText();

      if (queryText == null || queryText.trim().isEmpty()) {
        return;
      }

      final ReclipseQuery query = Queries.parse(queryText, new ReclipseErrorListener(rtv.getSite().getShell()));

      selection = 0;

      if (query != null) {
        matches = ctx.get().getHistoryEsperAdapter().executeQuery(query);
        if (matches != null && matches.size() > 0) {
          Collections.sort(matches);
          rtv.jumpToPointInTime(matches.get(0));
        }
        else {
          rtv.showInformation("", Texts.Query_NoResults); //$NON-NLS-1$
        }
      }
      else {
        matches = Collections.emptyList();
      }

      rtv.updateQueryResultsLabel();
    }
  }

  public class PrevQueryResultButtonHandler extends SelectionAdapter {

    @Override
    public void widgetSelected(final SelectionEvent e) {
      if (matches == null || matches.size() <= 1) {
        return;
      }

      selection = Math.floorMod(selection - 1, matches.size());

      rtv.jumpToPointInTime(matches.get(selection));
      rtv.updateQueryResultsLabel();
    }
  }

  public class NextQueryResultButtonHandler extends SelectionAdapter {

    @Override
    public void widgetSelected(final SelectionEvent e) {
      if (matches == null || matches.isEmpty()) {
        return;
      }

      selection = Math.floorMod(selection + 1, matches.size());

      rtv.jumpToPointInTime(matches.get(selection));
      rtv.updateQueryResultsLabel();
    }
  }

  public void reset() {
    matches = Collections.emptyList();
    selection = 0;
  }

  public int getResultCount() {
    return matches != null ? matches.size() : 0;
  }

  public int getCurrentResultSelection() {
    return (matches != null && matches.size() > 0) ? (selection + 1) : 0;
  }

}
