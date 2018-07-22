package net.sf.timeslottracker.gui.layouts.classic.timeslots;

import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;

import net.sf.timeslottracker.data.DataSource;
import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.TasksInterface;
import net.sf.timeslottracker.gui.TimeSlotEditDialog;
import net.sf.timeslottracker.gui.TimeSlotsInterface;
import net.sf.timeslottracker.gui.TimeSlotsInterface.Mode;
import net.sf.timeslottracker.gui.dnd.DataFlavors;
import net.sf.timeslottracker.gui.dnd.utils.TransferActionListener;
import net.sf.timeslottracker.gui.layouts.classic.JMenuItem;

/**
 * Popup menu to timeslots table. Contains several actions a user can do with
 * some timeslot.
 * 
 * @version File version: $Revision: 1113 $, $Date: 2009-06-21 19:32:18 +0700
 *          (Sun, 21 Jun 2009) $
 * @author Last change: $Author: ghermans $
 */
@SuppressWarnings("serial")
public class TimeslotsTablePopupMenu extends JPopupMenu {

  private LayoutManager layoutManager;

  private JTable timeslotsTable;

  private TimeSlotsInterface timeslots;

  private Listener listener;

  private JMenuItem startTiming;

  private JMenuItem pauseTiming;

  private JMenuItem stopTiming;

  private JMenuItem restartTiming;

  private JMenuItem gotoActiveTask;

  private JMenuItem editTimeslot;

  private JMenuItem copyTimeslot;

  private JMenuItem cloneTimeslot;

  private JMenuItem cutTimeslot;

  private JMenuItem pasteTimeslot;

  private JMenuItem deleteTimeslot;

  private JMenuItem splitTimeslot;

  TimeslotsTablePopupMenu(final LayoutManager layoutManager,
      final JTable timeslotsTable, final TimeSlotsInterface timeslots) {
    super(layoutManager.getString("timeslots.popupmenu.title"));
    this.layoutManager = layoutManager;
    this.timeslotsTable = timeslotsTable;
    this.timeslots = timeslots;
    startTiming = new JMenuItem(new StartTimingAction());
    KeyStroke keyStroke = KeyStroke.getKeyStroke(layoutManager
        .getString("timeslots.popupmenu.startTiming.mnemonic"));
    startTiming.setMnemonic(keyStroke.getKeyCode());
    startTiming.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0));

    add(startTiming);
    pauseTiming = new JMenuItem(new PauseTimingAction());
    keyStroke = KeyStroke.getKeyStroke(layoutManager
        .getString("timeslots.popupmenu.pauseTiming.mnemonic"));
    pauseTiming.setMnemonic(keyStroke.getKeyCode());
    add(pauseTiming);
    stopTiming = new JMenuItem(new StopTimingAction());
    keyStroke = KeyStroke.getKeyStroke(layoutManager
        .getString("timeslots.popupmenu.stopTiming.mnemonic"));
    stopTiming.setMnemonic(keyStroke.getKeyCode());
    stopTiming.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0));
    add(stopTiming);
    restartTiming = new JMenuItem(new RestartTimingAction());
    keyStroke = KeyStroke.getKeyStroke(layoutManager
        .getString("timeslots.popupmenu.restartTiming.mnemonic"));
    restartTiming.setMnemonic(keyStroke.getKeyCode());
    add(restartTiming);
    gotoActiveTask = new JMenuItem(new GotoActiveAction());
    keyStroke = KeyStroke.getKeyStroke(layoutManager
        .getString("timeslots.popupmenu.gotoActiveTask.mnemonic"));
    gotoActiveTask.setMnemonic(keyStroke.getKeyCode());
    add(gotoActiveTask);

    addSeparator();
    editTimeslot = new JMenuItem(new EditTimeslotAction());
    keyStroke = KeyStroke.getKeyStroke(layoutManager
        .getString("timeslots.popupmenu.editTimeslot.mnemonic"));
    editTimeslot.setMnemonic(keyStroke.getKeyCode());
    editTimeslot.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
    add(editTimeslot);
    JMenuItem addTimeslot = new JMenuItem(new AddTimeslotAction());
    keyStroke = KeyStroke.getKeyStroke(layoutManager
        .getString("timeslots.popupmenu.addNewTimeslot.mnemonic"));
    addTimeslot.setMnemonic(keyStroke.getKeyCode());
    addTimeslot.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0));
    add(addTimeslot);

    addSeparator();

    TransferActionListener transferActionListener = new TransferActionListener();

    copyTimeslot = new JMenuItem(null);
    copyTimeslot.setActionCommand((String) TransferHandler.getCopyAction()
        .getValue(Action.NAME));
    copyTimeslot.addActionListener(transferActionListener);
    copyTimeslot.setText(layoutManager
        .getString("timeslots.popupmenu.copyTimeslot.name"));
    copyTimeslot.setIcon(layoutManager.getIcon("copy"));
    keyStroke = KeyStroke.getKeyStroke(layoutManager
        .getString("timeslots.popupmenu.copyTimeslot.mnemonic"));
    copyTimeslot.setMnemonic(keyStroke.getKeyCode());
    copyTimeslot.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
        ActionEvent.CTRL_MASK));
    add(copyTimeslot);

    cloneTimeslot = new JMenuItem(new CloneTimeslotAction());
    keyStroke = KeyStroke.getKeyStroke(layoutManager
        .getString("timeslots.popupmenu.cloneTimeslot.mnemonic"));
    cloneTimeslot.setMnemonic(keyStroke.getKeyCode());
    add(cloneTimeslot);

    cutTimeslot = new JMenuItem(null);
    cutTimeslot.setActionCommand((String) TransferHandler.getCutAction()
        .getValue(Action.NAME));
    cutTimeslot.addActionListener(transferActionListener);
    cutTimeslot.setText(layoutManager
        .getString("timeslots.popupmenu.cutTimeslot.name"));
    cutTimeslot.setIcon(layoutManager.getIcon("cut"));
    keyStroke = KeyStroke.getKeyStroke(layoutManager
        .getString("timeslots.popupmenu.cutTimeslot.mnemonic"));
    cutTimeslot.setMnemonic(keyStroke.getKeyCode());
    cutTimeslot.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
        ActionEvent.CTRL_MASK));
    add(cutTimeslot);

    splitTimeslot = new JMenuItem(new SplitTimeslotAction());
    splitTimeslot.setText(layoutManager
        .getString("timeslots.popupmenu.splitTimeslot.name"));
    keyStroke = KeyStroke.getKeyStroke(layoutManager
        .getString("timeslots.popupmenu.splitTimeslot.mnemonic"));
    splitTimeslot.setMnemonic(keyStroke.getKeyCode());
    splitTimeslot.setAccelerator(KeyStroke.getKeyStroke(keyStroke.getKeyCode(),
        ActionEvent.CTRL_MASK));
    add(splitTimeslot);

    pasteTimeslot = new JMenuItem(null);
    pasteTimeslot.setActionCommand((String) TransferHandler.getPasteAction()
        .getValue(Action.NAME));
    pasteTimeslot.addActionListener(transferActionListener);
    pasteTimeslot.setText(layoutManager
        .getString("timeslots.popupmenu.pasteTimeslot.name"));
    pasteTimeslot.setIcon(layoutManager.getIcon("paste"));
    pasteTimeslot.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
        ActionEvent.CTRL_MASK));
    keyStroke = KeyStroke.getKeyStroke(layoutManager
        .getString("timeslots.popupmenu.pasteTimeslot.mnemonic"));
    cutTimeslot.setMnemonic(keyStroke.getKeyCode());
    add(pasteTimeslot);

    deleteTimeslot = new JMenuItem(new DeleteTimeslotAction());
    keyStroke = KeyStroke.getKeyStroke(layoutManager
        .getString("timeslots.popupmenu.deleteTimeslot.mnemonic"));
    deleteTimeslot.setMnemonic(keyStroke.getKeyCode());
    deleteTimeslot
        .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
    add(deleteTimeslot);

    listener = new Listener();

    timeslotsTable.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent e) {
        if (timeslots.getMode() != Mode.Task) {
          return;
        }

        TimeSlot selectedTimeslot = getSelectedTimeslot();

        int keyCode = e.getKeyCode();

        switch (keyCode) {
        case KeyEvent.VK_INSERT:
          new AddTimeslotAction().actionPerformed(emptyEvent());
          break;

        case KeyEvent.VK_F2:
          new EditTimeslotAction().actionPerformed(emptyEvent());
          break;

        case KeyEvent.VK_DELETE:
          new DeleteTimeslotAction().actionPerformed(emptyEvent());
          break;

        case KeyEvent.VK_L:
          if (e.getModifiers() == InputEvent.CTRL_MASK) {
            new SplitTimeslotAction().actionPerformed(emptyEvent());
          }
          break;

        case KeyEvent.VK_SPACE:
          if (selectedTimeslot != null && selectedTimeslot.canBeStoped()) {
            new StopTimingAction().actionPerformed(emptyEvent());
          } else {
            new StartTimingAction().actionPerformed(emptyEvent());
          }
          break;

        case KeyEvent.VK_CONTEXT_MENU:
          int col = timeslotsTable.getSelectedColumn();
          int row = timeslotsTable.getSelectedRow();
          Rectangle cellRect = timeslotsTable.getCellRect(row, col, false);
          getMouseListener().mousePressed(
              new MouseEvent(TimeslotsTablePopupMenu.this.timeslotsTable, 0,
                  System.currentTimeMillis(), 0, (int) cellRect.getCenterX(),
                  (int) cellRect.getCenterY(), 1, true));
          break;
        }
      }

      private ActionEvent emptyEvent() {
        return new ActionEvent(this, 0, null);
      }
    });
  }

  MouseAdapter getMouseListener() {
    return listener;
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
        TimeSlotsInterface timeslotsModule = timeslots;
        if (timeslotsModule.getMode() != TimeSlotsInterface.Mode.Task) {
          return;
        }

        Task actualTask = timeslotsModule.getSelectedTask();
        if (actualTask == null) {
          return;
        }
        final boolean isMultipleRowSelection = timeslotsTable
            .getSelectedRowCount() > 1;

        int selectedRow = timeslotsTable.getSelectedRow();
        if (!isMultipleRowSelection) {
          selectedRow = timeslotsTable.rowAtPoint(e.getPoint());
        }

        TimeSlot activeTimeSlot = layoutManager.getTimeSlotTracker()
            .getActiveTimeSlot();
        TimeSlot selectedTimeSlot = null;
        if (selectedRow >= 0) {
          selectedTimeSlot = getTimeSlotByTreeRowId(selectedRow);
        }

        gotoActiveTask.setEnabled(activeTimeSlot != null);

        if (selectedRow >= 0) {
          startTiming.setEnabled(selectedTimeSlot.canBeStarted()
              && !isMultipleRowSelection);
          pauseTiming.setEnabled(selectedTimeSlot.canBePaused()
              && !isMultipleRowSelection);
          stopTiming.setEnabled(selectedTimeSlot.canBeStoped()
              && !isMultipleRowSelection);
          restartTiming.setEnabled(selectedTimeSlot.canBeStarted()
              && !isMultipleRowSelection);
          editTimeslot.setEnabled(!isMultipleRowSelection);
          copyTimeslot.setEnabled(true);
          cutTimeslot.setEnabled(true);
          cloneTimeslot.setEnabled(!isMultipleRowSelection);
          deleteTimeslot.setEnabled(!isMultipleRowSelection
              && !(activeTimeSlot != null && activeTimeSlot
                  .equals(selectedTimeSlot)));
          if (!isMultipleRowSelection) {
            timeslotsTable.setRowSelectionInterval(selectedRow, selectedRow);
          }
        } else {
          // only add item should be shown
          startTiming.setEnabled(actualTask.canBeStarted());
          pauseTiming.setEnabled(false);
          stopTiming.setEnabled(false);
          restartTiming.setEnabled(false);
          editTimeslot.setEnabled(false);
          cutTimeslot.setEnabled(false);
          copyTimeslot.setEnabled(false);
          cloneTimeslot.setEnabled(false);
          deleteTimeslot.setEnabled(false);
        }

        Clipboard source = Toolkit.getDefaultToolkit().getSystemClipboard();
        pasteTimeslot.setEnabled(source
            .isDataFlavorAvailable(DataFlavors.TIME_SLOT));

        // finally show the popup menu
        TimeslotsTablePopupMenu.this.show(e.getComponent(), e.getX(), e.getY());
      }
    }
  }

  private class StartTimingAction extends AbstractAction {
    private StartTimingAction() {
      super(layoutManager.getString("timeslots.popupmenu.startTiming.name"),
          layoutManager.getIcon("play"));
    }

    public void actionPerformed(ActionEvent e) {
      layoutManager.getTimeSlotTracker().startTiming();
    }
  }

  private class PauseTimingAction extends AbstractAction {
    private PauseTimingAction() {
      super(layoutManager.getString("timeslots.popupmenu.pauseTiming.name"),
          layoutManager.getIcon("pause"));
    }

    public void actionPerformed(ActionEvent e) {
      layoutManager.getTimeSlotTracker().pauseTiming();
    }
  }

  private class StopTimingAction extends AbstractAction {
    private StopTimingAction() {
      super(layoutManager.getString("timeslots.popupmenu.stopTiming.name"),
          layoutManager.getIcon("stop"));
    }

    public void actionPerformed(ActionEvent e) {
      layoutManager.getTimeSlotTracker().stopTiming();
    }
  }

  private class RestartTimingAction extends AbstractAction {
    private RestartTimingAction() {
      super(layoutManager.getString("timeslots.popupmenu.restartTiming.name"),
          layoutManager.getIcon("replay"));
    }

    public void actionPerformed(ActionEvent e) {
      int selectedRow = timeslotsTable.getSelectedRow();
      TimeSlot selectedTimeSlot = getTimeSlotByTreeRowId(selectedRow);
      if (selectedTimeSlot == null || timeslotsTable.getSelectedRowCount() != 1) {
        return;
      }
      String description = selectedTimeSlot.getDescription();
      layoutManager.getTimeSlotTracker().restartTiming(description);
    }
  }

  private class GotoActiveAction extends AbstractAction {
    private GotoActiveAction() {
      super(layoutManager.getString("timeslots.popupmenu.gotoActiveTask.name"),
          layoutManager.getIcon("title.icon"));
    }

    public void actionPerformed(ActionEvent e) {
      TimeSlot activeTimeSlot = layoutManager.getTimeSlotTracker()
          .getActiveTimeSlot();
      TasksInterface tasksTree = layoutManager.getTasksInterface();
      if (activeTimeSlot != null) {
        tasksTree.selectTask(activeTimeSlot.getTask());
      }
    }
  }

  private class EditTimeslotAction extends AbstractAction {
    private EditTimeslotAction() {
      super(layoutManager.getString("timeslots.popupmenu.editTimeslot.name"),
          layoutManager.getIcon("edit"));
    }

    public void actionPerformed(ActionEvent e) {
      timeslots.editSelected();
    }
  }

  private class AddTimeslotAction extends AbstractAction {
    private AddTimeslotAction() {
      super(layoutManager.getString("timeslots.popupmenu.addNewTimeslot.name"),
          layoutManager.getIcon("add"));
    }

    public void actionPerformed(ActionEvent e) {
      TimeSlotEditDialog dialog = new TimeSlotEditDialog(layoutManager, null,
          false);
      TimeSlot timeslot = dialog.getTimeslot();
      if (timeslot != null) {
        timeslots.add(timeslot);
        timeslots.refresh();
        timeslots.selectTimeSlot(timeslot);
      }
    }
  }

  private class CloneTimeslotAction extends AbstractAction {
    private CloneTimeslotAction() {
      super(layoutManager.getString("timeslots.popupmenu.cloneTimeslot.name"),
          layoutManager.getIcon("copy"));
    }

    public void actionPerformed(ActionEvent e) {
      if (timeslotsTable.getSelectedRowCount() > 1) {
        return; // no posibility to clone more then one task at time
      }
      TimeSlot selectedTimeSlot = getSelectedTimeslot();
      String description = selectedTimeSlot.getDescription();
      DataSource dataSource = layoutManager.getTimeSlotTracker()
          .getDataSource();
      if (dataSource == null) {
        return;
      }
      TimeSlot newTimeslot = dataSource.createTimeSlot(null, null, null,
          description);
      TimeSlotEditDialog dialog = new TimeSlotEditDialog(layoutManager,
          newTimeslot, false);
      TimeSlot timeslot = dialog.getTimeslot();
      if (timeslot != null) {
        timeslots.add(timeslot);
        timeslots.refresh();
      }
    }
  }

  private class DeleteTimeslotAction extends AbstractAction {
    private DeleteTimeslotAction() {
      super(layoutManager.getString("timeslots.popupmenu.deleteTimeslot.name"),
          layoutManager.getIcon("delete"));
    }

    public void actionPerformed(ActionEvent e) {
      int selectedRow = timeslotsTable.getSelectedRow();
      if (selectedRow < 0 || timeslotsTable.getSelectedRowCount() != 1) {
        return;
      }
      TimeSlot selectedTimeSlot = getTimeSlotByTreeRowId(selectedRow);
      if (selectedTimeSlot == null) {
        return;
      }
      
      // reset active timeslot before deleting active timeslot
      if (selectedTimeSlot.equals(layoutManager.getTimeSlotTracker()
          .getActiveTimeSlot())) {
        layoutManager.getTimeSlotTracker().setActiveTimeSlot(null);
      }
      
      Task selectedTask = selectedTimeSlot.getTask();
      selectedTask.deleteTimeslot(selectedTimeSlot);
      layoutManager.fireTimeSlotChanged(selectedTimeSlot);
      // because we have removed the link to task in deleteTimeslot method we
      // have to inform task listeners as well
      layoutManager.getTimeSlotTracker().fireTaskChanged(selectedTask);
    }
  }

  private TimeSlot getTimeSlotByTreeRowId(int selectedRow) {
    return ((TimeslotsTableModel) timeslotsTable.getModel())
        .getValueAt(timeslotsTable.convertRowIndexToModel(selectedRow));
  }

  private TimeSlot getSelectedTimeslot() {
    int selectedRow = timeslotsTable.getSelectedRow();
    if (selectedRow < 0) {
      return null;
    }
    return getTimeSlotByTreeRowId(selectedRow);
  }

  /**
   * Handles split timeslot action into two adjacent timeslots with identical
   * task description.
   */
  private class SplitTimeslotAction extends AbstractAction {
    private SplitTimeslotAction() {
      super(layoutManager.getString("timeslots.popupmenu.splitTimeslot.name"),
          layoutManager.getIcon("split"));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      timeslots.splitSelected();
    }
  }

}
