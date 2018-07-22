package net.sf.timeslottracker.data.common;

import java.util.Collection;

import net.sf.timeslottracker.data.AttributeType;

/**
 * Manages {@link AttributeType attribute types}
 * 
 * @version File version: $Revision: 1.23 $, $Date: 2007/04/07 03:59:30 $
 * @author Last change: $Author: cnitsa $
 */
public interface AttributeTypeManager {

  /**
   * Returns attribute type by it's name.
   * 
   * @param name
   *          attribute type's name to find
   * @return found attribute type or <code>null</code> if not found.
   * @throws NullPointerException
   *           when <code>name</code> is null.
   */
  AttributeType get(String name);

  /**
   * Register attribute type.
   * 
   * @param attributeType
   *          attributeType to be registered.
   */
  void register(AttributeType attributeType);

  /**
   * Returns attribute type list
   * 
   * @return not null attribute type list
   */
  Collection<AttributeType> list();

  /**
   * Updates attribute types according given list
   * 
   * @param list
   *          new attribute types
   */
  void update(Collection<AttributeType> list);
}