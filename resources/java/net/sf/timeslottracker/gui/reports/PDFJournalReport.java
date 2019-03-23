package net.sf.timeslottracker.gui.reports;

import java.util.Collection;
import java.util.Vector;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.reports.filters.EncodingFilter;
import net.sf.timeslottracker.gui.reports.filters.Filter;

/**
 * Journal report (by days) in PDF form
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class PDFJournalReport extends AbstractReport {
  private Collection<Filter> filters;

  public PDFJournalReport(LayoutManager layoutManager) {
    super(layoutManager);
    filters = new Vector<Filter>();
    filters.add(new EncodingFilter(layoutManager));
  }

  public String getTitle() {
    return layoutManager.getCoreString("reports.journalReport.title");
  }

  public ReportType getType() {
    return ReportType.PDF;
  }

  public boolean showReportTitle() {
    return false;
  }

  public Collection<Filter> getExtraFilters() {
    return filters;
  }

  public Source getXsltSource(String dataDirectory) {
    String filename = "/xslt/journal.xsl";
    return new StreamSource(PDFJournalReport.class.getResourceAsStream(filename));
  }
}
