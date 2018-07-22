package net.sf.timeslottracker.gui.layouts.classic.search;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import net.sf.timeslottracker.core.TimeSlotTracker;
import net.sf.timeslottracker.data.DataSource;
import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.gui.DialogPanel;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.TextAreaRenderer;

import org.apache.lucene.document.Document;

/**
 * Shown after a search process. Contains found results.
 * 
 * @author User: zgibek Date: 2008-09-02 Time: 23:39:05 $Id: ResultWindow.java
 *         800 2009-05-16 01:53:21Z cnitsa $
 */
public class ResultWindow extends JDialog {
  private LayoutManager layoutManager;
  private final ArrayList rows;
  private TimeSlotTracker timeSlotTracker;
  private DialogPanel dialog;
  private ResultsTableModel tableModel;
  private JTable table;

  public ResultWindow(LayoutManager layoutManager, ArrayList rows) {
    super(layoutManager.getTimeSlotTracker().getRootFrame(), layoutManager
        .getString("search.resultWindow.title"), true);
    this.layoutManager = layoutManager;
    this.rows = rows;
    this.timeSlotTracker = layoutManager.getTimeSlotTracker();
    createWindow();
    pack();
    setSize(760, 230);
    setLocationRelativeTo(getRootPane());

    ActionListener closeAction = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        dispose();
      }
    };
    getRootPane().registerKeyboardAction(closeAction,
        KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
        JComponent.WHEN_IN_FOCUSED_WINDOW);

    setVisible(true);
  }

  private void createWindow() {
    getContentPane().setLayout(new BorderLayout());
    Color background = getBackground();
    dialog = new DialogPanel(GridBagConstraints.HORIZONTAL, 0.0);
    dialog.setBackground(background);
    getContentPane().add(dialog, BorderLayout.CENTER);

    dialog.addRow(layoutManager.getString("search.resultWindow.topRow"));
    createTable();
    dialog.fillToEnd(new JScrollPane(table));
  }

  private void createTable() {
    tableModel = new ResultsTableModel(this, layoutManager, rows);
    table = new JTable();
    table.setAutoCreateColumnsFromModel(false);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    table.setModel(tableModel);
    createColumns();

    // listen if user double click on some timeslot to edit it
    table.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent me) {
        if (me.getClickCount() == 2) {
          int row = table.rowAtPoint(me.getPoint());
          chooseResult(row);
        }
      }
    });
    table.addKeyListener(new KeyListener());
  }

  private class KeyListener extends KeyAdapter {
    public void keyPressed(KeyEvent event) {
      int keyCode = event.getKeyCode();
      if (keyCode == KeyEvent.VK_ENTER) {
        event.consume();
        int row = table.getSelectedRow();
        chooseResult(row);
      }
    }
  }

  /**
   * Accepts user choose and follow the selected row.
   * <p/>
   * Moves context to task (and optionally timeslot) to selected row.
   * 
   * @param row
   *          row in result table.
   */
  private void chooseResult(int row) {
    //System.out.println("Selected row: " + row);
    //System.out.println("tableModel.getValueAt(row, 1) = "
        //+ tableModel.getValueAt(row, 1));
    Document doc = tableModel.getDocument(row);
    if (doc == null) {
      return;
    }
    String task_id = doc.get("task_id");
    String timeslot_id = doc.get("timeslot_id");
    DataSource ds = timeSlotTracker.getDataSource();
    try {
      Task task = ds.getTask(Integer.valueOf(task_id));
      layoutManager.getTasksInterface().selectTask(task);
      if (timeslot_id != null && task != null) {
        TimeSlot timeSlot = task.findTimeSlotById(timeslot_id);
        layoutManager.getTimeSlotsInterface().selectTimeSlot(timeSlot);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void createColumns() {
    final HeaderRenderer headerRenderer = new HeaderRenderer();
    for (int column = 0; column < tableModel.getColumnCount(); column++) {
      TableColumn tableColumn = new TableColumn(column,
          tableModel.getColumnWidth(column));
      tableColumn.setHeaderRenderer(headerRenderer);
      if (column == tableModel.getColumnCount() - 1) {
        tableColumn.setCellRenderer(new TextAreaRenderer(true));
      }
      table.addColumn(tableColumn);
    }
  }

  private class HeaderRenderer extends DefaultTableCellRenderer {
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
      if (table != null) {
        JTableHeader header = table.getTableHeader();
        if (header != null) {
          setForeground(header.getForeground());
          setBackground(header.getBackground());
          setFont(header.getFont());
        }
      }
      setText(value == null ? "" : value.toString());
      setBorder(UIManager.getBorder("TableHeader.cellBorder"));
      return this;
    }

    private HeaderRenderer() {
      setHorizontalAlignment(JLabel.CENTER);
    }
  }

}
