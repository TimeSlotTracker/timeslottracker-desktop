package net.sf.timeslottracker.filters;

import net.sf.timeslottracker.data.Task;

/**
 * Filter hidden tasks
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class HiddenTaskFilter implements TaskFilter {

  @Override
  public boolean accept(Task task) {
    return !task.isHidden();
  }

}
