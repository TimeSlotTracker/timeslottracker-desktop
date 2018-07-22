package net.sf.timeslottracker.filters;

/**
 * Filter returns all objects (do not filter)
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
class TrueFilter implements Filter<Object> {

  public boolean accept(Object object) {
    return true;
  }

}
