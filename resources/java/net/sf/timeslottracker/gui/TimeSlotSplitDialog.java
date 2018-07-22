package net.sf.timeslottracker.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import net.sf.timeslottracker.data.Attribute;
import net.sf.timeslottracker.data.DataSource;
import net.sf.timeslottracker.data.TimeSlot;

/**
 * A simple timeslot split dialog. Patch from Gerth Hermansson
 * <p>
 * It is composed of several fields. One field for year, one for month, etc.
 * 
 * File version: $Revision: 1.13 $, $Date: 2008-04-13 09:16:17 $ Last change:
 * $Author: $
 */
@SuppressWarnings("serial")
public class TimeSlotSplitDialog extends AbstractSimplePanelDialog {

  private LayoutManager layoutManager;

  private boolean readonly;

  private TimeSlot timeslot;

  private TimeSlot timeslotAfterSplit;

  private DatetimeEditPanel startDate;

  private DatetimeEditPanel splitDate;

  private DatetimeEditPanel stopDate;

  private DescriptionInputComboBox inputComboBox;

  private DescriptionInputComboBox inputComboBoxSplit;

  public TimeSlotSplitDialog(LayoutManager layoutManager, TimeSlot timeslot,
      boolean readonly) {
    super(layoutManager, layoutManager
        .getCoreString("splitDialog.timeslot.title"));

    this.layoutManager = layoutManager;
    this.timeslot = timeslot;
    this.readonly = readonly;

    this.inputComboBox = new DescriptionInputComboBox(layoutManager, readonly);
    this.inputComboBoxSplit = new DescriptionInputComboBox(layoutManager,
        readonly);
  }

  private void reloadFields() {
    if (timeslot == null) {
      return;
    }
    startDate.setDatetime(timeslot.getStartDate());
    long splitTime = (timeslot.getStopDate().getTime() + timeslot
        .getStartDate().getTime()) / 2L;
    splitDate.setDatetime(new Date(splitTime));
    stopDate.setDatetime(timeslot.getStopDate());
    inputComboBox.setActiveDescription(timeslot.getDescription());
    inputComboBoxSplit.setActiveDescription("-");
  }

  protected JPanel createButtons() {
    FlowLayout layout = new FlowLayout(FlowLayout.CENTER);
    layout.setHgap(15);
    JPanel buttons = new JPanel(layout);
    String key = readonly ? "editDialog.timeslot.button.cancel.readonly"
        : "editDialog.timeslot.button.cancel.editable";
    JButton cancelButton = new JButton(coreString(key), icon("cancel"));
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
      JButton saveButton = new JButton(
          coreString("editDialog.timeslot.button.save"), icon("save"));
      SaveAction saveAction = new SaveAction();
      saveButton.addActionListener(saveAction);
      buttons.add(saveButton);
      // set save button as a default button when in editable mode
      getRootPane().setDefaultButton(saveButton);
    }
    return buttons;
  }

  protected void fillDialogPanel(DialogPanel panel) {
    startDate = new DatetimeEditPanel(layoutManager, readonly, true, true);
    splitDate = new DatetimeEditPanel(layoutManager, readonly, true, true);
    stopDate = new DatetimeEditPanel(layoutManager, readonly, true, true);
    inputComboBox.setEnabled(!readonly);
    inputComboBoxSplit.setEnabled(!readonly);

    panel.addRow(coreString("editDialog.timeslot.start.date.name"), startDate);
    panel.addRow(coreString("editDialog.timeslot.description.name"),
        inputComboBox);
    panel.addRow(coreString("splitDialog.timeslot.split.date.name"), splitDate);
    panel.addRow(coreString("editDialog.timeslot.description.name"),
        inputComboBoxSplit);
    panel.addRow(coreString("editDialog.timeslot.stop.date.name"), stopDate);
  }

  protected void beforeShow() {
    reloadFields();
    setResizable(true);
    pack();
  }

  /**
   * Returns a timeslot after split
   * 
   * @return a timeslot after split or null value if a user canceled dialog
   */
  public TimeSlot getTimeslot() {
    return timeslot;
  }

  /**
   * Returns a new created or just saved timeslot
   * 
   * @return new created or null value if a user canceled dialog
   */
  public TimeSlot getTimeslotAfterSplit() {
    return timeslotAfterSplit;
  }

  /**
   * Action used when a user choose cancel/close button.
   * <p>
   * It simply reset task variable, so <code>getTimeslot</code> will return null
   * 
   * getTimeslot()
   */
  private class CancelAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      timeslot = null;
      dispose();
    }
  }

  /**
   * Class called when a user pressed "Save" button.
   * <p>
   * If timeslot was given in constructor actual data will be set in that
   * timeslot; <br>
   * If this is a new timeslot, new TimeSlot record will be created to be
   * returned with <code>getTimeslot</code> method.
   * 
   * getTimeslot()
   */
  private class SaveAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      try {
        String description = inputComboBox.getDescription();
        // check if a description isn't empty
        if (description == null || description.length() == 0) {
          String msgTitle = coreString("editDialog.timeslot.description.cannotBeEmpty.title");
          String msg = coreString("editDialog.timeslot.description.cannotBeEmpty.msg");
          JOptionPane.showMessageDialog(TimeSlotSplitDialog.this, msg,
              msgTitle, JOptionPane.WARNING_MESSAGE);
          return;
        }

        String descriptionSplit = inputComboBoxSplit.getDescription();
        // check if a description isn't empty
        if (descriptionSplit == null || descriptionSplit.length() == 0) {
          String msgTitle = coreString("editDialog.timeslot.description.cannotBeEmpty.title");
          String msg = coreString("editDialog.timeslot.description.cannotBeEmpty.msg");
          JOptionPane.showMessageDialog(TimeSlotSplitDialog.this, msg,
              msgTitle, JOptionPane.WARNING_MESSAGE);
          return;
        }

        // edit the original timeslot
        timeslot.setStartDate(startDate.getDatetime());
        timeslot.setStopDate(splitDate.getDatetime());
        timeslot.setDescription(description);

        // create a new timeslot for the split
        DataSource dataSource = layoutManager.getTimeSlotTracker()
            .getDataSource();
        timeslotAfterSplit = dataSource.createTimeSlot(null,
            splitDate.getDatetime(), stopDate.getDatetime(), descriptionSplit);

        Vector<Attribute> newAttributes = new Vector<Attribute>();
        for (Attribute attribute : timeslot.getAttributes()) {
          newAttributes.add(new Attribute(attribute.getAttributeType(),
              attribute.get()));
        }
        timeslotAfterSplit.setAttributes(newAttributes);

        dispose();
      } catch (NumberFormatException nfe) {
        String errorTitle = coreString("alert.error.title");
        String errorMsg = coreString("editDialog.timeslot.error.NumberFormatException");
        JOptionPane.showMessageDialog(TimeSlotSplitDialog.this, errorMsg,
            errorTitle, JOptionPane.ERROR_MESSAGE);
      }
    }
  }
}
