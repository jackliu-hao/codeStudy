package cn.hutool.core.swing;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;

public class ScreenUtil {
   public static Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();

   public static int getWidth() {
      return (int)dimension.getWidth();
   }

   public static int getHeight() {
      return (int)dimension.getHeight();
   }

   public static Rectangle getRectangle() {
      return new Rectangle(getWidth(), getHeight());
   }

   public static BufferedImage captureScreen() {
      return RobotUtil.captureScreen();
   }

   public static File captureScreen(File outFile) {
      return RobotUtil.captureScreen(outFile);
   }

   public static BufferedImage captureScreen(Rectangle screenRect) {
      return RobotUtil.captureScreen(screenRect);
   }

   public static File captureScreen(Rectangle screenRect, File outFile) {
      return RobotUtil.captureScreen(screenRect, outFile);
   }
}
