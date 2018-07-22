package net.sf.timeslottracker.data;


import java.util.Comparator;

public class ProjectSummaryTimeSlotComparable implements Comparator<ProjectSummaryTimeSlot> {

  @Override
  public int compare(ProjectSummaryTimeSlot o1,  ProjectSummaryTimeSlot o2) {
    int compared;
    compared = o1.getProjectNumber().compareToIgnoreCase(o2.getProjectNumber());
    if (compared == 0) {
      compared = o1.getProjectTask().compareToIgnoreCase(o2.getProjectTask());
      if (compared == 0) {
        compared = o1.getDateString().compareToIgnoreCase(o2.getDateString());
      }
    }

    return compared;
  }
}
