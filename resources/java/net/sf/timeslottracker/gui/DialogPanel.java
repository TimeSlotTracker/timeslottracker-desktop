package net.sf.timeslottracker.gui;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

/**
 * Helps constructing nice dialog panel and windows providing easy framework.
 * Extends <code>JPanel</code> so can be used anywhere where <code>JPanel</code>
 * can be. Makes use of <code>GridBagConstraints</code> divided to max two
 * columns surrounded with a thin border around all components to provide nice
 * look to end-user. Consists of several methods <code>addRow</code> to provide
 * easy framework to developers. Thanks to this class you have identical layout
 * for all app dialog windows as well as easy way to modify them.
 * 
 * File version: $Revision: 998 $, $Date: 2009-08-20 03:29:08 +0700 (Thu, 20 Aug
 * 2009) $ Last change: $Author: cnitsa $
 */
public class DialogPanel extends JPanel {

  /**
   * Used for update text for created components
   */
  public interface Updater {
    /**
     * Update with given text
     * 
     * @param text
     *          not null text
     */
    void updateText(String text);
  }

  /**
   * Layout manager this class uses for arranging components.
   */
  private GridBagLayout layout;

  /**
   * Constraints for first column (if second one is used). Is assumed that all
   * first columns (if second ones are used) contains label for a component
   * located on its right and have all the same constraints.
   */
  private GridBagConstraints labelConstraints;

  /**
   * Constraints for last column used in every row. It could be a component like
   * a text field or comboBox. It could be even another <code>JPanel</code> if
   * you want. Every last component in a row uses the same constraints.
   */
  private GridBagConstraints componentConstraints;

  /**
   * Default constructor uses <code>GridBagConstraints.BOTH</code> as constraint
   * for <code>GridBagConstraints.fill</code> variable and <code>1.0</code> as
   * <code>GridBagConstraints.weigthY</code>. It is useful if you want to
   * construct panel which will resize all its components in every direction.
   * <p>
   * If you prefer to make "properties" dialog, when fields have normal height
   * you would prefer the method of constructing DialogPanel:
   * <code>DialogPanel dialogPanel 
   *    = new DialogPanel(GridBagConstraints.HORIZONTAL, 0.0 );</code> which
   * means the components will fill the whole with parent has and they will not
   * resize their heights.
   */
  public DialogPanel() {
    this(GridBagConstraints.BOTH, 1.0);
  }

  /**
   * Construct dialog with the ability of control a behavior of added
   * components. For more information read the default constructor description
   * <code>DialogPanel</code>
   * 
   * @param fillType
   *          valid: <code>GridBagConstraints.HORIZONTAL</code>,
   *          <code>GridBagConstraints.VERTICAL</code>,
   *          <code>GridBagConstraints.BOTH</code>
   * @param weigthY
   *          Specifies how to distribute extra vertical space. Give 0.0 - for
   *          no extra space up to 1.0 to 100% resizing.
   * 
   * @see #DialogPanel()
   */
  public DialogPanel(int fillType, double weigthY) {
    super();
    layout = new GridBagLayout();
    setLayout(layout);
    labelConstraints = new GridBagConstraints();
    labelConstraints.fill = fillType;
    labelConstraints.weightx = 0.0;
    labelConstraints.weighty = weigthY;
    labelConstraints.insets = new Insets(3, 5, 3, 5);
    labelConstraints.anchor = GridBagConstraints.NORTHWEST;
    componentConstraints = new GridBagConstraints();
    componentConstraints.fill = fillType;
    componentConstraints.weightx = 1.0;
    componentConstraints.weighty = weigthY;
    componentConstraints.gridwidth = GridBagConstraints.REMAINDER;
    componentConstraints.insets = new Insets(3, 5, 3, 5);
    componentConstraints.anchor = GridBagConstraints.NORTHWEST;
    setBorder(new CompoundBorder(new EmptyBorder(5, 5, 5, 5), new EtchedBorder(
        EtchedBorder.RAISED)));
  }

  public void addRow(String label, Component component, double weightY) {
    JLabel description = new JLabel(label, JLabel.LEFT);
    addRow(description, component, weightY);
  }

  /**
   * Appends a new row to panel consisted of label and component (to edit).
   * Given <code>String</code> is converted to JLabel and then it is used to
   * call <code>addRow(Component first, Component component)</code>.
   * 
   * @param label
   *          shor description of right component (label)
   * @param component
   *          component to put on the right. It can be simply
   *          <code>JTextField</code>, <code>JCheckBox</code> or any other
   *          component, even <code>JPanel</code> if you want.
   */
  public void addRow(String label, Component component) {
    JLabel description = new JLabel(label, JLabel.LEFT);
    addRow(description, component);
  }

  /**
   * Appends a new row to panel consisted of label. Given <code>String</code> is
   * converted to JLabel and then it is used to call
   * <code>addRow(Component component)</code>.
   * 
   * @param label
   *          string to be converted to JLabel component
   */
  public void addRow(String label) {
    JLabel left = new JLabel(label, JLabel.LEFT);
    addRow(left);
  }

  /**
   * Adds a title component. It has a special looking.
   * <p>
   * Given <code>String</code> is converted to JLabel and then it is used to
   * call <code>addRow(Component component)</code>. There is also added some
   * font/color manipulations.
   * <p>
   * The title is added as a next row, so if you want it to be a really a title,
   * you <b>must</b> call it before any other <code>addRow</code> method.
   * 
   * @param title
   *          string to be used as a title
   */
  public void addTitle(String title) {
    addTitleWithUpdater(title);
  }

  /**
   * Adds title component.
   * 
   * @return updater for created component
   * @see DialogPanel#addTitle(String)
   */
  public Updater addTitleWithUpdater(String title) {
    final JLabel comp = new JLabel(title, JLabel.CENTER);
    Font fontUsed = comp.getFont();
    comp.setFont(fontUsed.deriveFont(Font.BOLD));
    addRow(comp);
    return new Updater() {
      @Override
      public void updateText(String arg0) {
        comp.setText(arg0);
      }
    };
  }

  /**
   * Appends a new row to panel consisted of two components.
   * 
   * @param left
   *          component used mostly as a label, but it can be anything.
   * @param right
   *          mostly editable component like text field or check box.
   */
  public void addRow(Component left, Component right) {
    addRow(left, right, this.componentConstraints.weighty);
  }

  public void addRow(Component left, Component right, double weightY) {
    layout.setConstraints(left, labelConstraints);
    add(left);
    componentConstraints.weighty = weightY;
    layout.setConstraints(right, componentConstraints);
    add(right);
  }

  /**
   * Appends a new row to panel consisted of <b>only one</b> component. Mostly
   * used to add row with complicated panel. This component will occupy both
   * columns.
   * 
   * @param component
   *          component we want to put on whole width
   */
  public void addRow(Component component) {
    layout.setConstraints(component, componentConstraints);
    add(component);
  }

  /**
   * Appends some components in a way it fills all the rest place to the parent
   * view (panel). It is also helpful if you make "properties" dialog to fill
   * the rest place with even empty JLabel.
   * 
   * @param component
   *          a component we want to put to fill all the rest space.
   */
  public void fillToEnd(Component component) {
    GridBagConstraints toEnd = new GridBagConstraints();
    toEnd.fill = GridBagConstraints.BOTH;
    toEnd.weightx = 1.0;
    toEnd.weighty = 1.0;
    toEnd.gridwidth = GridBagConstraints.REMAINDER;
    toEnd.insets = new Insets(3, 5, 3, 5);
    toEnd.anchor = GridBagConstraints.NORTHWEST;
    layout.setConstraints(component, toEnd);
    add(component);
  }

}
