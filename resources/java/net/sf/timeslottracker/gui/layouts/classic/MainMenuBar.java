package net.sf.timeslottracker.gui.layouts.classic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import net.sf.timeslottracker.gui.AboutDialog;
import net.sf.timeslottracker.gui.FileContentDialog;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.NewVersionDialog;
import net.sf.timeslottracker.gui.actions.AddTaskAction;
import net.sf.timeslottracker.gui.actions.ExportToICALAction;
import net.sf.timeslottracker.gui.actions.ImportFromCSVAction;
import net.sf.timeslottracker.gui.actions.RemoveDataAction;
import net.sf.timeslottracker.gui.actions.SaveDataAction;
import net.sf.timeslottracker.gui.attributes.AttributeTypesWindow;
import net.sf.timeslottracker.gui.configuration.ConfigurationWindow;
import net.sf.timeslottracker.gui.layouts.classic.today.TodayAction;
import net.sf.timeslottracker.gui.reports.ReportsHelper;
import net.sf.timeslottracker.updateversion.VersionInfo;
import net.sf.timeslottracker.updateversion.VersionManager;
import net.sf.timeslottracker.utils.SwingUtils;

/**
 * MainMenuBar for the application
 * 
 * @version File version: $Revision: 1175 $, $Date: 2009-05-16 09:00:38 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
@SuppressWarnings("serial")
public class MainMenuBar extends JMenuBar {

  private final JMenu viewMenu;

  private final LayoutManager layoutManager;

  private List<JMenuItem> layoutMenuItems;

  public MainMenuBar(LayoutManager layoutManager) {
    this.layoutManager = layoutManager;

    add(createFileMenu());
    viewMenu = createViewMenu();
    add(viewMenu);
    add(createReportsMenu());
    add(createConfigurationMenu());
    add(createWindowMenu());
    add(createHelpMenu());
  }

  /**
   * Update view menu with given menu items
   */
  void updateViewMenu(Collection<JMenuItem> menuItems) {
    viewMenu.removeAll();

    // add layout menu items
    if (layoutMenuItems != null) {
      for (JMenuItem item : layoutMenuItems) {
        viewMenu.add(item);
      }
    }
    viewMenu.addSeparator();

    // add additional menu items
    for (JMenuItem menuItem : menuItems) {
      viewMenu.add(menuItem);
    }

    // hide menu if no items in it
    viewMenu.setVisible(!menuItems.isEmpty());
  }

  private JMenu createFileMenu() {
    JMenu fileMenu = new JMenu(layoutManager.getString("menuBar.item.File"));
    fileMenu.setMnemonic(java.awt.event.KeyEvent.VK_F);

    JMenuItem addTaskAction = new JMenuItem(new AddTaskAction(layoutManager));
    fileMenu.add(addTaskAction);

    fileMenu.addSeparator();

    JMenuItem saveDataAction = new JMenuItem(new SaveDataAction(layoutManager));
    fileMenu.add(saveDataAction);

    JMenuItem removeDataAction = new JMenuItem(new RemoveDataAction(
        layoutManager));
    fileMenu.add(removeDataAction);

    fileMenu.addSeparator();

    JMenu importMenu = new JMenu(
        layoutManager.getString("menuBar.item.File.Import"));
    importMenu.setIcon(layoutManager.getIcon("import"));
    importMenu.setMnemonic(java.awt.event.KeyEvent.VK_I);
    importMenu.add(new JMenuItem(new ImportFromCSVAction(layoutManager)));
    fileMenu.add(importMenu);

    JMenu exportMenu = new JMenu(
        layoutManager.getString("menuBar.item.File.Export"));
    exportMenu.setIcon(layoutManager.getIcon("export"));
    exportMenu.setMnemonic(java.awt.event.KeyEvent.VK_E);
    exportMenu.add(new JMenuItem(new ExportToICALAction(layoutManager)));
    fileMenu.add(exportMenu);

    fileMenu.addSeparator();

    JMenuItem itemQuit = new JMenuItem(
        layoutManager.getString("menuBar.item.QuitApp"));
    itemQuit.setIcon(layoutManager.getIcon("exit"));
    itemQuit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        layoutManager.getTimeSlotTracker().quit();
      }
    });
    fileMenu.add(itemQuit);

    return fileMenu;
  }

  private JMenu createConfigurationMenu() {
    JMenu configurationMenu = new JMenu(
        layoutManager.getString("menuBar.item.Configuration"));
    configurationMenu.setMnemonic(java.awt.event.KeyEvent.VK_C);

    String itemName = layoutManager
        .getCoreString("configuration.window.menuName");
    JMenuItem itemConfiguration = new JMenuItem(itemName);
    itemConfiguration.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        new ConfigurationWindow(layoutManager);
      }
    });
    itemConfiguration.setIcon(layoutManager.getIcon("configuration"));
    configurationMenu.add(itemConfiguration);

    itemName = layoutManager.getCoreString("attributes.window.menuName");
    JMenuItem itemAttributes = new JMenuItem(itemName);
    itemAttributes.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        new AttributeTypesWindow(layoutManager);
      }
    });
    configurationMenu.add(itemAttributes);

    return configurationMenu;
  }

  private JMenu createHelpMenu() {

    JMenu helpMenu = new JMenu(layoutManager.getString("menuBar.item.Help"));
    helpMenu.setMnemonic(java.awt.event.KeyEvent.VK_H);

    JMenuItem helpDocItem = new JMenuItem(
        layoutManager.getString("menuBar.item.Help.Docs"));
    helpDocItem.setMnemonic(java.awt.event.KeyEvent.VK_E);
    helpDocItem.setIcon(layoutManager.getIcon("openurl"));
    helpDocItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        try {
          SwingUtils.browse(new URI(
              "https://sourceforge.net/p/timeslottracker/wiki/Documentation/"));
        } catch (URISyntaxException e1) {
          layoutManager.getTimeSlotTracker().errorLog(e1);
        }
      }
    });

    JMenuItem updateItem = new JMenuItem(
        layoutManager.getString("menuBar.item.Help.Update"));
    updateItem.setMnemonic(java.awt.event.KeyEvent.VK_U);
    updateItem.setIcon(layoutManager.getIcon("refresh"));
    updateItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {

        VersionManager versionManager = new VersionManager(layoutManager
            .getTimeSlotTracker());
        VersionInfo lastVersion = versionManager.getLastVersion();
        if (lastVersion == null) {
          JOptionPane.showMessageDialog(layoutManager.getGUIComponent(),
              layoutManager.getCoreString("checkUpdates.loadError.message"),
              layoutManager.getCoreString("alert.error.title"),
              JOptionPane.ERROR_MESSAGE);
          return;
        }

        if (lastVersion.isNewVersionAvailable()) {
          new NewVersionDialog(layoutManager, lastVersion).activate();
          return;
        }
        JOptionPane.showMessageDialog(layoutManager.getGUIComponent(),
            layoutManager.getCoreString("checkUpdates.noNewVersion.message"),
            layoutManager
                .getCoreString("checkUpdates.noNewVersion.message.title"),
            JOptionPane.INFORMATION_MESSAGE);
      }
    });

    JMenuItem changelogItem = new JMenuItem(
        layoutManager.getString("menuBar.item.Help.Changelog"));
    changelogItem.setMnemonic(java.awt.event.KeyEvent.VK_C);
    changelogItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        new FileContentDialog(layoutManager, "changelogDialog.title",
            "/ChangeLog").activate();
      }
    });

    JMenuItem issuesItem = new JMenuItem(
        layoutManager.getString("menuBar.item.Help.Issues"));
    issuesItem.setMnemonic(java.awt.event.KeyEvent.VK_I);
    issuesItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        new FileContentDialog(layoutManager, "issuesDialog.title",
            "/KnownIssues").activate();
      }
    });

    JMenuItem aboutItem = new JMenuItem(
        layoutManager.getString("menuBar.item.Help.About"));
    aboutItem.setMnemonic(java.awt.event.KeyEvent.VK_A);
    aboutItem.setIcon(layoutManager.getIcon("about"));
    aboutItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        new AboutDialog(layoutManager).activate();
      }
    });

    helpMenu.add(helpDocItem);
    helpMenu.add(changelogItem);
    helpMenu.add(issuesItem);
    helpMenu.addSeparator();
    helpMenu.add(updateItem);
    helpMenu.addSeparator();
    helpMenu.add(aboutItem);

    return helpMenu;
  }

  private JMenu createReportsMenu() {
    JMenu reportsMenu = ReportsHelper.getReportMenu(false, null);
    reportsMenu.setText(layoutManager.getString("menuBar.item.Reports"));
    reportsMenu.setMnemonic(java.awt.event.KeyEvent.VK_R);
    return reportsMenu;
  }

  private JMenu createViewMenu() {
    JMenu viewMenu = new JMenu(layoutManager.getString("menuBar.item.View"));
    viewMenu.setText(layoutManager.getString("menuBar.item.View"));
    viewMenu.setMnemonic(java.awt.event.KeyEvent.VK_V);
    return viewMenu;
  }

  private JMenu createWindowMenu() {
    JMenu dailyMenu = new JMenu(layoutManager.getString("menuBar.item.Windows"));
    dailyMenu.setMnemonic(java.awt.event.KeyEvent.VK_D);
    JMenuItem menuItem = new JMenuItem(new TodayAction(layoutManager,
        layoutManager.getString("menuBar.item.Windows.Today")));
    menuItem.setIcon(layoutManager.getIcon("calendar"));
    dailyMenu.add(menuItem);

    return dailyMenu;
  }

  /**
   * Sets layout menu items
   */
  void setLayoutMenu(List<JMenuItem> layoutMenuItems) {
    this.layoutMenuItems = layoutMenuItems;
  }

}
