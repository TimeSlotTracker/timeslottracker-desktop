package net.sf.timeslottracker.gui.configuration;

import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.gui.LayoutManager;

/**
 * Class with configuration panel for Customization options.
 * 
 * @version File version: $Revision: 1104 $, $Date: 2009-07-06 20:43:56 +0700
 *          (Mon, 06 Jul 2009) $
 * @author Last change: $Author: cnitsa $
 */
@SuppressWarnings("serial")
class CustomizationConfigTab extends ConfigurationPanel {
  CustomizationConfigTab(LayoutManager layoutManager) {
    super(layoutManager);
    createPanel();
  }

  public String getTitle() {
    return timeSlotTracker.getString("configuration.tab.customConfig.title");
  }

  private void createPanel() {
    addCoreCheckBox(Configuration.CUSTOM_MINIMIZE_WINDOW_AFTER_START);
    addCoreCheckBox(Configuration.CUSTOM_SHOW_MESSAGE_AFTER_CANCEL_TASK);
    addCoreCheckBox(Configuration.CUSTOM_SHOW_TASK_BY_DAYS_SUMMARY);
    addCoreCheckBox(Configuration.CUSTOM_USE_MAC_SYSTEM_TRAY_ICONS);
  }
}
