package de.tuda.stg.reclipse.graphview.model.querylanguage;

import de.tuda.stg.reclipse.graphview.Texts;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ReclipseErrorListener extends BaseErrorListener {

  protected final Shell shell;

  public ReclipseErrorListener(final Shell shell) {
    this.shell = shell;
  }

  @Override
  public void syntaxError(final Recognizer<?, ?> recognizer, final Object offendingSymbol, final int line, final int charPositionInLine, final String msg,
          final RecognitionException e) {
    Display.getDefault().asyncExec(new Runnable() {

      @Override
      public void run() {
        MessageDialog.openError(shell, Texts.Query_ParsingError_Title, msg);
      }
    });
  }

}
