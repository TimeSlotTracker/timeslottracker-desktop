package net.sf.timeslottracker.gui;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Calendar;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import net.sf.timeslottracker.utils.SwingUtils;

/**
 * TimeSlot restart dialog
 * <p>
 * It contains input field for setting a new date/time for activity
 * <p>
 * 
 * @version File version: $Revision: 1118 $, $Date: 2009-08-04 19:26:06 +0700
 *          (Tue, 04 Aug 2009) $
 * @author Last change: $Author: cnitsa $
 */
@SuppressWarnings("serial")
public class TimeSlotRestartDialog extends AbstractSimplePanelDialog {

  private final AbstractAction ACTION_CANCEL = new AbstractAction() {
    public void actionPerformed(ActionEvent e) {
      startDate = null;
      close();
    }
  };

  private final AbstractAction ACTION_OK = new AbstractAction("OK") {
    public void actionPerformed(ActionEvent e) {
      close();
    }
  };

  private final LayoutManager layoutManager;

  private DatetimeEditPanel startDate;

  public TimeSlotRestartDialog(LayoutManager layoutManager)
      throws HeadlessException {
    super(layoutManager, layoutManager
        .getString("timeslots.popupmenu.restartTiming.name"));

    this.layoutManager = layoutManager;

    initActions();
  }

  private void initActions() {
    // connect cancelAction with ESC key
    getRootPane().registerKeyboardAction(ACTION_CANCEL,
        KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
        JComponent.WHEN_IN_FOCUSED_WINDOW);

    // connect okAction with Enter key
    getRootPane().registerKeyboardAction(ACTION_OK,
        KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
        JComponent.WHEN_IN_FOCUSED_WINDOW);

    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        ACTION_CANCEL.actionPerformed(new ActionEvent(this, 1, ""));
      }
    });
  }

  private void close() {
    SwingUtils.saveLocation(this);
    dispose();
  }

  protected JPanel createButtons() {
    FlowLayout layout = new FlowLayout();
    layout.setHgap(15);

    JButton saveButton = new JButton(
        layoutManager.getCoreString("timing.start.input.button.save"),
        layoutManager.getIcon("save"));
    saveButton.addActionListener(ACTION_OK);
    getRootPane().setDefaultButton(saveButton);

    JButton cancelButton = new JButton(
        layoutManager.getCoreString("timing.start.input.button.cancel"),
        layoutManager.getIcon("cancel"));
    cancelButton.addActionListener(ACTION_CANCEL);

    JPanel buttonsPanel = new JPanel(layout);
    buttonsPanel.add(cancelButton);
    buttonsPanel.add(saveButton);

    return buttonsPanel;
  }

  @Override
  protected DialogPanel getDialogPanel() {
    return new DialogPanel(GridBagConstraints.HORIZONTAL, 0.0);
  }

  @Override
  protected void fillDialogPanel(DialogPanel panel) {
    // allow user to select start time
    startDate = new DatetimeEditPanel(layoutManager, false, true, true);
    startDate.setDatetime(Calendar.getInstance().getTime());
    panel.addRow(coreString("editDialog.timeslot.start.date.name") + ":",
        startDate);

  }

  @Override
  protected void beforeShow() {
    pack();
    setResizable(true);
  }

  @Override
  protected int getDefaultHeight() {
    return (int) getSize().getHeight();
  }

  @Override
  protected int getDefaultWidth() {
    return 450;
  }

  /**
   * @return Date - user selected timeslot start date, may be null
   */
  public Date getStartDate() {
    return (startDate == null) ? null : startDate.getDatetime();
  }

}
