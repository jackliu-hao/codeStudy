/*    */ package com.google.zxing;
/*    */ 
/*    */ import com.google.zxing.common.BitArray;
/*    */ import com.google.zxing.common.BitMatrix;
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
/*    */ public abstract class Binarizer
/*    */ {
/*    */   private final LuminanceSource source;
/*    */   
/*    */   protected Binarizer(LuminanceSource source) {
/* 35 */     this.source = source;
/*    */   }
/*    */   
/*    */   public final LuminanceSource getLuminanceSource() {
/* 39 */     return this.source;
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
/*    */   public abstract BitArray getBlackRow(int paramInt, BitArray paramBitArray) throws NotFoundException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public abstract BitMatrix getBlackMatrix() throws NotFoundException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public abstract Binarizer createBinarizer(LuminanceSource paramLuminanceSource);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final int getWidth() {
/* 80 */     return this.source.getWidth();
/*    */   }
/*    */   
/*    */   public final int getHeight() {
/* 84 */     return this.source.getHeight();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\Binarizer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */