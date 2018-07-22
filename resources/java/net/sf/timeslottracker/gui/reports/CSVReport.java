package net.sf.timeslottracker.gui.reports;

import java.io.InputStream;
import java.util.Collection;
import java.util.Vector;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.reports.filters.EncodingFilter;
import net.sf.timeslottracker.gui.reports.filters.FieldSeparatorFilter;
import net.sf.timeslottracker.gui.reports.filters.Filter;
import net.sf.timeslottracker.gui.reports.filters.IncludeColumnNamesFilter;
import net.sf.timeslottracker.gui.reports.filters.TimeFormatFilter;

/**
 * CSV report returing data in a summary as a coma separated value.
 * <p>
 * The "comma" is selected via filter.
 * 
 * @version File version: $Revision: 888 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class CSVReport extends AbstractReport {

  private Collection<Filter> filters;

  private FieldSeparatorFilter separatorFilter;

  private IncludeColumnNamesFilter columnNamesFilter;

  private EncodingFilter encodingFilter;

  public CSVReport(LayoutManager layoutManager) {
    super(layoutManager);
    filters = new Vector<Filter>();
    separatorFilter = new FieldSeparatorFilter(layoutManager);
    filters.add(separatorFilter);
    encodingFilter = new EncodingFilter(layoutManager);
    filters.add(encodingFilter);
    columnNamesFilter = new IncludeColumnNamesFilter(layoutManager);
    filters.add(columnNamesFilter);
    filters.add(new TimeFormatFilter(layoutManager));
  }

  public String getTitle() {
    return layoutManager.getCoreString("reports.summaryReport.title");
  }

  public ReportType getType() {
    return ReportType.CSV;
  }

  public boolean showReportTitle() {
    return false;
  }

  public Collection<Filter> getExtraFilters() {
    return filters;
  }

  public Source getXsltSource(String dataDirectory) {
    String filename = "/xslt/csv.xml";
    InputStream stream = CSVReport.class.getResourceAsStream(filename);
    return new StreamSource(stream);
  }

}
