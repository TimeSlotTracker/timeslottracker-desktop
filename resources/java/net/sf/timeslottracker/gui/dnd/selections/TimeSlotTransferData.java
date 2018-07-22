package net.sf.timeslottracker.gui.dnd.selections;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import net.sf.timeslottracker.data.TimeSlot;

/**
 * Helper class for TimeSlotSelection.
 * 
 * <p>
 * Aggregates following: source timeSlot info, target task, flag of moving DnD
 * action and flag of having at least one DnD action. Last used for right
 * processing of timeslot's move action (or rather implementation deleting
 * timeSlot only after when was import action. See {@link #canDoAction}).
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class TimeSlotTransferData {
  private Object taskId;

  private final HashMap<Object, String> timeSlotId2StringRep = new HashMap<Object, String>();

  private Object targetTaskId;

  /** flag of moving action with d'n'd action. default: false (mean copying) */
  private boolean markedAsMoving;

  /** indicate was at least one DnD action (import or export). default: false */
  private boolean wasDnDAction;

  public TimeSlotTransferData(Collection<TimeSlot> timeSlots) {
    for (TimeSlot timeSlot : timeSlots) {
      this.taskId = timeSlot.getTask().getId(); // the same for all timeslots

      timeSlotId2StringRep.put(timeSlot.getId(), timeSlot.toString());
    }
  }

  /**
   * @return task id
   */
  public Object getTaskId() {
    return taskId;
  }

  /**
   * @return timeslot id
   */
  public Collection<Object> getTimeSlotIds() {
    return Collections.unmodifiableSet(timeSlotId2StringRep.keySet());
  }

  /**
   * @return string representation of timeslot
   */
  public String getStringRepresentation() {
    StringBuffer stringBuffer = new StringBuffer();
    for (String representation : timeSlotId2StringRep.values()) {
      stringBuffer.append(representation).append('\n');
    }
    return stringBuffer.toString();
  }

  /**
   * @return flag of action as moving
   */
  public boolean isMarkedAsMoving() {
    return markedAsMoving;
  }

  /**
   * Sets flag of action as moving
   * 
   * @param markedAsMoving
   *          new flag
   */
  public void setMarkedAsMoving(boolean markedAsMoving) {
    this.markedAsMoving = markedAsMoving;
  }

  /**
   * Sets flag as was at least one DnD action
   */
  public void setWasDnDAction() {
    this.wasDnDAction = true;
  }

  /** Checking if we have all data needed for d'n'd action */
  public boolean canDoAction() {
    return targetTaskId != null && wasDnDAction;
  }

  /**
   * Gets target task id
   * 
   * @return task id, always not null
   */
  public Object getTargetTaskId() {
    return targetTaskId;
  }

  /**
   * Sets target task id
   * 
   * @param targetTaskId
   *          , must be not null
   */
  public void setTargetTaskId(Object targetTaskId) {
    this.targetTaskId = targetTaskId;
  }
}
