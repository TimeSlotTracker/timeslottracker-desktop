package net.sf.timeslottracker.gui.configuration;

import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.gui.LayoutManager;

/**
 * Class with configuration panel for user idle detector
 *
 * @version File version: $Revision: 1.1 $, $Date: 2008/02/02 16:46:31 $
 * @author Last change: $Author: cnitsa $
 */
class UserIdleDetectorConfigTab extends ConfigurationPanel {
  UserIdleDetectorConfigTab(LayoutManager layoutManager) {
    super(layoutManager);
    createPanel();
  }

  public String getTitle() {
    return timeSlotTracker
        .getString("configuration.tab.user.idle.detector.title");
  }

  private void createPanel() {
    addCoreCheckBox(Configuration.USER_IDLE_DETECTOR_ENABLED);
    addCoreLine(Configuration.USER_IDLE_DETECTOR_TIMEOUT, true);
    addLabel(timeSlotTracker
        .getString("configuration.user.idle.detector.help1"));
    addLabel(timeSlotTracker
        .getString("configuration.user.idle.detector.help2"));
  }
}
