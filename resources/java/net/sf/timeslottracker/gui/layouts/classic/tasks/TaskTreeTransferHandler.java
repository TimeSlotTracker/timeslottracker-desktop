package net.sf.timeslottracker.gui.layouts.classic.tasks;

import java.awt.Point;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultTreeModel;

import net.sf.timeslottracker.data.Attribute;
import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.dnd.DataFlavors;
import net.sf.timeslottracker.gui.dnd.handlers.TaskHandler;
import net.sf.timeslottracker.gui.dnd.handlers.TimeSlotHandler;
import net.sf.timeslottracker.gui.dnd.handlers.Utils;
import net.sf.timeslottracker.gui.layouts.classic.JMenuItem;

/**
 * @author User: zgibek Date: 2008-08-30 Time: 18:35:02 $Id:
 *         TaskTreeTransferHandler.java 946 2009-12-12 10:20:57Z cnitsa $
 */
class TaskTreeTransferHandler extends TransferHandler {
  TaskHandler taskHandler;
  TimeSlotHandler timeSlotHandler;

  int wasAction; // current action: copy or move
  boolean copyWithChildren; // do copy action with children
  private final LayoutManager layoutManager;
  private TasksTree tasksTree;

  public TaskTreeTransferHandler(TasksTree tasksTree,
      LayoutManager layoutManager) {
    this.tasksTree = tasksTree;
    this.layoutManager = layoutManager;
    taskHandler = new TaskHandler(layoutManager);
    timeSlotHandler = new TimeSlotHandler(layoutManager);
  }

  public int getSourceActions(JComponent c) {
    return TransferHandler.COPY_OR_MOVE;
  }

  protected Transferable createTransferable(JComponent c) {
    TaskTreeNode selectedtaskTreeNode = (TaskTreeNode) tasksTree.tree
        .getLastSelectedPathComponent();
    Task task = selectedtaskTreeNode.getTask();

    if (task == tasksTree.root.getTask()) {
      return null;
    }

    return taskHandler.wrap(task);
  }

  public void exportToClipboard(JComponent comp, Clipboard clip, int action)
      throws IllegalStateException {
    super.exportToClipboard(comp, clip, action);

    wasAction = action;

    if (action == MOVE) {
      TaskTreeNode cutTaskTreeNode = (TaskTreeNode) tasksTree.tree
          .getLastSelectedPathComponent();
      cutTaskTreeNode.setCut(true);
      layoutManager.getTimeSlotTracker().fireTaskChanged(
          cutTaskTreeNode.getTask());
    }
  }

  public boolean canImport(JComponent comp, DataFlavor[] flavors) {
    return taskHandler.canImport(flavors) || timeSlotHandler.canImport(flavors);
  }

  public boolean importData(JComponent comp, final Transferable t) {
    copyWithChildren = false; // by default do not copy children

    final TaskTreeNode targetTaskNode = (TaskTreeNode) tasksTree.tree
        .getLastSelectedPathComponent();
    final Task targetTask = targetTaskNode.getTask();

    if (t.isDataFlavorSupported(DataFlavors.TIME_SLOT)) {
      return timeSlotHandler.importData(t, targetTask);
    } else if (t.isDataFlavorSupported(DataFlavors.TASK)) {

      final Task sourceTask = taskHandler.getTask(t);
      final TaskTreeNode sourceTaskNode = tasksTree.findTask(tasksTree.root,
          sourceTask);

      final JPopupMenu jPopupMenu = new JPopupMenu();
      jPopupMenu.add(new JMenuItem(new AbstractAction(layoutManager
          .getString("taskstree.pastepopupmenu.insertBefore")) {
        public void actionPerformed(ActionEvent e) {
          int mark = -1;
          if (wasAction == TransferHandler.MOVE) {
            if (reorderNode(t, targetTaskNode, mark)) {
              tasksTree.setCutOff(sourceTaskNode, layoutManager);
            }
          } else if (wasAction == TransferHandler.COPY) {
            copyNode(t, targetTaskNode, mark, copyWithChildren);
          }
        }
      }));
      jPopupMenu.add(new JMenuItem(new AbstractAction(layoutManager
          .getString("taskstree.pastepopupmenu.insertAfter")) {
        public void actionPerformed(ActionEvent e) {
          int mark = +1;
          if (wasAction == TransferHandler.MOVE) {
            if (reorderNode(t, targetTaskNode, mark)) {
              tasksTree.setCutOff(sourceTaskNode, layoutManager);
            }
          } else if (wasAction == TransferHandler.COPY) {
            copyNode(t, targetTaskNode, mark, copyWithChildren);
          }
        }
      }));
      jPopupMenu.add(new JMenuItem(new AbstractAction(layoutManager
          .getString("taskstree.pastepopupmenu.pasteAsChild")) {
        public void actionPerformed(ActionEvent e) {
          if (wasAction == TransferHandler.MOVE) {
            // moving tasks action
            if (!taskHandler.moveData(t, targetTask, -1)) {
              return;
            }
            DefaultTreeModel model = (DefaultTreeModel) tasksTree.tree
                .getModel();
            model.removeNodeFromParent(sourceTaskNode);
            model.insertNodeInto(sourceTaskNode, targetTaskNode,
                targetTaskNode.getChildCount());
            tasksTree.selectTask(sourceTask);
            tasksTree.setCutOff(sourceTaskNode, layoutManager);
          } else if (wasAction == TransferHandler.COPY) {
            copyNode(t, targetTaskNode, 0, copyWithChildren);
          }
        }
      }));

      if (wasAction == TransferHandler.COPY) {
        jPopupMenu.addSeparator();
        jPopupMenu.add(new JCheckBoxMenuItem(new AbstractAction(layoutManager
            .getString("taskstree.pastepopupmenu.withChildren")) {
          public void actionPerformed(ActionEvent e) {
            copyWithChildren = true;
            jPopupMenu.setVisible(true);
          }
        }));

      }
      jPopupMenu.addSeparator();
      jPopupMenu.add(new JMenuItem(new AbstractAction(layoutManager
          .getString("taskstree.pastepopupmenu.cancel")) {
        public void actionPerformed(ActionEvent e) {
          Utils.clearClipboard();
          tasksTree.setCutOff(sourceTaskNode, layoutManager);
        }
      }));

      // determine point to show popupmenu
      Point point = tasksTree.getMenuPoint(false);
      jPopupMenu.show(tasksTree, (int) point.getX(), (int) point.getY());

      // TODO return real action result
      return true;
    } else if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
      Object o = DataFlavors.getTransferData(t, DataFlavor.stringFlavor);
      if (o != null && o.toString().trim().length() != 0) {
        tasksTree.add(o.toString(), (Collection<Attribute>) null);
      }
    }

    return false;
  }

  private boolean reorderNode(Transferable t, TaskTreeNode targetTaskNode,
      int indexChange) {
    Task sourceTask = taskHandler.getTask(t);
    Task targetTask = targetTaskNode.getTask();
    if (sourceTask.equals(targetTask)) {
      return false;
    }

    TaskTreeNode sourceTaskNode = tasksTree
        .findTask(tasksTree.root, sourceTask);
    TaskTreeNode sourceParentNode = (TaskTreeNode) sourceTaskNode.getParent();
    if (sourceParentNode == null) { // check if root
      return false;
    }
    TaskTreeNode targetParentNode = (TaskTreeNode) targetTaskNode.getParent();
    if (targetParentNode == null) { // check if root
      return false;
    }

    DefaultTreeModel model = (DefaultTreeModel) tasksTree.tree.getModel();
    model.removeNodeFromParent(sourceTaskNode);

    int targetNodeIndex = model.getIndexOfChild(targetParentNode,
        targetTaskNode);
    int newNodeIndex = indexChange < 0 ? targetNodeIndex : targetNodeIndex + 1;

    model.insertNodeInto(sourceTaskNode, targetParentNode, newNodeIndex);

    taskHandler.moveData(t, targetTask, newNodeIndex);

    tasksTree.selectTask(sourceTask);

    return true;
  }

  private void copyNode(Transferable t, TaskTreeNode targetTaskNode,
      int indexChange, boolean copyWithChildren) {
    Task sourceTask = taskHandler.getTask(t);
    Task targetTask = targetTaskNode.getTask();

    TaskTreeNode sourceTaskNode = tasksTree
        .findTask(tasksTree.root, sourceTask);
    TaskTreeNode sourceParentNode = (TaskTreeNode) sourceTaskNode.getParent();
    if (sourceParentNode == null) { // check if root
      return;
    }
    TaskTreeNode targetParentNode = (TaskTreeNode) targetTaskNode.getParent();
    if (targetParentNode == null) { // check if root
      return;
    }

    DefaultTreeModel model = (DefaultTreeModel) tasksTree.tree.getModel();

    int targetNodeIndex = model.getIndexOfChild(targetParentNode,
        targetTaskNode);
    int newNodeIndex = indexChange < 0 ? targetNodeIndex : targetNodeIndex + 1;
    if (indexChange == 0) {
      newNodeIndex = -1;
    }

    Task newTask = taskHandler.copyData(t, targetTask, newNodeIndex,
        copyWithChildren);

    tasksTree.addTask(newNodeIndex == -1 ? targetTaskNode : targetParentNode,
        newTask, newNodeIndex, copyWithChildren, true);
  }

  @Override
  protected void exportDone(JComponent source, Transferable data, int action) {
    super.exportDone(source, data, action);

    wasAction = action;
  }

}
