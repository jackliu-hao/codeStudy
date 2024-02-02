/*    */ package com.google.zxing.datamatrix.encoder;
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
/*    */ final class DataMatrixSymbolInfo144
/*    */   extends SymbolInfo
/*    */ {
/*    */   DataMatrixSymbolInfo144() {
/* 22 */     super(false, 1558, 620, 22, 22, 36, -1, 62);
/*    */   }
/*    */ 
/*    */   
/*    */   public int getInterleavedBlockCount() {
/* 27 */     return 10;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getDataLengthForInterleavedBlock(int index) {
/* 32 */     return (index <= 8) ? 156 : 155;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\datamatrix\encoder\DataMatrixSymbolInfo144.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */