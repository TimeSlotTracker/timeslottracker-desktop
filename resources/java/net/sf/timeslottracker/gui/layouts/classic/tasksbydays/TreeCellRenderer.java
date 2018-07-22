package net.sf.timeslottracker.gui.layouts.classic.tasksbydays;

import java.util.Calendar;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;

import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.core.TimeSlotTracker;
import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.utils.TimeUtils;

/**
 * Custom CellRenderer for TasksTree class
 * 
 * File version: $Revision: 926 $, $Date: 2009-06-21 18:47:38 +0700 (Sun, 21 Jun
 * 2009) $ Last change: $Author: cnitsa $
 */
@SuppressWarnings("serial")
public class TreeCellRenderer extends DefaultTreeCellRenderer {

  private TimeSlotTracker timeSlotTracker;

  private final ImageIcon iconIssueTask;

  private final ImageIcon iconClockEmpty;

  private final ImageIcon iconDate;

  public TreeCellRenderer(LayoutManager layoutManager) {
    super();

    this.timeSlotTracker = layoutManager.getTimeSlotTracker();
    this.iconIssueTask = layoutManager.getIcon("/users/jira-task-empty.gif");
    this.iconDate = layoutManager.getIcon("calendar");
    this.iconClockEmpty = layoutManager.getIcon("/users/clock-empty.gif");
  }

  public java.awt.Component getTreeCellRendererComponent(JTree tree,
      Object value, boolean selected, boolean expanded, boolean leaf, int row,
      boolean hasFocus) {

    super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf,
        row, hasFocus);

    TreeNode treeNode = (TreeNode) value;
    if (!(treeNode instanceof TaskNode)) {
      setIcon(iconDate);
    } else {
      Task task = ((TaskNode) treeNode).getTask();
      // Show task summary by day in node text
      Configuration configuration = timeSlotTracker.getConfiguration();
      if (configuration != null
          && configuration.getBoolean(
              Configuration.CUSTOM_SHOW_TASK_BY_DAYS_SUMMARY, false)) {
        setText(getTextWithDayDuration(treeNode));
      }
      if (timeSlotTracker.getIssueTracker().isIssueTask(task)) {
        setIcon(iconIssueTask);
      } else {
        setIcon(iconClockEmpty);
      }
    }
    return this;
  }

  /**
   * Formats node text with task duration in format "hh:mm".
   */
  private String getTextWithDayDuration(TreeNode treeNode) {
    Task task = ((TaskNode) treeNode).getTask();
    Date endDate = null;

    // End date, next day
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(((DateNode) treeNode.getParent()).getDate());
    calendar.roll(Calendar.DAY_OF_YEAR, 1);
    endDate = calendar.getTime();

    return task.getName()
        + " ("
        + TimeUtils.formatDuration(timeSlotTracker, task.getTime(true,
            ((DateNode) treeNode.getParent()).getDate(), endDate)) + ")";
  }

}
