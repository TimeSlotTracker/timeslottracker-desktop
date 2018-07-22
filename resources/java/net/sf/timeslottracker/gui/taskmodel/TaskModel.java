package net.sf.timeslottracker.gui.taskmodel;

import java.util.Vector;

import javax.swing.DefaultComboBoxModel;

/**
 * Model for storing task values
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-08-04 19:26:06 +0700
 *          (Tue, 04 Aug 2009) $
 * @author Last change: $Author: cnitsa $
 */
@SuppressWarnings("serial")
public class TaskModel extends DefaultComboBoxModel {

  public TaskModel(Vector<TaskValue> taskValues) {
    super(taskValues);
  }

  public void update(TaskValue updated) {
    int index = getTaskValueIndex(updated.getId());

    if (index == -1) {
      return;
    }

    TaskValue old = (TaskValue) getElementAt(index);
    old.updateFrom(updated);

    fireContentsChanged(old, index, index);
  }

  private int getTaskValueIndex(Object id) {
    for (int i = 0; i < getSize(); i++) {
      TaskValue value = (TaskValue) getElementAt(i);

      if (value.getId().equals(id)) {
        return i;
      }
    }
    return -1;
  }
}
