package net.sf.timeslottracker.gui.reports;

import java.io.InputStream;
import java.util.Collection;
import java.util.Vector;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.reports.filters.DateColumnLookFilter;
import net.sf.timeslottracker.gui.reports.filters.Filter;
import net.sf.timeslottracker.gui.reports.filters.TimeFormatFilter;

/**
 * Timesheet report printed as a table.
 * 
 * @version File version: $Revision: 888 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class TimesheetTableReport extends AbstractReport {

  public static enum Version {
    NO_DESCRIPTION, WITH_DESCRIPTION
  };

  private Collection<Filter> filters;

  private Version version;

  public TimesheetTableReport(LayoutManager layoutManager, Version version) {
    super(layoutManager);
    this.version = version;
    filters = new Vector<Filter>();
    filters.add(new DateColumnLookFilter(layoutManager));
    filters.add(new TimeFormatFilter(layoutManager));
  }

  public String getTitle() {
    String titleVersion = "reports.timesheet-table.title";
    if (version == Version.WITH_DESCRIPTION) {
      titleVersion = "reports.timesheet-table-with-description.title";
    }
    return layoutManager.getCoreString(titleVersion);
  }

  public ReportType getType() {
    return ReportType.HTML;
  }

  public boolean showReportTitle() {
    return true;
  }

  public Collection<Filter> getExtraFilters() {
    return filters;
  }

  public Source getXsltSource(String dataDirectory) {
    String filename = "/xslt/timesheet_table.xml";
    if (version == Version.WITH_DESCRIPTION) {
      filename = "/xslt/timesheet_table_with_description.xml";
    }
    InputStream stream = DetailReport.class.getResourceAsStream(filename);
    return new StreamSource(stream);
  }

}
