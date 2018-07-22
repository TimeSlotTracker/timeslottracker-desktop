package net.sf.timeslottracker.gui.reports;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.core.TimeSlotTracker;
import net.sf.timeslottracker.data.Attribute;
import net.sf.timeslottracker.data.AttributeCategory;
import net.sf.timeslottracker.data.AttributeType;
import net.sf.timeslottracker.data.DataSource;
import net.sf.timeslottracker.data.ProjectSummaryByDay;
import net.sf.timeslottracker.data.ProjectSummaryTimeSlot;
import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.gui.ChooseFileToSavePanel;
import net.sf.timeslottracker.gui.DialogPanel;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.reports.filters.DateFilter;
import net.sf.timeslottracker.gui.reports.filters.EncodingFilter;
import net.sf.timeslottracker.gui.reports.filters.Filter;
import net.sf.timeslottracker.gui.reports.filters.ReportTitleFilter;
import net.sf.timeslottracker.gui.reports.filters.RootTaskFilter;
import net.sf.timeslottracker.utils.SwingUtils;
import net.sf.timeslottracker.utils.TimeUtils;

/**
 * Report configuration window. It then uses report object to construct report.
 *
 * @author Last change: $Author: cnitsa $
 * @version File version: $Revision: 1142 $, $Date: 2009-07-31 17:41:54 +0700
 *          (Fri, 31 Jul 2009) $
 */
public class ReportConfiguration extends JDialog {

  private final LayoutManager layoutManager;

  private final ReportContext reportContext;

  private DialogPanel dialog;

  private final AbstractReport report;

  private DataSource dataSource;

  private final Calendar calendar;

  private final Locale locale;

  private final DateFormatSymbols dateFormatSymbols;

  private final SimpleDateFormat dateFormater;

  private JCheckBox useTemporaryXmlFile;

  private ChooseFileToSavePanel chooseXmlFile;

  private ChooseFileToSavePanel chooseFileResult;

  /** Contains a resource properties to localize reports. */
  private ResourceBundle reportStrings;

  /**
   * <code>true</code> if report was successfully completed. Sets inside
   * <code>runReport</code> method.
   */
  private boolean reportCompleted;

  /** Contains report problem message. Sets inside the <code>runReport</code>. */
  private String reportErrorMessage;

  /** output place to put reports */
  private PrintWriter writer;

  /** contains filters used to construct report * */
  private final Vector<Filter> filters;

  /** special instances to give the ability to know if there is any set date * */
  public DateFilter dateFilter;

  private final TimeSlotTracker timeSlotTracker;

  private final Configuration configuration;

  private ProjectSummaryByDay projectSummaryByDay;

  public ReportConfiguration(LayoutManager layoutManager, AbstractReport report) {
    this(layoutManager, report, null);
  }

  public ReportConfiguration(LayoutManager layoutManager,
      AbstractReport report, ReportContext reportContext) {
    super(layoutManager.getTimeSlotTracker().getRootFrame(), layoutManager
        .getCoreString("reports.configuration-window.title")
        + ": "
        + report.getTitleWithType(), true);
    this.layoutManager = layoutManager;
    this.reportContext = reportContext;
    this.timeSlotTracker = layoutManager.getTimeSlotTracker();
    configuration = timeSlotTracker.getConfiguration();
    this.report = report;
    filters = new Vector<Filter>();
    locale = this.timeSlotTracker.getLocale();
    calendar = Calendar.getInstance(locale);
    dateFormatSymbols = new DateFormatSymbols(locale);
    dateFormater = new SimpleDateFormat("yyyy-MM-dd", locale);
    createDialog();
    setVisible(true);
  }

  protected void setToolTipText(JComponent component, String keyString) {
    String toolTip = layoutManager.getCoreString(keyString);
    component.setToolTipText(toolTip);
  }

  @Override
  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSED) {
      SwingUtils.saveLocation(this);
    }
    super.processWindowEvent(e);
  }

  private void createDialog() {
    getContentPane().setLayout(new BorderLayout());

    Color background = getContentPane().getBackground();
    dialog = new DialogPanel();
    dialog.setBackground(background);

    // add specific filters
    if (report.showReportTitle()) {
      addFilter(new ReportTitleFilter(layoutManager));
    }

    if (report.showDatePeriod()) {
      dateFilter = new DateFilter(this, layoutManager);
      addFilter(dateFilter);
    }

    // add filters specific to report
    Collection<Filter> customFilters = report.getExtraFilters();
    if (customFilters != null) {
      for (Filter filter : customFilters) {
        addFilter(filter);
      }
    }

    // set reference to this in every registered filter.
    for (Filter filter : filters) {
      filter.setReportConfiguration(this);
    }

    // create part which occurs on every report
    createCommonPart();

    // set report context in every registered filter.
    setReportContext();

    getContentPane().add(dialog, BorderLayout.CENTER);
    getContentPane().add(createButtons(), BorderLayout.SOUTH);

    pack();
    setResizable(false);
    SwingUtils.setLocation(this);
  }

  /**
   * Creates common part wich occurs on every report. It contains
   * <ul>
   * <li>a way to export report to specific location</li>
   * </ul>
   */
  private void createCommonPart() {
    // choose starting task or (if not choosen) generate file from the root one.
    addFilter(new RootTaskFilter(layoutManager));

    // using temporary xml file or save it somewhere into disk
    useTemporaryXmlFile = new JCheckBox();
    dialog.addRow(layoutManager
        .getCoreString("reports.configuration-window.useTemplateXml"),
        useTemporaryXmlFile);
    setToolTipText(useTemporaryXmlFile,
        "reports.configuration-window.useTemplateXml.tooltip");
    chooseXmlFile = new ChooseFileToSavePanel(layoutManager);
    dialog
        .addRow(layoutManager
            .getCoreString("reports.configuration-window.xmlFileName"),
            chooseXmlFile);
    useTemporaryXmlFile.addActionListener(new TemporaryXmlCheckBoxAction());
    Boolean lastUseTemporaryXmlFile = configuration.getBoolean(
        Configuration.LAST_USE_TMP_XML, Boolean.TRUE);
    useTemporaryXmlFile.setSelected(lastUseTemporaryXmlFile.booleanValue());
    chooseXmlFile.setEnabled(!lastUseTemporaryXmlFile.booleanValue());
    if (!lastUseTemporaryXmlFile.booleanValue()) {
      chooseXmlFile.setFile(configuration.getString(
          Configuration.LAST_TMP_XML_FILE, null));
    }
    setToolTipText(chooseXmlFile,
        "reports.configuration-window.xmlFileName.tooltip");

    // chosing the result file
    chooseFileResult = new ChooseFileToSavePanel(layoutManager);
    String lastResultFile = configuration.getString(
        Configuration.LAST_RESULT_FILENAME, null);
    if (lastResultFile != null && lastResultFile.length() > 0) {
      chooseFileResult.setFile(lastResultFile);
    }

    setToolTipText(chooseFileResult, "reports.save.filename.field.tooltip");
    dialog.addRow(layoutManager.getCoreString("reports.save.filename.field"),
        chooseFileResult);
  }

  private JPanel createButtons() {
    JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JButton cancelButton = new JButton(
        layoutManager
            .getCoreString("reports.configuration-window.button.cancel"),
        layoutManager.getIcon("cancel"));
    CancelAction cancelAction = new CancelAction();
    cancelButton.addActionListener(cancelAction);
    buttons.add(cancelButton);

    // connect cancelAction with ESC key
    getRootPane().registerKeyboardAction(cancelAction,
        KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
        JComponent.WHEN_IN_FOCUSED_WINDOW);

    JButton runButton = new JButton(
        layoutManager.getCoreString("reports.configuration-window.button.run"),
        layoutManager.getIcon("report"));
    RunAction runAction = new RunAction();
    runButton.addActionListener(runAction);
    buttons.add(runButton);

    getRootPane().setDefaultButton(runButton);
    return buttons;
  }

  /**
   * Adds a filter to report.
   * <p/>
   * Filter is composed of panel where a user can input some filter and filter
   * methods to filter task.
   */
  private void addFilter(Filter filter) {
    if (filter == null) {
      return;
    }

    filters.add(filter);
    filter.update(dialog); // update dialog panel for given filter
  }

  /**
   * Makes report.
   * <p/>
   * <ol>
   * <li>It looks at every task and its timeslots.</li>
   * <li>Then it applies all filters</li>
   * <li>The output with formatted xml is then sent to xslt</li>
   * <li>The report is then saved when a users want it</li>
   * </ol>
   */
  private void runReport() {
    boolean createTemporaryXmlFile = useTemporaryXmlFile.isSelected();
    File xmlFile = null;

    try {
      prepareFilters();

      reportCompleted = false;
      reportErrorMessage = layoutManager
          .getCoreString("reports.configuration-window.run.unknownError");
      String debugMsg = layoutManager
          .getCoreString("reports.configuration-window.action.run.debug");
      timeSlotTracker.debugLog(debugMsg);

      dataSource = timeSlotTracker.getDataSource();
      if (dataSource == null) {
        reportErrorMessage = layoutManager
            .getCoreString("reports.configuration-window.run.noDataSource");
        return;
      }

      String dataDirectory = configuration.getString(
          Configuration.DATASOURCE_DIRECTORY, ".");
      File resultFile = chooseFileResult.getFile();
      if (resultFile == null) {
        reportErrorMessage = layoutManager
            .getCoreString("reports.configuration-window.run.noResultFile.msg");
        return;
      }
      configuration.set(Configuration.LAST_RESULT_FILENAME,
          resultFile.getAbsolutePath());

      if (createTemporaryXmlFile) {
        xmlFile = File.createTempFile("tstXmlData_", ".xml");
      } else {
        xmlFile = chooseXmlFile.getFile();
      }
      configuration.set(Configuration.LAST_USE_TMP_XML, createTemporaryXmlFile);
      configuration.set(Configuration.LAST_TMP_XML_FILE, xmlFile.getPath());

      // create data for report in (temporary) xml file
      writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
          new FileOutputStream(xmlFile), "UTF-8")));
      writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      writer.println("<!-- Generated: " + new Date() + " -->");
      writer.println("");
      writer.println("<TimeSlotTracker_Report>");
      writer.println("");
      exportReportStrings();
      writer.println("");
      exportPeriodLoop();
      writer.println("");

      projectSummaryByDay = new ProjectSummaryByDay();
      exportTasksByDayLoop(dataSource.getRoot(), "  ");
      projectSummaryByDay.toXml(writer);
      writer.println("");
      exportTask(dataSource.getRoot(), "  ");
      writer.println("");
      writer.println("</TimeSlotTracker_Report>");
      writer.flush();
      writer.close();

      // transform it with xslt template
      String dataPath = dataDirectory + System.getProperty("file.separator");
      Source xmlSource = new StreamSource(xmlFile);
      Source xsltSource = report.getXsltSource(dataPath);
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer trans = transformerFactory.newTransformer(xsltSource);
      prepareFilters(trans);

      String encoding = (String) trans
          .getParameter(EncodingFilter.PARAMETER_REPORT_OUTPUT_ENCODING);
      if (encoding == null) {
        encoding = "UTF-8";
      }

      PrintWriter printWriter = null;
      try {
        printWriter = new PrintWriter(new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream(resultFile), encoding)));
        Result result = new StreamResult(printWriter);
        trans.transform(xmlSource, result);
      } finally {
        if (printWriter != null) {
          printWriter.close();
        }
      }

      // copy css for html reports
      String method = trans.getOutputProperties().getProperty("method");
      if (method != null && method.equalsIgnoreCase("html")) {
        String destCssFileName = chooseFileResult.getFile().getParent();
        if (destCssFileName == null) {
          destCssFileName = "";
        }
        String reportFileName = "report.css";
        destCssFileName += File.separator + reportFileName;
        copyCssFile(reportFileName, new File(destCssFileName));
      }

      reportCompleted = true;

      openReport(resultFile);
    } catch (Exception e) {
      Object[] args = { e.getMessage() };
      String errorMsg = timeSlotTracker.getString(
          "reports.configuration-window.action.run.Exception", args);
      timeSlotTracker.errorLog(errorMsg);
      timeSlotTracker.errorLog(e);
      reportErrorMessage = errorMsg;
      return;
    } finally {
      if (createTemporaryXmlFile && xmlFile != null) {
        xmlFile.delete(); // delete the temporary xml data file
      }
    }
  }

  /**
   * Opens created document.
   *
   * @param file
   *          a file to open.
   */
  private void openReport(File file) {
    assert file != null;

    if (Desktop.isDesktopSupported()) {
      Desktop desktop = Desktop.getDesktop();
      try {
        desktop.open(file);
      } catch (Exception e) {
        e.printStackTrace();
        timeSlotTracker.errorLog("Error during opening document: " + e);
        timeSlotTracker.errorLog(e);
      }
    }
  }

  /** Prepares filters - calls their <code>beforeStart</code> methods. */
  private void prepareFilters() {
    for (Filter filter : filters) {
      filter.beforeStart();
    }
  }

  /**
   * Prepares filters - calls their <code>beforeStart(Transformer)</code>
   * methods.
   */
  private void prepareFilters(Transformer transformer) {
    for (Filter filter : filters) {
      filter.beforeStart(transformer);
    }
  }

  private void setReportContext() {
    if (reportContext == null) {
      return;
    }
    for (Filter filter : filters) {
      filter.setReportContext(reportContext);
    }
  }

  /**
   * Exports one tag enclosing its content in CDATA block.
   *
   * @param tagName
   *          tag name for this tag
   * @param value
   *          a value for this tag. A <code>null</code> value is changed to an
   *          empty string.
   * @param prefix
   *          a prefix for this tag.
   */
  private void exportTag(String tagName, String value, String prefix) {
    writer.print(prefix);
    writer.print("<" + tagName + "><![CDATA[");
    writer.print(value == null ? "" : value);
    writer.println("]]></" + tagName + ">");
  }

  /**
   * Exports one tag but <b>not</b> enclosing its content in CDATA block.
   *
   * @param tagName
   *          tag name for this tag
   * @param value
   *          a value for this tag. A <code>null</code> value is changed to an
   *          empty string.
   * @param prefix
   *          a prefix for this tag.
   */
  private void exportTag(String tagName, long value, String prefix) {
    writer.print(prefix);
    writer.print("<" + tagName + ">");
    writer.print(value);
    writer.println("</" + tagName + ">");
  }

  /** Checks if given task should be incluede into the ouput. */
  private boolean checkFilters(Task task) {
    boolean showTask = true;
    Iterator filtersIterator = filters.iterator();
    while (showTask && filtersIterator.hasNext()) {
      Filter filter = (Filter) filtersIterator.next();
      showTask = filter.matches(task);
    }
    return showTask;
  }

  /**
   * Exports one task into <code>writer</code> object.
   *
   * @return milliseconds in this task and its subtasks.
   */
  private long exportTask(Task task, String prefix) {
    boolean showTask = checkFilters(task);
    long milliseconds = 0;
    long millisecondsWithSubtasks = 0;
    String childPrefix = prefix + "  ";

    if (showTask) {
      writer.print(prefix);
      writer.println("<task taskId=\"_" + task.getId() + "\">");
      exportTag("name", task.getName(), childPrefix);
      exportTag("description", task.getDescription(), childPrefix);
      exportAttributes(task.getAttributes(), childPrefix);
      milliseconds = exportTimeSlots(task, childPrefix);
      millisecondsWithSubtasks = milliseconds;
    }

    // check children
    Collection childrenCollection = dataSource.getChildren(task);
    if (childrenCollection != null) {
      Iterator children = childrenCollection.iterator();
      while (children.hasNext()) {
        Task child = (Task) children.next();
        millisecondsWithSubtasks += exportTask(child, childPrefix);
      }
    }

    if (showTask) {
      // duration mast be printed at the end because of the need of counting the
      // duration in subtasks
      exportDuration("duration", milliseconds, millisecondsWithSubtasks,
          childPrefix);
      exportDuration("durationAll", task.getTime(false), task.getTime(true),
          childPrefix);

      writer.print(prefix);
      writer.println("</task>");
      writer.println("");
    }
    return millisecondsWithSubtasks;
  }

  /**
   * Exports tag <code>tagName</code> with subtags describing a duration period.
   *
   * @param tagName
   *          a name for a tag, e.g. "duration"
   * @param milliseconds
   *          milliseconds in this duration
   * @param millisecondsWithSubtasks
   *          if greater or equal then zero then it is also included in this
   *          duration tag.
   * @param prefix
   *          prefix to prefix this tag
   */
  private void exportDuration(String tagName, long milliseconds,
      long millisecondsWithSubtasks, String prefix) {
    String durationPrefix = prefix + "  ";
    writer.print(prefix);
    writer.println("<" + tagName + ">");
    exportTag("seconds", milliseconds / 1000, durationPrefix);
    exportTag("duration", layoutManager.formatDuration(milliseconds),
        durationPrefix);
    exportTag("secondsWithChildren", millisecondsWithSubtasks / 1000,
        durationPrefix);
    exportTag("durationWithChildren",
        layoutManager.formatDuration(millisecondsWithSubtasks), durationPrefix);
    writer.print(prefix);
    writer.println("</" + tagName + ">");
  }

  /**
   * Exports tag with timeslot.
   *
   * @return milliseconds took by only filtered timeslots
   */
  private long exportTimeSlots(Task parentTask, String prefix) {
    Collection timeslotsCollection = parentTask.getTimeslots();
    if (timeslotsCollection == null) {
      return 0;
    }
    long milliseconds = 0;
    Iterator timeslots = timeslotsCollection.iterator();
    while (timeslots.hasNext()) {
      TimeSlot timeslot = (TimeSlot) timeslots.next();
      milliseconds += exportTimeSlot(timeslot, prefix);
    }
    return milliseconds;
  }

  private long exportTimeSlot(TimeSlot timeslot, String prefix) {
    boolean showTimeSlot = true;
    Iterator filtersIterator = filters.iterator();
    while (showTimeSlot && filtersIterator.hasNext()) {
      Filter filter = (Filter) filtersIterator.next();
      showTimeSlot = filter.matches(timeslot);
    }
    if (!showTimeSlot) {
      return 0;
    }

    String childPrefix = prefix + "  ";
    writer.print(prefix);
    writer.println("<timeslot>");

    Date startDate = timeslot.getStartDate();
    Date stopDate = timeslot.getStopDate();
    if (stopDate == null
        || timeslot.equals(timeSlotTracker.getActiveTimeSlot())) {
      stopDate = TimeUtils.roundDate(new Date());
    }
    Date filterDate = null;

    // if timeslot started BEFORE report starting date get report starting date
    if (dateFilter != null && !dateFilter.getDatePeriod().isNoFiltering()) {
      filterDate = dateFilter.getDatePeriod().getStartPeriod();
    }
    if (filterDate != null && startDate != null && filterDate.after(startDate)) {
      startDate = filterDate;
    }

    // if timeslot stopped AFTER report stopping date get report stop date
    if (dateFilter != null && !dateFilter.getDatePeriod().isNoFiltering()) {
      filterDate = dateFilter.getDatePeriod().getEndPeriod();
    }
    if (filterDate != null && filterDate.before(stopDate)) {
      stopDate = filterDate;
    }

    if (startDate != null) {
      exportTagDate("startDate", startDate, childPrefix);
    }
    if (stopDate != null) {
      exportTagDate("stopDate", stopDate, childPrefix);
    }
    // duration
    long milliseconds = timeslot.getTime(startDate, stopDate);
    exportDuration("duration", milliseconds, -1, childPrefix);

    exportTag("description", timeslot.getDescription(), childPrefix);
    exportAttributes(timeslot.getAttributes(), childPrefix);

    writer.print(prefix);
    writer.println("</timeslot>");
    return milliseconds;
  }

  private void exportTagDate(String tagName, Date date, String prefix) {
    if (date == null) {
      return;
    }
    String datePrefix = prefix + " ";
    String childPrefix = datePrefix + " ";
    writer.print(prefix);
    writer.println("<" + tagName + ">");
    writer.print(datePrefix);
    writer.println("<datetime>");

    calendar.setTime(date);
    exportTag("date", dateFormater.format(date), childPrefix);
    exportTag("year", calendar.get(Calendar.YEAR), childPrefix);
    int month = calendar.get(Calendar.MONTH) + 1;
    exportTag("month", month, childPrefix);
    exportTag("monthName", dateFormatSymbols.getMonths()[month - 1],
        childPrefix);
    exportTag("day", calendar.get(Calendar.DATE), childPrefix);
    exportTag("weekOfYear", calendar.get(Calendar.WEEK_OF_YEAR), childPrefix);
    exportTag("weekOfMonth", calendar.get(Calendar.WEEK_OF_MONTH), childPrefix);

    // dayOfWeek. Starting with MONDAY=1, etc.
    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
    exportTag("dayOfWeekName", dateFormatSymbols.getWeekdays()[dayOfWeek],
        childPrefix);
    exportTag("dayOfWeekNameShort",
        dateFormatSymbols.getShortWeekdays()[dayOfWeek], childPrefix);
    dayOfWeek -= 1;
    if (dayOfWeek == 0) {
      // it was SUNDAY, by java used as a first day (==1)
      dayOfWeek = 7;
    }
    exportTag("dayOfWeek", dayOfWeek, childPrefix);
    exportTag("dayOfYear", calendar.get(Calendar.DAY_OF_YEAR), childPrefix);
    exportTag("hour", calendar.get(Calendar.HOUR_OF_DAY), childPrefix);
    exportTag("min", calendar.get(Calendar.MINUTE), childPrefix);

    writer.print(datePrefix);
    writer.println("</datetime>");
    writer.print(prefix);
    writer.println("</" + tagName + ">");
  }

  /**
   * Exports attributes from given collection of object of class Attribute.
   * <p/>
   * It constructs a tag "attributes" with subtags "attribute" one for every
   * attribute. Inside the "attribute" tag is "name" and "value" tags.
   *
   * @param attributes
   *          a collection of attributes to be included in the file.
   * @see net.sf.timeslottracker.data.Task#getAttributes()
   * @see net.sf.timeslottracker.data.TimeSlot#getAttributes()
   */
  private void exportAttributes(Collection attributes, String prefix) {
    writer.print(prefix);
    writer.println("<attributes>");
    String childPrefix = prefix + "  ";
    if (attributes != null) {
      Iterator attrs = attributes.iterator();
      while (attrs.hasNext()) {
        Attribute attribute = (Attribute) attrs.next();
        AttributeType type = attribute.getAttributeType();
        AttributeCategory category = type.getCategory();
        if (!type.isHiddenOnReports()) {
          writer.print(childPrefix);
          writer.println("<attribute>");
          exportTag("name", type.getName(), childPrefix + " ");
          exportTag("value", category.toString(attribute.get()), childPrefix
              + " ");
          writer.print(childPrefix);
          writer.println("</attribute>");
        }
      }
    }
    writer.print(prefix);
    writer.println("</attributes>");
  }

  /**
   * Gets a resource bundle report-oriented strings to enclose them into
   * template report xml file.
   * <p/>
   * The strings will be inserted into <b>dictonary</b> section.
   */
  private void getResourceBundleReportStrings() {
    try {
      /*
       * gets a string for a resource filename, based on the AbstractReport
       * class. Now it's got from one static file. String resource =
       * report.getClass().getName(); int lastDot = resource.lastIndexOf(".");
       * if (lastDot>0) { resource = resource.substring(lastDot+1); }
       */
      final String resource = "ReportStrings";
      reportStrings = ResourceBundle.getBundle(resource, locale);
    } catch (MissingResourceException e) {
      timeSlotTracker.errorLog(e);
    }
  }

  /** Exports a resource bundle strings from ReportStrings_[locale] to file. */
  private void exportReportStrings() {
    getResourceBundleReportStrings();
    if (reportStrings == null) {
      return;
    }
    writer.println("  <dictionary>");
    Enumeration keys = reportStrings.getKeys();
    while (keys.hasMoreElements()) {
      String key = (String) keys.nextElement();
      String value = reportStrings.getString(key);
      exportTag(key, value, "    ");
    }

    writer.println("  </dictionary>");
  }

  /**
   * Exports a loop with day by day from starting date to stopping date.
   * <p/>
   * Used to make a loop day by day in xslt.
   */
  private void exportPeriodLoop() {
    Date startDay = null;
    Date stopDay = null;
    if (dateFilter != null && !dateFilter.getDatePeriod().isNoFiltering()) {
      startDay = dateFilter.getDatePeriod().getStartPeriod();
      stopDay = dateFilter.getDatePeriod().getEndPeriod();
    }
    if (startDay == null || stopDay == null) {
      return;
    }

    calendar.setTime(startDay);
    writer.println("  <dayByDayLoop>");
    long startDayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
    exportTag("startDayOfYear", startDayOfYear, "    ");
    calendar.setTime(stopDay);
    long stopDayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
    exportTag("stopDayOfYear", stopDayOfYear, "    ");

    calendar.setTime(startDay);
    String key = "day";
    while (!calendar.getTime().after(stopDay)) {
      // long value = calendar.get(Calendar.DAY_OF_YEAR);
      // exportTag(key, value, "    ");
      exportTagDate(key, calendar.getTime(), "    ");
      calendar.add(Calendar.DATE, 1);
    }
    writer.println("  </dayByDayLoop>");
  }

  /**
   * Copy an css style file to the same location as final report is.
   *
   * @param cssFileName
   *          a name of a file with style definitions
   * @param destinationFile
   *          a File object to which we should copy a css file
   */
  private void copyCssFile(String cssFileName, File destinationFile) {
    InputStream source = null;
    FileOutputStream destination = null;
    try {
      String filename = "/xslt/" + cssFileName;
      source = ReportConfiguration.class.getResourceAsStream(filename);
      destination = new FileOutputStream(destinationFile);
      int readByte = 0;
      while ((readByte = source.read()) > 0) {
        destination.write(readByte);
      }
      destination.close();
      source.close();
    } catch (Exception e) {
      timeSlotTracker.errorLog(e);
    } finally {
      try {
        if (destination != null) {
          destination.close();
        }
        if (source != null) {
          source.close();
        }
      } catch (Exception e) {
        timeSlotTracker.errorLog(e);
      }
    }
  }

  /** Action used when a user chooses close button. */
  private class CancelAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      dispose();
    }
  }

  /** Action called when a useTemporaryXmlFile checkbox is clicked. */
  private class TemporaryXmlCheckBoxAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      chooseXmlFile.setEnabled(!useTemporaryXmlFile.isSelected());
    }
  }

  /** Action called when a user chooses run button. */
  private class RunAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      final TimeSlotTracker tst = layoutManager.getTimeSlotTracker();
      if (chooseFileResult.getFile() == null) {
        String msgTitle = layoutManager
            .getCoreString("reports.configuration-window.noResultFile.title");
        String msg = layoutManager
            .getCoreString("reports.configuration-window.noResultFile.msg");
        JOptionPane.showMessageDialog(ReportConfiguration.this, msg, msgTitle,
            JOptionPane.ERROR_MESSAGE);
        return;
      }

      try {
        tst.setCursorWait();
        Runnable runnable = new Runnable() {
          public void run() {
            Thread.yield();
            runReport();
          }
        };
        Thread runThread = new Thread(runnable);
        runThread.start();
        runThread.join();

        if (reportCompleted) {
          ReportConfiguration.this.dispose();
        } else {
          String title = layoutManager
              .getCoreString("reprots.configuration-window.run.errorTitle");
          JOptionPane.showMessageDialog(ReportConfiguration.this,
              reportErrorMessage, title, JOptionPane.ERROR_MESSAGE);
        }
      } catch (InterruptedException ex) {
      } finally {
        tst.setCursorDefault();
      }
    }
  }

  private boolean hasProjectSummaryAttributes(Task task) {
    boolean checkProjectNumber = false;
    boolean checkProjectTask = false;
    if (task != null && task.getAttributes() != null) {
      if (task.getAttributes().size() > 0) {
        Collection<Attribute> attrs = task.getAttributes();
        Iterator iterator = attrs.iterator();
        while (iterator.hasNext()) {
          Attribute a = ((Attribute) iterator.next());
          if ("Project number".equalsIgnoreCase(a.getAttributeType().getName()))
            checkProjectNumber = true;
          if ("Project task".equalsIgnoreCase(a.getAttributeType().getName()))
            checkProjectTask = true;
        }
      }
    }
    return checkProjectNumber && checkProjectTask;
  }

  private String getAttributeValue(Task task, String pName) {
    String value = "";
    if (task.getAttributes().size() > 0) {
      Collection<Attribute> attrs = task.getAttributes();
      Iterator iterator = attrs.iterator();
      while (iterator.hasNext()) {
        Attribute a = ((Attribute) iterator.next());
        if (pName.equalsIgnoreCase(a.getAttributeType().getName()))
          value = a.get().toString();
      }
    }

    if (value.equalsIgnoreCase("")) {
      if (task.getParentTask() != null) {
        if (task.getParentTask().getAttributes().size() > 0) {
          Collection<Attribute> attrs = task.getParentTask().getAttributes();
          Iterator iterator = attrs.iterator();
          while (iterator.hasNext()) {
            Attribute a = ((Attribute) iterator.next());
            if (pName.equalsIgnoreCase(a.getAttributeType().getName()))
              value = a.get().toString();
          }
        }
      }
    }
    return value;
  };

  /**
   * Exports a loop with tasks by day with attributes set.
   * <p/>
   * Used to make a loop tasks by day in xslt.
   */
  private void exportTasksByDayLoop(Task task, String prefix) {
    Date startDay = null;
    Date stopDay = null;
    if (dateFilter != null && !dateFilter.getDatePeriod().isNoFiltering()) {
      startDay = dateFilter.getDatePeriod().getStartPeriod();
      stopDay = dateFilter.getDatePeriod().getEndPeriod();
    }
    if (startDay == null || stopDay == null) {
      return;
    }

    projectSummaryByDay.setStartDate((new SimpleDateFormat("yyyy-MM-dd"))
        .format(startDay));
    projectSummaryByDay.setStopDate((new SimpleDateFormat("yyyy-MM-dd"))
        .format(stopDay));

    if (hasProjectSummaryAttributes(task)
        || hasProjectSummaryAttributes(task.getParentTask())) {
      Collection timeslotsCollection = task.getTimeslots();

      Iterator timeslots = timeslotsCollection.iterator();
      while (timeslots.hasNext()) {
        TimeSlot timeslot = (TimeSlot) timeslots.next();
        if (timeslot.getStartDate().after(startDay)
            && timeslot.getStartDate().before(stopDay)) {
          String pNumber = getAttributeValue(timeslot.getTask(),
              "Project number");
          String pTask = getAttributeValue(timeslot.getTask(), "Project task");
          ProjectSummaryTimeSlot projectSummaryTimeSlot = new ProjectSummaryTimeSlot(
              timeslot.getTask().getId(), pNumber, pTask,
              (new SimpleDateFormat("yyyy-MM-dd")).format(timeslot
                  .getStartDate()), timeslot.getTime());
          projectSummaryByDay.add(projectSummaryTimeSlot);
        }
      }
    }

    // check children
    Collection childrenCollection = dataSource.getChildren(task);
    if (childrenCollection != null) {
      Iterator children = childrenCollection.iterator();
      while (children.hasNext()) {
        Task child = (Task) children.next();
        exportTasksByDayLoop(child, "     ");
      }
    }
  }

}
