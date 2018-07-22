package net.sf.timeslottracker.data.common;

import java.io.File;
import java.text.MessageFormat;

import net.sf.timeslottracker.core.TimeSlotTracker;
import net.sf.timeslottracker.utils.FileUtils;

/**
 * Class implements transactional saving file's data
 * <p>
 * Using:
 * <ol>
 * <li>create class object with desired file name
 * <li>get file using {@link #begin()} method
 * <li>write data to this file (rewrite it)
 * <li>invoke {@link #commit()} method
 * <li>create class object and invoke {@link #check()} while system starting
 * cycle (before using mentioned file)
 * </ol>
 * 
 * <p>
 * Class uses temp file for saving data
 * 
 * @version File version: $Revision: 1051 $, $Date: 2009-08-06 09:55:17 +0700
 *          (Thu, 06 Aug 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class TransactionalFileSaver {
  /** file name for tempfile */
  private static final MessageFormat tempFileName = new MessageFormat(
      "{0}.temp");

  private final TimeSlotTracker timeSlotTracker;

  private final File sourceFile;
  private final File tempFile;

  private final String lastLineMarker;

  /**
   * Create transactional saver
   * 
   * @param fileName
   *          file's name for transactional save
   */
  public TransactionalFileSaver(TimeSlotTracker timeSlotTracker, String fileName) {
    this(timeSlotTracker, fileName, null);
  }

  /**
   * Create transactional saver
   * 
   * @param fileName
   *          file's name for transactional save
   * @param lastLineMarker
   *          string marker (last line of file) used as all data written
   *          insurance, maybe null - will no any marker checks
   */
  public TransactionalFileSaver(TimeSlotTracker timeSlotTracker,
      String fileName, String lastLineMarker) {
    this.timeSlotTracker = timeSlotTracker;
    this.sourceFile = new File(fileName);
    this.tempFile = new File(tempFileName.format(new Object[] { fileName }));
    this.lastLineMarker = lastLineMarker;
  }

  /**
   * Returns file to save data
   * 
   * @return file temp file to save data
   */
  public File begin() {
    return tempFile;
  }

  /**
   * Checks for correctinal completion {@link #commit()} operation
   */
  public void check() {
    if (!tempFile.exists()) {
      return;
    }

    if (tempFile.length() == 0) {
      tempFile.delete();
      return;
    }

    if (lastLineMarker != null && !isValidMarker()) {
      tempFile.delete();
      return;
    }

    if (!sourceFile.exists()) {
      tempFile.renameTo(sourceFile);
      return;
    }

    if (sourceFile.lastModified() < tempFile.lastModified()) {
      sourceFile.delete();
      tempFile.renameTo(sourceFile);
      return;
    }

    tempFile.delete();
  }

  /**
   * Commit transaction
   * 
   * @throws TransactionalFileSaverException
   *           error occurred while saving
   */
  public void commitWithException() throws TransactionalFileSaverException {
    if (tempFile.length() == 0) {
      throw new TransactionalFileSaverException(timeSlotTracker,
          "datasource.xml.XmlSave.Exception",
          new Object[] { timeSlotTracker
              .getString("datasource.xml.XmlSave.Exception.2") });
    }

    if (lastLineMarker != null && !isValidMarker()) {
      throw new TransactionalFileSaverException(timeSlotTracker,
          "datasource.xml.XmlSave.Exception",
          new Object[] { timeSlotTracker
              .getString("datasource.xml.XmlSave.Exception.3") });
    }

    if (!sourceFile.delete()) {
      throw new TransactionalFileSaverException(timeSlotTracker,
          "datasource.xml.XmlSave.Exception",
          new Object[] { timeSlotTracker
              .getString("datasource.xml.XmlSave.Exception.1") });
    }

    boolean renameTempToSource = tempFile.renameTo(sourceFile);
    if (!renameTempToSource) {
      throw new TransactionalFileSaverException(timeSlotTracker,
          "datasource.xml.XmlSave.Exception",
          new Object[] { timeSlotTracker
              .getString("datasource.xml.XmlSave.Exception.1") });
    }
  }

  /**
   * Commit transaction
   * 
   * @return true - successful commit, false - overwise
   */
  public boolean commit() {
    sourceFile.delete();
    return tempFile.renameTo(sourceFile);
  }

  private boolean isValidMarker() {
    String lastLine = FileUtils.readLastLine(tempFile);
    return lastLine != null && lastLine.trim().equals(lastLineMarker.trim());
  }

}
