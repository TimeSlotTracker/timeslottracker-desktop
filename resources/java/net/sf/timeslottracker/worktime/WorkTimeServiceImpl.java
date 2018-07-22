package net.sf.timeslottracker.worktime;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.core.TimeSlotTracker;
import net.sf.timeslottracker.utils.TimeUtils;

/**
 * Service implementation
 * 
 * @author glazachev
 */
public class WorkTimeServiceImpl implements WorkTimeService {

  private static final int DEFAULT_WORK_TIME_PER_DAY = 8;
  private static final String HOLIDAYS_PROPERTIES = "holidays.properties";
  private static final long M_SEC_PER_HOUR = 60 * 60 * 1000;

  private final Properties holidays = new Properties();
  private final Format dateFormat = new SimpleDateFormat("yyyy-MM-dd");
  private final Integer workDayTime;

  public WorkTimeServiceImpl(TimeSlotTracker timeSlotTracker)
      throws IOException {
    this.workDayTime = timeSlotTracker.getConfiguration().getInteger(
        Configuration.HOURS_PER_WORKING_DAY, DEFAULT_WORK_TIME_PER_DAY);

    String directory = timeSlotTracker.getConfiguration()
        .getPropertiesDirectory();
    String fileName = directory + System.getProperty("file.separator")
        + HOLIDAYS_PROPERTIES;

    FileReader reader = null;
    try {
      File file = new File(fileName);
      if (file.exists()) {
        reader = new FileReader(file);
        holidays.load(reader);
      }
    } finally {
      if (reader != null) {
        reader.close();
      }
    }
  }

  @Override
  public long getWorkTime(Date start, Date finish) {
    Calendar dayBegin = TimeUtils.getDayBegin(start);
    Calendar dayEnd = TimeUtils.getDayEnd(finish);

    int first = dayBegin.get(Calendar.DAY_OF_YEAR);
    int last = dayEnd.get(Calendar.DAY_OF_YEAR);

    long workTime = 0;

    for (int i = first; i <= last; i++) {
      dayBegin.add(Calendar.DAY_OF_YEAR, i == first ? 0 : 1);

      Date time = dayBegin.getTime();
      String value = dateFormat.format(time);

      Object override = holidays.get(value);
      if (override != null) {
        workTime += Long.parseLong(override.toString());
      } else {
        int day = dayBegin.get(Calendar.DAY_OF_WEEK);
        if (day != Calendar.SATURDAY && day != Calendar.SUNDAY) {
          workTime += workDayTime;
        }
      }
    }

    return workTime * M_SEC_PER_HOUR;
  }

}
