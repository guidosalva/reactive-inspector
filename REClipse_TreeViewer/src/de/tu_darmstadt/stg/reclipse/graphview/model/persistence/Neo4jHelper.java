package de.tu_darmstadt.stg.reclipse.graphview.model.persistence;

import de.tu_darmstadt.stg.reclipse.graphview.model.ISessionConfiguration;
import de.tu_darmstadt.stg.reclipse.logger.ReactiveVariable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class Neo4jHelper {

  public enum RelType implements RelationshipType {
    HAS, DEPENDS, NEXT, BELONGS_TO
  }

  public enum Label implements org.neo4j.graphdb.Label {
    VARIABLE, STATUS, EVENT
  }

  private final String id;
  private final ISessionConfiguration configuration;

  private GraphDatabaseService db;

  private int pointInTime = 0;
  private Node lastEvent;
  private Map<UUID, Node> variables;
  private Map<UUID, Relationship> lastStatusRelations;

  public Neo4jHelper(final String id, final ISessionConfiguration configuration) {
    this.id = id;
    this.configuration = configuration;

    init();
  }

  private void init() {
    final File dbFile = configuration.getDatabaseFilesDir().append(id + ".db").toFile(); //$NON-NLS-1$
    this.db = new GraphDatabaseFactory().newEmbeddedDatabase(dbFile.getAbsolutePath());

    this.variables = new HashMap<>();
    this.lastStatusRelations = new HashMap<>();
  }

  public void createNode(final ReactiveVariable r) {
    try (Transaction tx = db.beginTx()) {
      pointInTime++;

      final Node variable = db.createNode(Label.VARIABLE);
      variable.setProperty("id", r.getId().toString()); //$NON-NLS-1$
      variable.setProperty("name", r.getName());
      variable.setProperty("variableType", r.getReactiveVariableType().ordinal());
      variable.setProperty("typeSimple", r.getTypeSimple());
      variable.setProperty("typeFull", r.getTypeFull());
      variable.setProperty("from", pointInTime);

      final Node status = db.createNode(Label.STATUS);
      status.setProperty("value", r.getValueString());

      final Relationship relationship = variable.createRelationshipTo(status, RelType.HAS);
      relationship.setProperty("from", pointInTime);
      relationship.setProperty("to", Integer.MAX_VALUE);

      variables.put(r.getId(), variable);
      lastStatusRelations.put(r.getId(), relationship);

      createEvent(variable, r);

      tx.success();
    }
  }

  public void attachNode(final ReactiveVariable r, final UUID dependentId) {
    try (Transaction tx = db.beginTx()) {
      pointInTime++;

      final Node variable = findVariableNode(r);
      final Node dependentVariable = db.findNode(Label.VARIABLE, "id", dependentId.toString());

      dependentVariable.createRelationshipTo(variable, RelType.DEPENDS);
      createEvent(variable, r);

      tx.success();
    }
  }

  public void evaluationEnded(final ReactiveVariable r) {
    try (Transaction tx = db.beginTx()) {
      pointInTime++;

      final Node variable = findVariableNode(r);

      final Node status = db.createNode(Label.STATUS);
      status.setProperty("value", r.getValueString());

      updateVariableStatus(r.getId(), variable, status);
      createEvent(variable, r);

      tx.success();
    }
  }

  public void evaluationEndedWithException(final ReactiveVariable r) {
    try (Transaction tx = db.beginTx()) {
      pointInTime++;

      final Node variable = findVariableNode(r);

      final Node status = db.createNode(Label.STATUS);
      status.setProperty("value", r.getValueString());
      status.setProperty("exception", r.getAdditionalInformation());

      updateVariableStatus(r.getId(), variable, status);
      createEvent(variable, r);

      tx.success();
    }
  }

  public void evaluationStarted(final ReactiveVariable r) {
    try (Transaction tx = db.beginTx()) {
      pointInTime++;

      final Node variable = findVariableNode(r);
      createEvent(variable, r);

      tx.success();
    }
  }

  public void valueSet(final ReactiveVariable r) {
    try (Transaction tx = db.beginTx()) {
      pointInTime++;

      final Node variable = db.findNode(Label.VARIABLE, "id", r.getId().toString());

      final Node status = db.createNode(Label.STATUS);
      status.setProperty("value", r.getValueString());

      updateVariableStatus(r.getId(), variable, status);
      createEvent(variable, r);

      tx.success();
    }
  }

  private void updateVariableStatus(final UUID id, final Node variable, final Node status) {
    if (lastStatusRelations.containsKey(id)) {
      final Relationship last = lastStatusRelations.get(id);
      last.setProperty("to", (pointInTime - 1));
    }

    final Relationship relationship = variable.createRelationshipTo(status, RelType.HAS);
    relationship.setProperty("from", pointInTime);
    relationship.setProperty("to", Integer.MAX_VALUE);

    lastStatusRelations.put(id, relationship);
  }

  private void createEvent(final Node variable, final ReactiveVariable r) {
    final Node event = db.createNode(Label.EVENT);
    event.setProperty("time", pointInTime);
    event.setProperty("type", r.getDependencyGraphHistoryType().ordinal());
    event.createRelationshipTo(variable, RelType.BELONGS_TO);

    if (lastEvent != null) {
      lastEvent.createRelationshipTo(event, RelType.NEXT);
      lastEvent = event;
    }
  }

  private Node findVariableNode(final ReactiveVariable r) {
    return variables.get(r.getId());
    // return db.findNode(Label.VARIABLE, "id", r.getId().toString());
  }

  public void close() {
    if (db != null) {
      db.shutdown();
    }
  }

  public int getLastPointInTime() {
    return pointInTime;
  }
}
