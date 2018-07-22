package net.sf.timeslottracker.gui.configuration;

import java.util.Arrays;

import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.integrations.issuetracker.jira.JiraTracker;

/**
 * Class with configuration panel for Jira options.
 *
 * @version File version: $Revision: 1155 $, $Date: 2010-09-25 10:45:37 +0700
 *          (Sat, 25 Sep 2010) $
 * @author Last change: $Author: cnitsa $
 */
class JiraConfigTab extends ConfigurationPanel {
  JiraConfigTab(LayoutManager layoutManager) {
    super(layoutManager);
    createPanel();
  }

  public String getTitle() {
    return timeSlotTracker.getString("configuration.tab.jiraConfig.title");
  }

  private void createPanel() {
    addCoreCheckBox(Configuration.JIRA_ENABLED);
    addCoreCombo(Configuration.JIRA_VERSION,
        Arrays.asList(
            new StringConfigValue("Jira 6+", JiraTracker.JIRA_VERSION_6),
            new StringConfigValue("Jira 3.10+", JiraTracker.JIRA_VERSION_310),
            new StringConfigValue("Jira 3.x", JiraTracker.JIRA_VERSION_3)));
    addCoreLine(Configuration.JIRA_URL);
    addCoreLine(Configuration.JIRA_LOGIN);
    addCoreLine(Configuration.JIRA_PASSWORD, true);
    addCoreLine(Configuration.JIRA_FILTER);
  }

}
