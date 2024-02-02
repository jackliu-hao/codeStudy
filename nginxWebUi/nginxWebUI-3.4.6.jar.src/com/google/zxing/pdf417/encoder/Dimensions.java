/*    */ package com.google.zxing.pdf417.encoder;
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
/*    */ public final class Dimensions
/*    */ {
/*    */   private final int minCols;
/*    */   private final int maxCols;
/*    */   private final int minRows;
/*    */   private final int maxRows;
/*    */   
/*    */   public Dimensions(int minCols, int maxCols, int minRows, int maxRows) {
/* 32 */     this.minCols = minCols;
/* 33 */     this.maxCols = maxCols;
/* 34 */     this.minRows = minRows;
/* 35 */     this.maxRows = maxRows;
/*    */   }
/*    */   
/*    */   public int getMinCols() {
/* 39 */     return this.minCols;
/*    */   }
/*    */   
/*    */   public int getMaxCols() {
/* 43 */     return this.maxCols;
/*    */   }
/*    */   
/*    */   public int getMinRows() {
/* 47 */     return this.minRows;
/*    */   }
/*    */   
/*    */   public int getMaxRows() {
/* 51 */     return this.maxRows;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\pdf417\encoder\Dimensions.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */