package net.sf.timeslottracker.gui;

import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TimeSlot;

/**
 * Describes acceptable operations on a timeslots panel. In our classic skin it
 * will make a use of a table implemented with JTable.
 * 
 * File version: $Revision: 1125 $, $Date: 2009-05-16 08:53:21 +0700 (Sat, 16
 * May 2009) $ Last change: $Author: cnitsa $
 */
public interface TimeSlotsInterface {

  /**
   * View mode: timeslot for task, selected timeslots
   */
  enum Mode {
    Task, TimeSlots
  };

  /**
   * Shows task's timeslots.
   * 
   * @param task
   *          a task object which timeslots we want to see or null to clear the
   *          timeslot table
   */
  void show(Task task);

  /**
   * Refreshes table with timeslots.
   * <p>
   * It simply should reload all timeslot from actual task and then select the
   * same row was selected before (if it applies)
   */
  void refresh();

  /**
   * Opens a dialog window and gives a user the possibility to edit timeslot's
   * data.
   * 
   * @param timeslot
   *          an object to edit or null to add a new TimeSlot
   */
  void edit(TimeSlot timeslot);

  /**
   * Checks if any timeslot is selected and if it is it calls
   * <code>edit(TimeSlot)</code> to edit this timeslot.
   */
  void editSelected();

  /**
   * Adds a new timeslot to actually showed task.<br>
   * If no task is actually showed it does nothing.
   * 
   * @param timeslot
   *          a timeslot to add. * If <code>null</code> it does nothing
   */
  void add(TimeSlot timeslot);

  /**
   * Select desired timeSlot
   * 
   * @param timeslot
   *          a timeslot to select
   */
  void selectTimeSlot(TimeSlot timeslot);

  /**
   * @return selected task, may be null. shows timeslots for this task
   */
  Task getSelectedTask();

  /**
   * Update panel for timeslot
   * 
   * @param timeslot
   *          timeslot to update
   */
  void update(TimeSlot timeslot);

  /**
   * @return current view mode
   */
  Mode getMode();

  /**
   * Opens a dialog window and gives a user the possibility to filter timeslots
   * table.
   */
  void filter();

  /**
   * Checks if any timeslot is selected and if it is it calls
   * <code>split(TimeSlot)</code> to edit this timeslot.
   * 
   * @return new timeslot, may be null
   */
  TimeSlot splitSelected();

  /**
   * Get selected timeslot
   * 
   * @return TimeSlot, may be null - if nothing selected
   */
  TimeSlot getSelected();

}
