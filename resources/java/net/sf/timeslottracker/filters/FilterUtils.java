package net.sf.timeslottracker.filters;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Filter utils
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class FilterUtils {

  private FilterUtils() {
  }

  /**
   * Filter the specified collection with specified filter
   * 
   * @param collection
   *          collection for filter
   * @param filter
   *          filter
   * @return filtered collection
   */
  public static <T> Collection<T> filter(Collection<T> collection,
      Filter<T> filter) {
    ArrayList<T> filtered = new ArrayList<T>();

    for (T object : collection) {
      if (filter.accept(object)) {
        filtered.add(object);
      }
    }

    return filtered;
  }

  /**
   * Create compound filter by given filters
   * 
   * @param filters
   *          filter to compound
   * @return filter
   */
  public static <T> Filter<T> compound(Iterable<Filter<T>> filters) {
    return new CompoundFilter<T>(filters) {
    };
  }
}
