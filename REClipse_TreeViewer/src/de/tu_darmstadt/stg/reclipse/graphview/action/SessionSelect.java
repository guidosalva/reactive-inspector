package de.tu_darmstadt.stg.reclipse.graphview.action;

import de.tu_darmstadt.stg.reclipse.graphview.Activator;
import de.tu_darmstadt.stg.reclipse.graphview.Images;
import de.tu_darmstadt.stg.reclipse.graphview.Texts;
import de.tu_darmstadt.stg.reclipse.graphview.model.SessionContext;
import de.tu_darmstadt.stg.reclipse.graphview.model.SessionManager;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class SessionSelect extends Action implements IMenuCreator {

  // compare sessions by creation date
  private static final Comparator<SessionContext> SESSION_CONTEXT_COMPARATOR = new Comparator<SessionContext>() {

    @Override
    public int compare(final SessionContext o1, final SessionContext o2) {
      return -1 * o1.getCreated().compareTo(o2.getCreated()); // sort descending
    }
  };

  private static final SelectionListener SELECTION_LISTENER = new SelectionListener() {

    @Override
    public void widgetSelected(final SelectionEvent evt) {
      onSelected(evt);
    }

    @Override
    public void widgetDefaultSelected(final SelectionEvent evt) {
      onSelected(evt);
    }

    private void onSelected(final SelectionEvent evt) {
      final MenuItem item = (MenuItem) evt.getSource();
      final UUID sessionId = (UUID) item.getData();
      SessionManager.getInstance().selectSession(sessionId);
    }
  };

  private Menu menu;

  public SessionSelect() {
    super(Texts.SessionSelect_Text, IAction.AS_DROP_DOWN_MENU);

    setToolTipText(Texts.SessionSelect_Tooltip);
    setImageDescriptor(Activator.getImageDescriptor(Images.MONITOR));
    setMenuCreator(this);
  }

  @Override
  public Menu getMenu(final Control parent) {
    if (menu != null) {
      menu.dispose();
    }

    menu = new Menu(parent);

    final List<SessionContext> sessions = new ArrayList<>(SessionManager.getInstance().getSessions());
    Collections.sort(sessions, SESSION_CONTEXT_COMPARATOR);

    for (final SessionContext ctx : sessions) {
      final MenuItem item = new MenuItem(menu, SWT.PUSH);
      item.setText(MessageFormat.format(Texts.SessionSelect_Item, ctx.getCreated()));
      item.setData(ctx.getId());
      item.addSelectionListener(SELECTION_LISTENER);
    }

    return menu;
  }

  @Override
  public Menu getMenu(final Menu parent) {
    throw new UnsupportedOperationException("the menu is designed to be opened by a button"); //$NON-NLS-1$
  }

  @Override
  public void dispose() {
    if (menu != null) {
      menu.dispose();
      menu = null;
    }
  }
}
