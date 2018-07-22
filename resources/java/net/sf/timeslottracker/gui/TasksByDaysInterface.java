package net.sf.timeslottracker.gui;

import java.util.Collection;

/**
 * Describes tasks by days module interface
 * 
 * File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700 (Sat, 16 May
 * 2009) $ Last change: $Author: cnitsa $
 */
public interface TasksByDaysInterface {

  /** period of time: day */
  public enum Period {
    DAY
  };

  /**
   * Activate the module. Usually needs then module gets focus.
   */
  void activate();

  /**
   * @return current view menu items
   */
  Collection<javax.swing.JMenuItem> getMenuItems();

}
