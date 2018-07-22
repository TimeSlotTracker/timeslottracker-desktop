package net.sf.timeslottracker.gui.reports.filters;

import javax.xml.transform.Transformer;

import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.gui.DialogPanelUpdater;
import net.sf.timeslottracker.gui.reports.ReportConfiguration;
import net.sf.timeslottracker.gui.reports.ReportContext;

/**
 * An interface an every filter class should implement.
 * <p>
 * A filter class should be composed of a gui component via
 * {@link DialogPanelUpdater} and a methods that checks if a record should be
 * enclosed in an output.
 * 
 * @version File version: $Revision: 888 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public interface Filter extends DialogPanelUpdater {

  /**
   * Sets the ReportConfiguration object.
   */
  void setReportConfiguration(ReportConfiguration reportConfiguration);

  /**
   * Sets the report context (if it's important).
   * 
   * @param reportContext
   *          an object with some important information for some filters.
   */
  void setReportContext(ReportContext reportContext);

  /**
   * Called just before starting exporting data.
   * <p>
   * You can use this method if you want to do something extra before running
   * report.
   */
  void beforeStart();

  /**
   * Called just before starting transforming data with xslt.
   * <p>
   * You can use this method if you want to do something extra before running
   * report, for eg. setting some parameter into transformer (like a
   * ReportTitleFilter does).
   */
  void beforeStart(Transformer transformer);

  /**
   * Returns true if a task matches specified criteria.
   * 
   * @param task
   *          a task which is to be checked if it matches.
   */
  boolean matches(Task task);

  /**
   * Returns <code>true</code> if a timeslot matches specified criteria.
   * 
   * @param timeSlot
   *          timeslot to check.
   */
  boolean matches(TimeSlot timeSlot);

}
