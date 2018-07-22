package net.sf.timeslottracker.gui.attributes;

import java.util.Collection;
import java.util.Locale;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import net.sf.timeslottracker.data.AttributeType;
import net.sf.timeslottracker.gui.Column;
import net.sf.timeslottracker.gui.LayoutManager;

/**
 * Table model to store data - attributes type
 * 
 * @version File version: $Revision: 1086 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
class TypeTableModel extends AbstractTableModel {

  private LayoutManager layoutManager;

  private String USED_IN_TASKS;
  private String USED_IN_TIMESLOTS;

  private static Column[] columns;

  /** Vector (collection) to store rows **/
  private Vector rows;

  TypeTableModel(LayoutManager layoutManager) {
    this.layoutManager = layoutManager;
    USED_IN_TASKS = layoutManager
        .getCoreString("attributes.table.column.usedIn.tasks");
    USED_IN_TIMESLOTS = layoutManager
        .getCoreString("attributes.table.column.usedIn.timeSlots");
    Locale locale = layoutManager.getTimeSlotTracker().getLocale();
    // TableCellRenderer checkBoxRenderer = new CheckBoxTableCellRenderer();
    rows = new Vector();
    columns = new Column[9];
    columns[0] = new Column(
        layoutManager.getCoreString("attributes.window.table.column.name"),
        125, JLabel.LEFT);
    columns[1] = new Column(
        layoutManager.getCoreString("attributes.window.table.column.category"),
        125, JLabel.LEFT);
    columns[2] = new Column(
        layoutManager.getCoreString("attributes.window.table.column.usedIn"),
        125, JLabel.LEFT);
    columns[3] = new Column(
        layoutManager.getCoreString("attributes.window.table.column.usedCount"),
        70, JLabel.CENTER);
    columns[4] = new Column(
        layoutManager
            .getCoreString("attributes.window.table.column.hiddenOnReports"),
        45, JLabel.CENTER, Boolean.class, null, null);
    columns[5] = new Column(
        layoutManager
            .getCoreString("attributes.window.table.column.showInTaskInfo"),
        45, JLabel.CENTER, Boolean.class, null, null);
    columns[6] = new Column(
        layoutManager
            .getCoreString("attributes.window.table.column.showInTimeSlots"),
        45, JLabel.CENTER, Boolean.class, null, null);
    columns[7] = new Column(
            layoutManager
                .getCoreString("attributes.window.table.column.autoAddToTimeSlots"),
            45, JLabel.CENTER, Boolean.class, null, null);
    columns[8] = new Column(
        layoutManager
            .getCoreString("attributes.window.table.column.description"),
        250, JLabel.LEFT);

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

  public Class getColumnClass(int columnNo) {
    return columns[columnNo].getColumnClass();
  }

  public TableCellRenderer getColumnCellRenderer(int columnNo) {
    return columns[columnNo].getCellRenderer();
  }

  public TableCellEditor getColumnCellEditor(int columnNo) {
    return columns[columnNo].getCellEditor();
  }

  public boolean isCellEditable(int row, int columnNo) {
    return false;
  }

  public Object getValueAt(int row, int columnNo) {
    if (row < 0 || row >= getRowCount())
      return "";
    AttributeType element = (AttributeType) rows.elementAt(row);
    if (element == null) {
      return "";
    }
    switch (columnNo) {
    case 0:
      return element.getName();
    case 1:
      return element.getCategory();
    case 2:
      return usedIn(element);
    case 3:
      return countUsedBy(element);
      // case 4: return yesNo(element.isHiddenOnReports());
      // case 5: return yesNo(element.getShowInTaskInfo());
      // case 6: return yesNo(element.getShowInTimeSlots());
    case 4:
      return new Boolean(element.isHiddenOnReports());
    case 5:
      return new Boolean(element.getShowInTaskInfo());
    case 6:
      return new Boolean(element.getShowInTimeSlots());
    case 7:
        return new Boolean(element.isAutoAddToTimeSlots());
    case 8:
      return element.getDescription();
    }
    return "no-data";
  }

  /**
   * Returns "YES" or "NO" depending on the parameter.
   * <p>
   * The exact value is taken from properties file with keys
   * "simpleString.YES/NO".
   */
  private String yesNo(boolean value) {
    return layoutManager.getCoreString(value ? "simpleString.YES"
        : "simpleString.NO");
  }

  /**
   * Returns a string indicating whenever it is used in tasks, timeslots or
   * both.
   */
  private String usedIn(AttributeType element) {
    String usedIn = null;
    if (element.getUsedInTasks()) {
      usedIn = USED_IN_TASKS;
    }
    if (element.getUsedInTimeSlots()) {
      if (usedIn == null) {
        usedIn = "";
      } else {
        usedIn += ", ";
      }
      usedIn += USED_IN_TIMESLOTS;
    }
    return usedIn;
  }

  /**
   * Returns how many objects is using this type.
   */
  private Integer countUsedBy(AttributeType element) {
    Collection collection = element.getRegisteredObjects();
    return new Integer(collection.size());
  }

  /**
   * Returns AttributeType in given row.
   * 
   * @param rowNo
   *          row number of table's row we want to know the value.
   * @return AttributeType object in given row.
   */
  public AttributeType getValueAt(int rowNo) {
    if (rowNo < 0 || rowNo >= getRowCount())
      return null;
    return (AttributeType) rows.elementAt(rowNo);
  }

  /**
   * Set's a new set of data.
   * 
   * @param newRows
   *          null or an empty collection if there is no data or collection with
   *          AttributeType to show
   */
  void setRows(Collection newRows) {
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
   * Returns collection of rows - AttributeTypes.
   */
  public Collection getRows() {
    return rows;
  }

  /**
   * Adds new record to table model.
   * 
   * @return added row number.
   */
  int addRow(AttributeType record) {
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
  void removeRow(int rowNumber) {
    if (rows == null) {
      return;
    }
    rows.remove(rowNumber);
    fireTableRowsDeleted(rowNumber, rowNumber);
  }

}
