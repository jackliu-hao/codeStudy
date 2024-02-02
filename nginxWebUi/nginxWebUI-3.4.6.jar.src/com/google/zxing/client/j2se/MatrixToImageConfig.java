/*    */ package com.google.zxing.client.j2se;
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
/*    */ public final class MatrixToImageConfig
/*    */ {
/*    */   public static final int BLACK = -16777216;
/*    */   public static final int WHITE = -1;
/*    */   private final int onColor;
/*    */   private final int offColor;
/*    */   
/*    */   public MatrixToImageConfig() {
/* 37 */     this(-16777216, -1);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public MatrixToImageConfig(int onColor, int offColor) {
/* 45 */     this.onColor = onColor;
/* 46 */     this.offColor = offColor;
/*    */   }
/*    */   
/*    */   public int getPixelOnColor() {
/* 50 */     return this.onColor;
/*    */   }
/*    */   
/*    */   public int getPixelOffColor() {
/* 54 */     return this.offColor;
/*    */   }
/*    */   
/*    */   int getBufferedImageColorModel() {
/* 58 */     if (this.onColor == -16777216 && this.offColor == -1)
/*    */     {
/* 60 */       return 12;
/*    */     }
/* 62 */     if (hasTransparency(this.onColor) || hasTransparency(this.offColor))
/*    */     {
/* 64 */       return 2;
/*    */     }
/*    */     
/* 67 */     return 1;
/*    */   }
/*    */   
/*    */   private static boolean hasTransparency(int argb) {
/* 71 */     return ((argb & 0xFF000000) != -16777216);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\j2se\MatrixToImageConfig.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */