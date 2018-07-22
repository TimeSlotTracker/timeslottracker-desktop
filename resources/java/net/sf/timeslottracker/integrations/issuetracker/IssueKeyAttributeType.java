package net.sf.timeslottracker.integrations.issuetracker;

import net.sf.timeslottracker.data.AttributeType;
import net.sf.timeslottracker.data.SimpleTextAttribute;

/**
 * Attribute type for issue key
 * 
 * @version File version: $Revision: 923 $, $Date: 2009-08-20 03:29:08 +0700
 *          (Thu, 20 Aug 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class IssueKeyAttributeType extends AttributeType {
  private static IssueKeyAttributeType INSTANCE;
  static {
    INSTANCE = new IssueKeyAttributeType();
  }

  /** do not rename - will be persisted to xml */
  private static final String NAME = "ISSUE-KEY";

  private IssueKeyAttributeType() {
    super(new SimpleTextAttribute());

    setName(NAME);
    setDescription("Key of issue");
    setDefault("");
    setUsedInTasks(true);
    setBuiltin(true);
  }

  public static IssueKeyAttributeType getInstance() {
    return INSTANCE;
  }
}
