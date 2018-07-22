package net.sf.timeslottracker.filters;

import java.util.Date;

import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.utils.TimeUtils;

/**
 * Filter task with given start/end dates
 * 
 * @version File version: $Revision: 1.23 $, $Date: 2007/04/07 03:59:30 $
 * @author Last change: $Author: cnitsa $
 */
public class TaskInPeriodFilter implements TaskFilter {

  private final Date start;

  private final Date end;

  /**
   * Creates filter
   */
  public TaskInPeriodFilter(Date start, Date end) {
    this.start = TimeUtils.getDayBegin(start).getTime();
    this.end = TimeUtils.getDayEnd(end).getTime();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sf.timeslottracker.filters.Filter#accept(java.lang.Object)
   */
  @Override
  public boolean accept(Task task) {
    return task.getTimeAsLong(true, start, end) != null;
  }

}
