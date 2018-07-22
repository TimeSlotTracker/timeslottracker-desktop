package net.sf.timeslottracker.gui.dateperiod;

import static org.junit.Assert.*;
import net.sf.timeslottracker.gui.dateperiod.DatePeriod.PeriodType;

import org.junit.Test;

public class DatePeriodTest {

  @Test
  public void periodTypeValueOf() {
    PeriodType value = DatePeriod.PeriodType.USER_PERIOD;

    assertSame(value, DatePeriod.PeriodType.valueOf(value.getPersistentId()));
  }
}
