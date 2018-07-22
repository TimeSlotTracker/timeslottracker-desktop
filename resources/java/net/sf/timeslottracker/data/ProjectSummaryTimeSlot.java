package net.sf.timeslottracker.data;

import net.sf.timeslottracker.utils.TimeUtils;

public class ProjectSummaryTimeSlot{

  private Object taskId;
  private String projectNumber;
  private String projectTask;
  private String dateString;
  private long duration;

  public ProjectSummaryTimeSlot (Object taskId, String projectNumber, String projectTask, String dateString, long duration) {
    this.taskId = taskId;
    this.projectNumber = projectNumber;
    this.projectTask = projectTask;
    this.dateString = dateString;
    this.duration = duration;
  }

  public Object getTaskId() {
    return taskId;
  }

  public void setTaskId(Object taskId) {
    this.taskId = taskId;
  }

  public String getProjectNumber() {
    return projectNumber;
  }

  public void setProjectNumber(String projectNumber) {
    this.projectNumber = projectNumber;
  }

  public String getProjectTask() {
    return projectTask;
  }

  public void setProjectTask(String projectTask) {
    this.projectTask = projectTask;
  }

  public String getDateString() {
    return dateString;
  }

  public void setDateString(String dateString) {
    this.dateString = dateString;
  }

  /**
   * In milliseconds
   * @return duration In milliseconds
   */
  public long getDuration() {
    return duration;
  }

  /**
   *
   * @param duration In milliseconds
   */
  public void setDuration(long duration) {
    this.duration = duration;
  }

  public String getDurationInDecimalHours() {
    return TimeUtils.getDurationInDecimalHours(duration);
  }

  public String toString () {
    return "_"
    + taskId
    + " "
    + projectNumber
    + " "
    + projectTask
    + " "
    + dateString
    + " "
    + duration;
  }

  public String toXML () {
    return "<SummaryTimeSlot"
        + " taskId=\"_"
        + taskId
        + "\""
        + " projectNumber=\""
        + projectNumber
        + "\""
        + " projectTask=\""
        + projectTask
        + "\""
        + " date=\""
        + dateString
        + "\""
        + " duration=\""
        + getDurationInDecimalHours()
        + "\""
        + "/>";
  }

  public boolean isSameProjectTask(ProjectSummaryTimeSlot timeslot) {
    boolean isSame = false;
    if (timeslot != null)
      isSame = this.getProjectNumber().equalsIgnoreCase(timeslot.getProjectNumber())
        && this.getProjectTask().equalsIgnoreCase(timeslot.getProjectTask());
    return isSame;
  }

  public boolean isSameProjectTaskByDay(ProjectSummaryTimeSlot timeslot) {
    boolean isSame = false;
    if (timeslot != null)
      isSame = isSameProjectTask(timeslot)
          && this.getDateString().equalsIgnoreCase(timeslot.getDateString());
    return isSame;
  }
}
