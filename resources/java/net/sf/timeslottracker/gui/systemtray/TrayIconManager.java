package net.sf.timeslottracker.gui.systemtray;

import java.awt.TrayIcon.MessageType;

/**
 * Service for using system tray
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public interface TrayIconManager {

  /**
   * Show message of tray icon
   * 
   * @param message
   *          the message to show
   * @param messageType
   *          the type of message
   */
  public abstract void showMessage(String message, MessageType messageType);

  /**
   * Show message of tray icon
   * 
   * @param title
   *          the title of the message
   * @param message
   *          the message to show
   * @param messageType
   *          the type of message
   */
  public void showMessage(String title, String message, MessageType messageType);

  /**
   * Init service, show tray icon
   */
  public void init();

  /**
   * Informs if the application has support for the tray icon and if the tray
   * icon is enabled.
   * 
   * @return <code>true</code> if the icon is enabled and stays in tray.
   */
  public boolean hasTrayIcon();

  /**
   * Sets tooltip for icon (for example the same what in application title is).
   * 
   * @param tooltip
   *          text to set as a tooltip.
   */
  public void setTooltip(String tooltip);
}