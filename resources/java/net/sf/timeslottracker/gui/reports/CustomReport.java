package net.sf.timeslottracker.gui.reports;

import java.util.Collection;
import java.util.Vector;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.reports.filters.ChooseXsltFilter;
import net.sf.timeslottracker.gui.reports.filters.DateColumnLookFilter;
import net.sf.timeslottracker.gui.reports.filters.EncodingFilter;
import net.sf.timeslottracker.gui.reports.filters.Filter;

/**
 * Custom report which gives the ability to specify own xslt file.
 * 
 * @version File version: $Revision: 888 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class CustomReport extends AbstractReport {

  private Collection<Filter> filters;

  private ChooseXsltFilter chooseXsltFilter;

  private EncodingFilter encodingFilter;

  public CustomReport(LayoutManager layoutManager) {
    super(layoutManager);
    filters = new Vector<Filter>();
    chooseXsltFilter = new ChooseXsltFilter(layoutManager);
    filters.add(chooseXsltFilter);
    encodingFilter = new EncodingFilter(layoutManager);
    filters.add(encodingFilter);
    filters.add(new DateColumnLookFilter(layoutManager));
  }

  public String getTitle() {
    return layoutManager.getCoreString("reports.customReport.title");
  }

  public ReportType getType() {
    return ReportType.USER;
  }

  public Collection<Filter> getExtraFilters() {
    return filters;
  }

  public Source getXsltSource(String dataDirectory) {
    return new StreamSource(chooseXsltFilter.getFile());
  }

}
