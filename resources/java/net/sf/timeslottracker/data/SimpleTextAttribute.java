package net.sf.timeslottracker.data;

import java.awt.Component;

import javax.swing.JTextField;

import net.sf.timeslottracker.Starter;
import net.sf.timeslottracker.core.TimeSlotTracker;
import net.sf.timeslottracker.gui.LayoutManager;

/**
 * Describes text attribute - a one, single line with text represented by
 * JTextField.
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-08-23 17:55:36 +0700
 *          (Sun, 23 Aug 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class SimpleTextAttribute extends AttributeCategory {

  private JTextField editComponent;
  private static final int TEXT_FIELD_LENGTH = 40;

  /**
   * Constucts this type of attribute with proper editComponent
   */
  public SimpleTextAttribute() {
    super();
    editComponent = new JTextField(TEXT_FIELD_LENGTH);
    final TimeSlotTracker timeSlotTracker = Starter.getTimeSlotTracker();
    if (timeSlotTracker != null) {
      setLayoutManager(timeSlotTracker.getLayoutManager());
    }
  }

  public SimpleTextAttribute(LayoutManager layoutManager) {
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

  public Object beforeClose() {
    return editComponent.getText();
  }

}
