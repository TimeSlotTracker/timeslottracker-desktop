package net.sf.timeslottracker.data.xml;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import net.sf.timeslottracker.core.TimeSlotTracker;
import net.sf.timeslottracker.data.Attribute;
import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.utils.TimeUtils;

/**
 * A TimeSlot implementation used in xml context
 * 
 * File version: $Revision: 1153 $, $Date: 2010-09-17 21:58:51 +0700 (Fri, 17 Sep
 * 2010) $ Last change: $Author: cnitsa $
 */
public class XmlTimeSlot implements TimeSlot {

  private Integer id;

  private Date start;

  private Date stop;

  private String description;

  private Vector<Attribute> attributes = new Vector<Attribute>();

  private SimpleDateFormat dateFormat;

  /** a reference to task which holds this timeslot, set by Task.addTimeslot() */
  private Task task;

  /**
   * Creates a new XmlTimeSlot object.
   * <p>
   * It have a default, package access level because you shouldn't create
   * directly a new instances of this class by yourself. You should use the
   * <code>DataSource.createTimeSlot</code> method instead.
   */
  XmlTimeSlot(Locale locale, Integer timeslotId, Date start, Date stop,
      String description) {
    this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", locale);
    this.id = timeslotId;

    setStartDate(start);
    setStopDate(stop);
    setDescription(description);
  }

  public Object getId() {
    return id;
  }

  public Date getStartDate() {
    return start;
  }

  public void setStartDate(Date date) {
    this.start = TimeUtils.roundDate(date);

    /*
     * additionally, when this timeslot already belongs to some task, it should
     * resorted to keep the proper order
     */
    if (task != null && task instanceof XmlTask) {
      XmlTask xmlTask = (XmlTask) task;
      xmlTask.sortTimeSlots();
    }
  }

  public Date getStopDate() {
    return stop;
  }

  public void setStopDate(Date date) {
    this.stop = TimeUtils.roundDate(date);
  }

  public long getTime() {
    return getTime(null, null);
  }

  public long getTime(Date startDate, Date stopDate) {
    Long time = getTimeAsLong(startDate, stopDate);
    return time == null ? 0 : time;
  }

  public Long getTimeAsLong(Date startDate, Date stopDate) {
    return TimeUtils.getDuration(startDate, stopDate, getStartDate(),
        getStopDate());
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setTask(Task task) {
    this.task = task;
  }

  public Task getTask() {
    return task;
  }

  public Collection<Attribute> getAttributes() {
    return attributes;
  }

  public void setAttributes(Collection<Attribute> attributes) {
    this.attributes.clear();
    this.attributes.addAll(attributes);
  }

  public boolean hasAttributes() {
    return attributes != null && !attributes.isEmpty();
  }

  public String toString() {
    String value = "";
    if (start == null) {
      value += "(?)";
    } else {
      value += dateFormat.format(start);
    }
    value += " - ";
    if (stop == null) {
      value += "(?)";
    } else {
      value += dateFormat.format(stop);
    }
    if (description != null) {
      value += ": " + description;
    }
    return value;
  }

  public boolean canBeStarted() {
    return getTask() != null;
  }

  public boolean canBePaused() {
    XmlTask task = (XmlTask) getTask();
    if (task == null) {
      return false;
    }

    TimeSlotTracker timeSlotTracker = task.getTimeSlotTracker();
    TimeSlot activeTimeSlot = timeSlotTracker.getActiveTimeSlot();
    return equalsTimeSlot(activeTimeSlot)
        && (activeTimeSlot.getStartDate() != null);
  }

  public boolean canBeStoped() {
    XmlTask task = (XmlTask) getTask();
    if (task == null) {
      return false;
    }

    TimeSlotTracker tracker = task.getTimeSlotTracker();
    return equalsTimeSlot(tracker.getActiveTimeSlot());
  }

  public Object clone() {
    try {
      XmlTimeSlot clone = (XmlTimeSlot) super.clone();

      Vector<Attribute> newAttributes = new Vector<Attribute>();
      for (Attribute attribute : clone.attributes) {
        newAttributes.add(new Attribute(attribute.getAttributeType(), attribute
            .get()));
      }
      clone.attributes = newAttributes;
      return clone;
    } catch (CloneNotSupportedException e) {
      throw new AssertionError(e);
    }
  }

  private boolean equalsTimeSlot(TimeSlot activeTimeSlot) {
    return activeTimeSlot != null && activeTimeSlot.equals(this);
  }

  @Override
  public boolean isActive() {
    return getStartDate() != null && getStopDate() == null;
  }

  @Override
  public boolean isPaused() {
    return getStartDate() == null;
  }
}
