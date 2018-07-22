package net.sf.timeslottracker.gui.layouts.classic.tasksbydays;

import net.sf.timeslottracker.data.Task;

/**
 * Node for task
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
@SuppressWarnings("serial")
public class TaskNode extends DaysTreeNode {

  public TaskNode(Task task) {
    super(task);
  }

  public Task getTask() {
    return (Task) getUserObject();
  }
}
