/*    */ package com.google.zxing.common;
/*    */ 
/*    */ import com.google.zxing.NotFoundException;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class DefaultGridSampler
/*    */   extends GridSampler
/*    */ {
/*    */   public BitMatrix sampleGrid(BitMatrix image, int dimensionX, int dimensionY, float p1ToX, float p1ToY, float p2ToX, float p2ToY, float p3ToX, float p3ToY, float p4ToX, float p4ToY, float p1FromX, float p1FromY, float p2FromX, float p2FromY, float p3FromX, float p3FromY, float p4FromX, float p4FromY) throws NotFoundException {
/* 39 */     PerspectiveTransform transform = PerspectiveTransform.quadrilateralToQuadrilateral(p1ToX, p1ToY, p2ToX, p2ToY, p3ToX, p3ToY, p4ToX, p4ToY, p1FromX, p1FromY, p2FromX, p2FromY, p3FromX, p3FromY, p4FromX, p4FromY);
/*    */ 
/*    */ 
/*    */     
/* 43 */     return sampleGrid(image, dimensionX, dimensionY, transform);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public BitMatrix sampleGrid(BitMatrix image, int dimensionX, int dimensionY, PerspectiveTransform transform) throws NotFoundException {
/* 51 */     if (dimensionX <= 0 || dimensionY <= 0) {
/* 52 */       throw NotFoundException.getNotFoundInstance();
/*    */     }
/* 54 */     BitMatrix bits = new BitMatrix(dimensionX, dimensionY);
/* 55 */     float[] points = new float[2 * dimensionX];
/* 56 */     for (int y = 0; y < dimensionY; y++) {
/* 57 */       int max = points.length;
/* 58 */       float iValue = y + 0.5F; int x;
/* 59 */       for (x = 0; x < max; x += 2) {
/* 60 */         points[x] = (x / 2) + 0.5F;
/* 61 */         points[x + 1] = iValue;
/*    */       } 
/* 63 */       transform.transformPoints(points);
/*    */ 
/*    */       
/* 66 */       checkAndNudgePoints(image, points);
/*    */       try {
/* 68 */         for (x = 0; x < max; x += 2) {
/* 69 */           if (image.get((int)points[x], (int)points[x + 1]))
/*    */           {
/* 71 */             bits.set(x / 2, y);
/*    */           }
/*    */         } 
/* 74 */       } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */         
/* 82 */         throw NotFoundException.getNotFoundInstance();
/*    */       } 
/*    */     } 
/* 85 */     return bits;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\common\DefaultGridSampler.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */