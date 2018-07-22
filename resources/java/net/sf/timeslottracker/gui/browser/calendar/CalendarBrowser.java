package net.sf.timeslottracker.gui.browser.calendar;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JDialog;

import net.sf.timeslottracker.core.Action;
import net.sf.timeslottracker.core.ActionListener;
import net.sf.timeslottracker.core.TimeSlotTracker;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.browser.calendar.core.CalendarEventDispatcher;
import net.sf.timeslottracker.gui.browser.calendar.core.DayActionListener;
import net.sf.timeslottracker.gui.browser.calendar.core.MonthActionListener;
import net.sf.timeslottracker.gui.browser.calendar.core.YearActionListener;

/**
 * Select date from calendar dialog.
 * 
 * File version: $Revision: 998 $, $Date: 2009-05-16 09:00:38 +0700 (Sat, 16 May
 * 2009) $ Last change: $Author: cnitsa $
 */
public class CalendarBrowser extends JDialog implements CalendarEventDispatcher {
  private Collection monthChangedListeners = new Vector();
  private Collection yearChangedListeners = new Vector();
  private Collection dayChangedListeners = new Vector();

  private LayoutManager layoutManager;
  private TimeSlotTracker timeSlotTracker;
  private Calendar day;
  private boolean cancel = true;

  private TablePanel table;

  public CalendarBrowser(LayoutManager layoutManager, Calendar day, Dialog owner) {
    super(owner, layoutManager
        .getCoreString("editDialog.calendarBrowser.title"), true);
    this.layoutManager = layoutManager;
    this.timeSlotTracker = layoutManager.getTimeSlotTracker();
    this.day = new GregorianCalendar(timeSlotTracker.getLocale());
    if (day != null) {
      this.day.setTime(day.getTime());
    }
    createDialog();
    setVisible(true);
  }

  /**
   * Creates sub-panels and put it in selected area.
   */
  private void createDialog() {
    table = new TablePanel(layoutManager, this, day);
    ControlPanel control = new ControlPanel(layoutManager, table, this, day);
    MonthYearPanel tool = new MonthYearPanel(layoutManager, this, day);
    Container container = getContentPane();

    container.setLayout(new BorderLayout());
    container.add(tool, BorderLayout.NORTH);
    container.add(table, BorderLayout.CENTER);
    container.add(control, BorderLayout.SOUTH);

    pack();
    setResizable(false);
    setLocationRelativeTo(getRootPane());
    table.requestFocus();
    table.goSelectedDay();
  }

  /**
   * Get selected day.
   */
  public Calendar getSelectedDay() {
    if (cancel) {
      return null;
    }
    return table.getSelectedDay();
  }

  public void addActionListener(ActionListener listener) {
    if (listener instanceof YearActionListener) {
      yearChangedListeners.add(listener);
    } else if (listener instanceof MonthActionListener) {
      monthChangedListeners.add(listener);
    } else if (listener instanceof DayActionListener) {
      dayChangedListeners.add(listener);
    }
  }

  public void fireYearChange(Calendar year) {
    Action action = new Action("yearChangeAction", year, null);
    // timeSlotTracker.debugLog(action.getName() + "("+year+")");
    Iterator listeners = yearChangedListeners.iterator();
    while (listeners.hasNext()) {
      ActionListener listener = (ActionListener) listeners.next();
      listener.actionPerformed(action);
    }
  }

  public void fireMonthChange(Calendar month) {
    Action action = new Action("monthChangeAction", month, null);
    // timeSlotTracker.debugLog(action.getName() + "("+month+")");
    Iterator listeners = monthChangedListeners.iterator();
    while (listeners.hasNext()) {
      ActionListener listener = (ActionListener) listeners.next();
      listener.actionPerformed(action);
    }
  }

  public void fireDayChange(Calendar day) {
    Action action = new Action("dayChangeAction", day, null);
    // timeSlotTracker.debugLog(action.getName() + "("+day+")");
    Iterator listeners = dayChangedListeners.iterator();
    while (listeners.hasNext()) {
      ActionListener listener = (ActionListener) listeners.next();
      listener.actionPerformed(action);
    }
  }

  public void fireCancelAction() {
    dispose();
  }

  public void fireHomeAction() {
    // timeSlotTracker.debugLog(" CalendraBrower.goHome action");
    table.goHome();
  }

  public void fireSelectAction(Calendar day) {
    timeSlotTracker.debugLog(" CalendraBrower.select action (" + day + ")");
    this.day = day;
    cancel = false;
    dispose();
  }
}
