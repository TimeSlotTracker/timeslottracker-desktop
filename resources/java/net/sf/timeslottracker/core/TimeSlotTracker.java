package net.sf.timeslottracker.core;

import java.awt.Cursor;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import net.sf.timeslottracker.data.DataSource;
import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.systemtray.TrayIconManager;
import net.sf.timeslottracker.integrations.issuetracker.IssueTracker;
import net.sf.timeslottracker.worktime.WorkTimeService;

/**
 * An interface to main timetracker core module. It can be treated as a
 * "central point" of whole application.
 * 
 * @version File version: $Revision: 1126 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public interface TimeSlotTracker {

  /**
   * Returns actual data store, wherever it will be a XMLDataStore or some
   * DatabaseStore.
   * 
   * @returns data-store used by TimeSlotTracker or null if there is no one in
   *          use.
   */
  DataSource getDataSource();

  /**
   * Returns locale used in whole application.
   * <p>
   * Locale should be set somewhere in a configuration.
   * <p>
   * Depending of a given locale the program should use localize messages and
   * other output.
   * 
   */
  Locale getLocale();

  /**
   * Returns localized message from a property file.
   * 
   * @return localized message or null if message does not exists.
   */
  String getString(String key);

  /**
   * Returns localized message from a property file. Additionally, it formats
   * this message with arguments given as a second param.
   * <p>
   * Formating is done using a java.text.MessageFormat class.
   * 
   * @param key
   *          key for string to find it in a property file
   * @param args
   *          a object array with arguments to properly format found string
   * 
   * @return localized message or null if message does not exists.
   */
  String getString(final String key, final Object[] args);

  /**
   * Returns object which implements the Layout
   */
  LayoutManager getLayoutManager();

  /**
   * Logs debug message.
   * 
   * @param message
   *          a message to log
   */
  void debugLog(String message);

  /**
   * Logs error message.
   * 
   * @param message
   *          a message to error log
   */
  void errorLog(String message);

  /**
   * Logs an exception.
   * 
   * @param exception
   *          an exception to log
   */
  void errorLog(Exception exception);

  /**
   * Returns an Icon object got from the same class loader so the app is
   * (probably jar file).
   * 
   * @param iconPath
   *          path to icon, for example: "play"
   */
  ImageIcon getIcon(String iconPath);

  /**
   * Returns configuration object used in application.
   */
  Configuration getConfiguration();

  /**
   * Standard, default, "normal" cursor
   */
  Cursor CURSOR_DEFAULT = Cursor.getDefaultCursor();

  /**
   * Standard "wait" cursor to sygnalize that application is something doing
   * now.
   */
  Cursor CURSOR_WAIT = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);

  /**
   * Sets cursor to a predefined one.
   * <p>
   * You can choose from CURSOR_WAIT and CURSOR_DEFAULT
   */
  void setCursor(Cursor cursor);

  /**
   * Sets cursor to "wait" one.
   * 
   * @see #setCursor(java.awt.Cursor)
   * @see #CURSOR_WAIT
   */
  void setCursorWait();

  /**
   * Sets cursor to "default" one.
   * 
   * @see #setCursor(java.awt.Cursor)
   * @see #CURSOR_DEFAULT
   */
  void setCursorDefault();

  /**
   * Sets active TimeSlot.
   * <p>
   * According to our specification only ONE task can be active.
   * 
   * @see TimeSlot#getTask()
   */
  void setActiveTimeSlot(TimeSlot timeslot);

  /**
   * Gets active time slot
   * 
   * @return null if there is no active task or Task object - an active one
   * 
   * @see #setActiveTimeSlot(TimeSlot)
   * @see TimeSlot#getTask()
   */
  TimeSlot getActiveTimeSlot();

  /**
   * Starts a new timeslot in actually selected task in TasksInterface (on a
   * tree).
   * <p>
   * It creates a new TimeSlot with a current start date and description took
   * from an input dialog. Then it adds this timeslot to task and refresh the
   * timeslots table.<br>
   * At the end it sets this timeslot as an active one.
   * 
   * @return true - timing starting, false - canceled
   * 
   * @see #setActiveTimeSlot(TimeSlot)
   */
  boolean startTiming();

  /**
   * Starts a new timeslot in actually selected task in TasksInterface (on a
   * tree).
   * <p>
   * It creates a new TimeSlot with a current start date and given description.
   * Then it adds this timeslot to task and refresh the timeslots table.<br>
   * At the end it sets this timeslot as an active one.
   * 
   * @param description
   *          a new description a task should have.<br>
   *          If <code>null</code> or <code>length()==0</code> an input dialog
   *          will be shown to enter description.
   * 
   * @return true - timing starting, false - canceled
   * 
   * @see #setActiveTimeSlot(TimeSlot)
   */
  boolean startTiming(String description);

  boolean restartTiming(String description);

  /**
   * Pause timing active task (but remember - it's not selected task it's the
   * one is actually timing).
   * <p>
   * It stops current timeslot and then starts a new one, but with an empty
   * start and stop dates. It copies a description from the old actual one and
   * sets it into a new timeslot.<br>
   * At the end it adds this timeslot to task and refresh the timeslots table.
   * 
   * @see #getActiveTimeSlot()
   */
  void pauseTiming();

  /**
   * Stops timing the active (not neccessary selected in a TasksInterface - tree
   * module.
   * <p>
   * It stops current timeslot and then refresh the timeslots table.
   * 
   * @see #getActiveTimeSlot()
   */
  void stopTiming();

  /**
   * adds a specific action Listener.
   * 
   * @see net.sf.timeslottracker.core.ActionListener
   * @see net.sf.timeslottracker.data.DataLoadedListener
   */
  void addActionListener(ActionListener listener);

  /**
   * adds an action Listener to default queue on given action code.
   * 
   * @param listener
   *          a listener to add
   * @param selector
   *          a actionCode to select on which event we want to listen to.
   * 
   * @see net.sf.timeslottracker.core.Action
   */
  void addActionListener(ActionListener listener, String selector);

  /**
   * Fires an event that data was loaded.
   * 
   * @see net.sf.timeslottracker.data.DataLoadedListener
   */
  void fireDataLoaded();

  /**
   * Fires an event that a task was changed. (It's data or status)
   * 
   * @param changedTask
   *          a task that has changed.
   */
  void fireTaskChanged(Task changedTask);

  /**
   * Fires an event to default (main) listeners.
   * 
   * @param actionCode
   *          code to construct Action object.
   * 
   * @see #fireAction(Action)
   * @see net.sf.timeslottracker.core.Action
   */
  void fireAction(String actionCode);

  /**
   * Fires an event to default (main) listeners.
   * <p>
   * Event is sent only to those listeners which are listening to specific
   * selector (equal to <code>action.getName()</code>)
   * 
   * @param action
   *          to send to listeners.
   */
  void fireAction(Action action);

  /**
   * @return root frame of program
   */
  JFrame getRootFrame();

  /**
   * Saves data and closes application.
   */
  void quit();

  /**
   * @return issue tracker service (JIRA (R) for now)
   */
  IssueTracker getIssueTracker();

  /**
   * @return work time planning service
   */
  WorkTimeService getWorkTimeService();

  /**
   * @return tray icon service
   */
  TrayIconManager getTrayIconService();

  /**
   * Indicates that the application is closing by shutdown hook, so you cannot
   * display any messages.
   * 
   * @param closing
   *          set to <code>true</code> to indicate that application is closing
   *          now.
   */
  void setClosing(boolean closing);

  /**
   * Returns information if application is closing now. In this state you
   * shouldn't show any messages to user.
   * 
   * @return <code>true</code> if the application is closing now.
   */
  boolean isClosing();

}
