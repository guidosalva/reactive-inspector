package de.tuda.stg.reclipse.graphview.view.graph;

public class StyleProperties {

  private boolean grayedOut;
  private boolean valueChanged;
  private boolean searchResult;

  public boolean isGrayedOut() {
    return grayedOut;
  }

  public void setGrayedOut(final boolean grayedOut) {
    this.grayedOut = grayedOut;
  }

  public boolean isValueChanged() {
    return valueChanged;
  }

  public void setValueChanged(final boolean valueChanged) {
    this.valueChanged = valueChanged;
  }

  public boolean isSearchResult() {
    return searchResult;
  }

  public void setSearchResult(final boolean searchResult) {
    this.searchResult = searchResult;
  }
}
