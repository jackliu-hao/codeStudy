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
/*    */ 
/*    */ public class LineCaptcha
/*    */   extends AbstractCaptcha
/*    */ {
/*    */   private static final long serialVersionUID = 8691294460763091089L;
/*    */   
/*    */   public LineCaptcha(int width, int height) {
/* 32 */     this(width, height, 5, 150);
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
/*    */   public LineCaptcha(int width, int height, int codeCount, int lineCount) {
/* 44 */     super(width, height, codeCount, lineCount);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Image createImage(String code) {
/* 51 */     BufferedImage image = new BufferedImage(this.width, this.height, 1);
/* 52 */     Graphics2D g = GraphicsUtil.createGraphics(image, (Color)ObjectUtil.defaultIfNull(this.background, Color.WHITE));
/*    */ 
/*    */     
/* 55 */     drawInterfere(g);
/*    */ 
/*    */     
/* 58 */     drawString(g, code);
/*    */     
/* 60 */     return image;
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
/* 72 */     if (null != this.textAlpha) {
/* 73 */       g.setComposite(this.textAlpha);
/*    */     }
/* 75 */     GraphicsUtil.drawStringColourful(g, code, this.font, this.width, this.height);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private void drawInterfere(Graphics2D g) {
/* 84 */     ThreadLocalRandom random = RandomUtil.getRandom();
/*    */     
/* 86 */     for (int i = 0; i < this.interfereCount; i++) {
/* 87 */       int xs = random.nextInt(this.width);
/* 88 */       int ys = random.nextInt(this.height);
/* 89 */       int xe = xs + random.nextInt(this.width / 8);
/* 90 */       int ye = ys + random.nextInt(this.height / 8);
/* 91 */       g.setColor(ImgUtil.randomColor(random));
/* 92 */       g.drawLine(xs, ys, xe, ye);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\captcha\LineCaptcha.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */