package net.sf.timeslottracker.gui.layouts.classic.tasks;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.core.TimeSlotTracker;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.TaskTimePanelConfigDialog;

/**
 * A panel with times of whole task, current day, week and month.
 * <p>
 * Done for RFE: #1281912 (by furu)
 * <p>
 * Access is a package one because it is used only in TaskInfo
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-06-21 18:47:38 +0700
 *          (Sun, 21 Jun 2009) $
 * @author Last change: $Author: cnitsa $
 */
class TaskInfoTimePanel extends JPanel {

  /** logging using java.util.logging package **/
  private static Logger logger = Logger
      .getLogger("net.sf.timeslottracker.gui.layouts.classic.tasks");

  private LayoutManager layoutManager;

  private boolean includingSubtasks;

  private JLabel allValue;

  private JLabel dayLabel, dayValue;

  private JLabel weekLabel, weekValue;

  private JLabel monthLabel, monthValue;

  private String defaultDayLabel;

  private String defaultWeekLabel;

  private String defaultMonthLabel;

  private TimeSlotTracker timeSlotTracker;

  private Configuration configuration;

  private Date selectedDay, selectedWeek, selectedMonth;

  final static private SimpleDateFormat dateFormater = new SimpleDateFormat(
      "yyyy-MM-dd");

  final static private SimpleDateFormat monthDayFormater = new SimpleDateFormat(
      "MM-dd");

  final static private SimpleDateFormat dayFormater = new SimpleDateFormat("dd");

  final static private SimpleDateFormat monthFormater = new SimpleDateFormat(
      "yyyy-MM");

  TaskInfoTimePanel(LayoutManager layoutManager, boolean includingSubtasks) {
    super(new FlowLayout(FlowLayout.LEFT, 5, 3));
    this.includingSubtasks = includingSubtasks;
    this.layoutManager = layoutManager;
    timeSlotTracker = layoutManager.getTimeSlotTracker();
    configuration = timeSlotTracker.getConfiguration();

    defaultDayLabel = layoutManager.getString("taskinfo.timepanel.day");
    defaultWeekLabel = layoutManager.getString("taskinfo.timepanel.week");
    defaultMonthLabel = layoutManager.getString("taskinfo.timepanel.month");

    dayLabel = new JLabel();
    weekLabel = new JLabel();
    monthLabel = new JLabel();

    setLabels();

    allValue = new JLabel();
    dayValue = new JLabel();
    weekValue = new JLabel();
    monthValue = new JLabel();

    MouseAdapter action = new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        new ConfigureAction().actionPerformed(null);
      }
    };
    dayLabel.addMouseListener(action);
    weekLabel.addMouseListener(action);
    monthLabel.addMouseListener(action);

    add(allValue);
    add(dayLabel);
    add(dayValue);
    add(weekLabel);
    add(weekValue);
    add(monthLabel);
    add(monthValue);
  }

  private void setLabels() {
    // re-read configuration values
    selectedDay = getField(Configuration.LAST_TIMEPANEL_DAY);
    selectedWeek = getField(Configuration.LAST_TIMEPANEL_WEEK);
    selectedMonth = getField(Configuration.LAST_TIMEPANEL_MONTH);
    // show new values
    String dayLabelText = selectedDay == null ? defaultDayLabel : dateFormater
        .format(selectedDay) + ":";
    dayLabel.setText(wrapAsAnchor(dayLabelText));

    if (selectedWeek == null) {
      weekLabel.setText(wrapAsAnchor(defaultWeekLabel));
    } else {
      Calendar calendar = new GregorianCalendar(timeSlotTracker.getLocale());
      calendar.setTime(selectedWeek);
      int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

      logger.finest("getFirstDayOfWeek()=" + calendar.getFirstDayOfWeek());
      logger.finest("get(DAY_OF_WEEK)=" + calendar.get(Calendar.DAY_OF_WEEK));
      int diff = (dayOfWeek - calendar.getFirstDayOfWeek() + 7) % 7;
      logger.finest("diff=" + diff);
      logger.info("dayOfWeek=" + dayOfWeek);
      calendar.add(Calendar.DATE, -diff);
      int startMonth = calendar.get(Calendar.MONTH);

      String week = dateFormater.format(calendar.getTime());
      week += "..";

      calendar.add(Calendar.DATE, 6);
      int stopMonth = calendar.get(Calendar.MONTH);
      logger.info("startMonth=" + startMonth + ", stopMonth=" + stopMonth);
      if (startMonth != stopMonth) {
        week += monthDayFormater.format(calendar.getTime());
      } else {
        week += dayFormater.format(calendar.getTime());
      }
      weekLabel.setText(wrapAsAnchor(week + ":"));
    }

    if (selectedMonth == null) {
      monthLabel.setText(wrapAsAnchor(defaultMonthLabel));
    } else {
      monthLabel
          .setText(wrapAsAnchor(monthFormater.format(selectedMonth) + ":"));
    }
  }

  private String wrapAsAnchor(String value) {
    return "<html><a href=1>" + value + "</a></html>";
  }

  void setTimes(String[] times) {
    allValue.setText(times[0]);
    dayValue.setText(times[1]);
    weekValue.setText(times[2]);
    monthValue.setText(times[3]);
  }

  Date getSelectedDay() {
    return selectedDay;
  }

  Date getSelectedWeek() {
    return selectedWeek;
  }

  Date getSelectedMonth() {
    return selectedMonth;
  }

  void clear() {
    allValue.setText("");
    dayValue.setText("");
    weekValue.setText("");
    monthValue.setText("");
  }

  /**
   * Gets value from configuration.
   */
  private Date getField(String configName) {
    Date date = null;
    configName += includingSubtasks ? ".thisLevel" : ".includingSubtasks";
    String configValue = configuration.getString(configName, null);
    if (configValue != null) {
      try {
        date = dateFormater.parse(configValue);
      } catch (java.text.ParseException e) {
      }
    }
    return date;
  }

  private class ConfigureAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      TaskTimePanelConfigDialog dialog = new TaskTimePanelConfigDialog(
          layoutManager, includingSubtasks);
      setLabels();
      // update times
      timeSlotTracker.fireAction(TaskInfo.ACTION_UPDATE_TIMERS);
    }
  }

}
