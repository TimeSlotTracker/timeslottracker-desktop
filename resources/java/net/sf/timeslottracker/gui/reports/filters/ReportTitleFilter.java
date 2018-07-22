package net.sf.timeslottracker.gui.reports.filters;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.xml.transform.Transformer;

import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.gui.DialogPanel;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.reports.ReportConfiguration;
import net.sf.timeslottracker.gui.reports.ReportContext;

/**
 * A filter with a JTextField to choose the title for report.
 * 
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class ReportTitleFilter extends JPanel implements Filter {

  public static final String PARAMETER_REPORT_TITLE = "reportTitle";

  private LayoutManager layoutManager;

  private JLabel label;

  private JTextField titleField;

  private Configuration configuration;

  public ReportTitleFilter(LayoutManager layoutManager) {
    super(new FlowLayout(FlowLayout.LEFT, 1, 0));
    this.layoutManager = layoutManager;
    this.configuration = layoutManager.getTimeSlotTracker().getConfiguration();
    constructPanel();
  }

  public void setReportConfiguration(ReportConfiguration reportConfiguration) {
  }

  public void setReportContext(ReportContext reportContext) {
  }

  private void constructPanel() {
    label = new JLabel(
        layoutManager.getCoreString("reports.filter.reportTitle.label"));

    titleField = new JTextField(40);
    String lastValue = configuration.getString(Configuration.LAST_REPORT_TITLE,
        null);
    if (lastValue != null && lastValue.length() > 0) {
      titleField.setText(lastValue);
    }
    add(titleField);
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
    configuration.set(Configuration.LAST_REPORT_TITLE, titleField.getText());
  }

  public void beforeStart(Transformer transformer) {
    String title = titleField.getText();
    if (title != null && title.length() > 0) {
      transformer.setParameter(PARAMETER_REPORT_TITLE, title);
    }
  }

  public boolean matches(Task task) {
    return true;
  }

  public boolean matches(TimeSlot timeSlot) {
    return true; // every timeslot should be enclosed. No filter.
  }

}
