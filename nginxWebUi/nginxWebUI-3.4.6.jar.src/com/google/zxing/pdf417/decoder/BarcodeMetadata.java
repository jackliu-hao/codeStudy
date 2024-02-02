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
/*    */ final class BarcodeMetadata
/*    */ {
/*    */   private final int columnCount;
/*    */   private final int errorCorrectionLevel;
/*    */   private final int rowCountUpperPart;
/*    */   private final int rowCountLowerPart;
/*    */   private final int rowCount;
/*    */   
/*    */   BarcodeMetadata(int columnCount, int rowCountUpperPart, int rowCountLowerPart, int errorCorrectionLevel) {
/* 31 */     this.columnCount = columnCount;
/* 32 */     this.errorCorrectionLevel = errorCorrectionLevel;
/* 33 */     this.rowCountUpperPart = rowCountUpperPart;
/* 34 */     this.rowCountLowerPart = rowCountLowerPart;
/* 35 */     this.rowCount = rowCountUpperPart + rowCountLowerPart;
/*    */   }
/*    */   
/*    */   int getColumnCount() {
/* 39 */     return this.columnCount;
/*    */   }
/*    */   
/*    */   int getErrorCorrectionLevel() {
/* 43 */     return this.errorCorrectionLevel;
/*    */   }
/*    */   
/*    */   int getRowCount() {
/* 47 */     return this.rowCount;
/*    */   }
/*    */   
/*    */   int getRowCountUpperPart() {
/* 51 */     return this.rowCountUpperPart;
/*    */   }
/*    */   
/*    */   int getRowCountLowerPart() {
/* 55 */     return this.rowCountLowerPart;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\pdf417\decoder\BarcodeMetadata.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */