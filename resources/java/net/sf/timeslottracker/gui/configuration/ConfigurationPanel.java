package net.sf.timeslottracker.gui.configuration;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.core.TimeSlotTracker;
import net.sf.timeslottracker.gui.DialogPanel;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.utils.StringUtils;

/**
 * This is a superclass for every configuration panel used in our application.
 * If a specific module wants to have it's own "tab" in a configuration dialog
 * it should have a dialog derived from this abstract class.
 * 
 * @version File version: $Revision: 1155 $, $Date: 2009-06-06 18:55:00 +0700
 *          (Sat, 06 Jun 2009) $
 * @author Last change: $Author: cnitsa $
 */
public abstract class ConfigurationPanel extends JPanel {

  protected LayoutManager layoutManager;
  protected TimeSlotTracker timeSlotTracker;
  protected DialogPanel dialog;
  protected Configuration configuration;
  /** Stores properties. Key=property's name; Value=a component (eg. JTextField) */
  protected Map properties;

  protected ConfigurationPanel(LayoutManager layoutManager) {
    super(new BorderLayout());
    setLayoutManager(layoutManager);
    configuration = timeSlotTracker.getConfiguration();

    Color background = getBackground();
    dialog = new DialogPanel(GridBagConstraints.HORIZONTAL, 0.0);
    dialog.setBackground(background);
    add(dialog, BorderLayout.CENTER);
    properties = new TreeMap();
  }

  /**
   * Sets connection with layoutManager.
   */
  public void setLayoutManager(LayoutManager layoutManager) {
    this.layoutManager = layoutManager;
    this.timeSlotTracker = layoutManager.getTimeSlotTracker();
  }

  /**
   * Returns the title for the tab.
   */
  public abstract String getTitle();

  /**
   * Adds one line with configuration to configuration panel.
   * 
   * @param label
   *          a label to name the property.
   * @param propertyName
   *          a property name to get the configuration value from Configuration
   *          object.
   * @param tooltipText
   *          tooltipText, maybe null
   */
  protected void addLine(String label, String propertyName, String tooltipText) {
    JTextField textField = new JTextField(30);
    if (tooltipText != null) {
      String tooltipTextHTML = "<html>" + tooltipText.replaceAll("\n", "<br>")
          + "</html>";
      textField.setToolTipText(tooltipTextHTML);
    }
    properties.put(propertyName, textField);
    textField.setText(configuration.get(propertyName, null));
    dialog.addRow(label, textField);
  }

  /**
   * Adds one line with simple label
   * 
   * @param label
   *          label text
   */
  protected void addLabel(String label) {
    dialog.addRow(label);
  }

  /**
   * Adds one line contained with label and text field for given property. The
   * label is taken from the properties (language oriented) files. The name for
   * the property should be "configuration.property.[propertyName].label".
   * 
   * @param propertyName
   *          a name of property (eg. "app.window.height"). A localization
   *          property files should also contains property with label for this
   *          propertyName.
   */
  protected void addCoreLine(String propertyName) {
    addCoreLine(propertyName, false);
  }

  /**
   * /** Adds one line contained with label and text field for given property.
   * The label is taken from the properties (language oriented) files. The name
   * for the property should be <br/>
   * "configuration.property.[propertyName].label". <br/>
   * The tooltip for this property should be named: <br/>
   * "configuration.property.[propertyName].comment".
   * 
   * @param propertyName
   *          a name of property (eg. "app.window.height"). A localization
   *          property files should also contains property with label for this
   *          propertyName.
   * @param useComment
   *          use comment for selected property and set it as a tooltip
   * 
   * @see #addCoreLine(String)
   */
  protected void addCoreLine(String propertyName, boolean useComment) {
    String propertyNameLabel = "configuration.property." + propertyName
        + ".label";
    String label = timeSlotTracker.getString(propertyNameLabel);

    String comment = null;
    if (useComment) {
      String propertyComment = "configuration.property." + propertyName
          + ".comment";
      comment = timeSlotTracker.getString(propertyComment);
    }
    addLine(label, propertyName, comment);
  }

  /**
   * Adds one line containted with combo-box and text field for given property.
   * The label is taken from the properties (language oriented) files. The name
   * for the property should be "configuration.property.[propertyName].label".
   * 
   * @param propertyName
   *          a name of property (eg. "app.window.height"). A localization
   *          property files should also contains property with label for this
   *          propertyName.
   */
  protected JComboBox addCoreCombo(String propertyName, List<? extends ConfigValue> values) {
    String propertyNameLabel = "configuration.property." + propertyName
        + ".label";
    String label = timeSlotTracker.getString(propertyNameLabel);
    JComboBox comboField = new JComboBox(values.toArray());
    properties.put(propertyName, comboField);

    String selectedValue = configuration.get(propertyName, null);
    ConfigValue selectedConfigValue = null;
    for (ConfigValue value : values) {
      if (value.getValue().equals(selectedValue)) {
        selectedConfigValue = value;
      }
    }
    comboField.setSelectedItem(selectedConfigValue);

    comboField.setEditable(false);
    dialog.addRow(label, comboField);
    return comboField;
  }

  /**
   * Adds one line contained with check-box. The label is taken from the
   * properties (language oriented) files. The name for the property should be
   * "configuration.property.[propertyName].label".
   * 
   * @param propertyName
   *          a name of property (eg. "app.window.height"). A localization
   *          property files should also contains property with label for this
   *          propertyName.
   */
  protected JCheckBox addCoreCheckBox(String propertyName) {
    return addCoreCheckBox(propertyName, null);
  }

  /**
   * @see #addCoreCheckBox(String)
   * @param defaultValue
   *          default value for property
   */
  protected JCheckBox addCoreCheckBox(String propertyName, Boolean defaultValue) {
    String propertyNameLabel = "configuration.property." + propertyName
        + ".label";
    String label = timeSlotTracker.getString(propertyNameLabel);
    JCheckBox checkBoxField = new JCheckBox();
    properties.put(propertyName, checkBoxField);
    Boolean configValue = configuration.getBoolean(propertyName, defaultValue);
    if (configValue != null) {
      checkBoxField.setSelected(configValue.booleanValue());
    }
    dialog.addRow(label, checkBoxField);
    return checkBoxField;
  }

  /**
   * Verifies if given configuration data on this tab are correct ones before
   * saving.
   * <p/>
   * Examples:
   * <ul>
   * <li>backup directory - if it exists</li>
   * <li>connection to jira - if connection info entered correctly</li>
   * </ul>
   * <p/>
   * You have to put your own implementation if you want to control this values.
   * The default implementation returns <code>true</code>.
   * 
   * @return <code>true</code> if values are ok (<b>default value</b>),
   *         <code>false</code> if configuration shouldn't be saved.
   */
  protected boolean verify() {
    return true;
  }

  /**
   * Saves the properties back into configuration.
   * <p>
   * It's called from ConfigurationWindow when a user presses the Save button.
   */
  protected void save() {
    Collection propertyNames = properties.keySet();
    Iterator names = propertyNames.iterator();
    while (names.hasNext()) {
      String propertyName = (String) names.next();
      Object objectField = properties.get(propertyName);
      String value = StringUtils.EMPTY;
      if (objectField instanceof JTextField) {
        JTextField field = (JTextField) objectField;
        value = field.getText();
      } else if (objectField instanceof JComboBox) {
        JComboBox field = (JComboBox) objectField;
        Object o = field.getSelectedItem();
        if (o instanceof ConfigValue) {
          value = ((ConfigValue) o).getValue();
        } else if (o != null) {
          value = o.toString();
        }
      } else if (objectField instanceof JCheckBox) {
        JCheckBox field = (JCheckBox) objectField;
        boolean booleanValue = field.isSelected();
        value = Boolean.toString(booleanValue);
      }
      configuration.set(propertyName, value);
    }
  }

  public String toString() {
    return getTitle();
  }
}
