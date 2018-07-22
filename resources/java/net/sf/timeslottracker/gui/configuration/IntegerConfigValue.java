package net.sf.timeslottracker.gui.configuration;

/**
 * Integer config value
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class IntegerConfigValue implements ConfigValue {
  private final String name;
  private final int value;

  public IntegerConfigValue(String name, int value) {
    this.name = name;
    this.value = value;
  }

  public String getDescription() {
    return name;
  }

  public String getValue() {
    return Integer.toString(value);
  }

  public String toString() {
    return name;
  }
}
