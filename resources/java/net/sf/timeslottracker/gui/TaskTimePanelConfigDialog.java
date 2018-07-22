package net.sf.timeslottracker.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.core.TimeSlotTracker;

/**
 * A simple dialog to day, week and month to show inside task info panel.
 * 
 * File version: $Revision: 998 $, $Date: 2009-06-21 18:47:38 +0700 (Sun, 21 Jun
 * 2009) $ Last change: $Author: cnitsa $
 */
public class TaskTimePanelConfigDialog extends JDialog {

  private LayoutManager layoutManager;
  private boolean includingSubtasks;
  private DatetimeEditPanel fieldDay;
  private DatetimeEditPanel fieldWeek;
  private DatetimeEditPanel fieldMonth;

  private TimeSlotTracker timeSlotTracker;
  private Configuration configuration;

  private static final SimpleDateFormat dateFormater = new SimpleDateFormat(
      "yyyy-MM-dd");

  public TaskTimePanelConfigDialog(LayoutManager layoutManager,
      boolean includingSubtasks) {
    super(layoutManager.getTimeSlotTracker().getRootFrame(), layoutManager
        .getCoreString("taskTimePanel.configuration.dialog.title"), true);
    this.layoutManager = layoutManager;
    this.includingSubtasks = includingSubtasks;
    timeSlotTracker = layoutManager.getTimeSlotTracker();
    configuration = timeSlotTracker.getConfiguration();
    // timeslotDescription.addFocusListener(new SelectAllText());
    createDialog();
    reloadFields();
    setVisible(true);
  }

  private void createDialog() {
    getContentPane().setLayout(new BorderLayout());

    fieldDay = new DatetimeEditPanel(layoutManager, false, true, false, false);
    fieldWeek = new DatetimeEditPanel(layoutManager, false, true, false, false);
    fieldMonth = new DatetimeEditPanel(layoutManager, false, true, false, false);

    DialogPanel dialog = new DialogPanel(GridBagConstraints.HORIZONTAL, 0.0);
    dialog.addRow(layoutManager
        .getCoreString("taskTimePanel.configuration.dialog.chooseDay"),
        fieldDay);
    dialog.addRow(layoutManager
        .getCoreString("taskTimePanel.configuration.dialog.chooseWeek"),
        fieldWeek);
    dialog.addRow(layoutManager
        .getCoreString("taskTimePanel.configuration.dialog.chooseMonth"),
        fieldMonth);

    String description = layoutManager
        .getCoreString("taskTimePanel.configuration.dialog.description");
    JTextArea textInfo = new JTextArea(description);
    textInfo.setEditable(false);
    textInfo.setLineWrap(true);
    textInfo.setWrapStyleWord(true);
    textInfo.setBackground(getContentPane().getBackground());

    dialog.fillToEnd(textInfo);
    getContentPane().add(dialog, BorderLayout.CENTER);
    getContentPane().add(createButtons(), BorderLayout.SOUTH);

    // set some dialog properties
    setSize(500, 250);
    setResizable(true);
    setLocationRelativeTo(getRootPane());
  }

  /**
   * Gets from configuration given field and converts it to <code>>Date</code>
   * object.
   */
  private Date getField(String configName) {
    Date date = null;
    configName += includingSubtasks ? ".thisLevel" : ".includingSubtasks";
    String configValue = configuration.getString(configName, null);
    if (configValue != null) {
      try {
        date = dateFormater.parse(configValue);
      } catch (java.text.ParseException e) {
      }
    }
    return date;
  }

  private void reloadFields() {
    fieldDay.setDatetime(getField(Configuration.LAST_TIMEPANEL_DAY));
    fieldWeek.setDatetime(getField(Configuration.LAST_TIMEPANEL_WEEK));
    fieldMonth.setDatetime(getField(Configuration.LAST_TIMEPANEL_MONTH));
  }

  private JPanel createButtons() {
    FlowLayout layout = new FlowLayout(FlowLayout.CENTER);
    layout.setHgap(15);
    JPanel buttons = new JPanel(layout);
    String cancelName = null;
    cancelName = layoutManager
        .getCoreString("taskTimePanel.configuration.dialog.button.cancel");
    JButton cancelButton = new JButton(cancelName,
        layoutManager.getIcon("cancel"));
    CancelAction cancelAction = new CancelAction();
    cancelButton.addActionListener(cancelAction);
    buttons.add(cancelButton);

    // connect cancelAction with ESC key
    getRootPane().registerKeyboardAction(cancelAction,
        KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
        JComponent.WHEN_IN_FOCUSED_WINDOW);

    String defaultName = layoutManager
        .getCoreString("taskTimePanel.configuration.dialog.button.defaults");
    JButton defaultsButton = new JButton(defaultName);
    DefaultsAction defaultsAction = new DefaultsAction();
    defaultsButton.addActionListener(defaultsAction);
    buttons.add(defaultsButton);

    String saveName = layoutManager
        .getCoreString("taskTimePanel.configuration.dialog.button.save");
    JButton saveButton = new JButton(saveName, layoutManager.getIcon("save"));
    SaveAction saveAction = new SaveAction();
    saveButton.addActionListener(saveAction);
    buttons.add(saveButton);

    // set save button as a default button
    getRootPane().setDefaultButton(saveButton);
    return buttons;
  }

  /**
   * Action used when a user choose cancel/close button.
   * <p>
   * It simply closes this dialog
   * 
   * @see #getTimeslot()
   */
  private class CancelAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      dispose();
    }
  }

  /**
   * Action used when a user choose "defaults" button.
   * <p>
   * It simply clears data.
   */
  private class DefaultsAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      fieldDay.setDatetime(null);
      fieldWeek.setDatetime(null);
      fieldMonth.setDatetime(null);
    }
  }

  /**
   * Action used when a user choose save button.
   * <p>
   * It simply stores configured values into Configuration
   */
  private class SaveAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      String postfix = includingSubtasks ? ".thisLevel" : ".includingSubtasks";
      if (fieldDay.getDatetime() != null) {
        configuration.set(Configuration.LAST_TIMEPANEL_DAY + postfix,
            dateFormater.format(fieldDay.getDatetime()));
      } else {
        configuration.remove(Configuration.LAST_TIMEPANEL_DAY + postfix);
      }
      if (fieldWeek.getDatetime() != null) {
        configuration.set(Configuration.LAST_TIMEPANEL_WEEK + postfix,
            dateFormater.format(fieldWeek.getDatetime()));
      } else {
        configuration.remove(Configuration.LAST_TIMEPANEL_WEEK + postfix);
      }
      if (fieldMonth.getDatetime() != null) {
        configuration.set(Configuration.LAST_TIMEPANEL_MONTH + postfix,
            dateFormater.format(fieldMonth.getDatetime()));
      } else {
        configuration.remove(Configuration.LAST_TIMEPANEL_MONTH + postfix);
      }
      dispose();
    }
  }

}
