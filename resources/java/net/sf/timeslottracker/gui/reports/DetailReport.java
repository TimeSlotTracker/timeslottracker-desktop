package net.sf.timeslottracker.gui.reports;

import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import net.sf.timeslottracker.gui.LayoutManager;

/**
 * Detail report returing all data
 * 
 * @version File version: $Revision: 888 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class DetailReport extends AbstractReport {

  public DetailReport(LayoutManager layoutManager) {
    super(layoutManager);
  }

  public String getTitle() {
    return layoutManager.getCoreString("reports.detailReport.title");
  }

  public ReportType getType() {
    return ReportType.HTML;
  }

  public boolean showReportTitle() {
    return true;
  }

  public Source getXsltSource(String dataDirectory) {
    String filename = "/xslt/detail.xml";
    InputStream stream = DetailReport.class.getResourceAsStream(filename);
    return new StreamSource(stream);
  }

}
