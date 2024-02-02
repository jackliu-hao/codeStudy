/*     */ package com.wf.captcha;
/*     */ import com.wf.captcha.base.ChineseCaptchaAbstract;
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
/*     */ public class ChineseGifCaptcha extends ChineseCaptchaAbstract {
/*     */   public ChineseGifCaptcha(int width, int height) {
/*  18 */     setWidth(width);
/*  19 */     setHeight(height);
/*     */   }
/*     */   public ChineseGifCaptcha() {}
/*     */   public ChineseGifCaptcha(int width, int height, int len) {
/*  23 */     this(width, height);
/*  24 */     setLen(len);
/*     */   }
/*     */   
/*     */   public ChineseGifCaptcha(int width, int height, int len, Font font) {
/*  28 */     this(width, height, len);
/*  29 */     setFont(font);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean out(OutputStream os) {
/*     */     try {
/*  35 */       char[] strs = textChar();
/*     */       
/*  37 */       Color[] fontColor = new Color[this.len];
/*  38 */       for (int i = 0; i < this.len; i++) {
/*  39 */         fontColor[i] = color();
/*     */       }
/*     */       
/*  42 */       int x1 = 5, y1 = num(5, this.height / 2);
/*  43 */       int x2 = this.width - 5, y2 = num(this.height / 2, this.height - 5);
/*  44 */       int ctrlx = num(this.width / 4, this.width / 4 * 3), ctrly = num(5, this.height - 5);
/*  45 */       if (num(2) == 0) {
/*  46 */         int ty = y1;
/*  47 */         y1 = y2;
/*  48 */         y2 = ty;
/*     */       } 
/*  50 */       int ctrlx1 = num(this.width / 4, this.width / 4 * 3), ctrly1 = num(5, this.height - 5);
/*  51 */       int[][] besselXY = { { x1, y1 }, { ctrlx, ctrly }, { ctrlx1, ctrly1 }, { x2, y2 } };
/*     */       
/*  53 */       GifEncoder gifEncoder = new GifEncoder();
/*  54 */       gifEncoder.setQuality(180);
/*  55 */       gifEncoder.setDelay(100);
/*  56 */       gifEncoder.setRepeat(0);
/*  57 */       gifEncoder.start(os); int j;
/*  58 */       for (j = 0; j < this.len; j++) {
/*  59 */         BufferedImage frame = graphicsImage(fontColor, strs, j, besselXY);
/*  60 */         gifEncoder.addFrame(frame);
/*  61 */         frame.flush();
/*     */       } 
/*  63 */       gifEncoder.finish();
/*  64 */       j = 1; return j;
/*  65 */     } catch (Exception e) {
/*  66 */       e.printStackTrace();
/*     */     } finally {
/*     */       try {
/*  69 */         os.close();
/*  70 */       } catch (IOException e) {
/*  71 */         e.printStackTrace();
/*     */       } 
/*     */     } 
/*  74 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toBase64() {
/*  79 */     return toBase64("data:image/gif;base64,");
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
/*  92 */     BufferedImage image = new BufferedImage(this.width, this.height, 1);
/*  93 */     Graphics2D g2d = (Graphics2D)image.getGraphics();
/*     */     
/*  95 */     g2d.setColor(Color.WHITE);
/*  96 */     g2d.fillRect(0, 0, this.width, this.height);
/*     */     
/*  98 */     g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*     */     
/* 100 */     g2d.setComposite(AlphaComposite.getInstance(3, 0.1F * num(10)));
/* 101 */     drawOval(2, g2d);
/*     */     
/* 103 */     g2d.setComposite(AlphaComposite.getInstance(3, 0.7F));
/* 104 */     g2d.setStroke(new BasicStroke(1.2F, 0, 2));
/* 105 */     g2d.setColor(fontColor[0]);
/* 106 */     CubicCurve2D shape = new CubicCurve2D.Double(besselXY[0][0], besselXY[0][1], besselXY[1][0], besselXY[1][1], besselXY[2][0], besselXY[2][1], besselXY[3][0], besselXY[3][1]);
/* 107 */     g2d.draw(shape);
/*     */     
/* 109 */     g2d.setFont(getFont());
/* 110 */     FontMetrics fontMetrics = g2d.getFontMetrics();
/* 111 */     int fW = this.width / strs.length;
/* 112 */     int fSp = (fW - (int)fontMetrics.getStringBounds("W", g2d).getWidth()) / 2;
/* 113 */     for (int i = 0; i < strs.length; i++) {
/*     */       
/* 115 */       AlphaComposite ac3 = AlphaComposite.getInstance(3, getAlpha(flag, i));
/* 116 */       g2d.setComposite(ac3);
/* 117 */       g2d.setColor(fontColor[i]);
/* 118 */       int fY = this.height - (this.height - (int)fontMetrics.getStringBounds(String.valueOf(strs[i]), g2d).getHeight() >> 1);
/* 119 */       g2d.drawString(String.valueOf(strs[i]), i * fW + fSp - 3, fY - 3);
/*     */     } 
/* 121 */     g2d.dispose();
/* 122 */     return image;
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
/* 133 */     int num = i + j;
/* 134 */     float r = 1.0F / (this.len - 1);
/* 135 */     float s = this.len * r;
/* 136 */     return (num >= this.len) ? (num * r - s) : (num * r);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\wf\captcha\ChineseGifCaptcha.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */