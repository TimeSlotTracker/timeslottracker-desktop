package net.sf.timeslottracker.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import net.sf.timeslottracker.core.TimeSlotTracker;

/**
 * File utils
 * 
 * @version File version: $Revision: 1052 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class FileUtils {
  // declaring logger
  private static final Logger LOG = Logger
      .getLogger("net.sf.timeslottracker.data.xml");

  /**
   * Copies one file to another one.
   * <p>
   * If the destination file already exists it is deleted first.
   * 
   * @param source
   *          source file name with path
   * @param destination
   *          destination file name with path
   * @param timeSlotTracker
   */
  public static void copyFile(String source, String destination,
      TimeSlotTracker timeSlotTracker) {
    LOG.info("copying [" + source + "] to [" + destination + "]");
    BufferedInputStream sourceStream = null;
    BufferedOutputStream destStream = null;
    try {
      File destinationFile = new File(destination);
      if (destinationFile.exists()) {
        destinationFile.delete();
      }
      sourceStream = new BufferedInputStream(new FileInputStream(source));
      destStream = new BufferedOutputStream(new FileOutputStream(
          destinationFile));
      int readByte;
      while ((readByte = sourceStream.read()) > 0) {
        destStream.write(readByte);
      }
      Object[] arg = { destinationFile.getName() };
      String msg = timeSlotTracker.getString("datasource.xml.copyFile.copied",
          arg);
      LOG.fine(msg);
    } catch (Exception e) {
      Object[] expArgs = { e.getMessage() };
      String expMsg = timeSlotTracker.getString(
          "datasource.xml.copyFile.exception", expArgs);
      timeSlotTracker.errorLog(expMsg);
      timeSlotTracker.errorLog(e);
    } finally {
      try {
        if (destStream != null) {
          destStream.close();
        }
        if (sourceStream != null) {
          sourceStream.close();
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

  /**
   * Read last line from given file
   * 
   * @param file
   *          file to read
   * @return last file line, null if file error
   */
  public static String readLastLine(File file) {
    java.io.RandomAccessFile fileHandler = null;
    try {
      fileHandler = new java.io.RandomAccessFile(file, "r");
      long fileLength = file.length() - 1;
      StringBuilder sb = new StringBuilder();

      for (long filePointer = fileLength; filePointer != -1; filePointer--) {
        fileHandler.seek(filePointer);
        int readByte = fileHandler.readByte();

        if (readByte == 0xA) {
          if (filePointer == fileLength) {
            continue;
          }
          break;
        } else if (readByte == 0xD) {
          if (filePointer == fileLength - 1) {
            continue;
          }
          break;
        }

        sb.append((char) readByte);
      }

      String lastLine = sb.reverse().toString();
      return lastLine;
    } catch (java.io.FileNotFoundException e) {
      LOG.warning(e.getMessage());
      return null;
    } catch (java.io.IOException e) {
      LOG.warning(e.getMessage());
      return null;
    } finally {
      if (fileHandler != null) {
        try {
          fileHandler.close();
        } catch (IOException e) {
        }
      }
    }
  }
}
