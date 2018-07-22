package net.sf.timeslottracker.gui.configuration;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import net.sf.timeslottracker.core.Action;
import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.core.TimeSlotTracker;
import net.sf.timeslottracker.gui.LayoutManager;

/**
 * A class witch contains tabs with configuration panels.
 * 
 * @see ConfigurationPanel
 * 
 * @version File version: $Revision: 1128 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
@SuppressWarnings("serial")
public class ConfigurationWindow extends JDialog {
  private final LayoutManager layoutManager;

  private final Collection<ConfigurationPanel> panels;

  private JSplitPane mainPane;

  private final TimeSlotTracker timeSlotTracker;

  public ConfigurationWindow(LayoutManager layoutManager) {
    super(layoutManager.getTimeSlotTracker().getRootFrame(), layoutManager
        .getCoreString("configuration.window.title"), true);
    this.layoutManager = layoutManager;
    this.timeSlotTracker = layoutManager.getTimeSlotTracker();
    panels = new Vector<ConfigurationPanel>();
    createWindow();
    pack();
    setSize(750, 300);
    setLocationRelativeTo(getRootPane());
    mainPane.setDividerLocation(0.25f);
    setVisible(true);
  }

  private void createWindow() {
    getContentPane().setLayout(new BorderLayout());

    panels.add(new GeneralTab(layoutManager));
    panels.add(new DataSourceTab(layoutManager));
    panels.add(new BackupTab(layoutManager));
    panels.add(new MonitoringConfigTab(layoutManager));
    panels.add(new UserIdleDetectorConfigTab(layoutManager));
    panels.add(new LayoutTab(layoutManager));
    panels.add(new TrayIconConfigTab(layoutManager));
    panels.add(new ConfirmationsConfigTab(layoutManager));
    panels.add(new CustomizationConfigTab(layoutManager));
    panels.add(new JiraConfigTab(layoutManager));
    panels.add(new CheckNewVersionConfigTab(layoutManager));

    final JPanel propertiesPanel = new JPanel(new BorderLayout());
    propertiesPanel.add(
        new JLabel(layoutManager.getCoreString("configuration.window.title")),
        BorderLayout.CENTER);

    JTree jTree = new JTree(new Vector<ConfigurationPanel>(panels));
    jTree.setEditable(false);
    jTree.setCellRenderer(new DefaultTreeCellRenderer() {
      public Component getTreeCellRendererComponent(JTree tree, Object value,
          boolean selected, boolean expanded, boolean leaf, int row,
          boolean hasFocus) {
        Component cellRendererComponent = super.getTreeCellRendererComponent(
            tree, value, selected, expanded, leaf, row, hasFocus);
        setIcon(null);
        return cellRendererComponent;
      }
    });
    jTree.addTreeSelectionListener(new TreeSelectionListener() {
      public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath()
            .getLastPathComponent();
        propertiesPanel.removeAll();
        propertiesPanel.add((JComponent) node.getUserObject(),
            BorderLayout.CENTER);
        propertiesPanel.validate();
        propertiesPanel.repaint();
      }
    });
    jTree.getSelectionModel().setSelectionPath(jTree.getPathForRow(0));

    mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(
        jTree), propertiesPanel);
    getContentPane().add(mainPane, BorderLayout.CENTER);

    ButtonsPanel buttonsPanel = new ButtonsPanel(layoutManager, this);
    getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
  }

  /**
   * Closes the configuration window. Doesn't perform any saving. Just closes
   * the window.
   */
  void closeWindow() {
    dispose();
  }

  /**
   * Saves changes into configuration file and then closes the window.
   */
  void saveChanges() {
    Iterator<ConfigurationPanel> tabs = panels.iterator();
    while (tabs.hasNext()) {
      ConfigurationPanel tab = tabs.next();
      tab.save();
    }
    Configuration configuration = layoutManager.getTimeSlotTracker()
        .getConfiguration();
    configuration.save();

    // sending event about configuration changes
    layoutManager.getTimeSlotTracker().fireAction(
        new Action(Action.ACTION_CONFIGURATION_CHANGED, this, null));

    closeWindow();
  }

  public boolean verifyConfiguration() {
    Iterator<ConfigurationPanel> tabs = panels.iterator();
    boolean verified = true;
    while (tabs.hasNext() && verified) {
      ConfigurationPanel tab = tabs.next();
      verified = tab.verify();
    }
    if (!verified) {
      String title = timeSlotTracker
          .getString("configuration.window.verify.error.title");
      String msg = timeSlotTracker
          .getString("configuration.window.verify.error.msg");
      JOptionPane
          .showMessageDialog(this, msg, title, JOptionPane.ERROR_MESSAGE);
    }
    return verified;
  }
}
