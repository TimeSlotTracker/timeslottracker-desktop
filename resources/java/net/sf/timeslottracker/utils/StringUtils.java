package net.sf.timeslottracker.utils;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Class with static methods for use with Strings.
 * 
 * File version: $Revision: 1038 $, $Date: 2009-05-16 08:53:21 +0700 (Sat, 16 May
 * 2009) $ Last change: $Author: cnitsa $
 */
public final class StringUtils {

  /** Empty string constant */
  public static String EMPTY = "";

  /**
   * Converts LOV (separated with ";") into Collection of Strings.
   * <p>
   * It simply uses the <code>java.util.StringTokenizer</code> class.
   * 
   * @param lov
   *          list of values separated with ";"
   * @return Collection of tokens.
   */
  public static Collection convertStringLOV2Collection(String lov) {
    StringTokenizer tokenizer = new StringTokenizer(lov, ";");
    Collection values = new Vector();
    while (tokenizer.hasMoreTokens()) {
      values.add(tokenizer.nextToken());
    }
    return values;
  }

  /**
   * Trimm string with processing null values
   * 
   * @param value
   *          string
   * @return trimmed value, null if value empty
   */
  public static String trim(String value) {
    if (value == null) {
      return null;
    }

    String trimmedValue = value.trim();
    return trimmedValue.length() == 0 ? null : trimmedValue;
  }

  /**
   * @return true if given string null or blank
   */
  public static boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }

  /**
   * Return number for given string
   * 
   * @return -1 if string is not number, and number if it is
   */
  public static int getNumber(String column) {
    if (isBlank(column)) {
      return -1;
    }

    try {
      return Integer.parseInt(column);
    } catch (NumberFormatException e) {
      return -1;
    }
  }

  /**
   * @return charset for it's name, or UTF-8 if unknown
   */
  public static Charset charset(String charsetName) {
    try {
      return Charset.forName(charsetName);
    } catch (IllegalArgumentException e) {
      return Charset.forName("UTF-8");
    }
  }
}
