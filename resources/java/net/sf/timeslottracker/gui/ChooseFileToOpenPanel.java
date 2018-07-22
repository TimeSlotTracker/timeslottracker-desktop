package net.sf.timeslottracker.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * A simple panel with require an input of file name of a file to open
 * <p>
 * It is composed of fields: field for enter a filaname and a button which opens
 * a system dialog to choose a file to save (so, in fact the file can not
 * exists)
 * <p>
 * It is provided to make a developer life easier and to give him an object to
 * easy manipulate with it
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-06-21 19:08:04 +0700
 *          (Sun, 21 Jun 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class ChooseFileToOpenPanel extends JPanel {

  private LayoutManager layoutManager;

  private JTextField fileNameField = new JTextField(20);
  private JButton chooseButton = new JButton();

  private FocusListener selectAllText;

  /**
   * Constructs a new panel.
   * 
   * @param layoutManager
   *          a reference to LayoutManager class object, the one used to manage
   *          our layout (standard ClassicLayout).
   */
  public ChooseFileToOpenPanel(LayoutManager layoutManager) {
    super(new FlowLayout(FlowLayout.LEFT, 1, 0));

    this.layoutManager = layoutManager;
    selectAllText = new SelectAllOnFocusListener();
    constructPanel();
  }

  private void constructPanel() {
    add(fileNameField);

    fileNameField.addFocusListener(selectAllText);
    chooseButton.setText(layoutManager
        .getCoreString("gui.chooseFileToOpen.button.chooseFile"));
    chooseButton.setIcon(layoutManager.getIcon("open"));
    chooseButton.addActionListener(new chooseFileAction());
    add(chooseButton);
  }

  /**
   * Returns file choosen by user.
   * <p>
   * If not choosen, a null is returned.
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
      int answer = selectFile.showOpenDialog(null);
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
   * Sets the tooltip into file name field, not panel itselfs.
   */
  public void setToolTipText(String toolTipText) {
    fileNameField.setToolTipText(toolTipText);
  }

  /**
   * Sets given filepath in component
   * 
   * @param filePath
   *          non-empty file path
   */
  public void setFilePath(String filePath) {
    fileNameField.setText(filePath);
  }
}
