package net.sf.timeslottracker.gui.dateperiod;

import java.util.Date;

import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.filters.TimeSlotFilter;
import net.sf.timeslottracker.filters.TimeSlotStartedInPeriod;
import net.sf.timeslottracker.utils.TimeUtils;

/**
 * Data object used for storing date period data. Used for filtering timeslots,
 * etc
 * <p>
 * Mutable object
 * 
 * File version: $Revision: 803 $, $Date: 2009-05-16 08:53:21 +0700 (Sat, 16 May
 * 2009) $ Last change: $Author: cnitsa $
 */
public class DatePeriod {

  /**
   * Type of date period
   */
  public enum PeriodType {
    ALL(0), LAST_WEEK(1), LAST_MONTH(2), LAST_YEAR(3), USER_PERIOD(4);

    private final int persistentId;

    private PeriodType(int id) {
      this.persistentId = id;
    }

    /**
     * @return id used for persistent purpose
     */
    public int getPersistentId() {
      return persistentId;
    }

    /**
     * Returns period type by given persistentId
     * 
     * @return period type, never null, if pesistentId wrong, return {@link ALL}
     */
    public static PeriodType valueOf(int persistentId) {
      PeriodType[] values = PeriodType.values();
      for (int i = 0; i < values.length; i++) {
        if (values[i].persistentId == persistentId) {
          return values[i];
        }
      }

      return ALL;
    }

  };

  private PeriodType periodType;

  private Date start;

  private Date end;

  private final int firstDayOfWeek;

  /**
   * Create filter data with given params
   * 
   * @param firstDayOfWeek
   *          parameter from {@link Configuration#WEEK_FIRST_DAY}
   */
  public DatePeriod(int firstDayOfWeek) {
    this.periodType = PeriodType.ALL;
    this.start = null;
    this.end = null;
    this.firstDayOfWeek = firstDayOfWeek;
  }

  /**
   * @return end of period
   */
  public Date getUserPeriodEnd() {
    return end;
  }

  /**
   * @return period type
   */
  public PeriodType getPeriodType() {
    return periodType;
  }

  /**
   * @return true if no filtering selected, same as period type = ALL
   */
  public boolean isNoFiltering() {
    return PeriodType.ALL == periodType;
  }

  /**
   * @return start of period accoding it's type
   */
  public Date getUserPeriodStart() {
    return start;
  }

  /**
   * @return created timeslotfilter from this data
   */
  public TimeSlotFilter getTimeSlotFilter() {
    return isNoFiltering() ? null : new TimeSlotStartedInPeriod(
        getStartPeriod(), getEndPeriod());
  }

  /**
   * Sets start/end of user period (for period type
   * {@link PeriodType#USER_PERIOD})
   */
  public void setUserPeriod(Date start, Date end) {
    this.start = start;
    this.end = end;
    resetPeriodTypeIfNull(start);
    resetPeriodTypeIfNull(end);
  }

  /**
   * Sets period type
   */
  public void setPeriodType(PeriodType periodType) {
    this.periodType = periodType;
  }

  /**
   * Updated period type to {@link PeriodType#ALL} if given date null
   */
  private void resetPeriodTypeIfNull(Date date) {
    if (PeriodType.USER_PERIOD == this.periodType && date == null) {
      this.periodType = PeriodType.ALL;
    }
  }

  /**
   * @return end of period according it's type
   */
  public Date getEndPeriod() {
    Date currentTime = new Date();

    switch (periodType) {
    case ALL:
      return null;
    case LAST_WEEK:
      return TimeUtils.getWeekEnd(currentTime, firstDayOfWeek).getTime();
    case LAST_MONTH:
      return TimeUtils.getMonthEnd(currentTime).getTime();
    case LAST_YEAR:
      return TimeUtils.getYearEnd(currentTime).getTime();
    case USER_PERIOD:
      return end == null ? null : TimeUtils.getDayEnd(end).getTime();
    default:
      throw new IllegalArgumentException("Wrong period type: " + periodType);
    }
  }

  /**
   * @return start of period according it's type
   */
  public Date getStartPeriod() {
    Date currentTime = new Date();

    switch (periodType) {
    case ALL:
      return null;
    case LAST_WEEK:
      return TimeUtils.getWeekBegin(currentTime, firstDayOfWeek).getTime();
    case LAST_MONTH:
      return TimeUtils.getMonthBegin(currentTime).getTime();
    case LAST_YEAR:
      return TimeUtils.getYearBegin(currentTime).getTime();
    case USER_PERIOD:
      return start == null ? null : TimeUtils.getDayBegin(start).getTime();
    default:
      throw new IllegalArgumentException("Wrong period type: " + periodType);
    }
  }
}