package net.sf.timeslottracker.gui.configuration;

/**
 * Configuration value
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public interface ConfigValue {

  /**
   * @return entry description
   */
  String getDescription();

  /**
   * @return string value
   */
  String getValue();
}
