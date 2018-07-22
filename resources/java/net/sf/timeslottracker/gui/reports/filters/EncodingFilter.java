package net.sf.timeslottracker.gui.reports.filters;

import java.awt.FlowLayout;
import java.nio.charset.Charset;
import java.util.Vector;

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

/**
 * A filter to choose the encoding of output file.
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class EncodingFilter extends JPanel implements Filter {

  public final static String PARAMETER_REPORT_OUTPUT_ENCODING = "reportOutputEncoding";

  private LayoutManager layoutManager;

  private JLabel label;

  private JComboBox encodingField;

  private Configuration configuration;

  public EncodingFilter(LayoutManager layoutManager) {
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
        layoutManager.getCoreString("reports.filter.encoding.label"));

    encodingField = new JComboBox(new Vector<Charset>(Charset
        .availableCharsets().values()));
    encodingField.setSelectedItem(getSelectedCharset());

    add(encodingField);
  }

  private Charset getSelectedCharset() {
    String lastCharsetName = configuration.getString(
        Configuration.LAST_ENCODING_FILTER, null);
    Charset lastCharset;
    try {
      lastCharset = Charset.forName(lastCharsetName);
    } catch (IllegalArgumentException e) {
      lastCharset = Charset.forName("UTF-8");
    }
    return lastCharset;
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
    configuration.set(Configuration.LAST_ENCODING_FILTER, encodingField
        .getSelectedItem().toString());
  }

  public void beforeStart(Transformer transformer) {
    String encoding = encodingField.getSelectedItem().toString();
    if (encoding != null && encoding.length() > 0) {
      transformer.setParameter(PARAMETER_REPORT_OUTPUT_ENCODING, encoding);
    }
  }

  public boolean matches(Task task) {
    return true;
  }

  public boolean matches(TimeSlot timeSlot) {
    return true; // every timeslot should be enclosed. No filter.
  }

}
