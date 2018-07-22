package net.sf.timeslottracker.gui.layouts.classic.timeslots;

import net.sf.timeslottracker.core.Action;
import net.sf.timeslottracker.data.DataLoadedListener;

/**
 * Listener to change the configuration changing.
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
class SetColumnListener implements DataLoadedListener {

  private TimeslotsTableModel tableModel;

  /**
   * Constructs the listener based on the table model.
   */
  SetColumnListener(TimeslotsTableModel tableModel) {
    this.tableModel = tableModel;
  }

  public void actionPerformed(Action action) {
    tableModel.setColumns();
  }
}
