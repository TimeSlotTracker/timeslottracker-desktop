package net.sf.timeslottracker.gui.configuration;

/**
 * String config value
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class StringConfigValue implements ConfigValue {
  private final String name;
  private final String value;

  public StringConfigValue(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public StringConfigValue(String name) {
    this(name, name);
  }

  public String getDescription() {
    return name;
  }

  public String getValue() {
    return value;
  }

  public String toString() {
    return name;
  }
}
