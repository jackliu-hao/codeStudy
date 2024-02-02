/*    */ package com.wf.captcha;
/*    */ 
/*    */ import com.wf.captcha.base.ArithmeticCaptchaAbstract;
/*    */ import java.awt.Color;
/*    */ import java.awt.Font;
/*    */ import java.awt.FontMetrics;
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.RenderingHints;
/*    */ import java.awt.image.BufferedImage;
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ import javax.imageio.ImageIO;
/*    */ 
/*    */ 
/*    */ public class ArithmeticCaptcha
/*    */   extends ArithmeticCaptchaAbstract
/*    */ {
/*    */   public ArithmeticCaptcha() {}
/*    */   
/*    */   public ArithmeticCaptcha(int width, int height) {
/* 21 */     this();
/* 22 */     setWidth(width);
/* 23 */     setHeight(height);
/*    */   }
/*    */   
/*    */   public ArithmeticCaptcha(int width, int height, int len) {
/* 27 */     this(width, height);
/* 28 */     setLen(len);
/*    */   }
/*    */   
/*    */   public ArithmeticCaptcha(int width, int height, int len, Font font) {
/* 32 */     this(width, height, len);
/* 33 */     setFont(font);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean out(OutputStream out) {
/* 44 */     checkAlpha();
/* 45 */     return graphicsImage(getArithmeticString().toCharArray(), out);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toBase64() {
/* 50 */     return toBase64("data:image/png;base64,");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private boolean graphicsImage(char[] strs, OutputStream out) {
/*    */     try {
/* 62 */       BufferedImage bi = new BufferedImage(this.width, this.height, 1);
/* 63 */       Graphics2D g2d = (Graphics2D)bi.getGraphics();
/*    */       
/* 65 */       g2d.setColor(Color.WHITE);
/* 66 */       g2d.fillRect(0, 0, this.width, this.height);
/*    */       
/* 68 */       g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*    */       
/* 70 */       drawOval(2, g2d);
/*    */       
/* 72 */       g2d.setFont(getFont());
/* 73 */       FontMetrics fontMetrics = g2d.getFontMetrics();
/* 74 */       int fW = this.width / strs.length;
/* 75 */       int fSp = (fW - (int)fontMetrics.getStringBounds("8", g2d).getWidth()) / 2; int i;
/* 76 */       for (i = 0; i < strs.length; i++) {
/* 77 */         g2d.setColor(color());
/* 78 */         int fY = this.height - (this.height - (int)fontMetrics.getStringBounds(String.valueOf(strs[i]), g2d).getHeight() >> 1);
/* 79 */         g2d.drawString(String.valueOf(strs[i]), i * fW + fSp + 3, fY - 3);
/*    */       } 
/* 81 */       g2d.dispose();
/* 82 */       ImageIO.write(bi, "png", out);
/* 83 */       out.flush();
/* 84 */       i = 1; return i;
/* 85 */     } catch (IOException e) {
/* 86 */       e.printStackTrace();
/*    */     } finally {
/*    */       try {
/* 89 */         out.close();
/* 90 */       } catch (IOException e) {
/* 91 */         e.printStackTrace();
/*    */       } 
/*    */     } 
/* 94 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\wf\captcha\ArithmeticCaptcha.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */