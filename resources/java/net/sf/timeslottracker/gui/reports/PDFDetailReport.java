package net.sf.timeslottracker.gui.reports;

import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import net.sf.timeslottracker.gui.LayoutManager;

/**
 * Detail report returning all data in PDF format
 * 
 * @version File version: $Revision: 888 $, $Date: 2019-03-25 18:53:21 +0700
 *          (Sat, 25 Mon 2019) $
 * @author Last change: $Author: frotondella $
 */
public class PDFDetailReport extends AbstractReport {

  public PDFDetailReport(LayoutManager layoutManager) {
    super(layoutManager);
  }

  public String getTitle() {
    return layoutManager.getCoreString("reports.detailReport.title");
  }

  public ReportType getType() {
    return ReportType.PDF;
  }

  public boolean showReportTitle() {
    return true;
  }

  public Source getXsltSource(String dataDirectory) {
    String filename = "/xslt/detail.xsl";
    return new StreamSource(PDFDetailReport.class.getResourceAsStream(filename));
  }
}
