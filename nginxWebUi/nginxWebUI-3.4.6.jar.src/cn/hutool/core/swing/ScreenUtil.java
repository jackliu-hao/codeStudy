/*    */ package cn.hutool.core.swing;
/*    */ 
/*    */ import java.awt.Dimension;
/*    */ import java.awt.Rectangle;
/*    */ import java.awt.Toolkit;
/*    */ import java.awt.image.BufferedImage;
/*    */ import java.io.File;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ScreenUtil
/*    */ {
/* 16 */   public static Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static int getWidth() {
/* 24 */     return (int)dimension.getWidth();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static int getHeight() {
/* 33 */     return (int)dimension.getHeight();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Rectangle getRectangle() {
/* 41 */     return new Rectangle(getWidth(), getHeight());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static BufferedImage captureScreen() {
/* 52 */     return RobotUtil.captureScreen();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static File captureScreen(File outFile) {
/* 63 */     return RobotUtil.captureScreen(outFile);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static BufferedImage captureScreen(Rectangle screenRect) {
/* 74 */     return RobotUtil.captureScreen(screenRect);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static File captureScreen(Rectangle screenRect, File outFile) {
/* 86 */     return RobotUtil.captureScreen(screenRect, outFile);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\swing\ScreenUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */