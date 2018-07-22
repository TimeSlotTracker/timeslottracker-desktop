package net.sf.timeslottracker.gui.layouts.classic.timeslots;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableStringConverter;

import net.sf.timeslottracker.core.Action;
import net.sf.timeslottracker.core.ConfigurationHelper;
import net.sf.timeslottracker.core.TimeSlotTracker;
import net.sf.timeslottracker.data.Attribute;
import net.sf.timeslottracker.data.AttributeType;
import net.sf.timeslottracker.data.DataSource;
import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.gui.Column;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.utils.StringUtils;

/**
 * Table model to store data - task's timeslots
 * 
 * @version File version: $Revision: 1038 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
@SuppressWarnings("serial")
class TimeslotsTableModel extends AbstractTableModel {

  public static final int COLUMN_DESCRIPTION_INDEX = 3;

  public static final int COLUMN_DURATION_INDEX = 2;

  public static final int COLUMN_START_INDEX = 0;

  public static final int COLUMN_STOP_INDEX = 1;

  public static final String CONFIGURATION_COLUMN_DESCRIPTION_WIDTH = "column.description.width";

  public static final String CONFIGURATION_COLUMN_DURATION_WIDTH = "column.duration.width";

  public static final String CONFIGURATION_COLUMN_START_WIDTH = "column.start.width";

  public static final String CONFIGURATION_COLUMN_STOP_WIDTH = "column.stop.width";

  // declaring logger
  private static Logger logger = Logger
      .getLogger("net.sf.timeslottracker.gui.layouts.classic.timeslots");

  /** STANDARD count of columns **/
  private static final int STANDARD_COLUMNS_NUMBER = 4;

  private static String dateToString(Date date) {
    return date == null ? StringUtils.EMPTY : String.valueOf(date
        .getTime());
  }

  private Column[] columns;

  /** Listener to configuration changes to set new column set */
  private SetColumnListener configurationListener;

  /** Date formater we will use **/
  private final SimpleDateFormat dateFormater;

  /** columns added after default set */
  private Vector extraColumns; // should be Vector, we are using get(int);

  private LayoutManager layoutManager;

  /** Vector (collection) to store rows **/
  private Vector rows;

  private Timeslots timeslots;

  private TimeSlotTracker timeSlotTracker;

  TimeslotsTableModel(Timeslots timeslots, LayoutManager layoutManager) {
    this.timeslots = timeslots;
    this.layoutManager = layoutManager;
    this.timeSlotTracker = layoutManager.getTimeSlotTracker();
    Locale locale = layoutManager.getTimeSlotTracker().getLocale();
    this.dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm", locale);

    rows = new Vector();
    configurationListener = new SetColumnListener(this);
    layoutManager.getTimeSlotTracker().addActionListener(configurationListener); // after
                                                                                 // data
                                                                                 // loaded
    layoutManager.getTimeSlotTracker().addActionListener(configurationListener,
        Action.ACTION_CONFIGURATION_CHANGED); // after attibutes changed

    setColumns();
  }

  public int getColumnAlignment(int columnNo) {
    return columns[columnNo].getAlignment();
  }

  public TableCellEditor getColumnCellEditor(int columnNo) {
    return columns[columnNo].getCellEditor();
  }

  public TableCellRenderer getColumnCellRenderer(int columnNo) {
    return columns[columnNo].getCellRenderer();
  }

  public Class getColumnClass(int columnNo) {
    return columns[columnNo].getColumnClass();
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

  public int getRowCount() {
    return rows == null ? 0 : rows.size();
  }

  /**
   * Returns TimeSlot in given row.
   * 
   * @param rowNo
   *          row number of table's row we want to know the value.
   * @return TimeSlot object in given row.
   */
  public TimeSlot getValueAt(int rowNo) {
    if (rowNo < 0 || rowNo >= getRowCount())
      return null;
    return (TimeSlot) rows.elementAt(rowNo);
  }

  public Object getValueAt(int row, int columnNo) {
    if (row < 0 || row >= getRowCount())
      return "";
    TimeSlot element = (TimeSlot) rows.elementAt(row);
    if (element == null) {
      return "";
    }
    switch (columnNo) {
    case COLUMN_START_INDEX:
      if (element.getStartDate() == null) {
        return null;
      } else {
        return dateFormater.format(element.getStartDate());
      }
    case COLUMN_STOP_INDEX:
      if (element.getStopDate() == null) {
        return null;
      } else {
        return dateFormater.format(element.getStopDate());
      }
    case COLUMN_DURATION_INDEX:
      if (element.getStartDate() == null || element.getStopDate() == null) {
        return null;
      } else {
        return layoutManager.formatDuration(element.getTime());
      }
    case COLUMN_DESCRIPTION_INDEX:
      return element.getDescription();
    default:
      return getExtraColumnValue(element, columnNo - STANDARD_COLUMNS_NUMBER);
    }
  }

  public boolean isCellEditable(int row, int columnNo) {
    return false;
  }

  /**
   * Determines extra columns - based on the attribute types.
   * <p>
   * <b>Must set the <code>extraColumns</code> to something.</b> If there is NO
   * extra column an empty collection (NOT NULL!) should be set.
   */
  private void determineExtraColumns() {
    extraColumns = new Vector();
    DataSource dataSource = timeSlotTracker.getDataSource();
    if (dataSource == null) {
      return;
    }
    Collection attributeTypes = dataSource.getAttributeTypes();
    if (attributeTypes == null) {
      return;
    }
    Iterator types = attributeTypes.iterator();
    while (types.hasNext()) {
      AttributeType type = (AttributeType) types.next();
      if (type.getShowInTimeSlots()) {
        extraColumns.add(type);
      }
    }
  }

  /**
   * Returns extra added column value.
   * <p>
   * These columns are based on attributes (attributes types) configured in
   * attribute-types configuration window.<br>
   * If timeSlot contains attribute with selected AttributeType the value of
   * this attribute is returned.<br>
   * If not contains - <code>null</code> is returned.
   * 
   * @param element
   *          TimeSlot object we want to explore.
   * @param columnNo
   *          number of extra column. It does not contain the starting columns,
   *          so the first extra column has the number zero.
   * @return value of attribute if it's attribute type matches extra columnn
   *         with number <code>columnNo</code>
   */
  private Object getExtraColumnValue(TimeSlot element, int columnNo) {
    Object column = extraColumns.get(columnNo);
    if (column instanceof AttributeType) {
      AttributeType type = (AttributeType) column;
      Collection attributesCollection = element.getAttributes();
      if (attributesCollection == null) {
        return null;
      } else {
        Iterator attributes = attributesCollection.iterator();
        while (attributes.hasNext()) {
          Attribute attribute = (Attribute) attributes.next();
          AttributeType timeSlotAttrType = attribute.getAttributeType();
          if (type.getName().equals(timeSlotAttrType.getName())) {
            String categoryClassName = type.getCategory().getClass().getName();
            if (categoryClassName
                .equals("net.sf.timeslottracker.data.CheckBoxAttribute")) {
              return new Boolean(attribute.get().toString());
            } else {
              return attribute.get();
            }
          }
        }
        return null; // not found - return null
      }
    }
    return null;
  }

  TableStringConverter getTableStringConverter() {
    return new TableStringConverter() {
      @Override
      public String toString(TableModel model, int row, int column) {
        TimeSlot timeSlot = ((TimeslotsTableModel) model).getValueAt(row);
        if (timeSlot == null) {
          return StringUtils.EMPTY;
        }

        switch (column) {
        case 0: // start
          return dateToString(timeSlot.getStartDate());
        case 1: // end
          return dateToString(timeSlot.getStartDate());
        case 2: // duration
          return String.valueOf(timeSlot.getTime());
        case 3: // description
          return timeSlot.getDescription().toString().toLowerCase();
        default: // extra columns
          return getExtraColumnValue(timeSlot, column - STANDARD_COLUMNS_NUMBER)
              .toString().toLowerCase();
        }
      }

    };
  }

  void saveCurrentColumnOrder() {
    timeslots.saveColumnOrderAndSorting();
  }

  /**
   * Sets the columns based on the attribute types visibility.
   */
  void setColumns() {
    determineExtraColumns();

    columns = new Column[STANDARD_COLUMNS_NUMBER + extraColumns.size()];
    columns[COLUMN_START_INDEX] = new Column(
        layoutManager.getString("timeslots.table.column.start"),
        ConfigurationHelper.getInteger(this, CONFIGURATION_COLUMN_START_WIDTH,
            125), JLabel.CENTER, String.class, null, null);
    columns[COLUMN_STOP_INDEX] = new Column(
        layoutManager.getString("timeslots.table.column.stop"),
        ConfigurationHelper.getInteger(this, CONFIGURATION_COLUMN_STOP_WIDTH,
            125), JLabel.CENTER, String.class, null, null);
    columns[COLUMN_DURATION_INDEX] = new Column(
        layoutManager.getString("timeslots.table.column.duration"),
        ConfigurationHelper.getInteger(this,
            CONFIGURATION_COLUMN_DURATION_WIDTH, 60), JLabel.RIGHT,
        String.class, null, null);
    columns[COLUMN_DESCRIPTION_INDEX] = new Column(
        layoutManager.getString("timeslots.table.column.description"),
        ConfigurationHelper.getInteger(this,
            CONFIGURATION_COLUMN_DESCRIPTION_WIDTH, 300), JLabel.LEFT,
        String.class, null, null);

    int columnNumber = STANDARD_COLUMNS_NUMBER; // starting at
    // STANDARD_COLUMNS_NUMBER.
    // add the extraColumns now.
    Iterator extras = extraColumns.iterator();
    while (extras.hasNext()) {
      Object column = extras.next();
      if (column instanceof AttributeType) {
        AttributeType type = (AttributeType) column;
        String categoryClassName = type.getCategory().getClass().getName();
        logger.finest("Loading extra-column with category: "
            + categoryClassName);
        if (categoryClassName
            .equals("net.sf.timeslottracker.data.CheckBoxAttribute")) {
          columns[columnNumber] = new Column(type.getName(), 45, JLabel.CENTER,
              Boolean.class, null, null);
        } else {
          columns[columnNumber] = new Column(type.getName(), 125, JLabel.LEFT,
              String.class, null, null);
        }
        columnNumber++;
      }
    }

    timeslots.createColumns();
    fireTableStructureChanged();

    timeslots.restoreColumnSorting();
  }

  /**
   * Set's a new set of data.
   * 
   * @param newRows
   *          null or an empty collection if there is no data or collection with
   *          timeslots to show
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

}
