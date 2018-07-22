package net.sf.timeslottracker.gui.attributes;

import java.util.Collection;
import java.util.Locale;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.table.AbstractTableModel;

import net.sf.timeslottracker.data.Attribute;
import net.sf.timeslottracker.data.AttributeCategory;
import net.sf.timeslottracker.gui.Column;
import net.sf.timeslottracker.gui.LayoutManager;

/**
 * Table model to store data - attributes type
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class TaskAttributeTableModel extends AbstractTableModel {

  private LayoutManager layoutManager;

  private static Column[] columns;

  /** Vector (collection) to store rows **/
  private Vector rows;

  public TaskAttributeTableModel(LayoutManager layoutManager) {
    this.layoutManager = layoutManager;
    Locale locale = layoutManager.getTimeSlotTracker().getLocale();
    rows = new Vector();
    columns = new Column[2];
    columns[0] = new Column(
        layoutManager.getCoreString("attributes.task.table.column.type"), 125,
        JLabel.LEFT);
    columns[1] = new Column(
        layoutManager.getCoreString("attributes.task.table.column.value"), 360,
        JLabel.LEFT);
  }

  public int getRowCount() {
    return rows == null ? 0 : rows.size();
  }

  public int getColumnCount() {
    return columns.length;
  }

  public String getColumnName(int columnNo) {
    return columns[columnNo].getName();
  }

  public int getColumnWidth(int columnNo) {
    return columns[columnNo].getWidth();
  }

  public int getColumnAlignment(int columnNo) {
    return columns[columnNo].getAlignment();
  }

  public boolean isCellEditable(int row, int columnNo) {
    return false;
  }

  public Object getValueAt(int row, int columnNo) {
    if (row < 0 || row >= getRowCount())
      return "";
    Attribute element = (Attribute) rows.elementAt(row);
    if (element == null) {
      return "";
    }
    switch (columnNo) {
    case 0:
      return element.getAttributeType().getName();
    case 1:
      return getStringValue(element);
    }
    return "no-data";
  }

  private String getStringValue(Attribute element) {
    AttributeCategory category = element.getAttributeType().getCategory();
    return category.toString(element.get());
  }

  /**
   * Returns Attribute in given row.
   * 
   * @param rowNo
   *          row number of table's row we want to know the value.
   * @return Attribute object in given row.
   */
  public Attribute getValueAt(int rowNo) {
    if (rowNo < 0 || rowNo >= getRowCount())
      return null;
    return (Attribute) rows.elementAt(rowNo);
  }

  /**
   * Set's a new set of data.
   * 
   * @param newRows
   *          null or an empty collection if there is no data or collection with
   *          Attribute to show
   */
  public void setRows(Collection newRows) {
    Vector rows = null;
    if (newRows == null) {
      rows = new Vector();
    } else {
      rows = new Vector(newRows);
    }
    this.rows = rows;
    fireTableDataChanged();
  }

  /**
   * Returns collection of rows - Attributes.
   */
  public Collection getRows() {
    return rows;
  }

  /**
   * Adds new record to table model.
   * 
   * @return added row number.
   */
  public int addRow(Attribute record) {
    if (rows == null) {
      rows = new Vector();
    }
    rows.add(record);
    int recNo = rows.size() - 1;
    fireTableRowsInserted(recNo, recNo);
    return recNo;
  }

  /**
   * Deletes one record from table.
   * 
   * @param rowNumber
   *          row number to remove.
   */
  public void removeRow(int rowNumber) {
    if (rows == null) {
      return;
    }
    rows.remove(rowNumber);
    fireTableRowsDeleted(rowNumber, rowNumber);
  }

}
