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
/*    */ 
/*    */ public final class FinderPattern
/*    */   extends ResultPoint
/*    */ {
/*    */   private final float estimatedModuleSize;
/*    */   private final int count;
/*    */   
/*    */   FinderPattern(float posX, float posY, float estimatedModuleSize) {
/* 34 */     this(posX, posY, estimatedModuleSize, 1);
/*    */   }
/*    */   
/*    */   private FinderPattern(float posX, float posY, float estimatedModuleSize, int count) {
/* 38 */     super(posX, posY);
/* 39 */     this.estimatedModuleSize = estimatedModuleSize;
/* 40 */     this.count = count;
/*    */   }
/*    */   
/*    */   public float getEstimatedModuleSize() {
/* 44 */     return this.estimatedModuleSize;
/*    */   }
/*    */   
/*    */   int getCount() {
/* 48 */     return this.count;
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
/*    */ 
/*    */   
/*    */   boolean aboutEquals(float moduleSize, float i, float j) {
/* 62 */     if (Math.abs(i - getY()) <= moduleSize && Math.abs(j - getX()) <= moduleSize) {
/*    */       float moduleSizeDiff;
/* 64 */       if ((moduleSizeDiff = Math.abs(moduleSize - this.estimatedModuleSize)) <= 1.0F || moduleSizeDiff <= this.estimatedModuleSize) return true;  return false;
/*    */     } 
/* 66 */     return false;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   FinderPattern combineEstimate(float i, float j, float newModuleSize) {
/* 75 */     int combinedCount = this.count + 1;
/* 76 */     float combinedX = (this.count * getX() + j) / combinedCount;
/* 77 */     float combinedY = (this.count * getY() + i) / combinedCount;
/* 78 */     float combinedModuleSize = (this.count * this.estimatedModuleSize + newModuleSize) / combinedCount;
/* 79 */     return new FinderPattern(combinedX, combinedY, combinedModuleSize, combinedCount);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\qrcode\detector\FinderPattern.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */