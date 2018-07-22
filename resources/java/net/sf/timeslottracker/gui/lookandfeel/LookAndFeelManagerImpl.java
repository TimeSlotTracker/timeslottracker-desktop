package net.sf.timeslottracker.gui.lookandfeel;

import javax.swing.SwingUtilities;

import net.sf.timeslottracker.core.Action;
import net.sf.timeslottracker.core.ActionListener;
import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.core.TimeSlotTracker;
import net.sf.timeslottracker.utils.SwingUtils;

/**
 * Class that manages look and feels for application
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class LookAndFeelManagerImpl {

  private static final String DEFAULT_LOOK_AND_FEEL_CLASS = "com.jgoodies.looks.plastic.PlasticLookAndFeel";

  private final TimeSlotTracker timeSlotTracker;

  public LookAndFeelManagerImpl(TimeSlotTracker timeSlotTracker) {
    this.timeSlotTracker = timeSlotTracker;

    SwingUtils.updateLookAndFeel(
        timeSlotTracker.getConfiguration().getString(
            Configuration.LOOK_AND_FEEL_CLASS, DEFAULT_LOOK_AND_FEEL_CLASS),
        timeSlotTracker, true);

    timeSlotTracker.addActionListener(getConfigurationChangesListener(),
        Action.ACTION_CONFIGURATION_CHANGED);
  }

  private ActionListener getConfigurationChangesListener() {
    return new ActionListener() {
      @Override
      public void actionPerformed(Action action) {

        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            SwingUtils.updateLookAndFeel(
                timeSlotTracker.getConfiguration().getString(
                    Configuration.LOOK_AND_FEEL_CLASS,
                    DEFAULT_LOOK_AND_FEEL_CLASS), timeSlotTracker, true);
          }
        });
      }
    };
  }

}
