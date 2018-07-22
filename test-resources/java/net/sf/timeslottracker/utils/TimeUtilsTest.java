package net.sf.timeslottracker.utils;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

public class TimeUtilsTest {

  public static Date date(String date) throws Throwable {
    return new SimpleDateFormat("yyyy-MM-dd").parse(date);
  }

  public static Date dateTime(String date) throws Throwable {
    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(date);
  }

  @Test
  public void testGetWeekBegin() throws Throwable {
    Calendar weekBegin = TimeUtils.getWeekBegin(date("2009-05-16"),
        Calendar.MONDAY);
    assertEquals(11, weekBegin.get(Calendar.DATE));

    weekBegin = TimeUtils.getWeekBegin(date("2009-05-10"), Calendar.MONDAY);
    assertEquals(4, weekBegin.get(Calendar.DATE));
  }

  @Test
  public void testGetWeekEnd() throws Throwable {
    Calendar weekEnd = TimeUtils
        .getWeekEnd(date("2009-05-16"), Calendar.MONDAY);
    assertEquals(17, weekEnd.get(Calendar.DATE));

    weekEnd = TimeUtils.getWeekEnd(date("2009-05-10"), Calendar.MONDAY);
    assertEquals(10, weekEnd.get(Calendar.DATE));
  }

  /**
   * Test to regression test reduction of duplicate code in net.sf.timeslottracker.utils.TimeUtils.
   * @throws Throwable
   */
  @Test
  public void testGetDayBegin() throws Throwable {
    Calendar calendar = TimeUtils.getDayBegin(dateTime("2014-03-26 13:12:11.123"));
    // assert 2014-03-26 00:00:00.000
    assertEquals(2014, calendar.get(Calendar.YEAR));
    assertEquals(Calendar.MARCH, calendar.get(Calendar.MONTH));
    assertEquals(26, calendar.get(Calendar.DAY_OF_MONTH));
    assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY));
    assertEquals(0, calendar.get(Calendar.MINUTE));
    assertEquals(0, calendar.get(Calendar.SECOND));
    assertEquals(0, calendar.get(Calendar.MILLISECOND));
  }

  /**
   * Test to regression test reduction of duplicate code in net.sf.timeslottracker.utils.TimeUtils.
   * @throws Throwable
   */
  @Test
  public void testGetDayEnd() throws Throwable {
    Calendar calendar = TimeUtils.getDayEnd(dateTime("2014-03-26 13:12:11.123"));
    // assert 2014-03-26 23:59:59.999
    assertEquals(2014, calendar.get(Calendar.YEAR));
    assertEquals(Calendar.MARCH, calendar.get(Calendar.MONTH));
    assertEquals(26, calendar.get(Calendar.DAY_OF_MONTH));
    assertEquals(23, calendar.get(Calendar.HOUR_OF_DAY));
    assertEquals(59, calendar.get(Calendar.MINUTE));
    assertEquals(59, calendar.get(Calendar.SECOND));
    assertEquals(999, calendar.get(Calendar.MILLISECOND));
  }

  /**
   * Test to regression test reduction of duplicate code in net.sf.timeslottracker.utils.TimeUtils.
   * @throws Throwable
   */
  @Test
  public void testGetMonthBegin() throws Throwable {
    Calendar calendar = TimeUtils.getMonthBegin(dateTime("2014-02-26 13:12:11.123"));
    assertEquals(2014, calendar.get(Calendar.YEAR));
    assertEquals(Calendar.FEBRUARY, calendar.get(Calendar.MONTH));
    // assert 1
    assertEquals(1, calendar.get(Calendar.DAY_OF_MONTH));
  }

  /**
   * Test to regression test reduction of duplicate code in net.sf.timeslottracker.utils.TimeUtils.
   * @throws Throwable
   */
  @Test
  public void testGetMonthEnd() throws Throwable {
    Calendar calendar = TimeUtils.getMonthEnd(dateTime("2014-02-26 13:12:11.123"));
    // assert 2014-02-28
    assertEquals(2014, calendar.get(Calendar.YEAR));
    assertEquals(Calendar.FEBRUARY, calendar.get(Calendar.MONTH));
    assertEquals(28, calendar.get(Calendar.DAY_OF_MONTH));

    // leap year
    calendar = TimeUtils.getMonthEnd(dateTime("2012-02-26 13:12:11.123"));
    // assert 2012-02-29
    assertEquals(2012, calendar.get(Calendar.YEAR));
    assertEquals(Calendar.FEBRUARY, calendar.get(Calendar.MONTH));
    assertEquals(29, calendar.get(Calendar.DAY_OF_MONTH));

    calendar = TimeUtils.getMonthEnd(dateTime("2014-01-26 13:12:11.123"));
    // assert 2014-01-31
    assertEquals(2014, calendar.get(Calendar.YEAR));
    assertEquals(Calendar.JANUARY, calendar.get(Calendar.MONTH));
    assertEquals(31, calendar.get(Calendar.DAY_OF_MONTH));

    calendar = TimeUtils.getMonthEnd(dateTime("2014-04-26 13:12:11.123"));
    // assert 2014-04-30
    assertEquals(2014, calendar.get(Calendar.YEAR));
    assertEquals(Calendar.APRIL, calendar.get(Calendar.MONTH));
    assertEquals(30, calendar.get(Calendar.DAY_OF_MONTH));
  }

  /**
   * Test to regression test reduction of duplicate code in net.sf.timeslottracker.utils.TimeUtils.
   * @throws Throwable
   */
  @Test
  public void testGetYearBegin() throws Throwable {
    Calendar calendar = TimeUtils.getYearBegin(dateTime("2014-02-26 13:12:11.123"));
    // assert 2014-01-01 00:00:00.000
    assertEquals(2014, calendar.get(Calendar.YEAR));
    assertEquals(Calendar.JANUARY, calendar.get(Calendar.MONTH));
    assertEquals(1, calendar.get(Calendar.DAY_OF_MONTH));
    assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY));
    assertEquals(0, calendar.get(Calendar.MINUTE));
    assertEquals(0, calendar.get(Calendar.SECOND));
    assertEquals(0, calendar.get(Calendar.MILLISECOND));
  }

  /**
   * Test to regression test reduction of duplicate code in net.sf.timeslottracker.utils.TimeUtils.
   * @throws Throwable
   */
  @Test
  public void testGetYearEnd() throws Throwable {
    Calendar calendar = TimeUtils.getYearEnd(dateTime("2014-02-26 13:12:11.123"));
    // assert 2014-12-31 23:59:59.999
    assertEquals(2014, calendar.get(Calendar.YEAR));
    assertEquals(Calendar.DECEMBER, calendar.get(Calendar.MONTH));
    assertEquals(31, calendar.get(Calendar.DAY_OF_MONTH));
    assertEquals(23, calendar.get(Calendar.HOUR_OF_DAY));
    assertEquals(59, calendar.get(Calendar.MINUTE));
    assertEquals(59, calendar.get(Calendar.SECOND));
    assertEquals(999, calendar.get(Calendar.MILLISECOND));
  }
}
