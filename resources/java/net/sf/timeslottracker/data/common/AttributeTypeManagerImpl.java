package net.sf.timeslottracker.data.common;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import net.sf.timeslottracker.data.AttributeType;
import net.sf.timeslottracker.integrations.issuetracker.IssueKeyAttributeType;
import net.sf.timeslottracker.integrations.issuetracker.IssueWorklogStatusType;
import net.sf.timeslottracker.monitoring.ScreenshotAttributeType;

/**
 * Attribute Type Manager implementation
 * 
 * @author User: zgibek Date: 2009-07-14 Time: 07:11:53 $Id: not-commited-yet
 *         zgibek Exp $
 */
public class AttributeTypeManagerImpl implements AttributeTypeManager {

  private static final AttributeTypeManager INSTANCE = new AttributeTypeManagerImpl();

  public static AttributeTypeManager getInstance() {
    return INSTANCE;
  }

  private final HashMap<String, AttributeType> attributeTypes = new HashMap<String, AttributeType>();

  public AttributeTypeManagerImpl() {
    /* auto add builtin attribute types */
    registerBuiltin();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.sf.timeslottracker.data.common.AttributeTypeManager#get(java.lang.String
   * )
   */
  public AttributeType get(String name) {
    if (name == null) {
      throw new NullPointerException("AttributeType's name can't be null");
    }

    return attributeTypes.get(name);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sf.timeslottracker.data.common.AttributeTypeManager#list()
   */
  @Override
  public Collection<AttributeType> list() {
    return Collections.unmodifiableCollection(attributeTypes.values());
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.sf.timeslottracker.data.common.AttributeTypeManager#register(net.sf
   * .timeslottracker.data.AttributeType)
   */
  public void register(AttributeType attributeType) {
    String key = attributeType.getName();
    if (attributeTypes.containsKey(key)) {
      return;
    }

    attributeTypes.put(key, attributeType);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.sf.timeslottracker.data.common.AttributeTypeManager#update(java.util
   * .Collection)
   */
  @Override
  public void update(Collection<AttributeType> list) {
    attributeTypes.clear();
    registerBuiltin();

    for (AttributeType attributeType : list) {
      register(attributeType);
    }
  }

  private void registerBuiltin() {
    register(ScreenshotAttributeType.getInstance());
    register(IssueKeyAttributeType.getInstance());
    register(IssueWorklogStatusType.getInstance());
  }

}
