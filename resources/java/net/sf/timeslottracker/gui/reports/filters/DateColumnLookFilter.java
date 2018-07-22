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
 * A filter with a JComboBox to choose the look of column with date.
 * <p>
 * It can be yyyy-MM-dd, just week-name or month day
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class DateColumnLookFilter extends JPanel implements Filter {

  /** logging using java.util.logging package **/
  private static Logger logger = Logger
      .getLogger("net.sf.timeslottracker.gui.reports");

  public final static String PARAMETER_REPORT_DATE_COLUMN_LOOK = "dateColumnLook";

  private LayoutManager layoutManager;

  private JLabel label;

  private JComboBox lookField;

  private Vector valuesForReport;

  private Configuration configuration;

  public DateColumnLookFilter(LayoutManager layoutManager) {
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
        layoutManager.getCoreString("reports.filter.dateColumnLook.label"));

    String valueList = layoutManager
        .getCoreString("reports.filter.dateColumnLook.values");
    String valueForReportList = layoutManager
        .getCoreString("reports.filter.dateColumnLook.valuesForReport");
    Vector values = new Vector(
        StringUtils.convertStringLOV2Collection(valueList));
    valuesForReport = new Vector(
        StringUtils.convertStringLOV2Collection(valueForReportList));
    lookField = new JComboBox(values);

    String lastValue = configuration.getString(
        Configuration.LAST_DATE_COLUMN_LOOK, null);
    if (lastValue != null && lastValue.length() > 0) {
      try {
        int i = Integer.parseInt(lastValue);
        lookField.setSelectedIndex(i);
      } catch (Exception e) {
      }
    }

    add(lookField);
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
    String look = "yyyy-MM-dd/dayOfWeekName"; // default Value
    int index = lookField.getSelectedIndex();
    if (index < 0) {
      configuration.remove(Configuration.LAST_DATE_COLUMN_LOOK);
    } else {
      configuration
          .set(Configuration.LAST_DATE_COLUMN_LOOK, new Integer(index));
      try {
        look = (String) valuesForReport.get(index);
      } catch (ArrayIndexOutOfBoundsException e) {
        logger
            .info("Exception (1): Properties not valid. valuesForReport not found. Check below messages");
        String comboValue = (String) lookField.getSelectedItem();
        logger.info("Exception (2): Searching for index [" + index
            + "], value = [" + comboValue + "]");
        logger.info("Exception (3): Using default value = [" + look + "]");
        logger.info("Exception (4): " + e);
      }
    }
    if (look != null && look.length() > 0) {
      transformer.setParameter(PARAMETER_REPORT_DATE_COLUMN_LOOK, look);
    }
  }

  public boolean matches(Task task) {
    return true;
  }

  public boolean matches(TimeSlot timeSlot) {
    return true; // every timeslot should be enclosed. No filter.
  }

}
