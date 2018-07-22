package net.sf.timeslottracker.gui.browser.calendar;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import net.sf.timeslottracker.core.Action;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.browser.calendar.core.CalendarEventDispatcher;
import net.sf.timeslottracker.gui.browser.calendar.core.MonthActionListener;
import net.sf.timeslottracker.gui.browser.calendar.core.YearActionListener;

/**
 * Sub-panel with month array.
 * 
 * File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700 (Sat, 16 May
 * 2009) $ Last change: $Author: cnitsa $
 */

class TablePanel extends JPanel {
  private Calendar originalDay;
  private Calendar selectedDay;
  private JTable monthTable;
  private CalendarEventDispatcher eventDispatcher;
  private LayoutManager layoutManager;

  private boolean disabledSelectingEvent = false;
  private int lastColumn = -1;
  private int lastRow = -1;

  public TablePanel(LayoutManager layoutManager,
      CalendarEventDispatcher eventDispatcher, Calendar day) {
    super(new BorderLayout());
    this.layoutManager = layoutManager;
    this.eventDispatcher = eventDispatcher;
    this.originalDay = (Calendar) day.clone();
    this.selectedDay = day;

    prepareTable();
  }

  private void prepareTable() {
    TableModel tableModel = new TableModel(layoutManager, selectedDay);
    TableCellRenderer tableCellRenderer = new TableCellRenderer(
        (Calendar) selectedDay.clone());

    tableCellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
    monthTable = new JTable(tableModel);

    JScrollPane scrollPane = new JScrollPane(monthTable);
    scrollPane.getViewport().setBackground(monthTable.getBackground());
    scrollPane.setPreferredSize(new Dimension((int) monthTable.getSize()
        .getWidth(), (monthTable.getRowHeight() + monthTable.getRowMargin())
        * (monthTable.getRowCount() + 1)));

    monthTable.setRowSelectionAllowed(false);
    monthTable.setColumnSelectionAllowed(false);

    monthTable.setShowGrid(false);

    // listen if user doubleclick on some day to select
    monthTable.addMouseListener(new CellSelected());

    // connect cancelAction with ESC key
    monthTable.registerKeyboardAction(new CancelAction(),
        KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
        JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

    // connect selectAction with Enter key
    monthTable.registerKeyboardAction(new SelectAction(),
        KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
        JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

    // connect selectAction with Home key
    monthTable.registerKeyboardAction(new HomeAction(),
        KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0),
        JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

    SelectionListener sl = new SelectionListener();
    ListSelectionModel rowSM = monthTable.getSelectionModel();
    rowSM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    rowSM.addListSelectionListener(sl);
    ListSelectionModel columnSM = monthTable.getColumnModel()
        .getSelectionModel();
    columnSM.addListSelectionListener(sl);

    TableColumn column = null;
    for (int columnNo = 0; columnNo < 7; columnNo++) {
      column = monthTable.getColumnModel().getColumn(columnNo);
      column.setCellRenderer(tableCellRenderer);
      column.setPreferredWidth(tableModel.getColumnWidth(columnNo));
    }
    eventDispatcher.addActionListener(new YearChangedAction());
    eventDispatcher.addActionListener(new MonthChangedAction());
    add(scrollPane);
  }

  /**
   * Method reset selected day to day given on begining.
   */
  public void goHome() {
    selectedDay = originalDay;
    TableModel tableModel = (TableModel) monthTable.getModel();
    tableModel.setDate(selectedDay);
    eventDispatcher.fireDayChange(selectedDay); // inform all that day was
                                                // changed
    goSelectedDay();
  }

  public void goSelectedDay() {
    // System.out.println("goSelectedDay for: "+selectedDay.get(Calendar.YEAR)+"-"+(selectedDay.get(Calendar.MONTH)+1)+"-"+selectedDay.get(Calendar.DATE));
    // Thread.dumpStack();
    monthTable.requestFocus();
    TableModel tableModel = (TableModel) monthTable.getModel();
    monthTable.clearSelection();
    lastRow = tableModel.findDay(TableModel.GET_ROW, selectedDay);
    lastColumn = tableModel.findDay(TableModel.GET_COLUMN, selectedDay);

    // invoke later changing the selection, but disable our selecting listener
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        disabledSelectingEvent = true;
        monthTable.changeSelection(lastRow, lastColumn, false, false);
        disabledSelectingEvent = false;
      }
    });
  }

  public Calendar getSelectedDay() {
    return selectedDay;
  }

  private class YearChangedAction implements YearActionListener {
    public void actionPerformed(Action event) {
      Calendar newMonth = (Calendar) event.getSource();
      TableModel tableModel = (TableModel) monthTable.getModel();

      selectedDay.set(Calendar.YEAR, newMonth.get(Calendar.YEAR));
      tableModel.setDate(selectedDay);
      goSelectedDay();
    }
  }

  private class MonthChangedAction implements MonthActionListener {
    public void actionPerformed(Action event) {
      Calendar newMonth = (Calendar) event.getSource();
      TableModel tableModel = (TableModel) monthTable.getModel();

      selectedDay.set(Calendar.MONTH, newMonth.get(Calendar.MONTH));
      tableModel.setDate(selectedDay);
      goSelectedDay();
    }
  }

  private class SelectionListener implements ListSelectionListener {

    public void valueChanged(ListSelectionEvent event) {
      // Ignore extra messages.
      if (event.getValueIsAdjusting()) {
        return;
      }

      if (disabledSelectingEvent) {
        return;
      }

      ListSelectionModel lsm = (ListSelectionModel) event.getSource();
      if (!lsm.isSelectionEmpty()) {
        int selectedRow = monthTable.getSelectedRow();
        int selectedColumn = monthTable.getSelectedColumn();
        // System.out.println("Selecting: "+selectedRow+", "+selectedColumn);

        if (selectedRow >= 0 && selectedColumn >= 0
            && (selectedRow != lastRow || selectedColumn != lastColumn)) {

          lastRow = selectedRow;
          lastColumn = selectedColumn;
          TableModel tableModel = (TableModel) monthTable.getModel();
          selectedDay = tableModel.getDayAt(selectedRow, selectedColumn);
          // System.out.println("Selected day: "+selectedDay.get(Calendar.YEAR)+"-"+(selectedDay.get(Calendar.MONTH)+1)+"-"+selectedDay.get(Calendar.DATE));
          tableModel.setDate(selectedDay);
          eventDispatcher.fireDayChange(selectedDay);
        }
        System.out.println("**>>** SelectionListener: " + selectedDay + " ("
            + selectedColumn + ", " + selectedRow + ")");
      }

      goSelectedDay();
    }
  }

  /**
   * Used when user select day by mouse double clik.
   */
  private class CellSelected extends MouseAdapter {
    public void mouseClicked(MouseEvent me) {
      if (me.getClickCount() == 2) {
        int row = monthTable.rowAtPoint(me.getPoint());
        int column = monthTable.columnAtPoint(me.getPoint());
        TableModel tableModel = (TableModel) monthTable.getModel();
        selectedDay = tableModel.getDayAt(row, column);
        eventDispatcher.fireSelectAction(selectedDay);
      }
      System.out.println("*** CellSelected:" + me.getClickCount()
          + ": selectedDay=" + selectedDay);
    }
  }

  /**
   * Action used when a user press escape key.
   */
  private class CancelAction implements java.awt.event.ActionListener {
    public void actionPerformed(ActionEvent e) {
      eventDispatcher.fireCancelAction();
    }
  }

  /**
   * Action used when a user press enter key.
   */
  private class SelectAction implements java.awt.event.ActionListener {
    public void actionPerformed(ActionEvent e) {
      eventDispatcher.fireSelectAction(selectedDay);
    }
  }

  /**
   * Action used when a user press home key.
   */
  private class HomeAction implements java.awt.event.ActionListener {
    public void actionPerformed(ActionEvent e) {
      eventDispatcher.fireHomeAction();
    }
  }

}
