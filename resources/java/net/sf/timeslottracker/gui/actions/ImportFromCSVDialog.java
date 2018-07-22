package net.sf.timeslottracker.gui.actions;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sf.timeslottracker.core.ConfigurationHelper;
import net.sf.timeslottracker.gui.AbstractSimplePanelDialog;
import net.sf.timeslottracker.gui.ChooseFileToOpenPanel;
import net.sf.timeslottracker.gui.DialogPanel;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.utils.StringUtils;

/**
 * Dialog for import from csv action
 * 
 * @version File version: $Revision: 1.23 $, $Date: 2007/04/07 03:59:30 $
 * @author Last change: $Author: cnitsa $
 */
class ImportFromCSVDialog extends AbstractSimplePanelDialog {

  private static final String DELIMITER = "delimiter";

  private static final String COLUMNWITHTASK = "columnwithtask";

  private static final String FIRSTROW = "firstrow";

  private static final String CSVFILE = "csvfile";

  private static final String ENCODING = "encoding";

  private boolean canceled = true;

  private final ChooseFileToOpenPanel chooseCsvFile;

  private int column;

  private JTextField columnField = new JTextField(2);

  private JTextField delimField = new JTextField(1);

  private char delimiter;

  private File file;
  private int firstDataRow;
  private JTextField rowField = new JTextField(1);
  private JComboBox encoding = new JComboBox(new Vector<Charset>(Charset
      .availableCharsets().values()));

  public ImportFromCSVDialog(LayoutManager layoutManager) {
    super(layoutManager, layoutManager.getCoreString("import.csv.dialog.title"));

    chooseCsvFile = new ChooseFileToOpenPanel(layoutManager);

    // sets default values
    chooseCsvFile.setFilePath(ConfigurationHelper.getString(this, CSVFILE,
        StringUtils.EMPTY));
    rowField.setText(ConfigurationHelper.getString(this, FIRSTROW, "0"));
    columnField.setText(ConfigurationHelper
        .getString(this, COLUMNWITHTASK, "0"));
    delimField.setText(ConfigurationHelper.getString(this, DELIMITER, ","));
    encoding.setSelectedItem(StringUtils.charset(ConfigurationHelper.getString(
        this, ENCODING, "UTF-8")));
  }

  /**
   * @return column with task name
   */
  public int getColumn() {
    return column;
  }

  /**
   * @return csv fields delimiter
   */
  public char getDelimiter() {
    return delimiter;
  }

  /**
   * @return csv file
   */
  public File getFile() {
    return file;
  }

  /**
   * @return number of first row with data
   */
  public int getFirstDataRow() {
    return firstDataRow;
  }

  /**
   * @return true if dialog was canceled
   */
  public boolean isCanceled() {
    return canceled;
  }

  /**
   * @return csv file encoding
   */
  public Charset getEncoding() {
    return StringUtils.charset(encoding.getSelectedItem().toString());
  }

  @Override
  protected void beforeShow() {
    setResizable(true);
  }

  @Override
  protected void fillDialogPanel(DialogPanel panel) {
    JPanel panel1 = getjPanel();
    panel1.add(chooseCsvFile);
    panel.addRow(coreString("import.csv.dialog.file"), panel1);

    panel.addRow(coreString("import.csv.dialog.enc"), encoding);

    JPanel panel2 = getjPanel();
    panel2.add(rowField);
    panel.addRow(coreString("import.csv.dialog.firstrow"), panel2);

    JPanel panel3 = getjPanel();
    panel3.add(columnField);
    panel.addRow(coreString("import.csv.dialog.taskcol"), panel3);

    JPanel panel4 = getjPanel();
    panel4.add(delimField);
    panel.addRow(coreString("import.csv.dialog.delim"), panel4);
  }

  private JPanel getjPanel() {
    return new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
  }

  @Override
  protected Collection<JButton> getButtons() {
    JButton processButton = new JButton("Process"); // TODO localize
    processButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        File fileTemp = chooseCsvFile.getFile();
        if (fileTemp == null || !fileTemp.exists() || !fileTemp.isFile()) {
          JOptionPane.showMessageDialog(ImportFromCSVDialog.this,
              "The file for csv file should be not null and exist"); // TODO
          // localize
          return;
        }

        String columnTemp = columnField.getText();
        int columnNumber = StringUtils.getNumber(columnTemp);
        if (StringUtils.isBlank(columnTemp) || -1 == columnNumber) {

          JOptionPane.showMessageDialog(ImportFromCSVDialog.this,
              "The column for task name should be not null and >= 0"); // TODO
          // localize
          return;
        }

        String rowTemp = rowField.getText();
        int rowNumber = StringUtils.getNumber(rowTemp);
        if (StringUtils.isBlank(rowTemp) || -1 == rowNumber) {

          JOptionPane.showMessageDialog(ImportFromCSVDialog.this,
              "The first row with data should be not null and >= 0"); // TODO
          // localize
          return;
        }

        String delimTemp = delimField.getText();
        if (StringUtils.isBlank(columnTemp) || 1 != delimTemp.length()) {
          JOptionPane.showMessageDialog(ImportFromCSVDialog.this,
              "The delimiter should be not null character"); // TODO
          // localize
          return;
        }

        file = fileTemp;
        column = columnNumber;
        firstDataRow = rowNumber;
        delimiter = delimTemp.charAt(0);
        canceled = false;

        // save current values in config
        ConfigurationHelper.setProperty(ImportFromCSVDialog.this, CSVFILE,
            file.getAbsolutePath());
        ConfigurationHelper.setProperty(ImportFromCSVDialog.this, FIRSTROW,
            firstDataRow);
        ConfigurationHelper.setProperty(ImportFromCSVDialog.this,
            COLUMNWITHTASK, column);
        ConfigurationHelper.setProperty(ImportFromCSVDialog.this, DELIMITER,
            delimTemp);
        ConfigurationHelper.setProperty(ImportFromCSVDialog.this, ENCODING,
            encoding.getSelectedItem().toString());

        ImportFromCSVDialog.this.dispose();
      }

    });
    processButton.setIcon(icon("open"));

    return Arrays.asList(processButton);
  }

  @Override
  protected int getDefaultHeight() {
    return 225;
  }

  @Override
  protected int getDefaultWidth() {
    return 600;
  }

  @Override
  protected DialogPanel getDialogPanel() {
    return new DialogPanel(GridBagConstraints.HORIZONTAL, 0.0);
  }

}
