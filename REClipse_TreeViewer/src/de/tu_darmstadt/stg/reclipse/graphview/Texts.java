package de.tu_darmstadt.stg.reclipse.graphview;

import org.eclipse.osgi.util.NLS;

public class Texts extends NLS {

  public static String Log_Error;

  public static String SaveImage_Text;
  public static String SaveImage_Tooltip;
  public static String SaveImage_Result;

  public static String Relayout_Text;
  public static String Relayout_Tooltip;

  public static String ZoomIn_Text;
  public static String ZoomIn_Tooltip;
  public static String ZoomOut_Text;
  public static String ZoomOut_Tooltip;

  public static String VariableNode_Collapsed;
  public static String VariableNode_Fulltype;
  public static String VariableNode_Name;
  public static String VariableNode_Id;
  public static String VariableNode_Value;
  public static String VariableNode_AdditionKeyPostfix;

  public static String Menu_Layouts;
  public static String Layout_Custom_Text;
  public static String Layout_Custom_Tooltip;
  public static String Layout_GXTree_Text;
  public static String Layout_GXTree_Tooltip;
  public static String Layout_Tree_Text;
  public static String Layout_Tree_Tooltip;
  public static String Layout_HorizontalTree_Text;
  public static String Layout_HorizontalTree_Tooltip;
  public static String Layout_Radial_Text;
  public static String Layout_Radial_Tooltip;
  public static String Layout_Grid_Text;
  public static String Layout_Grid_Tooltip;
  public static String Layout_Spring_Text;
  public static String Layout_Spring_Tooltip;
  public static String Layout_DirectedGraph_Text;
  public static String Layout_DirectedGraph_Tooltip;

  public static String Query_Submit;
  public static String Query_NoResults;

  private static final String BUNDLE_NAME = Texts.class.getPackage().getName() + ".texts"; //$NON-NLS-1$

  static {
    NLS.initializeMessages(BUNDLE_NAME, Texts.class);
  }

  private Texts() {
  }
}
