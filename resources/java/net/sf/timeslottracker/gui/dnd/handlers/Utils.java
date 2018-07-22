package net.sf.timeslottracker.gui.dnd.handlers;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Util's class, contains common routines for DnD logics
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class Utils {
  private static final EmptyClipboardOwner DUMMY_OWNER = new EmptyClipboardOwner();

  private Utils() {
  }

  /**
   * Clear system clipboard
   */
  public static void clearClipboard() {
    Toolkit.getDefaultToolkit().getSystemClipboard()
        .setContents(DUMMY_OWNER, DUMMY_OWNER);
  }

  private static class EmptyClipboardOwner implements ClipboardOwner,
      Transferable {
    DataFlavor[] flavors = new DataFlavor[] {};

    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }

    public Object getTransferData(DataFlavor flavor)
        throws UnsupportedFlavorException, IOException {
      return null;
    }

    public DataFlavor[] getTransferDataFlavors() {
      return flavors;
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
      return false;
    }
  }
}
