package net.sf.timeslottracker.data.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.sf.timeslottracker.core.*;
import net.sf.timeslottracker.data.Attribute;
import net.sf.timeslottracker.data.AttributeType;
import net.sf.timeslottracker.data.DataSource;
import net.sf.timeslottracker.data.DataSourceException;
import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.data.common.AttributeTypeManagerImpl;
import net.sf.timeslottracker.data.common.TransactionalFileSaver;
import net.sf.timeslottracker.data.common.TransactionalFileSaverException;
import net.sf.timeslottracker.gui.configuration.ConfigurationPanel;
import net.sf.timeslottracker.utils.FileUtils;
import net.sf.timeslottracker.utils.StringUtils;
import net.sf.timeslottracker.utils.UniqueNumberSequence;

import org.xml.sax.SAXException;

/**
 * Xml-based representation of DataStore.
 * <p>
 * It is an initial one - we started with xml as a data source, but we want to
 * give an ability to use anything else.
 * <p>
 * For now every <code>save</code> method calls <code>saveAll</code> method
 * because to save anything we have to construct the whole file.
 * 
 * @version File version: $Revision: 1168 $, $Date: 2009-06-16 17:50:01 +0700
 *          (Tue, 16 Jun 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class XmlDataSource implements DataSource {

  private static final String TST_DTD = "timeslottracker.dtd";

  // declaring logger
  private static final Logger logger = Logger
      .getLogger("net.sf.timeslottracker.data.xml");

  /** Stores the filename for file with data */
  private static final String TST_XML_FILENAME = "timeslottracker.xml";

  /**
   * Flag - was error while loading xml file. Default: false (no errors) Use it
   * for avoid save. We can't save xml after bad reading (otherwise xml file
   * will be zero size)
   */
  private boolean dataReadError;

  private final String dtdDirectory = System.getProperty(
      DataSource.DTD_DIRECTORY, null);

  /** Stores initialy read favourites list (from an xml file). */
  private final Collection<Task> favourites = new Vector<Task>();

  /** root task */
  private Task root;

  /** used for unique task id sequence */
  private final UniqueNumberSequence taskIdSequence = new UniqueNumberSequence();

  /**
   * Map of collections. A key in map is parentId. An object stored at this key
   * is a collection of records (tasks) which have this parent id.
   */
  private Map<Task, Collection<Task>> tasks = new Hashtable<Task, Collection<Task>>();

  /**
   * Map to collect tasks ordered with it's id
   */
  private final Map<Object, Task> tasksById = new Hashtable<Object, Task>();

  /** used for unique timeslot id sequence */
  private final UniqueNumberSequence timeslotIdSequence = new UniqueNumberSequence();

  private final String fileSeparator = System.getProperty("file.separator");

  /**
   * Access to main application interface.
   */
  private TimeSlotTracker timeSlotTracker;

  private Configuration configuration;

  private String dataFilePathName;

  private String dtdFilePathName;

  private String dataFileDirectory;

  private String backupDataFileDirectory;

  public void setTimeSlotTracker(TimeSlotTracker timeSlotTracker) {
    this.timeSlotTracker = timeSlotTracker;
    init(timeSlotTracker);
  }

  public void setRoot(Task root) {
    this.root = root;
  }

  private void init(TimeSlotTracker timeSlotTracker) {
    this.configuration = timeSlotTracker.getConfiguration();
    this.dataFileDirectory = getDataFileDirectory();
    this.dataFilePathName = dataFileDirectory + XmlDataSource.TST_XML_FILENAME;
    this.dtdFilePathName = getDTDFileName(dataFileDirectory);
    this.backupDataFileDirectory = getBackupDataFileDirectory(dataFileDirectory);
  }

  @Override
  public Task copyTask(Task sourceTask, Task targetTask, int newNodeIndex,
      boolean deepCopy) {
    return doCopyTask(sourceTask, targetTask, newNodeIndex, deepCopy, null);
  }

  public synchronized Task createTask(Task parentTask, Object id, String name,
      String description, boolean hidden) {
    Integer taskId = getTaskId(id);

    Task newTask = new XmlTask(timeSlotTracker, taskId, name, description,
        hidden);
    moveTask(newTask, parentTask);
    tasksById.put(newTask.getId(), newTask);
    Object[] msgArgs = { newTask, parentTask, taskId };
    String msg = timeSlotTracker.getString(
        "datasource.xml.method.createTask.debug", msgArgs);
    logger.fine(msg);
    return newTask;
  }

  public synchronized TimeSlot createTimeSlot(Task parentTask, Date start,
      Date stop, String description) {
    return createTimeSlot(parentTask, null, start, stop, description);
  }

  public synchronized TimeSlot createTimeSlot(Task parentTask, Object id,
      Date start, Date stop, String description) {
    Locale locale = timeSlotTracker.getLocale();
    Integer timeslotId = getTimeslotId(id);
    TimeSlot timeslot = new XmlTimeSlot(locale, timeslotId, start, stop,
        description);
    if (parentTask != null) {
      parentTask.addTimeslot(timeslot);
    }
    return timeslot;
  }

  @Override
  public void saveAttributeTypes(Collection<AttributeType> records) {
    AttributeTypeManagerImpl.getInstance().update(records);
  }

  public Collection<AttributeType> getAttributeTypes() {
    return AttributeTypeManagerImpl.getInstance().list();
  }

  public Collection<Task> getChildren(Task parent) {
    if (parent == null) {
      return null;
    }

    return tasks.get(parent);
  }

  public ConfigurationPanel getConfigurationPanel() {
    return null;
  }

  public Collection<Task> getFavourites() {
    return favourites;
  }

  public Task getRoot() {
    return root;
  }

  public Task getTask(Object id) {
    if (id == null) {
      return null;
    }
    return tasksById.get(id);
  }

  public synchronized void moveTask(Task task, int newPosition) {
    if (task == null || task.getParentTask() == null) {
      return;
    }
    Task parent = task.getParentTask();
    Vector<Task> children = (Vector<Task>) tasks.get(parent);
    if (children.remove(task)) {
      children.add(newPosition, task);
    }
  }

  public synchronized void moveTask(Task task, Task newParent) {
    Task oldParent = task.getParentTask();
    if (oldParent != null) {
      Collection<Task> children = tasks.get(oldParent);
      if (children == null) {
        Object[] msgArgs = { task, oldParent };
        String msg = timeSlotTracker.getString(
            "datasource.xml.method.moveTask.noChildrenInOldParent", msgArgs);
        timeSlotTracker.errorLog(msg);
        return;
      }

      children.remove(task);
    }

    if (newParent != null) {
      Collection<Task> children = tasks.get(newParent);
      if (children == null) {
        children = new Vector<Task>();
        tasks.put(newParent, children);
      }
      children.add(task);
    }

    task.setParentTask(newParent);

    // debug info
    Object[] msgArgs = { task, oldParent, newParent };
    String msg = timeSlotTracker.getString(
        "datasource.xml.method.moveTask.debug", msgArgs);
    logger.fine(msg);
  }

  public boolean reloadData() {
    try {
      timeSlotTracker.setCursorWait();

      // restore xml file after broken save if need
      XmlSave xmlSave = new XmlSave(this, dataFilePathName, dtdDirectory,
          timeSlotTracker);
      xmlSave.check();

      backup(Configuration.BACKUP_ON_STARTUP);

      Runnable runnable = new Runnable() {
        public void run() {
          Thread.yield();
          SAXParser saxParser;
          try {
            synchronized (XmlDataSource.this) {
              tasks.clear();
              favourites.clear();
              SAXParserFactory saxFactory = SAXParserFactory.newInstance();
              saxFactory.setValidating(true);
              saxParser = saxFactory.newSAXParser();
              XmlParser parser = new XmlParser(timeSlotTracker,
                  XmlDataSource.this);

              // check if a file exists. If not - make an initial copy from jar
              // archive
              File xmlFile = new File(dataFilePathName);
              if (!xmlFile.exists() || xmlFile.length() == 0) {
                copyTemplateSource(xmlFile);
              }

              // always copy dtd because of new version
              copyTemplateSource(new File(dtdFilePathName));

              Object[] loadingArgs = { dataFilePathName };
              String loadingMsg = timeSlotTracker.getString(
                  "datasource.xml.reload.filename", loadingArgs);
              logger.info(loadingMsg);

              saxParser.parse(xmlFile, parser);
              favourites.addAll(parser.getFavourites());
              timeSlotTracker.fireDataLoaded();
            }
          } catch (ParserConfigurationException e) {
            String errorMsg = timeSlotTracker
                .getString("datasource.xml.ParserConfigurationException");
            timeSlotTracker.errorLog(errorMsg);
            timeSlotTracker.errorLog(e);
            tasks = null;
          } catch (IOException e) {
            String errorMsg = timeSlotTracker
                .getString("datasource.xml.IOException");
            timeSlotTracker.errorLog(errorMsg);
            timeSlotTracker.errorLog(e);
            tasks = null;
          } catch (IllegalArgumentException e) {
            String errorMsg = timeSlotTracker
                .getString("datasource.xml.IllegalArgumentException");
            timeSlotTracker.errorLog(errorMsg);
            timeSlotTracker.errorLog(e);
            tasks = null;
          } catch (SAXException e) {
            String errorMsg = timeSlotTracker
                .getString("datasource.xml.SAXException");
            timeSlotTracker.errorLog(errorMsg);
            timeSlotTracker.errorLog(e);
            tasks = null;
          } catch (Throwable e) {
            e.printStackTrace();
            logger.warning(e.toString());
            timeSlotTracker.errorLog(e.toString());
            tasks = null;
          }
        }

      };
      Thread reloadThread = new Thread(runnable);
      reloadThread.start();
      reloadThread.join();
    } catch (InterruptedException ex) {
      logger.warning(ex.toString());
    } finally {
      timeSlotTracker.setCursorDefault();
    }

    dataReadError = (tasks == null);

    return !dataReadError;
  }

  private String getBackupDataFileDirectory(String dataFileDirectory) {
    String backupDirectory = configuration.getString(
        Configuration.BACKUP_DIRECTORY, null);

    if (StringUtils.isBlank(backupDirectory)) {
      backupDirectory = dataFileDirectory;
    }

    if (!backupDirectory.endsWith(fileSeparator)) {
      backupDirectory += fileSeparator;
    }

    // reset backup dataFileDirectory
    configuration.set(Configuration.BACKUP_DIRECTORY, backupDirectory);

    return backupDirectory;
  }

  private String getDTDFileName(String dataFileDirectory) {
    logger.info("Dtd file from dataFileDirectory: " + dtdDirectory);
    if (dtdDirectory == null) {
      return dataFileDirectory + TST_DTD;
    }

    if (!dtdDirectory.endsWith(fileSeparator)) {
      return dtdDirectory + fileSeparator + TST_DTD;
    }

    return dtdDirectory + TST_DTD;
  }

  private String getDataFileDirectory() {
    // first, if the DataSource.TIMESLOTTRACKER_DIRECTORY is defined as
    // environment variable, it is more important
    String folder = System.getProperty(DataSource.TIMESLOTTRACKER_DIRECTORY);

    // if not specified in command line check if data exists in the
    // dataFileDirectory with jar file
    if (StringUtils.isBlank(folder)) {
      File currentFolder = CurrentFolderFinder.getCurrentFolder();
      File checkFile = new File(currentFolder.getPath() + fileSeparator
          + TST_XML_FILENAME);
      if (checkFile.exists()) {
        folder = currentFolder.getPath();
      }
    }

    Boolean useCurrentFolder = configuration.getBoolean(
        Configuration.DATASOURCE_DIRECTORY_CURRENT_FOLDER, Boolean.FALSE);
    // if even there is no file look inside the user home dataFileDirectory
    // (only if not set to use current folder)
    if (StringUtils.isBlank(folder) && !useCurrentFolder) {
      folder = System.getProperty("user.home");
    }

    // finally - if in configuration is said something - use it, whatever we
    // set before (but only if not set to current folder)
    if (!useCurrentFolder) {
      folder = configuration.getString(Configuration.DATASOURCE_DIRECTORY,
          folder);
    }

    if (StringUtils.isBlank(folder)) {
      folder = StringUtils.EMPTY;
    }

    if (!StringUtils.isBlank(folder) && !folder.endsWith(fileSeparator)) {
      folder += fileSeparator;
    }

    logger.info("Setted data directory: " + folder);

    return folder;
  }

  public void save(Task task) {
    saveAll();
  }

  public void save(TimeSlot timeSlot) {
    saveAll();
  }

  @Override
  public boolean saveAll() {
    return saveAll(false);
  }

  @Override
  public synchronized boolean saveAll(boolean popupErrors) {
    if (dataReadError) {
      return false;
    }

    if (root == null) {
      return false;
    }

    backup(Configuration.BACKUP_ON_SHUTDOWN);

    try {
      XmlSave xmlSave = new XmlSave(this, dataFilePathName, dtdDirectory,
          timeSlotTracker);
      xmlSave.saveAll();

			timeSlotTracker.fireAction(new DataSaveAction(this));
      
      return true;
    } catch (DataSourceException e) {
      procesException(e, popupErrors);
      return false;
    } catch (TransactionalFileSaverException e) {
      procesException(e, popupErrors);
      return false;
    }
  }

  private void procesException(TimeSlotTrackerException e, boolean popupErrors) {
    if (timeSlotTracker.isClosing()) {
      return;
    }

		timeSlotTracker.fireAction(new DataSaveAction(this, e.getMessage()));

    if (popupErrors) {
      JFrame frame = timeSlotTracker.getRootFrame();
      String title = timeSlotTracker
          .getString("datasource.xml.XmlSave.Exception.title");
      JOptionPane.showMessageDialog(frame, e.getMessage(), title,
          JOptionPane.ERROR_MESSAGE);
    }

    timeSlotTracker.errorLog(e);
  }

  private void backup(String mode) {
    Boolean makeBackup = configuration.getBoolean(mode, Boolean.TRUE);
    if (makeBackup != null && makeBackup) {
      if (checkIfBackupDirectoryExists(backupDataFileDirectory)) {
        String suffix = mode.equals(Configuration.BACKUP_ON_STARTUP) ? "Startup"
            : "Shutdown";
        Locale locale = timeSlotTracker.getLocale();
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(locale);
        Calendar calendar = Calendar.getInstance(locale);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String dayOfWeekName = dateFormatSymbols.getWeekdays()[dayOfWeek];
        String backupName = "timeslottracker-backupOn" + suffix + "-"
            + dayOfWeekName + ".xml";
        String source = dataFileDirectory + TST_XML_FILENAME;
        if (new File(source).length() != 0) {
          FileUtils.copyFile(source, backupDataFileDirectory + backupName,
              timeSlotTracker);
        }
      }
    }
  }

  /**
   * Checks if given backup dataFileDirectory exists.
   * 
   * @param backupDirectory directory for backup file
   * @return <code>true</code> if this dataFileDirectory exists.
   */
  private boolean checkIfBackupDirectoryExists(String backupDirectory) {
    File dirToCheck = new File(backupDirectory);
    boolean exists = dirToCheck.exists();
    timeSlotTracker.debugLog("Checking if backup dataFileDirectory ["
        + backupDirectory + "] exists...");
    if (!exists) {
      JFrame frame = timeSlotTracker.getRootFrame();
      if (!timeSlotTracker.isClosing()) {
        String title = timeSlotTracker
            .getString("dataSource.backupDirectoryNotExists.title");
        Object[] args = new Object[] { backupDirectory };
        String msg = timeSlotTracker.getString(
            "dataSource.backupDirectoryNotExists.msg", args);
        JOptionPane.showMessageDialog(frame, msg, title,
            JOptionPane.ERROR_MESSAGE);
      }
    }
    return exists;
  }

  /**
   * Copy an xml or dtd file from jar archive into a destination.
   * 
   * @param destinationFile
   *          a File object to which we should copy an timeslottracker.xml
   */
  private void copyTemplateSource(File destinationFile) {
    TransactionalFileSaver saver = new TransactionalFileSaver(timeSlotTracker,
        destinationFile.getAbsolutePath());

    InputStream source = null;
    FileOutputStream destination = null;
    try {
      String filename = "/" + destinationFile.getName();
      logger
          .info("Coping from [" + filename + "] to [" + destinationFile + "]");
      source = XmlDataSource.class.getResourceAsStream(filename);
      destination = new FileOutputStream(saver.begin());
      int readByte = 0;
      while ((readByte = source.read()) > 0) {
        destination.write(readByte);
      }
      destination.close();
      source.close();

      saver.commit();

      Object[] arg = { destinationFile.getName() };
      String msg = timeSlotTracker.getString("datasource.xml.copyFile.copied",
          arg);
      logger.fine(msg);
    } catch (Exception e) {
      Object[] expArgs = { e.getMessage() };
      String expMsg = timeSlotTracker.getString(
          "datasource.xml.copyFile.exception", expArgs);
      timeSlotTracker.errorLog(expMsg);
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
        Object[] expArgs = { e.getMessage() };
        String expMsg = timeSlotTracker.getString(
            "datasource.xml.copyFile.exception", expArgs);
        timeSlotTracker.errorLog(expMsg);
        timeSlotTracker.errorLog(e);
      }
    }
  }

  private Task doCopyTask(Task sourceTask, Task targetTask, int newNodeIndex,
      boolean deepCopy, Object taskIdToSkip) {
    Task parent = (newNodeIndex == -1) ? targetTask : targetTask
        .getParentTask();

    // creating new task
    Task newTask = createTask(parent, null, sourceTask.getName(),
        sourceTask.getDescription(), sourceTask.isHidden());

    // saving new task id to prevent recursive copying
    if (taskIdToSkip == null) {
      taskIdToSkip = newTask.getId();
    }

    // copying attributes
    ArrayList<Attribute> newTaskAttributes = new ArrayList<Attribute>();
    for (Attribute attribute : sourceTask.getAttributes()) {
      newTaskAttributes.add(new Attribute(attribute.getAttributeType(),
          attribute.get()));
    }
    newTask.setAttributes(newTaskAttributes);

    // move task according specified index, if need
    if (newNodeIndex != -1) {
      moveTask(newTask, newNodeIndex);
    }

    // copy children if need
    if (deepCopy && sourceTask.getChildren() != null) {
      for (Task task : sourceTask.getChildren()) {
        if (!task.getId().equals(taskIdToSkip)) {
          doCopyTask(task, newTask, -1, deepCopy, taskIdToSkip);
        }
      }
    }

    return newTask;
  }

  private Integer getId(Object id, UniqueNumberSequence sequence) {
    synchronized (sequence) {
      if (id != null && id instanceof Integer) {
        Integer numberId = (Integer) id;
        sequence.update(numberId);
        return numberId;
      }

      return sequence.getNextId();
    }
  }

  private Integer getTaskId(Object id) {
    return getId(id, taskIdSequence);
  }

  private Integer getTimeslotId(Object id) {
    return getId(id, timeslotIdSequence);
  }

}
