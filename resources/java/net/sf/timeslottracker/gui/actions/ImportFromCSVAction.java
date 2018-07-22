package net.sf.timeslottracker.gui.actions;

import net.sf.timeslottracker.data.Attribute;
import net.sf.timeslottracker.gui.LayoutManager;
import org.sfm.csv.CsvParser;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Import csv file gui action
 *
 * @author Last change: $Author: cnitsa $
 * @version File version: $Revision: 1.23 $, $Date: 2007/04/07 03:59:30 $
 */
public class ImportFromCSVAction extends AbstractAction {
  private final LayoutManager layoutManager;

  public ImportFromCSVAction(LayoutManager layoutManager) {
    super(layoutManager.getCoreString("import.action.name") + " ...", layoutManager.getIcon("new"));

    this.layoutManager = layoutManager;

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed(ActionEvent e) {
    final ImportFromCSVDialog dialog = new ImportFromCSVDialog(layoutManager);
    dialog.activate();
    if (dialog.isCanceled()) {
      return;
    }

    try {
      new SwingWorker<List<String>, String>() {
        @Override
        protected List<String> doInBackground() throws Exception {
          File file = dialog.getFile();
          int col = dialog.getColumn();
          int firstRow = dialog.getFirstDataRow();
          char delimiter = dialog.getDelimiter();
          Charset encoding = dialog.getEncoding();

          try {
            InputStreamReader csvReader = new InputStreamReader(
                new BufferedInputStream(new FileInputStream(file)), encoding);
            Iterator<String[]> it = CsvParser.separator(delimiter).
                skip(firstRow).iterator(csvReader);
            while (it.hasNext()) {
              String[] strings = it.next();
              if (strings.length == 0) {
                continue;
              }

              publish(strings[col]);
            }
          } catch (Exception e2) {
            layoutManager.getTimeSlotTracker().errorLog(e2);
          }

          return Collections.emptyList();
        }

        @Override
        protected void process(List<String> tasknames) {
          for (String taskname : tasknames) {
            if (isCancelled()) {
              break;
            }

            layoutManager.getTasksInterface().addWoDialog(taskname, new ArrayList<Attribute>());
          }
        }

      }.execute();

    } catch (Exception e2) {
      layoutManager.getTimeSlotTracker().errorLog(e2);
    }
  }

}
