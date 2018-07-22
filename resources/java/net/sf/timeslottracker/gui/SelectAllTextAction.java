package net.sf.timeslottracker.gui;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

/**
 * Class called when a user focused a textField description.
 * 
 */
public class SelectAllTextAction implements FocusListener {

  // implements FocusListener
  public void focusGained(FocusEvent e) {
    if (!e.isTemporary()) {
      if (e.getComponent() instanceof JTextField) {
        JTextField textField = (JTextField) e.getComponent();
        textField.selectAll();
      }
    }
  }

  public void focusLost(FocusEvent e) {
    if (!e.isTemporary()) {
      if (e.getComponent() instanceof JTextField) {
        JTextField textField = (JTextField) e.getComponent();
        // deselectAll
        textField.select(0, 0);
      }
    }
  }
}
