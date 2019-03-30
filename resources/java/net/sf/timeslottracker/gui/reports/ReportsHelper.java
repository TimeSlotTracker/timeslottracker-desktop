package net.sf.timeslottracker.gui.reports;

import java.awt.*;
import java.util.Collection;
import java.util.Vector;

import javax.swing.*;

import net.sf.timeslottracker.Starter;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.layouts.classic.JMenuItem;

/**
 * Gives a uniform way to configure all reports used in program. It should be
 * the only one place where the all reports are created, and then they
 * references are used in menu or other popups.
 *
 * @author Created by User: zgibek Create date: 2008-01-25 01:15:59
 * @author Last change: $Author: cnitsa $
 * @version File version: $Revision: 1105 $, $Date: 2009-05-16 08:53:21 +0700
 * (Sat, 16 May 2009) $
 */
public class ReportsHelper {
  private static Vector<AbstractReport> reports;

  static {
    LayoutManager layoutManager = Starter.getTimeSlotTracker()
        .getLayoutManager();
    assert layoutManager != null;
    AbstractReport separator = null;

    reports = new Vector<>();
    reports.add(new SummaryReport(layoutManager));
    reports.add(new DetailReport(layoutManager));
    reports.add(new JournalReport(layoutManager));
    reports.add(new TimesheetTableReport(layoutManager,
        TimesheetTableReport.Version.NO_DESCRIPTION));
    reports.add(new TimesheetTableReport(layoutManager,
        TimesheetTableReport.Version.WITH_DESCRIPTION));
    reports.add(new ProjectTasksReport(layoutManager));
    reports.add(separator);
    reports.add(new CSVReport(layoutManager));
    reports.add(new CSVJournalReport(layoutManager));
    reports.add(new CSVTimesheetTableReport(layoutManager));
    reports.add(separator);
    reports.add(new TXTDetailReport(layoutManager));
    reports.add(new TXTJournalReport(layoutManager));
    reports.add(new TXTJournalMonthlyReport(layoutManager));
    try {
      String report = "net.sf.timeslottracker.gui.reports.PDFSummaryReport";
      Class.forName(report); // checks for pdf classes
      reports.add(separator);
      addReport(layoutManager, report);
      addReport(layoutManager, "net.sf.timeslottracker.gui.reports.PDFDetailReport");
      addReport(layoutManager, "net.sf.timeslottracker.gui.reports.PDFJournalReport");
    } catch (Exception e) {
    }
    reports.add(separator);
    reports.add(new CustomReport(layoutManager));
  }

  private static void addReport(LayoutManager layoutManager, String report) throws InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
    reports.add((AbstractReport) Class.forName(report).getConstructor(LayoutManager.class).newInstance(layoutManager));
  }

  /**
   * Returns Collection of AbstractReprots. If any element is <code>null</code>
   * it means it's a separator (for grouping purposes).
   *
   * @return collection of object extending AbstractReport, with
   * <code>null</code> as separator.
   */
  public static Collection<AbstractReport> getReports() {
    return reports;
  }

  /**
   * Returns a menu with report items ready to add it to (popup) menu.
   *
   * @param plainFont     <code>true</code> if it should be drawn with thick (plain) font,
   *                      not a bold default
   * @param reportContext context to extra set report filters
   * @return whole menu for reports.
   */
  public static JMenu getReportMenu(boolean plainFont,
                                    final ReportContext reportContext) {
    assert reports != null;
    LayoutManager layoutManager = Starter.getTimeSlotTracker()
        .getLayoutManager();
    assert layoutManager != null;

    JMenu reportMenuItems = new JMenu();
    if (plainFont) {
      reportMenuItems.setFont(reportMenuItems.getFont().deriveFont(Font.PLAIN));
    }
    for (AbstractReport report : reports) {
      if (report != null) {
        reportMenuItems.add(makeReportMenuItem(report, layoutManager,
            plainFont, reportContext));
      } else {
        reportMenuItems.addSeparator();
      }
    }
    return reportMenuItems;
  }

  private static JMenuItem makeReportMenuItem(final AbstractReport report,
                                              final LayoutManager layoutManager, boolean plainFont,
                                              final ReportContext reportContext) {
    JMenuItem item = new JMenuItem(report.getTitleWithType(), plainFont);
    item.addActionListener(ae -> new ReportConfiguration(layoutManager, report, reportContext));
    return item;
  }
}
