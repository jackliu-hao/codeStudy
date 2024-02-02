/*     */ package com.wf.captcha;
/*     */ 
/*     */ import com.wf.captcha.base.Captcha;
/*     */ import com.wf.captcha.utils.GifEncoder;
/*     */ import java.awt.AlphaComposite;
/*     */ import java.awt.BasicStroke;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.RenderingHints;
/*     */ import java.awt.geom.CubicCurve2D;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ 
/*     */ public class GifCaptcha
/*     */   extends Captcha
/*     */ {
/*     */   public GifCaptcha() {}
/*     */   
/*     */   public GifCaptcha(int width, int height) {
/*  24 */     setWidth(width);
/*  25 */     setHeight(height);
/*     */   }
/*     */   
/*     */   public GifCaptcha(int width, int height, int len) {
/*  29 */     this(width, height);
/*  30 */     setLen(len);
/*     */   }
/*     */   
/*     */   public GifCaptcha(int width, int height, int len, Font font) {
/*  34 */     this(width, height, len);
/*  35 */     setFont(font);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean out(OutputStream os) {
/*     */     try {
/*  41 */       char[] strs = textChar();
/*     */       
/*  43 */       Color[] fontColor = new Color[this.len];
/*  44 */       for (int i = 0; i < this.len; i++) {
/*  45 */         fontColor[i] = color();
/*     */       }
/*     */       
/*  48 */       int x1 = 5, y1 = num(5, this.height / 2);
/*  49 */       int x2 = this.width - 5, y2 = num(this.height / 2, this.height - 5);
/*  50 */       int ctrlx = num(this.width / 4, this.width / 4 * 3), ctrly = num(5, this.height - 5);
/*  51 */       if (num(2) == 0) {
/*  52 */         int ty = y1;
/*  53 */         y1 = y2;
/*  54 */         y2 = ty;
/*     */       } 
/*  56 */       int ctrlx1 = num(this.width / 4, this.width / 4 * 3), ctrly1 = num(5, this.height - 5);
/*  57 */       int[][] besselXY = { { x1, y1 }, { ctrlx, ctrly }, { ctrlx1, ctrly1 }, { x2, y2 } };
/*     */       
/*  59 */       GifEncoder gifEncoder = new GifEncoder();
/*  60 */       gifEncoder.setQuality(180);
/*  61 */       gifEncoder.setDelay(100);
/*  62 */       gifEncoder.setRepeat(0);
/*  63 */       gifEncoder.start(os); int j;
/*  64 */       for (j = 0; j < this.len; j++) {
/*  65 */         BufferedImage frame = graphicsImage(fontColor, strs, j, besselXY);
/*  66 */         gifEncoder.addFrame(frame);
/*  67 */         frame.flush();
/*     */       } 
/*  69 */       gifEncoder.finish();
/*  70 */       j = 1; return j;
/*  71 */     } catch (Exception e) {
/*  72 */       e.printStackTrace();
/*     */     } finally {
/*     */       try {
/*  75 */         os.close();
/*  76 */       } catch (IOException e) {
/*  77 */         e.printStackTrace();
/*     */       } 
/*     */     } 
/*  80 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toBase64() {
/*  85 */     return toBase64("data:image/gif;base64,");
/*     */   }
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
/*     */   private BufferedImage graphicsImage(Color[] fontColor, char[] strs, int flag, int[][] besselXY) {
/*  98 */     BufferedImage image = new BufferedImage(this.width, this.height, 1);
/*  99 */     Graphics2D g2d = (Graphics2D)image.getGraphics();
/*     */     
/* 101 */     g2d.setColor(Color.WHITE);
/* 102 */     g2d.fillRect(0, 0, this.width, this.height);
/*     */     
/* 104 */     g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*     */     
/* 106 */     g2d.setComposite(AlphaComposite.getInstance(3, 0.1F * num(10)));
/* 107 */     drawOval(2, g2d);
/*     */     
/* 109 */     g2d.setComposite(AlphaComposite.getInstance(3, 0.7F));
/* 110 */     g2d.setStroke(new BasicStroke(1.2F, 0, 2));
/* 111 */     g2d.setColor(fontColor[0]);
/* 112 */     CubicCurve2D shape = new CubicCurve2D.Double(besselXY[0][0], besselXY[0][1], besselXY[1][0], besselXY[1][1], besselXY[2][0], besselXY[2][1], besselXY[3][0], besselXY[3][1]);
/* 113 */     g2d.draw(shape);
/*     */     
/* 115 */     g2d.setFont(getFont());
/* 116 */     FontMetrics fontMetrics = g2d.getFontMetrics();
/* 117 */     int fW = this.width / strs.length;
/* 118 */     int fSp = (fW - (int)fontMetrics.getStringBounds("W", g2d).getWidth()) / 2;
/* 119 */     for (int i = 0; i < strs.length; i++) {
/*     */       
/* 121 */       AlphaComposite ac3 = AlphaComposite.getInstance(3, getAlpha(flag, i));
/* 122 */       g2d.setComposite(ac3);
/* 123 */       g2d.setColor(fontColor[i]);
/* 124 */       int fY = this.height - (this.height - (int)fontMetrics.getStringBounds(String.valueOf(strs[i]), g2d).getHeight() >> 1);
/* 125 */       g2d.drawString(String.valueOf(strs[i]), i * fW + fSp + 3, fY - 3);
/*     */     } 
/* 127 */     g2d.dispose();
/* 128 */     return image;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private float getAlpha(int i, int j) {
/* 139 */     int num = i + j;
/* 140 */     float r = 1.0F / (this.len - 1);
/* 141 */     float s = this.len * r;
/* 142 */     return (num >= this.len) ? (num * r - s) : (num * r);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\wf\captcha\GifCaptcha.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */