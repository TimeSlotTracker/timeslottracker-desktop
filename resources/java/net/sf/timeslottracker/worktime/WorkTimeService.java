package net.sf.timeslottracker.worktime;

import java.util.Date;

/**
 * Work time planning service
 * 
 * @author glazachev
 */
public interface WorkTimeService {

  /**
   * Evaluates planning work time with specified period
   * 
   * @param start
   *          start of time period
   * @param finish
   *          finish of time period
   * @return
   */
  long getWorkTime(Date start, Date finish);

}
