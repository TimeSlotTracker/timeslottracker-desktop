package net.sf.timeslottracker.gui.layouts.classic;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.JMenuItem;
import javax.swing.border.Border;

import net.sf.timeslottracker.core.Action;
import net.sf.timeslottracker.core.ActionListener;
import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.core.TimeoutTimer;
import net.sf.timeslottracker.data.xml.DataSaveAction;
import net.sf.timeslottracker.gui.FavouritesInterface;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.TaskInfoInterface;
import net.sf.timeslottracker.gui.TasksByDaysInterface;
import net.sf.timeslottracker.gui.TasksInterface;
import net.sf.timeslottracker.gui.TimeSlotsInterface;
import net.sf.timeslottracker.gui.layouts.classic.favourites.Favourites;
import net.sf.timeslottracker.gui.layouts.classic.search.SearchPanel;
import net.sf.timeslottracker.gui.layouts.classic.tasks.TaskInfo;
import net.sf.timeslottracker.gui.layouts.classic.tasks.TasksTree;
import net.sf.timeslottracker.gui.layouts.classic.tasksbydays.DaysTree;
import net.sf.timeslottracker.gui.layouts.classic.timeslots.Timeslots;
import net.sf.timeslottracker.utils.SwingUtils;

/**
 * Class gathering all information about classic layout and made for
 * communicating with TimeSlotTracker system.
 * 
 * File version: $Revision: 1174 $, $Date: 2009-05-16 17:03:52 +0700 (Sat, 16 May
 * 2009) $ Last change: $Author: cnitsa $
 */
public class ClassicLayout extends LayoutManager {

  /**
   * Listens to Action Action.ACTION_SET_CONFIGURATION to perform actions to set
   * current values of split dividers locations.
   */
  private class SetConfigurationActionListener implements ActionListener {
    public void actionPerformed(Action action) {
      if (action.getSource() instanceof Configuration) {
        Configuration configuration = (Configuration) action.getSource();

        configuration.set(Configuration.LAST_TASKS_FAVOURITES_SPLIT,
            leftSplit.getDividerLocation());

        configuration.set(Configuration.LAST_TASKINFO_TIMESLOTS_SPLIT,
            taskTreeProperties.getRightDividerLocation());

        configuration.set(Configuration.LAST_MAIN_SPLIT,
            mainSplit.getDividerLocation());
      }
    }
  }

  /** properties of view */
  private class ViewProperties {

    private int rightDividerLocation = -1;

    private final boolean taskInfoVisible;

    ViewProperties(boolean taskInfoVisible) {
      this.taskInfoVisible = taskInfoVisible;
    }

    int getRightDividerLocation() {
      return rightDividerLocation == -1 ? rightSplit.getDividerLocation()
          : rightDividerLocation;
    }

    public void save() {
      rightDividerLocation = rightSplit.getDividerLocation();
    }

    public void apply() {
      rightSplit.setDividerLocation(rightDividerLocation);
      taskInfo.setVisible(taskInfoVisible);
    }
  }

  private ViewProperties taskTreeProperties = new ViewProperties(true);

  private ViewProperties daysTreeProperties = new ViewProperties(false);

  private JPanel layoutPanel;

  private Favourites favourites;

  private TaskInfo taskInfo;

  private TasksTree tasksTree;

  private DaysTree daysTree;

  private Timeslots timeslots;

  private MainMenuBar mainMenuBar;

  private ToolBar toolBar;

  private JTextField memoryField;
  private JTextField dataStatusField;

  /**
   * Split between left pane (Tasks tree and favourites) and a right one (task
   * info and time slots)
   */
  private JSplitPane mainSplit;

  /**
   * Split between tasks tree and favourites
   */
  private JSplitPane leftSplit;

  /**
   * Split between Task info and timeslots
   */
  private JSplitPane rightSplit;

  private final JPanel changeblePanel = new JPanel(new BorderLayout());

  public TasksByDaysInterface getTasksByDaysInterface() {
    return daysTree;
  }

  public FavouritesInterface getFavouritesInterface() {
    return favourites;
  }

  public JComponent getGUIComponent() {
    return layoutPanel;
  }

  public JMenuBar getMenuBar() {
    return mainMenuBar;
  }

  @Override
  public JToolBar getToolBar() {
    return toolBar;
  }

  public TaskInfoInterface getTaskInfoInterface() {
    return taskInfo;
  }

  public TasksInterface getTasksInterface() {
    return tasksTree;
  }

  public TimeSlotsInterface getTimeSlotsInterface() {
    return timeslots;
  }

  protected void initSubclass() {
    layoutPanel = new JPanel(new BorderLayout());
    favourites = new Favourites(this);
    taskInfo = new TaskInfo(this);
    timeslots = new Timeslots(this);

    javax.swing.AbstractAction switchViewAction = new javax.swing.AbstractAction() {
      /*
       * (non-Javadoc)
       * 
       * @see
       * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
       * )
       */
      public void actionPerformed(ActionEvent e) {
        JComboBox comboBox = (JComboBox) e.getSource();
        doSwitchAction(comboBox.getSelectedIndex());
      }
    };

    tasksTree = new TasksTree(this, switchViewAction);
    changeblePanel.add(tasksTree, BorderLayout.CENTER); // default is task tree
    daysTree = new DaysTree(this, switchViewAction);

    JPanel taskTreeView = new JPanel(new BorderLayout());
    taskTreeView.add(changeblePanel, BorderLayout.CENTER);

    leftSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, taskTreeView,
        favourites);
    leftSplit.setOneTouchExpandable(true);

    rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, taskInfo, timeslots);
    rightSplit.setOneTouchExpandable(true);

    mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSplit,
        rightSplit);
    mainSplit.setOneTouchExpandable(true);

    layoutPanel.add(mainSplit);

    mainMenuBar = new MainMenuBar(this);
    mainMenuBar.setLayoutMenu(getMenuItems());
    mainMenuBar.updateViewMenu(tasksTree.getMenuItems());

    SearchPanel searchField = new SearchPanel(this);

    toolBar = new ToolBar(this);

    Border emptyBorder = BorderFactory.createEmptyBorder();

    // memory usage field
    memoryField = new JTextField();
    memoryField.setForeground(Color.GRAY);
    memoryField.setEditable(false);
    memoryField.setPreferredSize(new Dimension(60,0));
    memoryField.setBorder(emptyBorder);
    updateMemoryUsage();

    // data status field
    dataStatusField = new JTextField();
    dataStatusField.setBorder(emptyBorder);
    dataStatusField.setForeground(Color.GRAY);
    dataStatusField.setEditable(false);

		toolBar.add(Arrays.asList(
				Collections.singletonList(toolBar.getAddTaskAction()),
				Arrays.asList(toolBar.getStartAction(),
						toolBar.getPauseAction(), toolBar.getStopAction(),
						toolBar.getRestartAction()),
				(List) Collections.singletonList(searchField),
				(List) Collections.singletonList(memoryField),
				(List) Collections.singletonList(dataStatusField)));

    toolBar.setPreferredSize(SwingUtils.determinePreferredSize(toolBar));

    addActionListener(timeslots);
    addActionListener(favourites);
    timeSlotTracker.addActionListener(new SetConfigurationActionListener(),
        Action.ACTION_SET_CONFIGURATION);

    // register thread for memory updates
    new TimeoutTimer(timeSlotTracker, "Memory usage updater", new ActionListener() {
      @Override
      public void actionPerformed(Action action) {
        updateMemoryUsage();
      }
    }, 30, -1);
    
    timeSlotTracker.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(Action action) {
        updateDataStatus((DataSaveAction)action);
      }
    }, DataSaveAction.ACTION_NAME);
  }

	private void updateMemoryUsage() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final long used = Runtime.getRuntime().totalMemory()
						- Runtime.getRuntime().freeMemory();
				memoryField.setText(getString("status.memory.template",
						mbyte(used), mbyte(Runtime.getRuntime().maxMemory())));
			}
		});
	}

	private void updateDataStatus(final DataSaveAction action) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				dataStatusField.setText(getString("status.data.template",
						(action.hasError()
								? "Error: " + action.getErrorMessage() : "OK"),
						new Date()));
			}
		});
	}

  private long mbyte(long bytes) {
    return bytes / 1024 / 1024;
  }

  private void doSwitchAction(int index) {
    changeblePanel.removeAll();
    Collection<JMenuItem> menuItems = Collections.emptyList();
    if (index == 0) {
      daysTreeProperties.save();
      taskTreeProperties.apply();

      menuItems = getTasksInterface().getMenuItems();
      changeblePanel.add(tasksTree, BorderLayout.CENTER);

      getTasksInterface().activate();

    } else if (index == 1) {
      taskTreeProperties.save();
      daysTreeProperties.apply();

      menuItems = getTasksByDaysInterface().getMenuItems();
      changeblePanel.add(daysTree, BorderLayout.CENTER);

      getTasksByDaysInterface().activate();
    }

    // updates menu
    mainMenuBar.updateViewMenu(menuItems);

    // update whole panel
    changeblePanel.validate();
    changeblePanel.repaint();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sf.timeslottracker.gui.LayoutManager#getMenuItems()
   */
  @Override
  public List<JMenuItem> getMenuItems() {
    javax.swing.AbstractAction action1 = new javax.swing.AbstractAction("1 "
        + getString("taskstree.title")) {
      {
        putValue(javax.swing.Action.MNEMONIC_KEY, KeyEvent.VK_1);
      }

      public void actionPerformed(ActionEvent e) {
        doSwitchAction(0);
      }
    };
    javax.swing.AbstractAction action2 = new javax.swing.AbstractAction("2 "
        + getString("daystree.title")) {
      {
        putValue(javax.swing.Action.MNEMONIC_KEY, KeyEvent.VK_2);
      }

      public void actionPerformed(ActionEvent e) {
        doSwitchAction(1);
      }
    };
    return Arrays.asList(new JMenuItem(action1), new JMenuItem(action2));
  }

	public void postInit() {
		Configuration configuration = timeSlotTracker.getConfiguration();
		int location = configuration
				.getInteger(Configuration.LAST_TASKS_FAVOURITES_SPLIT, -1);
		if (location < 0) {
			leftSplit.setDividerLocation(0.70);
		} else {
			leftSplit.setDividerLocation(location);
		}

		location = configuration
				.getInteger(Configuration.LAST_TASKINFO_TIMESLOTS_SPLIT, -1);
		if (location < 0) {
			rightSplit.setDividerLocation(0.30);
		} else {
			rightSplit.setDividerLocation(location);
		}

		location = configuration.getInteger(Configuration.LAST_MAIN_SPLIT, -1);
		if (location < 0) {
			mainSplit.setDividerLocation(0.30);
		} else {
			mainSplit.setDividerLocation(location);
		}

		timeslots.fireActions();
	}

}
