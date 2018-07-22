package net.sf.timeslottracker.gui.attributes;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.sf.timeslottracker.gui.LayoutManager;

/**
 * A panel with buttons for saving or discarding changes made in attribute types
 * window.
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-06-21 19:32:18 +0700
 *          (Sun, 21 Jun 2009) $
 * @author Last change: $Author: cnitsa $
 */
class ButtonsPanel extends JPanel {

  private JButton buttonAdd;

  private JButton buttonEdit;

  private JButton buttonRemove;

  private JButton buttonSave;

  private JButton buttonCancel;

  private AttributeTypesWindow attributeTypesWindow;

  ButtonsPanel(LayoutManager layoutManager,
      AttributeTypesWindow attributeTypesWindow) {
    super(new FlowLayout(FlowLayout.CENTER, 10, 5));
    this.attributeTypesWindow = attributeTypesWindow;

    buttonAdd = new JButton(
        layoutManager.getCoreString("attributes.window.button.add.label"),
        layoutManager.getIcon("add"));
    buttonAdd.addActionListener(new AddAction());

    buttonEdit = new JButton(
        layoutManager.getCoreString("attributes.window.button.edit.label"),
        layoutManager.getIcon("edit"));
    buttonEdit.addActionListener(new EditAction());

    buttonRemove = new JButton(
        layoutManager.getCoreString("attributes.window.button.remove.label"),
        layoutManager.getIcon("delete"));
    buttonRemove.addActionListener(new RemoveAction());

    buttonCancel = new JButton(
        layoutManager.getCoreString("attributes.window.button.cancel.label"),
        layoutManager.getIcon("cancel"));
    buttonCancel.addActionListener(new CancelAction());

    buttonSave = new JButton(
        layoutManager.getCoreString("attributes.window.button.save.label"),
        layoutManager.getIcon("save"));
    buttonSave.addActionListener(new SaveAction());

    add(buttonAdd);
    add(buttonEdit);
    add(buttonRemove);
    add(buttonCancel);
    add(buttonSave);
  }

  /**
   * Action used when a user chooses add button.
   */
  private class AddAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      attributeTypesWindow.add();
    }
  }

  /**
   * Action used when a user chooses edit button.
   */
  private class EditAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      attributeTypesWindow.edit();
    }
  }

  /**
   * Action used when a user chooses remove button.
   */
  private class RemoveAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      attributeTypesWindow.remove();
    }
  }

  /**
   * Action used when a user chooses cancel button.
   */
  private class CancelAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      attributeTypesWindow.closeWindow();
    }
  }

  /**
   * Action used when a user chooses save button.
   */
  private class SaveAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      attributeTypesWindow.saveChanges();
    }
  }

}
