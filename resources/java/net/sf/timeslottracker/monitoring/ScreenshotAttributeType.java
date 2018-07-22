package net.sf.timeslottracker.monitoring;

import net.sf.timeslottracker.data.AttributeType;
import net.sf.timeslottracker.data.ImageAttribute;

/**
 * Attribute type for screenshot purpose
 * 
 * @author Last change: $Author: cnitsa $
 * @version File version: $Revision: 1.1 $, $Date: 2007-04-30 15:47:49 $
 */
public class ScreenshotAttributeType extends AttributeType {
  private static ScreenshotAttributeType INSTANCE;
  static {
    INSTANCE = new ScreenshotAttributeType();
  }

  /**
   * do not rename - will be persisted to xml
   */
  private static final String NAME = "Screenshot";

  private ScreenshotAttributeType() {
    super(new ImageAttribute());

    setName(NAME);
    setDescription("Path to screenshot");
    setDefault("");
    setBuiltin(true);

    setUsedInTimeSlots(true);
  }

  public static ScreenshotAttributeType getInstance() {
    return INSTANCE;
  }
}