package net.sf.timeslottracker.gui.layouts.classic.today;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.taskmodel.TaskValue;
import net.sf.timeslottracker.utils.StringUtils;
import net.sf.timeslottracker.utils.TimeUtils;

/**
 * Model for daily table
 * 
 * @version File version: $Revision: 1081 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
@SuppressWarnings("serial")
class TodayTableModel extends AbstractTableModel {

  static final int START_COLUMN_INDEX = 0;
  static final int STOP_COLUMN_INDEX = 1;
  static final int DURATION_COLUMN_INDEX = 2;
  static final int TASK_COLUMN_INDEX = 4;

  private final TodayTableController controller;

  private final LayoutManager layoutManager;

  private final List<TimeSlotValue> timeslotValues;

  public TodayTableModel(List<TimeSlotValue> timeslotvalues,
      TodayTableController controller, LayoutManager layoutManager) {
    this.timeslotValues = timeslotvalues;
    this.controller = controller;
    this.layoutManager = layoutManager;
  }

  @Override
  public Class<?> getColumnClass(int columnIndex) {
    return columnIndex == TASK_COLUMN_INDEX ? TaskValue.class : String.class;
  }

  @Override
  public int getColumnCount() {
    return 5;
  }

  @Override
  public String getColumnName(int column) {
    switch (column) {
    case 0:
      return layoutManager.getString("today.table.header.start");
    case 1:
      return layoutManager.getString("today.table.header.stop");
    case DURATION_COLUMN_INDEX:
      return layoutManager.getString("today.table.header.duration");
    case 3:
      return layoutManager.getString("today.table.header.description");
    case TASK_COLUMN_INDEX:
      return layoutManager.getString("today.table.header.task");
    }
    return StringUtils.EMPTY;
  }

  @Override
  public int getRowCount() {
    return timeslotValues.size();
  }

  public TimeSlotValue getTimeSlotValue(int rowIndex) {
    return timeslotValues.get(rowIndex);
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    TimeSlotValue timeSlotValue = getTimeSlotValue(rowIndex);
    switch (columnIndex) {
    case 0:
      return timeSlotValue.getStartValue();
    case 1:
      return timeSlotValue.getStopValue();
    case DURATION_COLUMN_INDEX:
      Long duration = TimeUtils.getDuration(null, null,
          timeSlotValue.getStart(), timeSlotValue.getStop());
      if (duration == null) {
        duration = 0L;
      }
      return layoutManager.formatDuration(duration);
    case 3:
      return timeSlotValue.getDescription();
    case TASK_COLUMN_INDEX:
      return timeSlotValue.getTaskValue();
    }

    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return columnIndex != DURATION_COLUMN_INDEX;
  }

  public void remove(TimeSlotValue removed) {
    int index = indexOf(removed.getTimeSlotId());

    if (index != -1) {
      timeslotValues.remove(removed);
      fireTableRowsDeleted(index, index);
    }
  }

  @Override
  public void setValueAt(Object value, int rowIndex, int columnIndex) {
    TimeSlotValue timeSlotValue;
    try {
      timeSlotValue = getTimeSlotValue(rowIndex).clone();
    } catch (CloneNotSupportedException e) {
      throw new UnsupportedOperationException("this impossible");
    }

    TimeSlotValue relatedTimeSlotValue = null;
    switch (columnIndex) {
    case 0:
      relatedTimeSlotValue = findTimeSlotValueWithDate(timeSlotValue, true);
      timeSlotValue.setStartValue(value);
      if (relatedTimeSlotValue != null) {
        relatedTimeSlotValue.setStopValue(value);
      }
      break;
    case 1:
      relatedTimeSlotValue = findTimeSlotValueWithDate(timeSlotValue, false);
      timeSlotValue.setStopValue(value);
      if (relatedTimeSlotValue != null) {
        relatedTimeSlotValue.setStartValue(value);
      }
      break;
    case DURATION_COLUMN_INDEX:
      throw new UnsupportedOperationException(
          "Duration column is not editable.");
    case 3:
      timeSlotValue.setDescription(value);
      break;
    case TASK_COLUMN_INDEX:
      timeSlotValue.setTaskValue(value);
      break;
    }

    controller.update(timeSlotValue);
    if (relatedTimeSlotValue != null) {
      controller.update(relatedTimeSlotValue);
    }
  }

  private TimeSlotValue findTimeSlotValueWithDate(TimeSlotValue timeSlotValue,
      boolean start) {
    Date date = start ? timeSlotValue.getStart() : timeSlotValue.getStop();
    if (date == null) {
      return null;
    }

    Calendar cal = TimeUtils.create(date);

    for (TimeSlotValue value : timeslotValues) {
      Date date2 = start ? value.getStop() : value.getStart();
      if (date2 == null) {
        continue;
      }

      Calendar stop = TimeUtils.create(date2);

      boolean eq = eq(cal, stop, Calendar.YEAR);
      boolean eq2 = eq(cal, stop, Calendar.DAY_OF_YEAR);
      boolean eq3 = eq(cal, stop, Calendar.HOUR_OF_DAY);
      boolean eq4 = eq(cal, stop, Calendar.MINUTE);
      if (!value.getTimeSlotId().equals(timeSlotValue.getTimeSlotId()) && eq
          && eq2 && eq3 && eq4) {
        return value;
      }
    }
    return null;
  }

  private boolean eq(Calendar first, Calendar second, int cons) {
    return second.get(cons) == first.get(cons);
  }

  public void update(TimeSlotValue updated) {
    int index = indexOf(updated.getTimeSlotId());

    if (index == -1) {
      timeslotValues.add(updated);

      int newRowIndex = timeslotValues.size() - 1;
      fireTableRowsInserted(newRowIndex, newRowIndex);
    } else {
      getTimeSlotValue(index).updateFrom(updated);

      fireTableRowsUpdated(index, index);
    }
  }

  private int indexOf(Object timeSlotId) {
    for (TimeSlotValue value : timeslotValues) {
      if (timeSlotId.equals(value.getTimeSlotId())) {
        return timeslotValues.indexOf(value);
      }
    }
    return -1;
  }

}
