package de.tu_darmstadt.stg.reclipse.graphview.layouts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.AbstractLayoutAlgorithm;
import org.eclipse.zest.layouts.dataStructures.InternalNode;
import org.eclipse.zest.layouts.dataStructures.InternalRelationship;

public class CustomLayout extends AbstractLayoutAlgorithm {

  private static final double HORIZONTAL_SPACING = 20;
  private static final double VERTICAL_SPACING = 20;

  private double maxHeight = 0;
  protected double maxWidth = 0;

  private int lineHeight = 0;
  private int columnWidth = 0;

  public CustomLayout() {
    super(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
  }

  @Override
  public void setLayoutArea(final double x, final double y, final double width, final double height) {
    // do nothing
  }

  @Override
  protected int getCurrentLayoutStep() {
    return 0;
  }

  @Override
  protected int getTotalNumberOfLayoutSteps() {
    return 0;
  }

  @Override
  protected boolean isValidConfiguration(final boolean asynchronous, final boolean continuous) {
    return true;
  }

  @Override
  protected void postLayoutAlgorithm(final InternalNode[] entitiesToLayout, final InternalRelationship[] relationshipsToConsider) {
    // do nothing
  }

  @Override
  protected void preLayoutAlgorithm(final InternalNode[] entitiesToLayout, final InternalRelationship[] relationshipsToConsider, final double x, final double y,
          final double width, final double height) {
    // do nothing
  }

  @Override
  protected void applyLayoutInternal(final InternalNode[] entitiesToLayout, final InternalRelationship[] relationshipsToConsider, final double boundsX, final double boundsY,
          final double boundsWidth, final double boundsHeight) {

    // create new modifiable lists
    final List<InternalNode> entitiesList = new ArrayList<>(Arrays.asList(entitiesToLayout));
    final List<InternalRelationship> relationshipsList = new ArrayList<>(Arrays.asList(relationshipsToConsider));

    // calculate maximum height of nodes
    maxHeight = 0;
    maxWidth = 0;
    for (final InternalNode in : entitiesList) {
      maxHeight = Math.max(maxHeight, in.getHeightInLayout());
      maxWidth = Math.max(maxWidth, in.getWidthInLayout());
    }
    // calculate width and height
    lineHeight = (int) (maxHeight + VERTICAL_SPACING);
    columnWidth = (int) (maxWidth + HORIZONTAL_SPACING);

    // build the node lines without laying out
    final List<List<NodeWrapper>> rows = buildRows(entitiesList, relationshipsList);

    // the tree is build up from bottom, so reverse the row order
    Collections.reverse(rows);

    // layout the nodes

    int currentY = (int) ((rows.size() - 1) * lineHeight - VERTICAL_SPACING);
    boolean first = true;
    for (final List<NodeWrapper> row : rows) {
      // place nodes vertically
      for (final NodeWrapper n : row) {
        n.setY(currentY);
      }

      // place nodes horizontally
      if (first) {
        // place bottom row in a line as a starting point
        // start placing it in the middle
        final int x = (int) ((boundsWidth / 2) - ((row.size() / 2) * columnWidth));
        placeHorizontally(row, x);

        first = false;
      }
      else {
        placeRow(row);
      }
      currentY -= lineHeight;
    }
  }

  /* builds the rows of th graph */
  private List<List<NodeWrapper>> buildRows(final List<InternalNode> nodes, final List<InternalRelationship> relations) {
    final List<List<NodeWrapper>> rows = new ArrayList<>();
    final List<NodeWrapper> firstRow = new ArrayList<>();
    final List<NodeWrapper> secondRow = new ArrayList<>();
    rows.add(firstRow);
    rows.add(secondRow);

    // for children adding we need a reference to the already processed nodes
    final Map<InternalNode, NodeWrapper> nodesMap = new HashMap<>();

    while (!nodes.isEmpty()) {
      final InternalNode root = getRootNode(nodes, relations);

      // lay out the current root node and
      // remove it from the list of entities left
      nodes.remove(root);
      final NodeWrapper rootWrapper = new NodeWrapper(root);
      rootWrapper.setRow(0);
      firstRow.add(rootWrapper);
      nodesMap.put(root, rootWrapper);

      // build the tree that spreads from this current root.
      buildTreeRows(nodesMap, rootWrapper, nodes, relations, secondRow, rows);
    }

    trimEmptyRows(rows);

    moveRowsDown(rows);

    return rows;
  }

  /* Remove rows that are empty */
  private static void trimEmptyRows(final List<List<NodeWrapper>> rows) {
    final List<List<NodeWrapper>> rowsCopy = new ArrayList<>(rows);
    for (final List<NodeWrapper> row : rowsCopy) {
      if (row.isEmpty()) {
        rows.remove(row);
      }
    }
  }

  private static void moveRowsDown(final List<List<NodeWrapper>> rows) {
    // iterate through all rows apart from the last one, because there no move
    // is possible
    for (int rowIndex = 0; rowIndex < rows.size() - 1; rowIndex++) {
      final List<NodeWrapper> row = rows.get(rowIndex);

      final List<NodeWrapper> removalList = new LinkedList<>();
      NodeWrapper previousNode = null;
      // look for each node if it can be moved
      for (final NodeWrapper nw : row) {
        if (nw.canMoveRowDown()) {
          final int newRowIndex = rowIndex + 1;
          final List<NodeWrapper> nextRow = rows.get(newRowIndex);
          final int insertIndex;
          if (previousNode == null) {
            // if no previous node in this line exists,
            // set the node at the start of the next line
            insertIndex = 0;
          }
          else {
            final int maxIndex = previousNode.getMaxChildIndexInRow(nextRow);
            if (maxIndex >= 0) {
              // insert after the children of the previous node
              insertIndex = maxIndex + 1;
            }
            else {
              // set the node at the start of the next line
              insertIndex = 0;
            }
          }
          // insert in the next row
          nextRow.add(insertIndex, nw);
          // set new row index
          nw.setRow(newRowIndex);
          // mark the node for removal
          removalList.add(nw);
        }
        else {
          previousNode = nw;
        }
      }
      // after iterating remove the nodes that were moved
      row.removeAll(removalList);
    }
  }

  /* recursively go through the nodes */
  private void buildTreeRows(final Map<InternalNode, NodeWrapper> nodesMap, final NodeWrapper node, final List<InternalNode> entities, final List<InternalRelationship> relations,
          final List<NodeWrapper> row, final List<List<NodeWrapper>> rows) {

    final List<NodeWrapper> addedNodes = new LinkedList<>();
    final int rowIndex = rows.indexOf(row);

    final List<InternalRelationship> relationsCopy = new ArrayList<>(relations);
    // Orders the children of the currRoot in the given row (the row under it)
    for (final InternalRelationship rel : relationsCopy) {
      if (node.node.equals(rel.getSource())) {
        final InternalNode destNode = rel.getDestination();

        NodeWrapper child = null;
        if (entities.contains(destNode)) {
          // if the destination node hasn't been laid out yet
          // place it in the row
          child = new NodeWrapper(destNode);
          nodesMap.put(destNode, child);
          row.add(child);
          child.setRow(rowIndex);
          addedNodes.add(child);
          entities.remove(destNode);
        }
        else {
          child = nodesMap.get(destNode);
        }
        // set the child relationship for later use
        if (child != null) {
          node.addChild(child);
        }
        // remove the relationship since both ends have been laid out
        relations.remove(rel);
      }
    }

    // if new nodes have been added
    if (addedNodes.size() > 0) {
      final List<NodeWrapper> nextRow;
      // Create a next row if necessary
      if (rows.size() - 1 <= rowIndex) {
        nextRow = new ArrayList<>();
        rows.add(nextRow);
      }
      else {
        nextRow = rows.get(rowIndex + 1);
      }

      for (final NodeWrapper nw : addedNodes) {
        buildTreeRows(nodesMap, nw, entities, relations, nextRow, rows);
      }
    }
  }

  /* return the first node that is not a destination i.e. it's a starting point
     if none such exists we will choose the first available */
  private static InternalNode getRootNode(final List<InternalNode> entitiesList, final List<InternalRelationship> relationshipsList) {
    final List<InternalNode> entitiesLeft = new ArrayList<>(entitiesList);

    // go through all the relationships and remove destination nodes
    for (final InternalRelationship rel : relationshipsList) {
      entitiesLeft.remove(rel.getDestination());
    }
    if (!entitiesLeft.isEmpty()) {
      return entitiesLeft.get(0);
    }

    // if all the nodes were destination nodes then return the first one.
    // just possible in case of circular dependencies.
    return entitiesList.get(0);
  }

  /* calculate the place for each node in the row */
  private void placeRow(final List<NodeWrapper> row) {
    final List<NodeWrapper> childless = new ArrayList<>();
    NodeWrapper last = null;

    final Set<Integer> usedIndices = new HashSet<>();
    for (int j = 0; j < row.size(); j++) {
      final NodeWrapper node = row.get(j);

      if (!node.children.isEmpty()) {
        // if the node has children
        // place the node in the center above them
        int parentX = 0;
        for (final NodeWrapper child : node.children) {
          parentX += child.getX();
        }
        parentX /= node.children.size();
        if (last != null && last.getX() > parentX) {
          parentX = last.getX() + columnWidth;
        }
        while (usedIndices.contains(parentX)) {
          parentX += columnWidth;
        }
        usedIndices.add(parentX);
        node.setX(parentX);

        // and layout the childless collected so far
        if (!childless.isEmpty()) {
          placeChildless(childless, last, node, row.subList(j, row.size() - 1));
          childless.clear();
        }
        last = node;
      }
      else {
        // collect the childless nodes
        childless.add(node);
      }
    }

    if (!childless.isEmpty()) {
      // place childless who are extra on the right as they were not laid out
      // yet
      placeChildless(childless, last, null, new LinkedList<NodeWrapper>());
    }
  }

  private void placeChildless(final List<NodeWrapper> nodes, final NodeWrapper parentLeft, final NodeWrapper parentRight, final List<NodeWrapper> restOfRow) {
    int x_start = 0;

    // There's only a parent on the right
    if (parentLeft == null && parentRight != null) {
      x_start = parentRight.getX() - (columnWidth * nodes.size());
    }
    // there's a parent on the left
    else if (parentLeft != null) {
      x_start = parentLeft.getX() + columnWidth;

      // There's a parent on the right as well
      // meaning the childless are between two parents.
      // We need to make sure there's enough room to place them.
      if (parentRight != null) {
        final int endMark = x_start + (columnWidth * nodes.size());

        if (endMark > parentRight.getX()) {
          // if there isn't enough room to place the childless between the
          // parents
          // shift everything on the right to the right by the missing amount of
          // space.
          final int shiftAmount = endMark - parentRight.getX();
          for (final NodeWrapper nw : restOfRow) {
            nw.shift(shiftAmount);
          }
        }
      }
    }

    // now the room has been assured, place all nodes.
    placeHorizontally(nodes, x_start);
  }

  /* place all nodes in a horizontal line, starting at point x with the given spacing */
  private void placeHorizontally(final List<NodeWrapper> nodes, final int x_start) {
    int x = x_start;
    for (final NodeWrapper item : nodes) {
      item.setX(x);
      x += columnWidth;
    }
  }

  private class NodeWrapper {

    public final InternalNode node;
    public final List<NodeWrapper> children = new ArrayList<>();
    private int rowIndex;
    private int y;
    private int x;

    public NodeWrapper(final InternalNode n) {
      node = n;
    }

    public void setX(final int x_new) {
      x = x_new;
      setLocation();
    }

    public void setY(final int y_new) {
      y = y_new;
      setLocation();
    }

    private void setLocation() {
      final double x_real = x + ((maxWidth - node.getWidthInLayout()) / 2);
      node.setLocation(x_real, y);
    }

    public void addChild(final NodeWrapper n) {
      children.add(n);
    }

    public int getX() {
      return x;
    }

    public void setRow(final int r) {
      rowIndex = r;
    }

    /* shift the node itself and all children by the given amount */
    public void shift(final int amount) {
      setX(x + amount);
      for (final NodeWrapper child : children) {
        child.shift(amount);
      }
    }

    public boolean canMoveRowDown() {
      int minRow = Integer.MAX_VALUE;
      for (final NodeWrapper nw : children) {
        minRow = Math.min(minRow, nw.rowIndex);
      }

      return (minRow != Integer.MAX_VALUE && rowIndex + 1 < minRow);
    }

    public int getMaxChildIndexInRow(final List<NodeWrapper> l) {
      int ret = -1;
      if (l != null) {
        for (final NodeWrapper nw : children) {
          ret = Math.max(ret, l.indexOf(nw));
        }
      }
      return ret;
    }
  }
}