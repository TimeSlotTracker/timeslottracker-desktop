package net.sf.timeslottracker.core;

import java.util.Collection;

import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TimeSlot;

/**
 * Defines interface for search engine.
 * <p/>
 * The search engine should enable user to search in tasks, descriptions,
 * attributes and so on. It should give the feedback to gui part to update the
 * displayed state (for example to show that it's indexing).
 * 
 * @author User: zgibek Date: 2008-08-30 Time: 17:14:35 $Id: SearchEngine.java
 *         992 2010-09-18 13:49:06Z cnitsa $
 */
public interface SearchEngine {

  public final static String INDEX_CREATED = "search.engine.INDEX_CREATED";
  public final static String INDEXING = "search.engine.INDEXING";
  public final static String SEARCHING_FINISHED = "search.engine.SEARCHING_FINISHED";

  /**
   * Initiates creation of index.
   * <p/>
   * After index creating the event {@link #INDEX_CREATED} should be sent.
   * 
   * @param root
   *          the root task from the indexing should be stared
   * @see Action
   * @see ActionListener
   */
  public void createIndex(Task root);

  /**
   * Do search in tasks and returns collection of Task and TimeSlots matching
   * query
   * 
   * @param query
   *          query entered by user
   * @return collection (<b>never null</b>) with {@link Task} and
   *         {@link net.sf.timeslottracker.data.TimeSlot} which matches user
   *         query.
   * @throws TimeSlotTrackerException
   *           when index was not yet created.
   */
  public Collection doSearch(String query) throws TimeSlotTrackerException;

  /**
   * Updates information about task in index (not subtasks).
   * 
   * @param task
   *          task to update (or to add as new, if it's a new record)
   */
  public void update(Task task);

  /**
   * Updates information about timeslot in dictionary.
   * 
   * @param timeSlot
   *          timeslot to update (or to add as new, if it's a new record)
   */
  public void update(TimeSlot timeSlot);
}
