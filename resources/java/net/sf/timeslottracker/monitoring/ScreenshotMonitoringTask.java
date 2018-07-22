package net.sf.timeslottracker.monitoring;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import net.sf.timeslottracker.core.ActionListener;
import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.core.TimeSlotTracker;
import net.sf.timeslottracker.data.Attribute;
import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.TimeSlotInputDialog;

/**
 * Task for grabbing screenshots on configured time period. Based on poltrex
 * patch for version 1.1.7 (thanks) and adopted for trunk and make more
 * configurable.
 * 
 * @author polrtex, Last change: $Author: zgibek $
 * @version File version: $Revision: 905 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 */
public class ScreenshotMonitoringTask implements ActionListener {

  private static final Logger LOG = Logger
      .getLogger(ScreenshotMonitoringTask.class.getName());
  private final Configuration config;
  private final LayoutManager layoutMgr;
  private final TimeSlotTracker timeSlotTracker;
  private final ScreenshotAttributeType screenshotType;
  private final Font commentFont;

  public ScreenshotMonitoringTask(TimeSlotTracker timeSlotTracker) {
    this.timeSlotTracker = timeSlotTracker;
    this.config = timeSlotTracker.getConfiguration();
    this.layoutMgr = timeSlotTracker.getLayoutManager();
    this.screenshotType = ScreenshotAttributeType.getInstance();
    this.commentFont = new Font("SansSerif", Font.PLAIN, 20);
  }

  public BufferedImage grabScreenshot() {
    LOG.info("Grabbing screenshot...");
    try {
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      Dimension screenSize = toolkit.getScreenSize();
      Robot robot = new Robot();
      BufferedImage image = robot
          .createScreenCapture(new Rectangle(screenSize));
      return image;
    } catch (Throwable ex) {
      LOG.log(Level.SEVERE,
          "Problem during grabbing screenshot: " + ex.getMessage(), ex);
    }
    return null;
  }

  public File saveScreenshot(BufferedImage image, String comment, Date dateTaken) {
    File outputFile = null;
    try {
      // draw the comment and timestamp on the image
      DateFormat sdfTimestamp = new SimpleDateFormat("yyyyMMdd_HHmmss");
      DateFormat sdfImage = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String timeStamp = sdfTimestamp.format(dateTaken);

      Graphics2D g2d = (Graphics2D) image.getGraphics();
      g2d.setComposite(AlphaComposite
          .getInstance(AlphaComposite.SRC_OVER, 0.7f));
      g2d.setFont(commentFont);
      FontMetrics fontMetrics = g2d.getFontMetrics();
      final String dateTakenString = layoutMgr.getCoreString(
          "ScreenshotMonitoringTask.shapshot.taken.at",
          sdfImage.format(dateTaken));
      Rectangle2D dateStringRect = fontMetrics.getStringBounds(dateTakenString,
          g2d);
      Rectangle2D commentRect = fontMetrics.getStringBounds(comment, g2d);
      double height = commentRect.getHeight();

      g2d.setColor(Color.LIGHT_GRAY);
      g2d.fillRoundRect(25, 28 - (int) height,
          (int) dateStringRect.getWidth() + 10, (int) height + 10, 15, 15);
      g2d.fillRoundRect(25, 78 - (int) height,
          (int) commentRect.getWidth() + 10, (int) height + 10, 15, 15);

      g2d.setComposite(AlphaComposite.SrcAtop);
      g2d.setColor(Color.RED);
      g2d.drawString(dateTakenString, 30, 30);
      g2d.drawString(comment, 30, 80);

      File outputDir = new File(config.getString(
          Configuration.MONITORING_IMAGE_DIR, "."));
      outputFile = new File(outputDir, "screenshot_" + timeStamp + ".png");
      LOG.info("Writing  screenshot to file: " + outputFile);
      ImageIO.write(image, "png", outputFile);
    } catch (Throwable ex) {
      LOG.log(Level.SEVERE,
          "Problem during grabbing screenshot: " + ex.getMessage(), ex);
      outputFile = null;
    }
    return outputFile;
  }

  public void actionPerformed(net.sf.timeslottracker.core.Action action) {
    BufferedImage screenshot = null;
    if (config.getBoolean(Configuration.MONITORING_GRABBER_ENABLED,
        Boolean.FALSE)) {
      screenshot = grabScreenshot();
    }

    final Date dateTaken = new Date();
    TimeSlot currTs = timeSlotTracker.getActiveTimeSlot();
    TimeSlotInputDialog dlg = new TimeSlotInputDialog(
        timeSlotTracker.getLayoutManager());
    dlg.activate();
    String newDesc = dlg.getDescription();
    if (currTs == null) {
      if (newDesc != null && newDesc.length() > 0) {
        timeSlotTracker.startTiming(newDesc);
      } else {
        return; // no active timeslot and user didn't want to enter any new
                // desc, so exit.
      }
    } else {
      String currDesc = currTs.getDescription();
      if (newDesc != null && !currDesc.equals(newDesc)) {
        timeSlotTracker.startTiming(newDesc);
      }
    }
    currTs = timeSlotTracker.getActiveTimeSlot();
    if (currTs == null) {
      return; // guard for future changing the implementation and allow to skip
              // creating new timeslot.
    }

    if (screenshot != null) {
      String comment = currTs.getDescription();
      File outputFile = saveScreenshot(screenshot, comment, dateTaken);
      if (outputFile != null) {
        Attribute screenshotAttr = new Attribute(this.screenshotType);
        screenshotAttr.set(outputFile.getAbsolutePath());
        currTs.getAttributes().add(screenshotAttr);
      }
    }

  }
}