package net.sf.timeslottracker.gui.configuration;

import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.gui.LayoutManager;

/**
 * Class with configuration panel for Confirmation options.
 *
 * @version File version: $Revision: 1128 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
class ConfirmationsConfigTab extends ConfigurationPanel {
  ConfirmationsConfigTab(LayoutManager layoutManager) {
    super(layoutManager);
    createPanel();
  }

  public String getTitle() {
    return timeSlotTracker.getString("configuration.tab.confirmations.title");
  }

  private void createPanel() {
    addCoreCheckBox(Configuration.CONFIRMATION_PREVIOUS_TIMESLOT_EXISTS,
        Boolean.TRUE);
    addCoreCheckBox(
        Configuration.CONFIRMATION_SHOW_TASK_HAS_JUST_STARTED_MESSAGE,
        Boolean.TRUE);
    addCoreCheckBox(
        Configuration.CONFIRMATION_SHOW_DIALOG_FOR_CUSTOM_RESTART_TIME,
        Boolean.TRUE);
  }
}
