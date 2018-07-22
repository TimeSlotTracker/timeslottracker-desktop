package net.sf.timeslottracker.gui.reports.filters;

import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.xml.transform.Transformer;

import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.core.TimeSlotTracker;
import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.filters.TaskInPeriodFilter;
import net.sf.timeslottracker.filters.TimeSlotIncludedInPeriod;
import net.sf.timeslottracker.gui.DialogPanel;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.dateperiod.DatePeriod;
import net.sf.timeslottracker.gui.dateperiod.DatePeriod.PeriodType;
import net.sf.timeslottracker.gui.dateperiod.DatePeriodException;
import net.sf.timeslottracker.gui.dateperiod.DatePeriodPanel;
import net.sf.timeslottracker.gui.reports.ReportConfiguration;
import net.sf.timeslottracker.gui.reports.ReportContext;

/**
 * An class to filter it to specified date period.
 * <p>
 * It used {@link DatePeriodPanel}
 * 
 * @version File version: $Revision: 1082 $, $Date: 2009-06-06 11:46:49 +0700
 *          (Sat, 06 Jun 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class DateFilter implements Filter {

  public final static String PARAMETER_REPORT_START_DATE = "startDate";

  public final static String PARAMETER_REPORT_START_DAY_OF_YEAR = "startDayOfYear";

  public final static String PARAMETER_REPORT_STOP_DATE = "stopDate";

  public final static String PARAMETER_REPORT_STOP_DAY_OF_YEAR = "stopDayOfYear";

  protected final LayoutManager layoutManager;

  protected final TimeSlotTracker timeSlotTracker;

  protected final SimpleDateFormat dateFormater;

  public ReportConfiguration reportConfiguration;

  protected Configuration configuration;

  protected Calendar calendar;

  protected Locale locale;

  private final DatePeriodPanel datePeriodPanel;

  private final DatePeriod datePeriod;

  private TaskInPeriodFilter taskFilter;

  private TimeSlotIncludedInPeriod timeSlotFilter;

  public DateFilter(Component component, LayoutManager layoutManager) {
    this.layoutManager = layoutManager;
    this.timeSlotTracker = layoutManager.getTimeSlotTracker();
    this.configuration = timeSlotTracker.getConfiguration();

    this.locale = layoutManager.getTimeSlotTracker().getLocale();
    this.dateFormater = new SimpleDateFormat("yyyy-MM-dd", locale);
    this.calendar = Calendar.getInstance(locale);

    int firstDayOfWeek = configuration.getInteger(Configuration.WEEK_FIRST_DAY,
        Calendar.MONDAY);
    this.datePeriod = new DatePeriod(firstDayOfWeek);
    restore();
    this.datePeriodPanel = new DatePeriodPanel(layoutManager, datePeriod);
  }

  private void restore() {
    Integer reportType = configuration.getInteger(
        Configuration.LAST_REPORT_PERIOD_TYPE,
        PeriodType.USER_PERIOD.getPersistentId());
    datePeriod.setPeriodType(PeriodType.valueOf(reportType));

    Date start = getDate(Configuration.LAST_START_DATE);
    Date stop = getDate(Configuration.LAST_STOP_DATE);
    datePeriod.setUserPeriod(start, stop);
  }

  private Date getDate(String key) {
    Date date = null;
    try {
      String value = configuration.getString(key, null);
      if (value != null && value.length() > 0) {
        date = dateFormater.parse(value);
      }
    } catch (java.text.ParseException pe) {
    }
    return date;
  }

  public void setReportConfiguration(ReportConfiguration reportConfiguration) {
    this.reportConfiguration = reportConfiguration;
  }

  public void setReportContext(ReportContext reportContext) {
  }

  /**
   * Returns a date and/or time in Date object according to user input
   */
  public DatePeriod getDatePeriod() {
    return datePeriod;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.sf.timeslottracker.gui.DialogPanelUpdater#update(net.sf.timeslottracker
   * .gui.DialogPanel)
   */
  @Override
  public void update(DialogPanel panel) {
    this.datePeriodPanel.update(panel);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sf.timeslottracker.gui.reports.filters.Filter#beforeStart()
   */
  public void beforeStart() {
    // TODO check user input correctness
    try {
      datePeriodPanel.save();
    } catch (DatePeriodException e) {
      throw new RuntimeException(e.getMessage());
    }

    store();

    if (datePeriod.isNoFiltering()) {
      return;
    }

    taskFilter = new TaskInPeriodFilter(datePeriod.getStartPeriod(),
        datePeriod.getEndPeriod());

    timeSlotFilter = new TimeSlotIncludedInPeriod(timeSlotTracker,
        datePeriod.getStartPeriod(), datePeriod.getEndPeriod());
  }

  private void store() {
    configuration.set(Configuration.LAST_REPORT_PERIOD_TYPE, datePeriod
        .getPeriodType().getPersistentId());

    if (datePeriod.getUserPeriodStart() == null
        || dateFormater.format(datePeriod.getUserPeriodEnd()) == null) {
      return;
    }

    configuration.set(Configuration.LAST_START_DATE,
        dateFormater.format(datePeriod.getUserPeriodStart()));

    configuration.set(Configuration.LAST_STOP_DATE,
        dateFormater.format(datePeriod.getUserPeriodEnd()));
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.sf.timeslottracker.gui.reports.filters.Filter#beforeStart(javax.xml
   * .transform.Transformer)
   */
  public void beforeStart(Transformer transformer) {
    if (datePeriod.isNoFiltering()) {
      return;
    }

    transformer.setParameter(PARAMETER_REPORT_START_DATE,
        dateFormater.format(datePeriod.getStartPeriod()));

    calendar.setTime(datePeriod.getStartPeriod());
    transformer.setParameter(PARAMETER_REPORT_START_DAY_OF_YEAR, new Integer(
        calendar.get(Calendar.DAY_OF_YEAR)));

    transformer.setParameter(PARAMETER_REPORT_STOP_DATE,
        dateFormater.format(datePeriod.getEndPeriod()));

    calendar.setTime(datePeriod.getEndPeriod());
    transformer.setParameter(PARAMETER_REPORT_STOP_DAY_OF_YEAR, new Integer(
        calendar.get(Calendar.DAY_OF_YEAR)));
  }

  /*
   * (non-Javadoc)
   * 
   * @seenet.sf.timeslottracker.gui.reports.filters.Filter#matches(net.sf.
   * timeslottracker.data.Task)
   */
  public boolean matches(Task task) {
    if (task == null) {
      return false;
    }

    if (datePeriod.isNoFiltering()) {
      return true;
    }

    return taskFilter.accept(task);
  }

  /*
   * (non-Javadoc)
   * 
   * @seenet.sf.timeslottracker.gui.reports.filters.Filter#matches(net.sf.
   * timeslottracker.data.TimeSlot)
   */
  public boolean matches(TimeSlot timeSlot) {
    if (timeSlot == null) {
      return false;
    }

    if (datePeriod.isNoFiltering()) {
      return true;
    }

    return timeSlotFilter.accept(timeSlot);
  }

}
