/*     */ package cn.hutool.core.img;
/*     */ 
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import java.awt.AlphaComposite;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Image;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.RenderingHints;
/*     */ import java.awt.image.BufferedImage;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GraphicsUtil
/*     */ {
/*     */   public static Graphics2D createGraphics(BufferedImage image, Color color) {
/*  35 */     Graphics2D g = image.createGraphics();
/*     */     
/*  37 */     if (null != color) {
/*     */       
/*  39 */       g.setColor(color);
/*  40 */       g.fillRect(0, 0, image.getWidth(), image.getHeight());
/*     */     } 
/*     */     
/*  43 */     return g;
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
/*     */   public static int getCenterY(Graphics g, int backgroundHeight) {
/*     */     int y;
/*  57 */     FontMetrics metrics = null;
/*     */     try {
/*  59 */       metrics = g.getFontMetrics();
/*  60 */     } catch (Exception exception) {}
/*     */ 
/*     */ 
/*     */     
/*  64 */     if (null != metrics) {
/*  65 */       y = (backgroundHeight - metrics.getHeight()) / 2 + metrics.getAscent();
/*     */     } else {
/*  67 */       y = backgroundHeight / 3;
/*     */     } 
/*  69 */     return y;
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
/*     */ 
/*     */   
/*     */   public static Graphics drawStringColourful(Graphics g, String str, Font font, int width, int height) {
/*  84 */     return drawString(g, str, font, null, width, height);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Graphics drawString(Graphics g, String str, Font font, Color color, int width, int height) {
/* 101 */     if (g instanceof Graphics2D) {
/* 102 */       ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*     */     }
/*     */     
/* 105 */     g.setFont(font);
/*     */ 
/*     */     
/* 108 */     int midY = getCenterY(g, height);
/* 109 */     if (null != color) {
/* 110 */       g.setColor(color);
/*     */     }
/*     */     
/* 113 */     int len = str.length();
/* 114 */     int charWidth = width / len;
/* 115 */     for (int i = 0; i < len; i++) {
/* 116 */       if (null == color)
/*     */       {
/* 118 */         g.setColor(ImgUtil.randomColor());
/*     */       }
/* 120 */       g.drawString(String.valueOf(str.charAt(i)), i * charWidth, midY);
/*     */     } 
/* 122 */     return g;
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static Graphics drawString(Graphics g, String str, Font font, Color color, Rectangle rectangle) {
/*     */     Dimension dimension;
/* 139 */     int backgroundWidth = rectangle.width;
/* 140 */     int backgroundHeight = rectangle.height;
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 145 */       dimension = FontUtil.getDimension(g.getFontMetrics(font), str);
/* 146 */     } catch (Exception e) {
/*     */       
/* 148 */       dimension = new Dimension(backgroundWidth / 3, backgroundHeight / 3);
/*     */     } 
/*     */     
/* 151 */     rectangle.setSize(dimension.width, dimension.height);
/* 152 */     Point point = ImgUtil.getPointBaseCentre(rectangle, backgroundWidth, backgroundHeight);
/*     */     
/* 154 */     return drawString(g, str, font, color, point);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static Graphics drawString(Graphics g, String str, Font font, Color color, Point point) {
/* 170 */     if (g instanceof Graphics2D) {
/* 171 */       ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*     */     }
/*     */     
/* 174 */     g.setFont(font);
/* 175 */     g.setColor((Color)ObjectUtil.defaultIfNull(color, Color.BLACK));
/* 176 */     g.drawString(str, point.x, point.y);
/*     */     
/* 178 */     return g;
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
/*     */   public static Graphics drawImg(Graphics g, Image img, Point point) {
/* 190 */     return drawImg(g, img, new Rectangle(point.x, point.y, img
/* 191 */           .getWidth(null), img.getHeight(null)));
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
/*     */   public static Graphics drawImg(Graphics g, Image img, Rectangle rectangle) {
/* 203 */     g.drawImage(img, rectangle.x, rectangle.y, rectangle.width, rectangle.height, null);
/* 204 */     return g;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Graphics2D setAlpha(Graphics2D g, float alpha) {
/* 215 */     g.setComposite(AlphaComposite.getInstance(10, alpha));
/* 216 */     return g;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\img\GraphicsUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */