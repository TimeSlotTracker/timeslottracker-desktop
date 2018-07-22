package net.sf.timeslottracker.gui.actions;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import net.sf.timeslottracker.core.ConfigurationHelper;
import net.sf.timeslottracker.gui.AbstractSimplePanelDialog;
import net.sf.timeslottracker.gui.ChooseFileToSavePanel;
import net.sf.timeslottracker.gui.DialogPanel;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.utils.StringUtils;

/**
 * Dialog for export to ical action
 * 
 * @version File version: $Revision: 1.23 $, $Date: 2007/04/07 03:59:30 $
 * @author Last change: $Author: cnitsa $
 */
public class ExportToICALDialog extends AbstractSimplePanelDialog {

  private static final String ICAL_EXPORT_FILE = "icalExportFile";

  private boolean canceled = true;

  private final ChooseFileToSavePanel chooseIcalFile;

  private File file;

  public ExportToICALDialog(LayoutManager layoutManager) {
    super(layoutManager, "Export task to ical"); // TODO localize

    chooseIcalFile = new ChooseFileToSavePanel(layoutManager);

    // sets default values
    String fileName = ConfigurationHelper.getString(this, ICAL_EXPORT_FILE,
        StringUtils.EMPTY);
    if (!StringUtils.isBlank(fileName)) {
      chooseIcalFile.setFile(fileName);
    }
  }

  /**
   * @return ical file
   */
  public File getFile() {
    return file;
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
    panel.addRow("Output ical file", chooseIcalFile); // TODO localize
  }

  @Override
  protected DialogPanel getDialogPanel() {
    return new DialogPanel(GridBagConstraints.HORIZONTAL, 0.0);
  }

  @Override
  protected Collection<JButton> getButtons() {
    JButton processButton = new JButton("Save"); // TODO localize
    processButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        File fileTemp = chooseIcalFile.getFile();
        if (fileTemp == null) {
          JOptionPane.showMessageDialog(ExportToICALDialog.this,
              "The icalendar file should be not null"); // TODO
          // localize
          return;
        }

        file = fileTemp;
        canceled = false;

        // save current values in config
        ConfigurationHelper.setProperty(ExportToICALDialog.this,
            ICAL_EXPORT_FILE, file.getAbsolutePath());

        ExportToICALDialog.this.dispose();
      }

    });
    processButton.setIcon(icon("save"));

    return Arrays.asList(processButton);
  }

  @Override
  protected int getDefaultHeight() {
    return 105;
  }

  @Override
  protected int getDefaultWidth() {
    return 510;
  }

}
