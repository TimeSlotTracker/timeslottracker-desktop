package net.sf.timeslottracker.gui.attributes;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.sf.timeslottracker.core.TimeSlotTracker;
import net.sf.timeslottracker.gui.LayoutManager;

/**
 * A panel with buttons for adding, removing and editing attributes. Used in
 * task's attribute and timeSlot's attribute table.
 * <p>
 * Buttons are represented via icons and comminicates with the parent window via
 * <code>EditingWindow</code> interface.
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-06-21 19:32:18 +0700
 *          (Sun, 21 Jun 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class TableButtonsPanel extends JPanel {

  private LayoutManager layoutManager;
  private TimeSlotTracker timeSlotTracker;
  private JButton buttonAdd;
  private JButton buttonEdit;
  private JButton buttonRemove;
  private EditingWindow window;

  public TableButtonsPanel(LayoutManager layoutManager, EditingWindow window) {
    super(new GridLayout(3, 1, 5, 0));
    this.setBorder(new EmptyBorder(3, 3, 3, 3));
    this.layoutManager = layoutManager;
    this.timeSlotTracker = layoutManager.getTimeSlotTracker();
    this.window = window;

    buttonAdd = new JButton(layoutManager.getIcon("add"));
    buttonAdd.setToolTipText(layoutManager
        .getCoreString("attributes.table.buttons.add.tooltip"));
    buttonAdd.addActionListener(new AddAction());

    buttonEdit = new JButton(layoutManager.getIcon("edit"));
    buttonEdit.setToolTipText(layoutManager
        .getCoreString("attributes.table.buttons.edit.tooltip"));
    buttonEdit.addActionListener(new EditAction());

    buttonRemove = new JButton(layoutManager.getIcon("delete"));
    buttonRemove.setToolTipText(layoutManager
        .getCoreString("attributes.table.buttons.remove.tooltip"));
    buttonRemove.addActionListener(new RemoveAction());

    add(buttonAdd);
    add(buttonEdit);
    add(buttonRemove);
  }

  /**
   * Action used when a user chooses add button.
   */
  private class AddAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      window.add();
    }
  }

  /**
   * Action used when a user chooses edit button.
   */
  private class EditAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      window.edit();
    }
  }

  /**
   * Action used when a user chooses remove button.
   */
  private class RemoveAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      window.remove();
    }
  }

}
