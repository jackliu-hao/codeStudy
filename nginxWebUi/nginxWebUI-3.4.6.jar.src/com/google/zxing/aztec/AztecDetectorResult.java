/*    */ package com.google.zxing.aztec;
/*    */ 
/*    */ import com.google.zxing.ResultPoint;
/*    */ import com.google.zxing.common.BitMatrix;
/*    */ import com.google.zxing.common.DetectorResult;
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
/*    */ public final class AztecDetectorResult
/*    */   extends DetectorResult
/*    */ {
/*    */   private final boolean compact;
/*    */   private final int nbDatablocks;
/*    */   private final int nbLayers;
/*    */   
/*    */   public AztecDetectorResult(BitMatrix bits, ResultPoint[] points, boolean compact, int nbDatablocks, int nbLayers) {
/* 40 */     super(bits, points);
/* 41 */     this.compact = compact;
/* 42 */     this.nbDatablocks = nbDatablocks;
/* 43 */     this.nbLayers = nbLayers;
/*    */   }
/*    */   
/*    */   public int getNbLayers() {
/* 47 */     return this.nbLayers;
/*    */   }
/*    */   
/*    */   public int getNbDatablocks() {
/* 51 */     return this.nbDatablocks;
/*    */   }
/*    */   
/*    */   public boolean isCompact() {
/* 55 */     return this.compact;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\aztec\AztecDetectorResult.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */