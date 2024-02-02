/*     */ package cn.hutool.core.swing;
/*     */ 
/*     */ import cn.hutool.core.exceptions.UtilException;
/*     */ import cn.hutool.core.img.ImgUtil;
/*     */ import cn.hutool.core.swing.clipboard.ClipboardUtil;
/*     */ import java.awt.AWTException;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.Robot;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.io.File;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RobotUtil
/*     */ {
/*     */   private static final Robot ROBOT;
/*     */   private static int delay;
/*     */   
/*     */   static {
/*     */     try {
/*  28 */       ROBOT = new Robot();
/*  29 */     } catch (AWTException e) {
/*  30 */       throw new UtilException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Robot getRobot() {
/*  41 */     return ROBOT;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setDelay(int delayMillis) {
/*  52 */     delay = delayMillis;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getDelay() {
/*  62 */     return delay;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void mouseMove(int x, int y) {
/*  73 */     ROBOT.mouseMove(x, y);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void click() {
/*  83 */     ROBOT.mousePress(16);
/*  84 */     ROBOT.mouseRelease(16);
/*  85 */     delay();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void rightClick() {
/*  95 */     ROBOT.mousePress(4);
/*  96 */     ROBOT.mouseRelease(4);
/*  97 */     delay();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void mouseWheel(int wheelAmt) {
/* 107 */     ROBOT.mouseWheel(wheelAmt);
/* 108 */     delay();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void keyClick(int... keyCodes) {
/* 119 */     for (int keyCode : keyCodes) {
/* 120 */       ROBOT.keyPress(keyCode);
/* 121 */       ROBOT.keyRelease(keyCode);
/*     */     } 
/* 123 */     delay();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void keyPressString(String str) {
/* 132 */     ClipboardUtil.setStr(str);
/* 133 */     keyPressWithCtrl(86);
/* 134 */     delay();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void keyPressWithShift(int key) {
/* 143 */     ROBOT.keyPress(16);
/* 144 */     ROBOT.keyPress(key);
/* 145 */     ROBOT.keyRelease(key);
/* 146 */     ROBOT.keyRelease(16);
/* 147 */     delay();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void keyPressWithCtrl(int key) {
/* 156 */     ROBOT.keyPress(17);
/* 157 */     ROBOT.keyPress(key);
/* 158 */     ROBOT.keyRelease(key);
/* 159 */     ROBOT.keyRelease(17);
/* 160 */     delay();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void keyPressWithAlt(int key) {
/* 169 */     ROBOT.keyPress(18);
/* 170 */     ROBOT.keyPress(key);
/* 171 */     ROBOT.keyRelease(key);
/* 172 */     ROBOT.keyRelease(18);
/* 173 */     delay();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BufferedImage captureScreen() {
/* 182 */     return captureScreen(ScreenUtil.getRectangle());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static File captureScreen(File outFile) {
/* 192 */     ImgUtil.write(captureScreen(), outFile);
/* 193 */     return outFile;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BufferedImage captureScreen(Rectangle screenRect) {
/* 203 */     return ROBOT.createScreenCapture(screenRect);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static File captureScreen(Rectangle screenRect, File outFile) {
/* 214 */     ImgUtil.write(captureScreen(screenRect), outFile);
/* 215 */     return outFile;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void delay() {
/* 222 */     if (delay > 0)
/* 223 */       ROBOT.delay(delay); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\swing\RobotUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */