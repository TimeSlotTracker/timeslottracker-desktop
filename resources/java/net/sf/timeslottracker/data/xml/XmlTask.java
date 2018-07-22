package net.sf.timeslottracker.data.xml;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import net.sf.timeslottracker.core.TimeSlotTracker;
import net.sf.timeslottracker.data.Attribute;
import net.sf.timeslottracker.data.DataSource;
import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TimeSlot;

/**
 * A task in a xml version.
 * <p>
 * Basically, it should looks like any other Task's class, but it can contains
 * some specific attributes to work with xml.
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-07-31 17:41:54 +0700
 *          (Fri, 31 Jul 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class XmlTask implements Task {

  /** logging using java.util.logging package **/
  private static final Logger LOG = Logger
      .getLogger("net.sf.timeslottracker.data.xml");

  private TimeSlotTracker timeSlotTracker;

  private Task parentTask;

  private Integer id;

  private String name;

  private String description;

  private Vector<TimeSlot> timeslots;

  private Collection<Attribute> attributes = new Vector<Attribute>();

  private StartDateSorter startDateSorter;

  // task hide flag
  private boolean hidden;

  /**
   * Creates a new Xml Task object.
   * <p>
   * This constructor has only a default, friendly acces because you shoundn't
   * create instances of this object by yourself. You should use the
   * <code>DataSource.createTask</code> method instead.
   * 
   * @param timeSlotTracker
   *          reference to main application interface
   * @param id
   *          a identifier you want to be used be this new task.<br>
   *          This implementation uses Integer as a key value, so use only a
   *          Integer objects. If you pass another object it wouldn't be be used
   *          and a new Integer id would be created using <code>getNectId</code>
   *          static method. Give <code>null</code> to get next id from a
   *          sequence.
   * @param name
   *          a name for a new task
   * @param description
   *          a description for a new task (null allowed).
   * @param hidden
   *          hidden task's flag
   * 
   * @see #getNextId()
   */
  XmlTask(TimeSlotTracker timeSlotTracker, Integer id, String name,
      String description, boolean hidden) {
    this.timeSlotTracker = timeSlotTracker;

    setParentTask(parentTask);

    this.id = id;
    this.name = name;
    this.description = description;
    this.startDateSorter = new StartDateSorter();
    this.timeslots = new Vector<TimeSlot>();
    this.hidden = hidden;
  }

  /**
   * A method, used by XmlTimeSlot when a timeslot changed it's start date. Then
   * we have to re-sort again timeslots to ensure that they are shown in a
   * proper order.
   */
  void sortTimeSlots() {
    Collections.sort(timeslots, startDateSorter);
  }

  public Object getId() {
    return id;
  }

  public String getName() {
    return name == null ? "no-name" : name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String toString() {
    return getName();
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Task getParentTask() {
    return parentTask;
  }

  public void setParentTask(Task parentTask) {
    this.parentTask = parentTask;
  }

  public void addTimeslot(TimeSlot timeslot) {
    timeslots.add(timeslot);
    timeslot.setTask(this);
    sortTimeSlots();
  }

  public void deleteTimeslot(TimeSlot timeslot) {
    timeslots.remove(timeslot);
    timeslot.setTask(null);
  }

  public Collection<TimeSlot> getTimeslots() {
    return timeslots;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sf.timeslottracker.data.Task#getLastTimeSlot()
   */
  @Override
  public TimeSlot getLastTimeSlot() {
    Object[] timeslots = this.timeslots.toArray();

    TimeSlot lastTimeSlot = null;
    if (timeslots.length > 0) {
      lastTimeSlot = (TimeSlot) timeslots[timeslots.length - 1];
    }
    return lastTimeSlot;
  }

  public TimeSlot getTimeSlot(Object timeslotId) {
    for (TimeSlot timeSlot : timeslots) {
      if (timeSlot.getId().equals(timeslotId)) {
        return timeSlot;
      }
    }

    return null;
  }

  public Collection<Task> getChildren() {
    DataSource dataSource = timeSlotTracker.getDataSource();
    if (dataSource == null) {
      return null;
    }
    return dataSource.getChildren(this);
  }

  public long getTime(boolean includeSubtasks) {
    Long time = getTime(includeSubtasks, null, null);
    if (time == null) {
      return 0;
    }
    return time.longValue();
  }

  public long getTime(boolean includeSubtasks, Date startDate, Date stopDate) {
    Long time = getTimeAsLong(includeSubtasks, startDate, stopDate);
    return time == null ? 0 : time.longValue();
  }

  public Long getTimeAsLong(boolean includeSubtasks, Date startDate,
      Date stopDate) {
    LOG.fine("Task.getTime(" + startDate + " : " + stopDate);
    Collection<TimeSlot> timeslotsCollection = getTimeslots();
    Long time = null;
    if (timeslotsCollection != null) {
      Iterator<TimeSlot> timeslots = timeslotsCollection.iterator();
      while (timeslots.hasNext()) {
        TimeSlot timeslot = timeslots.next();
        Long timeslotTime = timeslot.getTimeAsLong(startDate, stopDate);
        if (timeslotTime != null) {
          time = (time == null ? 0 : time.longValue())
              + timeslotTime.longValue();
        }

      }
    }

    if (includeSubtasks) {
      Collection<Task> subtasks = getChildren();
      if (subtasks != null) {
        Iterator<Task> children = subtasks.iterator();
        while (children.hasNext()) {
          Task child = children.next();
          Long childTime = child.getTimeAsLong(true, startDate, stopDate);
          if (childTime != null) {
            time = (time == null ? 0 : time.longValue())
                + childTime.longValue();
          }
        }
      }
    }

    return time;
  }

  public Collection<Attribute> getAttributes() {
    return attributes;
  }

  public void setAttributes(Collection<Attribute> attributes) {
    this.attributes = attributes;
  }

  public boolean canBeStarted() {
    return !isRoot();
  }

  public boolean canBePaused() {
    TimeSlot activeTimeSlot = timeSlotTracker.getActiveTimeSlot();

    return !isRoot() && equalsTask(activeTimeSlot)
        && activeTimeSlot.getStartDate() != null;
  }

  public boolean canBeStoped() {
    return !isRoot() && equalsTask(timeSlotTracker.getActiveTimeSlot());
  }

  @Override
  public boolean isRoot() {
    return parentTask == null;
  }

  public TimeSlot findTimeSlotById(Object timeslotId) {
    if (timeslots == null || timeslots.isEmpty()) {
      return null;
    }
    for (TimeSlot ts : timeslots) {
      if (ts.getId().equals(timeslotId)) {
        return ts;
      }
    }
    return null;
  }

  @Override
  public boolean isHidden() {
    return hidden;
  }

  @Override
  public void setHidden(boolean hidden) {
    this.hidden = hidden;
  }

  private boolean equalsTask(TimeSlot activeTimeSlot) {
    return activeTimeSlot != null && activeTimeSlot.getTask().equals(this);
  }

  TimeSlotTracker getTimeSlotTracker() {
    return timeSlotTracker;
  }

  /**
   * Class to implement Comparator interface to ensure that timeslots are sorted
   * by their startDate.
   */
  private class StartDateSorter implements Comparator<TimeSlot> {
    public int compare(TimeSlot o1, TimeSlot o2) {
      Date d1 = o1.getStartDate();
      Date d2 = o2.getStartDate();
      if (d1 == null && d2 == null) {
        return 0;
      }
      if (d1 == null && d2 != null) {
        return 1;
      }
      if (d1 != null && d2 == null) {
        return -1;
      }
      return d1.compareTo(d2);
    }

    public boolean equals(Object obj) {
      return obj.equals(this);
    }
  }

}
