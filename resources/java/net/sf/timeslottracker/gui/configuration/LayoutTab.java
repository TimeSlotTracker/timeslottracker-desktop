package net.sf.timeslottracker.gui.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.gui.LayoutManager;

/**
 * Class with configuration panel for Layout options.
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
class LayoutTab extends ConfigurationPanel {

  LayoutTab(LayoutManager layoutManager) {
    super(layoutManager);
    createPanel();
  }

  public String getTitle() {
    return timeSlotTracker.getString("configuration.tab.layout.title");
  }

  private void createPanel() {
    // getting installed LookAndFeels
    List<ConfigValue> configValues = new ArrayList<ConfigValue>();
    for (UIManager.LookAndFeelInfo installedLookAndFeel : UIManager
        .getInstalledLookAndFeels()) {
      configValues.add(new StringConfigValue(installedLookAndFeel.getName(),
          installedLookAndFeel.getClassName()));
    }

    // auto add jgoodies look and feels
    String[] jgoodiesClasses = new String[] {
        "com.jgoodies.looks.windows.WindowsLookAndFeel",
        "com.jgoodies.looks.plastic.PlasticLookAndFeel",
        "com.jgoodies.looks.plastic.Plastic3DLookAndFeel",
        "com.jgoodies.looks.plastic.PlasticXPLookAndFeel" };
    for (String jgoodiesClass : jgoodiesClasses) {
      String name = getLFName(jgoodiesClass);
      if (name != null) {
        configValues.add(new StringConfigValue(jgoodiesClass
            .contains("WindowsLookAndFeel") ? name + " (Windows only)" : name,
            jgoodiesClass));
      }
    }

    // auto add winLAF look and feel
    String winLAFClass = "net.java.plaf.windows.WindowsLookAndFeel";
    String lfName = getLFName(winLAFClass);
    if (lfName != null) {
      configValues.add(new StringConfigValue(lfName, winLAFClass));
    }

    // adding fields
    addCoreCombo(Configuration.LOOK_AND_FEEL_CLASS, configValues);
    addCoreLine(Configuration.LAYOUTMANAGER_CLASS);
    addCoreLine(Configuration.TASKINFO_REFRESH_TIMEOUT);
    addCoreLine(Configuration.APP_TITLE_TEMPLATE_PASSIVE, true);
    addCoreLine(Configuration.APP_TITLE_TEMPLATE_ACTIVE, true);
  }

  private String getLFName(String className) {
    try {
      LookAndFeel lookAndFeel = (LookAndFeel) Class.forName(className)
          .newInstance();
      return lookAndFeel.getName();
    } catch (InstantiationException e) {
      // nothing to do
    } catch (IllegalAccessException e) {
      // nothing to do
    } catch (ClassNotFoundException e) {
      // nothing to do
    }
    return null;
  }
}
