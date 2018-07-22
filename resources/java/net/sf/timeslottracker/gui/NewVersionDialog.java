package net.sf.timeslottracker.gui;

import net.sf.timeslottracker.updateversion.VersionInfo;

/**
 * New version dialog.
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-08-23 18:01:57 +0700
 *          (Sun, 23 Aug 2009) $
 * @author Last change: $Author: cnitsa $
 */
@SuppressWarnings("serial")
public class NewVersionDialog extends AbstractSimplePanelDialog {

  private final VersionInfo versionInfo;

  public NewVersionDialog(LayoutManager layoutManager, VersionInfo versionInfo) {
    super(layoutManager, layoutManager.getCoreString("newVersionDialog.title"));

    this.versionInfo = versionInfo;
  }

  @Override
  protected void fillDialogPanel(DialogPanel panel) {
    panel.addRow(coreString("newVersionDialog.version"),
        label(versionInfo.getVersion()));
    panel.addRow(coreString("newVersionDialog.released"),
        label(versionInfo.getReleaseDate()));
    panel.addRow(coreString("newVersionDialog.releaseNotesLink"),
        linkLabel(versionInfo.getNotesLink()));
    panel.addRow(coreString("newVersionDialog.releaseFilesLink"),
        linkLabel(versionInfo.getFilesLink()));
  }

  @Override
  protected void beforeShow() {
    setSize(450, 170);
    setResizable(true);
    pack();
  }

}
