package net.sf.timeslottracker.gui.configuration;

import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.gui.LayoutManager;

/**
 * Class with configuration panel for check new version options.
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
@SuppressWarnings("serial")
class CheckNewVersionConfigTab extends ConfigurationPanel {
  CheckNewVersionConfigTab(LayoutManager layoutManager) {
    super(layoutManager);
    createPanel();
  }

  public String getTitle() {
    return timeSlotTracker.getString("configuration.tab.checkNewVersion.title");
  }

  private void createPanel() {
    addCoreCheckBox(Configuration.CHECK_NEW_VERSION_ENABLED, true);
    addCoreLine(Configuration.CHECK_NEW_VERSION_DAYS);
  }
}
