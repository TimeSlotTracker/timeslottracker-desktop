package net.sf.timeslottracker.gui.systemtray;

import java.awt.TrayIcon;
import java.util.MissingResourceException;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Logger;

import net.sf.timeslottracker.core.Action;
import net.sf.timeslottracker.core.ActionListener;
import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.core.TimeSlotTracker;
import net.sf.timeslottracker.core.TimeoutTimer;

/**
 * Enables the Tip of the day service - shows the user some extra informations
 * via tray icon.
 * <p/>
 * The tray icon must be supported and enabled in application.
 * <p/>
 * The tips are located in <code>TimeSlotTracker_xx.properties</code> file, so
 * they can be localized. This class gets one by one tip until there is no tip
 * with given number. <br/>
 * The prefix is {@link #TIP_OF_THE_DAY_PREFIX} and then "tip_number".
 * 
 * @author User: zgibek Date: 2008-10-30 Time: 13:10:10 $Id:
 *         TipOfTheDayService.java 800 2009-05-16 01:53:21Z cnitsa $
 */
public class TipOfTheDayService {
  private static final Logger logger = Logger
      .getLogger("net.sf.timeslottracker.gui.systemtray");

  private ResourceBundle localization;
  private Vector<String> tips;

  private String tipOfTheDayTitle;
  private static final String TIP_OF_THE_DAY_PREFIX = "trayIcon.title.tipOfTheDay.";

  private ShowTipTask showTipTask;
  private TimeoutTimer reminderTimer;
  private final Configuration configuration;
  private final TimeSlotTracker timeSlotTracker;

  public TipOfTheDayService(TimeSlotTracker timeSlotTracker) {
    this.timeSlotTracker = timeSlotTracker;
    tips = new Vector<String>();

    configuration = timeSlotTracker.getConfiguration();
    final Boolean showTips = configuration.getBoolean(
        Configuration.TIP_OF_THE_DAY_ENABLED, Boolean.TRUE);
    if (!showTips) {
      return; // nothing to do
    }

    localization = ResourceBundle.getBundle("tipOfTheDay",
        timeSlotTracker.getLocale());
    int tipNo = 0;
    String tip;
    // noinspection NestedAssignment
    while ((tip = getString(TIP_OF_THE_DAY_PREFIX + "tips." + tipNo)) != null) {
      tips.add(tip);
      tipNo++;
    }
    logger.info("Read " + tipNo + " tips of the day");
    tipOfTheDayTitle = getString(TIP_OF_THE_DAY_PREFIX + "title");

    Integer delayMinutes = configuration.getInteger(
        Configuration.TIP_OF_THE_DAY_MINUTES_REPEAT, 10);
    // noinspection UnnecessaryBoxing,UnnecessaryUnboxing
    delayMinutes = Integer.valueOf(delayMinutes.intValue() * 60);

    // install new reminder and show first message after 10 seconds.
    showTipTask = new ShowTipTask(timeSlotTracker);
    reminderTimer = new TimeoutTimer(timeSlotTracker, "tipOfTheDayTimer",
        showTipTask, delayMinutes, tips.size() - 1);

    // be carefull on configuration change
    ActionListener configurationChangeListener = new ConfigurationChangeListener();
    timeSlotTracker.addActionListener(configurationChangeListener,
        Action.ACTION_CONFIGURATION_CHANGED);
  }

  public String getString(String key) {
    try {
      if (localization != null) {
        return localization.getString(key);
      } else {
        logger
            .warning("TipOfTheDayService#getString():Localizator object is null!");
      }
    } catch (MissingResourceException e) {
      String message = "Cannot find a property for key: " + key;
      logger.warning(message);
    }
    return null;
  }

  /**
   * Shows randomly selected tip of the day.
   */
  private class ShowTipTask implements ActionListener {
    private TrayIconManager trayIconManager;
    private Vector<String> unusedTips;
    private Random randomGenerator;

    public ShowTipTask(TimeSlotTracker timeSlotTracker) {
      trayIconManager = timeSlotTracker.getTrayIconService();
      unusedTips = new Vector<String>(tips.size());
      randomGenerator = new Random();
    }

    private String getRandomTip() {
      if (unusedTips.isEmpty()) {
        unusedTips.addAll(tips);
      }
      final int howMany = unusedTips.size();
      final int random = randomGenerator.nextInt(howMany);
      final String tip = unusedTips.get(random);
      unusedTips.remove(random);
      return tip;
    }

    /**
     * Shows randomly selected tip in tray Icon
     * 
     * @param action
     *          action - not used.
     */
    public void actionPerformed(Action action) {
      trayIconManager.showMessage(tipOfTheDayTitle, getRandomTip(),
          TrayIcon.MessageType.INFO);
    }
  }

  /**
   * Listens to configuration change to update the delay timeout as well as the
   * enable option of tip.
   */
  private class ConfigurationChangeListener implements ActionListener {
    public void actionPerformed(Action action) {
      final Boolean showTips = configuration.getBoolean(
          Configuration.TIP_OF_THE_DAY_ENABLED, Boolean.TRUE);
      Integer delayMinutes = configuration.getInteger(
          Configuration.TIP_OF_THE_DAY_MINUTES_REPEAT, 10);
      // noinspection UnnecessaryBoxing,UnnecessaryUnboxing
      delayMinutes = Integer.valueOf(delayMinutes.intValue() * 60);

      // install new reminder and show first message after 10 seconds.
      if (reminderTimer != null) {
        reminderTimer.stop();
        reminderTimer = null;
      }
      if (showTips) {
        reminderTimer = new TimeoutTimer(timeSlotTracker, "tipOfTheDayTimer",
            showTipTask, delayMinutes, tips.size() - 1);
      }
    }
  }
}
