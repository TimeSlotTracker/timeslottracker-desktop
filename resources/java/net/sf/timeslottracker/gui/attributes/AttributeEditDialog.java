package net.sf.timeslottracker.gui.attributes;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import net.sf.timeslottracker.data.Attribute;
import net.sf.timeslottracker.data.AttributeType;
import net.sf.timeslottracker.data.DataSource;
import net.sf.timeslottracker.gui.DialogPanel;
import net.sf.timeslottracker.gui.LayoutManager;

/**
 * A simple dialog to edit attribute (whenever it belongs to task or timeslot).
 * 
 * @version File version: $Revision: 1075 $, $Date: 2009-08-23 17:38:35 +0700
 *          (Sun, 23 Aug 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class AttributeEditDialog extends JDialog {

  private LayoutManager layoutManager;
  private boolean readonly;
  private boolean showUsedInTask;
  private boolean showUsedInTimeSlot;

  private DataSource dataSource;
  private Attribute attribute;
  /** Holds currently used attribute type **/
  private AttributeType attributeType;
  private DialogPanel dialog;
  private JPanel buttonsPanel;
  private JComboBox attrType = new JComboBox();
  private Component attrValue;

  public AttributeEditDialog(LayoutManager layoutManager, Attribute attribute,
      boolean showUsedInTask, boolean showUsedInTimeSlot, boolean readonly) {
    super(layoutManager.getTimeSlotTracker().getRootFrame(), layoutManager
        .getCoreString("editDialog.attribute.title"), true);
    this.layoutManager = layoutManager;
    this.dataSource = layoutManager.getTimeSlotTracker().getDataSource();
    this.attribute = attribute;
    this.readonly = readonly;
    this.showUsedInTask = showUsedInTask;
    this.showUsedInTimeSlot = showUsedInTimeSlot;

    reloadFields();

    getContentPane().setLayout(new BorderLayout());
    buttonsPanel = createButtons();
    createDialog();
    setResizable(true);
    setLocationRelativeTo(getRootPane());

    attrType.addActionListener(new ChangeEditComponentAction());

    setVisible(true);
  }

  private void reloadFields() {
    // reload combo with categories
    attrType.removeAllItems();
    Iterator types = dataSource.getAttributeTypes().iterator();
    while (types.hasNext()) {
      AttributeType type = (AttributeType) types.next();
      if ((showUsedInTask && type.getUsedInTasks())
          || (showUsedInTimeSlot && type.getUsedInTimeSlots())) {
        attrType.addItem(type);
      }
    }

    // reload old fields
    if (attribute == null) {
      /*
       * because there is no existing attribute (it's a new record) activate the
       * first category in the combobox.
       */
      attributeType = (AttributeType) attrType.getSelectedItem();
      if (attributeType != null) {
        attrValue = attributeType.getCategory().getEditComponent();
        attributeType.getCategory().beforeShow(null, attributeType);
      }
      return;
    }
    attributeType = attribute.getAttributeType();
    attrType.setSelectedItem(attributeType);
    attrValue = attributeType.getCategory().getEditComponent();
    attributeType.getCategory().beforeShow(attribute.get(), attributeType);
  }

  private void createDialog() {

    attrType.setEnabled(!readonly);
    if (attrValue != null) {
      attrValue.setEnabled(!readonly);
    }

    if (dialog == null) {
      // first run - add buttons
      getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
    } else {
      // buttons are already added, but remove previously added dialog
      getContentPane().remove(dialog);
    }
    dialog = new DialogPanel();
    dialog.addRow(layoutManager.getCoreString("editDialog.attribute.attrType"),
        attrType);
    if (attrValue != null) {
      // this will make the value 10 times heavier than the label.. good enough
      dialog.addRow(
          layoutManager.getCoreString("editDialog.attribute.attrValue"),
          attrValue, 10);
    }
    getContentPane().add(dialog, BorderLayout.CENTER);

    // set some dialog properties
    getContentPane().validate();
    pack();
  }

  private JPanel createButtons() {
    FlowLayout layout = new FlowLayout(FlowLayout.CENTER);
    layout.setHgap(15);
    JPanel buttons = new JPanel(layout);
    String cancelName;
    if (readonly) {
      cancelName = layoutManager
          .getCoreString("editDialog.attribute.button.cancel.readonly");
    } else {
      cancelName = layoutManager
          .getCoreString("editDialog.attribute.button.cancel.editable");
    }
    JButton cancelButton = new JButton(cancelName,
        layoutManager.getIcon("cancel"));
    CancelAction cancelAction = new CancelAction();
    cancelButton.addActionListener(cancelAction);
    buttons.add(cancelButton);

    // connect cancelAction with ESC key
    getRootPane().registerKeyboardAction(cancelAction,
        KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
        JComponent.WHEN_IN_FOCUSED_WINDOW);
    if (readonly) {
      getRootPane().setDefaultButton(cancelButton);
    } else {
      String saveName = layoutManager
          .getCoreString("editDialog.attribute.button.save");
      JButton saveButton = new JButton(saveName, layoutManager.getIcon("save"));
      SaveAction saveAction = new SaveAction();
      saveButton.addActionListener(saveAction);
      buttons.add(saveButton);
      // set save button as a default button when in editable mode
      getRootPane().setDefaultButton(saveButton);
    }
    return buttons;
  }

  /**
   * Returns a new created or just saved attribute type
   * 
   * @return new created or edited task or null value if a user canceled dialog
   */
  public Attribute getAttribute() {
    return attribute;
  }

  /**
   * Action used when a user change the selection in combo.
   * <p>
   * It removes actual editor component and replaces it with the new one, got
   * from AttributeCategory (from AttributeType).
   * 
   * @see net.sf.timeslottracker.data.AttributeCategory#getEditComponent()
   */
  private class ChangeEditComponentAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      String oldValue = null;
      if (attrValue != null && attributeType != null) {
        oldValue = attributeType.getCategory().getString();
      }
      attributeType = (AttributeType) attrType.getSelectedItem();
      attrValue = attributeType.getCategory().getEditComponent();
      if (oldValue != null) {
        attributeType.getCategory().beforeShow(oldValue, attributeType);
      }
      createDialog();
    }
  }

  /**
   * Action used when a user choose cancel/close button.
   * <p>
   * It simply restet task variable, so <code>getTask</code> will return null
   * 
   */
  private class CancelAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      attribute = null;
      dispose();
    }
  }

  /**
   * Class called when a user pressed "Save" button.
   * <p>
   * If task was given in constructor actual data will be set in that task; <br>
   * If this is a new task, new Task record will be created to be returned with
   * <code>getTask</code> method.
   * 
   */
  private class SaveAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      AttributeType type = (AttributeType) attrType.getSelectedItem();
      if (!type.getCategory().validate()) {
        String errorTitle = layoutManager
            .getCoreString("editDialog.attribute.invalidValue.title");
        String errorMessage = layoutManager
            .getCoreString("editDialog.attribute.invalidValue");
        JOptionPane.showMessageDialog(AttributeEditDialog.this, errorMessage,
            errorTitle, JOptionPane.WARNING_MESSAGE);
        return;
      }
      if (attribute == null) {
        attribute = new Attribute(type);
      } else {
        attribute.setAttributeType(type);
      }
      attribute.set(type.getCategory().beforeClose());
      dispose();
    }
  }

}
