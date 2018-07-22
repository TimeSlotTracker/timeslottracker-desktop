package net.sf.timeslottracker.filters;

import java.util.Date;

import net.sf.timeslottracker.core.TimeSlotTracker;
import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.utils.TimeUtils;

/**
 * Filter returns timeslot included in given time period
 * 
 * @version File version: $Revision: 800 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class TimeSlotIncludedInPeriod implements TimeSlotFilter {

  private final Date end;

  private final Date start;

  private final TimeSlotTracker timeSlotTracker;

  /**
   * Creates filter with given start and end dates
   * <p>
   * TimeSlotTracker used for getting active timeslot
   */
  public TimeSlotIncludedInPeriod(TimeSlotTracker timeSlotTracker, Date start,
      Date end) {
    this.timeSlotTracker = timeSlotTracker;
    this.start = TimeUtils.getDayBegin(start).getTime();
    this.end = TimeUtils.getDayEnd(end).getTime();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sf.timeslottracker.filters.Filter#accept(java.lang.Object)
   */
  public boolean accept(TimeSlot timeSlot) {
    Date timeSlotStartDate = timeSlot.getStartDate();

    Date timeSlotEndDate;
    if (timeSlot.equals(timeSlotTracker.getActiveTimeSlot())) {
      timeSlotEndDate = TimeUtils.roundDate(new Date());
    } else {
      timeSlotEndDate = timeSlot.getStopDate();
    }

    boolean result = true;
    if (timeSlotStartDate != null && !end.after(timeSlotStartDate)) {
      result = false;
    }

    if (timeSlotEndDate != null && !start.before(timeSlotEndDate)) {
      result = false;
    }

    return result;
  }

}
