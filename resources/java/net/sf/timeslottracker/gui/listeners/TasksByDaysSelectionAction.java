package net.sf.timeslottracker.gui.listeners;

import java.util.Collection;

import net.sf.timeslottracker.core.Action;
import net.sf.timeslottracker.data.TimeSlot;

/**
 * Action for TasksByDaysSelectionChangeListener
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class TasksByDaysSelectionAction extends Action {

  public TasksByDaysSelectionAction(Object source, String timeSlotsDescription,
      Collection<TimeSlot> timeSlots) {
    super("TasksByDaysSelectionAction", source, new Object[] {
        timeSlotsDescription, timeSlots });
  }

  public String getTimeSlotsDescription() {
    return (String) getParam(0);
  }

  public Collection<TimeSlot> getTimeSlots() {
    return (Collection<TimeSlot>) getParam(1);
  }

  private Object getParam(int i) {
    return ((Object[]) getParam())[i];
  }
}
