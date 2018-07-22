package net.sf.timeslottracker.gui.browser.calendar;

import java.awt.FlowLayout;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.timeslottracker.core.Action;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.browser.calendar.core.CalendarEventDispatcher;
import net.sf.timeslottracker.gui.browser.calendar.core.DayActionListener;

/**
 * Simply sub-panel with two controls: spinner for month spinner for year
 * 
 * File version: $Revision: 998 $, $Date: 2009-05-16 09:00:38 +0700 (Sat, 16 May
 * 2009) $ Last change: $Author: cnitsa $
 */

class MonthYearPanel extends JPanel {
  private LayoutManager layoutManager;
  private Calendar day;
  private CalendarEventDispatcher calendarEventDispatcher;
  private JSpinner year;
  private JSpinner month;
  /**
   * Tells if change listeners of month and year are enabled. They should be
   * disabled when date was set via DayChangedAction.
   */
  private boolean selectingEventEnabled = true;

  public MonthYearPanel(LayoutManager layoutManager,
      CalendarEventDispatcher calendarEventDispatcher, Calendar day) {
    super(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    this.layoutManager = layoutManager;
    this.calendarEventDispatcher = calendarEventDispatcher;
    this.day = day;
    calendarEventDispatcher.addActionListener(new DayChangedAction());

    createPanel();
  }

  /**
   * Create all controls for sub-panel
   */
  private void createPanel() {
    SpinnerListModel monthModel = new SpinnerListModel(getMonthNames());
    List monthStrings = monthModel.getList();
    monthModel.setValue(monthStrings.get(day.get(Calendar.MONTH)));

    JLabel spinnerName = new JLabel(
        layoutManager.getCoreString("editDialog.calendarBrowser.label.month"));
    add(spinnerName);

    month = new JSpinner(monthModel);
    month.addChangeListener(new MonthChange());
    spinnerName.setLabelFor(month);
    add(month);
    add(new JLabel("  "));

    JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) month.getEditor();
    JFormattedTextField ftf = editor.getTextField();
    if (ftf != null) {
      ftf.setColumns(7);
      ftf.setHorizontalAlignment(JTextField.CENTER);
    }

    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
    SpinnerModel yearModel = new SpinnerNumberModel(currentYear, // initial
                                                                 // value
        currentYear - 100, // min
        currentYear + 100, // max
        1);

    spinnerName = new JLabel(
        layoutManager.getCoreString("editDialog.calendarBrowser.label.year"));
    add(spinnerName);

    year = new JSpinner(yearModel);
    JSpinner.NumberEditor yearEditor = new JSpinner.NumberEditor(year, "0000");
    year.setEditor(yearEditor);
    year.addChangeListener(new YearChange());
    spinnerName.setLabelFor(year);
    add(year);
  }

  /**
   * Returns array of string with months name.
   */
  private String[] getMonthNames() {
    Locale locale = layoutManager.getTimeSlotTracker().getLocale();
    String[] months = new DateFormatSymbols(locale).getMonths();
    int lastIndex = months.length - 1;

    if (months[lastIndex] == null || months[lastIndex].length() <= 0) {
      String[] monthStrings = new String[lastIndex];
      System.arraycopy(months, 0, monthStrings, 0, lastIndex);
      return monthStrings;
    }
    return months;
  }

  private class MonthChange implements ChangeListener {
    public void stateChanged(ChangeEvent e) {
      JSpinner monthSpinner = (JSpinner) e.getSource();
      SpinnerListModel monthModel = (SpinnerListModel) monthSpinner.getModel();
      List monthStrings = monthModel.getList();
      Iterator iterator = monthStrings.iterator();
      String monthName = (String) monthSpinner.getValue();
      int index = 0;
      while (iterator.hasNext() && !monthName.equals((String) iterator.next())) {
        index++;
      }
      Calendar month = Calendar.getInstance();
      month.set(Calendar.MONTH, index);
      if (selectingEventEnabled) {
        calendarEventDispatcher.fireMonthChange(month);
      }
    }
  }

  private class YearChange implements ChangeListener {
    public void stateChanged(ChangeEvent e) {
      JSpinner yearSpinner = (JSpinner) e.getSource();
      Calendar year = Calendar.getInstance();
      Integer value = (Integer) yearSpinner.getValue();
      year.set(Calendar.YEAR, value.intValue());
      if (selectingEventEnabled) {
        calendarEventDispatcher.fireYearChange(year);
      }
    }
  }

  private class DayChangedAction implements DayActionListener {
    public void actionPerformed(Action event) {
      Calendar day = (Calendar) event.getSource();

      SpinnerListModel monthModel = (SpinnerListModel) month.getModel();
      List monthStrings = monthModel.getList();
      Iterator iterator = monthStrings.iterator();
      for (int index = 0; index < day.get(Calendar.MONTH); index++) {
        iterator.next();
      }
      String monthName = (String) iterator.next();

      selectingEventEnabled = false;
      month.setValue(monthName);
      year.setValue(new Integer(day.get(Calendar.YEAR)));
      selectingEventEnabled = true;
    }
  }
}
