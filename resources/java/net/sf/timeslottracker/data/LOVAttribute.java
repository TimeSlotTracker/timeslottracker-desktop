package net.sf.timeslottracker.data;

import java.awt.Component;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JComboBox;

import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.utils.StringUtils;

/**
 * Describes list of values text attribute.
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class LOVAttribute extends AttributeCategory {

  private JComboBox editComponent;

  /**
   * Constucts this type of attribute with proper editComponent
   */
  public LOVAttribute() {
    super();
    editComponent = new JComboBox();
  }

  public LOVAttribute(LayoutManager layoutManager) {
    this();
    setLayoutManager(layoutManager);
  }

  public Component getEditComponent() {
    return editComponent;
  }

  public String getString() {
    String value = null;
    if (editComponent.getSelectedItem() != null) {
      value = editComponent.getSelectedItem().toString();
    }
    return value;
  }

  public void beforeShow(Object value, AttributeType type) {
    editComponent.removeAllItems();
    Collection values = StringUtils.convertStringLOV2Collection(type
        .getDefault());
    if (values == null) {
      return;
    }
    Iterator i = values.iterator();
    int selected = -1;
    int counter = 0;
    while (i.hasNext()) {
      String next = (String) i.next();
      editComponent.addItem(next);
      if (value != null && value.equals(next)) {
        selected = counter;
      }
      counter++;
    }
    if (selected >= 0) {
      editComponent.setSelectedIndex(selected);
    }
  }

  public Object beforeClose() {
    return editComponent.getSelectedItem();
  }

}
