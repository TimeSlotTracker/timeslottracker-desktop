package net.sf.timeslottracker.data;

import net.sf.timeslottracker.core.Action;
import net.sf.timeslottracker.core.ActionListener;
import net.sf.timeslottracker.core.TimeSlotTracker;

/**
 * A class which calls DataSource.saveAll() method to save all data.
 * 
 * File version: $Revision: 1037 $, $Date: 2009-05-16 08:53:21 +0700 (Sat, 16 May
 * 2009) $ Last change: $Author: cnitsa $
 */
public class AutoSaveTask implements ActionListener {

  private final TimeSlotTracker timeSlotTracker;

  public AutoSaveTask(TimeSlotTracker timeSlotTracker) {
    this.timeSlotTracker = timeSlotTracker;
  }

  public void actionPerformed(Action action) {
    DataSource dataSource = timeSlotTracker.getDataSource();
    if (dataSource != null) {
      dataSource.saveAll();
    }
  }
}
