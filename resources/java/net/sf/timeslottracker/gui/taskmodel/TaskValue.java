package net.sf.timeslottracker.gui.taskmodel;

import net.sf.timeslottracker.data.Task;

/**
 * Task value object for daily table model
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-08-04 19:26:06 +0700
 *          (Tue, 04 Aug 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class TaskValue {
  private final Object id;

  private String name;

  private String parentName;

  public TaskValue(Task task) {
    this(task.getId(), task.getName(), getParentName(task));
  }

  private static String getParentName(Task task) {
    return task.getParentTask() == null ? null : task.getParentTask().getName();
  }

  private TaskValue(Object id, String name, String parentName) {
    this.id = id;
    this.name = name;
    this.parentName = parentName;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof TaskValue)) {
      return false;
    }

    TaskValue another = (TaskValue) obj;
    return id.equals(another.id);
  }

  public Object getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public String toString() {
    return parentName == null ? name : name + " - " + parentName;
  }

  public void updateFrom(TaskValue updated) {
    name = updated.name;
    parentName = updated.parentName;
  }

}
