package net.sf.timeslottracker.gui.layouts.classic.tasks;

import java.awt.event.ActionEvent;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import net.sf.timeslottracker.data.DataSource;
import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.gui.FavouritesInterface;
import net.sf.timeslottracker.gui.LayoutManager;

/**
 * A DeleteTask action for TreePopupMenu
 * 
 * File version: $Revision: 929 $, $Date: 2009-06-21 19:32:18 +0700 (Sun, 21 Jun
 * 2009) $ Last change: $Author: cnitsa $
 */
@SuppressWarnings("serial")
class DeleteTaskAction extends AbstractAction {

  private LayoutManager layoutManager;

  private JTree tree;

  DeleteTaskAction(LayoutManager layoutManager, JTree tree) {
    super(layoutManager.getString("taskstree.popupmenu.deleteTask.name"),
        layoutManager.getIcon("delete"));

    this.layoutManager = layoutManager;
    this.tree = tree;

  }

  public void actionPerformed(ActionEvent e) {
    TreePath selectedPath = tree.getSelectionPath();
    if (selectedPath == null) {
      return;
    }
    TaskTreeNode selectedTaskNode = (TaskTreeNode) selectedPath
        .getLastPathComponent();
    Task taskToBeDeleted = selectedTaskNode.getTask();
    if (taskToBeDeleted.getParentTask() == null) {
      String errorTitle = layoutManager
          .getString("taskstree.popupmenu.deleteTask.err");
      String errorMsg = layoutManager
          .getString("taskstree.popupmenu.deleteTask.err.cannotDeleteRoot");
      JOptionPane.showMessageDialog(tree, errorMsg, errorTitle,
          JOptionPane.WARNING_MESSAGE);
      return;
    }
    TimeSlot activeTimeSlot = layoutManager.getTimeSlotTracker()
        .getActiveTimeSlot();
    if (activeTimeSlot != null) {
      Task activeTask = activeTimeSlot.getTask();
      if (activeTask.equals(taskToBeDeleted)) {
        String errorTitle = layoutManager
            .getString("taskstree.popupmenu.deleteTask.err");
        String errorMsg = layoutManager
            .getString("taskstree.popupmenu.deleteTask.err.cannotDeleteActiveTask");
        JOptionPane.showMessageDialog(tree, errorMsg, errorTitle,
            JOptionPane.WARNING_MESSAGE);
        return;
      }
      Task parentTask = activeTask.getParentTask();
      while (parentTask != null && !parentTask.equals(taskToBeDeleted)) {
        parentTask = parentTask.getParentTask();
      }
      if (parentTask != null /* && parentTask.equals( taskToBeDeleted ) */) {
        String errorTitle = layoutManager
            .getString("taskstree.popupmenu.deleteTask.err");
        String errorMsg = layoutManager
            .getString("taskstree.popupmenu.deleteTask.err.cannotDeleteActiveTasksParent");
        JOptionPane.showMessageDialog(tree, errorMsg, errorTitle,
            JOptionPane.WARNING_MESSAGE);
        return;
      }
    }
    if (!selectedTaskNode.isLeaf()) {
      String errorTitle = layoutManager
          .getString("taskstree.popupmenu.deleteTask.question");
      String errorMsg = layoutManager
          .getString("taskstree.popupmenu.deleteTask.question.taskHasChildren");
      int answer = JOptionPane.showConfirmDialog(tree, errorMsg, errorTitle,
          JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
      if (answer != JOptionPane.YES_OPTION) {
        return;
      }
    }
    Collection<TimeSlot> timeSlots = taskToBeDeleted.getTimeslots();
    if (timeSlots != null && !timeSlots.isEmpty()) {
      String errorTitle = layoutManager
          .getString("taskstree.popupmenu.deleteTask.question");
      String errorMsg = layoutManager
          .getString("taskstree.popupmenu.deleteTask.question.taskHasTimeslots");
      int answer = JOptionPane.showConfirmDialog(tree, errorMsg, errorTitle,
          JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
      if (answer != JOptionPane.YES_OPTION) {
        return;
      }
    }

    layoutManager.getTasksInterface().selectTask(
        taskToBeDeleted.getParentTask());

    DataSource dataSource = layoutManager.getTimeSlotTracker().getDataSource();
    FavouritesInterface favourites = layoutManager.getFavouritesInterface();
    if (dataSource != null) {
      if (favourites != null) {
        favourites.removeTree(taskToBeDeleted);
      }
      dataSource.moveTask(taskToBeDeleted, null);
      DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
      model.removeNodeFromParent(selectedTaskNode);
    }
  }
}
