package net.sf.timeslottracker.gui.layouts.classic.tasksbydays;

import java.util.Date;

/**
 * Node for dates
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
@SuppressWarnings("serial")
public class DateNode extends DaysTreeNode {

  private final Date date;

  private final int calendarDateType;

  public DateNode(Date date, int calendarDateType, String nodeName) {
    super(nodeName);
    this.date = date;
    this.calendarDateType = calendarDateType;
  }

  public Date getDate() {
    return date;
  }

  public int getCalendarDateType() {
    return calendarDateType;
  }

}
