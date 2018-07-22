package net.sf.timeslottracker.utils;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.IOException;
import java.net.URI;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import net.sf.timeslottracker.core.ConfigurationHelper;
import net.sf.timeslottracker.core.TimeSlotTracker;
import net.sf.timeslottracker.filters.TreeNodeFilter;

/**
 * Useful swing utilities.
 * 
 * @version File version: $Revision: 1038 $, $Date: 2009-08-23 17:55:36 +0700
 *          (Sun, 23 Aug 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class SwingUtils {

  private static final Logger LOG = Logger
      .getLogger(SwingUtils.class.getName());

  /**
   * Browse given url
   * 
   * @param issueUrl
   *          url to browse
   */
  public static void browse(URI issueUrl) {
    if (Desktop.isDesktopSupported()
        && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
      try {
        Desktop.getDesktop().browse(issueUrl);
      } catch (IOException e) {
        LOG.throwing(SwingUtils.class.getName(), "open url", e);
      }
    }
  }

  /**
   * Gets parent for selected component
   * 
   * @param component
   *          selected component
   * @return dialog or frame, maybe null if not found
   */
  public static Component getParent(Component component) {
    if (component == null) {
      return null;
    }

    if (component instanceof Dialog || component instanceof Frame) {
      return component;
    }

    return getParent(component.getParent());
  }

  /**
   * Save location for selected window in configuration
   */
  public static void saveLocation(Window window) {
    Point location = window.getLocation();
    ConfigurationHelper.setProperty(window, "x", location.x);
    ConfigurationHelper.setProperty(window, "y", location.y);
  }

  /**
   * Save width and height for selected window in configuration
   */
  public static void saveWidthHeight(Window window) {
    ConfigurationHelper.setProperty(window, "width", window.getWidth());
    ConfigurationHelper.setProperty(window, "height", window.getHeight());
  }

  /**
   * This method takes the root node and node filter to find node which returns
   * accept = true. After that processing complete.
   * 
   * @param rootNode
   *          root node of tree
   * @param filter
   *          tree node filter
   * 
   * @return node or null if not found
   */
  public static TreeNode searchNode(TreeNode rootNode, TreeNodeFilter filter) {
    DefaultMutableTreeNode node = null;

    // Get the enumeration
    Enumeration enum_ = ((DefaultMutableTreeNode) rootNode)
        .preorderEnumeration();

    // iterate through the enumeration
    int i = 0;
    while (enum_.hasMoreElements()) {
      i += 1;

      // get the node
      node = (DefaultMutableTreeNode) enum_.nextElement();

      // check the filter
      if (filter.accept(node)) {
        return node;
      }
    }

    // tree node with string node found return null
    return null;
  }

  /**
   * Sets location from configuration for selected window
   */
  public static void setLocation(Window window) {
    Integer x = ConfigurationHelper.getInteger(window, "x", null);
    Integer y = ConfigurationHelper.getInteger(window, "y", null);

    // reset if too big for display
    Dimension screenSize = getScreenSize();
    if ((x != null && y != null)
        && (x >= screenSize.getWidth() - 50 || y >= screenSize.getHeight() - 50)) {
      x = null;
      y = null;
    }

    if (x != null && y != null) {
      window.setLocation(x.intValue(), y.intValue());
    } else {
      Component parent = null;
      if (window instanceof JFrame) {
        parent = ((JFrame) window).getRootPane();
      } else if (window instanceof JDialog) {
        parent = ((JDialog) window).getRootPane();
      }
      window.setLocationRelativeTo(parent);
    }
  }

  /**
   * Sets width and height from configuration for selected window
   */
  public static void setWidthHeight(Window window, int defaultWidth,
      int defaultHeight) {
    Integer width = ConfigurationHelper.getInteger(window, "width",
        defaultWidth);
    Integer height = ConfigurationHelper.getInteger(window, "height",
        defaultHeight);

    window.setSize(width, height);
  }

  /**
   * Update LookAndFeel from selected class name
   * 
   * @param className
   *          LookAndFeel class name
   * @param timeSlotTracker
   *          reference to TimeSlotTracker object
   * @param needRepaint
   *          need repaint
   */
  public static void updateLookAndFeel(String className,
      TimeSlotTracker timeSlotTracker, boolean needRepaint) {
    if (className != null
        && !className.trim().equalsIgnoreCase(StringUtils.EMPTY)) {
      try {
        UIManager.setLookAndFeel(className);
      } catch (Exception e) {
        SwingUtils.LOG.log(Level.SEVERE, e.toString(), e);
      }

      SwingUtilities.updateComponentTreeUI(timeSlotTracker.getRootFrame());
    }
  }

  private static Dimension getScreenSize() {
    Toolkit t = Toolkit.getDefaultToolkit();
    return t.getScreenSize();
  }

  /**
   * Determines the preferred size of the JToolBar based on the sum of its
   * components.
   * 
   * @param toolbar
   * @return Dimension
   */
  public static Dimension determinePreferredSize(JToolBar toolbar) {
    int height = 0;
    int width = 0;
    for (int i = 0, len = toolbar.getComponentCount(); i < len; i++) {
      Dimension d = toolbar.getComponentAtIndex(i).getPreferredSize();
      width += d.width;
      height = Math.max(height, d.height);
    }
    return new Dimension(width, height);
  }

}
