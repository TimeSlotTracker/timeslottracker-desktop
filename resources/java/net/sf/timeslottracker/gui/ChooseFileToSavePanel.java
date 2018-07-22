package net.sf.timeslottracker.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * A simple panel with require an input of file name of a file to save
 * <p>
 * It is composed of fields: field for enter a filaname and a button which opens
 * a system dialog to choose a file to save (so, in fact the file can not
 * exists)
 * <p>
 * It is provided to make a developer life easier and to give him an object to
 * easy manipulate with it
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-07-04 16:01:54 +0700
 *          (Sat, 04 Jul 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class ChooseFileToSavePanel extends JPanel {

  private LayoutManager layoutManager;

  private JTextField fileNameField = new JTextField(20);
  private JButton chooseButton = new JButton();

  private Locale locale;

  private FocusListener selectAllText;

  /**
   * Constructs a new panel.
   * 
   * @param layoutManager
   *          a reference to LayoutManager class object, the one used to manage
   *          our layout (standard ClassicLayout).
   */
  public ChooseFileToSavePanel(LayoutManager layoutManager) {
    super(new FlowLayout(FlowLayout.LEFT, 1, 0));
    this.layoutManager = layoutManager;
    locale = layoutManager.getTimeSlotTracker().getLocale();
    selectAllText = new SelectAllOnFocusListener();
    constructPanel();
  }

  private void constructPanel() {
    add(fileNameField);

    fileNameField.addFocusListener(selectAllText);
    chooseButton.setText(layoutManager
        .getCoreString("gui.chooseFileToSave.button.chooseFile"));
    chooseButton.setIcon(layoutManager.getIcon("saveas"));
    chooseButton.addActionListener(new chooseFileAction());
    add(chooseButton);
  }

  /**
   * Sets the fileNameField's text.
   */
  public void setFile(String filename) {
    fileNameField.setText(filename);
  }

  /**
   * Returns file chosen by user.
   * <p>
   * If not chosen, a null is returned.
   */
  public File getFile() {
    if (fileNameField.getText().length() == 0) {
      return null;
    }
    return new File(fileNameField.getText());
  }

  private class chooseFileAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      JFileChooser selectFile = new JFileChooser();
      if (fileNameField.getText().length() > 0) {
        File currentFile = new File(fileNameField.getText());
        selectFile.setSelectedFile(currentFile);
      }
      int answer = selectFile.showSaveDialog(null);
      if (answer != JFileChooser.APPROVE_OPTION) {
        return;
      }
      File choosenFile = selectFile.getSelectedFile();
      fileNameField.setText(choosenFile.getAbsolutePath());
    }
  }

  /**
   * Sets as enabled or disabled the whole control.
   */
  public void setEnabled(boolean enabled) {
    fileNameField.setEnabled(enabled);
    chooseButton.setEnabled(enabled);
  }

  /**
   * Sets the tooltip into file name field, not panel itself.
   */
  public void setToolTipText(String toolTipText) {
    fileNameField.setToolTipText(toolTipText);
  }

}
