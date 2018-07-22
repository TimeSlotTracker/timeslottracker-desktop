package net.sf.timeslottracker.gui;

import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sf.timeslottracker.gui.browser.calendar.CalendarBrowser;
import net.sf.timeslottracker.utils.SwingUtils;
import net.sf.timeslottracker.utils.TimeUtils;

/**
 * A simple panel with controls to edit date and time.
 * <p>
 * It is composed of several fields. One field for year, one for month, etc.
 * <p>
 * It is provided to make a developer life easier and to give him an object to
 * easy manipulate with it
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-07-31 17:41:54 +0700
 *          (Fri, 31 Jul 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class DatetimeEditPanel extends JPanel {
  private static final String SET_CURRENT_DATETIME = "setCurrentDatetime";

  private static final String SET_DATETIME = "setDatetime";

  private LayoutManager layoutManager;

  private boolean readonly;

  private boolean showDate;

  private boolean showTime;

  private JTextField yearField = new JTextField(4);

  private JTextField monthField = new JTextField(2);

  private JTextField dayField = new JTextField(2);

  private JTextField hourField = new JTextField(2);

  private JTextField minField = new JTextField(2);

  private JLabel dateSeparator1 = new JLabel("-");

  private JLabel dateSeparator2 = new JLabel("-");

  private JLabel timeSeparator = new JLabel(":");

  private JButton setNow = new JButton();

  private JButton setDate = new JButton();

  private Calendar calendar;

  private Locale locale;

  private FocusListener selectAllText;

  /**
   * Constructs a new Datetime panel.
   * 
   * @param layoutManager
   *          a reference to LayoutManager class object, the one used to manage
   *          our layout (standard ClassicLayout).
   * @param readonly
   *          <code>true</code> if fields should be disabled
   * @param showDate
   *          <code>true</code> if controls for year/month/day should be visible
   *          (i.e. you want to set date).
   * @param showTime
   *          <code>true</code> if controls for hour/minutes should be visible
   *          (i.e. you want to set time)
   */
  public DatetimeEditPanel(LayoutManager layoutManager, boolean readonly,
      boolean showDate, boolean showTime, boolean showNowButton) {
    super(new FlowLayout(FlowLayout.LEFT, 1, 0));
    this.layoutManager = layoutManager;
    this.readonly = readonly;
    this.showDate = showDate;
    this.showTime = showTime;
    locale = layoutManager.getTimeSlotTracker().getLocale();
    calendar = Calendar.getInstance(locale);
    selectAllText = new SelectAllOnFocusListener();
    constructPanel(showNowButton);
  }

  /**
   * Constructs a new Datetime panel with shown button "now".
   * 
   * @see #DatetimeEditPanel(LayoutManager,boolean,boolean,boolean,boolean)
   **/
  public DatetimeEditPanel(LayoutManager layoutManager, boolean readonly,
      boolean showDate, boolean showTime) {
    this(layoutManager, readonly, showDate, showTime, true);
  }

  public void setReadOnly(boolean value) {
    yearField.setEnabled(!value);
    monthField.setEnabled(!value);
    dayField.setEnabled(!value);
    hourField.setEnabled(!value);
    minField.setEnabled(!value);
    setNow.setEnabled(!value);
    setDate.setEnabled(!value);
  }

  private void setToolTipText(JComponent component, String keyString) {
    String toolTip = layoutManager.getCoreString(keyString);
    component.setToolTipText(toolTip);
  }

  private void constructPanel(boolean showNowButton) {
    yearField.setHorizontalAlignment(JTextField.RIGHT);
    yearField.addFocusListener(selectAllText);
    setToolTipText(yearField, "editDialog.datetimeEditPanel.field.year.tooltip");
    monthField.setHorizontalAlignment(JTextField.RIGHT);
    monthField.addFocusListener(selectAllText);
    setToolTipText(monthField,
        "editDialog.datetimeEditPanel.field.month.tooltip");
    dayField.setHorizontalAlignment(JTextField.RIGHT);
    dayField.addFocusListener(selectAllText);
    setToolTipText(dayField, "editDialog.datetimeEditPanel.field.day.tooltip");
    hourField.setHorizontalAlignment(JTextField.RIGHT);
    hourField.addFocusListener(selectAllText);
    setToolTipText(hourField, "editDialog.datetimeEditPanel.field.hour.tooltip");
    minField.setHorizontalAlignment(JTextField.RIGHT);
    minField.addFocusListener(selectAllText);
    setToolTipText(minField,
        "editDialog.datetimeEditPanel.field.minute.tooltip");
    if (showDate) {
      add(yearField);
      add(dateSeparator1);
      add(monthField);
      add(dateSeparator2);
      add(dayField);

      setDate.setBorderPainted(false);
      // setDate.setFocusPainted(false);
      setDate.setMargin(new java.awt.Insets(0, 0, 0, 0));
      setDate.setIcon(layoutManager.getIcon("calendar"));
      setDate.setActionCommand(SET_DATETIME);
      setDate.addActionListener(new SetDatetimeAction());
      add(setDate);
    }
    if (showDate && showTime) {
      add(new JLabel("      "));
    }
    if (showTime) {
      add(hourField);
      add(timeSeparator);
      add(minField);
    }

    if (showNowButton) {
      add(new JLabel("      "));
      setNow.setText(layoutManager
          .getCoreString("editDialog.datetimeEditPanel.button.now"));
      setToolTipText(setNow, "editDialog.datetimeEditPanel.button.now.tooltip");
      setNow.setActionCommand(SET_CURRENT_DATETIME);
      setNow.addActionListener(new SetDatetimeAction());
      add(setNow);
    }
  }

  /**
   * Sets date and/or time in panel controls
   * 
   * @param datetime
   *          null if you want to clear all fields or a java.util.Date object to
   *          set date and/or time fields
   * @see #showDate
   * @see #showTime
   */
  public void setDatetime(Date datetime) {
    if (datetime == null) {
      yearField.setText("");
      monthField.setText("");
      dayField.setText("");
      hourField.setText("");
      minField.setText("");
      return;
    }
    calendar.setTime(datetime);
    yearField.setText("" + calendar.get(Calendar.YEAR));
    monthField.setText("" + (calendar.get(Calendar.MONTH) + 1));
    dayField.setText("" + calendar.get(Calendar.DATE));
    hourField.setText("" + calendar.get(Calendar.HOUR_OF_DAY));
    minField.setText("" + calendar.get(Calendar.MINUTE));
  }

  /**
   * Returns a date and/or time in Date object according to user input
   */
  public Date getDatetime() throws NumberFormatException {
    String year = yearField.getText();
    String month = monthField.getText();
    String day = dayField.getText();
    String hour = hourField.getText();
    String min = minField.getText();
    if ((year == null || year.length() == 0)
        && (month == null || month.length() == 0)
        && (day == null || day.length() == 0)
        && (hour == null || hour.length() == 0)
        && (min == null || min.length() == 0)) {
      return null;
    }
    calendar.set(Calendar.SECOND, 0);
    if (showDate) {
      calendar.set(Calendar.YEAR, Integer.parseInt(year));
      calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);
      calendar.set(Calendar.DATE, Integer.parseInt(day));
    }
    if (showTime) {
      calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
      calendar.set(Calendar.MINUTE, Integer.parseInt(min));
    } else {
      calendar.set(Calendar.HOUR_OF_DAY, 0);
      calendar.set(Calendar.MINUTE, 0);
    }
    return calendar.getTime();
  }

  private class SetDatetimeAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      if (SET_CURRENT_DATETIME.equals(e.getActionCommand())) {
        Date date = TimeUtils.roundDate(new Date());
        setDatetime(date);
      } else if (SET_DATETIME.equals(e.getActionCommand())) {
        Calendar day = Calendar.getInstance();
        if (yearField.getText() != null && monthField.getText() != null
            && dayField.getText() != null && yearField.getText().length() > 0
            && monthField.getText().length() > 0
            && dayField.getText().length() > 0) {
          day.set(Integer.parseInt(yearField.getText()),
              Integer.parseInt(monthField.getText()) - 1,
              Integer.parseInt(dayField.getText()));
        }
        CalendarBrowser calendar = new CalendarBrowser(layoutManager, day,
            (Dialog) SwingUtils.getParent(DatetimeEditPanel.this));
        Calendar selectedDay = calendar.getSelectedDay();
        System.out.println("**++**: SetDatetimeAction, selectedDay = "
            + selectedDay);
        if ((selectedDay != null)
            && (day.get(Calendar.YEAR) != selectedDay.get(Calendar.YEAR) || day
                .get(Calendar.DAY_OF_YEAR) != selectedDay
                .get(Calendar.DAY_OF_YEAR))) {
          setDatetime(selectedDay.getTime());
        }
      }
    }
  }

}
