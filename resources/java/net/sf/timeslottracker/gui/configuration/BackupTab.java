package net.sf.timeslottracker.gui.configuration;

import java.io.File;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.gui.LayoutManager;

/**
 * Class with configuration panel for General options.
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
@SuppressWarnings("serial")
class BackupTab extends ConfigurationPanel {

  BackupTab(LayoutManager layoutManager) {
    super(layoutManager);
    createPanel();
  }

  public String getTitle() {
    return timeSlotTracker.getString("configuration.tab.backup.title");
  }

  private void createPanel() {
    addCoreCheckBox(Configuration.BACKUP_ON_STARTUP);
    addCoreCheckBox(Configuration.BACKUP_ON_SHUTDOWN);

    addCoreLine(Configuration.BACKUP_DIRECTORY);
  }

  /**
   * Check if backup directory exists.
   * 
   * @return <code>true</code> if directory exists or user choose to create one
   *         and it is done.
   */
  @Override
  protected boolean verify() {
    // check if any backup checkbox is checked
    boolean checkDirectory;

    Object objectBox = properties.get(Configuration.BACKUP_ON_STARTUP);
    JCheckBox box = (JCheckBox) objectBox;
    checkDirectory = box.isSelected();

    objectBox = properties.get(Configuration.BACKUP_ON_SHUTDOWN);
    box = (JCheckBox) objectBox;
    checkDirectory = checkDirectory || box.isSelected();

    if (!checkDirectory) {
      return true;
    }

    Object objectField = properties.get(Configuration.BACKUP_DIRECTORY);
    JTextField field = (JTextField) objectField;
    String directory = field.getText();
    File folderToCheck = new File(directory);
    if (folderToCheck.exists()) {
      return true;
    }

    // try to create one (if user agree to do that).
    String title = timeSlotTracker
        .getString("configuration.property.backup.directory.notFound.title");
    Object[] args = new Object[] { directory };
    String msg = timeSlotTracker.getString(
        "configuration.property.backup.directory.notFound.msg", args);
    int answer = JOptionPane.showConfirmDialog(this, msg, title,
        JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
    boolean dirCreated = false;
    if (answer == JOptionPane.YES_OPTION) {
      try {
        dirCreated = folderToCheck.mkdirs();
      } catch (Exception e) {
        timeSlotTracker
            .errorLog("Exception during creating new directory for backup: "
                + e.getMessage());
        timeSlotTracker.errorLog(e);
        dirCreated = false;
      }
      if (!dirCreated) {
        title = timeSlotTracker
            .getString("configuration.property.backup.directory.create.failed.title");
        msg = timeSlotTracker.getString(
            "configuration.property.backup.directory.create.failed.msg", args);
        JOptionPane.showMessageDialog(this, msg, title,
            JOptionPane.ERROR_MESSAGE);
      }
    }
    return dirCreated;
  }
}
