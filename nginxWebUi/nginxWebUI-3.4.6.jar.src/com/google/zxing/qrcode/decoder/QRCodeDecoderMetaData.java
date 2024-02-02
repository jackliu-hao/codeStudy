/*    */ package com.google.zxing.qrcode.decoder;
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
/*    */ public final class QRCodeDecoderMetaData
/*    */ {
/*    */   private final boolean mirrored;
/*    */   
/*    */   QRCodeDecoderMetaData(boolean mirrored) {
/* 32 */     this.mirrored = mirrored;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isMirrored() {
/* 39 */     return this.mirrored;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void applyMirroredCorrection(ResultPoint[] points) {
/* 48 */     if (!this.mirrored || points == null || points.length < 3) {
/*    */       return;
/*    */     }
/* 51 */     ResultPoint bottomLeft = points[0];
/* 52 */     points[0] = points[2];
/* 53 */     points[2] = bottomLeft;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\qrcode\decoder\QRCodeDecoderMetaData.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */