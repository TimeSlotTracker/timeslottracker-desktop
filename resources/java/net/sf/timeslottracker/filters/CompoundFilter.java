package net.sf.timeslottracker.filters;

/**
 * Compound filter which filters object by given filters collection
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
abstract class CompoundFilter<T> implements Filter<T> {

  private final Iterable<Filter<T>> filters;

  public CompoundFilter(Iterable<Filter<T>> filters) {
    this.filters = filters;
  }

  @Override
  public boolean accept(T object) {

    for (Filter<T> filter : filters) {
      if (filter.accept(object)) {
        return true;
      }
    }

    return false;
  }
}
