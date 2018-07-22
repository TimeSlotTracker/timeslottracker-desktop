package net.sf.timeslottracker.data;

import java.awt.Component;

import javax.swing.JCheckBox;

import net.sf.timeslottracker.gui.LayoutManager;

/**
 * Describes checkbox attribute
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class CheckBoxAttribute extends AttributeCategory {

  private JCheckBox editComponent;

  /**
   * Constucts this type of attribute with proper editComponent
   */
  public CheckBoxAttribute() {
    super();
    editComponent = new JCheckBox();
  }

  public CheckBoxAttribute(LayoutManager layoutManager) {
    this();
    setLayoutManager(layoutManager);
  }

  public Component getEditComponent() {
    return editComponent;
  }

  public String getString() {
    String isSelected = Boolean.toString(editComponent.isSelected());
    return layoutManager
        .getCoreString("attributes.category.CheckBoxAttribute.value."
            + isSelected);
  }

  public String toString(Object value) {
    if (value == null) {
      return "";
    }
    String isSelected = value.toString();
    return layoutManager
        .getCoreString("attributes.category.CheckBoxAttribute.value."
            + isSelected);
  }

  public void beforeShow(Object value, AttributeType type) {
    if (value == null) {
      editComponent.setSelected(false);
    } else {
      Boolean booleanObject = Boolean.valueOf(value.toString());
      editComponent.setSelected(booleanObject.booleanValue());
    }
  }

  public Object beforeClose() {
    return Boolean.toString(editComponent.isSelected());
  }

}
