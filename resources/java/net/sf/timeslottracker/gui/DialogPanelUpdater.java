package net.sf.timeslottracker.gui;

/**
 * Implementations should use this interface if they want update
 * {@link DialogPanel}
 * 
 * @version File version: $Revision: 1.23 $, $Date: 2007/04/07 03:59:30 $
 * @author Last change: $Author: cnitsa $
 */
public interface DialogPanelUpdater {

  /**
   * Update given dialog panel
   * 
   * @param panel
   *          dialog panel, never null
   */
  void update(DialogPanel panel);

}
