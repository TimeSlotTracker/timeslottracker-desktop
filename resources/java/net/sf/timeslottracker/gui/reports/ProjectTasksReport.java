package net.sf.timeslottracker.gui.reports;

import net.sf.timeslottracker.gui.LayoutManager;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;

/**
 * Project tasks report returning all data, but without the timeslots
 * 
 * @version File version: $Revision: 888 $, $Date: 2014-02-02 09:04:21 +0100
 *          (Tue, 4 Feb 2014) $
 * @author Last change: $Author: ghermans $
 */
public class ProjectTasksReport extends AbstractReport {

  public ProjectTasksReport(LayoutManager layoutManager) {
    super(layoutManager);
  }

  public String getTitle() {
    return layoutManager.getCoreString("reports.projectTasksReport.title");
  }

  public ReportType getType() {
    return ReportType.HTML;
  }

  public boolean showReportTitle() {
    return true;
  }

  public Source getXsltSource(String dataDirectory) {
    String filename = "/xslt/project_tasks_timesheet_table.xml";
    InputStream stream = ProjectTasksReport.class.getResourceAsStream(filename);
    return new StreamSource(stream);
  }

}
