package net.sf.timeslottracker.data;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Describes an image attribute - represented by a an image in a JLabel
 * 
 * @version File version: $Revision: 1.5 $, $Date: 2007-09-05 17:02:17 $
 * @author Last change: $Author: cnitsa $
 */
public class ImageAttribute extends AttributeCategory {

  private JLabel imagePathLabel;
  private JPanel editorPanel;
  private ImagePanel imagePanel;
  private BufferedImage image;

  /**
   * Constucts this type of attribute with proper editComponent
   */
  public ImageAttribute() {
    super();
    imagePathLabel = new JLabel();
    editorPanel = new JPanel(new BorderLayout());
    imagePanel = new ImagePanel(image);
    editorPanel.add(imagePathLabel, BorderLayout.NORTH);
    editorPanel.add(imagePanel, BorderLayout.CENTER);
  }

  public boolean includeInIndex() {
    return false;
  }

  public Component getEditComponent() {
    return editorPanel;
  }

  public String getString() {
    return imagePathLabel.getText();
  }

  public void beforeShow(Object value, AttributeType type) {
    String newPath = toString(value);
    String currPath = imagePathLabel.getText();
    if (image == null) {
      loadImage(newPath);
    } else {
      if (currPath != null && !currPath.equals(newPath)) {
        loadImage(newPath);
      }
    }
    imagePathLabel.setText(newPath);
    imagePanel.setImage(image);
  }

  public void loadImage(String imgPath) {
    int scaleFactor = 8;

    try {
      image = ImageIO.read(new File(imgPath));

    } catch (IOException ex) {
      Logger.getLogger(ImageAttribute.class.getName()).log(Level.SEVERE, null,
          ex);
      image = null;
    }
  }

  public Object beforeClose() {
    return imagePathLabel.getText();
  }
}

class ImagePanel extends JPanel {
  private static final long serialVersionUID = 3530272191904273534L;

  Image bgImage;

  public ImagePanel(Image bgImage) {
    this.bgImage = bgImage;
  }

  public Image getImage() {
    return bgImage;
  }

  public void setImage(Image bgImage) {
    this.bgImage = bgImage;
  }

  @Override
  public void paintComponent(Graphics g) {
    double scaleX = this.getWidth() / (double) bgImage.getWidth(null);
    double scaleY = this.getHeight() / (double) bgImage.getHeight(null);
    AffineTransform xform = AffineTransform.getScaleInstance(scaleX, scaleY);
    ((Graphics2D) g).drawImage(bgImage, xform, this);
  }

  @Override
  public Dimension getPreferredSize() {
    final int width = (int) (0.55 * bgImage.getWidth(this));
    final int height = (int) (0.55 * bgImage.getHeight(this));
    return new Dimension(width, height);
  }

}
