package net.sf.timeslottracker.utils;

/**
 * Contains utils methods for some specific platforms or to check what the
 * platform is.
 * 
 * @author User: zgibek Date: 2009-07-14 Time: 07:11:53 $Id: not-commited-yet
 *         zgibek Exp $
 */
public class PlatformUtils {

  /**
   * Returns <code>true</code> if the system is Mac OS X.
   * <p/>
   * Can be used to check if some special trick should be used.
   * 
   * @return <code>true</code> if the system is Mac OS X
   */
  public static boolean isMacOsX() {
    return check("mac");
  }

  /**
   * Returns <code>true</code> if the system is *nix OS.
   * <p/>
   * Can be used to check if some special trick should be used.
   * 
   * @return <code>true</code> if the system is *unix OS
   */
  public static boolean isLinux() {
    return check("nux");
  }

  /**
   * Returns <code>true</code> if the system is Windows OS.
   * <p/>
   * Can be used to check if some special trick should be used.
   * 
   * @return <code>true</code> if the system is Windows OS
   */
  public static boolean isWindows() {
    return check("win");
  }

  private static boolean check(String os) {
    final String lcOSName = System.getProperty("os.name").toLowerCase();
    if (lcOSName == null) {
      return false;
    }
    return lcOSName.indexOf(os) >= 0;
  }

}
