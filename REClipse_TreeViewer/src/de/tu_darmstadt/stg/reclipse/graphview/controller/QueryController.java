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
    "nodeEvaluated(<nodeName>)", //$NON-NLS-1$
    "nodeValueSet(<nodeName>)", //$NON-NLS-1$
    "dependencyCreated(<nodeName>, <nodeName>)", //$NON-NLS-1$
    "evaluationYielded(<nodeName>, \"<value>\")", //$NON-NLS-1$
    "evaluationException(<nodeName>?)" //$NON-NLS-1$
  };

  protected final ReactiveTreeView rtv;
  protected List<Integer> matches;

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

      if (query != null) {
        matches = ctx.get().getHistoryEsperAdapter().executeQuery(query);
        if (matches != null && matches.size() > 0) {
          rtv.jumpToPointInTime(matches.get(0));
        }
        else {
          rtv.showInformation("", Texts.Query_NoResults); //$NON-NLS-1$
        }
      }
      else {
        matches = Collections.emptyList();
      }
    }
  }

  public class PrevQueryResultButtonHandler extends SelectionAdapter {

    @Override
    public void widgetSelected(final SelectionEvent e) {
      if (matches == null || matches.size() <= 1) {
        return;
      }
      final int currentPointInTime = rtv.getCurrentSliderValue();
      final int closest = closestMatch(currentPointInTime);
      if (closest < currentPointInTime) {
        rtv.jumpToPointInTime(closest);
      }
      else {
        final int index = matches.indexOf(closest) - 1;
        if (index >= 0 && index < matches.size()) {
          rtv.jumpToPointInTime(matches.get(index));
        }
        else {
          // if we are at the first position, jump to the last match again
          rtv.jumpToPointInTime(matches.get(matches.size() - 1));
        }
      }
    }
  }

  public class NextQueryResultButtonHandler extends SelectionAdapter {

    @Override
    public void widgetSelected(final SelectionEvent e) {
      if (matches == null || matches.size() <= 1) {
        return;
      }
      final int currentPointInTime = rtv.getCurrentSliderValue();
      final int closest = closestMatch(currentPointInTime);
      if (closest > currentPointInTime) {
        rtv.jumpToPointInTime(closest);
      }
      else {
        final int index = matches.indexOf(closest) + 1;
        if (index >= 0 && index < matches.size()) {
          rtv.jumpToPointInTime(matches.get(index));
        }
        else {
          // if we are at the last position, jump to the first match again
          rtv.jumpToPointInTime(matches.get(0));
        }
      }
    }
  }

  protected int closestMatch(final int of) {
    if (matches == null || matches.size() == 0) {
      return -1;
    }
    int minDiff = Integer.MAX_VALUE;
    int closest = of;

    for (final int v : matches) {
      final int diff = Math.abs(v - of);

      if (diff < minDiff) {
        minDiff = diff;
        closest = v;
      }
    }

    return closest;
  }

}
