package net.sf.timeslottracker.core;

/**
 * An exception which is thrown when some problems in application occurs. Main
 * exception class which every application specific exception should extend.
 * 
 * File version: $Revision: 1038 $, $Date: 2009-05-16 08:53:21 +0700 (Sat, 16 May
 * 2009) $ Last change: $Author: cnitsa $
 */
@SuppressWarnings("serial")
public class TimeSlotTrackerException extends Exception {

  /**
   * Construct a new exception object.
   * 
   * @param tst
   *          reference to TimeSlotTracker object to give an ability to get
   *          localized message.
   * @param messageId
   *          string id in the properties file with localized strings.
   */
  public TimeSlotTrackerException(TimeSlotTracker tst, String messageId) {
    this(tst, tst.getString(messageId), null);
  }

  /**
   * Construct a new exception object.
   * 
   * @param tst
   *          reference to TimeSlotTracker object to give an ability to get
   *          localized message.
   * @param messageId
   *          string id in the properties file with localized strings.
   * @param args
   *          arguments for message
   */
  public TimeSlotTrackerException(TimeSlotTracker tst, String messageId,
      Object[] args) {
    super(args == null ? tst.getString(messageId) : tst.getString(messageId,
        args));
    tst.errorLog(this);
  }

}
