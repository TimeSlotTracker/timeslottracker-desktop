package net.sf.timeslottracker.gui.actions;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import javax.swing.JButton;

import net.sf.timeslottracker.core.ConfigurationHelper;
import net.sf.timeslottracker.gui.AbstractSimplePanelDialog;
import net.sf.timeslottracker.gui.DatetimeEditPanel;
import net.sf.timeslottracker.gui.DialogPanel;
import net.sf.timeslottracker.gui.LayoutManager;

import org.apache.commons.lang.StringUtils;

/**
 * Dialog for remove stale data
 * 
 * @version File version: $Revision: 1.23 $, $Date: 2007/04/07 03:59:30 $
 * @author Last change: $Author: cnitsa $
 */
public class RemoveDataDialog extends AbstractSimplePanelDialog {

  interface Action {
    void perform(Date removeBeforeDate);
  }

  private static final String REMOVE_DATE = "beforeRemoveDate";
  private static final SimpleDateFormat dateFormater = new SimpleDateFormat(
      "yyyy-MM-dd");

  private final DatetimeEditPanel fieldDay;
  private final Action action;

  private boolean canceled = true;
  private Date removeBeforeDate;

  public RemoveDataDialog(LayoutManager layoutManager, Action action) {
    super(layoutManager, layoutManager
        .getCoreString("remove.data.dialog.title"));
    this.action = action;

    fieldDay = new DatetimeEditPanel(getLayoutManager(), false, true, false,
        false);
  }

  /**
   * @return remove before date
   */
  public Date getRemoveBeforeDate() {
    return removeBeforeDate;
  }

  /**
   * @return true if dialog was canceled
   */
  public boolean isCanceled() {
    return canceled;
  }

  @Override
  protected void beforeShow() {
    setResizable(true);
  }

  @Override
  protected void fillDialogPanel(DialogPanel panel) {

    DialogPanel dialog = new DialogPanel(GridBagConstraints.HORIZONTAL, 0.0);
    dialog.addRow(
        getLayoutManager().getCoreString(
            "taskTimePanel.configuration.dialog.chooseDay"), fieldDay);

    // sets default values
    String value = ConfigurationHelper.getString(this, REMOVE_DATE, "");
    if (!StringUtils.isBlank(value)) {
      try {
        fieldDay.setDatetime(dateFormater.parse(value));
      } catch (ParseException e) {
        // ignore
      }
    }

    panel.addRow(coreString("remove.data.dialog.date") + ":", fieldDay);
  }

  @Override
  protected Collection<JButton> getButtons() {
    JButton processButton = new JButton(
        coreString("remove.data.dialog.button.remove.label"));
    processButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        removeBeforeDate = fieldDay.getDatetime();

        action.perform(removeBeforeDate);

        canceled = false;

        // save current values in config
        ConfigurationHelper.setProperty(RemoveDataDialog.this, REMOVE_DATE,
            dateFormater.format(removeBeforeDate));

        RemoveDataDialog.this.dispose();
      }

    });
    processButton.setIcon(icon("delete"));

    return Arrays.asList(processButton);
  }

  @Override
  protected int getDefaultHeight() {
    return 150;
  }

  @Override
  protected int getDefaultWidth() {
    return 350;
  }

  @Override
  protected DialogPanel getDialogPanel() {
    return new DialogPanel(GridBagConstraints.HORIZONTAL, 0.0);
  }

}
