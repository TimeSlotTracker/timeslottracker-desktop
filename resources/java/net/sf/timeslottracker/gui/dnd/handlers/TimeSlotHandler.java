package net.sf.timeslottracker.gui.dnd.handlers;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.TransferHandler;

import net.sf.timeslottracker.core.TimeSlotTracker;
import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.dnd.DataFlavors;
import net.sf.timeslottracker.gui.dnd.selections.TimeSlotSelection;
import net.sf.timeslottracker.gui.dnd.selections.TimeSlotTransferData;

/**
 * Task's routines for Dnd actions
 * 
 * @version File version: $Revision: 1023 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class TimeSlotHandler {
  private final LayoutManager layoutManager;

  public TimeSlotHandler(LayoutManager layoutManager) {
    this.layoutManager = layoutManager;
  }

  /**
   * Make transferable by timeslot
   * 
   * @param timeSlot
   *          timeslot
   * @return transferable for timeslot
   */
  public TimeSlotSelection wrap(Collection<TimeSlot> timeSlots) {
    return new TimeSlotSelection(new TimeSlotTransferData(timeSlots));
  }

  /**
   * Can use following transferFlavors?
   * 
   * @param transferFlavors
   *          flavors array
   * @return true - can import, false - otherwise
   */
  public boolean canImport(DataFlavor[] transferFlavors) {
    return DataFlavors.contains(DataFlavors.TIME_SLOT, transferFlavors);
  }

  /**
   * Do import DnD action within selected transferable and target task
   * 
   * @param t
   *          transferable with timeslot
   * @param targetTask
   *          task for importing timeslot
   * @return true - successful finishing, false - otherwise
   */
  public boolean importData(Transferable t, Task targetTask) {
    if (!canImport(t.getTransferDataFlavors())) {
      return false;
    }

    if (targetTask == null) {
      return false;
    }

    TimeSlotTransferData transferData = getTransferData(t);

    transferData.setTargetTaskId(targetTask.getId());

    doActionIfNeed(transferData);

    transferData.setWasDnDAction();

    return true;
  }

  /**
   * Do export DnD action within selected transferable and action
   * 
   * @param data
   *          transferable with timeslot
   * @param action
   *          DnD action
   */
  public void exportDone(Transferable data, int action) {
    if (!data.isDataFlavorSupported(DataFlavors.TIME_SLOT)) {
      return;
    }

    TimeSlotTransferData transferData = getTransferData(data);
    transferData.setMarkedAsMoving(action == TransferHandler.MOVE);

    doActionIfNeed(transferData);

    transferData.setWasDnDAction();
  }

  private void doActionIfNeed(TimeSlotTransferData transferData) {
    if (!transferData.canDoAction()) {
      return;
    }

    Task targetTask = getTask(transferData.getTargetTaskId());

    ArrayList<TimeSlot> timeSlots = new ArrayList<TimeSlot>(
        getTimeSlot(transferData));

    // filtering timeslots (if we selected timeslots with source = target task
    // and moving action)
    if (transferData.isMarkedAsMoving()) {
      for (Iterator<TimeSlot> iter = timeSlots.iterator(); iter.hasNext();) {
        if (targetTask.getTimeslots().contains(iter.next())) {
          iter.remove();
        }
      }
    }

    // at first - removing if needs
    Object activeTimeSlotId = null;
    if (transferData.isMarkedAsMoving()) {
      
      Task oldtask = null;
      if (!timeSlots.isEmpty()) {
        oldtask = timeSlots.get(0).getTask();
      }
      
      for (TimeSlot timeSlot : timeSlots) {
        timeSlot.getTask().deleteTimeslot(timeSlot);

        if (getTimeSlotTracker().getActiveTimeSlot() == timeSlot) {
          activeTimeSlotId = timeSlot.getId();
          getTimeSlotTracker().setActiveTimeSlot(null);
        }
        
        layoutManager.fireTimeSlotChanged(timeSlot);
      }

      if (!timeSlots.isEmpty()) {
        getTimeSlotTracker().fireTaskChanged(oldtask);
      }
    }

    // at second - adding timeslots
    ArrayList<TimeSlot> newTimeSlots = new ArrayList<TimeSlot>();
    for (TimeSlot timeSlot : timeSlots) {
      TimeSlot newTimeSlot;
      if (transferData.isMarkedAsMoving()) {
        newTimeSlot = timeSlot;
      } else {
        newTimeSlot = (TimeSlot) timeSlot.clone();
      }
      newTimeSlots.add(newTimeSlot);
      targetTask.addTimeslot(newTimeSlot);
    }

    // at third - refreshing gui
    layoutManager.getTimeSlotsInterface().refresh();

    for (TimeSlot newTimeSlot : newTimeSlots) {
      layoutManager.fireTimeSlotChanged(newTimeSlot);

      if (newTimeSlot.getId().equals(activeTimeSlotId)) {
        getTimeSlotTracker().setActiveTimeSlot(newTimeSlot);
        getTimeSlotTracker().fireTaskChanged(newTimeSlot.getTask());
      }
    }

    if (!newTimeSlots.isEmpty()) {
      layoutManager.getTimeSlotsInterface().selectTimeSlot(
          newTimeSlots.get(newTimeSlots.size() - 1));
    }

    Utils.clearClipboard();
  }

  private TimeSlotTracker getTimeSlotTracker() {
    return layoutManager.getTimeSlotTracker();
  }

  private Collection<TimeSlot> getTimeSlot(TimeSlotTransferData transferData) {
    Task task = getTask(transferData.getTaskId());

    ArrayList<TimeSlot> timeSlots = new ArrayList<TimeSlot>();

    for (Object timeSlotId : transferData.getTimeSlotIds()) {
      timeSlots.add(task.getTimeSlot(timeSlotId));
    }

    return timeSlots;
  }

  private Task getTask(Object taskId) {
    return getTimeSlotTracker().getDataSource().getTask(taskId);
  }

  private TimeSlotTransferData getTransferData(Transferable data) {
    return (TimeSlotTransferData) DataFlavors.getTransferData(data,
        DataFlavors.TIME_SLOT);
  }
}
