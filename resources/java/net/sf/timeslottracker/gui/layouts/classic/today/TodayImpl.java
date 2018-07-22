package net.sf.timeslottracker.gui.layouts.classic.today;

import java.util.ArrayList;
import java.util.Date;

import net.sf.timeslottracker.core.Action;
import net.sf.timeslottracker.data.DataSource;
import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TaskChangedListener;
import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.data.TimeSlotChangedListener;
import net.sf.timeslottracker.filters.FilterUtils;
import net.sf.timeslottracker.filters.TimeSlotStartedInPeriod;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.TodayInterface;
import net.sf.timeslottracker.gui.taskmodel.TaskModel;
import net.sf.timeslottracker.gui.taskmodel.TaskModelFactory;
import net.sf.timeslottracker.gui.taskmodel.TaskValue;
import net.sf.timeslottracker.utils.TaskIterator;

/**
 * Today implementation for classic layout.
 * <p>
 * MVC pattern used: all models created with initial data, updates via common
 * dialogs (see Starter class, ...), then models updates occurred and so on.
 * 
 * @author glazachev
 */
public class TodayImpl implements TodayInterface {

  private final TodayTableController dailyTableController;

  private final TodayTableModel dailyTableModel;

  private final TaskModel dailyTaskModel;

  private final LayoutManager layoutManager;

  public TodayImpl(LayoutManager layoutManager) {
    this.layoutManager = layoutManager;
    this.dailyTableController = createTableController();
    this.dailyTableModel = createTableModel();
    this.dailyTaskModel = createTaskModel();

    // updates when timeslot changed
    layoutManager.addActionListener(new TimeSlotChangedListener() {
      public void actionPerformed(Action action) {
        TimeSlot timeSlot = (TimeSlot) action.getParam();
        if (getFilter().accept(timeSlot)) {
          dailyTableModel.update(createValue(timeSlot));
        } else {
          dailyTableModel.remove(createValue(timeSlot));
        }
      }
    });

    // updates when task changed
    layoutManager.getTimeSlotTracker().addActionListener(
        new TaskChangedListener() {
          public void actionPerformed(Action action) {
            Task task = (Task) action.getParam();
            dailyTaskModel.update(new TaskValue(task));

            for (TimeSlot timeSlot : FilterUtils.filter(task.getTimeslots(),
                getFilter())) {
              dailyTableModel.update(createValue(timeSlot));
            }
          }
        });
  }

  public void show() {
    new TodayDialog(layoutManager, dailyTableController, dailyTableModel,
        dailyTaskModel).activate();
  }

  private TodayTableController createTableController() {
    return new TodayTableController() {
      @Override
      public boolean start() {
        return layoutManager.getTimeSlotTracker().startTiming();
      }

      @Override
      public boolean start(TimeSlotValue timeSlotValue) {
        Object taskId = timeSlotValue.getTaskId();
        Task task = layoutManager.getTimeSlotTracker().getDataSource()
            .getTask(taskId);
        if (task != null) {
          layoutManager.getTasksInterface().selectTask(task);
        }

        return layoutManager.getTimeSlotTracker().startTiming(
            timeSlotValue.getDescription());
      }

      @Override
      public void stop() {
        layoutManager.getTimeSlotTracker().stopTiming();
      }

      @Override
      public void update(TimeSlotValue value) {
        DataSource dataSource = getDataSource();

        // updating fields
        Task oldTask = dataSource.getTask(value.getTaskId());
        TimeSlot timeSlot = oldTask.getTimeSlot(value.getTimeSlotId());
        timeSlot.setDescription(value.getDescription());
        timeSlot.setStartDate(value.getStart());
        timeSlot.setStopDate(value.getStop());
        if (timeSlot.getStopDate() != null) {
          TimeSlot activeTimeSlot = layoutManager.getTimeSlotTracker()
              .getActiveTimeSlot();
          if (activeTimeSlot != null && activeTimeSlot == timeSlot) {
            layoutManager.getTimeSlotTracker().setActiveTimeSlot(null);
          }
        }

        // updating task
        Task currentTask = dataSource.getTask(value.getTaskValue().getId());
        if (!oldTask.getId().equals(currentTask.getId())) {
          oldTask.deleteTimeslot(timeSlot);
          currentTask.addTimeslot(timeSlot);
          layoutManager.getTimeSlotsInterface().refresh();
        }

        layoutManager.fireTimeSlotChanged(timeSlot);
      }
    };
  }

  private TodayTableModel createTableModel() {
    TimeSlotStartedInPeriod timeSlotByDateFilter = getFilter();

    ArrayList<TimeSlot> timeslots = new ArrayList<TimeSlot>();

    TaskIterator taskIterator = new TaskIterator(getDataSource().getRoot());
    while (taskIterator.hasNext()) {
      Task task = taskIterator.next();
      timeslots.addAll(FilterUtils.filter(task.getTimeslots(),
          timeSlotByDateFilter));
    }

    ArrayList<TimeSlotValue> timeSlotValues = new ArrayList<TimeSlotValue>();
    for (TimeSlot timeSlot : timeslots) {
      timeSlotValues.add(createValue(timeSlot));
    }

    return new TodayTableModel(timeSlotValues, dailyTableController,
        layoutManager);
  }

  private TimeSlotStartedInPeriod getFilter() {
    return new TimeSlotStartedInPeriod(new Date());
  }

  private TaskModel createTaskModel() {
    return new TaskModelFactory(layoutManager).createTaskModel();
  }

  private TimeSlotValue createValue(TimeSlot timeSlot) {
    Task task = timeSlot.getTask();
    return new TimeSlotValue(timeSlot.getStartDate(), timeSlot.getStopDate(),
        timeSlot.getDescription(), timeSlot.getId(), new TaskValue(task));
  }

  private DataSource getDataSource() {
    return layoutManager.getTimeSlotTracker().getDataSource();
  }
}
