package net.sf.timeslottracker.gui.dnd.selections;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;

import net.sf.timeslottracker.gui.dnd.DataFlavors;

/**
 * Transferable wrapper for {@link net.sf.timeslottracker.data.Task}
 * 
 * <p>
 * Stores task id object
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class TaskSelection implements Serializable, Transferable {
  public static final DataFlavor[] DATA_FLAVOR = new DataFlavor[] {
      DataFlavors.TASK, DataFlavor.stringFlavor };

  private final Object taskId;
  private final String stringRepresentation;

  public TaskSelection(Object taskId, String stringRepresentation) {
    this.taskId = taskId;
    this.stringRepresentation = stringRepresentation;
  }

  /**
   * @return string representation of task
   */
  public String getStringRepresentation() {
    return stringRepresentation;
  }

  public Object getTransferData(DataFlavor flavor)
      throws UnsupportedFlavorException, IOException {
    if (flavor == DataFlavors.TASK) {
      return taskId;
    } else if (flavor == DataFlavor.stringFlavor) {
      return stringRepresentation;
    }

    return null;
  }

  public DataFlavor[] getTransferDataFlavors() {
    return DATA_FLAVOR;
  }

  public boolean isDataFlavorSupported(DataFlavor flavor) {
    return DataFlavors.contains(flavor, getTransferDataFlavors());
  }
}
