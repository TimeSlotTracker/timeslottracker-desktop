package net.sf.timeslottracker.integrations.issuetracker;

import java.net.URI;

import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TimeSlot;

/**
 * Issue tracker
 * 
 * @version File version: $Revision: 997 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public interface IssueTracker {

  /**
   * Add selected time slot in issue tracker
   * 
   * @param timeSlot
   *          time slot to add
   * @throws IssueTrackerException
   *           occurred while working issue tracker
   */
  void add(TimeSlot timeSlot) throws IssueTrackerException;

  /**
   * Returns the issue for selected key
   * 
   * @param key
   *          issue's key
   * @return issue
   * @throws IssueTrackerException
   *           occurred while working issue tracker
   */
  Issue getIssue(String key) throws IssueTrackerException;

  /**
   * Returns issue url for given task
   * <p>
   * Task must returns true for method {@link #isIssueTask(Task)}
   * 
   * @param task
   *          given issue task
   * @return url
   * @throws IssueTrackerException
   *           if task is not issue task or error occurred while creating uri
   */
  URI getIssueUrl(Task task) throws IssueTrackerException;

  /**
   * Checks if selected task is issue
   * 
   * @param task
   *          selected task
   * @return true - task is issue, false - overwise
   */
  boolean isIssueTask(Task task);

  /**
   * Checks key accuracy
   * 
   * @param key
   *          key to check
   * @return true - key is valid, false - otherwise
   * @throws IssueTrackerException
   *           occurred while working issue tracker
   */
  boolean isValidKey(String key) throws IssueTrackerException;

  /**
   * Returns issues by given filter
   * 
   * @param filterId
   *          filter id
   * @param handler
   *          callback for issue processing
   * @throws IssueTrackerException
   *           occurred while working issue tracker
   */
  void getFilterIssues(String filterId, IssueHandler handler)
      throws IssueTrackerException;
}
