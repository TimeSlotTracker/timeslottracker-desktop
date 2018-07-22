package net.sf.timeslottracker.gui.reports.filters;

import java.awt.FlowLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.xml.transform.Transformer;

import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.gui.DialogPanel;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.reports.ReportConfiguration;
import net.sf.timeslottracker.gui.reports.ReportContext;

/**
 * A filter with a checkbox to choose the if column names should be included on
 * the top of the file.
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class IncludeColumnNamesFilter extends JPanel implements Filter {

  public final static String PARAMETER_REPORT_INCLUDE_NAMES = "includeColumnNames";

  private LayoutManager layoutManager;

  private JLabel label;

  private JCheckBox includeField;

  public IncludeColumnNamesFilter(LayoutManager layoutManager) {
    super(new FlowLayout(FlowLayout.LEFT, 1, 0));
    this.layoutManager = layoutManager;
    constructPanel();
  }

  public void setReportConfiguration(ReportConfiguration reportConfiguration) {
  }

  public void setReportContext(ReportContext reportContext) {
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

  private void constructPanel() {
    label = new JLabel(
        layoutManager.getCoreString("reports.filter.includeColumnNames.label"));

    includeField = new JCheckBox();
    includeField.setSelected(true);
    add(includeField);
  }

  public void beforeStart() {
  }

  public void beforeStart(Transformer transformer) {
    String include = includeField.isSelected() ? "yes" : "no";
    transformer.setParameter(PARAMETER_REPORT_INCLUDE_NAMES, include);
  }

  public boolean matches(Task task) {
    return true;
  }

  public boolean matches(TimeSlot timeSlot) {
    return true; // every timeslot should be enclosed. No filter.
  }

}
