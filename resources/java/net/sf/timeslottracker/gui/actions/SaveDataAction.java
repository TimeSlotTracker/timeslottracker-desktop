package net.sf.timeslottracker.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import net.sf.timeslottracker.gui.LayoutManager;

/**
 * Save data gui action
 * 
 * @version File version: $Revision: 1.23 $, $Date: 2007/04/07 03:59:30 $
 * @author Last change: $Author: cnitsa $
 */
public class SaveDataAction extends AbstractAction {
  private final LayoutManager layoutManager;

  public SaveDataAction(LayoutManager layoutManager) {
    super(layoutManager.getString("menuBar.item.File.SaveNow"), layoutManager
        .getIcon("savefile"));
    this.layoutManager = layoutManager;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    layoutManager.getTimeSlotTracker().getDataSource().saveAll(true);
  }

}
