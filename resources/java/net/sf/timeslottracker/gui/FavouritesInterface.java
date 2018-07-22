package net.sf.timeslottracker.gui;

import java.util.Collection;

import net.sf.timeslottracker.data.Task;

/**
 * Describes acceptable operations on favourites component. It must be used, not
 * any favourites panel directly.
 * 
 * File version: $Revision: 1037 $, $Date: 2009-05-16 08:53:21 +0700 (Sat, 16 May
 * 2009) $ Last change: $Author: cnitsa $
 */
public interface FavouritesInterface {

  /**
   * Sets new favourites collection.
   * 
   * @param favourtes
   *          null or an empty collection when there is no favourites or
   *          collection of Task objects with favourites ones
   */
  void setFavourites(Collection<Task> favourtes);

  /**
   * Add a task to favourites.
   */
  void add(Task task);

  /**
   * Removes a task from favourtes.
   */
  void remove(Task task);

  /**
   * Removes a task and his children from favourtes.
   * <p>
   * It's mostly useful when deleting a task from a TasksInterface object and
   * from favourites should be removed that task as well as his children,
   * because they are no longer valid.
   */
  void removeTree(Task task);

  /**
   * Clears all tasks from favourites.
   */
  void removeAll();

  /**
   * Returns a collection with Task object which are in the favourites.
   */
  Collection<Task> getFavourites();

  /**
   * Checks if a specified task exists in a favourites.
   * 
   * @param task
   *          a task we want to check if it exists in favourites
   * @return <code>true</code> if favourites contains specified task
   */
  boolean contains(Task task);

  /**
   * Reload data
   */
  void reload();
}
