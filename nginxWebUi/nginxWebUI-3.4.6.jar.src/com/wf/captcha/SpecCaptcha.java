/*    */ package com.wf.captcha;
/*    */ 
/*    */ import com.wf.captcha.base.Captcha;
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
/*    */ 
/*    */ public class SpecCaptcha
/*    */   extends Captcha
/*    */ {
/*    */   public SpecCaptcha() {}
/*    */   
/*    */   public SpecCaptcha(int width, int height) {
/* 22 */     this();
/* 23 */     setWidth(width);
/* 24 */     setHeight(height);
/*    */   }
/*    */   
/*    */   public SpecCaptcha(int width, int height, int len) {
/* 28 */     this(width, height);
/* 29 */     setLen(len);
/*    */   }
/*    */   
/*    */   public SpecCaptcha(int width, int height, int len, Font font) {
/* 33 */     this(width, height, len);
/* 34 */     setFont(font);
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
/* 45 */     return graphicsImage(textChar(), out);
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
/* 72 */       g2d.setStroke(new BasicStroke(2.0F, 0, 2));
/* 73 */       drawBesselLine(1, g2d);
/*    */       
/* 75 */       g2d.setFont(getFont());
/* 76 */       FontMetrics fontMetrics = g2d.getFontMetrics();
/* 77 */       int fW = this.width / strs.length;
/* 78 */       int fSp = (fW - (int)fontMetrics.getStringBounds("W", g2d).getWidth()) / 2; int i;
/* 79 */       for (i = 0; i < strs.length; i++) {
/* 80 */         g2d.setColor(color());
/* 81 */         int fY = this.height - (this.height - (int)fontMetrics.getStringBounds(String.valueOf(strs[i]), g2d).getHeight() >> 1);
/* 82 */         g2d.drawString(String.valueOf(strs[i]), i * fW + fSp + 3, fY - 3);
/*    */       } 
/* 84 */       g2d.dispose();
/* 85 */       ImageIO.write(bi, "png", out);
/* 86 */       out.flush();
/* 87 */       i = 1; return i;
/* 88 */     } catch (IOException e) {
/* 89 */       e.printStackTrace();
/*    */     } finally {
/*    */       try {
/* 92 */         out.close();
/* 93 */       } catch (IOException e) {
/* 94 */         e.printStackTrace();
/*    */       } 
/*    */     } 
/* 97 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\wf\captcha\SpecCaptcha.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */