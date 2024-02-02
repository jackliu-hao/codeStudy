/*    */ package com.google.zxing.aztec.encoder;
/*    */ 
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
/*    */ public final class AztecCode
/*    */ {
/*    */   private boolean compact;
/*    */   private int size;
/*    */   private int layers;
/*    */   private int codeWords;
/*    */   private BitMatrix matrix;
/*    */   
/*    */   public boolean isCompact() {
/* 38 */     return this.compact;
/*    */   }
/*    */   
/*    */   public void setCompact(boolean compact) {
/* 42 */     this.compact = compact;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getSize() {
/* 49 */     return this.size;
/*    */   }
/*    */   
/*    */   public void setSize(int size) {
/* 53 */     this.size = size;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getLayers() {
/* 60 */     return this.layers;
/*    */   }
/*    */   
/*    */   public void setLayers(int layers) {
/* 64 */     this.layers = layers;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getCodeWords() {
/* 71 */     return this.codeWords;
/*    */   }
/*    */   
/*    */   public void setCodeWords(int codeWords) {
/* 75 */     this.codeWords = codeWords;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public BitMatrix getMatrix() {
/* 82 */     return this.matrix;
/*    */   }
/*    */   
/*    */   public void setMatrix(BitMatrix matrix) {
/* 86 */     this.matrix = matrix;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\aztec\encoder\AztecCode.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */