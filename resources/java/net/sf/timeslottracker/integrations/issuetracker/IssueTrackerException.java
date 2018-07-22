package net.sf.timeslottracker.integrations.issuetracker;

/**
 * Exception occurred while working issue tracker
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
@SuppressWarnings("serial")
public class IssueTrackerException extends Exception {

  public IssueTrackerException(Throwable cause) {
    super(cause);
  }

  public IssueTrackerException(String message) {
    super(message);
  }

}
