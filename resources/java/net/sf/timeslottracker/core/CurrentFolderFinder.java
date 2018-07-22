package net.sf.timeslottracker.core;

import java.io.File;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Finds folder from the application was called.
 * <p>
 * It's <b>not</b> the directory from "java -jar ..." was called, but the folder
 * the jar file was located. <br/>
 * For example: The file is located in "/tmp/tst.jar", but we start the process
 * from "/home/user" with command: <br/>
 * 
 * <pre>
 * java - jar / tmp / tst.jar
 * </pre>
 * 
 * The static method {@link #getCurrentFolder()} returns the <code>File</code>
 * object with path "/tmp".
 * <p>
 * <b>This helper is NOT tested for unpack archives, just jar files</b>
 * 
 * @author Created by User: zgibek Create date: 2008-01-06 18:50:46
 * @author Last change: $Author: cnitsa $
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 */
public class CurrentFolderFinder {
  // declaring logger
  private static Logger logger = Logger
      .getLogger("net.sf.timeslottracker.core.CurrentFolderFinder");

  private static File starterFolder;

  static {
    ClassLoader classLoader = CurrentFolderFinder.class.getClassLoader();
    logger.info("CurrentFolderFinder.static intializer::classLoader = "
        + classLoader);
    String className = CurrentFolderFinder.class.getName();
    logger.info("CurrentFolderFinder.static intializer::className = "
        + className);
    className = className.replaceAll("\\.", "/");
    className = "/" + className + ".class";
    logger
        .info("CurrentFolderFinder.static intializer::className [after change]= "
            + className);
    URL url = CurrentFolderFinder.class.getResource(className);
    if (url != null) {
      String path = url.toString();
      if (path == null) {
        logger
            .severe("STRANGE: url is not null, but path (url.toString()) is null :(");
        starterFolder = null;
      } else {
        logger.info("CurrentFolderFinder.static intializer::path = " + path);
        /*
         * this should return something like
         * "jar:file:/path/to/jarfile/tst.jar!/net/sf/timeslottracker/core/CurrentFolderFinder.class"
         * We have to cut it and return only "/path/to/jarfile"
         */
        final String JAR_FILE = "jar:file:";
        if (path.startsWith(JAR_FILE)) {
          path = path.substring(JAR_FILE.length());
        }

        // cut path to class inside the jar file
        int endMark = path.indexOf("!");
        if (endMark >= 0) {
          path = path.substring(0, endMark);
        }

        // cut the jar file name
        if (path.endsWith(".jar")) {
          int lastSlash = path.lastIndexOf("/");
          if (lastSlash >= 0) {
            path = path.substring(0, lastSlash);
          }
        }

        logger
            .info("CurrentFolderFinder.static intializer::path [after changes] = "
                + path);
        starterFolder = new File(path);
      }
    } else {
      logger
          .severe("There is no resource with current class. STRANGE, very strange!");
      starterFolder = null;
    }

  }

  /**
   * Returns the folder from the class was loaded (by ClassLoader), so it's
   * should a folder with our app's jar file.
   * 
   * @return <code>File</code> object with path to starting point of our
   *         application.
   */
  public static File getCurrentFolder() {
    return starterFolder;
  }
}
