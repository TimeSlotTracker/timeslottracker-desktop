package net.sf.timeslottracker.core;

/**
 * An interface to action listener - a main one. This one is expanded by
 * specified action listeners. It defines only one method to be implement. It is
 * fired when some action (which it listens to) occurs.
 * 
 * File version: $Revision: 992 $, $Date: 2009-05-16 08:53:21 +0700 (Sat, 16 May
 * 2009) $ Last change: $Author: cnitsa $
 */
public interface ActionListener {

  /**
   * Defines what should be done on this specific action.
   * 
   * @param action
   *          Action object with filled all needed information about this action
   */
  void actionPerformed(Action action);
}
