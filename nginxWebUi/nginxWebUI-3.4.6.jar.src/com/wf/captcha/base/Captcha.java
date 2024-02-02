/*     */ package com.wf.captcha.base;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontFormatException;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.geom.CubicCurve2D;
/*     */ import java.awt.geom.QuadCurve2D;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.util.Base64;
/*     */ 
/*     */ public abstract class Captcha
/*     */   extends Randoms
/*     */ {
/*  17 */   public static final int[][] COLOR = new int[][] { { 0, 135, 255 }, { 51, 153, 51 }, { 255, 102, 102 }, { 255, 153, 0 }, { 153, 102, 0 }, { 153, 102, 153 }, { 51, 153, 153 }, { 102, 102, 255 }, { 0, 102, 204 }, { 204, 51, 51 }, { 0, 153, 204 }, { 0, 51, 102 } };
/*     */   
/*     */   public static final int TYPE_DEFAULT = 1;
/*     */   
/*     */   public static final int TYPE_ONLY_NUMBER = 2;
/*     */   public static final int TYPE_ONLY_CHAR = 3;
/*     */   public static final int TYPE_ONLY_UPPER = 4;
/*     */   public static final int TYPE_ONLY_LOWER = 5;
/*     */   public static final int TYPE_NUM_AND_UPPER = 6;
/*     */   public static final int FONT_1 = 0;
/*     */   public static final int FONT_2 = 1;
/*     */   public static final int FONT_3 = 2;
/*     */   public static final int FONT_4 = 3;
/*     */   public static final int FONT_5 = 4;
/*     */   public static final int FONT_6 = 5;
/*     */   public static final int FONT_7 = 6;
/*     */   public static final int FONT_8 = 7;
/*     */   public static final int FONT_9 = 8;
/*     */   public static final int FONT_10 = 9;
/*  36 */   private static final String[] FONT_NAMES = new String[] { "actionj.ttf", "epilog.ttf", "fresnel.ttf", "headache.ttf", "lexo.ttf", "prefix.ttf", "progbot.ttf", "ransom.ttf", "robot.ttf", "scandal.ttf" };
/*  37 */   private Font font = null;
/*  38 */   protected int len = 5;
/*  39 */   protected int width = 130;
/*  40 */   protected int height = 48;
/*  41 */   protected int charType = 1;
/*  42 */   protected String chars = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected char[] alphas() {
/*  50 */     char[] cs = new char[this.len];
/*  51 */     for (int i = 0; i < this.len; i++) {
/*  52 */       switch (this.charType) {
/*     */         case 2:
/*  54 */           cs[i] = alpha(8);
/*     */           break;
/*     */         case 3:
/*  57 */           cs[i] = alpha(8, charMaxIndex);
/*     */           break;
/*     */         case 4:
/*  60 */           cs[i] = alpha(8, 31);
/*     */           break;
/*     */         case 5:
/*  63 */           cs[i] = alpha(31, lowerMaxIndex);
/*     */           break;
/*     */         case 6:
/*  66 */           cs[i] = alpha(31);
/*     */           break;
/*     */         default:
/*  69 */           cs[i] = alpha(); break;
/*     */       } 
/*     */     } 
/*  72 */     this.chars = new String(cs);
/*  73 */     return cs;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Color color(int fc, int bc) {
/*  84 */     if (fc > 255)
/*  85 */       fc = 255; 
/*  86 */     if (bc > 255)
/*  87 */       bc = 255; 
/*  88 */     int r = fc + num(bc - fc);
/*  89 */     int g = fc + num(bc - fc);
/*  90 */     int b = fc + num(bc - fc);
/*  91 */     return new Color(r, g, b);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Color color() {
/* 100 */     int[] color = COLOR[num(COLOR.length)];
/* 101 */     return new Color(color[0], color[1], color[2]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract boolean out(OutputStream paramOutputStream);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract String toBase64();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toBase64(String type) {
/* 126 */     ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
/* 127 */     out(outputStream);
/* 128 */     return type + Base64.getEncoder().encodeToString(outputStream.toByteArray());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String text() {
/* 137 */     checkAlpha();
/* 138 */     return this.chars;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public char[] textChar() {
/* 147 */     checkAlpha();
/* 148 */     return this.chars.toCharArray();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void checkAlpha() {
/* 155 */     if (this.chars == null) {
/* 156 */       alphas();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void drawLine(int num, Graphics2D g) {
/* 167 */     drawLine(num, (Color)null, g);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void drawLine(int num, Color color, Graphics2D g) {
/* 178 */     for (int i = 0; i < num; i++) {
/* 179 */       g.setColor((color == null) ? color() : color);
/* 180 */       int x1 = num(-10, this.width - 10);
/* 181 */       int y1 = num(5, this.height - 5);
/* 182 */       int x2 = num(10, this.width + 10);
/* 183 */       int y2 = num(2, this.height - 2);
/* 184 */       g.drawLine(x1, y1, x2, y2);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void drawOval(int num, Graphics2D g) {
/* 195 */     drawOval(num, (Color)null, g);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void drawOval(int num, Color color, Graphics2D g) {
/* 206 */     for (int i = 0; i < num; i++) {
/* 207 */       g.setColor((color == null) ? color() : color);
/* 208 */       int w = 5 + num(10);
/* 209 */       g.drawOval(num(this.width - 25), num(this.height - 15), w, w);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void drawBesselLine(int num, Graphics2D g) {
/* 220 */     drawBesselLine(num, (Color)null, g);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void drawBesselLine(int num, Color color, Graphics2D g) {
/* 231 */     for (int i = 0; i < num; i++) {
/* 232 */       g.setColor((color == null) ? color() : color);
/* 233 */       int x1 = 5, y1 = num(5, this.height / 2);
/* 234 */       int x2 = this.width - 5, y2 = num(this.height / 2, this.height - 5);
/* 235 */       int ctrlx = num(this.width / 4, this.width / 4 * 3), ctrly = num(5, this.height - 5);
/* 236 */       if (num(2) == 0) {
/* 237 */         int ty = y1;
/* 238 */         y1 = y2;
/* 239 */         y2 = ty;
/*     */       } 
/* 241 */       if (num(2) == 0) {
/* 242 */         QuadCurve2D shape = new QuadCurve2D.Double();
/* 243 */         shape.setCurve(x1, y1, ctrlx, ctrly, x2, y2);
/* 244 */         g.draw(shape);
/*     */       } else {
/* 246 */         int ctrlx1 = num(this.width / 4, this.width / 4 * 3), ctrly1 = num(5, this.height - 5);
/* 247 */         CubicCurve2D shape = new CubicCurve2D.Double(x1, y1, ctrlx, ctrly, ctrlx1, ctrly1, x2, y2);
/* 248 */         g.draw(shape);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public Font getFont() {
/* 254 */     if (this.font == null) {
/*     */       try {
/* 256 */         setFont(0);
/* 257 */       } catch (Exception e) {
/* 258 */         setFont(new Font("Arial", 1, 32));
/*     */       } 
/*     */     }
/* 261 */     return this.font;
/*     */   }
/*     */   
/*     */   public void setFont(Font font) {
/* 265 */     this.font = font;
/*     */   }
/*     */   
/*     */   public void setFont(int font) throws IOException, FontFormatException {
/* 269 */     setFont(font, 32.0F);
/*     */   }
/*     */   
/*     */   public void setFont(int font, float size) throws IOException, FontFormatException {
/* 273 */     setFont(font, 1, size);
/*     */   }
/*     */   
/*     */   public void setFont(int font, int style, float size) throws IOException, FontFormatException {
/* 277 */     this.font = Font.createFont(0, getClass().getResourceAsStream("/" + FONT_NAMES[font])).deriveFont(style, size);
/*     */   }
/*     */   
/*     */   public int getLen() {
/* 281 */     return this.len;
/*     */   }
/*     */   
/*     */   public void setLen(int len) {
/* 285 */     this.len = len;
/*     */   }
/*     */   
/*     */   public int getWidth() {
/* 289 */     return this.width;
/*     */   }
/*     */   
/*     */   public void setWidth(int width) {
/* 293 */     this.width = width;
/*     */   }
/*     */   
/*     */   public int getHeight() {
/* 297 */     return this.height;
/*     */   }
/*     */   
/*     */   public void setHeight(int height) {
/* 301 */     this.height = height;
/*     */   }
/*     */   
/*     */   public int getCharType() {
/* 305 */     return this.charType;
/*     */   }
/*     */   
/*     */   public void setCharType(int charType) {
/* 309 */     this.charType = charType;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\wf\captcha\base\Captcha.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */