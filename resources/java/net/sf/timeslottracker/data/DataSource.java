package net.sf.timeslottracker.data;

import java.util.Collection;
import java.util.Date;

import net.sf.timeslottracker.core.TimeSlotTracker;
import net.sf.timeslottracker.gui.configuration.ConfigurationPanel;

/**
 * Interface to access our data -to get them, adds, deletes, modifies. The idea
 * is that a data store implementation should be transparent to whole
 * application. It doesn't matter if we will use an xml file, flat files, sql
 * database system or even any EJBs and application server. If any
 * implementation requires any special functions, for example a timed
 * regenerating (like in xml) it should be done by itself - the rest of
 * application shouen't have to know anything about these requirements.
 * 
 * @version File version: $Revision: 1168 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public interface DataSource {

  /**
   * If program started with this environment variable set then the data and
   * properties file are loaded from this directory.
   * <p>
   * Run program with
   * 
   * <pre>
   *  java -Dtst.directory=path_to_directory_where_store_data -jar tst.jar
   * </pre>
   */
  String TIMESLOTTRACKER_DIRECTORY = "tst.directory";

  /**
   * If program started with this environment variable set then the DTD file is
   * loaded from this directory (but WITHOUT the last "\" or "/" char).
   * <p>
   * Run program with
   * 
   * <pre>
   *  java -Ddtd.directory=path_to_directory_with_DTD -jar tst.jar
   * </pre>
   */
  String DTD_DIRECTORY = "dtd.directory";

  /** Event name to fire when the data are loaded */
  String DATA_LOADED = "datasource.DATA_LOADED";

  /**
   * Returns a panel with a configuration screen to put it into a tab in
   * configuration window.
   */
  ConfigurationPanel getConfigurationPanel();

  /**
   * Binds this DataSource object with main application interface.
   */
  void setTimeSlotTracker(TimeSlotTracker timeSlotTracker);

  /**
   * Reloads all data from an source.
   * 
   * @return <code>true</code> when data successfully loaded. If any error
   *         occurred a <code>false</code> should be returned.
   */
  boolean reloadData();

  /**
   * Saves Task to underlying data source. It saves only the one-level datas.
   * Sub tasks aren't saving in this method. The same applies to TimeSlot(s).
   * There is another method for them.
   */
  void save(Task task);

  /**
   * Saves a time slot in underlying data source
   */
  void save(TimeSlot timeSlot);

  /**
   * Saves a collection with AttributeType records.
   * <p>
   * The records has not been immediately written to underlying datasource. They
   * can be stored when a normal save action occurs.
   * 
   * @param records
   *          a collection of records of type AttributeType.
   */
  void saveAttributeTypes(Collection<AttributeType> records);

  /**
   * Returns current collection of attribute types.
   */
  Collection<AttributeType> getAttributeTypes();

  /**
   * Saves all to underlying data source with logging error to log file.
   */
  boolean saveAll();

  /**
   * Saves all to underlying data source
   * 
   * @param popupErrors
   *          show popup dialogs with errors
   */
  boolean saveAll(boolean popupErrors);

  /**
   * Sets root - a main task with all other below them.
   */
  void setRoot(Task root);

  /**
   * Returns one - the main one - task, root of all others.
   * 
   */
  Task getRoot();

  /**
   * Returns task with given Id.
   */
  Task getTask(Object id);

  /**
   * Creates a new Task object.
   * 
   * @param parentTask
   *          a task which should hold a new created task. Specify
   *          <code>null</code> if it is the root.
   * @param id
   *          a new identifier this task should have. Specify <code>null</code>
   *          to set it automatically.
   * @param name
   *          a name for a task.
   * @param description
   *          a description a new created task should have
   * @param hidden
   *          hidden task flag
   */
  Task createTask(Task parentTask, Object id, String name, String description,
      boolean hidden);

  /**
   * Moves task from one parent to another one.
   * <p>
   * If you give <code>null</code> as a <code>newParent</code> this task should
   * be deleted from DataSource
   * <p>
   * You should call this method to rearrange the task between parents.
   * Implementation of this method should also set a new parent to Task object
   */
  void moveTask(Task task, Task newParent);

  /**
   * Moves task within its parent order.
   * <p>
   * This method should be used to reorder tasks in datasource. <br>
   * Remember that this change probably has nothing to your layout order - this
   * has to be implement besides this.
   * <p>
   * If <code>task</code> has no parent nothing change.
   * 
   * @param task
   *          a task to move
   * @param newPosition
   *          new position of the <code>task</code> in its parent
   */
  void moveTask(Task task, int newPosition);

  /**
   * Creates a new TimeSlot object without timeslotId (it will be generated).
   * 
   * @see #createTask(Task, Object, String, String, boolean)
   */
  TimeSlot createTimeSlot(Task parentTask, Date start, Date stop,
      String description);

  /**
   * Creates a new TimeSlot object.
   * 
   * @param parentTask
   *          a task which will hold new created time slot
   * @param start
   *          start date for this time slot. A <code>null</code> is possible.
   * @param stop
   *          stop date for this time slot. A <code>null</code> is possible.
   * @param description
   *          an extra description for this time slot. A <code>null</code> value
   *          is possible.
   */
  TimeSlot createTimeSlot(Task parentTask, Object timeslotId, Date start,
      Date stop, String description);

  /**
   * Returns a children of a given parent task (even root). Task returned are
   * parent's immediately children (one level down only).
   */
  Collection<Task> getChildren(Task parent);

  /**
   * Returns a collection from data source of favorites.
   * <p>
   * It isn't a collection of favorites tasks actually used in system. It's only
   * a startup favorites list. It's needed to fill the favorites module just
   * after data loading.
   * <p>
   * To get actual favorites list use
   * <code>FavouritesInterface.getFavourites</code> method.
   * 
   * @see net.sf.timeslottracker.gui.FavouritesInterface#getFavourites()
   */
  Collection<Task> getFavourites();

  /**
   * Copy task
   * 
   * @param sourceTask
   *          task to copy
   * @param targetTask
   *          task that will have new task
   * @param newNodeIndex
   *          index, -1 means make new task as child of targetTask, another
   *          value means copy task to parent of target with selected index
   * @param deepCopy
   *          copy with children
   * 
   * @return new task
   */
  Task copyTask(Task sourceTask, Task targetTask, int newNodeIndex,
      boolean deepCopy);

}
