/*     */ package cn.hutool.captcha;
/*     */ 
/*     */ import cn.hutool.core.img.GraphicsUtil;
/*     */ import cn.hutool.core.img.ImgUtil;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import cn.hutool.core.util.RandomUtil;
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Image;
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
/*     */ public class ShearCaptcha
/*     */   extends AbstractCaptcha
/*     */ {
/*     */   private static final long serialVersionUID = -7096627300356535494L;
/*     */   
/*     */   public ShearCaptcha(int width, int height) {
/*  31 */     this(width, height, 5);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ShearCaptcha(int width, int height, int codeCount) {
/*  42 */     this(width, height, codeCount, 4);
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
/*     */   public ShearCaptcha(int width, int height, int codeCount, int thickness) {
/*  54 */     super(width, height, codeCount, thickness);
/*     */   }
/*     */ 
/*     */   
/*     */   public Image createImage(String code) {
/*  59 */     BufferedImage image = new BufferedImage(this.width, this.height, 1);
/*  60 */     Graphics2D g = GraphicsUtil.createGraphics(image, (Color)ObjectUtil.defaultIfNull(this.background, Color.WHITE));
/*     */ 
/*     */     
/*  63 */     drawString(g, code);
/*     */ 
/*     */     
/*  66 */     shear(g, this.width, this.height, (Color)ObjectUtil.defaultIfNull(this.background, Color.WHITE));
/*     */     
/*  68 */     drawInterfere(g, 0, RandomUtil.randomInt(this.height) + 1, this.width, RandomUtil.randomInt(this.height) + 1, this.interfereCount, ImgUtil.randomColor());
/*     */     
/*  70 */     return image;
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
/*     */   private void drawString(Graphics2D g, String code) {
/*  82 */     if (null != this.textAlpha) {
/*  83 */       g.setComposite(this.textAlpha);
/*     */     }
/*  85 */     GraphicsUtil.drawStringColourful(g, code, this.font, this.width, this.height);
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
/*     */   private void shear(Graphics g, int w1, int h1, Color color) {
/*  97 */     shearX(g, w1, h1, color);
/*  98 */     shearY(g, w1, h1, color);
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
/*     */   private void shearX(Graphics g, int w1, int h1, Color color) {
/* 111 */     int period = RandomUtil.randomInt(this.width);
/*     */     
/* 113 */     int frames = 1;
/* 114 */     int phase = RandomUtil.randomInt(2);
/*     */     
/* 116 */     for (int i = 0; i < h1; i++) {
/* 117 */       double d = (period >> 1) * Math.sin(i / period + 6.283185307179586D * phase / frames);
/* 118 */       g.copyArea(0, i, w1, 1, (int)d, 0);
/* 119 */       g.setColor(color);
/* 120 */       g.drawLine((int)d, i, 0, i);
/* 121 */       g.drawLine((int)d + w1, i, w1, i);
/*     */     } 
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
/*     */   private void shearY(Graphics g, int w1, int h1, Color color) {
/* 136 */     int period = RandomUtil.randomInt(this.height >> 1);
/*     */     
/* 138 */     int frames = 20;
/* 139 */     int phase = 7;
/* 140 */     for (int i = 0; i < w1; i++) {
/* 141 */       double d = (period >> 1) * Math.sin(i / period + 6.283185307179586D * phase / frames);
/* 142 */       g.copyArea(i, 0, 1, h1, 0, (int)d);
/* 143 */       g.setColor(color);
/*     */       
/* 145 */       g.drawLine(i, (int)d, i, 0);
/* 146 */       g.drawLine(i, (int)d + h1, i, h1);
/*     */     } 
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
/*     */ 
/*     */   
/*     */   private void drawInterfere(Graphics g, int x1, int y1, int x2, int y2, int thickness, Color c) {
/* 166 */     g.setColor(c);
/* 167 */     int dX = x2 - x1;
/* 168 */     int dY = y2 - y1;
/*     */     
/* 170 */     double lineLength = Math.sqrt((dX * dX + dY * dY));
/*     */     
/* 172 */     double scale = thickness / 2.0D * lineLength;
/*     */ 
/*     */ 
/*     */     
/* 176 */     double ddx = -scale * dY;
/* 177 */     double ddy = scale * dX;
/* 178 */     ddx += (ddx > 0.0D) ? 0.5D : -0.5D;
/* 179 */     ddy += (ddy > 0.0D) ? 0.5D : -0.5D;
/* 180 */     int dx = (int)ddx;
/* 181 */     int dy = (int)ddy;
/*     */ 
/*     */     
/* 184 */     int[] xPoints = new int[4];
/* 185 */     int[] yPoints = new int[4];
/*     */     
/* 187 */     xPoints[0] = x1 + dx;
/* 188 */     yPoints[0] = y1 + dy;
/* 189 */     xPoints[1] = x1 - dx;
/* 190 */     yPoints[1] = y1 - dy;
/* 191 */     xPoints[2] = x2 - dx;
/* 192 */     yPoints[2] = y2 - dy;
/* 193 */     xPoints[3] = x2 + dx;
/* 194 */     yPoints[3] = y2 + dy;
/*     */     
/* 196 */     g.fillPolygon(xPoints, yPoints, 4);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\captcha\ShearCaptcha.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */