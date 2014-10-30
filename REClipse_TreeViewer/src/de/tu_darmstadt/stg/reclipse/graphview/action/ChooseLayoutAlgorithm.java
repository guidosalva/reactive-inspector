package de.tu_darmstadt.stg.reclipse.graphview.action;

import de.tu_darmstadt.stg.reclipse.graphview.Activator;
import de.tu_darmstadt.stg.reclipse.graphview.Texts;
import de.tu_darmstadt.stg.reclipse.graphview.layouts.CustomLayout;
import de.tu_darmstadt.stg.reclipse.graphview.layouts.DirectedGraphLayout;
import de.tu_darmstadt.stg.reclipse.graphview.layouts.GXTreeLayoutAlgorithm;
import de.tu_darmstadt.stg.reclipse.graphview.layouts.GridLayout;
import de.tu_darmstadt.stg.reclipse.graphview.layouts.HorizontalTreeLayout;
import de.tu_darmstadt.stg.reclipse.graphview.layouts.RadialLayout;
import de.tu_darmstadt.stg.reclipse.graphview.layouts.SpringLayout;
import de.tu_darmstadt.stg.reclipse.graphview.layouts.TreeLayout;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.layouts.LayoutAlgorithm;

/**
 * Provides the action to choose one layout algorithm from all the available
 * layout algorithms.
 */
public class ChooseLayoutAlgorithm extends Action {

  protected static abstract class Callback {

    abstract void stateChanged();
  }

  private static class Algorithm {

    final String identifier;
    final LayoutAlgorithm algorithm;
    final String menuText;
    final String tooltip;

    Algorithm(final String id, final LayoutAlgorithm algo, final String txt, final String tt) {
      identifier = id;
      algorithm = algo;
      menuText = txt;
      tooltip = tt;
    }
  }

  private final static List<Algorithm> availableAlgorithms = new LinkedList<>();
  protected static String selectedIdentifier;

  static {
    availableAlgorithms.add(new Algorithm("customLayout", new CustomLayout(), Texts.Layout_Custom_Text, Texts.Layout_Custom_Tooltip)); //$NON-NLS-1$
    availableAlgorithms.add(new Algorithm("gxTreeLayout", new GXTreeLayoutAlgorithm(), Texts.Layout_GXTree_Text, Texts.Layout_GXTree_Tooltip));//$NON-NLS-1$
    availableAlgorithms.add(new Algorithm("treeLayout", new TreeLayout(), Texts.Layout_Tree_Text, Texts.Layout_Tree_Tooltip)); //$NON-NLS-1$
    availableAlgorithms.add(new Algorithm("horizontalTreeLayout", new HorizontalTreeLayout(), Texts.Layout_HorizontalTree_Text, Texts.Layout_HorizontalTree_Tooltip));//$NON-NLS-1$
    availableAlgorithms.add(new Algorithm("radialLayout", new RadialLayout(), Texts.Layout_Radial_Text, Texts.Layout_Radial_Tooltip));//$NON-NLS-1$
    availableAlgorithms.add(new Algorithm("gridLayout", new GridLayout(), Texts.Layout_Grid_Text, Texts.Layout_Grid_Tooltip));//$NON-NLS-1$
    availableAlgorithms.add(new Algorithm("springLayout", new SpringLayout(), Texts.Layout_Spring_Text, Texts.Layout_Spring_Tooltip));//$NON-NLS-1$
    availableAlgorithms.add(new Algorithm("directedGraphLayout", new DirectedGraphLayout(), Texts.Layout_DirectedGraph_Text, Texts.Layout_DirectedGraph_Tooltip));//$NON-NLS-1$
  }

  private static final String PROP_LASTIDENTIFIER = "ChooseLayoutAlgorithm.lastIdentifier"; //$NON-NLS-1$

  private final GraphViewer viewer;
  private final LayoutAlgorithm algorithm;
  protected final String identifier;
  private final Callback callback;

  private ChooseLayoutAlgorithm(final GraphViewer v, final String text, final String tooltip, final LayoutAlgorithm la, final String id, final Callback cb) {
    super(text, IAction.AS_RADIO_BUTTON);
    viewer = v;
    algorithm = la;
    identifier = id;
    callback = cb;

    setToolTipText(tooltip);
  }

  @Override
  public void run() {
    viewer.setLayoutAlgorithm(algorithm, true);
    selectedIdentifier = identifier;
    callback.stateChanged();
  }

  public static void addActions(final MenuManager m, final GraphViewer viewer) {
    final List<ChooseLayoutAlgorithm> actions = new LinkedList<>();

    final Callback cb = new Callback() {

      @Override
      void stateChanged() {
        Activator.getDefault().setProperty(PROP_LASTIDENTIFIER, selectedIdentifier);
        for (final ChooseLayoutAlgorithm a : actions) {
          a.setChecked(a.identifier.equals(selectedIdentifier));
        }
      }
    };

    for (final Algorithm a : availableAlgorithms) {
      final ChooseLayoutAlgorithm act = new ChooseLayoutAlgorithm(viewer, a.menuText, a.tooltip, a.algorithm, a.identifier, cb);
      act.setChecked(a.identifier.equals(selectedIdentifier));
      actions.add(act);
      m.add(act);
    }
  }

  public static LayoutAlgorithm getInitialLayoutAlgorithm() {
    Algorithm initial = null;
    final String lastIdentifier = Activator.getDefault().getProperty(PROP_LASTIDENTIFIER);
    if (lastIdentifier != null) {
      for (final Algorithm a : availableAlgorithms) {
        if (a.identifier.equals(lastIdentifier)) {
          initial = a;
          break;
        }
      }
    }

    if (initial == null) {
      initial = availableAlgorithms.get(0);
    }
    selectedIdentifier = initial.identifier;
    return initial.algorithm;
  }
}
