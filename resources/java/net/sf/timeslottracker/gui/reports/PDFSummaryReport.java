package net.sf.timeslottracker.gui.reports;

import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import net.sf.timeslottracker.gui.LayoutManager;

/**
 * Summary report returning all data in PDF form, but without the timeslots
 * 
 * @version File version: $Revision: 888 $, $Date: 2019-03-19 19:36:21 +0700
 *          (Tue, 19 Mar 2019) $
 * @author Last change: $Author: frotondella $
 */
public class PDFSummaryReport extends AbstractReport {

  public PDFSummaryReport(LayoutManager layoutManager) {
    super(layoutManager);
  }

  public String getTitle() {
    return layoutManager.getCoreString("reports.summaryReport.title");
  }

  public ReportType getType() {
    return ReportType.PDF;
  }

  public boolean showReportTitle() {
    return true;
  }

  public Source getXsltSource(String dataDirectory) {
    String filename = "/xslt/summary.xsl";
    return new StreamSource(PDFSummaryReport.class.getResourceAsStream(filename));
  }
}
