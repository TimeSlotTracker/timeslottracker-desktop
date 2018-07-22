package net.sf.timeslottracker.gui.systemtray;

import java.awt.AWTException;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.sf.timeslottracker.core.Action;
import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.core.TimeSlotTracker;
import net.sf.timeslottracker.data.DataLoadedListener;
import net.sf.timeslottracker.data.TaskChangedListener;
import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.data.TimeSlotChangedListener;
import net.sf.timeslottracker.gui.layouts.classic.today.TodayAction;
import net.sf.timeslottracker.utils.PlatformUtils;

/**
 * Implementation of service for using system tray
 * 
 * @version File version: $Revision: 1176 $, $Date: 2010-06-26 21:46:06 +0700
 *          (Sat, 26 Jun 2010) $
 * @author Last change: $Author: cnitsa $
 */
public class TrayIconManagerImp implements TrayIconManager {

  /**
   * Fired when the balloon is shown and user clicks on it. <br/>
   * Fired also when double clicked on tray icon.
   */
  private static class BallonActionListener implements ActionListener {
    private final TimeSlotTracker timeSlotTracker;

    private BallonActionListener(TimeSlotTracker timeSlotTracker) {
      this.timeSlotTracker = timeSlotTracker;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      // System.out.println("test ActionEvent: "+e);
    }
  }

  /**
   * Shows/hides the main TimeSlotTracker window.
   * <p/>
   * Listens for clicking with left mouse button.
   */
  private class MouseAction extends MouseAdapter {

    MouseAction() {
    }

    @Override
    public void mousePressed(MouseEvent e) {
      if (e.getClickCount() != 1) { // only one click allowed
        return;
      }
      if (e.getButton() != 1) { // only left button allowed
        return;
      }

      int showHideMask = InputEvent.BUTTON1_MASK | InputEvent.CTRL_MASK;
      int changeActiveTaskMask = InputEvent.BUTTON1_MASK;

      if (macStyleShortcuts()) {
        showHideMask = InputEvent.BUTTON1_MASK | InputEvent.CTRL_MASK;
        changeActiveTaskMask = InputEvent.BUTTON1_MASK | InputEvent.CTRL_MASK
            | InputEvent.SHIFT_MASK;
      }

      // maybe make new timeslot
      if (e.getModifiers() == changeActiveTaskMask) {
        changeActiveTask();
        e.consume();
      } else if (e.getModifiers() == showHideMask) { // show/hide action?
        showHideMainWindow();
        e.consume();
      }
    }
  }

  private final Configuration configuration;

  private final TimeSlotTracker timeSlotTracker;

  private final TrayIcon trayIcon;

  public TrayIconManagerImp(TimeSlotTracker timeSlotTracker) {
    this.timeSlotTracker = timeSlotTracker; // set only at first line
    this.configuration = timeSlotTracker.getConfiguration();
    this.trayIcon = createTrayIcon(timeSlotTracker);

    // updates when timeslot changed
    timeSlotTracker.getLayoutManager().addActionListener(
        new TimeSlotChangedListener() {
          @Override
          public void actionPerformed(Action action) {
            doUpdate((TimeSlot) action.getParam());
          }
        });

    // updates when task changed
    timeSlotTracker.addActionListener(new TaskChangedListener() {
      @Override
      public void actionPerformed(Action action) {
        doUpdate();
      }
    });
  
    // updates when data loaded
    timeSlotTracker.addActionListener(new DataLoadedListener() {
      public void actionPerformed(Action action) {
        doUpdate();
      }
    });

    // updates when configuration changed
    timeSlotTracker.addActionListener(new net.sf.timeslottracker.core.ActionListener() {
      public void actionPerformed(Action action) {
        doUpdate();
      }
    }, Action.ACTION_CONFIGURATION_CHANGED);
  }

  @Override
  public boolean hasTrayIcon() {
    return trayIcon != null;
  }

  @Override
  public void init() {
    if (hasTrayIcon()) {
      try {
        SystemTray.getSystemTray().add(trayIcon);
      } catch (AWTException e) {
        timeSlotTracker.errorLog(e);
      }
    }
  }

  @Override
  public void setTooltip(final String tooltip) {
    if (hasTrayIcon()) {
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          trayIcon.setToolTip(tooltip);
        }
      });
    }
  }

  @Override
  public void showMessage(final String message, final MessageType messageType) {
    showMessage("TimeSlotTracker", message, messageType);
  }

  @Override
  public void showMessage(final String title, final String message,
      final MessageType messageType) {
    if (hasTrayIcon()) {
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          trayIcon.displayMessage(title, message, messageType);
        }
      });
    }
  }

  private void changeActiveTask() {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        timeSlotTracker.startTiming();
      }
    });
  }

  private TrayIcon createTrayIcon(final TimeSlotTracker timeSlotTracker) {
    if (!isEnabled()) {
      return null;
    }

    TrayIcon trayIcon = new TrayIcon(
        getIcon(timeSlotTracker.getActiveTimeSlot()));
    trayIcon.setImageAutoSize(true);
    trayIcon.addActionListener(new BallonActionListener(timeSlotTracker));
    trayIcon.addMouseListener(new MouseAction());

    PopupMenu popup = new PopupMenu();
    String showHideShortcut = "Ctrl + Click";
    String changeTaskShortcut = "Click";
    if (macStyleShortcuts()) {
      showHideShortcut = "Ctrl + Click";
      changeTaskShortcut = "Shift + Ctrl + Click";
    }
    MenuItem changeActiveTaskItrem = new MenuItem(
        timeSlotTracker.getString("trayIcon.menu.changetask") + " ("
            + changeTaskShortcut + ")");
    changeActiveTaskItrem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        changeActiveTask();
      }
    });
    popup.add(changeActiveTaskItrem);

    MenuItem showDailyItem = new MenuItem(
        timeSlotTracker.getString("trayIcon.menu.today"));
    showDailyItem.addActionListener(new TodayAction(timeSlotTracker
        .getLayoutManager(), timeSlotTracker.getString("trayIcon.menu.today")));
    popup.add(showDailyItem);

    MenuItem hideshowItem = new MenuItem(
        timeSlotTracker.getString("trayIcon.menu.hideshow.appwindow") + " ("
            + showHideShortcut + ")");
    hideshowItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        showHideMainWindow();
      }
    });
    popup.add(hideshowItem);
    popup.addSeparator();

    MenuItem quitItem = new MenuItem(
        timeSlotTracker.getString("trayIcon.menu.exit"));
    quitItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        timeSlotTracker.quit();
      }
    });
    popup.add(quitItem);

    trayIcon.setPopupMenu(popup);
    return trayIcon;
  }

  private void doUpdate(final TimeSlot timeSlot) {
    if (!isEnabled() || !hasTrayIcon()) {
      return;
    }

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        trayIcon.setImage(getIcon(timeSlot));
      }
    });
  }

  private void doUpdate() {
    doUpdate(TrayIconManagerImp.this.timeSlotTracker.getActiveTimeSlot());
  }

  private Image getIcon(TimeSlot timeSlot) {
    return timeSlotTracker.getIcon(getIconName(timeSlot)).getImage();
  }

	private String getIconName(TimeSlot timeSlot) {
		boolean isMac = configuration != null && configuration.getBoolean(
				Configuration.CUSTOM_USE_MAC_SYSTEM_TRAY_ICONS, false);
    String iconName = "tray.icon";
		if (timeSlot != null) {
			if (timeSlot.isActive()) {
				iconName = "tray.icon.active";
			} else if (timeSlot.isPaused()) {
				iconName = "tray.icon.paused";
			}
		}

    if (isMac) {
      iconName += ".mac";
    }

    return iconName;
	}

  private boolean isEnabled() {
    return SystemTray.isSupported()
        && timeSlotTracker.getConfiguration().getBoolean(
            Configuration.TRAY_ICON_ENABLED, true);
  }

  private boolean macStyleShortcuts() {
    final Boolean configurationMacStyleShortcuts = configuration.getBoolean(
        Configuration.TRAY_ICON_MAC_SHORTCUTS, Boolean.FALSE);
    return (PlatformUtils.isMacOsX() || configurationMacStyleShortcuts);
  }

  private void showHideMainWindow() {
    final Boolean minimizeInTray = configuration.getBoolean(
        Configuration.TRAY_ICON_MINIMIZE, true);

    final JFrame frame = timeSlotTracker.getRootFrame();
    final boolean wasVisible = frame.isVisible();
    
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        if (hasTrayIcon() && minimizeInTray) {
          frame.setVisible(!wasVisible);

          if (!wasVisible) {
            frame.setState(Frame.NORMAL);
            frame.toFront();
          }
        } else {
          if (!wasVisible) {
            frame.setVisible(true);
          }

          int wasState = frame.getState();
          frame.setState(wasState == Frame.ICONIFIED ? Frame.NORMAL
              : Frame.ICONIFIED);
        }

        if (!wasVisible) {
          frame.toFront();
        }
      }
    });

  }

}
