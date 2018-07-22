package net.sf.timeslottracker.gui.dnd.handlers;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import net.sf.timeslottracker.core.TimeSlotTracker;
import net.sf.timeslottracker.data.DataSource;
import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.dnd.DataFlavors;
import net.sf.timeslottracker.gui.dnd.selections.TaskSelection;

/**
 * Task's routines for Dnd actions
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class TaskHandler {
  private final TimeSlotTracker timeSlotTracker;
  private final LayoutManager layoutManager;

  public TaskHandler(LayoutManager layoutManager) {
    this.timeSlotTracker = layoutManager.getTimeSlotTracker();
    this.layoutManager = layoutManager;
  }

  /**
   * Move task to new target parent. And reorder in target child list (if set
   * (!=-1) newNodeIndex param)
   * 
   * @param t
   *          source task
   * @param targetTask
   *          target task for moving
   * @param newNodeIndex
   *          set order position after moving to target task. Value -1 means
   *          don't reorder, just move
   * @return flag of successful operation
   */
  public boolean moveData(Transferable t, Task targetTask, int newNodeIndex) {
    if (!t.isDataFlavorSupported(DataFlavors.TASK)) {
      return false;
    }

    Task sourceTask = getTask(t);

    if (sourceTask.equals(targetTask)) {
      return false;
    }

    DataSource dataSource = layoutManager.getTimeSlotTracker().getDataSource();
    if (dataSource != null) {
      if (newNodeIndex == -1) {
        dataSource.moveTask(sourceTask, targetTask);
      } else {
        if (!sourceTask.getParentTask().equals(targetTask.getParentTask())) {
          dataSource.moveTask(sourceTask, targetTask.getParentTask());
        }
      }

      layoutManager.getTimeSlotTracker().fireTaskChanged(sourceTask);

      if (newNodeIndex != -1) {
        dataSource.moveTask(sourceTask, newNodeIndex);
        layoutManager.getTimeSlotTracker().fireTaskChanged(sourceTask);
      }
    }

    Utils.clearClipboard();

    return true;
  }

  /**
   * Copy task to new target parent. And reorder in target child list (if set
   * (!=-1) newNodeIndex param)
   * 
   * @param t
   *          source task
   * @param targetTask
   *          target task for copying
   * @param newNodeIndex
   *          set order position after copying to target task. Value -1 means
   *          don't reorder, just copy
   * @param copyWithChildren
   *          copy with children
   * @return new task or null if error arised
   */
  public Task copyData(Transferable t, Task targetTask, int newNodeIndex,
      boolean copyWithChildren) {
    if (!t.isDataFlavorSupported(DataFlavors.TASK)) {
      return null;
    }

    Task newTask = null;
    DataSource dataSource = layoutManager.getTimeSlotTracker().getDataSource();
    if (dataSource != null) {
      newTask = dataSource.copyTask(getTask(t), targetTask, newNodeIndex,
          copyWithChildren);
      layoutManager.getTimeSlotTracker().fireTaskChanged(newTask);
    }

    Utils.clearClipboard();

    return newTask;
  }

  /**
   * Gets task from transferable
   * 
   * @param t
   *          transferable
   * @return task object, never returns null
   */
  public Task getTask(Transferable t) {
    Object taskId = DataFlavors.getTransferData(t, DataFlavors.TASK);
    return timeSlotTracker.getDataSource().getTask(taskId);
  }

  /**
   * Make transferable by task
   * 
   * @param task
   *          task
   * @return transferable for task
   */
  public TaskSelection wrap(Task task) {
    return new TaskSelection(task.getId(), task.toString());
  }

  /**
   * Can use following transferFlavors?
   * 
   * @param transferFlavors
   *          flavors array
   * @return true - can import, false - otherwise
   */
  public boolean canImport(DataFlavor[] transferFlavors) {
    return DataFlavors.contains(DataFlavors.TASK, transferFlavors);
  }
}
