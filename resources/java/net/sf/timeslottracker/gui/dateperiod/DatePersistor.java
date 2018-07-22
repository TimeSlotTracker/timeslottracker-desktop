package net.sf.timeslottracker.gui.dateperiod;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.timeslottracker.utils.StringUtils;

/**
 * Class for convertion date object to/from string
 * 
 * @version File version: $Revision: 1038 $, $Date: 2009-08-04 19:26:06 +0700
 *          (Tue, 04 Aug 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class DatePersistor {
  public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
      "yyyy-MM-dd");

  private DatePersistor() {
  }

  public static Date date(String string) {
    try {
      return DATE_FORMAT.parse(string);
    } catch (ParseException e) {
      return null;
    }
  }

  public static String string(Date date) {
    return date == null ? StringUtils.EMPTY : DATE_FORMAT.format(date);
  }

}