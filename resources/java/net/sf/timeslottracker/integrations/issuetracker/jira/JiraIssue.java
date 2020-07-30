package net.sf.timeslottracker.integrations.issuetracker.jira;

import net.sf.timeslottracker.integrations.issuetracker.Issue;

/**
 * Jira issue
 * 
 * <p>
 * JIRA (R) Issue tracking project management software
 * (http://www.atlassian.com/software/jira)
 */
public class JiraIssue implements Issue {

  private String key;
  private String id;
  private String summary;
  private String assignee;
  private boolean subTask;

  public JiraIssue() {
  }

  public JiraIssue(String key, String id, String summary) {
    this.key = key;
    this.id = id;
    this.summary = summary;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public String getAssignee() {
    return assignee;
  }

  public void setAssignee(String assignee) {
    this.assignee = assignee;
  }

  @Override
  public boolean isSubTask() {
    return subTask;
  }

  public void setSubTask(boolean subTask) {
    this.subTask = subTask;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();

    if (isSubTask()) {
      builder.append("- ");
    }

    return builder.append(key).append(" ").append(summary).append(" @ ").append(assignee).toString();
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
