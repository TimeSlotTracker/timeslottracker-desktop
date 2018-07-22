package net.sf.timeslottracker.gui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import net.sf.timeslottracker.core.Configuration;

/**
 * About dialog.
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
@SuppressWarnings("serial")
public class AboutDialog extends AbstractSimplePanelDialog {

  private final Configuration configuration;

  private final ImageIcon logo;

  public AboutDialog(LayoutManager layoutManager) {
    super(layoutManager, layoutManager.getCoreString("aboutDialog.title"));

    this.configuration = layoutManager.getTimeSlotTracker().getConfiguration();
    this.logo = layoutManager.getIcon("/users/clock-big.gif");
  }

  @Override
  protected void fillDialogPanel(DialogPanel panelAll) {
    getContentPane().setLayout(new BorderLayout());

    Color background = getContentPane().getBackground();
    JPanel textPanel = new JPanel(new BorderLayout(5, 5));
    String string = coreString("starter.title") + ", "
        + configuration.getVersionString();
    string += "\n\n" + coreString("aboutDialog.text.general.details");
    textPanel.add(textArea(string, background));

    JPanel imagePanel = new JPanel(new BorderLayout(5, 5));
    JLabel comp = new JLabel(logo);
    comp.setVerticalAlignment(JLabel.TOP);
    imagePanel.add(comp);

    DialogPanel commonPanel = new DialogPanel();
    commonPanel.addRow(imagePanel, textPanel);
    commonPanel.addRow(label(coreString("aboutDialog.text.homepage")),
        linkLabel(coreString("aboutDialog.text.homepage.details")));
    commonPanel.addRow(label(coreString("aboutDialog.text.licence")),
        textArea(coreString("aboutDialog.text.licence.details"), background));
    commonPanel.addRow(
        label(coreString("aboutDialog.text.iconsLicence")),
        textArea(coreString("aboutDialog.text.iconsLicence.details"),
            background));
    commonPanel
        .addRow(
            label(coreString("aboutDialog.text.jiraLicence")),
            textArea(coreString("aboutDialog.text.jiraLicence.details"),
                background));
    commonPanel.addRow(label(coreString("aboutDialog.text.crew")),
        textArea(coreString("aboutDialog.text.crew.details"), background));

    DialogPanel librariesPanel = new DialogPanel();
    librariesPanel.addRow(new JScrollPane(textArea(
        coreString("aboutDialog.text.librariesLicense"), background, false,
        false)));

    JTabbedPane jTabbedPane = new JTabbedPane();
    jTabbedPane.add(coreString("aboutDialog.tab.common"), commonPanel);
    jTabbedPane.add(coreString("aboutDialog.tab.libraries"), librariesPanel);

    panelAll.addRow(jTabbedPane);
    panelAll.setBorder(BorderFactory.createEmptyBorder());
  }

  @Override
  protected int getDefaultHeight() {
    return 550;
  }

  @Override
  protected int getDefaultWidth() {
    return 500;
  }

}
