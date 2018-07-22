package net.sf.timeslottracker.data;

import java.util.Collection;
import java.util.Date;

/**
 * An interface a timeslot must implement. A TimeSlot is a smallest information
 * we will collect.
 * <p>
 * It is consisted of start and stop date and time as well as a description.
 * 
 * File version: $Revision: 1153 $, $Date: 2010-09-17 21:58:51 +0700 (Fri, 17 Sep
 * 2010) $ Last change: $Author: cnitsa $
 */
public interface TimeSlot extends Cloneable {

  /**
   * Returns time slot id. It's persistent.
   * 
   * @return time slot id
   */
  Object getId();

  /**
   * Returns timeslot's begin date and time
   */
  Date getStartDate();

  /**
   * Sets a new start date
   * <p>
   * It's probably very good idea to store only full minutes, with seconds
   * equals to zero. One minute is the lowest value we are interested in.
   */
  void setStartDate(Date date);

  /**
   * Returns timeslot's finish date and time.
   */
  Date getStopDate();

  /**
   * Sets a new stop date
   * <p>
   * It's probably very good idea to store only full minutes, with seconds
   * equals to zero. One minute is the lowest value we are interested in.
   */
  void setStopDate(Date date);

  /**
   * Returns time (in milliseconds) spent in this timeslot.
   * <p>
   * If timeslot is still active it will return value between the startDate and
   * current date.
   * <p>
   * If there is no startDate (a task is paused) a value <code>0</code> would be
   * returned.
   * 
   * @return milliseconds (difference between startDate and stopDate)
   */
  long getTime();

  /**
   * Returns time (in milliseconds) spent in this timeslot.
   * <p>
   * If timeslot is still active it will return value between the startDate and
   * current date.
   * <p>
   * If there is no startDate (a task is paused) a value <code>0</code> would be
   * returned.
   * <p>
   * If <code>startDate</code> and <code>stopDate</code> are null the dates are
   * not checked. (It is the same as <code>getTime()</code> method)
   * 
   * @param startDate
   *          a date (including) to count time
   * @param stopDate
   *          a date (excluding) to count time
   * @return milliseconds (difference between startDate and stopDate)
   */
  long getTime(Date startDate, Date stopDate);

  /**
   * Returns the same as <code>getTime(Date,Date)</code> but as a object
   * <code>Long</code>. Difference is that it returns non null value (==0) when
   * a timeslots exists, but with 0 milliseconds between startDate and stopDate.
   */
  Long getTimeAsLong(Date startDate, Date stopDate);

  /**
   * Returns timeslot's description.
   */
  String getDescription();

  /**
   * Sets new description
   */
  void setDescription(String description);

  /**
   * Sets task, which holds this timeslot.
   * <p>
   * It should be set by <code>Task.addTimeslot()</code> method
   */
  void setTask(Task task);

  /**
   * Returns task, which holds this timeslot.
   */
  Task getTask();

  /**
   * Returns the collection of attributes for this timeslot.
   * 
   * @return Collection of records of type Attribute
   */
  Collection<Attribute> getAttributes();

  /**
   * Sets a collection of attributes for this timeslot.
   * 
   * @param attributes
   *          Collection of record of type Attribute.
   */
  void setAttributes(Collection<Attribute> attributes);

  /**
   * Checks if at least one attribute exist
   * @return true - has at least one attribute, false - otherwise
   */
  boolean hasAttributes();

  /**
   * @return true if current timeslot can be started
   */
  boolean canBeStarted();

  /**
   * @return true if current timeslot can be paused
   */
  boolean canBePaused();

  /**
   * @return true if current timeslot can be stopped
   */
  boolean canBeStoped();

  /**
   * Clone current timeSlot
   * <p>
   * id of timeSlot is transient. It has no importance.
   * 
   * @return timeSlot object
   */
  Object clone();

  boolean isActive();

  boolean isPaused();

}
