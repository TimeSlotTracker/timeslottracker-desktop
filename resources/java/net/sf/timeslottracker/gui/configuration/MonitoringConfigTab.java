package net.sf.timeslottracker.gui.configuration;

import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.gui.LayoutManager;

/**
 * Class with configuration panel for monitoring config options.
 * <p>
 * Based on patch #2545808 from portlex
 * 
 * @version File version: $Revision: 1.1 $, $Date: 2008/02/02 16:46:31 $
 * @author Last change: $Author: cnitsa $
 */
class MonitoringConfigTab extends ConfigurationPanel {
  MonitoringConfigTab(LayoutManager layoutManager) {
    super(layoutManager);
    createPanel();
  }

  public String getTitle() {
    return timeSlotTracker.getString("configuration.tab.monitoring.title");
  }

  private void createPanel() {
    addCoreCheckBox(Configuration.MONITORING_ENABLED);
    addCoreLine(Configuration.MONITORING_INTERVAL);
    addCoreCheckBox(Configuration.MONITORING_GRABBER_ENABLED);
    addCoreLine(Configuration.MONITORING_IMAGE_DIR);
    addCoreLine(Configuration.MONITORING_IMAGE_TIMEOUT_DAYS);
  }
}
