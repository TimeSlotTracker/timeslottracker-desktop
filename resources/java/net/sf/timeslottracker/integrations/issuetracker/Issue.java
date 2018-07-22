package net.sf.timeslottracker.integrations.issuetracker;

/**
 * Issue of Issue Tracker
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public interface Issue {
  /**
   * @return internal issue id
   */
  String getId();

  /**
   * @return external issue key
   */
  String getKey();

  /**
   * @return issue summary
   */
  String getSummary();
}
