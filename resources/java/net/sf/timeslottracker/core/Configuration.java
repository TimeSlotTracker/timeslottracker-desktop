package net.sf.timeslottracker.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import net.sf.timeslottracker.data.DataSource;
import net.sf.timeslottracker.data.common.TransactionalFileSaver;
import net.sf.timeslottracker.utils.StringUtils;

/**
 * A class to customize TimeSlotTracker application.
 * <p>
 * With it you can save any configuration value.
 *
 * @version File version: $Revision: 1190 $, $Date: 2009-08-04 19:26:06 +0700
 *          (Tue, 04 Aug 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class Configuration {
  // declaring logger
  private static Logger logger = Logger
      .getLogger("net.sf.timeslottracker.core.Configuration");

  private static final String SNAPSHOT = "SNAPSHOT";

  public static final String APP_TITLE_REFRESH_TIMEOUT = "app.window.title.refreshTimeout.seconds";
  public static final String APP_TITLE_TEMPLATE_ACTIVE = "app.window.title.template.active";
  public static final String APP_TITLE_TEMPLATE_PASSIVE = "app.window.title.template.passive";
  public static final String APP_LOCALE = "app.locale.code";

  public static final String HOURS_PER_WORKING_DAY = "time.duration.format.hoursPerWorkingDay";
  public static final String TIME_DURATION_FORMAT = "time.duration.format";
  public static final String WEEK_FIRST_DAY = "week.first.day";

  public static final String DATASOURCE_DIRECTORY = "dataSource.data.directory";
  public static final String DATASOURCE_DIRECTORY_CURRENT_FOLDER = "dataSource.data.directory.currentFolder";
  public static final String DATASOURCE_AUTOSAVE_TIMEOUT = "dataSource.autoSave.timeout.seconds";
  public static final String DATASOURCE_CLASS = "app.dataSource.class";

  public static final String CONFIRMATION_PREVIOUS_TIMESLOT_EXISTS = "app.confirmation.previousTimeSlotExists";
  public static final String CONFIRMATION_SHOW_TASK_HAS_JUST_STARTED_MESSAGE = "tray.icon.show.task.has.just.started.message";
  public static final String CONFIRMATION_SHOW_DIALOG_FOR_CUSTOM_RESTART_TIME = "show.dialog.for.restart.at.custom.time";

  public static final String LAYOUTMANAGER_CLASS = "app.layoutManager.class";

  public static final String TASKINFO_REFRESH_TIMEOUT = "gui.classicLayout.taskInfo.refreshTimeout.seconds";

  public static final String LOOK_AND_FEEL_CLASS = "gui.lookandfeel.class";

  public static final String TIMESLOT_MAX_DESCRIPTION_HISTORY = "timeSlots.inputDescriptionDialog.maxHistory";

  public static final String BACKUP_ON_STARTUP = "backup.option.onStartup";
  public static final String BACKUP_ON_SHUTDOWN = "backup.option.onShutdown";
  public static final String BACKUP_DIRECTORY = "backup.directory";

  public static final String LAST_REPORT_TITLE = "temp.report.title";
  public static final String LAST_REPORT_PERIOD_TYPE = "temp.report.periodType";
  public static final String LAST_START_DATE = "temp.report.startDate";
  public static final String LAST_STOP_DATE = "temp.report.stopDate";
  public static final String LAST_RESULT_FILENAME = "temp.report.resultFileName";
  public static final String LAST_ENCODING_FILTER = "temp.report.encoding.output";
  public static final String LAST_USE_TMP_XML = "temp.report.useTemporaryXmlFile";
  public static final String LAST_TMP_XML_FILE = "temp.report.TemporaryXmlFileName";
  public static final String LAST_DATE_COLUMN_LOOK = "temp.report.dateColumnLook";
  public static final String LAST_DURATION_FORMAT = "temp.report.durationFormat";
  public static final String LAST_TASKS_FAVOURITES_SPLIT = "temp.split.tasks.favourites.position";
  public static final String LAST_TASKINFO_TIMESLOTS_SPLIT = "temp.split.taskInfo.timeSlots.position";
  public static final String LAST_MAIN_SPLIT = "temp.split.tasks.timeSlots.position";
  public static final String LAST_TIMEPANEL_DAY = "temp.taskinfo.timepanel.day.value";
  public static final String LAST_TIMEPANEL_WEEK = "temp.taskinfo.timepanel.week.value";
  public static final String LAST_TIMEPANEL_MONTH = "temp.taskinfo.timepanel.month.value";
  public static final String LAST_TIMESLOTS_TABLE_COLUMN_ORDER = "temp.timeslots.table.column.order";
  public static final String LAST_TIMESLOTS_TABLE_SORT_COLUMN = "temp.timeslots.table.sort.column";
  public static final String LAST_TIMESLOTS_TABLE_SORT_DIR = "temp.timeslots.table.sort.direction";
  public static final String LAST_TIMESLOTS_TABLE_FILTER_TYPE = "temp.timeslots.table.filter.type";
  public static final String LAST_TIMESLOTS_TABLE_FILTER_START = "temp.timeslots.table.filter.start";
  public static final String LAST_TIMESLOTS_TABLE_FILTER_END = "temp.timeslots.table.filter.end";

  public static final String JIRA_ENABLED = "jira.enabled";
  public static final String JIRA_URL = "jira.url";
  public static final String JIRA_LOGIN = "jira.login";
  public static final String JIRA_PASSWORD = "jira.password";
  public static final String JIRA_FILTER = "jira.filter";
  public static final String JIRA_ISSUE_URL_TEMPLATE = "jira.issue.url.template";
  public static final String JIRA_FILTER_URL_TEMPLATE = "jira.filter.url.template";
  public static final String JIRA_VERSION = "jira.version";

  public static final String TRAY_ICON_ENABLED = "tray.icon.enabled";
  public static final String TRAY_ICON_MAC_SHORTCUTS = "tray.icon.shorcuts.macstyle";
  public static final String TRAY_ICON_MINIMIZE = "tray.icon.minimize";
  public static final String TRAY_ICON_CLOSING_SHOULD_MINIMIZE = "tray.icon.closing.should.minimize";

  public static final String TIP_OF_THE_DAY_ENABLED = "tipOfTheDay.enabled";
  public static final String TIP_OF_THE_DAY_MINUTES_REPEAT = "tipOfTheDay.minutes.repeat";

  public static final String CHECK_NEW_VERSION_ENABLED = "check.new.version.enabled";
  public static final String CHECK_NEW_VERSION_DAYS = "check.new.version.days";
  public static final String CHECK_NEW_VERSION_LAST_UPDATE = "check.new.version.last.update";

  public static final String CUSTOM_MINIMIZE_WINDOW_AFTER_START = "custom.minimize.window.after.start";
  public static final String CUSTOM_SHOW_MESSAGE_AFTER_CANCEL_TASK = "custom.show.message.after.cancel.task";
  public static final String CUSTOM_SHOW_TASK_BY_DAYS_SUMMARY = "custom.show.task.by.days.summary";
  public static final String CUSTOM_USE_MAC_SYSTEM_TRAY_ICONS = "custom.use.mac.system.tray.icons";

  public static final String TASK_TREE_SHOW_HIDDEN_TASKS = "task.tree.show.hidden.tasks";

  public static final String MONITORING_ENABLED = "monitoring.enabled";
  public static final String MONITORING_INTERVAL = "monitoring.interval";
  public static final String MONITORING_GRABBER_ENABLED = "monitoring.image.grabber.enabled";
  public static final String MONITORING_IMAGE_DIR = "monitoring.image.dir";
  public static final String MONITORING_IMAGE_TIMEOUT_DAYS = "monitoring.image.timeout.days";

  public static final String USER_IDLE_DETECTOR_ENABLED = "user.idle.detector.enabled";
  public static final String USER_IDLE_DETECTOR_TIMEOUT = "user.idle.detector.timeout";

  private String configurationFileName;

  private Properties properties;

  private final TimeSlotTracker timeSlotTracker;

  private Properties version;

  private static final String TIMESLOTTRACKER_PROPERTIES_FILENAME = "timeslottracker.properties";

  /**
   * If program started with this environment variable set then the properties
   * file are loaded from this directory.
   * <p>
   * Run program with
   *
   * <pre>
   *  java -Dprop.directory=path_to_directory_where_properties_stored -jar tst.jar
   * </pre>
   */
  String PROPERTY_DIRECTORY = "prop.directory";

  public Configuration(TimeSlotTracker timeSlotTracker) {
    this.timeSlotTracker = timeSlotTracker;
    reload();
    loadVersionProperties();
  }

  /**
   * Loads a version property from jar file - a property file with version
   * number and information it is a developer one.
   * <p>
   * This file is automatically generated by the ant script.
   */
  private void loadVersionProperties() {
    try {
      InputStream stream = Configuration.class
          .getResourceAsStream("/version.properties");
      version = new Properties();
      version.load(stream);
    } catch (IOException ex) {
      timeSlotTracker.errorLog(ex);
      Object[] msgArgs = { ex.getMessage() };
      String warningTitle = timeSlotTracker
          .getString("configuration.loadVersionProperties.exception.title");
      String warningMsg = timeSlotTracker.getString(
          "configuration.loadVersionProperties.exception.msg", msgArgs);
      JOptionPane.showMessageDialog(timeSlotTracker.getRootFrame(), warningMsg,
          warningTitle, JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * Returns loaded version properties.
   *
   * @see #loadVersionProperties()
   */
  public Properties getVersionProperties() {
    return version;
  }

  /**
   * Returns <code>true</code> if <code>version</code> property contains SNAPSHOT.
   */
  public boolean isDeveloperVersion() {
    return getVersion().contains(SNAPSHOT);
  }

  private String getVersion() {
    return version.getProperty("version", "no-info");
  }

  /**
   */
  public String getVersionString() {
    // add version number
    Object[] msgArgs = { version.getProperty("version", "no-info") };
    String msgVersion;
    if (isDeveloperVersion()) {
      msgVersion = timeSlotTracker.getString("starter.title.version.developer",
          msgArgs);
    } else {
      msgVersion = timeSlotTracker.getString("starter.title.version.release",
          msgArgs);
    }
    return msgVersion;
  }

  /**
   * Loads configuration.
   * <p>
   * This method reloads configuration (throwing away any changes was made since
   * last saving).
   */
  public void reload() {
    String directory = getPropertiesDirectory();

    configurationFileName = directory + System.getProperty("file.separator")
        + TIMESLOTTRACKER_PROPERTIES_FILENAME;
    try {
      // restore broken previous save file action
      TransactionalFileSaver saver = new TransactionalFileSaver(timeSlotTracker,
          configurationFileName);
      saver.check();

      File file = new File(configurationFileName);
      if (!file.exists()) {
        properties = new Properties();
        Object[] loadedArgs = { configurationFileName };
        String loadedMsg = timeSlotTracker
            .getString("configuration.reload.debug.not.loaded", loadedArgs);
        logger.info(loadedMsg);
      } else {
        FileInputStream inputStream = new FileInputStream(
            configurationFileName);
        properties = new Properties();
        properties.load(inputStream);
        inputStream.close();
        Object[] loadedArgs = { configurationFileName };
        String loadedMsg = timeSlotTracker
            .getString("configuration.reload.debug.loaded", loadedArgs);
        logger.info(loadedMsg);
      }
    } catch (IOException ex) {
      timeSlotTracker.errorLog(ex);
      Object[] msgArgs = { ex.getMessage() };
      String warningTitle = timeSlotTracker
          .getString("configuration.reload.exception.title");
      String warningMsg = timeSlotTracker
          .getString("configuration.reload.exception.msg", msgArgs);
      JOptionPane.showMessageDialog(null, warningMsg, warningTitle,
          JOptionPane.ERROR_MESSAGE);
    }
  }

  public String getPropertiesDirectory() {
    final String fileSeparator = System.getProperty("file.separator");

    // if the PROPERTY_DIRECTORY is defined as environment variable, it is more
    // important
    String directory = System.getProperty(PROPERTY_DIRECTORY);

    // then, if the DataSource.TIMESLOTTRACKER_DIRECTORY is defined as
    // environment variable, it is more important
    if (directory == null) {
      directory = System.getProperty(DataSource.TIMESLOTTRACKER_DIRECTORY);
    }

    // if not specified in command line check if data exists in the directory
    // with jar file
    if (directory == null) {
      File currentFolder = CurrentFolderFinder.getCurrentFolder();
      logger.info(
          "Configuration.reload::currentFolder (where the application is located)= "
              + currentFolder);
      File checkFile = new File(currentFolder.getPath() + fileSeparator
          + TIMESLOTTRACKER_PROPERTIES_FILENAME);
      if (checkFile.exists()) {
        directory = currentFolder.getPath();
      }
    }

    // if even there is no file look inside the user home directory
    if (directory == null) {
      directory = System.getProperty("user.home");
    }
    return directory;
  }

  public void save() {
    getConfigurationFromListeners();

    // saving properties tempfile
    TransactionalFileSaver saver = new TransactionalFileSaver(timeSlotTracker,
        configurationFileName);
    File tempConfFile = saver.begin();
    OutputStream outputfile = null;
    try {
      outputfile = new BufferedOutputStream(new FileOutputStream(tempConfFile));
      properties.store(outputfile, null);
      outputfile.flush();
    } catch (IOException ex) {
      timeSlotTracker.errorLog(ex);
      Object[] msgArgs = { ex.getMessage(), configurationFileName };
      String warningTitle = timeSlotTracker
          .getString("configuration.save.exception.title");
      String warningMsg = timeSlotTracker
          .getString("configuration.save.exception.msg", msgArgs);
      JOptionPane.showMessageDialog(timeSlotTracker.getRootFrame(), warningMsg,
          warningTitle, JOptionPane.ERROR_MESSAGE);
      return;
    } finally {
      if (outputfile != null) {
        try {
          outputfile.close();
        } catch (IOException e) {
        }
      }
    }

    // if saved successful, commit saving
    if (!saver.commit()) {
      String errorMsg = timeSlotTracker.getString(
          "configuration.save.exception.msg",
          new Object[] {
              "error renaming temp properties file to properties file",
              configurationFileName });
      timeSlotTracker.errorLog(errorMsg);
      return;
    }

    Object[] savedArgs = { configurationFileName };
    logger.info(
        timeSlotTracker.getString("configuration.save.debug.saved", savedArgs));
  }

  /**
   * Fires an Action to all listeners to set their configuration values if not
   * done yet.
   *
   * @see Action
   * @see TimeSlotTracker#fireAction(Action)
   */
  private void getConfigurationFromListeners() {
    Action setConfigurationAction = new Action(Action.ACTION_SET_CONFIGURATION,
        this, null);
    timeSlotTracker.fireAction(setConfigurationAction);
  }

  /**
   * Sets a property into configuration
   *
   * @param key
   *          property name
   * @param value
   *          property value
   */
  public void set(String key, String value) {
    Object[] args = { key, value };
    String msg = timeSlotTracker.getString("configuration.set.debug", args);
    logger.fine(msg);
    properties.setProperty(key, value);
  }

  /**
   * Sets a property into configuration
   *
   * @param key
   *          property name
   * @param value
   *          property value
   */
  public void set(String key, Boolean value) {
    set(key, value == null ? null : value.toString());
  }

  /**
   * Sets a property into configuration
   *
   * @param key
   *          property name
   * @param value
   *          property value
   */
  public void set(String key, boolean value) {
    set(key, "" + value);
  }

  /**
   * Gets a string property with a given name. It simply calls a
   * <code>getString</code> method.
   *
   * @see #get(String, String)
   */
  public String get(String key, String defaultValue) {
    return getString(key, defaultValue);
  }

  /**
   * Gets a String property with a given name. When a no property with this name
   * is found it returns the defaultValue
   *
   * @param key
   *          property name
   * @param defaultValue
   *          default value for key if not found. When the value is not found in
   *          configuration file and the <code>defaultValue</code> is not null
   *          the property is then put into configuration file.
   * @return value property value
   */
  public String getString(String key, String defaultValue) {
    String value = properties.getProperty(key);
    if (StringUtils.isBlank(value) && defaultValue != null) {
      // automatically add this value to give the user the ability to change
      // this by hand
      set(key, defaultValue);
      value = defaultValue;
    }
    return value;
  }

  /**
   * Gets a <code>Boolean</code> object with a given name.
   * <p>
   * In the configuration file it's set using the <code>true</code> and
   * <code>false</code> strings.
   *
   * @param key
   *          property name
   * @param defaultValue
   *          default value for key if not found. When the value is not found in
   *          configuration file and the <code>defaultValue</code> is not null
   *          the property is then put into configuration file.
   * @return value property value (Boolean object)
   */
  public Boolean getBoolean(String key, Boolean defaultValue) {
    String stringDefault = null;
    if (defaultValue != null) {
      stringDefault = defaultValue.toString();
    }
    String stringValue = getString(key, stringDefault);
    return Boolean.valueOf(stringValue);
  }

  /**
   * Sets the Integer property <code>value</code> into configuration under
   * <code>key</code>.
   */
  public void set(String key, Integer value) {
    if (value == null) {
      set(key, (String) null);
    } else {
      set(key, value.toString());
    }
  }

  /**
   * Removes value from configuration.
   */
  public void remove(String key) {
    properties.remove(key);
  }

  /**
   * Gets an Integer property with a given name. When a no property with this
   * name is found it returns the defaultValue. When a property was found, but
   * it cannot be converted to requested type the <code>defaultValue</code> also
   * would be returned and such a problem reported.
   *
   * @param key
   *          property name
   * @param defaultValue
   *          default value for key if not found
   * @return value property value
   */
  public Integer getInteger(String key, Integer defaultValue) {
    Integer value = defaultValue;
    String stringValue = getString(key,
        value == null ? null : value.toString());
    try {
      if (stringValue != null) {
        value = new Integer(stringValue);
      }
    } catch (NumberFormatException e) {
      Object[] args = { key, "Integer", defaultValue, e.getMessage() };
      String msg = timeSlotTracker.getString("configuration.get.integer.error",
          args);
      timeSlotTracker.errorLog(msg);
    }
    return value;
  }

  /**
   * Gets an Integer property with a given name. When a no property with this
   * name is found it returns the defaultValue. When a property was found, but
   * it cannot be converted to requested type the <code>defaultValue</code> also
   * would be returned and such a problem reported.
   *
   * @param key
   *          property name
   * @param defaultValue
   *          default value for key if not found
   * @return value property value
   */
  public Integer getInteger(String key, int defaultValue) {
    return getInteger(key, new Integer(defaultValue));
  }

}
