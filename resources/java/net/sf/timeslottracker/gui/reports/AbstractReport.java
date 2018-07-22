package net.sf.timeslottracker.gui.reports;

import java.util.Collection;

import javax.swing.JComponent;
import javax.xml.transform.Source;

import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.reports.filters.Filter;

/**
 * An abstract class every report have to extend.
 * 
 * @version File version: $Revision: 888 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public abstract class AbstractReport {
  protected LayoutManager layoutManager;

  public AbstractReport(LayoutManager layoutManager) {
    this.layoutManager = layoutManager;
  }

  protected void setToolTipText(JComponent component, String keyString) {
    String toolTip = layoutManager.getCoreString(keyString);
    component.setToolTipText(toolTip);
  }

  /**
   * Returns report's title.
   */
  public abstract String getTitle();

  /**
   * @return report's title with report's type description
   */
  public final String getTitleWithType() {
    return getType().getDescription() + " " + getTitle();
  }

  /**
   * @return type of report
   */
  public abstract ReportType getType();

  /**
   * Returns <code>true</code> if a filter panel with the report title should be
   * shown.
   */
  public boolean showReportTitle() {
    return true;
  }

  /**
   * Returns <code>true</code> if a panel with date period should be shown.
   */
  public boolean showDatePeriod() {
    return true;
  }

  /**
   * Returns a xslt source. You can decide - take it from the jar file or open
   * it from any file.
   * 
   * @param dataDirectory
   *          a path (with slash|backslash) of data files
   */
  public abstract Source getXsltSource(String dataDirectory);

  /**
   * Puts an extra configurations into configuration window. It also returns a
   * collection of filters to check if a task or timeslot should be included
   * into result xml.
   * <p>
   * It allows you to specify some specific options for your report.
   * 
   * @return <code>Collection</code> object with objects implementing
   *         <code>Filter</code> interface. <code>null</code> if there is no
   *         extra filters (default value).
   */
  public Collection<Filter> getExtraFilters() {
    return null;
  }
}
