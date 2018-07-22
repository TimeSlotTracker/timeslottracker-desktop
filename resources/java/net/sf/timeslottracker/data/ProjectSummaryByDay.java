package net.sf.timeslottracker.data;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

import net.sf.timeslottracker.utils.TimeUtils;

public class ProjectSummaryByDay {

  private ArrayList<ProjectSummaryTimeSlot> psTimeslots = new ArrayList<ProjectSummaryTimeSlot>();
  private long duration = 0;
  private String startDate = "";
  private String stopDate = "";

  public void add(ProjectSummaryTimeSlot psts) {
    psTimeslots.add(psts);
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public void setStopDate(String stopDate) {
    this.stopDate = stopDate;
  }

  public void toXml(PrintWriter write) {

    Collections.sort(psTimeslots, new ProjectSummaryTimeSlotComparable());
    sum();

    write.println("<ProjectSummaryByDay " + " startDate=\"" + startDate + "\""
        + " stopDate=\"" + stopDate + "\"" + " totalHours=\""
        + TimeUtils.getDurationInDecimalHours(duration) + "\">");

    tasksToXml(write);
    for (ProjectSummaryTimeSlot t : psTimeslots)
      write.println(t.toXML());
    write.println("</ProjectSummaryByDay>");
  }

  public void sum() {
    ArrayList<ProjectSummaryTimeSlot> timeslots = new ArrayList<ProjectSummaryTimeSlot>();
    ProjectSummaryTimeSlot previous = null;
    ProjectSummaryTimeSlot current = null;

    for (ProjectSummaryTimeSlot t : psTimeslots) {
      current = t;
      duration += current.getDuration();
      if (current.isSameProjectTaskByDay(previous)) {
        previous.setDuration(previous.getDuration() + current.getDuration());
      } else {
        if (previous != null) {
          timeslots.add(previous);
        }
        previous = current;
      }
    }
    if (previous != null) {
      timeslots.add(previous);
    }

    psTimeslots = timeslots;
  }

  public void tasksToXml(PrintWriter writer) {
    writer.println("<SummaryTasks>");
    ProjectSummaryTimeSlot previous = null;
    ProjectSummaryTimeSlot current = null;

    for (ProjectSummaryTimeSlot t : psTimeslots) {
      current = t;
      if (previous == null) {
        writer.println("<SummaryTask taskId=\"_" + current.getTaskId() + "\""
            + " projectNumber=\"" + current.getProjectNumber() + "\""
            + " projectTask=\"" + current.getProjectTask() + "\"/>");
      } else if (!current.isSameProjectTask(previous)) {
        writer.println("<SummaryTask taskId=\"_" + current.getTaskId() + "\""
            + " projectNumber=\"" + current.getProjectNumber() + "\""
            + " projectTask=\"" + current.getProjectTask() + "\"/>");
      }
      previous = current;
    }
    writer.println("</SummaryTasks>");
  }

}
