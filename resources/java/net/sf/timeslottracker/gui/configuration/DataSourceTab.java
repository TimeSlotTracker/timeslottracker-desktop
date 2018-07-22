package net.sf.timeslottracker.gui.configuration;

import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.gui.LayoutManager;

/**
 * Class with configuration panel for DataSource options.
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
class DataSourceTab extends ConfigurationPanel {

  DataSourceTab(LayoutManager layoutManager) {
    super(layoutManager);
    createPanel();
  }

  public String getTitle() {
    return timeSlotTracker.getString("configuration.tab.dataSource.title");
  }

  private void createPanel() {
    addCoreLine(Configuration.DATASOURCE_CLASS);
    addCoreCheckBox(Configuration.DATASOURCE_DIRECTORY_CURRENT_FOLDER,
        Boolean.FALSE);
    addCoreLine(Configuration.DATASOURCE_DIRECTORY);
    addCoreLine(Configuration.DATASOURCE_AUTOSAVE_TIMEOUT);
  }
}
