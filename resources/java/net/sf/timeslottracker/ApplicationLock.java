package net.sf.timeslottracker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements application lock. It allows only one instance of application.
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class ApplicationLock {

  private static final Logger LOG = Logger.getLogger(ApplicationLock.class
      .getName());

  private final File file;

  private FileLock lock;

  private RandomAccessFile randomAccessFile;

  private static final String TIMESLOTTRACKER_LOCK_FILENAME = "timeslottracker.lock";

  public ApplicationLock() {
    String filename = System.getProperty("java.io.tmpdir")
        + System.getProperty("file.separator") + TIMESLOTTRACKER_LOCK_FILENAME;
    LOG.info("Checking lock file at: " + filename);
    this.file = new File(filename);
  }

  /**
   * Try to receive lock
   * 
   * @return true - lock received, false - otherwise
   */
  public boolean tryLock() {
    try {
      randomAccessFile = new RandomAccessFile(file, "rw");
      FileChannel channel = randomAccessFile.getChannel();

      try {
        lock = channel.tryLock();
      } catch (OverlappingFileLockException e) {
        // File is already locked in this thread or virtual machine
        return false;
      }

      if (lock == null) {
        return false;
      }

    } catch (FileNotFoundException e) {
      LOG.warning(e.getMessage());
    } catch (IOException e) {
      LOG.log(Level.SEVERE, e.getMessage(), e);
    }

    return true;
  }

  /**
   * Release lock
   */
  public void releaseLock() {
    if (lock != null) {
      try {
        lock.release();
        randomAccessFile.close();
        file.delete();

      } catch (IOException e) {
        LOG.log(Level.SEVERE, e.getMessage(), e);
      }
    }
  }
}