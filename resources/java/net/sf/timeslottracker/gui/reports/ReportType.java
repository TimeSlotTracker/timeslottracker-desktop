package net.sf.timeslottracker.gui.reports;

/**
 * Type of report
 * 
 * File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700 (Sat, 16 May
 * 2009) $ Last change: $Author: cnitsa $
 */
public enum ReportType {
  CSV("CSV"), HTML("HTML"), TXT("TXT"), USER("*");

  private final String description;

  ReportType(String description) {
    this.description = description;
  }

  /**
   * @return description of type report
   */
  public String getDescription() {
    return description;
  }
}
