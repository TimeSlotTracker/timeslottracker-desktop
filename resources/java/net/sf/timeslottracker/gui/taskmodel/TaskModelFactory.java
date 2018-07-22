package net.sf.timeslottracker.gui.taskmodel;

import java.util.Vector;

import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.utils.TaskIterator;

/**
 * Factory to create task model
 * 
 * @version File version: $Revision: 1107 $, $Date: 2009-08-04 19:26:06 +0700
 *          (Tue, 04 Aug 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class TaskModelFactory {

  private final LayoutManager layoutManager;

  public TaskModelFactory(LayoutManager layoutManager) {
    this.layoutManager = layoutManager;
  }

  public TaskModel createTaskModel() {
    Vector<TaskValue> taskValues = new Vector<TaskValue>();

    TaskIterator taskIterator = new TaskIterator(layoutManager
        .getTimeSlotTracker().getDataSource().getRoot());
    while (taskIterator.hasNext()) {
      Task task = taskIterator.next();

      if (task.isHidden() || task.isRoot()) {
        continue;
      }

      taskValues.add(new TaskValue(task));
    }

    return new TaskModel(taskValues);
  }

}
