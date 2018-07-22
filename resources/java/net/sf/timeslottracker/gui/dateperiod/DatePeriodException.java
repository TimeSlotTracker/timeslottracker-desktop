package net.sf.timeslottracker.gui.dateperiod;

/**
 * Exception occurred while working with {@link DatePeriod}
 * 
 * @version File version: $Revision: 1.23 $, $Date: 2007/04/07 03:59:30 $
 * @author Last change: $Author: cnitsa $
 */
public class DatePeriodException extends Exception {

  public DatePeriodException(String message, Throwable cause) {
    super(message, cause);
  }

  public DatePeriodException(String message) {
    super(message);
  }

}
