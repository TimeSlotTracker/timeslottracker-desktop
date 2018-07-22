package net.sf.timeslottracker.gui.layouts.classic.today;

/**
 * Controller for changing timeslot
 * 
 * @version File version: $Revision: 1001 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public interface TodayTableController {

  /**
   * Start timings for given description
   * 
   * @return true - timing starting, false - canceled
   */
  boolean start();

  /**
   * Restart timings for given timeslot
   * 
   * @return true - timing starting, false - canceled
   */
  boolean start(TimeSlotValue timeSlotValue);

  /**
   * Update timeslot with given time slot value
   * 
   * @param timeSlotValue
   *          value with new data
   */
  void update(TimeSlotValue timeSlotValue);

  /**
   * Stop current active task
   */
  void stop();

}
