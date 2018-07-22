package net.sf.timeslottracker.gui.browser.calendar;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.timeslottracker.core.Action;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.browser.calendar.core.CalendarEventDispatcher;
import net.sf.timeslottracker.gui.browser.calendar.core.DayActionListener;
import net.sf.timeslottracker.gui.browser.calendar.core.MonthActionListener;
import net.sf.timeslottracker.gui.browser.calendar.core.YearActionListener;

/**
 * Sub-panel with controls: button: to goto previous date - go Home, text
 * box/label: with selected date. button: ok/select button: cancel
 * 
 */

class ControlPanel extends JPanel {
  private LayoutManager layoutManager;
  private CalendarEventDispatcher eventDispatcher;
  private TablePanel table;

  private JButton goHome;
  private JLabel currentDay;
  private JButton cancel;

  public ControlPanel(LayoutManager layoutManager, TablePanel table,
      CalendarEventDispatcher eventDispatcher, Calendar day) {
    super(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    this.layoutManager = layoutManager;
    this.table = table;
    this.eventDispatcher = eventDispatcher;

    createPanel();
    setDay(day);
  }

  /**
   * Sets the date based on the TablePanel selected day.
   */
  private void setDay() {
    setDay(table.getSelectedDay());
  }

  /**
   * Sets date from a given day to label.
   */
  public void setDay(Calendar day) {
    String year = "" + day.get(Calendar.YEAR);
    String month = "0" + (day.get(Calendar.MONTH) + 1);
    String dayOfM = "0" + day.get(Calendar.DAY_OF_MONTH);

    month = month.length() == 3 ? month.substring(1) : month;
    dayOfM = dayOfM.length() == 3 ? dayOfM.substring(1) : dayOfM;

    currentDay.setText(year + "-" + month + "-" + dayOfM);
  }

  /**
   * Do all to prepare sub-panel.
   */
  private void createPanel() {
    currentDay = new JLabel("", JLabel.CENTER);
    add(currentDay);
    // add(new JLabel("  "));

    goHome = new JButton();
    goHome.setIcon(layoutManager.getIcon("home"));
    goHome.setMargin(new java.awt.Insets(0, 0, 0, 0));
    goHome.addActionListener(new GoHomeAction());
    add(goHome);
    // add(new JLabel("  "));

    cancel = new JButton(
        layoutManager.getCoreString("editDialog.calendarBrowser.button.cancel"),
        layoutManager.getIcon("cancel"));
    cancel.addActionListener(new CancelAction());
    add(cancel);

    eventDispatcher.addActionListener(new DayChangedAction());
    eventDispatcher.addActionListener(new MonthChangedAction());
    eventDispatcher.addActionListener(new YearChangedAction());
  }

  /**
   * Action used when a user choose cancel button.
   */
  private class CancelAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      eventDispatcher.fireCancelAction();
    }
  }

  /**
   * Action used when a user choose home button.
   */
  private class GoHomeAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      eventDispatcher.fireHomeAction();
    }
  }

  /**
   * Action used when a user change a day.
   */
  private class DayChangedAction implements DayActionListener {
    public void actionPerformed(Action event) {
      setDay();
    }
  }

  /**
   * Action used when a user change a month.
   */
  private class MonthChangedAction implements MonthActionListener {
    public void actionPerformed(Action event) {
      setDay();
    }
  }

  /**
   * Action used when a user change a year.
   */
  private class YearChangedAction implements YearActionListener {
    public void actionPerformed(Action event) {
      setDay();
    }
  }

}
