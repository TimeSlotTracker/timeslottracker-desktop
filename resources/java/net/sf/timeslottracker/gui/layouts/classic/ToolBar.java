package net.sf.timeslottracker.gui.layouts.classic;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import net.sf.timeslottracker.core.Action;
import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TaskChangedListener;
import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.actions.AddTaskAction;
import net.sf.timeslottracker.gui.listeners.TaskSelectionChangeListener;

/**
 * ToolBar for the application
 * 
 * @author cnitsa
 */
@SuppressWarnings("serial")
class ToolBar extends JToolBar {

  private final LayoutManager layoutManager;

  private final AddTaskAction addTaskAction;

  private final StartTimingAction startAction;

  private final PauseTimingAction pauseAction;

  private final StopTimingAction stopAction;

  private final RestartTimingAction restartAction;

  ToolBar(LayoutManager layoutManager) {
    this.layoutManager = layoutManager;

    addTaskAction = new AddTaskAction(layoutManager);
    startAction = new StartTimingAction();
    pauseAction = new PauseTimingAction();
    stopAction = new StopTimingAction();
    restartAction = new RestartTimingAction();

    setFloatable(true);

    this.layoutManager.addActionListener(new TaskSelectionChangeListener() {

      @Override
      public void actionPerformed(Action action) {
        updateActionState((Task) action.getParam());
      }
    });
    this.layoutManager.getTimeSlotTracker().addActionListener(
        new TaskChangedListener() {
          @Override
          public void actionPerformed(Action action) {
            updateActionState((Task) action.getParam());
          }
        });
  }

  /**
   * Add components, after every list added separator
   */
  public void add(List<List> components) {
    for (List list : components) {
      for (Object elem : list) {
        if (elem instanceof JComponent) {
          JComponent new_name = (JComponent) elem;

          add(new_name);
        } else if (elem instanceof javax.swing.Action) {
          add((javax.swing.Action) elem);
        }
      }
      if (!list.isEmpty()) {
        addSeparator();
      }
    }

    disableActions();

    for (int i = 0, len = this.getComponentCount(); i < len; i++) {
      Component component = this.getComponent(i);
      if (!(component instanceof JButton)) {
        continue;
      }
      JButton button = (JButton) component;
      button.setFocusPainted(false);
    }
  }

  AddTaskAction getAddTaskAction() {
    return addTaskAction;
  }

  StartTimingAction getStartAction() {
    return startAction;
  }

  PauseTimingAction getPauseAction() {
    return pauseAction;
  }

  StopTimingAction getStopAction() {
    return stopAction;
  }

  RestartTimingAction getRestartAction() {
    return restartAction;
  }

  /**
   * Disable all actions by default
   */
  private void disableActions() {
    addTaskAction.setEnabled(false);
    startAction.setEnabled(false);
    pauseAction.setEnabled(false);
    stopAction.setEnabled(false);
    restartAction.setEnabled(false);
  }

	/**
	 * Updates actions state according given task
	 */
	private void updateActionState(Task task) {
		if (task == null) {
      addTaskAction.setEnabled(false);
      startAction.setEnabled(false);
      restartAction.setEnabled(false);
		} else {
			addTaskAction.setEnabled(true); // enabled if task selected
			startAction.setEnabled(task.canBeStarted());
			restartAction.setEnabled(task.canBeStarted());
		}

    TimeSlot activeTimeSlot = layoutManager.getTimeSlotTracker()
        .getActiveTimeSlot();
    boolean stopEnabled;
    boolean pauseEnabled;
    if (activeTimeSlot != null) {
      Task activeTask = activeTimeSlot.getTask();
      stopEnabled = activeTask.canBeStoped();
      pauseEnabled = activeTask.canBePaused();
    } else {
      stopEnabled = false;
      pauseEnabled=false;
    }
    stopAction.setEnabled(stopEnabled);
    pauseAction.setEnabled(pauseEnabled);
	}

  private class StartTimingAction extends AbstractAction {
    private StartTimingAction() {
      super(layoutManager.getString("timeslots.popupmenu.startTiming.name"),
          layoutManager.getIcon("play"));
      putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(
          layoutManager.getString("timeslots.popupmenu.startTiming.name")
              .charAt(0), Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
      putValue(SHORT_DESCRIPTION,
          layoutManager.getString("timeslots.popupmenu.startTiming.name"));
    }

    public void actionPerformed(ActionEvent e) {
      layoutManager.getTimeSlotTracker().startTiming();
      updateActionState(getSelectedTask());
    }

  }

  /**
   * @return selected task, maybe null
   */
  private Task getSelectedTask() {
    return layoutManager.getTimeSlotsInterface().getSelectedTask();
  }

  private class PauseTimingAction extends AbstractAction {
    private PauseTimingAction() {
      super(layoutManager.getString("timeslots.popupmenu.pauseTiming.name"),
          layoutManager.getIcon("pause"));
      putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(
          layoutManager.getString("timeslots.popupmenu.pauseTiming.name")
              .charAt(0), Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
      putValue(SHORT_DESCRIPTION,
          layoutManager.getString("timeslots.popupmenu.pauseTiming.name"));
    }

    public void actionPerformed(ActionEvent e) {
      layoutManager.getTimeSlotTracker().pauseTiming();
      updateActionState(getSelectedTask());
    }
  }

  private class StopTimingAction extends AbstractAction {
    private StopTimingAction() {
      super(layoutManager.getString("timeslots.popupmenu.stopTiming.name"),
          layoutManager.getIcon("stop"));
      putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(
          layoutManager.getString("timeslots.popupmenu.stopTiming.name")
              .charAt(0), Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
      putValue(SHORT_DESCRIPTION,
          layoutManager.getString("timeslots.popupmenu.stopTiming.name"));
    }

    public void actionPerformed(ActionEvent e) {
      layoutManager.getTimeSlotTracker().stopTiming();
      updateActionState(getSelectedTask());
    }
  }

  private class RestartTimingAction extends AbstractAction {
    private RestartTimingAction() {
      super(layoutManager.getString("timeslots.popupmenu.restartTiming.name"),
          layoutManager.getIcon("replay"));
      putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(
          layoutManager.getString("timeslots.popupmenu.restartTiming.mnemonic")
              .charAt(0), Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
      putValue(SHORT_DESCRIPTION,
          layoutManager.getString("timeslots.popupmenu.restartTiming.name"));
    }

    public void actionPerformed(ActionEvent e) {
      Task taskToBeRestarted = layoutManager.getTimeSlotsInterface()
          .getSelectedTask();
      if (taskToBeRestarted == null) {
        return;
      }

      TimeSlot lastTimeSlot = taskToBeRestarted.getLastTimeSlot();
      if (lastTimeSlot == null) {
        return;
      }

      layoutManager.getTimeSlotTracker().restartTiming(lastTimeSlot.getDescription());

      updateActionState(getSelectedTask());
    }
  }

}
