package net.sf.timeslottracker.gui;

import net.sf.timeslottracker.data.Task;

/**
 * An interface a task info module must implement to be possible to upgrade or
 * change the implementation of this module.
 * 
 * File version: $Revision: 1099 $, $Date: 2009-05-16 08:53:21 +0700 (Sat, 16 May
 * 2009) $ Last change: $Author: cnitsa $
 */
public interface TaskInfoInterface {

  /**
   * Shows task's data in the task info module (panel, component...)
   */
  void show(Task task);

  /**
   * Updates state
   */
  void refresh();

}
