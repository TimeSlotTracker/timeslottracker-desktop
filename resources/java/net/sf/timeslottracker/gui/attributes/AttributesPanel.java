package net.sf.timeslottracker.gui.attributes;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import net.sf.timeslottracker.data.Attribute;
import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.gui.LayoutManager;

/**
 * Complex control to operate on task's attributes.
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class AttributesPanel extends JPanel implements EditingWindow {

  /**
   * Listeners for attributesPanel state
   * 
   * @version File version: $Revision: 998 $, $Date: 2009-08-05 14:35:00 +0700
   *          (Wed, 05 Aug 2009) $
   * @author Last change: $Author: cnitsa $
   */
  public interface AttributesPanelListener {

    /**
     * Handle state's update
     */
    void handleUpdate();

  }

  private static final long serialVersionUID = 7318876590768342669L;

  private boolean saveImmediately;

  private JTable table;

  private TaskAttributeTableModel tableModel;

  private LayoutManager layoutManager;

  private Task task;

  private AttributesPanelListener attributesPanelListener;

  public AttributesPanel(LayoutManager layoutManager, Task task) {
    this(layoutManager, task, false);
  }

  public AttributesPanel(LayoutManager layoutManager, Task task,
      boolean saveImmediately) {
    super(new BorderLayout());

    this.layoutManager = layoutManager;
    this.task = task;
    this.saveImmediately = saveImmediately;

    createTable();
    createDialog();
    reloadFields();

    setVisible(true);
  }

  /**
   * Sets attributesPanel state listener
   */
  public void setAttributesPanelListener(
      AttributesPanelListener attributesPanelListener) {
    this.attributesPanelListener = attributesPanelListener;
  }

  /**
   * Sets explicit saveImmediately
   * 
   * @param saveImmediately
   *          new value
   */
  public void setSaveImmediately(boolean saveImmediately) {
    this.saveImmediately = saveImmediately;
  }

  private void createTable() {
    tableModel = new TaskAttributeTableModel(layoutManager);
    table = new JTable();
    table.setAutoCreateColumnsFromModel(false);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    table.setModel(tableModel);

    for (int column = 0; column < tableModel.getColumnCount(); column++) {
      DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
      renderer.setHorizontalAlignment(tableModel.getColumnAlignment(column));
      TableColumn tableColumn = new TableColumn(column,
          tableModel.getColumnWidth(column), renderer, null);
      table.addColumn(tableColumn);
    }

    tableModel.addTableModelListener(new TableModelListener() {
      public void tableChanged(TableModelEvent e) {
        layoutManager.getTimeSlotTracker().debugLog(
            "TABLE ROWS: " + table.getRowCount());
        table.repaint();
      }
    });
  }

  public void createDialog() {
    JScrollPane scrollTable = new JScrollPane(table);
    scrollTable.getViewport().setBackground(table.getBackground());

    add(scrollTable, BorderLayout.CENTER);
    add(new TableButtonsPanel(layoutManager, this), BorderLayout.EAST);

    // listen if user doubleclick on row to editit
    table.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent me) {
        if (me.getClickCount() == 2) {
          edit();
        }
      }
    });

  }

  public void reloadFields() {
    fireUpdateState();

    if (task != null) {
      tableModel.setRows(task.getAttributes());
    }
  }

  public void setTask(Task task) {
    this.task = task;
  }

  public void add(Collection<Attribute> attributes) {
    int rowNumber = 0;
    for (Attribute attribute : attributes) {
      rowNumber = tableModel.addRow(attribute);
    }

    if (rowNumber >= 0) {
      table.setRowSelectionInterval(rowNumber, rowNumber);
    }
    saveChangesImmediately();
  }

  public void add() {
    AttributeEditDialog dialog = new AttributeEditDialog(layoutManager, null,
        true, false, false);
    Attribute attribute = dialog.getAttribute();
    if (attribute == null) {
      return;
    }

    add(Arrays.asList(new Attribute[] { attribute }));
  }

  public void edit() {
    int rowNumber = table.getSelectedRow();
    if (rowNumber < 0) {
      return;
    }

    Attribute attribute = tableModel.getValueAt(rowNumber);
    AttributeEditDialog dialog = new AttributeEditDialog(layoutManager,
        attribute, true, false, false);
    attribute = dialog.getAttribute();
    if (attribute == null) {
      return;
    }
    tableModel.fireTableRowsUpdated(rowNumber, rowNumber);
    saveChangesImmediately();
  }

  public void remove() {
    int rowNumber = table.getSelectedRow();
    if (rowNumber < 0) {
      return;
    }
    Attribute attributeForDelete = tableModel.getValueAt(rowNumber);
    attributeForDelete.unregister();
    tableModel.removeRow(rowNumber);
    saveChangesImmediately();
  }

  public TaskAttributeTableModel getTableModel() {
    return tableModel;
  }

  private void saveChangesImmediately() {
    if (saveImmediately && task != null) {
      task.setAttributes(tableModel.getRows());
    }

    fireUpdateState();
  }

  private void fireUpdateState() {
    if (attributesPanelListener != null) {
      attributesPanelListener.handleUpdate();
    }
  }

  public String getAttributePanelTitle() {
    String title = layoutManager.getString("taskinfo.tab.attributes.title");
    if (task == null || task.getAttributes().isEmpty()) {
      return title;
    }

    return title + " (" + task.getAttributes().size() + ")";
  }
}
