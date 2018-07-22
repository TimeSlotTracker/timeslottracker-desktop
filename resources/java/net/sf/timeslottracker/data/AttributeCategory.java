package net.sf.timeslottracker.data;

import java.awt.Component;

import net.sf.timeslottracker.gui.LayoutManager;

/**
 * Describes attribute's category - (simple text, Integer, any other). The
 * division is done mostly based on the method of entering the value.
 * <p>
 * It is an abstract class, because the proper type should be definied by
 * subclass of this class.
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-08-23 17:55:36 +0700
 *          (Sun, 23 Aug 2009) $
 * @author Last change: $Author: cnitsa $
 */
public abstract class AttributeCategory {

  protected LayoutManager layoutManager;
  protected String categoryName;

  public AttributeCategory() {
  }

  public void setLayoutManager(LayoutManager layoutManager) {
    this.layoutManager = layoutManager;
    this.categoryName = getName();
  }

  /**
   * Returns a name for this category
   */
  public String getName() {
    String className = this.getClass().getName();
    int lastDot = className.lastIndexOf(".");
    if (lastDot > 0) {
      className = className.substring(lastDot + 1);
    }
    return layoutManager.getCoreString("attributes.category." + className
        + ".name");
  }

  /**
   * Returng GUI component used to set/edit value of this type of attribute.
   * <p>
   * It can be for eg. <code>JTextField</code> or any other, even complicated
   * component.
   * <p>
   * This component is then used to add it to window in which a user will edit
   * the attribute.
   */
  public abstract Component getEditComponent();

  /**
   * Returns the "user friendly" name of this category.
   * <p>
   * This value is get from properties files with languages.
   */
  public String toString() {
    return categoryName;
  }

  /**
   * Executes actions when user is just about to edit/set a value to attribute.
   * <p>
   * This method is called just before the window is show to the user. The most
   * common use it to set old value to edit component.
   * 
   * @param value
   *          an existed, old (if any) attribute's value passed from Attribute.
   * @param type
   *          an <code>AttributeType</code> instance of this object
   */
  public abstract void beforeShow(Object value, AttributeType type);

  /**
   * Executes actions when a user is just about to close the edit window.
   * <p>
   * It should get the value from <code>editComponent</code> and return its
   * value so an attribute new value can be set.
   * <p>
   * It is called only when a user wants to save his changes. If the edit window
   * is canceled this methos wouldn't be called.
   * 
   * @return new value got from <code>editComponent</code> for Attribute.
   */
  public abstract Object beforeClose();

  /**
   * Executes actions when a users wants to close this window and save a new
   * value of this attribute.
   * <p>
   * It simply should validate if this attribute has correct format.
   * <p>
   * If not overrided the default <code>true</code> is returned which means the
   * value the users entered is cool.
   * 
   * @return <code>true</code> if the value is properly validated.
   */
  public boolean validate() {
    return true;
  }

  /**
   * Returns value entered in edit component or <code>null</code> if it cannot
   * be done (f.eg. because of complex edit component. This value should be
   * converted to <code>String</code> object.
   * <p/>
   * <b>Note:</b> This method shoud not validate entered value. It is used
   * mostly when a user change the attribute type.
   * 
   * @see net.sf.timeslottracker.gui.attributes.AttributeEditDialog.ChangeEditComponentAction
   */
  public abstract String getString();

  /**
   * Converts a attribute's value into a string using type's specific actions.
   * <p>
   * For numbers is should f.e. adds missing zeros after decimal point, or for
   * dates converts it to proper format.
   * 
   * @param value
   *          attribute's value to convert to String using knowleadge of its
   *          type.
   */
  public String toString(Object value) {
    return value == null ? "" : value.toString();
  }

  /**
   * Controls the indexing process. If some category shouldn't be indexed (for
   * example image) it should returns false.
   * <p/>
   * Default is <code>true</code>
   * 
   * @return <code>true</code> if category shouldn't be indexed.
   */
  public boolean includeInIndex() {
    return true;
  }
}
