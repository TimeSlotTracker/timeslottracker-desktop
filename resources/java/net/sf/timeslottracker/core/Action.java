package net.sf.timeslottracker.core;

/**
 * An action which is fired every time some action occurs.
 * 
 * File version: $Revision: 1043 $, $Date: 2009-05-16 08:53:21 +0700 (Sat, 16 May
 * 2009) $ Last change: $Author: cnitsa $
 */
public class Action {

  /** listen configuration changes **/
  public static final String ACTION_CONFIGURATION_CHANGED = "configurationChanged";

  /**
   * listener HAS TO save its configuration values in <code>source</code> object
   **/
  public static final String ACTION_SET_CONFIGURATION = "setConfigurationValues";

  /**
   * A name of a Action
   */
  private String name;

  /**
   * Source object, where this action was fired
   */
  private Object source;

  /**
   * Some extra parameter (if needed) passed with this action
   */
  private Object param;

  /**
   * Default construction for this action.
   * 
   * @param name
   *          a name for a task
   * @param source
   *          a source where the action occurs
   * @param param
   *          an extra parameter passed to this action
   */
  public Action(String name, Object source, Object param) {
    this.name = name;
    this.source = source;
    this.param = param;
  }

  /**
   * Returns action's name
   */
  public String getName() {
    return name;
  }

  /**
   * Returns action's source.
   */
  public Object getSource() {
    return source;
  }

  /**
   * Returns action's param object.
   */
  public Object getParam() {
    return param;
  }

  /**
   * Formats a string for easy displaying.
   */
    @Override
  public String toString() {
    return "Action." + name + ":" + source;
  }

}
