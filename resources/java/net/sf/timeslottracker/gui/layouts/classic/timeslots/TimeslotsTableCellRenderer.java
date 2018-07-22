package net.sf.timeslottracker.gui.layouts.classic.timeslots;

import java.awt.Font;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.gui.LayoutManager;

/**
 * Custom CellRenderer for TimeslotsTableModel class
 * 
 * File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700 (Sat, 16 May
 * 2009) $ Last change: $Author: cnitsa $
 */
@SuppressWarnings("serial")
class TimeslotsTableCellRenderer extends DefaultTableCellRenderer {

  private Font inactiveTimeSlotFont;

  private Font activeTimeSlotFont;

  private LayoutManager layoutManager;

  private TimeslotsTableModel timeslotsTableModel;

  TimeslotsTableCellRenderer(LayoutManager layoutManager,
      TimeslotsTableModel timeslotsTableModel) {
    super();
    this.layoutManager = layoutManager;
    this.timeslotsTableModel = timeslotsTableModel;
  }

  public java.awt.Component getTableCellRendererComponent(JTable table,
      Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
        row, column);
    if (inactiveTimeSlotFont == null) {
      inactiveTimeSlotFont = getFont();
      activeTimeSlotFont = inactiveTimeSlotFont.deriveFont(Font.BOLD);
    }

    TimeSlot timeSlot = timeslotsTableModel.getValueAt(row);
    TimeSlot activeTimeSlot = layoutManager.getTimeSlotTracker()
        .getActiveTimeSlot();

    if (activeTimeSlot != null && timeSlot.equals(activeTimeSlot)) {
      setFont(activeTimeSlotFont);
    } else {
      setFont(inactiveTimeSlotFont);
    }

    return this;
  }

}
