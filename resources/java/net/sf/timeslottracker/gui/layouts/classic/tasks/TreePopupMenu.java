package net.sf.timeslottracker.gui.layouts.classic.tasks;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;

import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.gui.FavouritesInterface;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.actions.AddTaskAction;
import net.sf.timeslottracker.gui.dnd.DataFlavors;
import net.sf.timeslottracker.gui.dnd.selections.TaskSelection;
import net.sf.timeslottracker.gui.dnd.selections.TimeSlotSelection;
import net.sf.timeslottracker.gui.dnd.utils.TransferActionListener;
import net.sf.timeslottracker.gui.layouts.classic.JMenuItem;
import net.sf.timeslottracker.gui.reports.ReportsHelper;

/**
 * A class acting as a MousePopupListener
 * 
 * TODO move action's classes into net.sf.timeslottracker.gui.actions
 * 
 * @version File version: $Revision: 1113 $, $Date: 2009-06-21 19:32:18 +0700
 *          (Sun, 21 Jun 2009) $
 * @author Last change: $Author: ghermans $
 */
@SuppressWarnings("serial")
public class TreePopupMenu extends JPopupMenu {

  /** logging using java.util.logging package * */
  private static final Logger LOG = Logger
      .getLogger("net.sf.timeslottracker.gui.layouts.classic.tasks");

  private LayoutManager layoutManager;

  private TasksTree tasksTree;

  private JTree tree;

  private JMenuItem addJiraTask;

  private JMenuItem gotoIssueUrl;

  private JMenuItem startTask;

  private JMenuItem pauseTask;

  private JMenuItem stopTask;

  private JMenuItem gotoActiveTask;

  private JMenuItem restartLastTimeSlot;

  private JMenuItem copyTask;

  private JMenuItem cutTask;

  private JMenuItem pasteTask;

  private JMenuItem deleteTask;

  private JMenuItem hideTask;

  private JMenuItem moveTaskUp;

  private JMenuItem moveTaskDown;

  private JMenu reportsMenu;

  private JMenuItem favouritesAddTask;

  private JMenuItem favouritesRemoveTask;

  /** contains actually selected task * */
  private TaskTreeNode selectedTaskNode;

  private final TransferActionListener transferActionListener = new TransferActionListener();

  TreePopupMenu(final LayoutManager layoutManager, final TasksTree tasksTree,
      final JTree tree) {
    super(layoutManager.getString("taskstree.popupmenu.title"));
    this.layoutManager = layoutManager;
    this.tasksTree = tasksTree;
    this.tree = tree;
    startTask = new JMenuItem(new StartTaskAction());
    KeyStroke keyStroke = KeyStroke.getKeyStroke(layoutManager
        .getString("taskstree.popupmenu.startTask.mnemonic"));
    startTask.setMnemonic(keyStroke.getKeyCode());
    startTask.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0));
    add(startTask);

    pauseTask = new JMenuItem(new PauseTaskAction());
    keyStroke = KeyStroke.getKeyStroke(layoutManager
        .getString("taskstree.popupmenu.pauseTask.mnemonic"));
    pauseTask.setMnemonic(keyStroke.getKeyCode());
    add(pauseTask);

    stopTask = new JMenuItem(new StopTaskAction());
    keyStroke = KeyStroke.getKeyStroke(layoutManager
        .getString("taskstree.popupmenu.stopTask.mnemonic"));
    stopTask.setMnemonic(keyStroke.getKeyCode());
    stopTask.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0));
    add(stopTask);

    gotoActiveTask = new JMenuItem(new GotoActiveAction());
    keyStroke = KeyStroke.getKeyStroke(layoutManager
        .getString("taskstree.popupmenu.gotoActiveTask.mnemonic"));
    gotoActiveTask.setMnemonic(keyStroke.getKeyCode());
    add(gotoActiveTask);

    restartLastTimeSlot = new JMenuItem(new RestartTimingAction());
    keyStroke = KeyStroke.getKeyStroke(layoutManager
        .getString("taskstree.popupmenu.restartTiming.mnemonic"));
    restartLastTimeSlot.setMnemonic(keyStroke.getKeyCode());
    add(restartLastTimeSlot);

    addSeparator();

    JMenuItem editTask = new JMenuItem(new ActionEditTask());
    keyStroke = KeyStroke.getKeyStroke(layoutManager
        .getString("taskstree.popupmenu.editTask.mnemonic"));
    editTask.setMnemonic(keyStroke.getKeyCode());
    editTask.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
    add(editTask);

    JMenuItem cloneTask = new JMenuItem(new ActionCloneTask());
    add(cloneTask);

    JMenuItem addTask = new JMenuItem(new AddTaskAction(layoutManager));
    add(addTask);

    addSeparator();

    JMenu issueTrackerMenuItems = new JMenu(
        layoutManager.getString("taskstree.popupmenu.issueTracker.name"));
    addJiraTask = new JMenuItem(new ActionAddIssueTrackerTask());
    keyStroke = KeyStroke.getKeyStroke(layoutManager
        .getString("taskstree.popupmenu.addJiraTask.mnemonic"));
    addJiraTask.setMnemonic(keyStroke.getKeyCode());
    addJiraTask.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT,
        ActionEvent.ALT_MASK));
    issueTrackerMenuItems.add(addJiraTask);

    gotoIssueUrl = new JMenuItem(new ActionGotoIssueTrackerTask());
    keyStroke = KeyStroke.getKeyStroke(layoutManager
        .getString("taskstree.popupmenu.gotoIssueUrl.mnemonic"));
    gotoIssueUrl.setMnemonic(keyStroke.getKeyCode());
    gotoIssueUrl.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J,
        ActionEvent.CTRL_MASK));
    issueTrackerMenuItems.add(gotoIssueUrl);

    add(issueTrackerMenuItems);

    addSeparator();

    moveTaskUp = new JMenuItem(new MoveTaskUpAction());
    moveTaskUp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP,
        ActionEvent.SHIFT_MASK));
    add(moveTaskUp);

    moveTaskDown = new JMenuItem(new MoveTaskDownAction());
    moveTaskDown.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,
        ActionEvent.SHIFT_MASK));
    add(moveTaskDown);

    addSeparator();

    copyTask = new JMenuItem(null);
    copyTask.setActionCommand((String) TransferHandler.getCopyAction()
        .getValue(Action.NAME));
    copyTask.addActionListener(transferActionListener);
    copyTask.setText(layoutManager
        .getString("taskstree.popupmenu.copyTask.name"));
    copyTask.setIcon(layoutManager.getIcon("copy"));
    keyStroke = KeyStroke.getKeyStroke(layoutManager
        .getString("taskstree.popupmenu.copyTask.mnemonic"));
    copyTask.setMnemonic(keyStroke.getKeyCode());
    copyTask.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
        ActionEvent.CTRL_MASK));
    add(copyTask);

    cutTask = new JMenuItem(null);
    cutTask.setActionCommand((String) TransferHandler.getCutAction().getValue(
        Action.NAME));
    cutTask.addActionListener(transferActionListener);
    cutTask
        .setText(layoutManager.getString("taskstree.popupmenu.cutTask.name"));
    cutTask.setIcon(layoutManager.getIcon("cut"));
    keyStroke = KeyStroke.getKeyStroke(layoutManager
        .getString("taskstree.popupmenu.cutTask.mnemonic"));
    cutTask.setMnemonic(keyStroke.getKeyCode());
    cutTask.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
        ActionEvent.CTRL_MASK));
    add(cutTask);

    pasteTask = new JMenuItem(null);
    pasteTask.setActionCommand((String) TransferHandler.getPasteAction()
        .getValue(Action.NAME));
    pasteTask.addActionListener(transferActionListener);
    pasteTask.setText(layoutManager
        .getString("taskstree.popupmenu.pasteTask.name"));
    pasteTask.setIcon(layoutManager.getIcon("paste"));
    keyStroke = KeyStroke.getKeyStroke(layoutManager
        .getString("taskstree.popupmenu.pasteTask.mnemonic"));
    pasteTask.setMnemonic(keyStroke.getKeyCode());
    pasteTask.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
        ActionEvent.CTRL_MASK));
    add(pasteTask);

    deleteTask = new JMenuItem(new DeleteTaskAction(layoutManager, tree));
    keyStroke = KeyStroke.getKeyStroke(layoutManager
        .getString("taskstree.popupmenu.deleteTask.mnemonic"));
    deleteTask.setMnemonic(keyStroke.getKeyCode());
    deleteTask.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
    add(deleteTask);
    addSeparator();

    reportsMenu = ReportsHelper.getReportMenu(true, tasksTree);
    reportsMenu.setText(layoutManager
        .getString("taskstree.popupmenu.reports.name"));
    add(reportsMenu);
    addSeparator();

    hideTask = new JMenuItem(new HideTaskAction());
    keyStroke = KeyStroke.getKeyStroke(layoutManager
        .getString("taskstree.popupmenu.hideTask.mnemonic"));
    hideTask.setMnemonic(keyStroke.getKeyCode());
    hideTask.setIcon(layoutManager.getIcon("hide"));
    hideTask.setAccelerator(KeyStroke.getKeyStroke(keyStroke.getKeyCode(),
        ActionEvent.CTRL_MASK));
    add(hideTask);

    favouritesAddTask = new JMenuItem(new FavouritesAddTaskAction());
    keyStroke = KeyStroke.getKeyStroke(layoutManager
        .getString("taskstree.popupmenu.favouritesAddTask.mnemonic"));
    if (keyStroke != null) {
      favouritesAddTask.setMnemonic(keyStroke.getKeyCode());
    }
    add(favouritesAddTask);

    favouritesRemoveTask = new JMenuItem(new FavouritesRemoveTaskAction());
    keyStroke = KeyStroke.getKeyStroke(layoutManager
        .getString("taskstree.popupmenu.favouritesRemoveTask.mnemonic"));
    if (keyStroke != null) {
      favouritesRemoveTask.setMnemonic(keyStroke.getKeyCode());
    }
    add(favouritesRemoveTask);

    final Listener listener = new Listener();
    this.tree.addMouseListener(listener);
    this.tree.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_CONTEXT_MENU) {
          // makes surrogate mouse event
          Point menuPoint = tasksTree.getMenuPoint(true);
          listener
              .maybeShowPopup(new MouseEvent(TreePopupMenu.this.tree, 0, System
                  .currentTimeMillis(), 0, menuPoint.x, menuPoint.y, 1, true));
        }
      }
    });
  }

  private class Listener extends MouseAdapter {
    public void mousePressed(MouseEvent e) {
      maybeShowPopup(e);
    }

    public void mouseReleased(MouseEvent e) {
      maybeShowPopup(e);
    }

    public void maybeShowPopup(MouseEvent e) {
      if (e.isPopupTrigger()) {
        TreePath selPath = tree.getClosestPathForLocation(e.getX(), e.getY());
        // Interesting are only actions on a node
        if (selPath == null) {
          selectedTaskNode = null;
        } else {
          tree.setSelectionPath(selPath);
          selectedTaskNode = (TaskTreeNode) selPath.getLastPathComponent();
          Task selectedTask = selectedTaskNode.getTask();
          TimeSlot activeTimeSlot = layoutManager.getTimeSlotTracker()
              .getActiveTimeSlot();
          startTask.setEnabled(selectedTask.canBeStarted());
          pauseTask.setEnabled(selectedTask.canBePaused());
          stopTask.setEnabled(selectedTask.canBeStoped());

          cutTask.setEnabled(!selectedTaskNode.isRoot());

          Clipboard source = Toolkit.getDefaultToolkit().getSystemClipboard();
          DataFlavor[] dataFlavors = source.getAvailableDataFlavors();
          boolean isEnabled = DataFlavors.contains(dataFlavors,
              TaskSelection.DATA_FLAVOR)
              || DataFlavors.contains(dataFlavors,
                  TimeSlotSelection.DATA_FLAVOR);
          pasteTask.setEnabled(isEnabled);

          gotoActiveTask.setEnabled(activeTimeSlot != null);

          restartLastTimeSlot.setEnabled(selectedTask.canBeStarted()
              && !selectedTask.getTimeslots().isEmpty());

          FavouritesInterface favouritesInterface = layoutManager
              .getFavouritesInterface();
          favouritesAddTask.setEnabled(favouritesInterface != null
              && !favouritesInterface.contains(selectedTask));
          favouritesRemoveTask.setEnabled(favouritesInterface != null
              && favouritesInterface.contains(selectedTask));

          moveTaskUp.setEnabled(selectedTaskNode.getPreviousSibling() != null);
          moveTaskDown.setEnabled(selectedTaskNode.getNextSibling() != null);

          addJiraTask.setVisible(layoutManager.getTimeSlotTracker()
              .getConfiguration().getBoolean(Configuration.JIRA_ENABLED, false)
              .booleanValue());

          String hideActionText;
          if (selectedTaskNode.getTask().isHidden()) {
            hideActionText = layoutManager
                .getString("taskstree.popupmenu.unhideTask.name");
          } else {
            hideActionText = layoutManager
                .getString("taskstree.popupmenu.hideTask.name");
          }
          hideTask.setText(hideActionText);
          hideTask.setEnabled(!selectedTask.isRoot());

          TreePopupMenu.this.show(e.getComponent(), e.getX(), e.getY());
        }
      }
    }
  }

  private class ActionEditTask extends AbstractAction {
    private ActionEditTask() {
      super(layoutManager.getString("taskstree.popupmenu.editTask.name"),
          layoutManager.getIcon("edit"));
    }

    public void actionPerformed(ActionEvent e) {
      tasksTree.editSelected();
    }
  }

  private class ActionCloneTask extends AbstractAction {
    private ActionCloneTask() {
      super(layoutManager.getString("taskstree.popupmenu.cloneTask.name"),
          layoutManager.getIcon("edit"));
    }

    public void actionPerformed(ActionEvent e) {
      tasksTree.cloneSelected();
    }
  }

  private class ActionAddIssueTrackerTask extends AbstractAction {
    private ActionAddIssueTrackerTask() {
      super(layoutManager.getString("taskstree.popupmenu.addJiraTask.name"),
          layoutManager.getIcon("new"));
    }

    public void actionPerformed(ActionEvent e) {
      tasksTree.addTaskFromIssueTracker();
    }

  }

  private class ActionGotoIssueTrackerTask extends AbstractAction {
    private ActionGotoIssueTrackerTask() {
      super(layoutManager.getString("taskstree.popupmenu.gotoIssueUrl.name"),
          layoutManager.getIcon("openurl"));
    }

    public void actionPerformed(ActionEvent e) {
      tasksTree.gotoIssueUrl(selectedTaskNode);
    }
  }

  private class StartTaskAction extends AbstractAction {
    private StartTaskAction() {
      super(layoutManager.getString("taskstree.popupmenu.startTask.name"),
          layoutManager.getIcon("play"));
    }

    public void actionPerformed(ActionEvent e) {
      layoutManager.getTimeSlotTracker().startTiming();
      // tree.repaint();
    }
  }

  private class StopTaskAction extends AbstractAction {
    private StopTaskAction() {
      super(layoutManager.getString("taskstree.popupmenu.stopTask.name"),
          layoutManager.getIcon("stop"));
    }

    public void actionPerformed(ActionEvent e) {
      layoutManager.getTimeSlotTracker().stopTiming();
    }
  }

  private class GotoActiveAction extends AbstractAction {
    private GotoActiveAction() {
      super(layoutManager.getString("taskstree.popupmenu.gotoActiveTask.name"),
          layoutManager.getIcon("title.icon"));
    }

    public void actionPerformed(ActionEvent e) {
      TimeSlot activeTimeSlot = layoutManager.getTimeSlotTracker()
          .getActiveTimeSlot();
      if (activeTimeSlot != null) {
        tasksTree.selectTask(activeTimeSlot.getTask());
      }
    }
  }

  private class PauseTaskAction extends AbstractAction {
    private PauseTaskAction() {
      super(layoutManager.getString("taskstree.popupmenu.pauseTask.name"),
          layoutManager.getIcon("pause"));
    }

    public void actionPerformed(ActionEvent e) {
      layoutManager.getTimeSlotTracker().pauseTiming();
    }
  }

  private class RestartTimingAction extends AbstractAction {
    private RestartTimingAction() {
      super(layoutManager.getString("taskstree.popupmenu.restartTiming.name"),
          layoutManager.getIcon("replay"));
    }

    public void actionPerformed(ActionEvent e) {
      Task taskToBeRestarted = selectedTaskNode.getTask();
      LOG.fine("taskToBeRestarted = " + taskToBeRestarted);
      TimeSlot lastTimeSlot = taskToBeRestarted.getLastTimeSlot();
      LOG.fine("lastTimeSlot = " + lastTimeSlot);
      if (lastTimeSlot == null) {
        return;
      }
      String description = lastTimeSlot.getDescription();
      layoutManager.getTimeSlotTracker().restartTiming(description);
    }

  }

  private class FavouritesAddTaskAction extends AbstractAction {
    private FavouritesAddTaskAction() {
      super(layoutManager
          .getString("taskstree.popupmenu.favouritesAddTask.name"),
          layoutManager.getIcon("addfavourites"));
    }

    public void actionPerformed(ActionEvent e) {
      Task selectedTask = selectedTaskNode.getTask();
      FavouritesInterface favourites = layoutManager.getFavouritesInterface();
      if (favourites == null) {
        return;
      }
      favourites.add(selectedTask);
    }
  }

  private class FavouritesRemoveTaskAction extends AbstractAction {
    private FavouritesRemoveTaskAction() {
      super(layoutManager
          .getString("taskstree.popupmenu.favouritesRemoveTask.name"),
          layoutManager.getIcon("removefavourites"));
    }

    public void actionPerformed(ActionEvent e) {
      Task selectedTask = selectedTaskNode.getTask();
      FavouritesInterface favourites = layoutManager.getFavouritesInterface();
      if (favourites == null) {
        return;
      }
      favourites.remove(selectedTask);
    }
  }

  private class MoveTaskUpAction extends AbstractAction {
    private MoveTaskUpAction() {
      super(layoutManager.getString("taskstree.popupmenu.moveTaskUp.name"),
          layoutManager.getIcon("arrow-up"));
    }

    public void actionPerformed(ActionEvent e) {
      tasksTree.moveSelectedTaskUp(selectedTaskNode);
    }
  }

  private class MoveTaskDownAction extends AbstractAction {
    private MoveTaskDownAction() {
      super(layoutManager.getString("taskstree.popupmenu.moveTaskDown.name"),
          layoutManager.getIcon("arrow-down"));
    }

    public void actionPerformed(ActionEvent e) {
      tasksTree.moveSelectedTaskDown(selectedTaskNode);
    }
  }

  private class HideTaskAction extends AbstractAction {
    private HideTaskAction() {
      super(layoutManager.getString("taskstree.popupmenu.hideTask.name"), null);
    }

    public void actionPerformed(ActionEvent e) {
      tasksTree.hideSelectedTask();
    }
  }

}
