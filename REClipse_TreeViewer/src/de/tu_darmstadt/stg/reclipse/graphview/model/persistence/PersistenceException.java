package de.tu_darmstadt.stg.reclipse.graphview.model.persistence;

public class PersistenceException extends Exception {

  private static final long serialVersionUID = 1L;

  public PersistenceException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public PersistenceException(final String message) {
    super(message);
  }

  public PersistenceException(final Throwable cause) {
    super(cause);
  }
}
