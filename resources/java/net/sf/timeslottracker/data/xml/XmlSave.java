package net.sf.timeslottracker.data.xml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import net.sf.timeslottracker.core.TimeSlotTracker;
import net.sf.timeslottracker.data.Attribute;
import net.sf.timeslottracker.data.AttributeCategory;
import net.sf.timeslottracker.data.AttributeType;
import net.sf.timeslottracker.data.DataSource;
import net.sf.timeslottracker.data.DataSourceException;
import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.data.common.TransactionalFileSaver;
import net.sf.timeslottracker.data.common.TransactionalFileSaverException;
import net.sf.timeslottracker.gui.FavouritesInterface;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.utils.StringUtils;

/**
 * Class to save DataSource data to a xml datafile.
 * <p>
 * It construct the file by its own and is not using any xml tools.
 * <p>
 * For saving xml file it uses {@link TransactionalFileSaver}.
 * 
 * @version File version: $Revision: 1153 $, $Date: 2009-08-06 09:55:17 +0700
 *          (Thu, 06 Aug 2009) $
 * @author Last change: $Author: cnitsa $
 */
class XmlSave {

  private final DataSource dataSource;

  /** Date formatter we will use **/
  private final SimpleDateFormat dateFormater;

  private final String dtdDirectory;

  private final TimeSlotTracker timeSlotTracker;

  private final String fileSeparator;

  private final String xmlFileName;

  private static final String LAST_LINE = "</TimeSlotTracker>";

  XmlSave(DataSource dataSource, String xmlFileName, String dtdDirectory,
      TimeSlotTracker timeSlotTracker) {
    this.dataSource = dataSource;
    this.xmlFileName = xmlFileName;
    this.dtdDirectory = dtdDirectory;
    this.timeSlotTracker = timeSlotTracker;
    Locale locale = timeSlotTracker.getLocale();
    this.dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm", locale);
    this.fileSeparator = System.getProperty("file.separator");
  }

  /**
   * Encodes all quotation into
   * 
   * <pre>
   * &quot;
   * </pre>
   * 
   * tags to make it possible to include it into a attribute.
   */
  private String encode(String text) {
    text = text.replaceAll("&", "&amp;"); // must be the first translation!
    text = text.replaceAll("'", "&apos;");
    text = text.replaceAll("<", "&lt;");
    text = text.replaceAll(">", "&gt;");
    text = text.replaceAll("\"", "&quot;");
    return text = text.replaceAll("\n", "&#10;");
  }

  private void printlnXmlAttribute(PrintWriter writer, String prefix,
      String name, String value) {
    writer.print(prefix);
    writer.print(name);
    writer.print("=\"");
    writer.print(encode(value));
    writer.println("\"");
  }

  private void printXmlAttribute(PrintWriter writer, String name, String value) {
    writer.print(" ");
    writer.print(name);
    writer.print("=\"");
    writer.print(encode(value));
    writer.print("\"");
  }

  private void saveActive(PrintWriter writer) throws Exception {
    TimeSlot timeslot = timeSlotTracker.getActiveTimeSlot();
    if (timeslot != null) {
      Task task = timeslot.getTask();
      writer.print("  <active taskId=\"_");
      writer.print(task.getId());
      writer.print("\" timeslotIndex=\"");
      Vector<TimeSlot> vector = new Vector<TimeSlot>(task.getTimeslots());
      int index = vector.indexOf(timeslot);
      writer.print(index);
      writer.print("\"");
      writer.println(" />");
    }
  }

  /**
   * Saves all task's attributes.
   */
  private void saveAttributes(PrintWriter writer, Task task, String prefix)
      throws Exception {
    Collection<Attribute> attributesCollection = task.getAttributes();
    if (attributesCollection == null) {
      return;
    }
    Iterator<Attribute> attributes = attributesCollection.iterator();
    while (attributes.hasNext()) {
      Attribute attribute = attributes.next();
      writer.print(prefix);
      writer.print("<attribute");
      String type = attribute.getAttributeType().getName();
      printXmlAttribute(writer, "type", type);
      writer.print(" ><![CDATA[");
      String value = "";
      if (attribute.get() != null) {
        value = attribute.get().toString();
      }
      writer.print(value);
      writer.println("]]></attribute>");
    }
  }

  /**
   * Saves all time slot's attributes.
   */
  private void saveAttributes(PrintWriter writer, TimeSlot timeSlot,
      String prefix) throws Exception {
    Iterator<Attribute> attributes = timeSlot.getAttributes().iterator();
    while (attributes.hasNext()) {
      Attribute attribute = attributes.next();
      writer.print(prefix);
      writer.print("<timeslotAttribute");
      String type = attribute.getAttributeType().getName();
      printXmlAttribute(writer, "type", type);
      writer.print(" ><![CDATA[");
      String value = "";
      if (attribute.get() != null) {
        value = attribute.get().toString();
      }
      writer.print(value);
      writer.println("]]></timeslotAttribute>");
    }
  }

  private void saveAttributeTypes(PrintWriter writer) throws Exception {
    LayoutManager layoutManager = timeSlotTracker.getLayoutManager();
    if (layoutManager == null) {
      return;
    }
    Collection<AttributeType> collection = dataSource.getAttributeTypes();
    if (collection == null) {
      return;
    }
    Iterator<AttributeType> attributeTypes = collection.iterator();
    while (attributeTypes.hasNext()) {
      AttributeType type = attributeTypes.next();
      if (type.isBuiltin()) {
        continue;
      }

      AttributeCategory categoryClass = type.getCategory();
      String categoryClassName = categoryClass.getClass().getName();

      writer.println("  <attributeType");
      printlnXmlAttribute(writer, "     ", "category", categoryClassName);
      printlnXmlAttribute(writer, "     ", "name", type.getName());
      printlnXmlAttribute(writer, "     ", "description", type.getDescription());
      printlnXmlAttribute(writer, "     ", "defaultValue", type.getDefault());
      printlnXmlAttribute(writer, "     ", "usedInTasks",
          "" + type.getUsedInTasks());
      printlnXmlAttribute(writer, "     ", "usedInTimeSlots",
          "" + type.getUsedInTimeSlots());
      printlnXmlAttribute(writer, "     ", "hiddenOnReports",
          "" + type.isHiddenOnReports());
      printlnXmlAttribute(writer, "     ", "showInTaskInfo",
          "" + type.getShowInTaskInfo());
      printlnXmlAttribute(writer, "     ", "showInTimeSlots",
          "" + type.getShowInTimeSlots());
      printlnXmlAttribute(writer, "     ", "autoAddToTimeSlots",
              "" + type.isAutoAddToTimeSlots());
      writer.println("  />");
    }
  }

  private void saveChildren(PrintWriter writer, Task parentTask, String prefix)
      throws Exception {
    Collection<Task> childrenCollection = dataSource.getChildren(parentTask);
    if (childrenCollection == null)
      return;
    Iterator<Task> children = childrenCollection.iterator();
    while (children.hasNext()) {
      Task child = children.next();
      writer.print(prefix);

      writer.print("<task taskId=\"_" + child.getId() + "\"");
      printXmlAttribute(writer, "name", child.getName());
      printXmlAttribute(writer, "hidden", String.valueOf(child.isHidden()));
      if (!StringUtils.isBlank(child.getDescription())) {
        printXmlAttribute(writer, "description", child.getDescription());
      }
      writer.println(">");

      saveTimeslots(writer, child, prefix + "  ");
      saveAttributes(writer, child, prefix + "  ");
      saveChildren(writer, child, prefix + "  ");

      writer.print(prefix);
      writer.println("</task>");
    }
  }

  private void saveFavourites(PrintWriter writer) throws Exception {
    LayoutManager layoutManager = timeSlotTracker.getLayoutManager();
    if (layoutManager == null) {
      return;
    }
    FavouritesInterface favouritesModule = layoutManager
        .getFavouritesInterface();
    if (favouritesModule == null) {
      return;
    }
    Collection<Task> favouritesCollection = favouritesModule.getFavourites();
    if (favouritesCollection == null) {
      return;
    }
    Iterator<Task> favourites = favouritesCollection.iterator();
    while (favourites.hasNext()) {
      Task task = favourites.next();
      writer.print("  <favourites taskId=\"_");
      writer.print(task.getId());
      writer.println("\" />");
    }
  }

  /**
   * Saves the root into a <code>writer</code> object. Then initiates the
   * recursive chain of saving tasks.
   * 
   * @param writer
   */
  private void saveRoot(PrintWriter writer) throws Exception {
    Task root = dataSource.getRoot();
    writer.print("  <root taskId=\"_" + root.getId() + "\"");

    printXmlAttribute(writer, "name", root.getName());

    if (root.getDescription() != null) {
      printXmlAttribute(writer, "description", root.getDescription());
    }
    writer.println(">");
    saveTimeslots(writer, root, "    ");

    saveChildren(writer, root, "    ");

    writer.println("  </root>");
  }

  /**
   * Saves all timeslots stored in given task. They will be prefixed with a
   * given prefix.
   * 
   * @param writer
   */
  private void saveTimeslots(PrintWriter writer, Task task, String prefix)
      throws Exception {
    Collection<TimeSlot> timeslotsCollection = task.getTimeslots();
    if (timeslotsCollection == null) {
      return;
    }
    Iterator<TimeSlot> timeslots = timeslotsCollection.iterator();
    while (timeslots.hasNext()) {
      TimeSlot timeslot = timeslots.next();
      Date startDate = timeslot.getStartDate();
      Date stopDate = timeslot.getStopDate();

      writer.print(prefix);
      writer.print("<timeslot");
      printXmlAttribute(writer, "timeslotId", timeslot.getId().toString());
      if (startDate != null) {
        printXmlAttribute(writer, "start", dateFormater.format(startDate));
      }
      if (stopDate != null) {
        printXmlAttribute(writer, "stop", dateFormater.format(stopDate));
      }
      if (timeslot.getDescription() != null) {
        printXmlAttribute(writer, "description", timeslot.getDescription());
      }
      if (!timeslot.hasAttributes()) {
        writer.println("/>");
      } else {
        writer.println(">");
        saveAttributes(writer, timeslot, prefix + "  ");
        writer.print(prefix);
        writer.println("</timeslot>");
      }
    }
  }

  /**
   * @throws DataSourceException
   *           error saving data in file
   * @throws TransactionalFileSaverException
   *           error saving data in file
   */
  void saveAll() throws TransactionalFileSaverException, DataSourceException {
    TransactionalFileSaver fileSaver = getFileSaver();
    File tempXmlFile = fileSaver.begin();
    PrintWriter writer = null;
    try {
      writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
          new FileOutputStream(tempXmlFile), "UTF-8")));
      writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      writer.print("<!DOCTYPE TimeSlotTracker SYSTEM \"");
      if (dtdDirectory != null) {
        writer.print(dtdDirectory);
        if (!dtdDirectory.endsWith(fileSeparator)) {
          writer.print(fileSeparator);
        }
      }
      writer.println("timeslottracker.dtd\">");
      writer.println(StringUtils.EMPTY);
      writer.println("<TimeSlotTracker>");
      writer.println(StringUtils.EMPTY);
      saveAttributeTypes(writer);
      writer.println(StringUtils.EMPTY);
      saveRoot(writer);
      writer.println(StringUtils.EMPTY);
      saveActive(writer);
      writer.println(StringUtils.EMPTY);
      saveFavourites(writer);
      writer.println(StringUtils.EMPTY);
      writer.println(LAST_LINE);
    } catch (Exception e) {
      throw new DataSourceException(timeSlotTracker,
          "datasource.xml.XmlSave.Exception", new Object[] { e.getMessage() });
    } finally {
      if (writer != null) {
        writer.close();
      }
    }

    // second: commit saving
    fileSaver.commitWithException();
  }

  /**
   * Check xml file
   */
  public void check() {
    getFileSaver().check();
  }

  private TransactionalFileSaver getFileSaver() {
    return new TransactionalFileSaver(timeSlotTracker, xmlFileName, LAST_LINE);
  }

}
