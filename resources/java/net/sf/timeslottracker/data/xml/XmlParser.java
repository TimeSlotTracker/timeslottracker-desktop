package net.sf.timeslottracker.data.xml;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Stack;
import java.util.Vector;

import net.sf.timeslottracker.core.TimeSlotTracker;
import net.sf.timeslottracker.data.Attribute;
import net.sf.timeslottracker.data.AttributeCategory;
import net.sf.timeslottracker.data.AttributeType;
import net.sf.timeslottracker.data.DataSource;
import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.data.common.AttributeTypeManager;
import net.sf.timeslottracker.data.common.AttributeTypeManagerImpl;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parser to parse xml file with data.
 * 
 * @version File version: $Revision: 1144 $, $Date: 2009-08-20 03:29:08 +0700
 *          (Thu, 20 Aug 2009) $
 * @author Last change: $Author: cnitsa $
 */
class XmlParser extends DefaultHandler {

  /**
   * holds read characters inside a xml entry. It's written to object in the
   * endElement method.
   */
  private StringBuffer characters = new StringBuffer();

  private DataSource dataSource;

  private SimpleDateFormat dateFormater;

  /** holds a favourite list **/
  private Collection<Task> favourites = new Vector<Task>();

  /** Holds last created attribute **/
  private Attribute lastAttribute;

  /** Holds last created timeslot **/
  private TimeSlot lastTimeSlot;

  /** Stack to recreate the task hierarchy **/
  private Stack<Task> stack = new Stack<Task>();

  private AttributeTypeManager attributeTypeManager = AttributeTypeManagerImpl
      .getInstance();

  /**
   * Access to main application interface, mainly used for internationalization
   * needs.
   */
  private TimeSlotTracker timeSlotTracker;

  XmlParser(TimeSlotTracker timeSlotTracker, DataSource dataSource) {
    super();
    this.timeSlotTracker = timeSlotTracker;
    this.dataSource = dataSource;
    Locale locale = timeSlotTracker.getLocale();
    dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm", locale);
    dateFormater.setLenient(false);
  }

  public void characters(char[] ch, int start, int length) throws SAXException {
    // String txt = new String(ch, start, length);
    // System.out.println("\n\nTEKST: ["+txt+"]\n\n");
    characters.append(ch, start, length);
  }

  public void endElement(String uri, String localName, String qName)
      throws SAXException {
    String tag = qName;
    if (tag.equals("task") || tag.equals("root")) {
      stack.pop();
    }
    if ((tag.equals("attribute") || tag.equals("timeslotAttribute"))
        && characters.length() > 0 && lastAttribute != null) {
      lastAttribute.set(characters.toString());
    }
  }

  public void error(SAXParseException e) throws SAXException {
    timeSlotTracker.errorLog(e);
    throw new SAXException(e);
  }

  public void fatalError(SAXParseException e) throws SAXException {
    timeSlotTracker.errorLog(e);
    throw new SAXException(e);
  }

  public void startElement(String uri, String localName, String qName, // qualified
      // name
      Attributes attributes) throws SAXException {
    String tag = qName;

    String taskId = null;
    Integer id = null;
    String timeslotIndex = null;
    String name = null;
    String description = null;
    boolean hidden = false;
    String start = null;
    String stop = null;
    String timeslotId = null;
    Integer tId = null;
    Date startDate = null;
    Date stopDate = null;
    characters.setLength(0); // reset the length to 0 to store the entry of the
    // tag

    if (attributes != null) {
      taskId = attributes.getValue("taskId");
      timeslotIndex = attributes.getValue("timeslotIndex");
      name = attributes.getValue("name");
      description = attributes.getValue("description");
      timeslotId = attributes.getValue("timeslotId");
      start = attributes.getValue("start");
      stop = attributes.getValue("stop");
      hidden = Boolean.parseBoolean(attributes.getValue("hidden"));
    }

    // if taskId is given remove leading "_" from it and convert to Integer
    if (taskId != null && taskId.length() > 1) {
      taskId = taskId.substring(1);
      try {
        id = new Integer(taskId);
      } catch (NumberFormatException e) {
        Object[] msgArgs = { taskId, name };
        String errorMsg = timeSlotTracker.getString(
            "datasource.xml.parser.NumberFormatException.taskId", msgArgs);
        timeSlotTracker.errorLog(errorMsg);
        timeSlotTracker.errorLog(e);
      }
    }

    // parse timeslotId if given
    if (timeslotId != null) {
      try {
        tId = new Integer(timeslotId);
      } catch (NumberFormatException e) {
        Object[] msgArgs = { timeslotId, name };
        String errorMsg = timeSlotTracker.getString(
            "datasource.xml.parser.NumberFormatException.timeslotId", msgArgs);
        timeSlotTracker.errorLog(errorMsg);
        timeSlotTracker.errorLog(e);
      }
    }

    // parse start and stop date
    if (start != null) {
      try {
        startDate = dateFormater.parse(start);
      } catch (ParseException e) {
        Object[] msgArgs = { start, name };
        String errorMsg = timeSlotTracker.getString(
            "datasource.xml.parser.date.ParseException.start", msgArgs);
        timeSlotTracker.errorLog(errorMsg);
        timeSlotTracker.errorLog(e);
      }
    }
    if (stop != null) {
      try {
        stopDate = dateFormater.parse(stop);
      } catch (ParseException e) {
        Object[] msgArgs = { start, name };
        String errorMsg = timeSlotTracker.getString(
            "datasource.xml.parser.date.ParseException.stop", msgArgs);
        timeSlotTracker.errorLog(errorMsg);
        timeSlotTracker.errorLog(e);
      }
    }

    // display debug info
    //Object[] msgArgs = { tag, name, description, hidden };
    // String debugMsg = timeSlotTracker.getString(
    // "datasource.xml.parser.method.startElement.debug", msgArgs);
    // timeSlotTracker.debugLog(debugMsg);

    if (tag.equals("task")) {
      Task parentTask = stack.peek();
      Task newTask = dataSource.createTask(parentTask, id, name, description,
          hidden);
      stack.push(newTask);
    } else if (tag.equals("root")) {
      Task root = dataSource.createTask(null, id, name, description, false);
      dataSource.setRoot(root);
      stack.push(root);
    } else if (tag.equals("timeslot")) {
      Task parentTask = stack.peek();
      TimeSlot newTimeSlot = dataSource.createTimeSlot(parentTask, tId,
          startDate, stopDate, description);
      lastTimeSlot = newTimeSlot;
    } else if (tag.equals("attributeType")) {
      try {
        // read dictionary of attribute-types
        String categoryClass = attributes.getValue("category");
        // String name = attributes.getValue("name"); -> got earlier
        // String description = attributes.getValue("description"); -> got
        // earlier
        String defaultValue = attributes.getValue("defaultValue");
        Boolean usedInTasks = new Boolean(attributes.getValue("usedInTasks"));
        Boolean usedInTimeSlots = new Boolean(
            attributes.getValue("usedInTimeSlots"));
        Boolean hiddenOnReports = new Boolean(
            attributes.getValue("hiddenOnReports"));
        Boolean showInTaskInfo = new Boolean(
            attributes.getValue("showInTaskInfo"));
        Boolean showInTimeSlots = new Boolean(
            attributes.getValue("showInTimeSlots"));
        Boolean autoAddToTimeSlots = new Boolean(
                attributes.getValue("autoAddToTimeSlots"));
        AttributeCategory category = (AttributeCategory) Class.forName(
            categoryClass).newInstance();
        category.setLayoutManager(timeSlotTracker.getLayoutManager());
        AttributeType attributeType = new AttributeType(category);
        attributeType.setName(name);
        attributeType.setDescription(description);
        attributeType.setDefault(defaultValue);
        attributeType.setUsedInTasks(usedInTasks);
        attributeType.setUsedInTimeSlots(usedInTimeSlots);
        attributeType.setHiddenOnReports(hiddenOnReports);
        attributeType.setShowInTaskInfo(showInTaskInfo);
        attributeType.setShowInTimeSlots(showInTimeSlots);
        attributeType.setAutoAddToTimeSlots(autoAddToTimeSlots);
        attributeTypeManager.register(attributeType);
      } catch (Exception e) {
        System.err.println("Exception: " + e);
        timeSlotTracker.errorLog(e);
        throw new SAXException(e);
      }
    } else if (tag.equals("attribute")) {
      // read task's attributes
      Task parentTask = stack.peek();
      String type = attributes.getValue("type");
      AttributeType attributeType = attributeTypeManager.get(type);
      if (attributeType == null) {
        throw new SAXException("Cannot find attributeType with type [" + type
            + "]");
      }
      Attribute attribute = new Attribute(attributeType);
      lastAttribute = attribute;
      Collection<Attribute> collection = parentTask.getAttributes();
      collection.add(attribute);
    } else if (tag.equals("timeslotAttribute")) {
      // read timeSlot's attribute
      String type = attributes.getValue("type");
      AttributeType attributeType = attributeTypeManager.get(type);
      if (attributeType == null) {
        throw new SAXException("Cannot find attributeType with type [" + type
            + "]");
      }
      Attribute attribute = new Attribute(attributeType);
      lastAttribute = attribute;
      Collection<Attribute> collection = lastTimeSlot.getAttributes();
      collection.add(attribute);
    } else if (tag.equals("active") && timeslotIndex != null) {
      try {
        int index = Integer.parseInt(timeslotIndex);
        Task task = dataSource.getTask(id);
        Vector timeslots = new Vector(task.getTimeslots()); // TODO now we have
        // persistent
        // timeslotId,
        // should use
        TimeSlot timeslot = (TimeSlot) timeslots.get(index);
        timeSlotTracker.setActiveTimeSlot(timeslot);
      } catch (NumberFormatException e) {
        Object[] args = { timeslotIndex };
        String errorMsg = timeSlotTracker.getString(
            "datasource.xml.parser.NumberFormatException.active", args);
        timeSlotTracker.errorLog(errorMsg);
        timeSlotTracker.errorLog(e);
      }
    } else if (tag.equals("favourites") && id != null) {
      Task task = dataSource.getTask(id);
      favourites.add(task);
    }
  }

  public void warning(SAXParseException e) throws SAXException {
    timeSlotTracker.errorLog(e);
  }

  Collection<Task> getFavourites() {
    return favourites;
  }

}
