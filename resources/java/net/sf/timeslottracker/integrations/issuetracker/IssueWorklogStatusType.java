package net.sf.timeslottracker.integrations.issuetracker;

import net.sf.timeslottracker.data.AttributeType;
import net.sf.timeslottracker.data.IntegerAttribute;

/**
 * Attribute type for issue work log status
 * 
 * @version File version: $Revision: 923 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class IssueWorklogStatusType extends AttributeType {
  private static IssueWorklogStatusType INSTANCE;
  static {
    INSTANCE = new IssueWorklogStatusType();
  }

  /** do not rename - will be persisted to xml */
  private static final String NAME = "ISSUE-WORKLOG-STATUS";

  IssueWorklogStatusType() {
    super(new IntegerAttribute());

    setName(NAME);
    setDescription("Status of issue's worklog");
    setDefault("");
    setUsedInTimeSlots(true);
    setBuiltin(true);
  }

  public static IssueWorklogStatusType getInstance() {
    return INSTANCE;
  }
}
