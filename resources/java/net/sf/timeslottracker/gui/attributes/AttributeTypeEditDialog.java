package net.sf.timeslottracker.gui.attributes;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import net.sf.timeslottracker.data.AttributeCategory;
import net.sf.timeslottracker.data.AttributeType;
import net.sf.timeslottracker.gui.DialogPanel;
import net.sf.timeslottracker.gui.LayoutManager;

/**
 * A simple task edit attribute type.
 * 
 * @version File version: $Revision: 1086 $, $Date: 2009-08-20 03:29:08 +0700
 *          (Thu, 20 Aug 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class AttributeTypeEditDialog extends JDialog {

  private LayoutManager layoutManager;
  private boolean readonly;

  private AttributeType attributeType;
  private JComboBox attrCategory = new JComboBox();
  private JTextField attrName = new JTextField(40);
  private JTextField attrDefaultValue = new JTextField(40);
  private JTextField attrDescription = new JTextField(40);
  private JCheckBox usedInTasks = new JCheckBox();
  private JCheckBox usedInTimeSlots = new JCheckBox();
  private JCheckBox hiddenOnReports = new JCheckBox();
  private JCheckBox showInTaskInfo = new JCheckBox();
  private JCheckBox showInTimeSlots = new JCheckBox();
  private JCheckBox autoAddToTimeSlots = new JCheckBox();

  public AttributeTypeEditDialog(LayoutManager layoutManager,
      AttributeType attributeType, boolean readonly) {
    super(layoutManager.getTimeSlotTracker().getRootFrame(), layoutManager
        .getCoreString("editDialog.attributeType.title"), true);
    this.layoutManager = layoutManager;
    this.attributeType = attributeType;
    this.readonly = readonly;
    createDialog();
    reloadFields();
    setVisible(true);
  }

  private void reloadFields() {
    // reload combo with categories
    attrCategory.removeAllItems();
    attrCategory.addItem(new net.sf.timeslottracker.data.SimpleTextAttribute(
        layoutManager));
    attrCategory.addItem(new net.sf.timeslottracker.data.TextAreaAttribute(
        layoutManager));
    attrCategory.addItem(new net.sf.timeslottracker.data.LOVAttribute(
        layoutManager));
    attrCategory.addItem(new net.sf.timeslottracker.data.IntegerAttribute(
        layoutManager));
    attrCategory.addItem(new net.sf.timeslottracker.data.RealAttribute(
        layoutManager));
    attrCategory.addItem(new net.sf.timeslottracker.data.CheckBoxAttribute(
        layoutManager));
    attrCategory.addItem(new net.sf.timeslottracker.data.ImageAttribute());

    // reload old fields
    if (attributeType == null) {
      return;
    }
    AttributeCategory category = attributeType.getCategory();
    for (int count = 0; count < attrCategory.getItemCount(); count++) {
      if (attrCategory.getItemAt(count).toString().equals(category.toString())) {
        attrCategory.setSelectedIndex(count);
        break;
      }
    }
    attrName.setText(attributeType.getName());
    attrDescription.setText(attributeType.getDescription());
    attrDefaultValue.setText(attributeType.getDefault());
    usedInTasks.setSelected(attributeType.getUsedInTasks());
    usedInTimeSlots.setSelected(attributeType.getUsedInTimeSlots());
    hiddenOnReports.setSelected(attributeType.isHiddenOnReports());
    showInTaskInfo.setSelected(attributeType.getShowInTaskInfo());
    showInTimeSlots.setSelected(attributeType.getShowInTimeSlots());
    autoAddToTimeSlots.setSelected(attributeType.isAutoAddToTimeSlots());
  }

  private void createDialog() {
    getContentPane().setLayout(new BorderLayout());

    attrCategory.setEnabled(!readonly);
    attrName.setEnabled(!readonly);
    attrDescription.setEnabled(!readonly);
    attrDefaultValue.setEnabled(!readonly);
    usedInTasks.setEnabled(!readonly);
    usedInTimeSlots.setEnabled(!readonly);
    hiddenOnReports.setEnabled(!readonly);
    showInTaskInfo.setEnabled(!readonly);
    showInTimeSlots.setEnabled(!readonly);
    autoAddToTimeSlots.setEnabled(!readonly);

    DialogPanel dialog = new DialogPanel();
    dialog.addRow(
        layoutManager.getCoreString("editDialog.attributeType.attrCategory"),
        attrCategory);
    dialog.addRow(
        layoutManager.getCoreString("editDialog.attributeType.attrName"),
        attrName);
    dialog
        .addRow(layoutManager
            .getCoreString("editDialog.attributeType.attrDescription"),
            attrDescription);
    dialog.addRow(layoutManager
        .getCoreString("editDialog.attributeType.attrDefaultValue"),
        attrDefaultValue);
    dialog.addRow(
        layoutManager.getCoreString("editDialog.attributeType.usedInTasks"),
        usedInTasks);
    dialog.addRow(layoutManager
            .getCoreString("editDialog.attributeType.usedInTimeSlots"),
            usedInTimeSlots);
    dialog.addRow(layoutManager
            .getCoreString("editDialog.attributeType.hiddenOnReports"),
            hiddenOnReports);
    dialog.addRow(
        layoutManager.getCoreString("editDialog.attributeType.showInTaskInfo"),
        showInTaskInfo);
    dialog.addRow(layoutManager
            .getCoreString("editDialog.attributeType.showInTimeSlots"),
            showInTimeSlots);
    dialog.addRow(layoutManager
            .getCoreString("editDialog.attributeType.autoAddToTimeSlots"),
            autoAddToTimeSlots);
    getContentPane().add(dialog, BorderLayout.CENTER);
    getContentPane().add(createButtons(), BorderLayout.SOUTH);

    // set some dialog properties
    pack();
    setResizable(false);
    setLocationRelativeTo(getRootPane());
  }

  private JPanel createButtons() {
    FlowLayout layout = new FlowLayout(FlowLayout.CENTER);
    layout.setHgap(15);
    JPanel buttons = new JPanel(layout);
    String cancelName;
    if (readonly) {
      cancelName = layoutManager
          .getCoreString("editDialog.attributeType.button.cancel.readonly");
    } else {
      cancelName = layoutManager
          .getCoreString("editDialog.attributeType.button.cancel.editable");
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
          .getCoreString("editDialog.attributeType.button.save");
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
   * Action used when a user choose cancel/close button.
   * <p>
   * It simply reset task variable, so <code>getTask</code> will return null
   */
  private class CancelAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      attributeType = null;
      dispose();
    }
  }

  /**
   * Class called when a user pressed "Save" button.
   * <p>
   * If task was given in constructor actual data will be set in that task; <br>
   * If this is a new task, new Task record will be created to be returned with
   * <code>getTask</code> method.
   */
  private class SaveAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      AttributeCategory category = (AttributeCategory) attrCategory
          .getSelectedItem();
      if (attributeType == null) {
        attributeType = new AttributeType(category);
      } else {
        attributeType.setCategory(category);
      }
      attributeType.setName(attrName.getText());
      attributeType.setDefault(attrDefaultValue.getText());
      attributeType.setDescription(attrDescription.getText());
      attributeType.setUsedInTasks(usedInTasks.isSelected());
      attributeType.setUsedInTimeSlots(usedInTimeSlots.isSelected());
      attributeType.setHiddenOnReports(hiddenOnReports.isSelected());
      attributeType.setShowInTaskInfo(showInTaskInfo.isSelected());
      attributeType.setShowInTimeSlots(showInTimeSlots.isSelected());
      attributeType.setAutoAddToTimeSlots(autoAddToTimeSlots.isSelected());
      dispose();
    }
  }

  /**
   * Returns a new created or just saved attribute type
   * 
   * @return new created or edited task or null value if a user canceled dialog
   */
  public AttributeType getAttributeType() {
    return attributeType;
  }

}
