package net.sf.timeslottracker.gui.browser.calendar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Renderer for month array.
 * 
 * File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700 (Sat, 16 May
 * 2009) $ Last change: $Author: cnitsa $
 */

class TableCellRenderer extends DefaultTableCellRenderer {
  private Calendar day;
  private Font normalFont;
  private Font boldFont;
  private Color normalForeground;
  private Color normalBackground;
  private Border border = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);

  public TableCellRenderer(Calendar day) {
    super();
    this.day = day;
  }

  public TableCellRenderer() {
    this(Calendar.getInstance());
  }

  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {
    super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
        row, column);

    if (normalFont == null) {
      normalFont = getFont();
    }
    if (boldFont == null) {
      boldFont = normalFont.deriveFont(Font.BOLD);
    }
    if (normalForeground == null) {
      normalForeground = getForeground();
    }
    if (normalBackground == null) {
      normalBackground = getBackground();
    }

    TableModel tableModel = (TableModel) table.getModel();
    Calendar monthDay = tableModel.getDayAt(2, 3); // get date from the middle
    Calendar cellDay = tableModel.getDayAt(row, column);
    Calendar today = Calendar.getInstance();
    Calendar selectedDay = tableModel.getDate();

    setForeground(normalForeground);
    setBackground(normalBackground);
    setFont(normalFont);

    // red colour for saturday, sunday - weekend
    if (cellDay.get(Calendar.DAY_OF_WEEK) == 1
        || cellDay.get(Calendar.DAY_OF_WEEK) == 7) {
      setForeground(Color.RED);
      // setFont(fontBold);
    }

    // light colour for days out of month
    if (cellDay.get(Calendar.MONTH) != monthDay.get(Calendar.MONTH)) {
      setForeground(Color.LIGHT_GRAY);
    }

    // bold for selected day - value "equal" day
    if ((cellDay.get(Calendar.DAY_OF_YEAR) == day.get(Calendar.DAY_OF_YEAR))
        && (cellDay.get(Calendar.YEAR) == day.get(Calendar.YEAR))) {
      setForeground(Color.BLUE);
      setFont(boldFont);
    }

    // blue background for currently selected day
    if ((cellDay.get(Calendar.DAY_OF_YEAR) == selectedDay
        .get(Calendar.DAY_OF_YEAR))
        && (cellDay.get(Calendar.YEAR) == selectedDay.get(Calendar.YEAR))) {
      setBackground(Color.BLUE);
      setForeground(Color.WHITE);
    }

    // dark backgroud for today - value "equal" today
    if ((cellDay.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR))
        && (cellDay.get(Calendar.YEAR) == today.get(Calendar.YEAR))) {
      setFont(boldFont);
      if (!hasFocus) {
        setBorder(border);
      }
    }

    return this;
  }
}
