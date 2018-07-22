package net.sf.timeslottracker.gui.reports;

/**
 * Defines the entry for context to use with reports.
 * <p/>
 * If set in ReportConfiguration it is used to iterate by all reports and give
 * the ability to set some parameters (like from configuration)
 * 
 * @author Created by User: zgibek Create date: 2008-01-25 02:08:37
 * @author Last change: $Author: cnitsa $
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 */
public interface ReportContext {

  /**
   * Enumeration with proper context parameters.
   */
  public enum Context {
    STARTING_TASK
  };

  /**
   * Method called by ReportConfiguration with every used reportFilter. Gives a
   * chance to set extra information, for example: starting node.
   * 
   * @return an object with report context. It depends of implementation what to
   *         return.
   * @param parameter
   *          a parameter as enum value.
   */
  public Object getReportContext(Context parameter);

}
