/*    */ package org.apache.commons.compress.compressors.deflate;
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
/*    */ public class DeflateParameters
/*    */ {
/*    */   private boolean zlibHeader = true;
/* 31 */   private int compressionLevel = -1;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean withZlibHeader() {
/* 39 */     return this.zlibHeader;
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
/*    */   public void setWithZlibHeader(boolean zlibHeader) {
/* 51 */     this.zlibHeader = zlibHeader;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getCompressionLevel() {
/* 60 */     return this.compressionLevel;
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
/*    */   
/*    */   public void setCompressionLevel(int compressionLevel) {
/* 73 */     if (compressionLevel < -1 || compressionLevel > 9) {
/* 74 */       throw new IllegalArgumentException("Invalid Deflate compression level: " + compressionLevel);
/*    */     }
/* 76 */     this.compressionLevel = compressionLevel;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\deflate\DeflateParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */