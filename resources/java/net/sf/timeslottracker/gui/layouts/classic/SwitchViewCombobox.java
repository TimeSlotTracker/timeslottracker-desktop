package net.sf.timeslottracker.gui.layouts.classic;

import java.awt.Font;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import net.sf.timeslottracker.gui.LayoutManager;

/**
 * Combo box for switching task tree view
 * 
 * @version File version: $Revision: 1.23 $, $Date: 2007/04/07 03:59:30 $
 * @author Last change: $Author: cnitsa $
 */
public class SwitchViewCombobox extends JComboBox {

  private final LayoutManager layoutManager;

  public SwitchViewCombobox(LayoutManager layoutManager) {
    this.layoutManager = layoutManager;

    setModel(new DefaultComboBoxModel(new String[] {
        layoutManager.getString("taskstree.title"),
        layoutManager.getString("daystree.title") }));

    setFont(getFont().deriveFont(Font.BOLD));

  }

}