package net.sf.timeslottracker.gui.layouts.classic.favourites;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JList;

import net.sf.timeslottracker.core.TimeSlotTracker;
import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.gui.LayoutManager;

/**
 * Custom ListCellRenderer for FavouritesModel class
 * 
 * File version: $Revision: 998 $, $Date: 2010-09-17 16:01:41 +0700 (Fri, 17 Sep
 * 2010) $ Last change: $Author: cnitsa $
 */
class FavouritesListCellRenderer extends DefaultListCellRenderer {

  private LayoutManager layoutManager;
  private TimeSlotTracker timeSlotTracker;

  private ImageIcon iconClock;
  private ImageIcon iconPause;

  FavouritesListCellRenderer(LayoutManager layoutManager) {
    super();
    this.layoutManager = layoutManager;
    this.iconClock = layoutManager.getIcon("title.icon");
    this.iconPause = layoutManager.getIcon("pause");
    this.timeSlotTracker = layoutManager.getTimeSlotTracker();
  }

  public Component getListCellRendererComponent(JList list, Object value,
      int index, boolean isSelected, boolean hasFocus) {
    super
        .getListCellRendererComponent(list, value, index, isSelected, hasFocus);

    Task task = (Task) value;
    TimeSlot activeTimeSlot = layoutManager.getTimeSlotTracker()
        .getActiveTimeSlot();

    if (activeTimeSlot != null && task.equals(activeTimeSlot.getTask())) {
      if (activeTimeSlot.getStartDate() != null) {
        setIcon(iconClock);
      } else {
        setIcon(iconPause);
      }
    }

    return this;

  }

}
