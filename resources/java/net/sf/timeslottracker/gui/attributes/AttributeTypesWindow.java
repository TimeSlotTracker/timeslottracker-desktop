package net.sf.timeslottracker.gui.attributes;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Locale;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableColumn;

import net.sf.timeslottracker.core.Action;
import net.sf.timeslottracker.data.AttributeType;
import net.sf.timeslottracker.data.DataSource;
import net.sf.timeslottracker.gui.LayoutManager;

/**
 * A class witch contains gui for configuration of attributes.
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class AttributeTypesWindow extends JDialog {

  private LayoutManager layoutManager;

  private DataSource dataSource;

  private Locale locale;

  private JTable table;

  private TypeTableModel tableModel;

  public AttributeTypesWindow(LayoutManager layoutManager) {
    super(layoutManager.getTimeSlotTracker().getRootFrame(), layoutManager
        .getCoreString("attributes.window.title"), true);
    this.layoutManager = layoutManager;

    locale = layoutManager.getTimeSlotTracker().getLocale();
    createWindow();
    dataSource = layoutManager.getTimeSlotTracker().getDataSource();
    tableModel.setRows(dataSource.getAttributeTypes());

    // listen if user doubleclick on row to edit it
    table.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent me) {
        if (me.getClickCount() == 2) {
          int row = table.rowAtPoint(me.getPoint());
          edit();
        }
      }
    });
    setSize(750, 400);
    setLocationRelativeTo(getRootPane());
    setVisible(true);
  }

  private void createWindow() {
    getContentPane().setLayout(new BorderLayout());

    // add panel to center to make a border around the table
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(new EmptyBorder(5, 5, 5, 5));

    createTable();

    JScrollPane scrollTable = new JScrollPane(table);
    scrollTable.getViewport().setBackground(table.getBackground());
    panel.add(scrollTable, BorderLayout.CENTER);
    getContentPane().add(panel, BorderLayout.CENTER);
    ButtonsPanel buttonsPanel = new ButtonsPanel(layoutManager, this);
    getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
  }

  /**
   * Creates new table object to show task's timeslots
   */
  private void createTable() {
    tableModel = new TypeTableModel(layoutManager);
    table = new JTable();
    table.setAutoCreateColumnsFromModel(false);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    table.setModel(tableModel);

    for (int column = 0; column < tableModel.getColumnCount(); column++) {
      TableColumn tableColumn = new TableColumn(column,
          tableModel.getColumnWidth(column),
          tableModel.getColumnCellRenderer(column),
          tableModel.getColumnCellEditor(column));
      table.addColumn(tableColumn);
    }
  }

  /**
   * Adds new attribute type.
   */
  void add() {
    AttributeTypeEditDialog dialog = new AttributeTypeEditDialog(layoutManager,
        null, false);
    AttributeType attributeType = dialog.getAttributeType();
    if (attributeType == null) {
      return;
    }
    int rowNumber = tableModel.addRow(attributeType);
    if (rowNumber >= 0) {
      table.setRowSelectionInterval(rowNumber, rowNumber);
    }
  }

  /**
   * Edits selected attribute type
   */
  void edit() {
    int rowNumber = table.getSelectedRow();
    if (rowNumber < 0) {
      return;
    }

    AttributeType attributeType = tableModel.getValueAt(rowNumber);
    if (attributeType.isBuiltin()) {
      return;
    }
    AttributeTypeEditDialog dialog = new AttributeTypeEditDialog(layoutManager,
        attributeType, false);
    attributeType = dialog.getAttributeType();
    if (attributeType == null) {
      return;
    }
    tableModel.fireTableRowsUpdated(rowNumber, rowNumber);
  }

  /**
   * Removes selected attribute type
   */
  void remove() {
    int rowNumber = table.getSelectedRow();
    if (rowNumber < 0) {
      return;
    }
    AttributeType type = tableModel.getValueAt(rowNumber);
    if (type.isBuiltin()) {
      return;
    }

    Collection usedBy = type.getRegisteredObjects();
    if (usedBy.size() > 0) {
      Object[] args = { new Integer(usedBy.size()) };
      String errorTitle = layoutManager
          .getCoreString("attributes.window.removeType.notEmpty.title");
      String errorMessage = layoutManager.getCoreString(
          "attributes.window.removeType.notEmpty", args);
      JOptionPane.showMessageDialog(this, errorMessage, errorTitle,
          JOptionPane.WARNING_MESSAGE);
    } else {
      tableModel.removeRow(rowNumber);
    }
  }

  /**
   * Closes the configuration window. Doesn't perform any saving. Just closes
   * the window.
   */
  void closeWindow() {
    layoutManager.getTimeSlotTracker().fireAction(
        Action.ACTION_CONFIGURATION_CHANGED);
    dispose();
  }

  /**
   * Saves changes into timeSlotTracker structures
   */
  void saveChanges() {
    dataSource.saveAttributeTypes(tableModel.getRows());
    closeWindow();
  }

}
