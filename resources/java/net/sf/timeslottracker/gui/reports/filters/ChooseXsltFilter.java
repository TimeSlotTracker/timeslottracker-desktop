package net.sf.timeslottracker.gui.reports.filters;

import java.io.File;

import javax.swing.JLabel;
import javax.xml.transform.Transformer;

import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.gui.ChooseFileToOpenPanel;
import net.sf.timeslottracker.gui.DialogPanel;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.reports.ReportConfiguration;
import net.sf.timeslottracker.gui.reports.ReportContext;

/**
 * A filter with dialog box for choosing an xslt file.
 * <p>
 * It is composed of field to enter a filename and a file dialog button.
 * 
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class ChooseXsltFilter implements Filter {

  private LayoutManager layoutManager;

  private ChooseFileToOpenPanel chooseFilePanel;

  public ChooseXsltFilter(LayoutManager layoutManager) {
    this.layoutManager = layoutManager;
    chooseFilePanel = new ChooseFileToOpenPanel(layoutManager);
    chooseFilePanel.setToolTipText(layoutManager
        .getCoreString("reports.filter.chooseXsltFile.tooltip"));
  }

  public void setReportConfiguration(ReportConfiguration reportConfiguration) {
  }

  public void setReportContext(ReportContext reportContext) {
  }

  public File getFile() {
    return chooseFilePanel.getFile();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.sf.timeslottracker.gui.DialogPanelUpdater#update(net.sf.timeslottracker
   * .gui.DialogPanel)
   */
  @Override
  public void update(DialogPanel panel) {
    panel.addRow(
        new JLabel(layoutManager
            .getCoreString("reports.filter.chooseXsltFile.label")),
        chooseFilePanel);
  }

  public void beforeStart() {
  }

  public void beforeStart(Transformer transformer) {
  }

  public boolean matches(Task task) {
    // every task should be enclosed because it not a filter.
    return true;
  }

  public boolean matches(TimeSlot timeSlot) {
    return true; // every timeslot should be enclosed. No filter.
  }

}
