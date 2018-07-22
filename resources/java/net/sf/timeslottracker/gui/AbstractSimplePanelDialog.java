package net.sf.timeslottracker.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.utils.SwingUtils;

/**
 * Abstract dialog for simple creating different dialogs.
 * 
 * @version File version: $Revision: 1026 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
@SuppressWarnings("serial")
public abstract class AbstractSimplePanelDialog extends JDialog {

  /**
   * Action used when a user chooses close button.
   */
  private class CancelAction implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      dispose();
    }
  }

  private final LayoutManager layoutManager;

  private JButton closeButton;

  public AbstractSimplePanelDialog(LayoutManager layoutManager, String title) {
    super(layoutManager.getTimeSlotTracker().getRootFrame(), title, true);

    this.layoutManager = layoutManager;
  }

  public final void activate() {
    createDialog();
    setVisible(true);
  }

  public LayoutManager getLayoutManager() {
    return layoutManager;
  }

  protected void beforeShow() {
    // default implementation
  }

  protected final String coreString(String key) {
    return layoutManager.getCoreString(key);
  }

  protected final Configuration getConfiguration() {
    return layoutManager.getTimeSlotTracker().getConfiguration();
  }
  
  protected final ImageIcon icon(String iconName) {
    return layoutManager.getIcon(iconName);
  }

  protected final String string(String key) {
    return layoutManager.getString(key);
  }

  protected JPanel createButtons() {
    FlowLayout layout = new FlowLayout(FlowLayout.CENTER);
    JPanel buttons = new JPanel(layout);

    closeButton = new JButton(coreString("aboutDialog.button.Close"));
    CancelAction cancelAction = new CancelAction();
    closeButton.addActionListener(cancelAction);
    closeButton.setIcon(icon("cancel"));
    buttons.add(closeButton);

    // connect cancelAction with ESC key
    getRootPane().registerKeyboardAction(cancelAction,
        KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
        JComponent.WHEN_IN_FOCUSED_WINDOW);

    // add if given
    for (JButton jButton : getButtons()) {
      buttons.add(jButton);
    }

    return buttons;
  }

  protected JButton getDefaultButton() {
    return closeButton;
  }

  protected Collection<JButton> getButtons() {
    return Collections.EMPTY_SET;
  }

  protected abstract void fillDialogPanel(DialogPanel panel);

  protected final JLabel label(String string) {
    JLabel label = new JLabel(string);
    label.setVerticalAlignment(JLabel.TOP);
    return label;
  }

  protected final JLabel linkLabel(final String url) {
    JLabel link = label(wrapAsAnchor(url));
    link.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        try {
          SwingUtils.browse(new URI(url));
        } catch (URISyntaxException e1) {
          layoutManager.getTimeSlotTracker().errorLog(e1);
        }
      }
    });

    return link;
  }

  @Override
  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSED) {
      SwingUtils.saveLocation(this);
      SwingUtils.saveWidthHeight(this);
    }
    super.processWindowEvent(e);
  }

  /**
   * Creating simple JTextArea component.
   * 
   * @param text
   *          text to set into text area
   * @param background
   *          background of the JTextArea. If <code>null</code> it's not set.
   * @return created object
   */
  protected final JTextArea textArea(String text, Color background) {
    return textArea(text, background, false, true);
  }

  /**
   * Creating simple JTextArea Component with controlling the background,
   * editable and lineWrap attributes.
   * 
   * @param text
   *          text to set in text area
   * @param background
   *          background to set (if not <code>null</code>.
   * @param editable
   *          <code>true</code> if this field should be editable
   * @param lineWrap
   *          <code>true</code> if this lines should be automatically wrapped.
   * @return created JTextArea object
   */
  protected final JTextArea textArea(String text, Color background,
      boolean editable, boolean lineWrap) {
    JTextArea textArea = new JTextArea(text);
    textArea.setEditable(editable);
    textArea.setLineWrap(lineWrap);
    textArea.setWrapStyleWord(true);
    if (background != null) {
      textArea.setBackground(background);
    }
    return textArea;
  }

  protected final String wrapAsAnchor(String url) {
    return "<html><a href=\"" + url + "\">" + url + "</a></html>";
  }

  private void createDialog() {
    getContentPane().setLayout(new BorderLayout());

    DialogPanel dialog = getDialogPanel();

    fillDialogPanel(dialog);

    getContentPane().add(dialog, BorderLayout.CENTER);
    getContentPane().add(createButtons(), BorderLayout.SOUTH);

    getRootPane().setDefaultButton(getDefaultButton());
    setResizable(false);
    SwingUtils.setLocation(this);
    
    beforeShow();

    // setting sizes after beforeShow(), which can contains pack() method
    SwingUtils.setWidthHeight(this, getDefaultWidth(), getDefaultHeight());
  }

  protected DialogPanel getDialogPanel() {
    return new DialogPanel();
  }

  protected int getDefaultHeight() {
    return 550;
  }

  protected int getDefaultWidth() {
    return 550;
  }

}
