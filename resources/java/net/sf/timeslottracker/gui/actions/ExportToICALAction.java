package net.sf.timeslottracker.gui.actions;

import java.awt.event.ActionEvent;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Stack;

import javax.swing.AbstractAction;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.UidGenerator;
import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.gui.LayoutManager;

/**
 * Export to ical gui action
 * 
 * @version File version: $Revision: 1.23 $, $Date: 2007/04/07 03:59:30 $
 * @author Last change: $Author: cnitsa $
 */
public class ExportToICALAction extends AbstractAction {

  private final LayoutManager layoutManager;

  public ExportToICALAction(LayoutManager layoutManager) {
    super(layoutManager.getCoreString("export.action.name") + " ...",
        layoutManager.getIcon("new"));

    this.layoutManager = layoutManager;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed(ActionEvent e) {

    ExportToICALDialog dialog = new ExportToICALDialog(layoutManager);
    dialog.activate();

    if (dialog.isCanceled()) {
      return;
    }

    try {

      Calendar calendar = new Calendar();
      calendar.getProperties().add(
          new ProdId("-//Sourceforge//TimeSlotTracker 1.0//EN"));
      calendar.getProperties().add(Version.VERSION_2_0);
      calendar.getProperties().add(CalScale.GREGORIAN);

      Task actualTask = layoutManager.getTimeSlotsInterface().getSelectedTask();
      if (actualTask == null) {
        return;
      }

      UidGenerator ug = new UidGenerator("uidGen");

      Stack<Task> stack = new Stack<Task>();
      stack.add(actualTask);
      while (!stack.isEmpty()) {
        Task task = stack.pop();

        VToDo vToDo = new VToDo(null, task.getName());
        vToDo.getProperties().add(ug.generateUid());
        calendar.getComponents().add(vToDo);

        Collection<Task> children = task.getChildren();
        if (children != null && !children.isEmpty()) {
          stack.addAll(children);
        }
      }

      FileOutputStream fout = new FileOutputStream(dialog.getFile());
      CalendarOutputter outputter = new CalendarOutputter();
      outputter.output(calendar, fout);
    } catch (Exception e2) {
      layoutManager.getTimeSlotTracker().errorLog(e2);
    }

  }

}
