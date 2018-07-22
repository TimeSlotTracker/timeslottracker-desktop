package net.sf.timeslottracker.data;

import net.sf.timeslottracker.core.TimeSlotTracker;

/**
 * An exception which is thrown when DataSource could not be found.
 * 
 * File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700 (Sat, 16 May
 * 2009) $ Last change: $Author: cnitsa $
 */
public class DataSourceNotFoundException extends DataSourceException {

  /**
   * Construct a new exception object.
   * 
   * @param tst
   *          refererence to TimeSlotTracker object to give an ability to get
   *          localized message.
   * @param messageId
   *          string id in the properties file with localized strings.
   */
  public DataSourceNotFoundException(TimeSlotTracker tst, String messageId) {
    super(tst, messageId);
  }
}
