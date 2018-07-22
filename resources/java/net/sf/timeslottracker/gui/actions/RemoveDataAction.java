package net.sf.timeslottracker.gui.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;

import net.sf.timeslottracker.data.DataSource;
import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.filters.TimeSlotFilter;
import net.sf.timeslottracker.filters.TimeSlotIncludedInPeriod;
import net.sf.timeslottracker.gui.LayoutManager;

/**
 * Remove data gui action
 * 
 * @version File version: $Revision: 1.23 $, $Date: 2007/04/07 03:59:30 $
 * @author Last change: $Author: cnitsa $
 */
public class RemoveDataAction extends AbstractAction {
  private static final Date START = new Date(0);
  private final LayoutManager layoutManager;

  // flag: at least one timeslot is deleted
  private boolean deleted;

  public RemoveDataAction(LayoutManager layoutManager) {
    super(layoutManager.getCoreString("remove.data.action.name") + " ...",
        layoutManager.getIcon("erase"));
    this.layoutManager = layoutManager;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    DataSource dataSource = layoutManager.getTimeSlotTracker().getDataSource();
    final Task root = dataSource.getRoot();

    RemoveDataDialog dialog = new RemoveDataDialog(layoutManager,
        new RemoveDataDialog.Action() {
          @Override
          public void perform(Date removeBeforeDate) {
            TimeSlotIncludedInPeriod filter = new TimeSlotIncludedInPeriod(
                layoutManager.getTimeSlotTracker(), START, removeBeforeDate);

            removeTask(root, filter);

            if (deleted) {
              // refreshing all open panels. updating using event too slow
              layoutManager.getTaskInfoInterface().refresh();
              layoutManager.getTimeSlotsInterface().refresh();
              // saving data store
              layoutManager.getTimeSlotTracker().getDataSource().saveAll(true);
            }
          }
        });
    dialog.activate();
  }

  private void removeTask(Task parent, TimeSlotFilter filter) {
    removeTimeslots(parent, filter);

    Collection<Task> children = parent.getChildren();
    if (children == null || children.isEmpty()) {
      return;
    }
    for (Task task : children) {
      removeTask(task, filter);
    }
  }

  private void removeTimeslots(final Task task, TimeSlotFilter filter) {
    for (TimeSlot timeSlot : (List<TimeSlot>) new ArrayList(task.getTimeslots())) {
      if (filter.accept(timeSlot)) {
        // reset active timeslot before deleting active timeslot
        if (timeSlot.equals(layoutManager.getTimeSlotTracker()
            .getActiveTimeSlot())) {
          layoutManager.getTimeSlotTracker().setActiveTimeSlot(null);
        }

        Task selectedTask = timeSlot.getTask();
        selectedTask.deleteTimeslot(timeSlot);
        deleted = true;
      }
    }
  }

}
