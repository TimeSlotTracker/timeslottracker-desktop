package net.sf.timeslottracker.gui.layouts.classic.tasks;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.*;

import net.sf.timeslottracker.core.Action;
import net.sf.timeslottracker.core.ActionListener;
import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.core.TimeSlotTracker;
import net.sf.timeslottracker.core.TimeoutTimer;
import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TaskChangedListener;
import net.sf.timeslottracker.data.TimeSlotChangedListener;
import net.sf.timeslottracker.gui.DialogPanel;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.TaskInfoInterface;
import net.sf.timeslottracker.gui.TimeSlotFilterListener;
import net.sf.timeslottracker.gui.attributes.AttributeEditDialog;
import net.sf.timeslottracker.gui.attributes.AttributesPanel;
import net.sf.timeslottracker.gui.attributes.AttributesPanel.AttributesPanelListener;
import net.sf.timeslottracker.gui.dateperiod.DatePeriod;
import net.sf.timeslottracker.gui.listeners.TaskSelectionChangeListener;
import net.sf.timeslottracker.utils.StringUtils;

/**
 * A module for timeslottracker to present selected task data.
 * 
 * @version File version: $Revision: 1192 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
@SuppressWarnings("serial")
public class TaskInfo extends JTabbedPane implements TaskInfoInterface {

  private final JTextArea taskDescriptionArea;

  private class TimeSlotFilterAction implements TimeSlotFilterListener {

    @Override
    public void actionPerformed(Action action) {
      TaskInfo.this.data = (DatePeriod) action.getParam();
      updateTimes();
    }

  }

  static final String ACTION_UPDATE_TIMERS = "taskInfo.timePanel.updateTimers";

  public LayoutManager layoutManager;

  private final TimeSlotTracker timeSlotTracker;

  private final GregorianCalendar calendar;

  /** a reference to actually showed task */
  private Task actualTask;

  private final JLabel taskName = new JLabel();
  private final JLabel taskDescription = new JLabel();

  private final TaskInfoTimePanel timeThisTask;
  private final TaskInfoTimePanel timeIncludingSubtasks;

  private final AttributesPanel attributesPanel;

  private DatePeriod data;

  /**
   * Constructs a new task info panel
   */
  public TaskInfo(LayoutManager layoutManager) {
    this.layoutManager = layoutManager;
    this.timeSlotTracker = layoutManager.getTimeSlotTracker();
    this.calendar = new GregorianCalendar();

    // constructs dialog panel with our task's info variables
    JPanel infoPanel = new JPanel(new BorderLayout());
    JLabel labelName = new JLabel(layoutManager.getString("taskinfo.taskName"));
    Font fontUsed = labelName.getFont();
    Font plainFont = fontUsed.deriveFont(Font.PLAIN);
    labelName.setFont(plainFont);
    JLabel labelDescription = new JLabel(
        layoutManager.getString("taskinfo.taskDescription"));
    labelDescription.setFont(plainFont);
    JLabel labelTimeThisTask = new JLabel(
        layoutManager.getString("taskinfo.time.thisTask"));
    labelTimeThisTask.setFont(plainFont);
    JLabel labelTimeIncludingSubtasks = new JLabel(
        layoutManager.getString("taskinfo.time.includingSubtasks"));
    labelTimeIncludingSubtasks.setFont(plainFont);
    timeThisTask = new TaskInfoTimePanel(layoutManager, false);
    timeIncludingSubtasks = new TaskInfoTimePanel(layoutManager, true);

    /* dialog panel where we will keep all our info */
    DialogPanel dialogPanel = new DialogPanel(GridBagConstraints.BOTH, 0.0);
    dialogPanel.addRow(labelName, taskName);
    dialogPanel.addRow(labelDescription, taskDescription);
    dialogPanel.addRow(labelTimeThisTask, timeThisTask);
    dialogPanel.addRow(labelTimeIncludingSubtasks, timeIncludingSubtasks);
    infoPanel.add(dialogPanel, BorderLayout.CENTER);
    addTab(layoutManager.getString("taskinfo.tab.info.title"), infoPanel);

    // constructs task's description panel
    JPanel descriptionPanel = new JPanel(new BorderLayout());
    taskDescriptionArea = new JTextArea(3, 40);
    taskDescriptionArea.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER && e.getModifiers() == KeyEvent.CTRL_MASK) {
          updateTaskDescription();
        }
      }
    });
    JScrollPane descriptionPane = new JScrollPane(taskDescriptionArea);
    descriptionPanel.add(descriptionPane, BorderLayout.CENTER);

    JButton saveButton = new JButton(layoutManager.getIcon("savefile"));
    saveButton.setToolTipText(layoutManager.getString("taskinfo.action.save.tooltip"));
    saveButton.addActionListener(new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        updateTaskDescription();
      }
    });
    JPanel buttons = new JPanel();
    buttons.add(saveButton);
    descriptionPanel.add(buttons, BorderLayout.EAST);

    addTab(layoutManager.getString("taskinfo.tab.description.title"), descriptionPanel);

    // constructs task's attributes panel
    attributesPanel = new AttributesPanel(layoutManager, null, true);
    attributesPanel.setAttributesPanelListener(new AttributesPanelListener() {
      @Override
      public void handleUpdate() {
        setTitleAt(2, attributesPanel.getAttributePanelTitle());
      }
    });
    addTab(attributesPanel.getAttributePanelTitle(), attributesPanel);

    // create a timeout listener to update times every X seconds
    installTimeoutListener();
    timeSlotTracker.addActionListener(new TaskChangedAction());
    layoutManager.addActionListener(new TaskSelectionChangeAction());
    layoutManager.addActionListener(new TimeSlotChangeAction());
    layoutManager.addActionListener(new TimeSlotFilterAction());
  }

  private void updateTaskDescription() {
    if (actualTask == null) {
      return;
    }
    String desctiption = taskDescriptionArea.getText();
    if (desctiption.equals(actualTask.getDescription())) {
      return;
    }
    actualTask.setDescription(desctiption);
    System.out.println("saved");
    timeSlotTracker.fireTaskChanged(actualTask);
  }

  private void installTimeoutListener() {
    Configuration configuration = timeSlotTracker.getConfiguration();
    int updateTimeout = configuration.getInteger(Configuration.TASKINFO_REFRESH_TIMEOUT, 30);
    Object[] updateArgs = {updateTimeout};
    String updateName = layoutManager.getString("taskinfo.timer.update.name",
        updateArgs);
    TimeUpdater timeUpdater = new TimeUpdater();
    timeSlotTracker.addActionListener(timeUpdater, ACTION_UPDATE_TIMERS);
    new TimeoutTimer(timeSlotTracker, updateName, timeUpdater, updateTimeout, -1);
  }

  public void show(Task task) {
    actualTask = task;

    boolean noTask = task == null;
    taskName.setText(noTask ? StringUtils.EMPTY : task.getName());
    boolean noDescription = noTask || task.getDescription() == null;
    taskDescription.setText(noDescription ? StringUtils.EMPTY : task.getDescription().replace('\n', ' '));
    taskDescriptionArea.setText(noDescription ? StringUtils.EMPTY : task.getDescription());
    taskDescriptionArea.setCaretPosition(0);

    refresh();
  }

  public void refresh() {
    updateTimes();
    updateAttributes();
  }

  private void updateTimes() {
    if (actualTask == null) {
      timeThisTask.clear();
      timeIncludingSubtasks.clear();
    } else {
      timeThisTask.setTimes(getTimes(false));
      timeIncludingSubtasks.setTimes(getTimes(true));
    }
  }

  private void updateAttributes() {
    if (actualTask != null) {

      attributesPanel.setTask(actualTask);
      attributesPanel.reloadFields();
    }
  }

  private String[] getTimes(boolean includeSubtasks) {
    String[] times = new String[4];
    Date selectedDay;
    Date selectedWeek;
    Date selectedMonth;
    GregorianCalendar aktDay = new GregorianCalendar(
        timeSlotTracker.getLocale());
    if (includeSubtasks) {
      selectedDay = timeIncludingSubtasks.getSelectedDay();
      selectedWeek = timeIncludingSubtasks.getSelectedWeek();
      selectedMonth = timeIncludingSubtasks.getSelectedMonth();
    } else {
      selectedDay = timeThisTask.getSelectedDay();
      selectedWeek = timeThisTask.getSelectedWeek();
      selectedMonth = timeThisTask.getSelectedMonth();
    }
    if (selectedDay != null) {
      aktDay.setTime(selectedDay);
    }
    aktDay.set(GregorianCalendar.HOUR_OF_DAY, 0);
    aktDay.set(GregorianCalendar.MINUTE, 0);
    aktDay.set(GregorianCalendar.SECOND, 0);
    aktDay.set(GregorianCalendar.MILLISECOND, 0);

    Date startDate = aktDay.getTime();
    calendar.setTime(startDate);
    calendar.add(GregorianCalendar.DAY_OF_MONTH, 1);
    Date stopDate = calendar.getTime();

    times[0] = layoutManager.formatDuration(actualTask.getTime(includeSubtasks,
        getStartDate(null), getStopDate(null))); // null - means all time
    times[1] = layoutManager.formatDuration(actualTask.getTime(includeSubtasks,
        getStartDate(startDate), getStopDate(stopDate)));

    if (selectedWeek != null) {
      aktDay.setTime(selectedWeek);
    }
    calendar.setTime(aktDay.getTime());

    // Adjust for custom first day of week
    Configuration configuration = timeSlotTracker.getConfiguration();
    int diff = aktDay.get(GregorianCalendar.DAY_OF_WEEK)
        - configuration.getInteger(Configuration.WEEK_FIRST_DAY,
            aktDay.getFirstDayOfWeek());

    diff = (diff + 7) % 7;
    calendar.add(GregorianCalendar.DAY_OF_MONTH, -diff);
    startDate = calendar.getTime();
    calendar.add(GregorianCalendar.DAY_OF_MONTH, 7);
    stopDate = calendar.getTime();
    times[2] = layoutManager.formatDuration(actualTask.getTime(includeSubtasks,
        getStartDate(startDate), getStopDate(stopDate)));

    if (selectedMonth != null) {
      aktDay.setTime(selectedMonth);
    }
    calendar.setTime(aktDay.getTime());
    calendar.set(GregorianCalendar.DAY_OF_MONTH, 1);
    startDate = calendar.getTime();
    calendar.add(GregorianCalendar.MONTH, 1);
    stopDate = calendar.getTime();
    times[3] = layoutManager.formatDuration(actualTask.getTime(includeSubtasks,
        getStartDate(startDate), getStopDate(stopDate)));

    return times;
  }

  private Date getStopDate(Date stopDate) {
    if (data == null || data.isNoFiltering()) {
      return stopDate;
    }

    Date timeSlotFilterEnd = data.getEndPeriod();
    if (stopDate == null || stopDate.before(timeSlotFilterEnd)) {
      return stopDate;
    }
    return timeSlotFilterEnd;
  }

  private Date getStartDate(Date startDate) {
    if (data == null || data.isNoFiltering()) {
      return startDate;
    }

    Date timeSlotFilterStart = data.getStartPeriod();
    if (startDate == null || timeSlotFilterStart.after(startDate)) {
      return timeSlotFilterStart;
    }

    return startDate;
  }

  private class TimeUpdater implements ActionListener {
    public void actionPerformed(Action action) {
      if (actualTask != null) {
        updateTimes();
      }
    }
  }

  /**
   * Updates task's description from text area if it was change
   */
  private void updateDescription() {
    String taskDescription = actualTask.getDescription();
    String areaDescriptiontext = taskDescriptionArea.getText();
  }


  /**
   * Listener to action fired when a task was changed
   * <p>
   * It should repaint that task
   */
  private class TaskChangedAction implements TaskChangedListener {
    public void actionPerformed(Action action) {
      Task task = (Task) action.getParam();
      show(task);
    }
  }

  /**
   * Listener to action fired when a task was changed
   * <p>
   * It should repaint that node
   */
  private class TaskSelectionChangeAction implements
      TaskSelectionChangeListener {
    public void actionPerformed(Action action) {
      Task task = (Task) action.getParam();
      show(task);
    }
  }

  /**
   * Listener to action fired when a timeslot was changed
   * <p>
   * It should repaint that node
   */
  private class TimeSlotChangeAction implements TimeSlotChangedListener {
    public void actionPerformed(Action action) {
      if (actualTask != null) {
        updateTimes();
      }
    }
  }
}
