package net.sf.timeslottracker.integrations.issuetracker.jira;

import net.sf.timeslottracker.integrations.issuetracker.Issue;

/**
 * Jira issue
 * 
 * <p>
 * JIRA (R) Issue tracking project management software
 * (http://www.atlassian.com/software/jira)
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class JiraIssue implements Issue {
  private final String key;

  private final String id;

  private final String summary;

  public JiraIssue(String key, String id, String summary) {
    this.key = key;
    this.id = id;
    this.summary = summary;
  }

  public String getId() {
    return id;
  }

  public String getKey() {
    return key;
  }

  public String getSummary() {
    return summary;
  }

  @Override
  public String toString() {
    return key + " : " + summary;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((key == null) ? 0 : key.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    JiraIssue other = (JiraIssue) obj;
    if (key == null) {
      if (other.key != null)
        return false;
    } else if (!key.equals(other.key))
      return false;
    return true;
  }

}
