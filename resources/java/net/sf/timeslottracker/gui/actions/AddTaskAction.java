package net.sf.timeslottracker.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import net.sf.timeslottracker.gui.LayoutManager;

/**
 * Add task gui action
 * 
 * @version File version: $Revision: 1.23 $, $Date: 2007/04/07 03:59:30 $
 * @author Last change: $Author: cnitsa $
 */
public class AddTaskAction extends AbstractAction {
  private final LayoutManager layoutManager;

  public AddTaskAction(LayoutManager layoutManager) {
    super(layoutManager.getString("taskstree.popupmenu.addTask.name") + " ...",
        layoutManager.getIcon("new"));
    this.layoutManager = layoutManager;

    putValue(
        MNEMONIC_KEY,
        KeyStroke.getKeyStroke(
            layoutManager.getString("taskstree.popupmenu.addTask.mnemonic"))
            .getKeyCode());
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0));
    putValue(SHORT_DESCRIPTION,
        layoutManager.getString("taskstree.popupmenu.addTask.name"));
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed(ActionEvent e) {
    layoutManager.getTasksInterface().add(null, null);
  }

}
