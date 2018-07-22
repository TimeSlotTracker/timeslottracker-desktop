package net.sf.timeslottracker.data;

import java.util.Collection;
import java.util.Date;

/**
 * Interface declaring all needed operations to be done on task.
 * <p>
 * A task could be a project, (sub)project or even a really task done in a
 * project. TimeSlots can be registered on leafs as well as "parent" tasks, it
 * means every task (regarding it has children) can register works on it.
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-07-31 17:41:54 +0700
 *          (Fri, 31 Jul 2009) $
 * @author Last change: $Author: cnitsa $
 */
public interface Task {

  /**
   * Returns task's id.
   * <p>
   * <b>Id</b> can be any object. In some implementation it should be an String
   * object. In other - an Integer object representing integer id value
   * (probably in a database system primary key).
   */
  Object getId();

  /**
   * Returns task's name
   */
  String getName();

  /**
   * Sets task's name
   */
  void setName(String name);

  /**
   * Returns task's description.
   * 
   * @return task's description or null, if there is no description available.
   */
  String getDescription();

  /**
   * Sets task's description
   */
  void setDescription(String description);

  /**
   * Gets parent task (if any).
   * 
   * @return null if this is a root, or Task object - a parent one.
   */
  Task getParentTask();

  /**
   * Sets parent of this task (so it makes this taks as a subtask of given
   * parent).
   * <p>
   * This method has a default, package access level because a user shouldn't
   * directly set it. He should use the <code>DataSource.moveTask</code> method
   * instead.
   * 
   * @see DataSource#moveTask(Task,Task)
   */
  void setParentTask(Task parentTask);

  /**
   * Adds one timeslot to this task
   */
  void addTimeslot(TimeSlot timeslot);

  /**
   * Deletes one particular timeslot from the task
   */
  void deleteTimeslot(TimeSlot timeslot);

  /**
   * Returns a collection (even if empty one) of all time slots associated with
   * this task.
   */
  Collection<TimeSlot> getTimeslots();

  /**
   * Returns timeSlot by timeSlot id
   * 
   * @param timeSlotId
   *          timeSlot id
   * @see net.sf.timeslottracker.data.TimeSlot#getId()
   * @return TimeSlot or null, if not found
   */
  TimeSlot getTimeSlot(Object timeSlotId);

  /**
   * Returns a children of a this parent task (even root). Task returned are
   * parent's immediately children (one level down only).
   * 
   * @return null or an empty Collection where a task has no children<br>
   *         otherwise a Collection of Task objects
   */
  Collection<Task> getChildren();

  /**
   * Returns the collection of attributes for this task.
   * 
   * @return Collection of records of type Attribute
   */
  Collection<Attribute> getAttributes();

  /**
   * Sets a collection of attributes for this task.
   * 
   * @param attributes
   *          Collection of record of type Attribute.
   */
  void setAttributes(Collection<Attribute> attributes);

  /**
   * Returns summarized time spent on this task.
   * <p>
   * Use it when you want to know how much time you spent on some task. <br>
   * Use it also when you want to know the whole time, including subtasks
   * 
   * @param includeSubtasks
   *          <code>true</code> if you want to include also subtasks
   * @return number of milliseconds spent on this task
   */
  long getTime(boolean includeSubtasks);

  /**
   * Returns summarized time spent on this task between two dates.
   * <p>
   * Use it when you want to know how much time you spent on some task. <br>
   * Use it also when you want to know the whole time, including subtasks
   * <p>
   * If <code>startDate</code> and <code>stopDate</code> are null the dates are
   * not checked. (It is the same as <code>getTime(includeSubtasks)</code>
   * method)
   * 
   * @param includeSubtasks
   *          <code>true</code> if you want to include also subtasks
   * @param startDate
   *          a date (including) to count time
   * @param stopDate
   *          a date (excluding) to count time
   * @return number of milliseconds spent on this task
   */
  long getTime(boolean includeSubtasks, Date startDate, Date stopDate);

  /**
   * Returns the same as <code>getTime(Date,Date)</code> but as a object
   * <code>Long</code>. Difference is that it returns non null value (==0) when
   * a timeslots exists, but with 0 milliseconds between startDate and stopDate.
   */
  Long getTimeAsLong(boolean includeSubtasks, Date startDate, Date stopDate);

  /**
   * Returns <code>true</code> if this task can be started.
   */
  boolean canBeStarted();

  /**
   * Returns <code>true</code> if this task can be paused (i.e. it is running
   * now).
   */
  boolean canBePaused();

  /**
   * Returns <code>true</code> if this task can be stopped (i.e. it is running
   * now).
   */
  boolean canBeStoped();

  /**
   * @return true if task is hidden, false - otherwise
   */
  boolean isHidden();

  /**
   * Sets task's hide flag
   * 
   * @param isHidden
   *          - task is hidden
   */
  void setHidden(boolean isHidden);

  /**
   * @return true if this task is root (has no parent task)
   */
  boolean isRoot();

  /**
   * Finds timeslot by given id, but only in timeslots of this task.
   * 
   * @param timeslotId
   *          id to find
   * @return required timeslot or <code>null</code> if nothing matches.
   */
  TimeSlot findTimeSlotById(Object timeslotId);

  /**
   * Returns last timeslot for current task
   * 
   * @return timeslot or null if no timeslots at all
   */
  TimeSlot getLastTimeSlot();
}
