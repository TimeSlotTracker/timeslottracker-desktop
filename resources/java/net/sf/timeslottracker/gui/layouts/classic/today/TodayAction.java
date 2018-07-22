package net.sf.timeslottracker.gui.layouts.classic.today;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import net.sf.timeslottracker.gui.LayoutManager;

/**
 * Action for daily
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
@SuppressWarnings("serial")
public final class TodayAction extends AbstractAction {

  private final LayoutManager layoutManager;

  public TodayAction(LayoutManager layoutManager, String name) {
    super(name);
    this.layoutManager = layoutManager;

    putValue(AbstractAction.ACCELERATOR_KEY,
        KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
    putValue(AbstractAction.MNEMONIC_KEY, java.awt.event.KeyEvent.VK_D);
  }

  public void actionPerformed(ActionEvent ae) {
    new TodayImpl(layoutManager).show();
  }
}