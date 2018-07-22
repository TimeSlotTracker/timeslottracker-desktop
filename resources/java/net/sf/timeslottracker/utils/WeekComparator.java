package net.sf.timeslottracker.utils;

import java.util.Comparator;

/**
 * Sorts weeks based on weekKey for display in "Task by days", eg "2014-10"
 * "2014_9" will be sorted as "2014_9" "2014-10"
 */
public class WeekComparator implements Comparator<Object> {

  @Override
  public int compare(Object o1, Object o2) {

    int c = 0;
    if (o1.toString().length() < o2.toString().length()) {
      c = -1;
    } else if (o1.toString().length() > o2.toString().length()) {
      c = 1;
    } else {
      c = o1.toString().compareTo(o2.toString());
    }

    return c;
  }
}
