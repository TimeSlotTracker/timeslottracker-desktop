package net.sf.timeslottracker.gui;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * A class describing one column in table.
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class Column {
  private String name;

  private int width;

  private int align;

  private Class columnClass;

  private TableCellRenderer cellRenderer;

  private TableCellEditor cellEditor;

  public Column(String name, int width, int align) {
    this(name, width, align, null, null, null);
  }

  public Column(String name, int width, int align, Class columnClass,
      TableCellRenderer cellRenderer, TableCellEditor cellEditor) {
    this.name = name;
    this.width = width;
    this.align = align;
    this.columnClass = columnClass;
    this.cellRenderer = cellRenderer;
    if (this.cellRenderer == null && this.columnClass == null) {
      this.cellRenderer = new DefaultTableCellRenderer();
      ((DefaultTableCellRenderer) this.cellRenderer)
          .setHorizontalAlignment(this.align);
    }
    this.cellEditor = cellEditor;
  }

  public String getName() {
    return name;
  }

  public int getWidth() {
    return width;
  }

  public int getAlignment() {
    return align;
  }

  public Class getColumnClass() {
    return columnClass;
  }

  public TableCellRenderer getCellRenderer() {
    return cellRenderer;
  }

  public TableCellEditor getCellEditor() {
    return cellEditor;
  }
}
