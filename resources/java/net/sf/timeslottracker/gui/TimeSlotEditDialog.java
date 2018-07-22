package net.sf.timeslottracker.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.apache.commons.lang.StringUtils;

import net.sf.timeslottracker.data.Attribute;
import net.sf.timeslottracker.data.DataSource;
import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.gui.attributes.AttributeEditDialog;
import net.sf.timeslottracker.gui.attributes.EditingWindow;
import net.sf.timeslottracker.gui.attributes.TableButtonsPanel;
import net.sf.timeslottracker.gui.attributes.TimeSlotAttributeTableModel;
import net.sf.timeslottracker.utils.SwingUtils;

/**
 * A simple timeslot edit dialog.
 * <p>
 * It is composed of several fields. One field for year, one for month, etc.
 * 
 * File version: $Revision: 1022 $, $Date: 2009-06-21 18:47:38 +0700 (Sun, 21 Jun
 * 2009) $ Last change: $Author: cnitsa $
 */
@SuppressWarnings("serial")
public class TimeSlotEditDialog extends JDialog implements EditingWindow {
  private LayoutManager layoutManager;
  private boolean readonly;

  private TimeSlot timeslot;
  private DatetimeEditPanel startDate;
  private DatetimeEditPanel stopDate;
  private DescriptionInputComboBox inputComboBox;

  private JTable table;
  private TimeSlotAttributeTableModel tableModel;

  public TimeSlotEditDialog(LayoutManager layoutManager, TimeSlot timeslot,
      boolean readonly) {
    super(layoutManager.getTimeSlotTracker().getRootFrame(), layoutManager
        .getCoreString("editDialog.timeslot.title"
            + (timeslot == null ? ".create" : StringUtils.EMPTY)), true);

    this.layoutManager = layoutManager;
    this.timeslot = timeslot;
    this.readonly = readonly;

    this.inputComboBox = new DescriptionInputComboBox(layoutManager, readonly);
    this.inputComboBox.addFocusListener(new SelectAllTextAction());

    createTable();
    createDialog();
    reloadFields();
    setVisible(true);
  }

  private void reloadFields() {
    if (timeslot == null) {
      return;
    }
    startDate.setDatetime(timeslot.getStartDate());
    stopDate.setDatetime(timeslot.getStopDate());
    inputComboBox.setActiveDescription(timeslot.getDescription());
    tableModel.setRows(timeslot.getAttributes());
  }

  private void createTable() {
    tableModel = new TimeSlotAttributeTableModel(layoutManager);
    table = new JTable();
    table.setAutoCreateColumnsFromModel(false);
    table.setModel(tableModel);

    for (int column = 0; column < tableModel.getColumnCount(); column++) {
      DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
      renderer.setHorizontalAlignment(tableModel.getColumnAlignment(column));
      TableColumn tableColumn = new TableColumn(column,
          tableModel.getColumnWidth(column), renderer, null);
      table.addColumn(tableColumn);
    }
  }

  private void createDialog() {
    getContentPane().setLayout(new BorderLayout());

    startDate = new DatetimeEditPanel(layoutManager, readonly, true, true);
    stopDate = new DatetimeEditPanel(layoutManager, readonly, true, true);
    inputComboBox.setEnabled(!readonly);

    DialogPanel dialog = new DialogPanel(GridBagConstraints.HORIZONTAL, 0.0);
    dialog.addRow(
        layoutManager.getCoreString("editDialog.timeslot.start.date.name"),
        startDate);
    dialog.addRow(
        layoutManager.getCoreString("editDialog.timeslot.stop.date.name"),
        stopDate);
    dialog.addRow(
        layoutManager.getCoreString("editDialog.timeslot.description.name"),
        inputComboBox);

    JScrollPane scrollTable = new JScrollPane(table);
    JPanel tablePanel = new JPanel(new BorderLayout());
    scrollTable.setPreferredSize(new Dimension(250, 130));
    scrollTable.getViewport().setBackground(table.getBackground());
    tablePanel.add(scrollTable, BorderLayout.CENTER);
    tablePanel.add(new TableButtonsPanel(layoutManager, this),
        BorderLayout.EAST);
    dialog.addRow(new JLabel(layoutManager
        .getCoreString("editDialog.timeSlot.attributesTable")));
    dialog.fillToEnd(tablePanel);
    getContentPane().add(dialog, BorderLayout.CENTER);
    getContentPane().add(createButtons(), BorderLayout.SOUTH);

    // listen if user double click on row to edit it
    table.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent me) {
        if (me.getClickCount() == 2) {
          edit();
        }
      }
    });

    // set some dialog properties
    SwingUtils.setLocation(this);
    pack();
  }

  private JPanel createButtons() {
    FlowLayout layout = new FlowLayout(FlowLayout.CENTER);
    layout.setHgap(15);
    JPanel buttons = new JPanel(layout);
    String cancelName = null;
    if (readonly) {
      cancelName = layoutManager
          .getCoreString("editDialog.timeslot.button.cancel.readonly");
    } else {
      cancelName = layoutManager
          .getCoreString("editDialog.timeslot.button.cancel.editable");
    }
    JButton cancelButton = new JButton(cancelName,
        layoutManager.getIcon("cancel"));
    CancelAction cancelAction = new CancelAction();
    cancelButton.addActionListener(cancelAction);
    buttons.add(cancelButton);

    // connect cancelAction with ESC key
    getRootPane().registerKeyboardAction(cancelAction,
        KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
        JComponent.WHEN_IN_FOCUSED_WINDOW);
    if (readonly) {
      getRootPane().setDefaultButton(cancelButton);
    } else {
      String saveName = layoutManager
          .getCoreString("editDialog.timeslot.button.save");
      JButton saveButton = new JButton(saveName, layoutManager.getIcon("save"));
      SaveAction saveAction = new SaveAction();
      saveButton.addActionListener(saveAction);
      buttons.add(saveButton);
      // set save button as a default button when in editable mode
      getRootPane().setDefaultButton(saveButton);
    }
    return buttons;
  }

  /**
   * Returns a new created or just saved timeslot
   * 
   * @return new created or edited timeslot or null value if a user canceled
   *         dialog
   */
  public TimeSlot getTimeslot() {
    return timeslot;
  }

  public void add() {
    AttributeEditDialog dialog = new AttributeEditDialog(layoutManager, null,
        false, true, false);
    Attribute attribute = dialog.getAttribute();
    if (attribute == null) {
      return;
    }
    int rowNumber = tableModel.addRow(attribute);
    if (rowNumber >= 0) {
      table.setRowSelectionInterval(rowNumber, rowNumber);
    }
  }

  public void edit() {
    int rowNumber = table.getSelectedRow();
    if (rowNumber < 0) {
      return;
    }

    Attribute attribute = tableModel.getValueAt(rowNumber);
    AttributeEditDialog dialog = new AttributeEditDialog(layoutManager,
        attribute, false, true, false);
    attribute = dialog.getAttribute();
    if (attribute == null) {
      return;
    }
    tableModel.fireTableRowsUpdated(rowNumber, rowNumber);
  }

  public void remove() {
    int rowNumber = table.getSelectedRow();
    if (rowNumber < 0) {
      return;
    }
    Attribute attributeForDelete = tableModel.getValueAt(rowNumber);
    attributeForDelete.unregister();
    tableModel.removeRow(rowNumber);
  }

  /**
   * Action used when a user choose cancel/close button.
   * <p>
   * It simply reset task variable, so <code>getTimeslot</code> will return null
   * 
   * @see #getTimeslot()
   */
  private class CancelAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      timeslot = null;
      close();
    }
  }

  private void close() {
    SwingUtils.saveLocation(TimeSlotEditDialog.this);
    dispose();
  }

  /**
   * Class called when a user pressed "Save" button.
   * <p>
   * If timeslot was given in constructor actual data will be set in that
   * timeslot; <br>
   * If this is a new timeslot, new TimeSlot record will be created to be
   * returned with <code>getTimeslot</code> method.
   * 
   * @see #getTimeslot()
   */
  private class SaveAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      try {
        String description = inputComboBox.getDescription();
        // check if a description isn't empty
        if (description == null || description.length() == 0) {
          String msgTitle = layoutManager
              .getCoreString("editDialog.timeslot.description.cannotBeEmpty.title");
          String msg = layoutManager
              .getCoreString("editDialog.timeslot.description.cannotBeEmpty.msg");
          JOptionPane.showMessageDialog(TimeSlotEditDialog.this, msg, msgTitle,
              JOptionPane.WARNING_MESSAGE);
          return;
        }

        if (timeslot == null) {
          DataSource dataSource = layoutManager.getTimeSlotTracker()
              .getDataSource();
          timeslot = dataSource.createTimeSlot(null, startDate.getDatetime(),
              stopDate.getDatetime(), description);
        } else {
          timeslot.setStartDate(startDate.getDatetime());
          timeslot.setStopDate(stopDate.getDatetime());
          timeslot.setDescription(description);
        }
        timeslot.setAttributes(tableModel.getRows());
        if (stopDate.getDatetime() != null) {
          TimeSlot activeTimeSlot = layoutManager.getTimeSlotTracker()
              .getActiveTimeSlot();
          if (timeslot == activeTimeSlot) {
            layoutManager.getTimeSlotTracker().setActiveTimeSlot(null);
            layoutManager.fireTimeSlotChanged(timeslot);
            layoutManager.getTimeSlotTracker().fireTaskChanged(timeslot.getTask());
          }
        }
        close();
      } catch (NumberFormatException nfe) {
        String errorTitle = layoutManager.getCoreString("alert.error.title");
        String errorMsg = layoutManager
            .getCoreString("editDialog.timeslot.error.NumberFormatException");
        JOptionPane.showMessageDialog(TimeSlotEditDialog.this, errorMsg,
            errorTitle, JOptionPane.ERROR_MESSAGE);
        return;
      }
    }
  }
}
