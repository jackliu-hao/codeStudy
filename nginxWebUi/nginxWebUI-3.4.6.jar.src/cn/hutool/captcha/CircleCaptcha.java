/*    */ package cn.hutool.captcha;
/*    */ 
/*    */ import cn.hutool.core.img.GraphicsUtil;
/*    */ import cn.hutool.core.img.ImgUtil;
/*    */ import cn.hutool.core.util.ObjectUtil;
/*    */ import cn.hutool.core.util.RandomUtil;
/*    */ import java.awt.Color;
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.Image;
/*    */ import java.awt.image.BufferedImage;
/*    */ import java.util.concurrent.ThreadLocalRandom;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CircleCaptcha
/*    */   extends AbstractCaptcha
/*    */ {
/*    */   private static final long serialVersionUID = -7096627300356535494L;
/*    */   
/*    */   public CircleCaptcha(int width, int height) {
/* 31 */     this(width, height, 5);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CircleCaptcha(int width, int height, int codeCount) {
/* 42 */     this(width, height, codeCount, 15);
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
/*    */   public CircleCaptcha(int width, int height, int codeCount, int interfereCount) {
/* 54 */     super(width, height, codeCount, interfereCount);
/*    */   }
/*    */ 
/*    */   
/*    */   public Image createImage(String code) {
/* 59 */     BufferedImage image = new BufferedImage(this.width, this.height, 1);
/* 60 */     Graphics2D g = ImgUtil.createGraphics(image, (Color)ObjectUtil.defaultIfNull(this.background, Color.WHITE));
/*    */ 
/*    */     
/* 63 */     drawInterfere(g);
/*    */ 
/*    */     
/* 66 */     drawString(g, code);
/*    */     
/* 68 */     return image;
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
/*    */   private void drawString(Graphics2D g, String code) {
/* 80 */     if (null != this.textAlpha) {
/* 81 */       g.setComposite(this.textAlpha);
/*    */     }
/* 83 */     GraphicsUtil.drawStringColourful(g, code, this.font, this.width, this.height);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private void drawInterfere(Graphics2D g) {
/* 92 */     ThreadLocalRandom random = RandomUtil.getRandom();
/*    */     
/* 94 */     for (int i = 0; i < this.interfereCount; i++) {
/* 95 */       g.setColor(ImgUtil.randomColor(random));
/* 96 */       g.drawOval(random.nextInt(this.width), random.nextInt(this.height), random.nextInt(this.height >> 1), random.nextInt(this.height >> 1));
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\captcha\CircleCaptcha.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */