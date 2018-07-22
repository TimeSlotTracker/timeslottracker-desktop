package net.sf.timeslottracker.gui.layouts.classic.favourites;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.layouts.classic.JMenuItem;

/**
 * Popup menu to favourites.
 * 
 * File version: $Revision: 1000 $, $Date: 2009-06-21 19:32:18 +0700 (Sun, 21 Jun
 * 2009) $ Last change: $Author: cnitsa $
 */
@SuppressWarnings("serial")
public class FavouritesPopupMenu extends JPopupMenu {

  /** logging using java.util.logging package **/
  private static Logger logger = Logger
      .getLogger("net.sf.timeslottracker.gui.layouts.classic.favourites");

  private LayoutManager layoutManager;
  private JList favouritesList;
  private Favourites favourites;

  private JMenuItem startTiming;
  private JMenuItem pauseTiming;
  private JMenuItem stopTiming;
  private JMenuItem restartLastTimeSlot;
  private JMenuItem moveUp;
  private JMenuItem moveDown;
  private JMenuItem removeFromFavourites;
  private Listener listener;

  FavouritesPopupMenu(final LayoutManager layoutManager,
      final JList favouritesList, final Favourites favourites) {
    super(layoutManager.getString("favourites.popupmenu.title"));
    this.layoutManager = layoutManager;
    this.favouritesList = favouritesList;
    this.favourites = favourites;

    startTiming = new JMenuItem(new StartTimingAction());
    KeyStroke keyStroke = KeyStroke.getKeyStroke(layoutManager
        .getString("favourites.popupmenu.startTiming.mnemonic"));
    startTiming.setMnemonic(keyStroke.getKeyCode());
    startTiming.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0));
    add(startTiming);

    pauseTiming = new JMenuItem(new PauseTimingAction());
    keyStroke = KeyStroke.getKeyStroke(layoutManager
        .getString("favourites.popupmenu.pauseTiming.mnemonic"));
    pauseTiming.setMnemonic(keyStroke.getKeyCode());
    add(pauseTiming);

    stopTiming = new JMenuItem(new StopTimingAction());
    keyStroke = KeyStroke.getKeyStroke(layoutManager
        .getString("favourites.popupmenu.stopTiming.mnemonic"));
    stopTiming.setMnemonic(keyStroke.getKeyCode());
    stopTiming.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0));
    add(stopTiming);

    restartLastTimeSlot = new JMenuItem(new RestartTimingAction());
    keyStroke = KeyStroke.getKeyStroke(layoutManager
        .getString("favourites.popupmenu.restartTiming.mnemonic"));
    restartLastTimeSlot.setMnemonic(keyStroke.getKeyCode());
    add(restartLastTimeSlot);

    addSeparator();

    moveUp = new JMenuItem(new MoveUpAction());
    keyStroke = KeyStroke.getKeyStroke(layoutManager
        .getString("favourites.popupmenu.moveUp.mnemonic"));
    moveUp.setMnemonic(keyStroke.getKeyCode());
    add(moveUp);

    moveDown = new JMenuItem(new MoveDownAction());
    keyStroke = KeyStroke.getKeyStroke(layoutManager
        .getString("favourites.popupmenu.moveDown.mnemonic"));
    moveDown.setMnemonic(keyStroke.getKeyCode());
    add(moveDown);

    removeFromFavourites = new JMenuItem(new RemoveFromFavouritesAction());
    keyStroke = KeyStroke.getKeyStroke(layoutManager
        .getString("favourites.popupmenu.removeFromFavourites.mnemonic"));
    removeFromFavourites.setMnemonic(keyStroke.getKeyCode());
    add(removeFromFavourites);

    listener = new Listener();
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
        int selectedIndex = favouritesList.locationToIndex(e.getPoint());
        if (selectedIndex >= 0) {
          DefaultListModel favouritesListModel = (DefaultListModel) favouritesList
              .getModel();
          Task selectedTask = (Task) favouritesListModel.get(selectedIndex);

          startTiming.setEnabled(selectedTask.canBeStarted());
          pauseTiming.setEnabled(selectedTask.canBePaused());
          stopTiming.setEnabled(selectedTask.canBeStoped());
          Collection timeslots = selectedTask.getTimeslots();
          restartLastTimeSlot.setEnabled(selectedTask.canBeStarted()
              && !timeslots.isEmpty());

          moveUp.setEnabled(selectedIndex > 0);
          moveDown.setEnabled(selectedIndex < favouritesListModel.size() - 1);

          favouritesList.setSelectedIndex(selectedIndex);
          FavouritesPopupMenu.this.show(e.getComponent(), e.getX(), e.getY());
        }
      }
    }
  }

  private class StartTimingAction extends AbstractAction {
    private StartTimingAction() {
      super(layoutManager.getString("favourites.popupmenu.startTiming.name"),
          layoutManager.getIcon("play"));
    }

    public void actionPerformed(ActionEvent e) {
      layoutManager.getTimeSlotTracker().startTiming();
    }
  }

  private class PauseTimingAction extends AbstractAction {
    private PauseTimingAction() {
      super(layoutManager.getString("favourites.popupmenu.pauseTiming.name"),
          layoutManager.getIcon("pause"));
    }

    public void actionPerformed(ActionEvent e) {
      layoutManager.getTimeSlotTracker().pauseTiming();
    }
  }

  private class StopTimingAction extends AbstractAction {
    private StopTimingAction() {
      super(layoutManager.getString("favourites.popupmenu.stopTiming.name"),
          layoutManager.getIcon("stop"));
    }

    public void actionPerformed(ActionEvent e) {
      layoutManager.getTimeSlotTracker().stopTiming();
    }
  }

  private class RestartTimingAction extends AbstractAction {
    private RestartTimingAction() {
      super(layoutManager.getString("favourites.popupmenu.restartTiming.name"),
          layoutManager.getIcon("replay"));
    }

    public void actionPerformed(ActionEvent e) {
      int selectedIndex = favouritesList.getSelectedIndex();
      if (selectedIndex < 0) {
        return;
      }
      DefaultListModel favouritesListModel = (DefaultListModel) favouritesList
          .getModel();
      Task taskToBeRestarted = (Task) favouritesListModel.get(selectedIndex);
      logger.fine("taskToBeRestarted = " + taskToBeRestarted);
      Object[] timeslots = taskToBeRestarted.getTimeslots().toArray();

      TimeSlot lastTimeSlot = null;
      if (timeslots.length > 0) {
        lastTimeSlot = (TimeSlot) timeslots[timeslots.length - 1];
      }
      logger.fine("lastTimeSlot = " + lastTimeSlot);
      if (lastTimeSlot == null) {
        return;
      }
      String description = lastTimeSlot.getDescription();
      layoutManager.getTimeSlotTracker().startTiming(description);
    }
  }

  private class MoveUpAction extends AbstractAction {
    private MoveUpAction() {
      super(layoutManager.getString("favourites.popupmenu.moveUp.name"),
          layoutManager.getIcon("arrow-up"));
    }

    public void actionPerformed(ActionEvent e) {
      int selectedIndex = favouritesList.getSelectedIndex();
      if (selectedIndex <= 0) {
        return;
      }
      DefaultListModel favouritesListModel = (DefaultListModel) favouritesList
          .getModel();
      Task taskToBeMoved = (Task) favouritesListModel.remove(selectedIndex);
      favouritesListModel.add(selectedIndex - 1, taskToBeMoved);
      favouritesList.setSelectedIndex(selectedIndex - 1);
    }
  }

  private class MoveDownAction extends AbstractAction {
    private MoveDownAction() {
      super(layoutManager.getString("favourites.popupmenu.moveDown.name"),
          layoutManager.getIcon("arrow-down"));
    }

    public void actionPerformed(ActionEvent e) {
      int selectedIndex = favouritesList.getSelectedIndex();
      DefaultListModel favouritesListModel = (DefaultListModel) favouritesList
          .getModel();
      if (selectedIndex < 0 || selectedIndex == favouritesListModel.size() - 1) {
        return;
      }
      Task taskToBeMoved = (Task) favouritesListModel.remove(selectedIndex);
      favouritesListModel.add(selectedIndex + 1, taskToBeMoved);
      favouritesList.setSelectedIndex(selectedIndex + 1);
    }
  }

  private class RemoveFromFavouritesAction extends AbstractAction {
    private RemoveFromFavouritesAction() {
      super(layoutManager
          .getString("favourites.popupmenu.removeFromFavourites.name"),
          layoutManager.getIcon("removefavourites"));
    }

    public void actionPerformed(ActionEvent e) {
      int selectedIndex = favouritesList.getSelectedIndex();
      DefaultListModel favouritesListModel = (DefaultListModel) favouritesList
          .getModel();
      Task taskToBeDeleted = (Task) favouritesListModel.get(selectedIndex);
      favourites.remove(taskToBeDeleted);
      layoutManager.getTimeSlotTracker().fireTaskChanged(taskToBeDeleted);
      int favouritesSize = favouritesListModel.size();
      if (favouritesSize == 0) {
        return;
      }
      selectedIndex = (selectedIndex >= favouritesSize ? favouritesSize - 1
          : selectedIndex);
      favouritesList.setSelectedIndex(selectedIndex);
    }
  }

}
