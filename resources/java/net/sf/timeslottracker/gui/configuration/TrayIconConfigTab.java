package net.sf.timeslottracker.gui.configuration;

import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.gui.LayoutManager;

/**
 * Class with configuration panel for tray icon options.
 * 
 * @version File version: $Revision: 998 $, $Date: 2010-07-11 09:54:18 +0700
 *          (Sun, 11 Jul 2010) $
 * @author Last change: $Author: cnitsa $
 */
class TrayIconConfigTab extends ConfigurationPanel {
  TrayIconConfigTab(LayoutManager layoutManager) {
    super(layoutManager);
    createPanel();
  }

  public String getTitle() {
    return timeSlotTracker.getString("configuration.tab.trayIconConfig.title");
  }

  private void createPanel() {
    addCoreCheckBox(Configuration.TRAY_ICON_ENABLED, true);
    addCoreCheckBox(Configuration.TRAY_ICON_MINIMIZE, true);
    addCoreCheckBox(Configuration.TRAY_ICON_CLOSING_SHOULD_MINIMIZE, true);
    addCoreCheckBox(Configuration.TIP_OF_THE_DAY_ENABLED, true);
    addCoreLine(Configuration.TIP_OF_THE_DAY_MINUTES_REPEAT, true);
    addCoreCheckBox(Configuration.TRAY_ICON_MAC_SHORTCUTS, false);
  }
}
