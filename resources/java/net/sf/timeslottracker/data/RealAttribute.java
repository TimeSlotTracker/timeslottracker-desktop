package net.sf.timeslottracker.data;

import java.awt.Component;

import javax.swing.JTextField;

import net.sf.timeslottracker.gui.LayoutManager;

/**
 * Describes text attribute - a JTextField to enter a numeric (real) values.
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class RealAttribute extends AttributeCategory {

  private JTextField editComponent;
  private static final int TEXT_FIELD_LENGTH = 10;

  /**
   * Constucts this type of attribute with proper editComponent
   */
  public RealAttribute() {
    super();
    editComponent = new JTextField(TEXT_FIELD_LENGTH);
  }

  public RealAttribute(LayoutManager layoutManager) {
    this();
    setLayoutManager(layoutManager);
  }

  public Component getEditComponent() {
    return editComponent;
  }

  public String getString() {
    return editComponent.getText();
  }

  public void beforeShow(Object value, AttributeType type) {
    editComponent.setText(toString(value));
  }

  public boolean validate() {
    String textValue = editComponent.getText();
    try {
      if (textValue != null && textValue.length() > 0) {
        Double.parseDouble(textValue);
      }
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public Object beforeClose() {
    String textValue = editComponent.getText();
    Double value = null;
    try {
      if (textValue != null && textValue.length() > 0) {
        value = new Double(textValue);
      }
      return value;
    } catch (Exception e) {
      return null;
    }
  }

}
