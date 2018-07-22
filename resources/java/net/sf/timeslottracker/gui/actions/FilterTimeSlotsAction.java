package net.sf.timeslottracker.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import net.sf.timeslottracker.gui.LayoutManager;

/**
 * A Filter timeslots action
 * 
 * File version: $Revision: 998 $, $Date: 2009-11-05 20:15:33 +0600 (Thu, 05 Nov
 * 2009) $ Last change: $Author: cnitsa $
 */
@SuppressWarnings("serial")
public class FilterTimeSlotsAction extends AbstractAction {

  private LayoutManager layoutManager;

  public FilterTimeSlotsAction(LayoutManager layoutManager) {
    super(layoutManager.getString("menuBar.item.View.TaskTree.FilterData"));

    this.layoutManager = layoutManager;

    putValue(javax.swing.Action.NAME,
        layoutManager.getString("menuBar.item.View.TaskTree.FilterData"));
    update();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed(ActionEvent e) {
    layoutManager.getTimeSlotsInterface().filter();
    update();
  }

  private void update() {
    putValue(javax.swing.Action.SELECTED_KEY, false);
  }

}
