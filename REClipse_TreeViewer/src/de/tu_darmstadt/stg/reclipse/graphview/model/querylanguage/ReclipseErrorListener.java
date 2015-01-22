package de.tu_darmstadt.stg.reclipse.graphview.model.querylanguage;

import de.tu_darmstadt.stg.reclipse.graphview.view.ReactiveTreeView;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.eclipse.swt.widgets.Display;

public class ReclipseErrorListener extends BaseErrorListener {

  protected final ReactiveTreeView rtv;

  public ReclipseErrorListener(final ReactiveTreeView theRtv) {
    rtv = theRtv;
  }

  @Override
  public void syntaxError(final Recognizer<?, ?> recognizer, final Object offendingSymbol, final int line, final int charPositionInLine, final String msg,
          final RecognitionException e) {
    Display.getDefault().asyncExec(new Runnable() {

      @Override
      public void run() {
        rtv.showError("", msg); //$NON-NLS-1$
      }
    });
  }

}
