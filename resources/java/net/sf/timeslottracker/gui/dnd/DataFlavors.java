package net.sf.timeslottracker.gui.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TimeSlot;

/**
 * DataFlavors constants
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class DataFlavors {
  public static final DataFlavor TIME_SLOT = new DataFlavor(TimeSlot.class,
      "timeslot object");

  public static final DataFlavor TASK = new DataFlavor(Task.class,
      "task object");

  /**
   * Gets data object from transferable with given dataFlavor
   * <p>
   * This method exist only for wrapping exections
   * 
   * @return data object, maybe null
   */
  public static Object getTransferData(Transferable data, DataFlavor dataFlavor) {
    try {
      return data.getTransferData(dataFlavor);
    } catch (UnsupportedFlavorException e) {
      throw new AssertionError(e);
    } catch (IOException e) {
      throw new AssertionError(e);
    }
  }

  /**
   * Contains selected dataFlavor in transferFlavors list
   * 
   * @return true - contains, false - otherwise
   */
  public static boolean contains(DataFlavor dataFlavor,
      DataFlavor[] transferFlavors) {
    for (int i = 0; i < transferFlavors.length; i++) {
      if (dataFlavor == transferFlavors[i]) {
        return true;
      }
    }
    return false;
  }

  /**
   * Mentioned array has atleast one common DataFlavor
   * 
   * @param transferFlavors1
   *          first array of DataFlavor
   * @param transferFlavors2
   *          second array of DataFlavor
   * @return true - contains, false - otherwise
   */
  public static boolean contains(DataFlavor[] transferFlavors1,
      DataFlavor[] transferFlavors2) {
    for (int i = 0; i < transferFlavors1.length; i++) {
      DataFlavor dataFlavor = transferFlavors1[i];

      for (int j = 0; j < transferFlavors2.length; j++) {
        DataFlavor flavor = transferFlavors2[j];
        if (dataFlavor == flavor) {
          return true;
        }
      }
    }
    return false;
  }
}
