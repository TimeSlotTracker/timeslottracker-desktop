package net.sf.timeslottracker.gui.lookandfeel

import net.sf.timeslottracker.core.Action
import net.sf.timeslottracker.core.Configuration
import net.sf.timeslottracker.core.TimeSlotTracker
import net.sf.timeslottracker.utils.SwingUtils
import javax.swing.SwingUtilities

/**
 * Class that manages look and feels for application
 */
class LookAndFeelManagerImpl(private val timeSlotTracker: TimeSlotTracker) {
    init {
        update()
        timeSlotTracker.addActionListener({
            SwingUtilities.invokeLater {
                update()
            }
        }, Action.ACTION_CONFIGURATION_CHANGED)
    }

    private fun update() {
        SwingUtils.updateLookAndFeel(
                timeSlotTracker.configuration.getString(
                        Configuration.LOOK_AND_FEEL_CLASS, DEFAULT_LOOK_AND_FEEL_CLASS),
                timeSlotTracker, true)
    }

    companion object {
        private val DEFAULT_LOOK_AND_FEEL_CLASS = "com.jgoodies.looks.plastic.PlasticLookAndFeel"
    }
}
