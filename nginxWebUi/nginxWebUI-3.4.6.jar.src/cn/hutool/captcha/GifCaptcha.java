/*     */ package cn.hutool.captcha;
/*     */ 
/*     */ import cn.hutool.core.img.gif.AnimatedGifEncoder;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import cn.hutool.core.util.RandomUtil;
/*     */ import java.awt.AlphaComposite;
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Image;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GifCaptcha
/*     */   extends AbstractCaptcha
/*     */ {
/*     */   private static final long serialVersionUID = 7091627304326538464L;
/*  25 */   private int quality = 10;
/*     */   
/*  27 */   private int repeat = 0;
/*     */   
/*  29 */   private int minColor = 0;
/*     */   
/*  31 */   private int maxColor = 255;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GifCaptcha(int width, int height) {
/*  41 */     this(width, height, 5);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GifCaptcha(int width, int height, int codeCount) {
/*  50 */     super(width, height, codeCount, 10);
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
/*     */   public GifCaptcha setQuality(int quality) {
/*  63 */     if (quality < 1) {
/*  64 */       quality = 1;
/*     */     }
/*  66 */     this.quality = quality;
/*  67 */     return this;
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
/*     */   public GifCaptcha setRepeat(int repeat) {
/*  79 */     if (repeat >= 0) {
/*  80 */       this.repeat = repeat;
/*     */     }
/*  82 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GifCaptcha setMaxColor(int maxColor) {
/*  92 */     this.maxColor = maxColor;
/*  93 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GifCaptcha setMinColor(int minColor) {
/* 103 */     this.minColor = minColor;
/* 104 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void createCode() {
/* 109 */     generateCode();
/* 110 */     ByteArrayOutputStream out = new ByteArrayOutputStream();
/*     */     
/* 112 */     AnimatedGifEncoder gifEncoder = new AnimatedGifEncoder();
/*     */     
/* 114 */     gifEncoder.start(out);
/* 115 */     gifEncoder.setQuality(this.quality);
/*     */     
/* 117 */     int delay = 100;
/* 118 */     gifEncoder.setDelay(delay);
/* 119 */     gifEncoder.setRepeat(this.repeat);
/*     */     
/* 121 */     char[] chars = this.code.toCharArray();
/* 122 */     Color[] fontColor = new Color[chars.length];
/* 123 */     for (int i = 0; i < chars.length; i++) {
/* 124 */       fontColor[i] = getRandomColor(this.minColor, this.maxColor);
/* 125 */       BufferedImage frame = graphicsImage(chars, fontColor, chars, i);
/* 126 */       gifEncoder.addFrame(frame);
/* 127 */       frame.flush();
/*     */     } 
/* 129 */     gifEncoder.finish();
/* 130 */     this.imageBytes = out.toByteArray();
/*     */   }
/*     */ 
/*     */   
/*     */   protected Image createImage(String code) {
/* 135 */     return null;
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
/*     */   private BufferedImage graphicsImage(char[] chars, Color[] fontColor, char[] words, int flag) {
/* 147 */     BufferedImage image = new BufferedImage(this.width, this.height, 1);
/*     */     
/* 149 */     Graphics2D g2d = image.createGraphics();
/*     */     
/* 151 */     g2d.setColor((Color)ObjectUtil.defaultIfNull(this.background, Color.WHITE));
/* 152 */     g2d.fillRect(0, 0, this.width, this.height);
/*     */ 
/*     */     
/* 155 */     float y = ((this.height >> 1) + (this.font.getSize() >> 1));
/* 156 */     float m = 1.0F * (this.width - chars.length * this.font.getSize()) / chars.length;
/*     */     
/* 158 */     float x = Math.max(m / 2.0F, 2.0F);
/* 159 */     g2d.setFont(this.font);
/*     */     
/* 161 */     if (null != this.textAlpha) {
/* 162 */       g2d.setComposite(this.textAlpha);
/*     */     }
/* 164 */     for (int i = 0; i < chars.length; i++) {
/* 165 */       AlphaComposite ac = AlphaComposite.getInstance(3, getAlpha(chars.length, flag, i));
/* 166 */       g2d.setComposite(ac);
/* 167 */       g2d.setColor(fontColor[i]);
/* 168 */       g2d.drawOval(
/* 169 */           RandomUtil.randomInt(this.width), 
/* 170 */           RandomUtil.randomInt(this.height), 
/* 171 */           RandomUtil.randomInt(5, 30), 5 + RandomUtil.randomInt(5, 30));
/*     */       
/* 173 */       g2d.drawString(words[i] + "", x + (this.font.getSize() + m) * i, y);
/*     */     } 
/* 175 */     g2d.dispose();
/* 176 */     return image;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private float getAlpha(int v, int i, int j) {
/* 185 */     int num = i + j;
/* 186 */     float r = 1.0F / v;
/* 187 */     float s = (v + 1) * r;
/* 188 */     return (num > v) ? (num * r - s) : (num * r);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Color getRandomColor(int min, int max) {
/* 197 */     if (min > 255) {
/* 198 */       min = 255;
/*     */     }
/* 200 */     if (max > 255) {
/* 201 */       max = 255;
/*     */     }
/* 203 */     if (min < 0) {
/* 204 */       min = 0;
/*     */     }
/* 206 */     if (max < 0) {
/* 207 */       max = 0;
/*     */     }
/* 209 */     if (min > max) {
/* 210 */       min = 0;
/* 211 */       max = 255;
/*     */     } 
/* 213 */     return new Color(
/* 214 */         RandomUtil.randomInt(min, max), 
/* 215 */         RandomUtil.randomInt(min, max), 
/* 216 */         RandomUtil.randomInt(min, max));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\captcha\GifCaptcha.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */