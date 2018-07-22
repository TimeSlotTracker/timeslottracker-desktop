package net.sf.timeslottracker.utils;

import java.util.Calendar;
import java.util.Date;

import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.core.TimeSlotTracker;

/**
 * Calendar utils
 * 
 * @version File version: $Revision: 1150 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class TimeUtils {

  /**
   * Creates calendar by date
   * 
   * @return calendar for date
   */
  public static Calendar create(Date date) {
    Calendar instance = Calendar.getInstance();
    instance.setTime(date);
    instance.setLenient(false);
    return instance;
  }

  /**
   * Cuts hours from a given date.
   * <p>
   * Because timeslot start/stop date is composed of date & time we have to
   * throw out the part with hour+minutes. In our comparing we are using
   * Date.after() and Date.before() methods. So, if timeSlot.stopDate is equal
   * to let's say 2004-05-01 10:00 and we set our stop date to 2004-05-01 it
   * would not match if we hadn't cut it before.
   */
  public static Date cutHoursFromDate(Date dateToCut) {
    if (dateToCut == null) {
      return null;
    }
    long time = dateToCut.getTime();
    time /= (1000 * 60 * 60 * 24);
    time *= (1000 * 60 * 60 * 24);
    return new Date(time);
  }

  /**
   * Check if equal given calendars for specified Calendar.TIME_CONST
   * 
   * @param calendar1
   *          first calendar
   * @param calendar2
   *          second calendar
   * @param type
   *          type of time measure
   * @return true - time measure for given calendars is equal, false - otherwise
   */
  public static boolean equal(Calendar calendar1, Calendar calendar2, int type) {
    return calendar1.get(type) == calendar2.get(type);
  }

  public static Object[] setFormatStringArgs(Object[] arr, long days,
      long hours, long minutes) {
    arr[0] = new Long(days);
    arr[1] = new Long(hours);
    arr[2] = new Long(minutes);
    return arr;
  }

  /**
   * Formats task duration in classic format:
   * <p>
   * It formats duration in format "hh:mm" (when less then 24 hours) or
   * "x d, hh:mm" when more then one day is occupied by this duration (days,
   * hours, minutes) or in custom format (hours, minutes)
   * <p>
   * or in custom format:
   * <p>
   * "hh:mm" return String
   */
  public static String formatDuration(TimeSlotTracker timeSlotTracker,
      long milliseconds) {
    boolean negative = milliseconds < 0;
    milliseconds = Math.abs(milliseconds);

    long secs = milliseconds / 1000;
    long mins = secs / 60;
    long hours = mins / 60;
    String formatString;
    Object[] formatStringArgs = new Object[3];

    // round (floor) seconds and minutes
    secs %= 60;
    mins %= 60;

    Configuration configuration = timeSlotTracker.getConfiguration();

    if (configuration != null
        && configuration.getString(Configuration.TIME_DURATION_FORMAT,
            "days, hours, minutes").equalsIgnoreCase("hours, minutes")) {
      // custom: hours, minutes
      // round to minutes
      if (secs > 29)
        mins++;

      formatStringArgs = setFormatStringArgs(formatStringArgs, 0, hours, mins);
      formatString = "layoutManager.time.duration.lessThenOneDay";

    } else {
      // classic: days, hours, minutes
      // find how many hours is treated as one "working" day (FR #1186522)
      int hoursPerDay = 24;
      if (configuration != null) {
        hoursPerDay = configuration.getInteger(
            Configuration.HOURS_PER_WORKING_DAY, hoursPerDay).intValue();
      }
      long days = hours / hoursPerDay;
      hours %= hoursPerDay;

      formatStringArgs = setFormatStringArgs(formatStringArgs, days, hours,
          mins);

      if (days > 0) {
        formatString = "layoutManager.time.duration.moreThenOneDay";
      } else {
        formatString = "layoutManager.time.duration.lessThenOneDay";
      }
    }
    return ( negative ? "-" : StringUtils.EMPTY) + timeSlotTracker.getString(
      formatString, formatStringArgs);
  }

  /**
   * @return beginning of given date
   */
  public static Calendar getDayBegin(Date date) {
    return setCalendar(date, 0, 0, 0, 0);
  }

  /**
   * @return ending of given date
   */
  public static Calendar getDayEnd(Date date) {
    return setCalendar(date, 23, 59, 59, 999);
  }

  public static Long getDuration(Date startPeriod, Date stopPeriod, Date start,
      Date stop) {
    if (start == null) {
      return null;
    }

    if (stop == null) {
      stop = roundDate(new Date());
    }

    if (startPeriod != null) {
      startPeriod = roundDate(startPeriod);
      if (start.before(startPeriod)) {
        start = startPeriod;
      }
    }

    if (stopPeriod != null) {
      stopPeriod = roundDate(stopPeriod);
      if (stop.after(stopPeriod)) {
        stop = stopPeriod;
      }
    }
    long timeBetween = stop.getTime() - start.getTime();
    if (timeBetween < 0) {
      return null;
    }
    return new Long(timeBetween);
  }

  /**
   * @return month beginning for given date
   */
  public static Calendar getMonthBegin(Date date) {
    return setCalendar(date, 1, 0, 0, 0, 0);
  }

  /**
   * @return month ending for given date
   */
  public static Calendar getMonthEnd(Date date) {
    return setCalendar(date, create(date).getActualMaximum(Calendar.DATE), 23,
        59, 59, 999);
  }

  /**
   * @return week beginning for given date
   */
  public static Calendar getWeekBegin(Date date, int firstDayOfWeek) {
    Calendar weekCalendar = create(date);

    int current = weekCalendar.get(Calendar.DAY_OF_WEEK);

    int delta = firstDayOfWeek <= current ? (current - firstDayOfWeek)
        : (current + 7 - firstDayOfWeek);

    weekCalendar.add(Calendar.DAY_OF_WEEK, -delta);
    weekCalendar.set(Calendar.HOUR_OF_DAY, 0);
    weekCalendar.set(Calendar.MINUTE, 0);
    weekCalendar.set(Calendar.SECOND, 0);
    weekCalendar.set(Calendar.MILLISECOND, 0);
    return weekCalendar;
  }

  /**
   * @return week ending for given date
   */
  public static Calendar getWeekEnd(Date date, int firstDayOfWeek) {
    Calendar weekCalendar = getWeekBegin(date, firstDayOfWeek);
    weekCalendar.add(Calendar.DAY_OF_WEEK, 6);
    return weekCalendar;
  }

  /**
   * @return year beginning for given date
   */
  public static Calendar getYearBegin(Date date) {
    return setCalendar(date, Calendar.JANUARY, 1, 0, 0, 0, 0);
  }

  /**
   * @return year ending for given date
   */
  public static Calendar getYearEnd(Date date) {
    return setCalendar(date, Calendar.DECEMBER, 31, 23, 59, 59, 999);
  }

  /**
   * Check if given date is current day
   * 
   * @param date
   *          date to check
   * @return true - date is current day, false - otherwise
   */
  public static boolean isCurrentDay(Date date) {
    Calendar current = create(new Date());
    Calendar evaluated = create(date);
    return TimeUtils.equal(current, evaluated, Calendar.YEAR)
        && TimeUtils.equal(current, evaluated, Calendar.MONTH)
        && TimeUtils.equal(current, evaluated, Calendar.DATE);
  }

  /**
   * Rounds given date to date without seconds.
   */
  public static Date roundDate(Date date) {
    if (date == null) {
      return null;
    }
    long millis = date.getTime();

    // cut any milliseconds, leave full seconds
    millis /= 1000;
    millis *= 1000;

    // round to full minutes
    long secs = millis / 1000;
    secs %= 60;
    if (secs >= 30) {
      millis += (60 - secs) * 1000;
    } else {
      millis -= secs * 1000;
    }
    return new Date(millis);
  }

  /**
   * Sets given Calendar measure type from given calendar to given calendar
   */
  public static void set(Calendar to, Calendar from, int type) {
    to.set(type, from.get(type));
  }

  public static long dayEnd(Date date) {
    return getDayEnd(date).getTime().getTime();
  }

  public static long dayStart(Date date) {
    return getDayBegin(date).getTime().getTime();
  }

  public static String getDurationInDecimalHours(long duration) {
    long hours = 0;
    long minutes = 0;
    long fraction = 0;
    minutes = duration / 1000L / 60L;
    hours = duration / 1000L / 60L / 60L;

    fraction = (minutes - hours * 60) * 100L / 60L;
    return hours + "." + fraction;
  }

  /**
   * Set Calendar for week.
   * 
   * @return calendar for week
   */
  public static Calendar setCalendar(Date date, int hour, int minute,
      int second, int millisecond) {
    Calendar calendar = create(date);
    calendar.set(Calendar.HOUR_OF_DAY, hour);
    calendar.set(Calendar.MINUTE, minute);
    calendar.set(Calendar.SECOND, second);
    calendar.set(Calendar.MILLISECOND, millisecond);
    return calendar;
  }

  /**
   * Set Calendar for day in month.
   * 
   * @return calendar for day in month
   */
  public static Calendar setCalendar(Date date, int day_of_month, int hour,
      int minute, int second, int millisecond) {
    Calendar calendar = setCalendar(date, hour, minute, second, millisecond);
    calendar.set(Calendar.DATE, day_of_month);
    return calendar;
  }

  /**
   * Set Calendar for month.
   * 
   * @return calendar for month
   */
  public static Calendar setCalendar(Date date, int month, int day_of_month,
      int hour, int minute, int second, int millisecond) {
    Calendar calendar = setCalendar(date, day_of_month, hour, minute, second,
        millisecond);
    calendar.set(Calendar.MONTH, month);
    return calendar;
  }

  private TimeUtils() {
    // utility class
  }

}
