package net.sf.timeslottracker.data;

import net.sf.timeslottracker.core.TimeSlotTracker;
import net.sf.timeslottracker.core.TimeSlotTrackerException;

/**
 * An exception which is thrown when some problems with the DataSource occurs.
 * 
 * File version: $Revision: 1038 $, $Date: 2009-05-16 08:53:21 +0700 (Sat, 16 May
 * 2009) $ Last change: $Author: cnitsa $
 */
public class DataSourceException extends TimeSlotTrackerException {

  /**
   * Construct a new exception object.
   * 
   * @param tst
   *          refererence to TimeSlotTracker object to give an ability to get
   *          localized message.
   * @param messageId
   *          string id in the properties file with localized strings.
   */
  public DataSourceException(TimeSlotTracker tst, String messageId) {
    super(tst, messageId);
  }

  /**
   * Construct a new exception object.
   * 
   * @param tst
   *          refererence to TimeSlotTracker object to give an ability to get
   *          localized message.
   * @param messageId
   *          string id in the properties file with localized strings.
   * @param args
   *          arguments for message
   */
  public DataSourceException(TimeSlotTracker tst, String messageId,
      Object[] args) {
    super(tst, messageId, args);
  }

}
