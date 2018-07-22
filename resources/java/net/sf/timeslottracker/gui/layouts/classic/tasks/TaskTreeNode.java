package net.sf.timeslottracker.gui.layouts.classic.tasks;

import javax.swing.tree.DefaultMutableTreeNode;

import net.sf.timeslottracker.data.Task;

/**
 * A tree node to represent a node with task in a JTree.
 * 
 * File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700 (Sat, 16 May
 * 2009) $ Last change: $Author: cnitsa $
 */
class TaskTreeNode extends DefaultMutableTreeNode {

  /** <code>true</code> if this node was "cut" to move it to another node **/
  private boolean cut;

  /**
   * Creates a tree node with no parent, no children, but which allows children,
   * and initializes it with the specified user object.
   * 
   * @param task
   *          a task object we want to remember with this tree node
   */
  TaskTreeNode(Task task) {
    super(task);
    this.cut = false;
  }

  /**
   * Returns task remembered with this tree node.
   */
  Task getTask() {
    return (Task) getUserObject();
  }

  /**
   * Sets this node into a "cut" mode.
   * <p>
   * A cut mode means that a user wanted to move a task node into a another
   * node. So, a node must be "cut" and then "pasted" into another position.
   */
  void setCut(boolean cut) {
    this.cut = cut;
  }

  /**
   * Returns if this node is in a "cut" mode.
   */
  boolean isCut() {
    return cut;
  }

}
