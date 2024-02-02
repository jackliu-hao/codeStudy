/*    */ package com.google.zxing.qrcode.detector;
/*    */ 
/*    */ import com.google.zxing.ResultPoint;
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
/*    */ public final class AlignmentPattern
/*    */   extends ResultPoint
/*    */ {
/*    */   private final float estimatedModuleSize;
/*    */   
/*    */   AlignmentPattern(float posX, float posY, float estimatedModuleSize) {
/* 32 */     super(posX, posY);
/* 33 */     this.estimatedModuleSize = estimatedModuleSize;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   boolean aboutEquals(float moduleSize, float i, float j) {
/* 41 */     if (Math.abs(i - getY()) <= moduleSize && Math.abs(j - getX()) <= moduleSize) {
/*    */       float moduleSizeDiff;
/* 43 */       if ((moduleSizeDiff = Math.abs(moduleSize - this.estimatedModuleSize)) <= 1.0F || moduleSizeDiff <= this.estimatedModuleSize) return true;  return false;
/*    */     } 
/* 45 */     return false;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   AlignmentPattern combineEstimate(float i, float j, float newModuleSize) {
/* 53 */     float combinedX = (getX() + j) / 2.0F;
/* 54 */     float combinedY = (getY() + i) / 2.0F;
/* 55 */     float combinedModuleSize = (this.estimatedModuleSize + newModuleSize) / 2.0F;
/* 56 */     return new AlignmentPattern(combinedX, combinedY, combinedModuleSize);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\qrcode\detector\AlignmentPattern.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */