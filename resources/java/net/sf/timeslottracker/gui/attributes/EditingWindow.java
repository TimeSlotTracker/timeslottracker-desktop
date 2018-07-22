package net.sf.timeslottracker.gui.attributes;

/**
 * An interface used to communicating between TaskEditDialog and
 * TimeSlotEditDialog windows with TableButtonsPanel.
 * <p>
 * Defines three methods (add, edit, remove) any class implementing this
 * interface have to implement.
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public interface EditingWindow {

  /**
   * Called when a user pressed "add" button.
   */
  void add();

  /**
   * Called when a user pressed "edit" button.
   */
  void edit();

  /**
   * Called when a user pressed "remove" button.
   */
  void remove();
}
