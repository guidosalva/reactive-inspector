package de.tu_darmstadt.stg.reclipse.graphview.layouts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.AbstractLayoutAlgorithm;
import org.eclipse.zest.layouts.dataStructures.InternalNode;
import org.eclipse.zest.layouts.dataStructures.InternalRelationship;

/**
 * A layout algorithm that spreads the directed graph as a tree from top down
 * first a root is chosen (we offer a default way that may be overridden) then a
 * tree is first spread from that root down in a grid fashion after which it is
 * laid out bottom up to horizontally shift nodes. if the graph contains non
 * connected subgraphs then the process of choosing a root and spreading it's
 * child tree is continued.
 * 
 * @author ilmta
 */
public class GXTreeLayoutAlgorithm extends AbstractLayoutAlgorithm {

  private double horSpacing = 40;
  private double verSpacing = 60;

  public GXTreeLayoutAlgorithm() {
    super(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
  }

  @Override
  protected void applyLayoutInternal(final InternalNode[] entitiesToLayout, final InternalRelationship[] relationshipsToConsider, final double boundsX, final double boundsY,
          final double boundsWidth, final double boundsHeight) {

    final List<InternalNode> entitiesList = asList(entitiesToLayout);
    final List<InternalRelationship> relationshipsList = asList(relationshipsToConsider);

    // --- build the node matrix without laying out first ---//
    final List<List<GXMoreThanNode>> rows = buildNodeMatrix(entitiesList, relationshipsList);

    // --- layout the nodes --- //

    // we're going to put them together bottom - up.
    Collections.reverse(rows);

    // the vertical size of a node line
    final int verticalLineSize = (int) (entitiesToLayout[0].getLayoutEntity().getHeightInLayout() + verSpacing);

    // the vertical location we begin placing the nodes from
    int heightSoFar = (int) (((rows.size() - 1) * verticalLineSize) + verSpacing);

    // place bottom most row first - just center it.
    final List<GXMoreThanNode> childRow = rows.get(0);
    final int nodeSpacing = (int) (horSpacing + childRow.get(0).getNode().getLayoutEntity().getWidthInLayout());
    final int x = (int) ((boundsWidth / 2) - ((childRow.size() / 2) * nodeSpacing));
    placeStrand(childRow, x, heightSoFar, nodeSpacing);

    // place parent rows
    List<GXMoreThanNode> parentRow;

    // run through all parent rows
    for (int i = 1; i < rows.size(); i++) {

      // initialize stuff we'll need
      parentRow = rows.get(i);
      heightSoFar -= verticalLineSize;
      placeRow(parentRow, heightSoFar);
    }
  }

  /**
   * Lays out row with respect to it's children.
   * 
   * @param yLocation
   *          - the vertical location to start placing the nodes.
   * @param row
   *          - the row who's nodes we'd like to lay out.
   */
  private void placeRow(final List<GXMoreThanNode> row, final int yLocation) {
    final List<GXMoreThanNode> childlessStrand = new ArrayList<>();
    GXMoreThanNode parentLeft = null;

    // for each parent in this parent row
    for (int j = 0; j < row.size(); j++) {
      final GXMoreThanNode parentRight = row.get(j);
      // if the node has children
      if (!parentRight.getChildren().isEmpty()) {

        // place the node in the center above his children
        int parentX = 0;
        for (final GXMoreThanNode child : parentRight.getChildren()) {
          parentX += child.getX();
        }
        parentX /= parentRight.getChildren().size();
        parentRight.setLocation(parentX, yLocation);

        // and layout the childless strand
        if (!childlessStrand.isEmpty()) {
          placeChildless(childlessStrand, parentLeft, parentRight, yLocation);
          childlessStrand.clear();
        }
        parentLeft = parentRight;

      }
      else { // accumulate the childless nodes
        childlessStrand.add(parentRight);
      }
    }

    // place childless who are extra on the right. as in did not get taken care
    // of when
    // the parents were laid out.
    if (!childlessStrand.isEmpty()) {
      placeChildless(childlessStrand, parentLeft, null, yLocation);
    }
  }

  /**
   * Builds the matrix of nodes. each node will be wrapped with necessary
   * innformation such as who are the direct children and parent (only visually
   * in the graph not the actual relationships) the returned matrix is organized
   * more or less as the real tree.
   * 
   * @param entitiesList
   *          - entities to place in the matrix
   * @param relationshipsList
   *          - the relationships between the entities given
   * 
   * @return the matrix - (a list of rows of nodes)
   */
  private List<List<GXMoreThanNode>> buildNodeMatrix(final List<InternalNode> entitiesList, final List<InternalRelationship> relationshipsList) {
    final List<List<GXMoreThanNode>> rows = new ArrayList<>();
    while (!entitiesList.isEmpty()) {
      final InternalNode root = getFirstEntity(entitiesList, relationshipsList);

      // add root row if necessary
      if (rows.isEmpty()) {
        rows.add(new ArrayList<GXMoreThanNode>());
        rows.add(new ArrayList<GXMoreThanNode>());
      }

      // lay out the current root node and remove it from the list of entities
      // left
      entitiesList.remove(root);
      final GXMoreThanNode moreThanRoot = new GXMoreThanNode(root);
      rows.get(0).add(moreThanRoot);

      // build the tree that spreads from this current root.
      builtTreeFromRoot(moreThanRoot, entitiesList, relationshipsList, rows.get(1), rows);
    }

    trimEmptyRows(rows);

    return rows;
  }

  /**
   * Remove rows that are empty. This should only be the last row but since it's
   * not too expensive better safe than sorry.
   * 
   * @param rows
   *          - to trim
   */
  private static void trimEmptyRows(final List<List<GXMoreThanNode>> rows) {
    final List<List<GXMoreThanNode>> rowsCopy = new ArrayList<>(rows);
    for (final List<GXMoreThanNode> row : rowsCopy) {
      if (row.isEmpty()) {
        rows.remove(row);
      }
    }
  }

  /**
   * A childless stran is a "problem" in general because they break the evenness
   * of the tree There are three types of such strands, extra nodes to the left,
   * extra to the right, or extra in between parents. This method places those
   * strands in spot.
   * 
   * @param childlessStrand
   *          - the childless node to be laid out.
   * @param parentLeft
   *          - the nearest parent on the left (or <value>null</value> if none
   *          such exists)
   * @param parentRight
   *          - the nearest parent on the right (or <value>null</value> if none
   *          such exists)
   * @param yLoc
   *          - the vertical location to lay out the nodes on.
   */
  private void placeChildless(final List<GXMoreThanNode> childlessStrand, final GXMoreThanNode parentLeft, final GXMoreThanNode parentRight, final int yLoc) {
    int startMark = 0;
    final int spacing = (int) (childlessStrand.get(0).getNode().getLayoutEntity().getWidthInLayout() + horSpacing);

    // There's only a parent on the right
    if (parentLeft == null && parentRight != null) {
      startMark = parentRight.getX() - (spacing * childlessStrand.size());
    } // there's a parent on the left
    else if (parentLeft != null) {
      startMark = parentLeft.getX() + spacing;

      // there's a parent on the right as well meaning the childless are between
      // two parents
      // we need to make there's enough room to place them
      if (parentRight != null) {
        final int endMark = startMark + (spacing * childlessStrand.size());

        // if there isn't enough room to place the childless between the parents
        if (endMark > parentRight.getX()) {
          // shift everything on the right to the right by the missing amount of
          // space.
          shiftTreesRightOfMark(parentRight, endMark - parentRight.getX());
        }
      }
    }

    // now the room has been assured, place strand.
    placeStrand(childlessStrand, startMark, yLoc, spacing);
  }

  /**
   * Shifts the trees right of mark node
   * 
   * @param mark
   *          to shift from
   * @param shift
   *          - factor by which to move right by.
   */
  private void shiftTreesRightOfMark(final GXMoreThanNode mark, final int shift) {
    mark.setLocation(mark.getX() + shift, mark.getY());
    final GXMoreThanNode leftMostChild = getRightMostChild(mark);
    final List<GXMoreThanNode> treeRoots = leftMostChild.getRow().subList(leftMostChild.getRow().indexOf(leftMostChild), leftMostChild.getRow().size());
    for (final GXMoreThanNode root : treeRoots) {
      shiftTree(root, shift);
    }
  }

  /**
   * Returns the right most child of parent
   * 
   * @param parent
   * 
   * @return the right most child of parent given.
   */
  private static GXMoreThanNode getRightMostChild(final GXMoreThanNode parent) {
    GXMoreThanNode rightMost = parent.getChildren().get(0);

    // run through children
    for (final GXMoreThanNode child : parent.getChildren()) {
      if (child.getX() < rightMost.getX()) {
        rightMost = child;
      }
    }
    return rightMost;
  }

  /**
   * Shifts the given tree by the shift factor given to the right.
   * 
   * @param root
   *          - root of tree to shift
   * @param shift
   *          - factor to shirt by
   */
  private void shiftTree(final GXMoreThanNode root, final int shift) {
    root.setLocation(root.getX() + shift, root.getY());
    for (final GXMoreThanNode child : root.getChildren()) {
      shiftTree(child, shift);
    }
  }

  /**
   * Places the list of nodes horizontally at the given point and spaced on
   * horizontally by given spacing.
   * 
   * @param strand
   *          - list of nodes to be laid out.
   * @param x
   *          - location to begin the placing
   * @param y
   *          - vertical location to lay out the entire list.
   * @param spacing
   *          the horizontal spacing between nodes.
   */
  private static void placeStrand(final List<GXMoreThanNode> strand, final int x_start, final int y, final int spacing) {
    int x = x_start;
    for (final GXMoreThanNode item : strand) {
      item.setLocation(x, y);
      x += spacing;
    }
  }

  /**
   * follows the root by all its children to wrap the all up with all the extra
   * info needed and adds the tree to the matrix.
   * 
   * @param currRoot
   *          - root to go over tree from
   * @param entitiesList
   *          - entities available
   * @param relationshipsList
   *          - relationship between the given entities
   * @param currRow
   *          - the current row in the matrix we are working on
   * @param rows
   *          - the matrix.
   */
  private void builtTreeFromRoot(final GXMoreThanNode currRoot, final List<InternalNode> entitiesList, final List<InternalRelationship> relationshipsList,
          final List<GXMoreThanNode> currRow, final List<List<GXMoreThanNode>> rows) {

    // this is the first node that comes from the currRoot
    // we'll use the mark to know where to continue laying out from
    GXMoreThanNode mark = null;

    final List<InternalRelationship> relationshipsListCopy = new ArrayList<>(relationshipsList);
    // Orders the children of the currRoot in the given row (the row under it)
    for (final InternalRelationship rel : relationshipsListCopy) {
      if (currRoot.getNode().equals(rel.getSource())) {
        final InternalNode destNode = rel.getDestination();

        // if the destination node hasn't been laid out yet
        if (entitiesList.contains(destNode)) {

          // place it in the row (as in lay it out)
          final GXMoreThanNode currNode = new GXMoreThanNode(destNode);
          currRoot.addChild(currNode);
          currRow.add(currNode);
          currNode.addedToRow(currRow);
          entitiesList.remove(destNode);

          // if this is the first node, save it as a mark.
          if (mark == null) {
            mark = currNode;
          }

          // remove the relationship since both of its ends have been laid out.
          relationshipsList.remove(rel);
        }
      }
    }

    // if new children have been added
    if (mark != null) {

      // Create a next row if necessary
      if (rows.size() - 1 <= rows.indexOf(currRow)) {
        rows.add(new ArrayList<GXMoreThanNode>());
      }

      final List<GXMoreThanNode> nextRow = rows.get(rows.indexOf(currRow) + 1);
      for (int i = currRow.indexOf(mark); i < currRow.size(); i++) {
        builtTreeFromRoot(currRow.get(i), entitiesList, relationshipsList, nextRow, rows);
      }

    }
  }

  /**
   * Currently we will arbitrarily choose the first node that is not a
   * destination i.e. it's a starting point\ if none such exists we will choose
   * a random node.
   * 
   * @param entitiesList
   * @param relationshipsList
   */
  private static InternalNode getFirstEntity(final List<InternalNode> entitiesList, final List<InternalRelationship> relationshipsList) {
    final List<InternalNode> entitiesLeft = new ArrayList<>(entitiesList);

    // go through all the relationships and remove destination nodes
    for (final InternalRelationship rel : relationshipsList) {
      entitiesLeft.remove(rel.getDestination());
    }
    if (!entitiesLeft.isEmpty()) {
      // throw in random for fun of it
      // return entitiesLeft.get((int)(Math.round(Math.random()*(entitiesLef
      // t.size()-1))));
      return entitiesLeft.get(0);
    }
    // if all the nodes were destination nodes then return a random node.
    // return entitiesList.get((int)(Math.round(Math.random()*(entitiesLis
    // t.size()-1))));
    return entitiesList.get(0);
  }

  /**
   * Returns an ArrayList containing the elements from the given array. It's in
   * a separate method and to make sure a new ArrayList is created since
   * Arrays.asList(T ...) returns an unmodifiable array and throws runtime
   * exceptions when an innocent programmer is trying to manipulate the
   * resulting list.
   * 
   * @param <T>
   * @param entitiesToLayout
   * @return
   */
  private static <T> List<T> asList(final T[] entitiesToLayout) {
    return new ArrayList<>(Arrays.asList(entitiesToLayout));
  }

  @Override
  protected int getCurrentLayoutStep() {
    // do nothing
    return 0;
  }

  @Override
  protected int getTotalNumberOfLayoutSteps() {
    // do nothing
    return 0;
  }

  @Override
  protected boolean isValidConfiguration(final boolean asynchronous, final boolean continuous) {
    // do nothing
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
    // entitiesToLayout[0].getLayoutEntity().

  }

  @Override
  public void setLayoutArea(final double x, final double y, final double width, final double height) {
    // do nothing
  }

  public double getHorSpacing() {
    return horSpacing;
  }

  public void setHorSpacing(final double horSpace) {
    this.horSpacing = horSpace;
  }

  public double getVerSpacing() {
    return verSpacing;
  }

  public void setVerSpacing(final double verSpace) {
    this.verSpacing = verSpace;
  }

  /**
   * wraps a node with useful info like parent and children etc.
   */
  private class GXMoreThanNode {

    private final InternalNode m_node;
    private final List<GXMoreThanNode> m_children;
    private List<GXMoreThanNode> m_row;
    private int m_y;
    private int m_x;

    public GXMoreThanNode(final InternalNode node) {
      m_node = node;
      m_children = new ArrayList<>();
    }

    public void addedToRow(final List<GXMoreThanNode> row) {
      m_row = row;
    }

    public void setLocation(final int x, final int y) {
      m_x = x;
      m_y = y;
      m_node.setLocation(x, y);
    }

    public void addChild(final GXMoreThanNode currNode) {
      m_children.add(currNode);
    }

    public List<GXMoreThanNode> getChildren() {
      return m_children;
    }

    public InternalNode getNode() {
      return m_node;
    }

    public int getY() {
      return m_y;
    }

    public int getX() {
      return m_x;
    }

    public List<GXMoreThanNode> getRow() {
      return m_row;
    }
  }
}