package net.sf.timeslottracker.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import net.sf.timeslottracker.gui.dateperiod.DatePeriod;
import net.sf.timeslottracker.gui.dateperiod.DatePeriodException;
import net.sf.timeslottracker.gui.dateperiod.DatePeriodPanel;

/**
 * The dialog used for interaction with user for desired time slot filtering in
 * task view.
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-06-21 18:47:38 +0700
 *          (Sun, 21 Jun 2009) $
 * @author Last change: $Author: cnitsa $
 */
@SuppressWarnings("serial")
public class TimeSlotFilterDialog extends AbstractSimplePanelDialog {

  private final DatePeriodPanel datePeriodPanel;

  /**
   * Creates dialog with given layout manager, date period
   * <p>
   * Given {@link DatePeriod} object will be updated
   */
  public TimeSlotFilterDialog(LayoutManager layoutManager, DatePeriod datePeriod) {
    super(layoutManager, layoutManager.getCoreString("filterDataDialog.title"));
    this.datePeriodPanel = new DatePeriodPanel(layoutManager, datePeriod);
  }

  /**
   * @return updated date period
   */
  public DatePeriod getDatePeriod() {
    return datePeriodPanel.getDatePeriod();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sf.timeslottracker.gui.AbstractSimplePanelDialog#beforeShow()
   */
  @Override
  protected void beforeShow() {
    super.beforeShow();

    setSize(510, 180);
    setResizable(true);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.sf.timeslottracker.gui.AbstractSimplePanelDialog#fillDialogPanel(net
   * .sf.timeslottracker.gui.DialogPanel)
   */
  @Override
  protected void fillDialogPanel(DialogPanel panel) {
    datePeriodPanel.update(panel);
  }

  @Override
  protected Collection<JButton> getButtons() {
    JButton apply = new JButton(coreString("filterDataDialog.apply.name"));

    apply.setIcon(getLayoutManager().getIcon("save"));
    apply.addActionListener(new ActionListener() {
      /*
       * (non-Javadoc)
       * 
       * @see
       * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
       * )
       */
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          datePeriodPanel.save();
          dispose();
        } catch (DatePeriodException ex) {
          JOptionPane.showMessageDialog(TimeSlotFilterDialog.this,
              ex.getMessage(), coreString("filterDataDialog.title"),
              JOptionPane.ERROR_MESSAGE);
        }
      }

    });

    return Collections.singleton(apply);
  }

}
