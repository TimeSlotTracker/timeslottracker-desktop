package net.sf.timeslottracker.gui.layouts.classic.favourites;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.timeslottracker.core.Action;
import net.sf.timeslottracker.data.DataSource;
import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TaskChangedListener;
import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.gui.DialogPanel;
import net.sf.timeslottracker.gui.FavouritesInterface;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.TasksInterface;
import net.sf.timeslottracker.gui.dnd.DataFlavors;
import net.sf.timeslottracker.gui.dnd.handlers.TimeSlotHandler;
import net.sf.timeslottracker.gui.listeners.TaskSelectionChangeListener;

/**
 * A module for timeslottracker to present favourites tasks in a JList.
 * 
 * File version: $Revision: 1037 $, $Date: 2009-05-16 08:53:21 +0700 (Sat, 16 May
 * 2009) $ Last change: $Author: cnitsa $
 */
public class Favourites extends JPanel implements FavouritesInterface,
    TaskSelectionChangeListener {

  private class FavouritesKeyAdapter extends KeyAdapter {
    private final LayoutManager layoutManager;

    public FavouritesKeyAdapter(LayoutManager layoutManager) {
      this.layoutManager = layoutManager;
    }

    @Override
    public void keyReleased(KeyEvent e) {
      TimeSlot activeTimeSlot = layoutManager.getTimeSlotTracker()
          .getActiveTimeSlot();
      boolean isActive = activeTimeSlot != null;

      int hideKeyCode = KeyStroke.getKeyStroke(
          layoutManager.getString("taskstree.popupmenu.hideTask.mnemonic"))
          .getKeyCode();

      switch (e.getKeyCode()) {
      case KeyEvent.VK_SPACE:
        if (isActive
            && activeTimeSlot.getTask().equals(layoutManager.getTasksInterface().getSelected())
            && e.getModifiers() != InputEvent.SHIFT_MASK) {
          layoutManager.getTimeSlotTracker().stopTiming();
        } else {
          layoutManager.getTimeSlotTracker().startTiming();
        }
        break;

      }
    }

  }

  private LayoutManager layoutManager;

  /** dialog panel where we will keep all our list **/
  private DialogPanel dialogPanel;

  /** place to store favourites entries **/
  private JList favouritesList;

  private DefaultListModel listModel;

  public Favourites(final LayoutManager layoutManager) {
    super(new BorderLayout());
    this.layoutManager = layoutManager;

    listModel = new DefaultListModel();
    favouritesList = new JList(listModel);
    favouritesList.setFont(getFont().deriveFont(Font.PLAIN));
    favouritesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    favouritesList.addListSelectionListener(new FavouritesSelectionListener());
    favouritesList
        .setCellRenderer(new FavouritesListCellRenderer(layoutManager));

    // constructs dialog panel with our task's info variables
    dialogPanel = new DialogPanel(GridBagConstraints.BOTH, 0.0);
    dialogPanel.addTitle(layoutManager.getString("favourites.title"));
    dialogPanel.fillToEnd(new JScrollPane(favouritesList));
    add(dialogPanel, BorderLayout.CENTER);

    FavouritesPopupMenu popupMenu = new FavouritesPopupMenu(layoutManager,
        favouritesList, this);
    favouritesList.addMouseListener(popupMenu.getMouseListener());

    layoutManager.getTimeSlotTracker().addActionListener(
        new TaskChangedAction());

    favouritesList.setDragEnabled(true);
    favouritesList.setTransferHandler(new TransferHandler() {
      TimeSlotHandler timeSlotHandler = new TimeSlotHandler(layoutManager);

      public int getSourceActions(JComponent c) {
        return TransferHandler.NONE;
      }

      protected Transferable createTransferable(JComponent c) {
        // no DnD from favorites for now
        return null;
      }

      public boolean importData(JComponent comp, Transferable t) {
        if (t.isDataFlavorSupported(DataFlavors.TIME_SLOT)) {
          Task targetTask = (Task) listModel.get(favouritesList
              .getSelectedIndex());

          return timeSlotHandler.importData(t, targetTask);
        } else if (t.isDataFlavorSupported(DataFlavors.TASK)) {
          Object taskId = DataFlavors.getTransferData(t, DataFlavors.TASK);
          Task sourceTask = layoutManager.getTimeSlotTracker().getDataSource()
              .getTask(taskId);

          if (!listModel.contains(sourceTask)) {
            add(sourceTask);
          }

          return true;
        } else {
          return false;
        }
      }

      public boolean canImport(JComponent comp, DataFlavor[] flavors) {
        return DataFlavors.contains(DataFlavors.TASK, flavors)
            || timeSlotHandler.canImport(flavors);
      }
    });

    // adds keyboard shortcuts
    favouritesList.addKeyListener(new FavouritesKeyAdapter(layoutManager));
  }

  public void setFavourites(Collection<Task> favourites) {
    removeAll();
    if (favourites == null) {
      return;
    }
    Iterator<Task> i = favourites.iterator();
    while (i.hasNext()) {
      listModel.addElement(i.next());
    }
  }

  public void add(Task task) {
    listModel.addElement(task);
    layoutManager.getTimeSlotTracker().fireTaskChanged(task);
  }

  public void remove(Task task) {
    listModel.removeElement(task);
    layoutManager.getTimeSlotTracker().fireTaskChanged(task);
  }

  public void removeTree(Task task) {
    remove(task);
    Collection childrenCollection = task.getChildren();
    if (childrenCollection == null) {
      return;
    }
    Iterator children = childrenCollection.iterator();
    while (children.hasNext()) {
      Task child = (Task) children.next();
      removeTree(child);
    }
  }

  public void removeAll() {
    Iterator i = getFavourites().iterator();
    listModel.removeAllElements();
    while (i.hasNext()) {
      Task task = (Task) i.next();
      layoutManager.getTimeSlotTracker().fireTaskChanged(task);
    }
  }

  public Collection<Task> getFavourites() {
    Collection<Task> favourites = new Vector<Task>();
    Enumeration<Task> list = (Enumeration<Task>) listModel.elements();
    while (list.hasMoreElements()) {
      favourites.add(list.nextElement());
    }
    return favourites;
  }

  public boolean contains(Task task) {
    return listModel.contains(task);
  }

  public void reload() {
    removeAll();
    DataSource dataSource = layoutManager.getTimeSlotTracker().getDataSource();
    setFavourites(dataSource.getFavourites());
  }

  /**
   * Locates task in a favourites list and returns it's index.
   * 
   * @return -1 if task is no found, otherwise task's index.
   */
  private int locateTask(Task task) {
    int taskIndex = 0;
    boolean favouritesHasTask = false;

    while (taskIndex < listModel.size() && !favouritesHasTask) {
      favouritesHasTask = task.equals(listModel.get(taskIndex));
      if (!favouritesHasTask) {
        taskIndex++;
      }
    }

    if (!favouritesHasTask) {
      taskIndex = -1;
    }

    return taskIndex;
  }

  // realization of TaskSelectionChangeListener
  public void actionPerformed(Action action) {
    Task selectedTask = (Task) action.getParam();
    int taskIndex = selectedTask == null ? -1 : locateTask(selectedTask);

    if (taskIndex < 0) {
      favouritesList.clearSelection();
    } else {
      favouritesList.setSelectedIndex(taskIndex);
      favouritesList.ensureIndexIsVisible(taskIndex);
    }
  }

  /**
   * Our listener called when a user select some row in a favouritesList
   */
  private class FavouritesSelectionListener implements ListSelectionListener {
    public void valueChanged(ListSelectionEvent e) {
      if (e.getValueIsAdjusting()) {
        return;
      }
      int index = favouritesList.getSelectedIndex();
      if (index < 0) {
        return;
      }
      TasksInterface tasks = layoutManager.getTasksInterface();
      if (tasks == null) {
        return;
      }
      Task selectedTask = (Task) listModel.get(index);
      tasks.selectTask(selectedTask);
    }
  }

  /**
   * Listener to action fired when a task was changed. <br>
   * . It should repaint that task
   */
  private class TaskChangedAction implements TaskChangedListener {
    public void actionPerformed(Action action) {
      Task task = (Task) action.getParam();
      if (locateTask(task) >= 0) {
        favouritesList.repaint();
      }
    }
  }

}
