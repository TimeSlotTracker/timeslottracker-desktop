package net.sf.timeslottracker.integrations.issuetracker;

/**
 * Handler for processing issues
 * 
 * @version File version: $Revision: 1.23 $, $Date: 2007/04/07 03:59:30 $
 * @author Last change: $Author: cnitsa $
 */
public interface IssueHandler {

  /**
   * Process given issue
   * 
   * @throws IssueTrackerException
   *           error occurred while getting next issue
   */
  void handle(Issue issue) throws IssueTrackerException;

  /**
   * Check if handler do not want to process next issues
   * 
   * @return true - will stop issues's getting process, false - normal flow
   */
  boolean stopProcess();

}