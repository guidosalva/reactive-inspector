package de.tuda.stg.reclipse.graphview;

import org.eclipse.osgi.util.NLS;

public class Texts extends NLS {

  public static String AbsolutePerformanceLatestViewMode;
  public static String AbsolutePerformanceSumViewMode;

  public static String Log_Error;

  public static String SaveImage_Text;
  public static String SaveImage_Tooltip;
  public static String SaveImage_Error;
  public static String SaveImage_Result;

  public static String RelativePerformanceViewMode;

  public static String Relayout_Text;
  public static String Relayout_Tooltip;

  public static String ZoomIn_Text;
  public static String ZoomIn_Tooltip;
  public static String ZoomOut_Text;
  public static String ZoomOut_Tooltip;

  public static String Show_Heatmap;
  public static String SessionSelect_Text;
  public static String SessionSelect_Tooltip;
  public static String SessionSelect_Item;

  public static String ShowClassName_Text;
  public static String ShowClassName_Tooltip;

  public static String VariableNode_Collapsed;
  public static String VariableNode_Fulltype;
  public static String VariableNode_Name;
  public static String VariableNode_Id;
  public static String VariableNode_Value;
  public static String VariableNode_AdditionKeyPostfix;

  public static String Menu_Layouts;
  public static String Layout_Hierarchical_Text;
  public static String Layout_Hierarchical_Tooltip;

  public static String Query_Submit;
  public static String Query_NoResults;
  public static String Query_ParsingError_Title;

  public static String Search_Results;

  public static String MenuItem_Collapse_Fold;
  public static String MenuItem_Collapse_Unfold;
  public static String MenuItem_Highlighter_Highlight;
  public static String MenuItem_Highlighter_RemoveHighlight;
  public static String MenuItem_Highlighter_Ancestors;
  public static String MenuItem_Highlighter_Children;
  public static String MenuItem_Breakpoint_Enable;
  public static String MenuItem_Breakpoint_Disable;
  public static String MenuItem_Locate;

  public static String Pref_Description;
  public static String Pref_UpdateInterval;
  public static String Pref_EventLogging;

  public static String Graph_Tooltip_Name;
  public static String Graph_Tooltip_Type;
  public static String Graph_Tooltip_Value;
  public static String Graph_Tooltip_Exception;
  public static String Graph_Tooltip_Class;
  public static String Graph_Tooltip_Source;
  public static String Graph_Tooltip_Line;

  public static String BreakpointQuery_Add;
  public static String Breakpoint_Remove;
  public static String Breakpoint_Remove_All;

  public static String DefaultViewMode;
  public static String ViewMode_ToolTip;

  private static final String BUNDLE_NAME = Texts.class.getPackage().getName() + ".texts"; //$NON-NLS-1$

  static {
    NLS.initializeMessages(BUNDLE_NAME, Texts.class);
  }

  private Texts() {
  }
}
