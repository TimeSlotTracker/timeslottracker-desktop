package net.sf.timeslottracker.gui;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

public class SelectAllOnFocusListener implements FocusListener {
  // selectAll
  public void focusGained(FocusEvent e) {
    if (!e.isTemporary()) {
      if (e.getComponent() instanceof JTextField) {
        JTextField textField = (JTextField) e.getComponent();
        textField.selectAll();
      }
    }
  }

  // deselect
  public void focusLost(FocusEvent e) {
    if (!e.isTemporary()) {
      if (e.getComponent() instanceof JTextField) {
        JTextField textField = (JTextField) e.getComponent();
        textField.select(0, 0);
      }
    }
  }

}
