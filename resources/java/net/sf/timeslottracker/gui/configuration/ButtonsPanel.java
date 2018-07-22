package net.sf.timeslottracker.gui.configuration;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.sf.timeslottracker.core.TimeSlotTracker;
import net.sf.timeslottracker.gui.LayoutManager;

/**
 * A panel with buttons for saving or discarding changes made in configuration
 * tabs.
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-06-21 18:47:38 +0700
 *          (Sun, 21 Jun 2009) $
 * @author Last change: $Author: cnitsa $
 */
class ButtonsPanel extends JPanel {

  private LayoutManager layoutManager;
  private TimeSlotTracker timeSlotTracker;
  private JButton buttonSave;
  private JButton buttonCancel;
  private ConfigurationWindow configurationWindow;

  ButtonsPanel(LayoutManager layoutManager,
      ConfigurationWindow configurationWindow) {
    super(new FlowLayout(FlowLayout.RIGHT, 10, 5));
    this.layoutManager = layoutManager;
    this.configurationWindow = configurationWindow;

    buttonCancel = new JButton(
        layoutManager.getCoreString("configuration.window.button.cancel.label"),
        layoutManager.getIcon("cancel"));
    buttonCancel.addActionListener(new CancelAction());

    buttonSave = new JButton(
        layoutManager.getCoreString("configuration.window.button.save.label"),
        layoutManager.getIcon("save"));
    buttonSave.addActionListener(new SaveAction());

    add(buttonCancel);
    add(buttonSave);
  }

  /**
   * Action used when a user chooses cancel button.
   */
  private class CancelAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      configurationWindow.closeWindow();
    }
  }

  /**
   * Action used when a user chooses save button.
   */
  private class SaveAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      if (configurationWindow.verifyConfiguration()) {
        configurationWindow.saveChanges();
      }
    }
  }

}
