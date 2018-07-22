package net.sf.timeslottracker.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.text.JTextComponent;

import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.utils.StringUtils;

/**
 * Combo box for entering timeslot's description with some last descriptions
 * from active task (history)
 *
 * File version: $Revision: 1204 $, $Date: 2009-05-16 08:53:21 +0700 (Sat, 16
 * May 2009) $ Last change: $Author: cnitsa $
 */
@SuppressWarnings("serial")
public class DescriptionInputComboBox extends JComboBox {
  /**
   * max elements for description history in comboBox
   */
  private final int max_description_history;

  public DescriptionInputComboBox(LayoutManager layoutManager, boolean readonly) {
    this(layoutManager, readonly, layoutManager.getTimeSlotsInterface()
        .getSelectedTask());
  }

  public DescriptionInputComboBox(LayoutManager layoutManager,
      boolean readonly, Task task) {
    Configuration configuration = layoutManager.getTimeSlotTracker()
        .getConfiguration();
    this.max_description_history = configuration.getInteger(
        Configuration.TIMESLOT_MAX_DESCRIPTION_HISTORY, 25).intValue();

    setEditable(!readonly);
    setActiveTask(task, true);
  }

  /**
   * @return timeslot description, may be null
   */
  public String getDescription() {
    Object item = getEditor().getItem();

    if (item == null) {
      return null;
    }

    return StringUtils.trim(item.toString());
  }

  /**
   * Set current active description to show
   * 
   * @param description
   *          timeslot's description
   */
  public void setActiveDescription(String description) {
    setSelectedItem(description);
    getEditor().selectAll();
  }

  /**
   * Set history for active task
   * 
   * @param task
   *          given task
   * @param selectText
   *          true - select description text, false - overwise
   */
  public void setActiveTask(Task task, boolean selectText) {
    setModel(new DefaultComboBoxModel(getHistory(task)));
    if (selectText) {
      getEditor().selectAll();
    }
  }

  /**
   * Selects description with autocomplete
   */
  public void selectByDescription() {
    String description = getDescription();
    if (!this.isPopupVisible()) {
      return;
    }

    if (StringUtils.isBlank(description) || description.length() < 2) {
      return;
    }

    for (int i = 0; i <= getModel().getSize(); i++) {
      Object elementAt = getModel().getElementAt(i);
      if (elementAt == null) {
        continue;
      }
      String s = elementAt.toString();
      if (StringUtils.isBlank(s)) {
        continue;
      }
      if (s.startsWith(description)) {
        getModel().setSelectedItem(elementAt);
        getEditor().setItem(description);
        JTextComponent editorComponent = (JTextComponent) getEditor().getEditorComponent();
        if (!description.equals(getModel().getSelectedItem().toString())) {
          editorComponent.setCaretPosition(description.length());
        }
      }
    }
  }

  private Vector<String> getHistory(Task actualTask) {
    Set<String> history = new HashSet<String>();

    List<TimeSlot> timeSlots = new ArrayList<TimeSlot>(
        actualTask.getTimeslots());
    for (int i = timeSlots.size() - 1; i >= Math.max(0, timeSlots.size()
        - max_description_history); i--) {
      add(timeSlots.get(i).getDescription(), history);
    }

    String description = actualTask.getDescription();
    if (!StringUtils.isBlank(description)) {
      for (String aSplit : description.split("\n")) {
        add(aSplit, history);
      }
    }

    Vector<String> sorterHistory = new Vector(history);
    Collections.sort(sorterHistory);
    return sorterHistory;
  }

  private void add(String description, Set<String> history) {
    if (StringUtils.isBlank(description)) {
      return;
    }
    description = StringUtils.trim(description);
    if (!history.contains(description)) {
      history.add(description);
    }
  }
}
