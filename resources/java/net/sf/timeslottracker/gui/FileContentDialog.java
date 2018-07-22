package net.sf.timeslottracker.gui;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JScrollPane;

/**
 * Dialog which show only file content
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
@SuppressWarnings("serial")
public class FileContentDialog extends AbstractSimplePanelDialog {

  private final String filePath;

  public FileContentDialog(LayoutManager layoutManager,
      String titlePropertyName, String filePath) {
    super(layoutManager, layoutManager.getCoreString(titlePropertyName));

    this.filePath = filePath;
  }

  @Override
  protected void fillDialogPanel(DialogPanel panel) {
    Color background = getContentPane().getBackground();

    BufferedReader bufferedInputStream = new BufferedReader(
        new InputStreamReader(
            FileContentDialog.class.getResourceAsStream(filePath)));
    StringBuffer stringBuffer = new StringBuffer();
    try {
      String readLine = bufferedInputStream.readLine();
      while (readLine != null) {
        stringBuffer.append(readLine).append("\n");
        readLine = bufferedInputStream.readLine();
      }
    } catch (IOException e) {
      getLayoutManager().getTimeSlotTracker().errorLog(e);
      return;
    }

    panel.addRow(new JScrollPane(textArea(stringBuffer.toString(), background,
        false, false)));
  }

  @Override
  protected void beforeShow() {
    setResizable(true);
  }

}
