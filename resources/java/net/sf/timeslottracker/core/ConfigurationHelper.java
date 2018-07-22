package net.sf.timeslottracker.core;

import net.sf.timeslottracker.Starter;

/**
 * Contains common methods to store and get configuration for specific class
 * (i.e. windows as well as other objects).
 * 
 * @author Created by User: zgibek Create date: 2008-01-07 07:54:47
 * @author Last change: $Author: cnitsa $
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 */
public class ConfigurationHelper {

  public static final String CONFIGURATION_PREFIX = "configuration.classProperty.";

  private static Configuration getConfiguration() {
    TimeSlotTracker tst = Starter.getTimeSlotTracker();
    assert tst != null;
    if (tst == null) {
      return null;
    }
    Configuration configuration = tst.getConfiguration();
    return configuration;
  }

  private static String createPropertyName(Object object, String property) {
    return CONFIGURATION_PREFIX + object.getClass().getName() + "." + property;
  }

  /**
   * Stores in configuration property for given class.
   * 
   * @param object
   *          object for which configuration have to be saved
   * @param property
   *          property name (will be prefixed with class name as well as static
   *          prefix)
   * @param value
   *          value to store.
   */
  public static void setProperty(Object object, String property, String value) {
    if (getConfiguration() != null) {
      getConfiguration().set(createPropertyName(object, property), value);
    }
  }

  /**
   * Stores in configuration property for given class.
   * 
   * @param object
   *          object for which configuration have to be saved
   * @param property
   *          property name (will be prefixed with class name as well as static
   *          prefix)
   * @param value
   *          value to store.
   */
  public static void setProperty(Object object, String property, Integer value) {
    if (getConfiguration() != null) {
      getConfiguration().set(createPropertyName(object, property), value);
    }
  }

  /**
   * Stores in configuration property for given class.
   * 
   * @param object
   *          object for which configuration have to be saved
   * @param property
   *          property name (will be prefixed with class name as well as static
   *          prefix)
   * @param value
   *          value to store.
   */
  public static void setProperty(Object object, String property, Boolean value) {
    if (getConfiguration() != null) {
      getConfiguration().set(createPropertyName(object, property), value);
    }
  }

  public static String getString(Object object, String property,
      String defaultValue) {
    if (getConfiguration() == null) {
      return defaultValue;
    }
    return getConfiguration().getString(createPropertyName(object, property),
        defaultValue);
  }

  public static Integer getInteger(Object object, String property,
      Integer defaultValue) {
    if (getConfiguration() == null) {
      return defaultValue;
    }
    return getConfiguration().getInteger(createPropertyName(object, property),
        defaultValue);
  }

  public static Boolean getBoolean(Object object, String property,
      Boolean defaultValue) {
    if (getConfiguration() == null) {
      return defaultValue;
    }
    return getConfiguration().getBoolean(createPropertyName(object, property),
        defaultValue);
  }

}
