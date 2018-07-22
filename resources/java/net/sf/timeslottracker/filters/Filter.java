package net.sf.timeslottracker.filters;

/**
 * Filter for objects
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public interface Filter<T> {

  /**
   * Filter selected object
   * 
   * @param object
   *          object to filter
   * @return true - accept object, false - otherwise
   */
  boolean accept(T object);
}
