package net.sf.timeslottracker.gui.reports;

import java.io.InputStream;
import java.util.Collection;
import java.util.Vector;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.reports.filters.DateColumnLookFilter;
import net.sf.timeslottracker.gui.reports.filters.EncodingFilter;
import net.sf.timeslottracker.gui.reports.filters.FieldSeparatorFilter;
import net.sf.timeslottracker.gui.reports.filters.Filter;
import net.sf.timeslottracker.gui.reports.filters.IncludeColumnNamesFilter;
import net.sf.timeslottracker.gui.reports.filters.TimeFormatFilter;

/**
 * Timesheet report printed as a table in CSV format.
 * 
 * @version File version: $Revision: 888 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class CSVTimesheetTableReport extends AbstractReport {

  private Collection<Filter> filters;

  public CSVTimesheetTableReport(LayoutManager layoutManager) {
    super(layoutManager);
    filters = new Vector<Filter>();
    filters.add(new DateColumnLookFilter(layoutManager));
    filters.add(new TimeFormatFilter(layoutManager));
    filters.add(new FieldSeparatorFilter(layoutManager));
    filters.add(new EncodingFilter(layoutManager));
    filters.add(new IncludeColumnNamesFilter(layoutManager));
  }

  public String getTitle() {
    return layoutManager.getCoreString("reports.timesheet-table.title");
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
    String filename = "/xslt/csv_timesheet_table.xml";
    InputStream stream = DetailReport.class.getResourceAsStream(filename);
    return new StreamSource(stream);
  }
}
