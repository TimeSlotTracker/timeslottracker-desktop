package net.sf.timeslottracker.gui;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import net.sf.timeslottracker.integrations.issuetracker.Issue;
import net.sf.timeslottracker.utils.StringUtils;

/**
 * Dialog for selecting tracker's issue
 * 
 * @version File version: $Revision: 1.23 $, $Date: 2007/04/07 03:59:30 $
 * @author Last change: $Author: cnitsa $
 */
public class NewIssueDialog extends AbstractSimplePanelDialog {

  private class ApplyAction implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent action) {
      Object selectedValue = list.getSelectedValue();
      Issue issue = selectedValue == null ? findIssue() : (Issue) selectedValue;
      selectedKey = issue == null ? keyField.getText() : issue.getKey();

      dispose();
    }
  }

  private final JTextField keyField = new JTextField();

  private final JList list = new JList();

  private String selectedKey;

  private JButton applyButton;

  private List<Issue> issues = new ArrayList<Issue>();

  private DefaultListModel issueModel = new DefaultListModel();

  public NewIssueDialog(LayoutManager layoutManager) {
    super(layoutManager, layoutManager
        .getCoreString("issueTracker.newissuedialog.title"));
  }

  @Override
  protected DialogPanel getDialogPanel() {
    return new DialogPanel(GridBagConstraints.HORIZONTAL, 0.0);
  }

  @Override
  protected void fillDialogPanel(DialogPanel panel) {
    panel.addRow(coreString("issueTracker.newissuedialog.selectField"),
        keyField);
    keyField.addKeyListener(new KeyAdapter() {

      @Override
      public void keyReleased(KeyEvent e) {
        Issue issue = findIssue();
        if (issue == null) {
          list.clearSelection();
        } else {
          list.setSelectedValue(issue, true);
        }
      }

    });

    list.setModel(issueModel);
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    panel.fillToEnd(new JScrollPane(list));
  }

  private Issue findIssue() {
    String selected = keyField.getText();
    if (StringUtils.isBlank(selected)) {
      return null;
    }

    String trimmedValue = selected.trim().toLowerCase();
    for (Issue issue : issues) {
      if (issue.getKey().toLowerCase().contains(trimmedValue)
          || issue.getSummary().toLowerCase().contains(trimmedValue)) {
        return issue;
      }
    }
    return null;
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
   * @return get selected issue key
   */
  public String getKey() {
    return selectedKey;
  }

  @Override
  protected Collection<JButton> getButtons() {
    applyButton = new JButton(
        coreString("issueTracker.newissuedialog.apply.name"));
    applyButton.addActionListener(new ApplyAction());
    applyButton.setIcon(icon("save"));

    return Arrays.asList(applyButton);
  }

  @Override
  protected JButton getDefaultButton() {
    return applyButton;
  }

  /**
   * Add issue in dialog
   */
  public void add(Issue issue) {
    issueModel.addElement(issue);
    issues.add(issue);
  }

}
