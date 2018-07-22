package net.sf.timeslottracker.gui;

import java.awt.Component;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

/**
 * Cell renderer for multiline column.
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class TextAreaRenderer extends JTextArea implements TableCellRenderer {

  protected final static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
  protected final static Border focusBorder = UIManager
      .getBorder("Table.focusCellHighlighBorder");

  public TextAreaRenderer() {
    this(true);
  }

  public TextAreaRenderer(boolean lineWrap) {
    setEditable(false);
    setLineWrap(lineWrap);
    setWrapStyleWord(lineWrap);
    setBorder(noFocusBorder);
  }

  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {
    setBackground(isSelected ? table.getSelectionBackground() : table
        .getBackground());
    setForeground(isSelected ? table.getSelectionForeground() : table
        .getForeground());
    setFont(table.getFont());
    setBorder(hasFocus ? focusBorder : noFocusBorder);
    String stringValue;
    if (value == null) {
      stringValue = "";
    } else if (value instanceof String) {
      stringValue = String.class.cast(value);
    } else {
      stringValue = value.toString();
    }

    setText(stringValue);

    // set the row height
    int width = table.getColumnModel().getColumn(column).getWidth();
    setSize(width, 1000);
    int rowHeight = getPreferredSize().height;
    if (table.getRowHeight(row) != rowHeight) {
      table.setRowHeight(row, rowHeight);
    }

    return this;
  }

  public String getToolTipText(MouseEvent event) {
    return null;
  }

}
