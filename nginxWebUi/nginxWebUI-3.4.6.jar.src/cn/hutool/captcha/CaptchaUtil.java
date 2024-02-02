/*     */ package cn.hutool.captcha;
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
/*     */ public class CaptchaUtil
/*     */ {
/*     */   public static LineCaptcha createLineCaptcha(int width, int height) {
/*  19 */     return new LineCaptcha(width, height);
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
/*     */   public static LineCaptcha createLineCaptcha(int width, int height, int codeCount, int lineCount) {
/*  32 */     return new LineCaptcha(width, height, codeCount, lineCount);
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
/*     */   public static CircleCaptcha createCircleCaptcha(int width, int height) {
/*  44 */     return new CircleCaptcha(width, height);
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
/*     */   public static CircleCaptcha createCircleCaptcha(int width, int height, int codeCount, int circleCount) {
/*  58 */     return new CircleCaptcha(width, height, codeCount, circleCount);
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
/*     */   public static ShearCaptcha createShearCaptcha(int width, int height) {
/*  70 */     return new ShearCaptcha(width, height);
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
/*     */   public static ShearCaptcha createShearCaptcha(int width, int height, int codeCount, int thickness) {
/*  84 */     return new ShearCaptcha(width, height, codeCount, thickness);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static GifCaptcha createGifCaptcha(int width, int height) {
/*  95 */     return new GifCaptcha(width, height);
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
/*     */   public static GifCaptcha createGifCaptcha(int width, int height, int codeCount) {
/* 107 */     return new GifCaptcha(width, height, codeCount);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\captcha\CaptchaUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */