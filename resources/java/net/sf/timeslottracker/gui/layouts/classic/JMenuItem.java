package net.sf.timeslottracker.gui.layouts.classic;

import java.awt.Font;

import javax.swing.Action;

/**
 * Extends the <code>javax.swing.JMenuItem</code> to set the font to plain.
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class JMenuItem extends javax.swing.JMenuItem {
  private static Font plainFont;

  public JMenuItem(Action action) {
    super(action);
    setPlainFont();
  }

  public JMenuItem(boolean setPlainFont) {
    super();
    if (setPlainFont) {
      setPlainFont();
    }
  }

  public JMenuItem(String text, boolean setPlainFont) {
    super(text);
    if (setPlainFont) {
      setPlainFont();
    }
  }

  private void setPlainFont() {
    if (plainFont == null) {
      plainFont = getFont().deriveFont(Font.PLAIN);
    }
    setFont(plainFont);
  }
}
