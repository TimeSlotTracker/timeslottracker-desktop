package net.sf.timeslottracker.gui.browser.calendar.core;

import java.util.Calendar;

import net.sf.timeslottracker.core.ActionListener;

/**
 * An interface to CalendarBrowser event dispatcher.
 * 
 */

public interface CalendarEventDispatcher {

  /**
   * Register listener.
   */
  public void addActionListener(ActionListener listener);

  /**
   * Broadcast info about month change.
   */
  public void fireMonthChange(Calendar month);

  /**
   * Broadcast info about year change.
   */
  public void fireYearChange(Calendar year);

  /**
   * Broadcast info about day change.
   */
  public void fireDayChange(Calendar day);

  /**
   * Broadcast info about cancel action.
   */
  public void fireCancelAction();

  /**
   * Broadcast info about go home action..
   */
  public void fireHomeAction();

  /**
   * Broadcast info about select action.
   */
  public void fireSelectAction(Calendar day);
}
