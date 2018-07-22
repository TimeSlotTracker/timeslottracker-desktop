package net.sf.timeslottracker.gui.dnd.selections;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;

import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.gui.dnd.DataFlavors;

/**
 * Transferable wrapper for {@link TimeSlot}
 * 
 * <p>
 * Stores {@link TimeSlotTransferData} object
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class TimeSlotSelection implements Serializable, Transferable {
  public static final DataFlavor[] DATA_FLAVOR = new DataFlavor[] {
      DataFlavors.TIME_SLOT, DataFlavor.stringFlavor };

  private final TimeSlotTransferData transferData;

  public TimeSlotSelection(TimeSlotTransferData transferData) {
    this.transferData = transferData;
  }

  public Object getTransferData(DataFlavor flavor)
      throws UnsupportedFlavorException, IOException {
    if (flavor == DataFlavors.TIME_SLOT) {
      return transferData;
    } else if (flavor == DataFlavor.stringFlavor) {
      return transferData.getStringRepresentation();
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
