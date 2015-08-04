package de.tuda.stg.reclipse.graphview.model;

public interface ISessionSelectionListener {

  void onSessionSelected(SessionContext ctx);

  void onSessionDeselected(SessionContext ctx);
}
