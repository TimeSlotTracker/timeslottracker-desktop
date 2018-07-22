package net.sf.timeslottracker.gui.reports.filters;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.xml.transform.Transformer;

import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.gui.DialogPanel;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.reports.ReportConfiguration;
import net.sf.timeslottracker.gui.reports.ReportContext;

/**
 * A filter with a JTextField to choose the field separator during generating
 * (mainly) the csv report.
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class FieldSeparatorFilter extends JPanel implements Filter {

  public final static String PARAMETER_REPORT_COLUMN_SEPARATOR = "columnSeparator";

  private LayoutManager layoutManager;

  private JLabel label;

  private JTextField separatorField;

  public FieldSeparatorFilter(LayoutManager layoutManager) {
    super(new FlowLayout(FlowLayout.LEFT, 1, 0));
    this.layoutManager = layoutManager;
    constructPanel();
  }

  public void setReportConfiguration(ReportConfiguration reportConfiguration) {
  }

  public void setReportContext(ReportContext reportContext) {
  }

  private void constructPanel() {
    label = new JLabel(
        layoutManager.getCoreString("reports.filter.columnSeparator.label"));

    separatorField = new JTextField(3);
    separatorField.setText(";");
    add(separatorField);
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
    panel.addRow(label, this);
  }

  public void beforeStart() {
  }

  public void beforeStart(Transformer transformer) {
    String separator = separatorField.getText();
    if (separator != null && separator.length() > 0) {
      transformer.setParameter(PARAMETER_REPORT_COLUMN_SEPARATOR, separator);
    }
  }

  public boolean matches(Task task) {
    return true;
  }

  public boolean matches(TimeSlot timeSlot) {
    return true; // every timeslot should be enclosed. No filter.
  }

}
