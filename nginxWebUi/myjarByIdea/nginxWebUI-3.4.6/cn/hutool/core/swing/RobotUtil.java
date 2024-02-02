package cn.hutool.core.swing;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.swing.clipboard.ClipboardUtil;
import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;

public class RobotUtil {
   private static final Robot ROBOT;
   private static int delay;

   public static Robot getRobot() {
      return ROBOT;
   }

   public static void setDelay(int delayMillis) {
      delay = delayMillis;
   }

   public static int getDelay() {
      return delay;
   }

   public static void mouseMove(int x, int y) {
      ROBOT.mouseMove(x, y);
   }

   public static void click() {
      ROBOT.mousePress(16);
      ROBOT.mouseRelease(16);
      delay();
   }

   public static void rightClick() {
      ROBOT.mousePress(4);
      ROBOT.mouseRelease(4);
      delay();
   }

   public static void mouseWheel(int wheelAmt) {
      ROBOT.mouseWheel(wheelAmt);
      delay();
   }

   public static void keyClick(int... keyCodes) {
      int[] var1 = keyCodes;
      int var2 = keyCodes.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         int keyCode = var1[var3];
         ROBOT.keyPress(keyCode);
         ROBOT.keyRelease(keyCode);
      }

      delay();
   }

   public static void keyPressString(String str) {
      ClipboardUtil.setStr(str);
      keyPressWithCtrl(86);
      delay();
   }

   public static void keyPressWithShift(int key) {
      ROBOT.keyPress(16);
      ROBOT.keyPress(key);
      ROBOT.keyRelease(key);
      ROBOT.keyRelease(16);
      delay();
   }

   public static void keyPressWithCtrl(int key) {
      ROBOT.keyPress(17);
      ROBOT.keyPress(key);
      ROBOT.keyRelease(key);
      ROBOT.keyRelease(17);
      delay();
   }

   public static void keyPressWithAlt(int key) {
      ROBOT.keyPress(18);
      ROBOT.keyPress(key);
      ROBOT.keyRelease(key);
      ROBOT.keyRelease(18);
      delay();
   }

   public static BufferedImage captureScreen() {
      return captureScreen(ScreenUtil.getRectangle());
   }

   public static File captureScreen(File outFile) {
      ImgUtil.write(captureScreen(), outFile);
      return outFile;
   }

   public static BufferedImage captureScreen(Rectangle screenRect) {
      return ROBOT.createScreenCapture(screenRect);
   }

   public static File captureScreen(Rectangle screenRect, File outFile) {
      ImgUtil.write(captureScreen(screenRect), outFile);
      return outFile;
   }

   public static void delay() {
      if (delay > 0) {
         ROBOT.delay(delay);
      }

   }

   static {
      try {
         ROBOT = new Robot();
      } catch (AWTException var1) {
         throw new UtilException(var1);
      }
   }
}
