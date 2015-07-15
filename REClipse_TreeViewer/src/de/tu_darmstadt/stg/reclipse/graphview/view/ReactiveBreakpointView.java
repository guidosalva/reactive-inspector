package de.tu_darmstadt.stg.reclipse.graphview.view;

import de.tu_darmstadt.stg.reclipse.graphview.Texts;
import de.tu_darmstadt.stg.reclipse.graphview.controller.QueryController;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

public class ReactiveBreakpointView extends ViewPart {

  protected Combo queryTextField;

  @Override
  public void createPartControl(final Composite parent) {
    parent.setLayout(new GridLayout());

    final Composite queryComposite = new Composite(parent, SWT.NONE);
    queryComposite.setLayout(new GridLayout(2, false));
    queryComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

    queryTextField = new Combo(queryComposite, SWT.DROP_DOWN);
    queryTextField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    queryTextField.setItems(QueryController.QUERY_TEMPLATES);

    final Button queryButton = new Button(queryComposite, SWT.PUSH);
    queryButton.setText(Texts.BreakpointQuery_Add);

    final Table table = new Table(parent, SWT.NONE);
    table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    for (int i = 0; i < 10; i++) {
      final TableItem item = new TableItem(table, SWT.NONE);
      item.setText("Breakpoint " + i);
    }
  }

  @Override
  public void setFocus() {
    queryTextField.setFocus();
  }

}
