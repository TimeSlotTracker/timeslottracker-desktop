package net.sf.timeslottracker.data.common;

import net.sf.timeslottracker.core.TimeSlotTracker;
import net.sf.timeslottracker.core.TimeSlotTrackerException;

/**
 * Exception occurred while working with {@link TransactionalFileSaver}
 * 
 * @author cnitsa
 */
public class TransactionalFileSaverException extends TimeSlotTrackerException {

  public TransactionalFileSaverException(TimeSlotTracker tst, String messageId) {
    super(tst, messageId);
  }

  public TransactionalFileSaverException(TimeSlotTracker tst, String messageId,
      Object[] args) {
    super(tst, messageId, args);
  }

}
