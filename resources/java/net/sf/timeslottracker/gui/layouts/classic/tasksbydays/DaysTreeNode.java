package net.sf.timeslottracker.gui.layouts.classic.tasksbydays;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Days tree node. Represent the task, year, month, day.
 * 
 * @version $Revision: 998 $ $Date: 2009-05-16 08:53:21 +0700 (Sat, 16 May 2009)
 *          $ $Author: cnitsa $
 */
public abstract class DaysTreeNode extends DefaultMutableTreeNode {

  public DaysTreeNode(Object object) {
    super(object);
  }

}
