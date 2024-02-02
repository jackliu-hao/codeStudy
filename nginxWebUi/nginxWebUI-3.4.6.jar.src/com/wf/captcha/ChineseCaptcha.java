/*    */ package com.wf.captcha;
/*    */ 
/*    */ import com.wf.captcha.base.ChineseCaptchaAbstract;
/*    */ import java.awt.BasicStroke;
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
/*    */ public class ChineseCaptcha
/*    */   extends ChineseCaptchaAbstract {
/*    */   public ChineseCaptcha(int width, int height) {
/* 18 */     this();
/* 19 */     setWidth(width);
/* 20 */     setHeight(height);
/*    */   }
/*    */   public ChineseCaptcha() {}
/*    */   public ChineseCaptcha(int width, int height, int len) {
/* 24 */     this(width, height);
/* 25 */     setLen(len);
/*    */   }
/*    */   
/*    */   public ChineseCaptcha(int width, int height, int len, Font font) {
/* 29 */     this(width, height, len);
/* 30 */     setFont(font);
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
/* 41 */     return graphicsImage(textChar(), out);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toBase64() {
/* 46 */     return toBase64("data:image/png;base64,");
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
/* 58 */       BufferedImage bi = new BufferedImage(this.width, this.height, 1);
/* 59 */       Graphics2D g2d = (Graphics2D)bi.getGraphics();
/*    */       
/* 61 */       g2d.setColor(Color.WHITE);
/* 62 */       g2d.fillRect(0, 0, this.width, this.height);
/*    */       
/* 64 */       g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*    */       
/* 66 */       drawOval(3, g2d);
/*    */       
/* 68 */       g2d.setStroke(new BasicStroke(1.2F, 0, 2));
/* 69 */       drawBesselLine(1, g2d);
/*    */       
/* 71 */       g2d.setFont(getFont());
/* 72 */       FontMetrics fontMetrics = g2d.getFontMetrics();
/* 73 */       int fW = this.width / strs.length;
/* 74 */       int fSp = (fW - (int)fontMetrics.getStringBounds("王", g2d).getWidth()) / 2; int i;
/* 75 */       for (i = 0; i < strs.length; i++) {
/* 76 */         g2d.setColor(color());
/* 77 */         int fY = this.height - (this.height - (int)fontMetrics.getStringBounds(String.valueOf(strs[i]), g2d).getHeight() >> 1);
/* 78 */         g2d.drawString(String.valueOf(strs[i]), i * fW + fSp + 3, fY - 3);
/*    */       } 
/* 80 */       g2d.dispose();
/* 81 */       ImageIO.write(bi, "png", out);
/* 82 */       out.flush();
/* 83 */       i = 1; return i;
/* 84 */     } catch (IOException e) {
/* 85 */       e.printStackTrace();
/*    */     } finally {
/*    */       try {
/* 88 */         out.close();
/* 89 */       } catch (IOException e) {
/* 90 */         e.printStackTrace();
/*    */       } 
/*    */     } 
/* 93 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\wf\captcha\ChineseCaptcha.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */