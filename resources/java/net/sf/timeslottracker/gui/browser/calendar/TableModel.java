package net.sf.timeslottracker.gui.browser.calendar;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.gui.LayoutManager;

/**
 * This class provide data for JTable in TablePanel.
 * 
 * File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700 (Sat, 16 May
 * 2009) $ Last change: $Author: cnitsa $
 */

class TableModel extends AbstractTableModel {
  public static final int GET_ROW = 1;
  public static final int GET_COLUMN = 2;

  private Calendar day;
  private Vector columns;
  private Vector table;
  private int firstDayOfWeek;
  private LayoutManager layoutManager;
  /** for which day rows are generated */
  private Calendar generatedForDay;

  public TableModel(LayoutManager layoutManager, Calendar day) {
    this.layoutManager = layoutManager;
    this.day = day;

    this.firstDayOfWeek = layoutManager.getTimeSlotTracker().getConfiguration()
        .getInteger(Configuration.WEEK_FIRST_DAY, Calendar.MONDAY);

    generateHeader();
    generateRows();
  }

  public int getRowCount() {
    return table.size();
  }

  public String getColumnName(int columnNo) {
    return (String) columns.get(columnNo);
  }

  public int getColumnCount() {
    return columns.size();
  }

  public int getColumnWidth(int columnNo) {
    return 30;
  }

  public Object getValueAt(int rowNo, int columnNo) {
    Calendar day = getDayAt(rowNo, columnNo);
    return "" + day.get(Calendar.DAY_OF_MONTH);
  }

  public Calendar getDayAt(int rowNo, int columnNo) {
    Vector row = (Vector) table.get(rowNo);
    Calendar day = (Calendar) row.get(columnNo);
    return day;
  }

  public void setDate(Calendar day) {
    this.day = day;

    // generate rows only when the month is changed
    if (generatedForDay != null
        && (generatedForDay.get(Calendar.MONTH) != day.get(Calendar.MONTH) || generatedForDay
            .get(Calendar.YEAR) != day.get(Calendar.YEAR))) {
      generateRows();
    }
  }

  public Calendar getDate() {
    return day;
  }

  /**
   * Looking for a day in table and return row or lumn number. O(n)
   */
  public int findDay(int get, Calendar day) {
    for (int row = 0; row < getRowCount(); row++) {
      for (int column = 0; column < getColumnCount(); column++) {
        Calendar testDay = getDayAt(row, column);
        if (day.get(Calendar.YEAR) == testDay.get(Calendar.YEAR)
            && day.get(Calendar.DAY_OF_YEAR) == testDay
                .get(Calendar.DAY_OF_YEAR)) {
          if (get == GET_ROW) {
            return row;
          } else if (get == GET_COLUMN) {
            return column;
          }
        }
      }
    }

    return 0;
  }

  /**
   * Methods generate data for month tabel. First row has days from prevoius
   * month Last (two or one) row has days from next month.
   */
  private void generateRows() {
    table = new Vector();
    Calendar dayOfMonth = Calendar.getInstance();

    dayOfMonth.set(Calendar.DAY_OF_WEEK, 0);
    dayOfMonth.setMinimalDaysInFirstWeek(1);
    dayOfMonth.setFirstDayOfWeek(firstDayOfWeek);
    dayOfMonth.set(day.get(Calendar.YEAR), day.get(Calendar.MONTH), 1);

    int offset = (dayOfMonth.getFirstDayOfWeek() - dayOfMonth
        .get(Calendar.DAY_OF_WEEK));
    offset = offset >= 0 ? offset - 7 : offset;

    dayOfMonth.add(Calendar.DAY_OF_MONTH, offset);

    for (int rowIndex = 0; rowIndex < 6; rowIndex++) {
      Vector row = new Vector(7);
      for (int colIndex = 0; colIndex < 7; colIndex++) {
        row.add(dayOfMonth.clone());
        dayOfMonth.add(Calendar.DATE, 1);
      }
      table.add(row);

    }
    generatedForDay = (Calendar) day.clone();
    // System.out.println("generated new rows");
    fireTableDataChanged();
  }

  /**
   * Produce names of column using short name of days.
   */
  private void generateHeader() {
    Locale locale = layoutManager.getTimeSlotTracker().getLocale();
    String[] days = new DateFormatSymbols(locale).getShortWeekdays();
    columns = new Vector(days.length);
    int lastIndex = days.length - 1;
    int daysCount;
    int index;

    for (daysCount = 0, index = firstDayOfWeek; daysCount < lastIndex; daysCount++) {
      columns.add(days[index++]);
      index = index % (days.length);
      index = index == 0 ? 1 : index;
    }
  }
}
