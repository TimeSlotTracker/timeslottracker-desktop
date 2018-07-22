package net.sf.timeslottracker.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import net.sf.timeslottracker.data.Attribute;
import net.sf.timeslottracker.data.DataSource;
import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.gui.attributes.AttributesPanel;
import net.sf.timeslottracker.utils.StringUtils;
import net.sf.timeslottracker.utils.SwingUtils;

/**
 * A simple task edit dialog.
 * 
 * @version File version: $Revision: 1127 $, $Date: 2009-06-21 18:47:38 +0700
 *          (Sun, 21 Jun 2009) $
 * @author Last change: $Author: cnitsa $
 */
@SuppressWarnings("serial")
public class TaskEditDialog extends JDialog {

  private final LayoutManager layoutManager;
  private final boolean readonly;

  private Task task;
  private final JTextField taskName = new JTextField(40);
  private final JCheckBox hidden = new JCheckBox();
  private final JTextArea taskDescription = new JTextArea(3, 40);

  private AttributesPanel attributesPanel;

  /**
   * Creates dialog for task editing
   */
  public TaskEditDialog(LayoutManager layoutManager, Task task, boolean readonly) {
    super(layoutManager.getTimeSlotTracker().getRootFrame(), layoutManager
        .getCoreString("editDialog.task.title"), true);
    this.layoutManager = layoutManager;
    this.readonly = readonly;
    this.task = task;

    reloadFields();

    createDialog();
    setVisible(true);
  }

  /**
   * Creates dialog for new task
   */
  public TaskEditDialog(LayoutManager layoutManager, String taskName,
      Collection<Attribute> attributes, boolean readonly) {
    super(layoutManager.getTimeSlotTracker().getRootFrame(), layoutManager
        .getCoreString("editDialog.task.title.add"), true);

    this.layoutManager = layoutManager;
    this.readonly = readonly;

    this.taskName.setText(taskName);

    createDialog();

    if (attributes != null) {
      attributesPanel.add(attributes);
      attributesPanel.setSaveImmediately(true);
    }

    setVisible(true);
  }

  private void reloadFields() {
    if (task == null) {
      return;
    }

    taskName.setText(task.getName());
    taskDescription.setText(task.getDescription());
    taskDescription.setCaretPosition(0);
    hidden.setSelected(task.isHidden());

    // root can't be hidden
    if (task.isRoot()) {
      hidden.setEnabled(false);
    }
  }

  private void createDialog() {
    getContentPane().setLayout(new BorderLayout());

    taskName.setEnabled(!readonly);
    taskDescription.setEnabled(!readonly);
    attributesPanel = new AttributesPanel(layoutManager, task);

    DialogPanel dialog = new DialogPanel(GridBagConstraints.HORIZONTAL, 0.0);
    dialog.addRow(layoutManager.getCoreString("editDialog.task.taskName"),
        taskName);
    JScrollPane panel = new JScrollPane(taskDescription);
    dialog.addRow(layoutManager
        .getCoreString("editDialog.task.taskDescription"));
    dialog.fillToEnd(panel);
    dialog.addRow(new JLabel(layoutManager
        .getCoreString("editDialog.task.attributesTable")));
    dialog.fillToEnd(attributesPanel);
    dialog
        .addRow(layoutManager.getCoreString("editDialog.task.hidden"), hidden);

    getContentPane().add(dialog, BorderLayout.CENTER);
    getContentPane().add(createButtons(), BorderLayout.SOUTH);

    // set some dialog properties
    setResizable(true);
    SwingUtils.setLocation(this);
    SwingUtils.setWidthHeight(this, 600, 350);
  }

  private JPanel createButtons() {
    FlowLayout layout = new FlowLayout(FlowLayout.CENTER);
    layout.setHgap(15);
    JPanel buttons = new JPanel(layout);
    String cancelName;
    if (readonly) {
      cancelName = layoutManager
          .getCoreString("editDialog.task.button.cancel.readonly");
    } else {
      cancelName = layoutManager
          .getCoreString("editDialog.task.button.cancel.editable");
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
          .getCoreString("editDialog.task.button.save");
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
   * Returns a new created or just saved task
   * 
   * @return new created or edited task or null value if a user canceled dialog
   */
  public Task getTask() {
    return task;
  }

  /**
   * Action used when a user choose cancel/close button.
   * <p>
   * It simply reset task variable, so <code>getTask</code> will return null
   * 
   * @see TaskEditDialog#getTask()
   */
  private class CancelAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      task = null;
      close();
    }
  }

  private void close() {
    SwingUtils.saveLocation(this);
    SwingUtils.saveWidthHeight(this);
    dispose();
  }

  /**
   * Class called when a user pressed "Save" button.
   * <p>
   * If task was given in constructor actual data will be set in that task; <br>
   * If this is a new task, new Task record will be created to be returned with
   * <code>getTask</code> method.
   * 
   * @see TaskEditDialog#getTask()
   */
  private class SaveAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      String name = StringUtils.trim(taskName.getText());
      if (name == null) {
        int i = JOptionPane.showConfirmDialog(TaskEditDialog.this,
            layoutManager
                .getCoreString("editDialog.task.name.cannotBeEmpty.msg"),
            layoutManager
                .getCoreString("editDialog.task.name.cannotBeEmpty.title"),
            JOptionPane.WARNING_MESSAGE);
        if (i == JOptionPane.OK_OPTION) {
          return;
        }
        close();
        return;
      }

      String description = StringUtils.trim(taskDescription.getText());
      if (task == null) {
        DataSource dataSource = layoutManager.getTimeSlotTracker()
            .getDataSource();
        task = dataSource.createTask(null, null, name, description,
            hidden.isSelected());
      } else {
        task.setName(name);
        task.setDescription(description);
        task.setHidden(hidden.isSelected());
      }
      task.setAttributes(attributesPanel.getTableModel().getRows());
      layoutManager.getTimeSlotTracker().fireTaskChanged(task);
      close();
    }
  }

}
