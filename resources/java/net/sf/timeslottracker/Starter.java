package net.sf.timeslottracker;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.TrayIcon;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import net.sf.timeslottracker.core.Action;
import net.sf.timeslottracker.core.ActionListener;
import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.core.TimeSlotTracker;
import net.sf.timeslottracker.core.TimeoutTimer;
import net.sf.timeslottracker.data.AutoSaveTask;
import net.sf.timeslottracker.data.DataLoadedListener;
import net.sf.timeslottracker.data.DataSource;
import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TaskChangedListener;
import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.data.TimeSlotChangedListener;
import net.sf.timeslottracker.gui.IconManagerImpI;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.TimeSlotFilterListener;
import net.sf.timeslottracker.gui.TimeSlotInputDialog;
import net.sf.timeslottracker.gui.TimeSlotRestartDialog;
import net.sf.timeslottracker.gui.TimeSlotsInterface;
import net.sf.timeslottracker.gui.dateperiod.DatePeriod;
import net.sf.timeslottracker.gui.lookandfeel.LookAndFeelManagerImpl;
import net.sf.timeslottracker.gui.systemtray.TipOfTheDayService;
import net.sf.timeslottracker.gui.systemtray.TrayIconManager;
import net.sf.timeslottracker.gui.systemtray.TrayIconManagerImp;
import net.sf.timeslottracker.idledetector.UserIdleDetector;
import net.sf.timeslottracker.integrations.issuetracker.IssueTracker;
import net.sf.timeslottracker.integrations.issuetracker.jira.JiraTracker;
import net.sf.timeslottracker.monitoring.ScreenshotMonitoringTask;
import net.sf.timeslottracker.updateversion.VersionManager;
import net.sf.timeslottracker.utils.SSLUtils;
import net.sf.timeslottracker.utils.StringUtils;
import net.sf.timeslottracker.utils.SwingUtils;
import net.sf.timeslottracker.utils.TimeUtils;
import net.sf.timeslottracker.worktime.WorkTimeService;
import net.sf.timeslottracker.worktime.WorkTimeServiceImpl;

/**
 * Main class - it is run when a user starts application.
 * <p>
 * It implements the TimeSlotTracker interface so it is also our central point
 * for whole application
 * 
 * @version File version: $Revision: 1198 $, $Date: 2009-05-16 09:00:38 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
@SuppressWarnings("serial")
public class Starter extends JFrame implements TimeSlotTracker {

  /** logging using java.util.logging package * */
  private static final Logger LOG = Logger.getLogger("net.sf.timeslottracker");

  private static TimeSlotTracker timeSlotTrackerInstance;

  private static final ApplicationLock applicationLock = new ApplicationLock();

  /** Contains an active timeslot, if any. **/
  private TimeSlot activeTimeSlot;

  /**
   * Contains a resource messages
   */
  private ResourceBundle messages;

  private final List dataLoadedListeners = new ArrayList();
  private final List taskChangedListeners = new ArrayList();
  /**
   * a default queue for listeners. Key -> actionCode, Value - Collection of
   * listeners.
   */
  private final Map actionListeners = new java.util.HashMap();

  private TimeoutTimer autoSaveTimer;

  private TimeoutTimer monitorTimer;

  private Configuration configuration;

  private Locale locale;

  /** closing program state */
  private boolean closing;

  // services
  private IssueTracker jiraTracker;
  private WorkTimeService workTimeService;
  private TrayIconManager trayIconService;
  private IconManagerImpI iconManager;
  private LayoutManager layoutManager;
  private DataSource dataSource;

  private String additionalTitleSuffix = StringUtils.EMPTY;

  private UserIdleDetector userIdleDetector;

  private Starter() {
    super();
    timeSlotTrackerInstance = this;

    locale = Locale.getDefault();
    messages = ResourceBundle.getBundle("TimeSlotTracker", locale);
    configuration = new Configuration(this);
    System.setProperty("apple.laf.useScreenMenuBar", "true");

    // reload locale
    String language = configuration.getString(Configuration.APP_LOCALE, "en");
    locale = new Locale(language);
    messages = ResourceBundle.getBundle("TimeSlotTracker", locale);
    String msg = getString("starter.language.created.debug",
        new Object[] { locale });
    debugLog(msg);

    // setting look'n'feel
    new LookAndFeelManagerImpl(this);

    // create work time service
    try {
      workTimeService = new WorkTimeServiceImpl(this);
    } catch (IOException e1) {
      errorLog(e1);
      return;
    }

    // create datasource and layout
    try {
      iconManager = new IconManagerImpI();

      String dataSourceClass = configuration.getString(
          Configuration.DATASOURCE_CLASS,
          "net.sf.timeslottracker.data.xml.XmlDataSource");
      dataSource = (DataSource) Class.forName(dataSourceClass).newInstance();
      dataSource.setTimeSlotTracker(this);

      String layoutClass = configuration.getString(
          Configuration.LAYOUTMANAGER_CLASS,
          "net.sf.timeslottracker.gui.layouts.classic.ClassicLayout");
      layoutManager = (LayoutManager) Class.forName(layoutClass).newInstance();

      layoutManager.init(this);
    } catch (Exception e) {
      errorLog(e);
      return;
    }

    // create jira tracker
    jiraTracker = new JiraTracker(this);

    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());
    contentPane.add(layoutManager.getGUIComponent());

    if (layoutManager.getMenuBar() != null) {
      setJMenuBar(layoutManager.getMenuBar());
    }
    if (layoutManager.getToolBar() != null) {
      contentPane.add(layoutManager.getToolBar(), BorderLayout.NORTH);
    }

    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowCloser());

    // sets window size and location
    setResizable(true);
    SwingUtils.setWidthHeight(this, 900, 600);
    SwingUtils.setLocation(this);

    // sets title props
    setIconImage(getIcon("title.icon").getImage());

    // setup check new version manager
    VersionManager versionManager = new VersionManager(this);
    versionManager.installUpdater();

    // create and init tray icon service
    trayIconService = new TrayIconManagerImp(this);
    trayIconService.init();

    // setup thread for frame title updater
    installTitleUpdater();

    // setup the monitoring timer
    installMonitoringTimer();

    validate();
    addShutdownHook();
    layoutManager.postInit();

    userIdleDetector = new UserIdleDetector(this);
    userIdleDetector.start();

    // disable https certificate check
    try {
      SSLUtils.disableSSL();
    } catch (Exception e) {
      errorLog(e);
    }
  }

  private void installMonitoringTimer() {
    updateMonitoringTimer();

    // update listener - following listeners use it (as delegate)
    final ActionListener listener = new ActionListener() {
      public void actionPerformed(Action action) {
        if (isClosing()) {
          return;
        }
        updateMonitoringTimer();
      }
    };

    // updates when configuration changed
    addActionListener(listener, Action.ACTION_CONFIGURATION_CHANGED);
  }

  private void updateMonitoringTimer() {
    int delayMinutes = this.configuration.getInteger(
        Configuration.MONITORING_INTERVAL, 40);
    boolean enabled = this.configuration.getBoolean(
        Configuration.MONITORING_ENABLED, true) && delayMinutes > 0;

    if (enabled) { // enable
      int delaySeconds = delayMinutes * 60;
      if (monitorTimer != null && monitorTimer.getTimeout() == delaySeconds) {
        return;
      }

      if (monitorTimer != null) {
        monitorTimer.stop();
      }

      monitorTimer = new TimeoutTimer(this, "monitorTimer",
          new ScreenshotMonitoringTask(this), delaySeconds, -1);
      LOG.info("Monitoring thread restarted with timeout (minutes): "
          + delayMinutes);
    } else { // disable
      if (monitorTimer != null) {
        monitorTimer.stop();
        monitorTimer = null;
      }
      LOG.info("Monitoring thread stopped");
    }
  }

  private void installTitleUpdater() {
    // update listener - following listeners use it (as delegate)
    final ActionListener listener = new ActionListener() {

      private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
          "yyyy-MM-dd");

      public void actionPerformed(Action action) {
        if (isClosing()) {
          return;
        }

        Object param = action.getParam();
        if (param instanceof DatePeriod) {
          if (((DatePeriod) param).isNoFiltering()) {
            additionalTitleSuffix = StringUtils.EMPTY;
          } else {
            DatePeriod data = (DatePeriod) param;
            Date startPeriod = data.getStartPeriod();
            Date endPeriod = data.getEndPeriod();

            additionalTitleSuffix = " - (" + DATE_FORMAT.format(startPeriod)
                + " : " + DATE_FORMAT.format(endPeriod) + ")";
          }
        }

        final String title = getApplicationTitle();
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            Starter.this.setTitle(title);
          }
        });

        trayIconService.setTooltip(title);
      }
    };

    // updates when task changed
    addActionListener(new TaskChangedListener() {
      public void actionPerformed(Action action) {
        listener.actionPerformed(action);
      }
    });

    // updates when data loaded
    addActionListener(new DataLoadedListener() {
      public void actionPerformed(Action action) {
        listener.actionPerformed(action);
      }
    });

    // updates when configuration changed
    addActionListener(listener, Action.ACTION_CONFIGURATION_CHANGED);

    // updates when timeslot changed
    layoutManager.addActionListener(new TimeSlotChangedListener() {
      public void actionPerformed(Action action) {
        listener.actionPerformed(action);
      }
    });

    // update when time slot filter changes
    layoutManager.addActionListener(new TimeSlotFilterListener() {
      @Override
      public void actionPerformed(Action action) {
        listener.actionPerformed(action);
      }
    });

    // updates by timer
    Integer timeout = configuration.getInteger(
        Configuration.APP_TITLE_REFRESH_TIMEOUT, 30);
    String timerName = getString("starter.title.timer.update.name",
        new Object[] { timeout });
    new TimeoutTimer(this, timerName, listener, timeout.intValue(), -1);
  }

  /**
   * Helps to save app's data when system is shutdowned.
   */
  private void addShutdownHook() {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        setClosing(true);
        saveApplicationData(false);
      }
    });
  }

  public void addActionListener(ActionListener listener) {
    if (listener instanceof DataLoadedListener) {
      dataLoadedListeners.add(listener);
    } else if (listener instanceof TaskChangedListener) {
      taskChangedListeners.add(listener);
    }
  }

  public void addActionListener(ActionListener listener, String selector) {
    Collection listeners = (Collection) actionListeners.get(selector);
    if (listeners == null) {
      listeners = new Vector();
      actionListeners.put(selector, listeners);
    }
    listeners.add(listener);
  }

  public String getApplicationTitle() {
    // when data not yet loaded
    if (dataSource.getRoot() == null) {
      return getString("starter.title");
    }

    // show full title
    TimeSlot activeTimeSlot = getActiveTimeSlot();
    boolean isActive = activeTimeSlot != null;

    String titleFormat;
    if (isActive) {
      titleFormat = getConfiguration().getString(
          Configuration.APP_TITLE_TEMPLATE_ACTIVE,
          "%timeslotElapsedTime %timeslotDescription - %tst");
    } else {
      titleFormat = getConfiguration().getString(
          Configuration.APP_TITLE_TEMPLATE_PASSIVE, "%tst - %version");
    }

    if (isActive) {
      // add some information about active task, timeslot
      Task activeTask = activeTimeSlot.getTask();

      String timeslotElapsedTime = layoutManager.formatDuration(activeTimeSlot
          .getTime());
      titleFormat = titleFormat.replaceAll("%timeslotElapsedTime",
          timeslotElapsedTime);

      String timeslotDescription = activeTimeSlot.getDescription();
      titleFormat = titleFormat.replaceAll("%timeslotDescription",
          timeslotDescription);

      String taskName = activeTask.getName();
      titleFormat = titleFormat.replaceAll("%taskName", taskName);

      String taskElapsedTime = layoutManager.formatDuration(activeTask
          .getTime(true));
      titleFormat = titleFormat.replaceAll("%taskElapsedTime", taskElapsedTime);
    }

    // add some common information
    Date currentTime = new Date();
    Calendar startDate = TimeUtils.getDayBegin(currentTime);
    Calendar endDate = TimeUtils.getDayEnd(currentTime);

    Task task = dataSource.getRoot();
    long elapsedToday = task.getTime(true, startDate.getTime(),
        endDate.getTime());
    titleFormat = titleFormat.replaceAll("%elapsedToday",
        layoutManager.formatDuration(elapsedToday));

    Integer workingHours = getConfiguration().getInteger(
        Configuration.HOURS_PER_WORKING_DAY, 24);
    long plannedToday = workingHours * 60 * 60 * 1000;
    titleFormat = titleFormat.replaceAll("%plannedToday",
        layoutManager.formatDuration(plannedToday));

    long remainToday = Math.max(0, plannedToday - elapsedToday);
    titleFormat = titleFormat.replaceAll("%remainToday",
        layoutManager.formatDuration(remainToday));

    int workingPercentDone = Math.round((100 * elapsedToday) / plannedToday);
    titleFormat = titleFormat.replaceAll("%workingPercentDone",
        String.valueOf(workingPercentDone));

    String tst = getString("starter.title");
    titleFormat = titleFormat.replaceAll("%tst", String.valueOf(tst));

    String version = configuration.getVersionString();
    titleFormat = titleFormat.replaceAll("%version", String.valueOf(version));

    return titleFormat + additionalTitleSuffix;
  }

  public DataSource getDataSource() {
    return dataSource;
  }

  public String getString(String key) {
    try {
      if (messages != null) {
        return messages.getString(key);
      }
      errorLog("Starter.getString():Localizator object is null!");
    } catch (MissingResourceException e) {
      Object[] args = { key };
      String message = getString("starter.cannot-find-locale-key", args);
      errorLog(message);
      errorLog(e);
    }
    return null;
  }

  public String getString(final String key, final Object[] args) {
    if (messages == null) {
      errorLog("Starter.getString():Localizator object is null!");
    }
    String msgToFormat = getString(key);
    MessageFormat format = new MessageFormat(msgToFormat, getLocale());
    String msgDone;
    try {
      msgDone = format.format(args);
    } catch (IllegalArgumentException e) {
      // /Object msgArgs[] = { e.getMessage() };
      // msgDone = getString("starter.cannot-format-message", msgArgs);
      msgDone = "Cannot format message: " + e.getMessage();
      errorLog(e);
    }
    return msgDone;
  }

  /**
   * @see net.sf.timeslottracker.core.TimeSlotTracker#getLocale()
   */
  public Locale getLocale() {
    if (configuration == null || locale == null) {
      return Locale.getDefault();
    }
    return locale;
  }

  /**
   * @see net.sf.timeslottracker.core.TimeSlotTracker#getLayoutManager()
   */
  public LayoutManager getLayoutManager() {
    return layoutManager;
  }

  /**
   * Passes call to layout manager to read all data.
   */
  public boolean reloadData() {
    if (dataSource != null) {
      if (!dataSource.reloadData()) {
        String errTitle = getString("starter.dataSource.reloadData.error.title");
        String errMsg = getString("starter.dataSource.reloadData.error.msg");
        JOptionPane.showMessageDialog(this, errMsg, errTitle,
            JOptionPane.ERROR_MESSAGE);
        return false;
      }

      // create autoSaveTimer
      int autoSaveTimeout = configuration.getInteger(
          Configuration.DATASOURCE_AUTOSAVE_TIMEOUT, 60).intValue();
      Object[] autoSaveArgs = { new Integer(autoSaveTimeout) };
      String autoSaveName = getString("starter.dataSource.autoSave.timer.name",
          autoSaveArgs);
      if (autoSaveTimer != null) {
        autoSaveTimer.stop();
      }
      autoSaveTimer = new TimeoutTimer(this, autoSaveName, new AutoSaveTask(
          this), autoSaveTimeout, -1);
    }

    return true;
  }

  /**
   * Just starts the application. Makes a gui interface (represented by Layout
   * class)
   */
  public static void main(String[] args) {
    LOG.setLevel(Level.ALL);
    try {
      FileHandler loggerFileHandler = new FileHandler("%t" + File.separatorChar
              + "timeslottracker.%g.log");
      loggerFileHandler.setFormatter(new SimpleFormatter());
      LOG.addHandler(loggerFileHandler);
    } catch (Exception ex) {
      LOG.throwing("Starter", "main", ex);
    }

    LOG.info("Starting TimeSlotTrackerApplication");

    Starter starter = new Starter();
    starter.setVisible(true);

    if (!applicationLock.tryLock()) {
      JOptionPane.showMessageDialog(starter,
          starter.getString("starter.only-one-instance.exception.msg"),
          starter.getString("alert.error.title"), JOptionPane.ERROR_MESSAGE);
      System.exit(1);
    }

    if (!starter.reloadData()) {
      System.exit(1);
    }

    new TipOfTheDayService(starter);
    starter.setCursor(CURSOR_DEFAULT);
  }

  /**
   * Returns instance of TimeSlotTracker created during starting.
   * 
   * @return Class implementing TimeSlotTracker interface.
   */
  public static TimeSlotTracker getTimeSlotTracker() {
    return timeSlotTrackerInstance;
  }

  public void quit() {
    setClosing(true);
    if (saveApplicationData(true)) {
      System.exit(0);
    } else {
      setClosing(false);
    }
  }

  public IssueTracker getIssueTracker() {
    return jiraTracker;
  }

  @Override
  public WorkTimeService getWorkTimeService() {
    return workTimeService;
  }

  /**
   * Saves application data and returns <code>false</code> on fail.
   * 
   * @param askOnFail
   *          ask user if continue even after error during saving.
   * @return <code>true</code> if there was no error.
   */
  private boolean saveApplicationData(boolean askOnFail) {
    LOG.info("Saving data before closing");

    // saving window settings
    SwingUtils.saveWidthHeight(this);
    SwingUtils.saveLocation(this);

    if (dataSource != null) {
      if (!dataSource.saveAll() && askOnFail) {
        String errorTitle = getString("starter.WindowCloser.error.title");
        String errorMsg = getString("starter.WindowCloser.error.msg");
        int exitAnyway = JOptionPane.showConfirmDialog(this, errorMsg,
            errorTitle, JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
        if (exitAnyway != JOptionPane.YES_OPTION) {
          return false;
        }
      }
    }
    if (configuration != null) {
      configuration.save();
    }

    applicationLock.releaseLock();
    return true;
  }

  private class WindowCloser extends WindowAdapter {
    public void windowClosing(WindowEvent e) {
      final Boolean shouldMinimize = configuration.getBoolean(
          Configuration.TRAY_ICON_CLOSING_SHOULD_MINIMIZE, true);

      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          if (trayIconService.hasTrayIcon() && shouldMinimize) {
            setVisible(false);
          } else {
            quit();
          }
        }
      });
    }

    @Override
    public void windowIconified(WindowEvent e) {
      // minimize or hide window if need
      final Boolean minimizeInTray = configuration.getBoolean(
          Configuration.TRAY_ICON_MINIMIZE, true);
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          if (trayIconService.hasTrayIcon() && minimizeInTray) {
            setVisible(false);
          } else {
            setState(Frame.ICONIFIED);
          }
        }
      });
    }
  }

  public void errorLog(String message) {
    LOG.severe(message);
  }

  public void errorLog(Exception exception) {
    LOG.log(Level.SEVERE, exception.toString(), exception);
  }

  public void debugLog(String message) {
    LOG.fine(message);
  }

  public ImageIcon getIcon(String iconPath) {
    try {
      return iconManager.getIcon(iconPath);
    } catch (Exception e) {
      errorLog(e);
      return null;
    }
  }

  public Configuration getConfiguration() {
    return configuration;
  }

  public void setCursorWait() {
    setCursor(CURSOR_WAIT);
  }

  public void setCursorDefault() {
    setCursor(CURSOR_DEFAULT);
  }

  public void setActiveTimeSlot(TimeSlot timeslot) {
    this.activeTimeSlot = timeslot;
  }

  public TimeSlot getActiveTimeSlot() {
    return activeTimeSlot;
  }

  public boolean startTiming(String description, Date startTime) {
    // skip if selected root node
    Task selectedTask = layoutManager.getTimeSlotsInterface().getSelectedTask();
    if (selectedTask == dataSource.getRoot()) {
      return false;
    }

    // save time: when method invoked or given start time
    // (we will stop active timeslot by given time)
    Date invokeTime = startTime == null ? new Date() : startTime;

    // check if it's active timeslot needs stopping
    TimeSlot previousTimeslot = getActiveTimeSlot();
    Task previousTask = previousTimeslot != null ? previousTimeslot.getTask()
        : null;
    boolean taskChanged = previousTask != null && previousTask != selectedTask;
    boolean needStopPrevious = previousTimeslot != null
        && previousTimeslot.getStopDate() == null;
    boolean pausedPreviousTimeslot = needStopPrevious
        && previousTimeslot.getStartDate() == null;

    // if needs ask user for confirmation
    Boolean needConfirmation = getConfiguration().getBoolean(
        Configuration.CONFIRMATION_PREVIOUS_TIMESLOT_EXISTS, Boolean.TRUE);
    if (needStopPrevious && needConfirmation && !pausedPreviousTimeslot) {
      Object[] msgArgs = { previousTask, previousTimeslot };
      boolean stopSelected = JOptionPane.YES_OPTION == JOptionPane
          .showConfirmDialog(this,
              getString("timing.previousTimeSlotExists.msg", msgArgs),
              getString("timing.previousTimeSlotExists.title"),
              JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

      if (!stopSelected) {
        if (getConfiguration().getBoolean(
            Configuration.CUSTOM_SHOW_MESSAGE_AFTER_CANCEL_TASK, true)) {
          JOptionPane.showMessageDialog(this,
              getString("timing.previousTimeSlot.exit.msg"),
              getString("timing.previousTimeSlot.exit.title"),
              JOptionPane.INFORMATION_MESSAGE);
        }
        return false;
      }
    }

    // if description is null ask user for it
    if (StringUtils.isBlank(description)) {
      TimeSlotInputDialog dlg = new TimeSlotInputDialog(layoutManager);
      dlg.activate();
      description = dlg.getDescription();

      if (dlg.getStartDate() != null) {
        invokeTime = dlg.getStartDate();
      }

      // if a description is not given don't add record
      // a user chosen cancel button
      if (StringUtils.isBlank(description)) {
        if (needStopPrevious
            && getConfiguration().getBoolean(
                Configuration.CUSTOM_SHOW_MESSAGE_AFTER_CANCEL_TASK, true)) {
          JOptionPane.showMessageDialog(this,
              getString("timing.previousTimeSlot.exit.msg"),
              getString("timing.previousTimeSlot.exit.title"),
              JOptionPane.INFORMATION_MESSAGE);
        }
        return false;
      }
    }
    description = StringUtils.trim(description);

    // all data received, stops previousTimeslot if needs and start new
    if (needStopPrevious && !pausedPreviousTimeslot) {
      previousTimeslot.setStopDate(invokeTime);
      setActiveTimeSlot(null);
      layoutManager.fireTimeSlotChanged(previousTimeslot);
      fireTaskChanged(previousTimeslot.getTask());
    }

    TimeSlotsInterface timeslots = layoutManager.getTimeSlotsInterface();
    final TimeSlot timeslot;
    if (!pausedPreviousTimeslot) {
      timeslot = dataSource.createTimeSlot(null, invokeTime, null, description);
      if (timeslots != null) {
        timeslots.add(timeslot);
      }
    } else {
      timeslot = previousTimeslot;
      timeslot.setStartDate(invokeTime);
      timeslot.setDescription(description);

      if (taskChanged) {
        timeslot.getTask().deleteTimeslot(timeslot);
        setActiveTimeSlot(null);
        fireTaskChanged(previousTask);

        selectedTask.addTimeslot(timeslot);
      }

      layoutManager.fireTimeSlotChanged(timeslot);
      if (timeslots != null) {
        timeslots.refresh();
      }
    }

    // minimize or hide window if need
    final Boolean minimizeAfterStart = configuration.getBoolean(
        Configuration.CUSTOM_MINIMIZE_WINDOW_AFTER_START, false);
    final Boolean minimizeInTray = configuration.getBoolean(
        Configuration.TRAY_ICON_MINIMIZE, true);
    final String d = description;
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        if (minimizeAfterStart) {
          if (trayIconService.hasTrayIcon() && minimizeInTray) {
            setVisible(false);
          } else {
            setState(Frame.ICONIFIED);
          }
        }
      }
    });

    Boolean showTaskJustStarted = configuration.getBoolean(
        Configuration.CONFIRMATION_SHOW_TASK_HAS_JUST_STARTED_MESSAGE, true);
    if (showTaskJustStarted.booleanValue() == true) {
      final String title = getString("trayIcon.title.timing.started",
          new Object[] { layoutManager.getTasksInterface().getSelected()
              .getName() });
      trayIconService.showMessage(title, d + " ...", TrayIcon.MessageType.INFO);
    }

    // select active timeslot
    setActiveTimeSlot(timeslot);
    fireTaskChanged(timeslot.getTask());
    layoutManager.fireTaskSelectionChanged(timeslot.getTask());

    return true;
  }

  public boolean startTiming() {
    return startTiming(null, null);
  }

  public boolean startTiming(String description) {
    return startTiming(description, null);
  }

  public void pauseTiming() {
    LOG.info("PauseTiming");
    // check if there is an active any other timeslot now
    TimeSlot previousTimeslot = getActiveTimeSlot();
    if (previousTimeslot == null) {
      return;
    }
    Task previousTask = previousTimeslot.getTask();
    String description = previousTimeslot.getDescription();

    if (previousTimeslot.getStartDate() != null) {
      previousTimeslot.setStopDate(new Date());

      TimeSlot timeslot = dataSource.createTimeSlot(null, null, null,
          description);
      previousTask.addTimeslot(timeslot);
      TimeSlotsInterface timeslots = layoutManager.getTimeSlotsInterface();
      if (timeslots != null) {
        timeslots.update(previousTimeslot);
      }
      setActiveTimeSlot(timeslot);
      layoutManager.fireTimeSlotChanged(timeslot);

      fireTaskChanged(previousTask);
      layoutManager.fireTaskSelectionChanged(previousTask);
    }
  }

  public void stopTiming() {
    LOG.info("StopTiming");
    // check if there is an active any other timeslot now
    TimeSlot previousTimeslot = getActiveTimeSlot();
    if (previousTimeslot == null) {
      return;
    }

    TimeSlotsInterface timeslots = layoutManager.getTimeSlotsInterface();
    boolean isTimeSlots = timeslots != null;

    Task previousTask = previousTimeslot.getTask();
    if (previousTimeslot.getStartDate() != null) {
      previousTimeslot.setStopDate(new Date());
      if (isTimeSlots) {
        timeslots.update(previousTimeslot);
      }
    } else {
      previousTask.deleteTimeslot(previousTimeslot);
      if (isTimeSlots) {
        timeslots.refresh();
      }
    }

    setActiveTimeSlot(null);
    fireTaskChanged(previousTask);
  }

  public void fireDataLoaded() {
    LOG.entering(this.getClass().getName(), "fireDataLoaded");
    try {
      final Action action = new Action(DataSource.DATA_LOADED, dataSource, null);
      setCursorWait();
      Runnable runnable = new Runnable() {
        public void run() {
          Thread.yield();
          debugLog(action.getName());
          Iterator listeners = dataLoadedListeners.iterator();
          while (listeners.hasNext()) {
            ActionListener listener = (ActionListener) listeners.next();
            listener.actionPerformed(action);
          }
        }
      };
      Thread reloadThread = new Thread(runnable);
      reloadThread.start();
      reloadThread.join();
    } catch (InterruptedException ex) {
      LOG.throwing(this.getClass().getName(), "fireDataLoaded", ex);
    } finally {
      setCursorDefault();
      LOG.exiting(this.getClass().getName(), "fireDataLoaded");
    }
  }

  public void fireTaskChanged(Task changedTask) {
    Action action = new Action("taskChangedAction", null, changedTask);
    debugLog(action.getName() + ": " + changedTask);
    Iterator listeners = taskChangedListeners.iterator();
    while (listeners.hasNext()) {
      ActionListener listener = (ActionListener) listeners.next();
      listener.actionPerformed(action);
    }
  }

  public void fireAction(String actionCode) {
    fireAction(new Action(actionCode, null, null));
  }

  public void fireAction(Action action) {
    debugLog(action.getName());
    Collection listenersCollection = (Collection) actionListeners.get(action
        .getName());
    if (listenersCollection == null) {
      return;
    }
    Iterator listeners = listenersCollection.iterator();
    while (listeners.hasNext()) {
      ActionListener listener = (ActionListener) listeners.next();
      listener.actionPerformed(action);
    }
  }

  public JFrame getRootFrame() {
    return this;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sf.timeslottracker.core.TimeSlotTracker#getTrayIconService()
   */
  public TrayIconManager getTrayIconService() {
    return trayIconService;
  }

  public void setClosing(boolean closing) {
    this.closing = true;
  }

  public boolean isClosing() {
    return closing;
  }

  @Override
  public boolean restartTiming(String description) {
    LOG.info("RestartTiming");

    Boolean showDialogToSetCustomTime = configuration.getBoolean(
        Configuration.CONFIRMATION_SHOW_DIALOG_FOR_CUSTOM_RESTART_TIME, false);

    Date selectedDate;
    if (showDialogToSetCustomTime) {
      TimeSlotRestartDialog timeSlotRestartDialog = new TimeSlotRestartDialog(
          layoutManager);
      timeSlotRestartDialog.activate();
      selectedDate = timeSlotRestartDialog.getStartDate();
      if (selectedDate == null) {
        // user cancel restart
        return false;
      }
    } else {
      selectedDate = Calendar.getInstance().getTime();
    }

    // get selected timeslot
    TimeSlot selectedTimeslot = layoutManager.getTimeSlotsInterface()
        .getSelected();

    // check if we have a selected timeslot and use its description to
    // start a new timeslot
    if (selectedTimeslot != null) {
      description = selectedTimeslot.getDescription();
    }

    // start timing of new timeslot with selectedDate as start date
    startTiming(description, selectedDate);

    return true;
  }

}
