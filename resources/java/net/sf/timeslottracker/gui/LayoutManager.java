package net.sf.timeslottracker.gui;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import net.sf.timeslottracker.core.Action;
import net.sf.timeslottracker.core.ActionListener;
import net.sf.timeslottracker.core.TimeSlotTracker;
import net.sf.timeslottracker.data.DataLoadedListener;
import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.data.TimeSlotChangedListener;
import net.sf.timeslottracker.gui.configuration.ConfigurationPanel;
import net.sf.timeslottracker.gui.listeners.TaskSelectionChangeListener;
import net.sf.timeslottracker.gui.listeners.TasksByDaysSelectionAction;
import net.sf.timeslottracker.gui.listeners.TasksByDaysSelectionChangeListener;
import net.sf.timeslottracker.utils.TimeUtils;

/**
 * An abstract class which every layout-class should extend. It contains all
 * necessary methods for communicating with TimeSlotTracker as well as a few
 * abstract method a user should implement.
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 09:00:38 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public abstract class LayoutManager {

  /**
   * Listener to action fired when a data was loaded (or reloaded).
   * <p>
   * It repaints its view and then, if any task is active selects its node.
   */
  private class DataLoadedAction implements DataLoadedListener {
    public void actionPerformed(Action action) {
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          reloadData();
        }
      });
    }
  }

  /**
   * Return known superclass for given listener class
   * 
   * @return class, or null if unknown superclass
   */
  private static Class<? extends ActionListener> getKey(
      final Class<? extends ActionListener> clazz) {
    Class<? extends ActionListener> keyClazz = null;

    if (TaskSelectionChangeListener.class.isAssignableFrom(clazz)) {
      keyClazz = TaskSelectionChangeListener.class;
    } else if (TimeSlotChangedListener.class.isAssignableFrom(clazz)) {
      keyClazz = TimeSlotChangedListener.class;
    } else if (TasksByDaysSelectionChangeListener.class.isAssignableFrom(clazz)) {
      keyClazz = TasksByDaysSelectionChangeListener.class;
    } else if (TimeSlotFilterListener.class.isAssignableFrom(clazz)) {
      keyClazz = TimeSlotFilterListener.class;
    }

    return keyClazz;
  }

  protected TimeSlotTracker timeSlotTracker;

  private final Map<Class<? extends ActionListener>, List<ActionListener>> listeners = new HashMap<Class<? extends ActionListener>, List<ActionListener>>();

  /**
   * Contains a resource messages specific to this skin only
   */
  private ResourceBundle messages;

  protected LayoutManager() {
  }

  /**
   * adds a specific action Listener. We can add some listener for a task-change
   * on a tasks tree or to listen to if a record change or something like that
   */
  public void addActionListener(ActionListener listener) {
    Class<? extends ActionListener> key = getKey(listener.getClass());
    if (key == null) {
      return;
    }

    List<ActionListener> list;
    if (listeners.containsKey(key)) {
      list = listeners.get(key);
    } else {
      list = new ArrayList<ActionListener>();
      listeners.put(key, list);
    }

    list.add(listener);
  }

  /**
   * Fires event to every TasksByDaysSelectionChangeListener with action and
   * param which node is selected now.
   * 
   * @param action
   *          action with timeslots subset which is selected, and description of
   *          subset
   */
  public void fireTasksByDaysSelectionChanged(TasksByDaysSelectionAction action) {
    doFire(action, TasksByDaysSelectionChangeListener.class);
  }

  /**
   * Fires event to every TaskSelectionChangeListener with action and param
   * which task is selected now.
   * 
   * @param task
   *          task which is selected now (just after the change)
   */
  public void fireTaskSelectionChanged(Task task) {
    doFire(new Action("taskSelectionChanged", null, task),
        TaskSelectionChangeListener.class);
  }

  /**
   * Fires event to every TimeSlotChangedListener with action and param which
   * timeslot is selected now.
   * 
   * @param timeslot
   *          timeslot which is selected now (just after the change)
   */
  public void fireTimeSlotChanged(TimeSlot timeslot) {
    doFire(new Action("TimeSlotChanged", null, timeslot),
        TimeSlotChangedListener.class);
  }

  /**
   * Fires event to every TimeSlotFilterListener with given action
   */
  public void fireTimeSlotFilterChanged(Action action) {
    doFire(action, TimeSlotFilterListener.class);
  }

  /**
   * Formats time duration according to our needs.
   * <p>
   * It formats duration in format "hh:mm" (when less then 24 hours) or
   * "x d, hh:mm" when more then one day is occupied by this duration (days,
   * hous, minutes) or in custom format (hours, minutes)
   * <p>
   * or in custom format:
   * <p>
   * "hh:mm" return String
   */
  public String formatDuration(long milliseconds) {
    return TimeUtils.formatDuration(this.timeSlotTracker, milliseconds);
  }

  /**
   * Returns collection of configurations panels.
   * <p>
   * If our skin has some configuration they could be added to application
   * configuration. It can be simply done via this method. Just pack all options
   * you want to configure into one or more ConfigurationPanel and make an
   * object implementing Collection interface of it.
   * 
   * The default implementation returns a null value witch means there is no
   * need for any configuration.
   * 
   * @returns null or an empty Collection if there is no configuration panels
   *          need or a Collection with ConfigurationPanel objects
   * 
   * @see net.sf.timeslottracker.gui.configuration.ConfigurationPanel
   */
  public Collection<ConfigurationPanel> getConfigurationPanels(
      Collection<ConfigurationPanel> panels) {
    return null;
  }

  /**
   * Returns localized message form property file, got from a TimeSlotTracker.
   * 
   * @see net.sf.timeslottracker.core.TimeSlotTracker#getString(String)
   */
  public String getCoreString(String key) {
    return timeSlotTracker.getString(key);
  }

  /**
   * Returns localized message form property file, got from a TimeSlotTracker.
   * 
   * @see net.sf.timeslottracker.core.TimeSlotTracker#getString(String,
   *      Object[])
   */
  public String getCoreString(String key, Object... args) {
    return timeSlotTracker.getString(key, args);
  }

  /**
   * Returns reference to FavouritesInterface if exists, otherwise null.
   */
  public FavouritesInterface getFavouritesInterface() {
    return null;
  }

  /**
   * returns constructed component with all modules you want.
   */
  public abstract JComponent getGUIComponent();

  /**
   * Returns image got from a core timeslottracker object, it means from main
   * application class loader
   * 
   * @see net.sf.timeslottracker.core.TimeSlotTracker#getIcon(String)
   */
  public ImageIcon getIcon(String iconPath) {
    return timeSlotTracker.getIcon(iconPath);
  }

  /**
   * Gets menu bar if any
   */
  public JMenuBar getMenuBar() {
    return null;
  }

  /**
   * Returns localized message specific to this layout manager. This is not a
   * core message. If you want to get a core one, use
   * <code>getCoreMesssage</code> method.
   * 
   * @return localized string or null if the key is invalid.
   */
  public String getString(String key) {
    try {
      if (messages != null) {
        return messages.getString(key);
      }
      Object[] args = { key };
      String message = getCoreString("layoutManager.missing-locale-object",
          args);
      timeSlotTracker.errorLog(message);
    } catch (MissingResourceException e) {
      Object[] args = { key };
      String message = getCoreString("starter.cannot-find-locale-key", args);
      timeSlotTracker.errorLog(message);
      timeSlotTracker.errorLog(e);
    }
    return null;
  }

  /**
   * Returns localized message specific to this layout manager. This is not a
   * core message. If you want to get a core one, use
   * <code>getCoreMesssage</code> method.
   * 
   * @param key
   *          key for string to find it in a property file
   * @param args
   *          a object array with arguments to properly format found string
   * 
   * @return localized string or null if the key is invalid.
   */
  public String getString(final String key, final Object... args) {
    String msgToFormat = getString(key);
    MessageFormat format = new MessageFormat(msgToFormat,
        timeSlotTracker.getLocale());
    String msgDone;
    try {
      msgDone = format.format(args);
    } catch (IllegalArgumentException e) {
      Object[] msgArgs = { e.getMessage() };
      msgDone = getCoreString("layout.cannot-format-message", msgArgs);
      timeSlotTracker.errorLog(e);
    }
    return msgDone;
  }

  /**
   * Returns reference to TaskInfoInterface if exists, otherwise null.
   */
  public TaskInfoInterface getTaskInfoInterface() {
    return null;
  }

  /**
   * @return representation tasks by days
   */
  public TasksByDaysInterface getTasksByDaysInterface() {
    return null;
  }

  /**
   * Returns reference to TasksInterface if exists, otherwise null.
   */
  public TasksInterface getTasksInterface() {
    return null;
  }

  /**
   * Returns reference to TimeSlotsInterface if exists, otherwise null.
   */
  public TimeSlotsInterface getTimeSlotsInterface() {
    return null;
  }

  /**
   * Returns a reference to core TimeSlotTracker interface.
   */
  public TimeSlotTracker getTimeSlotTracker() {
    return timeSlotTracker;
  }

  /**
   * @return layout toolbar
   */
  public abstract JToolBar getToolBar();

  public void init(TimeSlotTracker timeSlotTracker) {
    setTimeSlotTracker(timeSlotTracker);
    getResourceBundleMessages();
    initSubclass();
    this.timeSlotTracker.addActionListener(new DataLoadedAction());
  }

  /**
   * @return layout main menu items
   */
  public abstract List<JMenuItem> getMenuItems();

  /**
   * Implement this method if you have any special actions to take, for example
   * resize the SplitPanels.
   */
  public void postInit() {
  }

  /**
   * Gets a resource bundle messages object.
   */
  protected void getResourceBundleMessages() {
    try {
      String resource = this.getClass().getName();
      int lastDot = resource.lastIndexOf(".");
      if (lastDot > 0) {
        resource = resource.substring(lastDot + 1);
      }
      messages = ResourceBundle
          .getBundle(resource, timeSlotTracker.getLocale());
    } catch (MissingResourceException e) {
      timeSlotTracker.errorLog(e);
    }
  }

  protected abstract void initSubclass();

  private void doFire(Action action, Class<? extends ActionListener> clazz) {
    timeSlotTracker.debugLog(action.getName() + ": " + action);

    List<ActionListener> list = listeners.get(getKey(clazz));
    if (list == null) {
      return;
    }

    for (ActionListener actionListener : list) {
      actionListener.actionPerformed(action);
    }
  }

  /**
   * Reloads all data. It rereads again everything from data source.
   */
  private void reloadData() {
    TasksInterface tasks = getTasksInterface();
    TasksByDaysInterface tasksByDays = getTasksByDaysInterface();
    TaskInfoInterface taskInfo = getTaskInfoInterface();
    TimeSlotsInterface timeSlots = getTimeSlotsInterface();
    FavouritesInterface favourites = getFavouritesInterface();

    if (timeSlots != null) {
      timeSlots.show((Task) null);
    }
    if (taskInfo != null) {
      taskInfo.show(null);
    }
    // update before tasks. overwise task tree shorts node names which in
    // Favorites
    if (favourites != null) {
      favourites.reload();
    }
    if (tasks != null) {
      tasks.reloadTree();
    }
  }

  /**
   * Binds with a TimeSlotTracker core object
   */
  private void setTimeSlotTracker(TimeSlotTracker timeSlotTracker) {
    this.timeSlotTracker = timeSlotTracker;
  }
}
