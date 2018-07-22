package net.sf.timeslottracker.gui;

import java.util.Collection;

import net.sf.timeslottracker.data.Attribute;
import net.sf.timeslottracker.data.Task;

/**
 * Describes acceptable operations on a tasks list. It have to be independent of
 * the implementation. For example, the classic theme (skin) will make use of a
 * tree implemented with JTree.
 * 
 * File version: $Revision: 1000 $, $Date: 2009-05-16 08:53:21 +0700 (Sat, 16 May
 * 2009) $ Last change: $Author: cnitsa $
 */
public interface TasksInterface {

  /**
   * Reloads whole tree.
   * <p>
   * It simply should reload all data from a data source.
   */
  void reloadTree();

  /**
   * Selects task given through a parameter.
   * <p>
   * For example, in the classic layout it should find where the task is located
   * in the tasks tree and then select that node.
   */
  void selectTask(Task task);

  /**
   * Add new task
   * 
   * @param taskName
   *          name of task, maybe null
   * @param attributes
   *          attributes for new task, maybe null
   */
  void add(String taskName, Collection<Attribute> attributes);

  /**
   * Add new task with popup dialog
   * 
   * @param name
   *          task name, can't be null
   * @param attributes
   *          attributes for task, maybe null
   */
  void addWoDialog(String name, Collection<Attribute> attributes);

  /**
   * Add new task from issue tracker
   */
  void addTaskFromIssueTracker();

  /**
   * @return current selected task, maybe null
   */
  Task getSelected();
  
  /**
   * Edit selected task
   */
  void editSelected();

  /**
   * @return flag to show hidden task
   */
  boolean showHiddenTasks();

  /**
   * Sets the flag to show hidden task
   * 
   * @param showHiddenTasks
   *          flag to show hidden task
   */
  void setShowHiddenTasks(boolean showHiddenTasks);

  /**
   * @return current view menu items
   */
  Collection<javax.swing.JMenuItem> getMenuItems();

  /**
   * Activate the module. Usually needs then module gets focus.
   */
  void activate();

}
