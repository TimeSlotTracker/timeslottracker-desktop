package net.sf.timeslottracker.data;

/**
 * Describes attribute.
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-08-20 03:29:08 +0700
 *          (Thu, 20 Aug 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class Attribute {

  private AttributeType attributeType;
  private AttributeCategory attributeCategory;
  private Object value;

  public Attribute(AttributeType attributeType) {
    setAttributeType(attributeType);
    this.attributeCategory = attributeType.getCategory();
  }

  public Attribute(AttributeType attributeType, Object value) {
    this(attributeType);
    this.value = value;
  }

  /**
   * Stores the link to attribute type. It also registers/unregisters this
   * attribute from attribute type link collection.
   */
  public void setAttributeType(AttributeType type) {
    unregister();
    this.attributeType = type;
    register();
  }

  public AttributeType getAttributeType() {
    return attributeType;
  }

  /**
   * Registers this record in AttributeType's link collection. This link is
   * neccessary to eg. have the knowleadge that this type is still used and it
   * cannot be deleted.
   * 
   * @see net.sf.timeslottracker.data.AttributeType#register(Attribute)
   */
  private void register() {
    if (attributeType != null) {
      attributeType.register(this);
    }
  }

  /**
   * Unregisters this record in AttributeType's link collection. Freeing this
   * link helps the type to know if we (f.eg.) can delete this attribute type.
   * 
   * @see net.sf.timeslottracker.data.AttributeType#unregister(Attribute)
   */
  public void unregister() {
    if (attributeType != null) {
      attributeType.unregister(this);
    }
  }

  public void set(Object newValue) {
    this.value = newValue;
  }

  public Object get() {
    return value;
  }

}
