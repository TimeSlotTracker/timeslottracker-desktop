package net.sf.timeslottracker.data;

import java.awt.Component;

import javax.swing.JTextArea;
import javax.swing.UIManager;

import net.sf.timeslottracker.gui.LayoutManager;

/**
 * Describes text attribute - represented by a TextArea
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class TextAreaAttribute extends AttributeCategory {

  private JTextArea editComponent;
  private final static int ROWS = 8;
  private final static int COLS = 50;

  /**
   * Constucts this type of attribute with proper editComponent
   */
  public TextAreaAttribute() {
    super();
    editComponent = new JTextArea(ROWS, COLS);
    editComponent.setBorder(UIManager.getBorder("TextField.border"));
    editComponent.setLineWrap(true);
    editComponent.setWrapStyleWord(true);
  }

  public TextAreaAttribute(LayoutManager layoutManager) {
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
    String text = toString(value);
    // text = text.replaceAll("~n", "\n");
    editComponent.setText(text);
  }

  public Object beforeClose() {
    String value = editComponent.getText();
    // value = value.replaceAll("\n", "~n");
    return value;
  }

}
