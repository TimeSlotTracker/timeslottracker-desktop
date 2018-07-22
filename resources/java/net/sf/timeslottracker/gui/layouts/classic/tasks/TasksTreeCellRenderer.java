package net.sf.timeslottracker.gui.layouts.classic.tasks;

import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import net.sf.timeslottracker.core.TimeSlotTracker;
import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.gui.LayoutManager;

/**
 * Custom CellRenderer for TasksTree class
 * 
 * File version: $Revision: 998 $, $Date: 2010-09-17 16:01:41 +0700 (Fri, 17 Sep
 * 2010) $ Last change: $Author: cnitsa $
 */
@SuppressWarnings("serial")
class TasksTreeCellRenderer extends DefaultTreeCellRenderer {

  private LayoutManager layoutManager;

  private TimeSlotTracker timeSlotTracker;

  private Font inactiveTaskFont;

  private Font activeTaskFont;

  private Color hiddenTextNonSelectedColor;

  private Color hiddenTextSelectedColor;

  private Color textNonSelectedColor;

  private Color textSelectedColor;

  private final ImageIcon iconCut;

  private final ImageIcon iconClock;

  private final ImageIcon iconClockEmpty;

  private final ImageIcon iconPause;

  private final ImageIcon iconIssueTask;

  TasksTreeCellRenderer(LayoutManager layoutManager) {
    super();

    this.layoutManager = layoutManager;
    this.timeSlotTracker = layoutManager.getTimeSlotTracker();
    this.iconCut = layoutManager.getIcon("cut");
    this.iconClock = layoutManager.getIcon("title.icon");
    this.iconClockEmpty = layoutManager.getIcon("/users/clock-empty.gif");
    this.iconPause = layoutManager.getIcon("pause");
    this.iconIssueTask = layoutManager.getIcon("/users/jira-task-empty.gif");
  }

  public java.awt.Component getTreeCellRendererComponent(JTree tree,
      Object value, boolean selected, boolean expanded, boolean leaf, int row,

      boolean hasFocus) {
    // getting active task and timeslot
    TimeSlot activeTimeSlot = timeSlotTracker.getActiveTimeSlot();
    TaskTreeNode taskTreeNode = (TaskTreeNode) value;
    Task task = taskTreeNode.getTask();

    // setting colors
    if (textNonSelectedColor == null) {
      this.textNonSelectedColor = getTextNonSelectionColor();
      this.textSelectedColor = getTextSelectionColor();
      this.hiddenTextNonSelectedColor = Color.GRAY;
      this.hiddenTextSelectedColor = Color.GRAY;
    }

    // if task hidden (before call super method!)
    if (task.isHidden()) {
      setTextNonSelectionColor(hiddenTextNonSelectedColor);
      setTextSelectionColor(hiddenTextSelectedColor);
    } else {
      setTextNonSelectionColor(textNonSelectedColor);
      setTextSelectionColor(textSelectedColor);
    }

    super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf,
        row, hasFocus);

    // saving font
    if (inactiveTaskFont == null) {
      inactiveTaskFont = getFont();
      activeTaskFont = inactiveTaskFont.deriveFont(Font.BOLD);
    }

    // setting icon and font
    if (taskTreeNode.isCut()) {
      setIcon(iconCut);
    } else {
      if (isActive(activeTimeSlot, task)) {
        setFont(activeTaskFont);

        if (activeTimeSlot.getStartDate() != null) {
          setIcon(iconClock);
        } else {
          setIcon(iconPause);
        }
      } else {
        setFont(inactiveTaskFont);

        if (timeSlotTracker.getIssueTracker().isIssueTask(task)) {
          setIcon(iconIssueTask);
        } else {
          setIcon(iconClockEmpty);
        }
      }
    }

    // set style if favorites contains this task
    if (layoutManager.getFavouritesInterface().contains(task)) {
      setText(getText() + "*");
    }

    return this;
  }

  private boolean isActive(TimeSlot activeTimeSlot, Task task) {
    return activeTimeSlot != null && task.equals(activeTimeSlot.getTask());
  }

}
