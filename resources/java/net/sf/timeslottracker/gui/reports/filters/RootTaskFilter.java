package net.sf.timeslottracker.gui.reports.filters;

import java.awt.FlowLayout;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.xml.transform.Transformer;

import net.sf.timeslottracker.data.DataSource;
import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.gui.DialogPanel;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.reports.ReportConfiguration;
import net.sf.timeslottracker.gui.reports.ReportContext;

/**
 * A filter with a combobox to choose the root task to generate report.
 * 
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class RootTaskFilter extends JPanel implements Filter {
  private static final Logger LOG = Logger
      .getLogger("net.sf.timeslottracker.gui.reports.RootTaskFilter");

  private LayoutManager layoutManager;

  private JLabel label;

  private JComboBox comboBox;

  /** Holds the valid tasks **/
  private Vector validTasks;

  public RootTaskFilter(LayoutManager layoutManager) {
    super(new FlowLayout(FlowLayout.LEFT, 1, 0));
    this.layoutManager = layoutManager;
    constructPanel();
  }

  public void setReportConfiguration(ReportConfiguration reportConfiguration) {
  }

  /**
   * Sets the starting point to report from.
   * 
   * @param reportContext
   */
  public void setReportContext(ReportContext reportContext) {
    Object rootTaskObject = reportContext
        .getReportContext(ReportContext.Context.STARTING_TASK);
    if (rootTaskObject == null) {
      return;
    }

    if (rootTaskObject instanceof Task) {
      Task rootTask = Task.class.cast(rootTaskObject);
      selectTask(rootTask);
    } else {
      LOG.severe("ReportContext of name STARTING_TASK, but not implements the Task interface! rootTaskObject = ["
          + rootTaskObject + "]");
    }
  }

  /**
   * Finds a rootTask in combo rows and if found selects it.
   * 
   * @param rootTask
   *          a task to find.
   */
  private void selectTask(Task rootTask) {
    int row = comboBox.getItemCount();
    while (row > 0) {
      row--;
      ComboBoxTask comboBoxTask = ComboBoxTask.class.cast(comboBox
          .getItemAt(row));
      if (comboBoxTask != null) {
        Task taskToCompare = comboBoxTask.getTask();
        if (taskToCompare != null
            && taskToCompare.getId().equals(rootTask.getId())) {
          comboBox.setSelectedIndex(row);
          break;
        }
      }
    }
  }

  private void constructPanel() {
    label = new JLabel(
        layoutManager.getCoreString("reports.filter.chooseRootTask.label"));

    comboBox = new JComboBox();
    fillComboBox();
    add(comboBox);
  }

  /**
   * Initiates the fill process - gets root task from DataSource and then fills
   * combo.
   */
  private void fillComboBox() {
    DataSource source = layoutManager.getTimeSlotTracker().getDataSource();
    if (source == null) {
      return;
    }

    // add empty record - match every task
    comboBox.addItem(new ComboBoxTask(null, ""));

    // fill with task tree
    Task root = source.getRoot();
    addChildrenTasks(root, "");
  }

  /**
   * Adds children to combo with given prefix.
   */
  private void addChildrenTasks(Task parent, String prefix) {
    if (parent == null) {
      return;
    }
    Collection childrenCollection = parent.getChildren();
    if (childrenCollection == null) {
      return;
    }
    Iterator children = childrenCollection.iterator();
    while (children.hasNext()) {
      Task task = (Task) children.next();
      comboBox.addItem(new ComboBoxTask(task, prefix));
      addChildrenTasks(task, prefix + "+-");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.sf.timeslottracker.gui.DialogPanelUpdater#update(net.sf.timeslottracker
   * .gui.DialogPanel)
   */
  @Override
  public void update(DialogPanel panel) {
    panel.addRow(label, this);
  }

  public void beforeStart() {
    ComboBoxTask rootComboTask = (ComboBoxTask) comboBox.getSelectedItem();
    if (rootComboTask == null) {
      return;
    }
    Task rootTask = rootComboTask.getTask();
    if (rootTask == null) {
      validTasks = null;
      return;
    }
    validTasks = new Vector();
    validTasks.add(rootTask);
    addValidChildren(rootTask);
  }

  private void addValidChildren(Task parent) {
    Collection children = parent.getChildren();
    if (children == null) {
      return;
    }
    validTasks.addAll(children);
    Iterator tasks = children.iterator();
    while (tasks.hasNext()) {
      Task task = (Task) tasks.next();
      addValidChildren(task);
    }
  }

  public void beforeStart(Transformer transformer) {
  }

  public boolean matches(Task task) {
    if (validTasks == null) {
      // there is no filter.
      return true;
    }
    return validTasks.contains(task);
  }

  public boolean matches(TimeSlot timeSlot) {
    return true; // every timeslot should be enclosed. No filter.
  }

  /**
   * A class which represents one row in comboBox.
   * <p>
   * It's composed of task and prefix to nicely display task and looks like the
   * hierarchy.
   */
  private class ComboBoxTask {
    private Task task;

    private String taskName;

    private ComboBoxTask(Task task, String prefix) {
      this.task = task;
      if (task == null) {
        taskName = "";
      } else {
        taskName = task.toString();
        if (taskName.length() > 50) {
          taskName = taskName.substring(0, 47) + "...";
        }
      }
      taskName = prefix + " " + taskName;
    }

    private Task getTask() {
      return task;
    }

    public String toString() {
      return taskName;
    }
  }

}
