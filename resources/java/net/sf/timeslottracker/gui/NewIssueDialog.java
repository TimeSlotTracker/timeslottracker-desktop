package net.sf.timeslottracker.gui;

import edu.emory.mathcs.backport.java.util.Collections;
import net.sf.timeslottracker.integrations.issuetracker.Issue;
import net.sf.timeslottracker.utils.StringUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Dialog for selecting tracker's issue
 */
public class NewIssueDialog extends AbstractSimplePanelDialog {

  public static final int KEY_COLUMN_INDEX = 0;

  private class ApplyAction implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent action) {
      if (table.getSelectedRowCount() > 0) {
        for (int selectedRow : table.getSelectedRows()) {
          selectedKeys.add(table.getModel().getValueAt(selectedRow, KEY_COLUMN_INDEX).toString());
        }
      }
      dispose();
    }
  }

  private final JTable table;
  private final DefaultTableModel issueModel;
  private final String[] columns = new String[]{"Key", "Summary", "Assignee"};
  private final JTextField filterField = new JTextField();
  private JButton applyButton;

  private final java.util.List<String> selectedKeys = new ArrayList<>();

  public NewIssueDialog(LayoutManager layoutManager) {
    super(layoutManager, layoutManager
        .getCoreString("issueTracker.newissuedialog.title"));
    issueModel = new DefaultTableModel() {
      @Override
      public String getColumnName(int column) {
        return columns[column];
      }

      @Override
      public int getColumnCount() {
        return columns.length;
      }
    };
    table = new JTable(issueModel);
    table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    table.setColumnSelectionAllowed(false);
    TableColumnModel columnModel = table.getColumnModel();
    columnModel.getColumn(KEY_COLUMN_INDEX).setMaxWidth(110);
    columnModel.getColumn(1).setMaxWidth(450);
    columnModel.getColumn(2).setMaxWidth(250);
  }

  @Override
  protected DialogPanel getDialogPanel() {
    return new DialogPanel(GridBagConstraints.HORIZONTAL, 0.0);
  }

  @Override
  protected void fillDialogPanel(DialogPanel panel) {
    panel.addRow(coreString("issueTracker.newissuedialog.selectField"), filterField);
    filterField.addKeyListener(new KeyAdapter() {

      @Override
      public void keyReleased(KeyEvent e) {
        int foundRow = findRowByFilter();
        if (foundRow < 0) {
          table.clearSelection();
        } else {
          table.getSelectionModel().setSelectionInterval(foundRow, foundRow);
        }
      }

    });

    panel.fillToEnd(new JScrollPane(table));
  }

  private int findRowByFilter() {
    String filterFieldText = filterField.getText();
    if (StringUtils.isBlank(filterFieldText)) {
      return -1;
    }

    String filterTestNormalized = filterFieldText.trim().toLowerCase();
    int rowCount = table.getModel().getRowCount();
    for (int i = 0; i < rowCount; i++) {
      for (int j = 0; j < columns.length; j++) {
        String valueAt = table.getModel().getValueAt(i, j).toString();
        if (valueAt.toLowerCase().contains(filterTestNormalized)) {
          return i;
        }
      }
    }
    return -1;
  }

  @Override
  protected void beforeShow() {
    setResizable(true);
  }

  @Override
  protected int getDefaultHeight() {
    return 200;
  }

  @Override
  protected int getDefaultWidth() {
    return 300;
  }

  /**
   * @return get selected issue keys
   */
  public List<String> getKeys() {
    return selectedKeys;
  }

  @Override
  protected Collection<JButton> getButtons() {
    applyButton = new JButton(
        coreString("issueTracker.newissuedialog.apply.name"));
    applyButton.addActionListener(new ApplyAction());
    applyButton.setIcon(icon("save"));

    return Collections.singletonList(applyButton);
  }

  @Override
  protected JButton getDefaultButton() {
    return applyButton;
  }

  /**
   * Add issue in dialog
   */
  public void add(Issue issue) {
    issueModel.addRow(new Object[]{issue.getKey(), (issue.isSubTask() ? "-" : "") + issue.getSummary(), issue.getAssignee()});
  }

}
