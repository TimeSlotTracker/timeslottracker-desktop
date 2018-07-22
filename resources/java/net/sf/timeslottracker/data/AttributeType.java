package net.sf.timeslottracker.data;

import java.util.Collection;
import java.util.Vector;

/**
 * Describes attribute's type. Types are defined by end user. He can for example
 * define a type describing a task's project number or project manager name.
 * 
 * @version File version: $Revision: 1086 $, $Date: 2009-08-20 03:29:08 +0700
 *          (Thu, 20 Aug 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class AttributeType {

  private AttributeCategory category;

  private String name;

  private String description;

  private String defaultValue;

  private boolean usedInTasks;

  private boolean usedInTimeSlots;

  private boolean hiddenOnReports;

  private boolean showInTaskInfo;

  private boolean showInTimeSlots;

  private boolean autoAddToTimeSlots;

  private boolean builtin;

  /** holds a list of object registered with this attribute type **/
  private Collection usedByObjects = new Vector();

  /**
   * Constructs a new type of attribute.
   */
  public AttributeType(AttributeCategory category) {
    setCategory(category);
  }

  /**
   * Sets a new category for this attribute type.
   * 
   * @throws NullPointerException
   *           if <code>category</code> is null.
   */
  public void setCategory(AttributeCategory category) {
    if (category == null) {
      throw new NullPointerException();
    }
    this.category = category;
  }

  public AttributeCategory getCategory() {
    return category;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public String toString() {
    return getName();
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  /**
   * Sets the default value used (if any) for this attr's type.
   * 
   * @param defaultValue
   *          default value for attributes of this type, or <code>null</code> if
   *          not applicable.
   */
  public void setDefault(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public String getDefault() {
    return defaultValue;
  }

  /**
   * Sets if this attribute's type can be used for tasks.
   */
  public void setUsedInTasks(boolean yesNo) {
    this.usedInTasks = yesNo;
  }

  /**
   * Returns if this type can be used for tasks.
   */
  public boolean getUsedInTasks() {
    return usedInTasks;
  }

  /**
   * Sets if this attribute's type can be used for time slots.
   */
  public void setUsedInTimeSlots(boolean yesNo) {
    this.usedInTimeSlots = yesNo;
  }

  /**
   * Returns if this type can be used for time slots.
   */
  public boolean getUsedInTimeSlots() {
    return usedInTimeSlots;
  }

  /**
   * Specifies if attributes of this type should be hidden when making report.
   */
  public boolean isHiddenOnReports() {
    return hiddenOnReports;
  }

  /**
   * Set's the visibility on reports.
   * <p>
   * When set to <code>false</code> attributes of this type won't be included in
   * reports.
   */
  public void setHiddenOnReports(boolean isHidden) {
    this.hiddenOnReports = isHidden;
  }

  /**
   * Specifies if this type should be visibile in task info.
   */
  public boolean getShowInTaskInfo() {
    return showInTaskInfo;
  }

  /**
   * Sets if this type should be visible in task info panel.
   */
  public void setShowInTaskInfo(boolean show) {
    this.showInTaskInfo = show;
  }

  /**
   * Specifies if this type should be visible in timeslots table.
   */
  public boolean getShowInTimeSlots() {
    return showInTimeSlots;
  }

  /**
   * Sets if this type should be visible in task info panel.
   */
  public void setShowInTimeSlots(boolean show) {
    this.showInTimeSlots = show;
  }

  /**
   * Specifies if this type should be automatically added to newly created timeslots.
   */
  public boolean isAutoAddToTimeSlots() {
	return autoAddToTimeSlots;
  }

  /**
   * Sets if this type should be automatically added to newly created timeslots.
   */
  public void setAutoAddToTimeSlots(boolean autoAddToTimeSlots) {
	this.autoAddToTimeSlots = autoAddToTimeSlots;
  }

/**
   * Registers a new object is using this attribute type.
   * <p>
   * This registering is important to know if we can delete the record.<br>
   * It also can be used to know which (and how many) objects (Tasks, TimeSlots)
   * are using this type. This can be used for some searching processes or just
   * reporting.
   * <p>
   * Task and TimeSlots are automatically registered when they are constructed,
   * or when their <code>setAttributeType</code> method is called.
   * <p>
   * Task and TimeSlots are unregistered automatically when the type is changed.
   * They have to be <b>manually</b> unregistered when they are deleted.
   * 
   * @param attribute
   *          an attribute to register with this type.
   * 
   * @see Attribute#setAttributeType(AttributeType)
   * @see Attribute#register()
   * @see Attribute#unregister()
   */
  public void register(Attribute attribute) {
    if (!usedByObjects.contains(attribute)) {
      usedByObjects.add(attribute);
    }
  }

  /**
   * Unregisters given attribute from collection of used objects.
   * 
   * @param attribute
   *          an attribute to unregister
   * 
   * @see #register(Attribute)
   */
  public void unregister(Attribute attribute) {
    usedByObjects.remove(attribute);
  }

  /**
   * Returns the collection of objects where this attribute is used.
   */
  public Collection getRegisteredObjects() {
    return usedByObjects;
  }

  /**
   * Specifies if current attribute type is builtin. Means: unpersistent, always
   * exist
   * 
   * @return
   */
  public boolean isBuiltin() {
    return builtin;
  }

  public void setBuiltin(boolean builtin) {
    this.builtin = builtin;
  }

  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof AttributeType)) {
      return false;
    }

    return getName().equals(((AttributeType) obj).getName());
  }

  public int hashCode() {
    return getName().hashCode();
  }
}
