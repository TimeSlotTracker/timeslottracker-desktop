package net.sf.timeslottracker.gui.layouts.classic.tasksbydays;

/**
 * Node for strings
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
@SuppressWarnings("serial")
public class StringNode extends DaysTreeNode {

  public StringNode(String nodeName) {
    super(nodeName);
  }

  public String getString() {
    return (String) getUserObject();
  }

}
