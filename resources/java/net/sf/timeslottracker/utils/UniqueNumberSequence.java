package net.sf.timeslottracker.utils;

/**
 * Class used for creating unique number's sequence
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-08-06 09:55:17 +0700
 *          (Thu, 06 Aug 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class UniqueNumberSequence {

  private Integer idCounter = 0;

  public UniqueNumberSequence() {
  }

  /**
   * Returns a next id as a Integer. It is guaranteed to be unique.
   */
  public synchronized Integer getNextId() {
    idCounter = new Integer(idCounter.intValue() + 1);
    return idCounter;
  }

  /**
   * Checks if manually added id isn't greater then our counter. If it is our
   * static counter is increased.
   */
  public synchronized void update(Integer id) {
    if (idCounter.compareTo(id) < 0) {
      idCounter = id;
    }
  }

}
