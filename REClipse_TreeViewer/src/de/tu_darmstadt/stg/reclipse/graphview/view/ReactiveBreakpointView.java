package de.tu_darmstadt.stg.reclipse.graphview.view;

import de.tu_darmstadt.stg.reclipse.graphview.Activator;
import de.tu_darmstadt.stg.reclipse.graphview.Images;
import de.tu_darmstadt.stg.reclipse.graphview.Texts;
import de.tu_darmstadt.stg.reclipse.graphview.controller.QueryController;
import de.tu_darmstadt.stg.reclipse.graphview.model.BreakpointQueryRegistry;
import de.tu_darmstadt.stg.reclipse.graphview.model.querylanguage.Queries;
import de.tu_darmstadt.stg.reclipse.graphview.model.querylanguage.ReclipseErrorListener;
import de.tu_darmstadt.stg.reclipse.graphview.model.querylanguage.ReclipseQuery;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

public class ReactiveBreakpointView extends ViewPart {

  protected Combo queryTextField;
  protected Table table;
  protected List<ReclipseQuery> queries;

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
    queryButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent e) {
        final String queryText = queryTextField.getText();

        if (queryText == null || queryText.trim().isEmpty()) {
          return;
        }

        final ReclipseQuery query = Queries.parse(queryText, new ReclipseErrorListener(getSite().getShell()));

        if (query != null) {
          queries.add(query);
          BreakpointQueryRegistry.getInstance().addQuery(query);

          addTableItem(query);
          queryTextField.setText(""); //$NON-NLS-1$
        }
      }
    });
    queryTextField.addTraverseListener(new TraverseListener() {

      @Override
      public void keyTraversed(final TraverseEvent e) {
        if (e.detail == SWT.TRAVERSE_RETURN) {
          queryButton.notifyListeners(SWT.Selection, new Event());
        }
      }
    });

    table = new Table(parent, SWT.BORDER);
    table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    final Menu contextMenu = new Menu(getSite().getShell(), SWT.POP_UP);
    table.setMenu(contextMenu);

    final MenuItem removeItem = new MenuItem(contextMenu, SWT.PUSH);
    removeItem.setImage(Activator.getImageDescriptor(Images.REMOVE).createImage());
    removeItem.setText(Texts.Breakpoint_Remove);
    removeItem.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent e) {
        final int index = table.getSelectionIndex();

        if (index == -1) {
          return;
        }

        final ReclipseQuery query = queries.remove(index);
        BreakpointQueryRegistry.getInstance().removeQuery(query);
        table.remove(index);
      }
    });

    final MenuItem removeAllItem = new MenuItem(contextMenu, SWT.PUSH);
    removeAllItem.setImage(Activator.getImageDescriptor(Images.REMOVE_ALL).createImage());
    removeAllItem.setText(Texts.Breakpoint_Remove_All);
    removeAllItem.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent e) {
        queries.clear();
        BreakpointQueryRegistry.getInstance().removeAllQueries();
        table.removeAll();
      }
    });

    queries = new ArrayList<>();
    queries.addAll(BreakpointQueryRegistry.getInstance().getQueries());

    for (final ReclipseQuery query : queries) {
      addTableItem(query);
    }
  }

  protected void addTableItem(final ReclipseQuery query) {
    final TableItem item = new TableItem(table, SWT.NONE);
    item.setImage(Activator.getImageDescriptor(Images.BREAKPOINT).createImage());
    item.setText(query.getQueryText());
  }

  @Override
  public void setFocus() {
    queryTextField.setFocus();
  }

}
