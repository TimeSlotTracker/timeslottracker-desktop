package net.sf.timeslottracker.gui.configuration;

import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.gui.LayoutManager;

/**
 * Class with configuration panel for General options.
 *
 * @version File version: $Revision: 1141 $, $Date: 2009-08-04 19:46:38 +0700
 *          (Tue, 04 Aug 2009) $
 * @author Last change: $Author: cnitsa $
 */
class GeneralTab extends ConfigurationPanel {

  GeneralTab(LayoutManager layoutManager) {
    super(layoutManager);
    createPanel();
  }

  public String getTitle() {
    return timeSlotTracker.getString("configuration.tab.general.title");
  }

  private void createPanel() {
    List<ConfigValue> locales = Arrays.asList(new ConfigValue[] {
        new StringConfigValue("en"), new StringConfigValue("de"),
        new StringConfigValue("pl"), new StringConfigValue("cs"),
        new StringConfigValue("ru"), new StringConfigValue("fr"),
        new StringConfigValue("it") });
    addCoreCombo(Configuration.APP_LOCALE, locales);

    addCoreLine(Configuration.HOURS_PER_WORKING_DAY);

    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
    calendar.get(Calendar.DAY_OF_WEEK);

    Locale locale = layoutManager.getTimeSlotTracker().getLocale();
    String[] days = new DateFormatSymbols(locale).getWeekdays();

    List<ConfigValue> daysOfWeek = Arrays.asList(new ConfigValue[] {
        new IntegerConfigValue(days[Calendar.SUNDAY], Calendar.SUNDAY),
        new IntegerConfigValue(days[Calendar.MONDAY], Calendar.MONDAY),
        new IntegerConfigValue(days[Calendar.TUESDAY], Calendar.TUESDAY),
        new IntegerConfigValue(days[Calendar.WEDNESDAY], Calendar.WEDNESDAY),
        new IntegerConfigValue(days[Calendar.THURSDAY], Calendar.THURSDAY),
        new IntegerConfigValue(days[Calendar.FRIDAY], Calendar.FRIDAY),
        new IntegerConfigValue(days[Calendar.SATURDAY], Calendar.SATURDAY) });

    addCoreCombo(Configuration.WEEK_FIRST_DAY, daysOfWeek);

    List<ConfigValue> durationFormats = Arrays.asList(new ConfigValue[] {
        new StringConfigValue("days, hours, minutes"),
        new StringConfigValue("hours, minutes") });

    addCoreCombo(Configuration.TIME_DURATION_FORMAT, durationFormats);

  }
}
