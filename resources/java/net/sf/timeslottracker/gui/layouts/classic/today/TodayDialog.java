package net.sf.timeslottracker.gui.layouts.classic.today;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.gui.AbstractSimplePanelDialog;
import net.sf.timeslottracker.gui.DialogPanel;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.taskmodel.TaskModel;

/**
 * Dialog to show daily timeslot values
 * 
 * TODO refactoring: join with timeslots
 * 
 * @author glazachev
 */
@SuppressWarnings("serial")
public class TodayDialog extends AbstractSimplePanelDialog {

  private final TodayTableController dailyTableController;

  private final TodayTableModel tableModel;

  private final TaskModel dailyTaskModel;

  private JTable table;

  public TodayDialog(LayoutManager layoutManager,
      TodayTableController dailyTableController, TodayTableModel tableModel,
      TaskModel dailyTaskModel) {
    super(layoutManager, layoutManager.getString("today.title",
        new Object[] { new Date() }));

    this.dailyTableController = dailyTableController;
    this.tableModel = tableModel;
    this.dailyTaskModel = dailyTaskModel;
  }

  @Override
  protected void beforeShow() {
    setResizable(true);
    setModal(false);
  }

  @Override
  protected Collection<JButton> getButtons() {
    ArrayList<JButton> list = new ArrayList<JButton>();

    JButton startButton = new JButton(string("today.button.Start"));
    startButton.setIcon(icon("play"));
    startButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        start(null);
      }
    });
    list.add(startButton);
    JButton restartButton = new JButton(string("today.button.Restart"));
    restartButton.setIcon(icon("replay"));
    restartButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        TimeSlotValue value = tableModel.getTimeSlotValue(table
            .convertRowIndexToModel(table.getSelectedRow()));
        start(value);
      }
    });
    list.add(restartButton);
    JButton stopButton = new JButton(string("today.button.Stop"));
    stopButton.setIcon(icon("stop"));
    stopButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        stop();
      }

    });
    list.add(stopButton);

    return list;
  }

  @Override
  protected void fillDialogPanel(DialogPanel panel) {
    table = new JTable(tableModel);

    TableColumnModel columnModel = table.getColumnModel();
    columnModel.getColumn(TodayTableModel.TASK_COLUMN_INDEX).setCellEditor(
        new DefaultCellEditor(new JComboBox(dailyTaskModel)));

    // set date's columns width
    columnModel.getColumn(TodayTableModel.START_COLUMN_INDEX).setMaxWidth(70);
    columnModel.getColumn(TodayTableModel.STOP_COLUMN_INDEX).setMaxWidth(70);
    columnModel.getColumn(TodayTableModel.DURATION_COLUMN_INDEX)
        .setMaxWidth(60);

    TableRowSorter<TodayTableModel> sorter = new TableRowSorter<TodayTableModel>(
        tableModel);
    sorter.setSortKeys(Arrays
        .asList(new RowSorter.SortKey[] { new RowSorter.SortKey(0,
            SortOrder.ASCENDING) }));
    table.setRowSorter(sorter);

    table.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
        case KeyEvent.VK_INSERT:
          start(null);
          break;
        case KeyEvent.VK_SPACE:
          if (e.getModifiers() == InputEvent.SHIFT_MASK
              || getLayoutManager().getTimeSlotTracker().getActiveTimeSlot() == null) {
            start(null);
          } else {
            stop();
          }
          break;
        default:
          super.keyReleased(e);
        }
      }

    });

    panel.addRow(new JScrollPane(table));
  }

  private void start(TimeSlotValue timeSlotValue) {
    boolean started = false;
    if (timeSlotValue == null) {
      started = dailyTableController.start();
    } else {
      started = dailyTableController.start(timeSlotValue);
    }

    final Boolean minimizeAfterStart = getConfiguration().getBoolean(
        Configuration.CUSTOM_MINIMIZE_WINDOW_AFTER_START, false);
    if (started && minimizeAfterStart) {
      dispose();
    }
  }

  private void stop() {
    dailyTableController.stop();
  }

}
