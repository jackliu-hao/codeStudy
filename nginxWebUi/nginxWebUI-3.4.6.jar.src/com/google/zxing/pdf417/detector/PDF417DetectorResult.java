/*    */ package com.google.zxing.pdf417.detector;
/*    */ 
/*    */ import com.google.zxing.ResultPoint;
/*    */ import com.google.zxing.common.BitMatrix;
/*    */ import java.util.List;
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
/*    */ public final class PDF417DetectorResult
/*    */ {
/*    */   private final BitMatrix bits;
/*    */   private final List<ResultPoint[]> points;
/*    */   
/*    */   public PDF417DetectorResult(BitMatrix bits, List<ResultPoint[]> points) {
/* 33 */     this.bits = bits;
/* 34 */     this.points = points;
/*    */   }
/*    */   
/*    */   public BitMatrix getBits() {
/* 38 */     return this.bits;
/*    */   }
/*    */   
/*    */   public List<ResultPoint[]> getPoints() {
/* 42 */     return this.points;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\pdf417\detector\PDF417DetectorResult.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */