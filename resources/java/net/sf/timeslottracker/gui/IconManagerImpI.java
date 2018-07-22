package net.sf.timeslottracker.gui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.ImageIcon;

/**
 * 
 * Implementation of icon manager
 * 
 * @version File version: $Revision: 1.23 $, $Date: 2007/04/07 03:59:30 $
 * @author Last change: $Author: cnitsa $
 */
public class IconManagerImpI {

  private final Properties properties;
  private final Map<String, ImageIcon> name2icon = new HashMap<String, ImageIcon>();

  public IconManagerImpI() throws IOException {
    properties = new Properties();
    properties.load(IconManagerImpI.class
        .getResourceAsStream("/icons.properties"));
  }

  /**
   * Return icon object for given icon name
   * 
   * @param iconName
   *          name of icon (see icons.properties)
   * @return icon
   */
  public ImageIcon getIcon(String iconName) {
    // LOG.finest("Getting ImageIcon: " + iconPath);
    if (name2icon.containsKey(iconName)) {
      return name2icon.get(iconName);
    }

    String iconPath = (String) properties.get(iconName);

    java.net.URL imageURL = IconManagerImpI.class.getResource(iconPath.trim());
    if (imageURL == null) {
      Object[] args = { iconPath };
      // String errorMessage = getString("starter.iconNotFound", args);
      // errorLog(errorMessage);
      return null;
    }
    ImageIcon imageIcon = new ImageIcon(imageURL);
    name2icon.put(iconName, imageIcon);

    return imageIcon;
  }

}
