package net.sf.timeslottracker.gui.layouts.classic.today;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import net.sf.timeslottracker.gui.taskmodel.TaskValue;
import net.sf.timeslottracker.utils.StringUtils;
import net.sf.timeslottracker.utils.TimeUtils;

/**
 * Timeslot Value object for daily table model
 * 
 * @version File version: $Revision: 1038 $, $Date: 2009-08-04 19:26:06 +0700
 *          (Tue, 04 Aug 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class TimeSlotValue implements Cloneable {
  private static final SimpleDateFormat DAILY_FORMAT = new SimpleDateFormat(
      "yyyy-MM-dd HH:mm");

  private static final SimpleDateFormat HOURLY_FORMAT = new SimpleDateFormat(
      "HH:mm");

  private static String format(Date date) {
    if (date == null) {
      return StringUtils.EMPTY;
    }

    SimpleDateFormat dateFormat = TimeUtils.isCurrentDay(date) ? TimeSlotValue.HOURLY_FORMAT
        : TimeSlotValue.DAILY_FORMAT;
    return dateFormat.format(date);
  }

  private static Date getDate(Object value) {
    if (value instanceof Date) {
      return (Date) value;
    }

    Date date = tryParse(value, HOURLY_FORMAT);
    if (date != null) {
      Calendar evaluated = TimeUtils.create(date);
      Calendar current = TimeUtils.create(new Date());
      TimeUtils.set(evaluated, current, Calendar.YEAR);
      TimeUtils.set(evaluated, current, Calendar.MONTH);
      TimeUtils.set(evaluated, current, Calendar.DATE);
      return evaluated.getTime();
    }

    return tryParse(value, DAILY_FORMAT);
  }

  private static Date tryParse(Object value, SimpleDateFormat format) {
    try {
      return value == null ? null : format.parse(value.toString());
    } catch (ParseException e) {
      return null;
    }
  }

  private String description;

  private Date start;

  private Date stop;

  private Object taskId; // taskId to search timeslot;

  private TaskValue taskValue; // this task can be different with taskId;

  private Object timeSlotId; // null means new timeslot

  public TimeSlotValue() {
  }

  public TimeSlotValue(Date start, Date stop, String description,
      Object timeSlotId, TaskValue taskValue) {
    this.start = start;
    this.stop = stop;
    this.description = description;
    this.timeSlotId = timeSlotId;

    setTask(taskValue);
  }

  public String getDescription() {
    return description;
  }

  public Date getStart() {
    return start;
  }

  public String getStartValue() {
    return format(start);
  }

  public Date getStop() {
    return stop;
  }

  public String getStopValue() {
    return format(stop);
  }

  public Object getTaskId() {
    return taskId;
  }

  public TaskValue getTaskValue() {
    return taskValue;
  }

  public Object getTimeSlotId() {
    return timeSlotId;
  }

  public void setDescription(Object description) {
    this.description = description.toString();
  }

  public void setStartValue(Object value) {
    start = getDate(value);
  }

  public void setStopValue(Object value) {
    stop = getDate(value);
  }

  public void setTaskValue(Object value) {
    taskValue = (TaskValue) value;
  }

  public void updateFrom(TimeSlotValue updated) {
    timeSlotId = updated.timeSlotId;
    start = updated.start;
    stop = updated.stop;
    description = updated.description;

    setTask(updated.taskValue);
  }

  @Override
  protected TimeSlotValue clone() throws CloneNotSupportedException {
    TimeSlotValue value = new TimeSlotValue();
    value.updateFrom(this);
    return value;
  }

  private void setTask(TaskValue taskValue) {
    this.taskId = taskValue.getId();
    this.taskValue = taskValue;
  }

}