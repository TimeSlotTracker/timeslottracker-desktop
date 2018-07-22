package net.sf.timeslottracker.gui.reports.filters;

import java.awt.FlowLayout;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.xml.transform.Transformer;

import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.gui.DialogPanel;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.reports.ReportConfiguration;
import net.sf.timeslottracker.gui.reports.ReportContext;
import net.sf.timeslottracker.utils.StringUtils;

/**
 * Filter to choose if we want to show the time as hh:mm or rather in decimal
 * format (i.e. 1 hour and 45 minutes == 1.75).
 * 
 * @author Created by User: zgibek Create date: 2008-01-08 00:02:42
 * @author Last change: $Author: cnitsa $
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 */
public class TimeFormatFilter extends JPanel implements Filter {

  /** logging using java.util.logging package **/
  private static Logger logger = Logger
      .getLogger("net.sf.timeslottracker.gui.reports");

  public final static String PARAMETER_REPORT_TIME_FORMAT_FILTER = "durationFormat";

  private LayoutManager layoutManager;

  private JLabel label;

  private JComboBox formatField;

  private Vector valuesForReport;

  private Configuration configuration;

  public TimeFormatFilter(LayoutManager layoutManager) {
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
    label = new JLabel(layoutManager.getCoreString("reports.filter."
        + PARAMETER_REPORT_TIME_FORMAT_FILTER + ".label"));

    String valueList = layoutManager.getCoreString("reports.filter."
        + PARAMETER_REPORT_TIME_FORMAT_FILTER + ".values");
    String valueForReportList = layoutManager.getCoreString("reports.filter."
        + PARAMETER_REPORT_TIME_FORMAT_FILTER + ".valuesForReport");
    Vector values = new Vector(
        StringUtils.convertStringLOV2Collection(valueList));
    valuesForReport = new Vector(
        StringUtils.convertStringLOV2Collection(valueForReportList));
    formatField = new JComboBox(values);

    String lastValue = configuration.getString(
        Configuration.LAST_DURATION_FORMAT, null);
    if (lastValue != null && lastValue.length() > 0) {
      try {
        int i = Integer.parseInt(lastValue);
        formatField.setSelectedIndex(i);
      } catch (Exception e) {
      }
    }

    add(formatField);
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
    String format = "hh:mm"; // default Value
    int index = formatField.getSelectedIndex();
    if (index < 0) {
      configuration.remove(Configuration.LAST_DURATION_FORMAT);
    } else {
      configuration.set(Configuration.LAST_DURATION_FORMAT, new Integer(index));
      try {
        format = (String) valuesForReport.get(index);
      } catch (ArrayIndexOutOfBoundsException e) {
        logger
            .info("Exception (1): Properties not valid. valuesForReport not found. Check below messages");
        String comboValue = (String) formatField.getSelectedItem();
        logger.info("Exception (2): Searching for index [" + index
            + "], value = [" + comboValue + "]");
        logger.info("Exception (3): Using default value = [" + format + "]");
        logger.info("Exception (4): " + e);
      }
    }
    if (format != null && format.length() > 0) {
      transformer.setParameter(PARAMETER_REPORT_TIME_FORMAT_FILTER, format);
    }
  }

  public boolean matches(Task task) {
    return true;
  }

  public boolean matches(TimeSlot timeSlot) {
    return true; // every timeslot should be enclosed. No filter.
  }

}
