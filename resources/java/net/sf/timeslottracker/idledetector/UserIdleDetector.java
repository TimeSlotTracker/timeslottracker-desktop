package net.sf.timeslottracker.idledetector;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.logging.Logger;

import javax.swing.Timer;

import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.core.TimeSlotTracker;
import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.utils.PlatformUtils;

public class UserIdleDetector implements ActionListener {
  public static final int DEFAULT_TIMEOUT = 30;

  private static final int MSEC_PER_MIN = 60 * 1000;
  private static final int PULL_PERIOD_MSEC = 10000;
  private static final Logger LOG = Logger.getLogger("net.sf.timeslottracker");

  private Timer timer;
  private final TimeSlotTracker timeSlotTracker;
  private boolean autopaused;

  public UserIdleDetector(TimeSlotTracker tracker) {
    timeSlotTracker = tracker;
  }

  enum OS {
    unknown, linux, windows, mac
  }

  OS os = OS.unknown;

  public boolean start() {
    if (PlatformUtils.isWindows()) {
      os = OS.windows;
    } else if (PlatformUtils.isLinux()) {
      os = OS.linux;
    } else if (PlatformUtils.isMacOsX()) {
      os = OS.mac;
    }
    if (os == OS.unknown) {
      return false;
    }
    timer = new javax.swing.Timer(PULL_PERIOD_MSEC, this);
    timer.start();
    return true;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (!timeSlotTracker.getConfiguration().getBoolean(
        Configuration.USER_IDLE_DETECTOR_ENABLED, false)) {
      return;
    }

    TimeSlot activeTimeSlot = timeSlotTracker.getActiveTimeSlot();
    if (activeTimeSlot == null) {
      // no task is timing
      autopaused = false;
      return;
    }

    final long idletime = getIdleTime();
    LOG.info("Idle time: " + idletime + " msec");

    Integer idleTimeout = timeSlotTracker.getConfiguration().getInteger(
        Configuration.USER_IDLE_DETECTOR_TIMEOUT, DEFAULT_TIMEOUT)
        * MSEC_PER_MIN;

    if (idletime > idleTimeout) {
      if (activeTimeSlot.getStartDate() != null) {
        // we're timing
        LOG.info("User inactive, pausing");
        timeSlotTracker.pauseTiming();
        // adjust stop time to last user activity (really important for
        // when computer is suspended)
        long stoptime = System.currentTimeMillis() - idletime;
        activeTimeSlot.setStopDate(new Date(stoptime));
        autopaused = true;
      }
    } else if (autopaused) {
      autopaused = false;
      LOG.info("User active, resuming timing");
      timeSlotTracker.restartTiming(activeTimeSlot.getDescription());
    }
  }

  private long getIdleTime() {
    switch (os) {
    case linux:
      return LinuxIdleTime.getIdleTimeMillis();
    case windows:
      return Win32IdleTime.getIdleTimeMillis();
    case mac:
      return MacIdleTime.getIdleTimeMillis();
    default:
      break;
    }
    return 0;
  }

}
