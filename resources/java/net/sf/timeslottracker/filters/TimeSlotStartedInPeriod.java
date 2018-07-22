package net.sf.timeslottracker.filters;

import static net.sf.timeslottracker.utils.TimeUtils.dayEnd;
import static net.sf.timeslottracker.utils.TimeUtils.dayStart;

import java.util.Date;

import net.sf.timeslottracker.data.TimeSlot;

/**
 * Filter returns timeslot started in given time period
 * 
 * @version File version: $Revision: 1076 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class TimeSlotStartedInPeriod implements TimeSlotFilter {

  private final long endDate;

  private final long startDate;

  /**
   * Created filter for given date
   * <p>
   * StartDate will be begin of date, endDate - end of date
   */
  public TimeSlotStartedInPeriod(Date date) {
    startDate = dayStart(date);
    endDate = dayEnd(date);
  }

  /**
   * Creates filter with given start and end dates
   */
  public TimeSlotStartedInPeriod(Date startDate, Date endDate) {
    this.startDate = dayStart(startDate);
    this.endDate = dayEnd(endDate);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sf.timeslottracker.filters.Filter#accept(java.lang.Object)
   */
  public boolean accept(TimeSlot timeSlot) {
    Date timeSlotStartDate = timeSlot.getStartDate();
    if (timeSlotStartDate == null) {
      return false;
    }

    if (timeSlot.getTask() == null) {
      return false;
    }
    
    long time = timeSlotStartDate.getTime();
    return startDate <= time && time <= endDate;
  }

}
