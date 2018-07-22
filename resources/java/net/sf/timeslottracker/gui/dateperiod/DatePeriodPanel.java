package net.sf.timeslottracker.gui.dateperiod;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Date;

import javax.swing.JComboBox;

import net.sf.timeslottracker.gui.DatetimeEditPanel;
import net.sf.timeslottracker.gui.DialogPanel;
import net.sf.timeslottracker.gui.DialogPanelUpdater;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.dateperiod.DatePeriod.PeriodType;

/**
 * Gui panel for editing date period
 * 
 * @version File version: $Revision: 1.23 $, $Date: 2007/04/07 03:59:30 $
 * @author Last change: $Author: cnitsa $
 */
public class DatePeriodPanel implements DialogPanelUpdater {
  private final DatePeriod datePeriod;

  private DatetimeEditPanel endPeriod;

  private final LayoutManager layoutManager;

  private JComboBox periodTypeComboBox;

  private DatetimeEditPanel startPeriod;

  public DatePeriodPanel(LayoutManager layoutManager, DatePeriod datePeriod) {
    this.layoutManager = layoutManager;
    this.datePeriod = datePeriod;
  }

  /**
   * @return user entered date period
   */
  public DatePeriod getDatePeriod() {
    return datePeriod;
  }

  /**
   * Save user entered date period, which returned via {@link #getDatePeriod()}
   * 
   * @throws DatePeriodException
   *           when error occurred with date period
   */
  public void save() throws DatePeriodException {
    PeriodType enteredPeriodType = getEnteredPeriodType();
    Date enteredStart = null;
    Date enteredEnd = null;

    if (PeriodType.USER_PERIOD == enteredPeriodType) {
      try {
        enteredStart = startPeriod.getDatetime();
        enteredEnd = endPeriod.getDatetime();
      } catch (NumberFormatException e) {
        throw new DatePeriodException(coreString("dateperiod.warn2"));
      }

      if (enteredStart == null || enteredEnd == null) {
        throw new DatePeriodException(coreString("dateperiod.warn2"));
      }

      if (enteredEnd.getTime() < enteredStart.getTime()) {
        throw new DatePeriodException(coreString("dateperiod.warn1"));
      }
    }

    datePeriod.setPeriodType(enteredPeriodType);
    datePeriod.setUserPeriod(enteredStart, enteredEnd);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.sf.timeslottracker.gui.DialogPanelUpdater#update(net.sf.timeslottracker
   * .gui.DialogPanel)
   */
  @Override
  public void update(DialogPanel panel) {
    // TODO
    String[] types = new String[] { coreString("dateperiod.type.all"),
        coreString("dateperiod.type.lastweek"),
        coreString("dateperiod.type.lastmonth"),
        coreString("dateperiod.type.lastyear"),
        coreString("dateperiod.type.userperiod") };

    this.periodTypeComboBox = new JComboBox(types);
    this.periodTypeComboBox.setSelectedIndex(this.datePeriod.getPeriodType()
        .getPersistentId());
    this.periodTypeComboBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {

        updateState(getEnteredPeriodType());
      }
    });
    panel.addRow(coreString("dateperiod.type.name"), this.periodTypeComboBox);

    this.startPeriod = new DatetimeEditPanel(layoutManager, false, true, false,
        true);
    this.startPeriod.setDatetime(this.datePeriod.getUserPeriodStart());
    panel.addRow(coreString("dateperiod.userperiod.start.name"),
        this.startPeriod);

    this.endPeriod = new DatetimeEditPanel(layoutManager, false, true, false,
        true);
    this.endPeriod.setDatetime(datePeriod.getUserPeriodEnd());
    panel.addRow(coreString("dateperiod.userperiod.end.name"), this.endPeriod);

    updateState(datePeriod.getPeriodType());
  }

  private String coreString(String key) {
    return layoutManager.getCoreString(key);
  }

  private PeriodType getEnteredPeriodType() {
    // used as persistentId for PeriodType
    int selectedIndex = periodTypeComboBox.getSelectedIndex();
    return PeriodType.valueOf(selectedIndex);
  }

  private void updateState(PeriodType periodType) {
    boolean b = (periodType != PeriodType.USER_PERIOD);
    startPeriod.setReadOnly(b);
    endPeriod.setReadOnly(b);
  }
}