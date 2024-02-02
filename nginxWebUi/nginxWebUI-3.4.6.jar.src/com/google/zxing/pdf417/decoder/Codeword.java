/*    */ package com.google.zxing.pdf417.decoder;
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
/*    */ final class Codeword
/*    */ {
/*    */   private static final int BARCODE_ROW_UNKNOWN = -1;
/*    */   private final int startX;
/*    */   private final int endX;
/*    */   private final int bucket;
/*    */   private final int value;
/* 30 */   private int rowNumber = -1;
/*    */   
/*    */   Codeword(int startX, int endX, int bucket, int value) {
/* 33 */     this.startX = startX;
/* 34 */     this.endX = endX;
/* 35 */     this.bucket = bucket;
/* 36 */     this.value = value;
/*    */   }
/*    */   
/*    */   boolean hasValidRowNumber() {
/* 40 */     return isValidRowNumber(this.rowNumber);
/*    */   }
/*    */   
/*    */   boolean isValidRowNumber(int rowNumber) {
/* 44 */     return (rowNumber != -1 && this.bucket == rowNumber % 3 * 3);
/*    */   }
/*    */   
/*    */   void setRowNumberAsRowIndicatorColumn() {
/* 48 */     this.rowNumber = this.value / 30 * 3 + this.bucket / 3;
/*    */   }
/*    */   
/*    */   int getWidth() {
/* 52 */     return this.endX - this.startX;
/*    */   }
/*    */   
/*    */   int getStartX() {
/* 56 */     return this.startX;
/*    */   }
/*    */   
/*    */   int getEndX() {
/* 60 */     return this.endX;
/*    */   }
/*    */   
/*    */   int getBucket() {
/* 64 */     return this.bucket;
/*    */   }
/*    */   
/*    */   int getValue() {
/* 68 */     return this.value;
/*    */   }
/*    */   
/*    */   int getRowNumber() {
/* 72 */     return this.rowNumber;
/*    */   }
/*    */   
/*    */   void setRowNumber(int rowNumber) {
/* 76 */     this.rowNumber = rowNumber;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 81 */     return this.rowNumber + "|" + this.value;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\pdf417\decoder\Codeword.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */