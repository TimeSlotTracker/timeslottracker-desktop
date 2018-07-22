package net.sf.timeslottracker.gui.layouts.classic.timeslots;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.TransferHandler;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import net.sf.timeslottracker.core.Action;
import net.sf.timeslottracker.core.ActionListener;
import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.core.ConfigurationHelper;
import net.sf.timeslottracker.core.TimeSlotTracker;
import net.sf.timeslottracker.data.Attribute;
import net.sf.timeslottracker.data.AttributeType;
import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.data.TimeSlotChangedListener;
import net.sf.timeslottracker.filters.FilterUtils;
import net.sf.timeslottracker.filters.TimeSlotFilter;
import net.sf.timeslottracker.gui.DialogPanel;
import net.sf.timeslottracker.gui.DialogPanel.Updater;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.TimeSlotEditDialog;
import net.sf.timeslottracker.gui.TimeSlotFilterDialog;
import net.sf.timeslottracker.gui.TimeSlotSplitDialog;
import net.sf.timeslottracker.gui.TimeSlotsInterface;
import net.sf.timeslottracker.gui.dateperiod.DatePeriod;
import net.sf.timeslottracker.gui.dateperiod.DatePersistor;
import net.sf.timeslottracker.gui.dnd.handlers.TimeSlotHandler;
import net.sf.timeslottracker.gui.listeners.TaskSelectionChangeListener;
import net.sf.timeslottracker.gui.listeners.TasksByDaysSelectionAction;
import net.sf.timeslottracker.gui.listeners.TasksByDaysSelectionChangeListener;

/**
 * A module for timeslottracker to present time slots in JTable.
 * <p>
 * TODO Now this is view/controller for timeslots table. Need to refactor.
 *
 * File version: $Revision: 1199 $, $Date: 2009-05-16 08:53:21 +0700 (Sat, 16
 * May 2009) $ Last change: $Author: cnitsa $
 */
@SuppressWarnings("serial")
public class Timeslots extends JPanel implements TimeSlotsInterface,
    TaskSelectionChangeListener {

  /**
   * Listens to Action Action.ACTION_SET_CONFIGURATION to perform actions to set
   * current values of columns order
   */
  private class SetConfigurationActionListener implements ActionListener {
    public void actionPerformed(Action action) {
      if (action.getSource() instanceof Configuration) {
        saveColumnOrderAndSorting();
        saveColumnsWidth();
      }
    }
  }

  private class TimeSlotChangeAction implements TimeSlotChangedListener {
    public void actionPerformed(Action action) {
      refresh();
    }
  }

  // declaring logger
  private static Logger logger = Logger
      .getLogger("net.sf.timeslottracker.gui.layouts.classic.timeslots");

  public Mode currentMode = Mode.Task;

  /** Actual task - the one which timeslots are shown * */
  private Task actualTask;

  /** dialog panel where we will keep all our info * */
  private DialogPanel dialogPanel;

  private final DatePeriod datePeriod;

  private final LayoutManager layoutManager;

  /** Table to store timeslots */
  private JTable table;

  /** table's model * */
  private TimeslotsTableModel tableModel;

  private final TimeSlotTracker timeSlotTracker;

  private final Updater titleUpdater;

  public Timeslots(final LayoutManager layoutManager) {
    super(new BorderLayout());
    this.layoutManager = layoutManager;
    this.timeSlotTracker = layoutManager.getTimeSlotTracker();
    Configuration configuration = layoutManager.getTimeSlotTracker()
        .getConfiguration();
    int firstDayOfWeek = configuration.getInteger(Configuration.WEEK_FIRST_DAY,
        Calendar.MONDAY);

    this.datePeriod = new DatePeriod(firstDayOfWeek);
    createTable();

    // constructs dialog panel with our task's info variables
    dialogPanel = new DialogPanel(GridBagConstraints.BOTH, 0.0);
    titleUpdater = dialogPanel.addTitleWithUpdater(layoutManager
        .getString("timeslots.title"));
    JScrollPane scrollTable = new JScrollPane(table);
    scrollTable.getViewport().setBackground(table.getBackground());
    dialogPanel.fillToEnd(scrollTable);

    add(dialogPanel, BorderLayout.CENTER);

    final TimeslotsTablePopupMenu popupMenu = new TimeslotsTablePopupMenu(
        layoutManager, table, this);

    // listen if user double click on some timeslot to edit it
    table.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent me) {
        if (me.getClickCount() == 2) {
          int row = table.rowAtPoint(me.getPoint());
          edit(getTimeSlotByTreeRowId(row));
        }
      }
    });
    table.addMouseListener(popupMenu.getMouseListener());
    scrollTable.addMouseListener(popupMenu.getMouseListener());
    layoutManager.addActionListener(new TimeSlotChangeAction());

    timeSlotTracker.addActionListener(new SetConfigurationActionListener(),
        Action.ACTION_SET_CONFIGURATION);

    layoutManager.addActionListener(new TasksByDaysSelectionChangeListener() {
      @Override
      public void actionPerformed(Action action) {
        TasksByDaysSelectionAction action2 = (TasksByDaysSelectionAction) action;
        MessageFormat messageFormat = new MessageFormat(layoutManager
            .getString("timeslots.title.filter"));
        String title = action2.getTimeSlotsDescription() == null ? null
            : messageFormat.format(new Object[] { action2
            .getTimeSlotsDescription() });
        doShow(Mode.TimeSlots, title, action2.getTimeSlots());
      }

      ;
    });

    table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    table.setDragEnabled(true);
    table.setTransferHandler(new TransferHandler() {
      TimeSlotHandler timeSlotHandler = new TimeSlotHandler(layoutManager);

      public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
        return timeSlotHandler.canImport(transferFlavors);
      }

      public int getSourceActions(JComponent c) {
        return TransferHandler.COPY_OR_MOVE;
      }

      public boolean importData(JComponent comp, Transferable t) {
        return timeSlotHandler.importData(t, actualTask);
      }

      protected Transferable createTransferable(JComponent c) {
        ArrayList<TimeSlot> selectedTimeSlots = new ArrayList<TimeSlot>();
        for (int row : table.getSelectedRows()) {
          selectedTimeSlots.add(getTimeSlotByTreeRowId(row));
        }

        return selectedTimeSlots.isEmpty() ? null : timeSlotHandler
            .wrap(selectedTimeSlots);
      }

      protected void exportDone(JComponent source, Transferable data,
          int action) {
        timeSlotHandler.exportDone(data, action);
      }
    });

    restore(configuration, datePeriod);
  }

  // realization of TaskSelectionChangeListener
  public void actionPerformed(Action action) {
    actualTask = (Task) action.getParam();
    show(actualTask);
    TimeSlot activeTimeSlot = layoutManager.getTimeSlotTracker()
        .getActiveTimeSlot();
    if (activeTimeSlot == null) {
      return;
    }
    Task activeTask = activeTimeSlot.getTask();
    if (activeTask.equals(actualTask)) {
      selectTimeSlot(activeTimeSlot);
    }
  }

  public void add(TimeSlot timeslot) {
    if (currentMode != Mode.Task) {
      return;
    }

    if (actualTask == null) {
      String debugMsg = layoutManager
          .getString("timeslots.method.add.noActualTask");
      layoutManager.getTimeSlotTracker().debugLog(debugMsg);
      return;
    }
    if (timeslot == null) {
      return;
    }

    Collection<Attribute> timeslotAttributes = timeslot.getAttributes();
    Collection<AttributeType> timeslotAttributeTypes = layoutManager
        .getTimeSlotTracker().getDataSource().getAttributeTypes();
    for (AttributeType at : timeslotAttributeTypes) {
      if (at.isAutoAddToTimeSlots()) {
        timeslotAttributes.add(new Attribute(at));
      }
    }

    actualTask.addTimeslot(timeslot);
    refresh();
    layoutManager.fireTimeSlotChanged(timeslot);
  }

  public void edit(TimeSlot timeslot) {
    if (currentMode != Mode.Task) {
      return;
    }

    TimeSlotEditDialog dialog = new TimeSlotEditDialog(layoutManager, timeslot,
        false);
    TimeSlot newTimeSlot = dialog.getTimeslot();
    if (newTimeSlot != null) {
      refresh();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sf.timeslottracker.gui.TimeSlotsInterface#editSelected()
   */
  public void editSelected() {
    if (currentMode != Mode.Task) {
      return;
    }

    if (actualTask == null) {
      return;
    }
    int rowSelected = table.getSelectedRow();
    if (rowSelected >= 0 && table.getSelectedRowCount() == 1) {
      edit(getTimeSlotByTreeRowId(rowSelected));
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sf.timeslottracker.gui.TimeSlotsInterface#splitSelected()
   */
  public TimeSlot splitSelected() {
    int rowSelected = table.getSelectedRow();
    if (rowSelected < 0 || table.getSelectedRowCount() != 1) {
      return null;
    }

    TimeSlotSplitDialog dialog = new TimeSlotSplitDialog(layoutManager,
        getTimeSlotByTreeRowId(rowSelected), false);
    dialog.activate();
    TimeSlot changedTimeSlot = dialog.getTimeslot();
    if (changedTimeSlot != null) {
      refresh();
      layoutManager.fireTimeSlotChanged(changedTimeSlot);
    }

    TimeSlot newTimeslot = dialog.getTimeslotAfterSplit();
    if (newTimeslot != null) {
      add(newTimeslot);
      selectTimeSlot(newTimeslot);
    }

    return newTimeslot;
  }

  @Override
  public void filter() {
    TimeSlotFilterDialog dialog = new TimeSlotFilterDialog(layoutManager,
        datePeriod);
    dialog.activate(); // datePeriod will updated in dialog

    store(layoutManager.getTimeSlotTracker().getConfiguration(), datePeriod);

    logger.info("timeslotfilter period type: " + datePeriod.getPeriodType());
    logger.info("timeslotfilter start: " + datePeriod.getStartPeriod());
    logger.info("timeslotfilter end: " + datePeriod.getEndPeriod());

    layoutManager.fireTimeSlotFilterChanged(new Action(
        "time slot filter changed", this, datePeriod));

    refresh();
  }

  public Task getSelectedTask() {
    return actualTask;
  }

  /**
   * @return current view mode
   */
  @Override
  public Mode getMode() {
    return currentMode;
  }

  public void refresh() {
    int selectedRow = table.getSelectedRow();
    TimeSlot selectedTimeSlot = null;
    if (selectedRow >= 0) {
      selectedTimeSlot = getTimeSlotByTreeRowId(selectedRow);
    }
    show(actualTask);
    int rowCount = table.getRowCount();
    if (rowCount <= 0) {
      return;
    }
    if (selectedTimeSlot != null) {
      selectTimeSlot(selectedTimeSlot);
    }
    if (table.getSelectedRow() >= 0) {
      return;
    }
    if (selectedRow >= rowCount || selectedRow < 0) {
      selectedRow = rowCount - 1;
    }
    table.setRowSelectionInterval(selectedRow, selectedRow);
  }

  public void selectTimeSlot(TimeSlot timeslot) {
    int index = findTimeSlot(timeslot);
    if (index >= 0) {
      table.setRowSelectionInterval(index, index);
      Rectangle cellRect = table.getCellRect(index, 0, true);
      if (cellRect != null) {
        table.scrollRectToVisible(cellRect);
      }
    }
  }

  public void show(Task task) {
    doShow(Mode.Task, layoutManager.getString("timeslots.title"),
        task == null ? null : task.getTimeslots());
  }

  public void update(TimeSlot previousTimeslot) {
    refresh();

    layoutManager.fireTimeSlotChanged(previousTimeslot);
  }

  /**
   * Creates new table object to show task's timeslots
   */
  private void createTable() {
    tableModel = new TimeslotsTableModel(this, layoutManager);
    table = new JTable();
    table.setAutoCreateColumnsFromModel(false);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    table.setModel(tableModel);

    createColumns();
  }

  private void doShow(Mode mode, String title, Collection<TimeSlot> timeSlots) {
    currentMode = mode;
    titleUpdater.updateText(title);

    TimeSlotFilter timeSlotFilter = datePeriod.getTimeSlotFilter();

    Collection<TimeSlot> collection2show = timeSlots;
    if (null != timeSlots && null != timeSlotFilter
        && TimeSlotsInterface.Mode.Task == currentMode) {
      collection2show = FilterUtils.filter(timeSlots, timeSlotFilter);
    }
    tableModel.setRows(collection2show);
  }

  private int findTimeSlot(TimeSlot timeslot) {
    int rowCount = table.getRowCount();
    if (rowCount == 0 || timeslot == null) {
      return -1;
    }
    int i = 0;
    while (i < rowCount && !timeslot.equals(getTimeSlotByTreeRowId(i))) {
      i++;
    }
    if (i == rowCount) {
      return -1;
    }
    return i;
  }

  private Integer getCurrentColumnWidth(int columnIndex) {
    Enumeration currentColumns = table.getColumnModel().getColumns();
    while (currentColumns.hasMoreElements()) {
      TableColumn tableColumn = TableColumn.class.cast(currentColumns
          .nextElement());
      if (tableColumn.getModelIndex() == columnIndex) {
        return tableColumn.getWidth();
      }
    }
    return null;
  }

  private int getSortedColumn() {
    RowSorter<? extends TableModel> rowSorter = table.getRowSorter();
    List<? extends SortKey> sortKeys = rowSorter.getSortKeys();
    SortKey sortKey = sortKeys.get(0);
    int sortedColumn = sortKey.getColumn();
    return sortedColumn;
  }

  private SortOrder getSorterOrder() {
    return table.getRowSorter().getSortKeys().get(0).getSortOrder();
  }

  private TimeSlot getTimeSlotByTreeRowId(int rowId) {
    return tableModel.getValueAt(table.convertRowIndexToModel(rowId));
  }

  /**
   * Saves into configuration current column widths.
   */
  private void saveColumnsWidth() {
    ConfigurationHelper.setProperty(tableModel,
        TimeslotsTableModel.CONFIGURATION_COLUMN_START_WIDTH,
        getCurrentColumnWidth(TimeslotsTableModel.COLUMN_START_INDEX));
    ConfigurationHelper.setProperty(tableModel,
        TimeslotsTableModel.CONFIGURATION_COLUMN_STOP_WIDTH,
        getCurrentColumnWidth(TimeslotsTableModel.COLUMN_STOP_INDEX));
    ConfigurationHelper.setProperty(tableModel,
        TimeslotsTableModel.CONFIGURATION_COLUMN_DURATION_WIDTH,
        getCurrentColumnWidth(TimeslotsTableModel.COLUMN_DURATION_INDEX));
    ConfigurationHelper.setProperty(tableModel,
        TimeslotsTableModel.CONFIGURATION_COLUMN_DESCRIPTION_WIDTH,
        getCurrentColumnWidth(TimeslotsTableModel.COLUMN_DESCRIPTION_INDEX));
  }

  void createColumns() {
    if (tableModel == null) {
      return;
    }
    logger.finest("Creating columns of timeslot table");
    Enumeration currentColumns = table.getColumnModel().getColumns();
    while (currentColumns.hasMoreElements()) {
      table.removeColumn((TableColumn) currentColumns.nextElement());
      // we have to refresh the columns set to current state.
      currentColumns = table.getColumnModel().getColumns();
    }

    // load columnOrder
    Configuration configuration = timeSlotTracker.getConfiguration();
    String columnOrder = configuration.getString(
        Configuration.LAST_TIMESLOTS_TABLE_COLUMN_ORDER, null);
    if (columnOrder == null) {
      columnOrder = "";
    }
    StringTokenizer tokenizer = new StringTokenizer(columnOrder, ";");
    int[] columnIndexes = new int[tokenizer.countTokens()];
    int index = 0;
    while (tokenizer.hasMoreTokens()) {
      columnIndexes[index++] = Integer.parseInt(tokenizer.nextToken());
    }

    for (int column = 0; column < tableModel.getColumnCount()
        || column < columnIndexes.length; column++) {
      int currentIndex = column;
      if (column < columnIndexes.length) {
        currentIndex = columnIndexes[column];
      }
      logger.fine("CurrentIndex=" + currentIndex + "; column=" + column
          + "; columnIndexes=" + columnIndexes);
      if (currentIndex >= tableModel.getColumnCount()) {
        logger.severe("currentIndex>=tableModel.getColumnCount(): aborted");
        continue;
      }
      TableColumn tableColumn = new TableColumn(currentIndex,
          tableModel.getColumnWidth(currentIndex), null,
          tableModel.getColumnCellEditor(currentIndex));
      table.addColumn(tableColumn);
    }
  }

  void restoreColumnSorting() {
    if (tableModel == null) {
      return;
    }

    // setting sorting
    Configuration configuration = timeSlotTracker.getConfiguration();
    Integer sortedColumn = configuration.getInteger(
        Configuration.LAST_TIMESLOTS_TABLE_SORT_COLUMN, 0);
    if (sortedColumn == null || sortedColumn < 0) {
      sortedColumn = 0;
    }

    Boolean sortedAsc = configuration.getBoolean(
        Configuration.LAST_TIMESLOTS_TABLE_SORT_DIR, null);
    logger.finest("Restoring column order: " + sortedColumn + ": " + sortedAsc);

    TableRowSorter<TimeslotsTableModel> sorter = new TableRowSorter<TimeslotsTableModel>(
        tableModel);
    sorter.setStringConverter(tableModel.getTableStringConverter());

    SortOrder sortOrder = (sortedAsc == null) ? SortOrder.UNSORTED
        : ((sortedAsc) ? SortOrder.ASCENDING : SortOrder.DESCENDING);
    sorter.setSortKeys(Arrays
        .asList(new RowSorter.SortKey[] { new RowSorter.SortKey(sortedColumn,
            sortOrder) }));

    table.setRowSorter(sorter);
  }

  void saveColumnOrderAndSorting() {
    Configuration configuration = timeSlotTracker.getConfiguration();

    String columnOrder = "";
    TableColumnModel columnModel = table.getColumnModel();
    for (int i = 0; i < columnModel.getColumnCount(); i++) {
      if (columnOrder.length() > 0) {
        columnOrder += ";";
      }
      int modelIndex = columnModel.getColumn(i).getModelIndex();
      columnOrder += modelIndex;
    }
    configuration.set(Configuration.LAST_TIMESLOTS_TABLE_COLUMN_ORDER,
        columnOrder);

    int sortedColumn = getSortedColumn();
    boolean sortedAsc = getSorterOrder() == SortOrder.ASCENDING ? true : false;
    logger.finest("Saving column order: " + sortedColumn + ": " + sortedAsc);
    configuration.set(Configuration.LAST_TIMESLOTS_TABLE_SORT_COLUMN,
        sortedColumn);
    configuration.set(Configuration.LAST_TIMESLOTS_TABLE_SORT_DIR, sortedAsc);
    logger
        .info(
            "Saved into Configuration current column order and sorting values");
  }

  public void fireActions() {
    layoutManager.fireTimeSlotFilterChanged(new Action(
        "time slot filter changed", this, datePeriod));
  }

  /**
   * Store date period settings in given configuration
   */
  public static void store(Configuration config, DatePeriod datePeriod) {
    config.set(Configuration.LAST_TIMESLOTS_TABLE_FILTER_TYPE, datePeriod
        .getPeriodType().getPersistentId());
    config.set(Configuration.LAST_TIMESLOTS_TABLE_FILTER_START,
        DatePersistor.string(datePeriod.getUserPeriodStart()));
    config.set(Configuration.LAST_TIMESLOTS_TABLE_FILTER_END,
        DatePersistor.string(datePeriod.getUserPeriodEnd()));
  }

  /**
   * Restore date period setting from given configuration
   */
  public static void restore(Configuration config, DatePeriod datePeriod) {
    datePeriod.setPeriodType(DatePeriod.PeriodType.valueOf(config.getInteger(
        Configuration.LAST_TIMESLOTS_TABLE_FILTER_TYPE, 0)));

    if (DatePeriod.PeriodType.USER_PERIOD == datePeriod.getPeriodType()) {
      datePeriod.setUserPeriod(DatePersistor.date(config.getString(
              Configuration.LAST_TIMESLOTS_TABLE_FILTER_START, null)),
          DatePersistor.date(config.getString(
              Configuration.LAST_TIMESLOTS_TABLE_FILTER_END, null))
      );
    }
  }

  /**
   * Returns the currently selected TimeSlot.
   *
   * @return TimeSlot
   */
  public TimeSlot getSelected() {
    int rowSelected = table.getSelectedRow();
    if (rowSelected < 0 || table.getSelectedRowCount() != 1) {
      return null;
    }
    return getTimeSlotByTreeRowId(rowSelected);
  }
}
