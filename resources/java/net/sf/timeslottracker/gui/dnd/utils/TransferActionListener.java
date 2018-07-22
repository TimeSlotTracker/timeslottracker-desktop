package net.sf.timeslottracker.gui.dnd.utils;

import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JComponent;

/**
 * Used for redirect copy/cut/paste keyboard actions to corresponding gui
 * component
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class TransferActionListener implements ActionListener,
    PropertyChangeListener {
  private JComponent focusOwner;

  public TransferActionListener() {
    KeyboardFocusManager manager = KeyboardFocusManager
        .getCurrentKeyboardFocusManager();
    manager.addPropertyChangeListener("permanentFocusOwner", this);
  }

  public void propertyChange(PropertyChangeEvent e) {
    Object o = e.getNewValue();
    if (o instanceof JComponent) {
      focusOwner = (JComponent) o;
    } else {
      focusOwner = null;
    }
  }

  public void actionPerformed(ActionEvent e) {
    if (focusOwner == null) {
      return;
    }

    String action = e.getActionCommand();
    Action a = focusOwner.getActionMap().get(action);
    if (a != null) {
      a.actionPerformed(new ActionEvent(focusOwner,
          ActionEvent.ACTION_PERFORMED, null));
    }
  }
}
