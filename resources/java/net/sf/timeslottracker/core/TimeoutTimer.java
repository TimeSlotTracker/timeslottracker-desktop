package net.sf.timeslottracker.core;

/**
 * An object to execute some task after given time.
 * <p>
 * It can fire an action only once, after some time, or this action can be
 * invoked every x seconds.
 * 
 * File version: $Revision: 820 $, $Date: 2009-05-16 08:53:21 +0700 (Sat, 16 May
 * 2009) $ Last change: $Author: cnitsa $
 */
public class TimeoutTimer {

  final private TimeSlotTracker timeSlotTracker;

  final private String timerName;

  final private ActionListener listener;

  final private long timeout;

  final private long count;

  /** An action which will be fired in listener when timeout occurs */
  final private Action action;

  /** a Thread object used as a timer **/
  final private Thread timer;

  /** A message used when timeout is interrupted */
  final private String interruptedMsg;

  /** A message used when timer is started **/
  final private String startedMsg;

  /** A message used when timer fires timeout action **/
  final private String firedMsg;

  /**
   * A simple constructor to create a timeout timer with the ability to run it
   * only once.
   * 
   * @param listener
   *          an ActionListener object which will wait to this timeout
   * @param timeout
   *          a timeout in seconds to wait before fire action
   */
  public TimeoutTimer(TimeSlotTracker timeSlotTracker, String timerName,
      ActionListener listener, long timeout) {
    this(timeSlotTracker, timerName, listener, timeout, 1);
  }

  /**
   * A simple constructor to create a timeout timer with the ability to run it
   * every <code>timeout</code> seconds
   * 
   * @param listener
   *          an ActionListener object which will wait to this timeout
   * @param timeout
   *          a timeout in seconds to wait before fire action
   * @param count
   *          how many times this action should be fired.<br>
   *          Specify <code>-1</code> to run it all the time, till the end of
   *          program.
   */
  public TimeoutTimer(TimeSlotTracker timeSlotTracker, String timerName,
      ActionListener listener, long timeout, long count) {
    this.timeSlotTracker = timeSlotTracker;
    this.timerName = timerName;
    this.listener = listener;
    this.timeout = timeout;
    this.count = count;

    Object args[] = { this.timerName };
    String name = timerName;
    if (count == 1) {
      name = timeSlotTracker.getString("timeoutTimer.timeoutOnce.thread.name",
          args);
      timer = new TimeoutOnce();
    } else {
      name = timeSlotTracker.getString(
          "timeoutTimer.timeoutRepeatedly.thread.name", args);
      timer = new TimeoutRepeatedly();
    }

    action = new Action(name, timer, null);
    Object[] msgArgs = { name };
    interruptedMsg = timeSlotTracker.getString(
        "timeoutTimer.InterruptedException", msgArgs);
    Object[] startedArgs = { name };
    startedMsg = timeSlotTracker.getString("timeoutTimer.started.debug",
        startedArgs);
    Object[] firedArgs = { timerName };
    firedMsg = timeSlotTracker.getString("timeoutTimer.fired.debug", firedArgs);

    timer.setName(name);
    timer.start();
  }

  public long getTimeout() {
    return timeout;
  }

  /**
   * Stops this timer.
   */
  public void stop() {
    if (timer == null) {
      return;
    }
    timer.interrupt();
  }

  /**
   * Implementing once running timer. After some time it will simply finish
   * work.
   * <p>
   * It simply fires the action after <code>timeout</code> (seconds) expired.
   */
  private class TimeoutOnce extends Thread {
    public void run() {
      timeSlotTracker.debugLog(startedMsg);
      try {
        sleep(timeout * 1000);
        timeSlotTracker.debugLog(firedMsg);
        listener.actionPerformed(action);
      } catch (InterruptedException e) {
        timeSlotTracker.debugLog(interruptedMsg);
      }
    }
  }

  /**
   * Implementation of repeatedly timeouts.
   * <p>
   * It simply repeats firing the action every <code>timeout</code> seconds.
   */
  private class TimeoutRepeatedly extends Thread {
    public void run() {
      timeSlotTracker.debugLog(startedMsg);
      try {
        long counter = 0;
        while (count == -1 || counter < count) {
          sleep(timeout * 1000);
          timeSlotTracker.debugLog(firedMsg);
          listener.actionPerformed(action);
          counter++;
        }
      } catch (InterruptedException e) {
        timeSlotTracker.debugLog(interruptedMsg);
      }
    }
  }

}
