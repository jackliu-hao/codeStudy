/*    */ package org.apache.commons.compress.archivers.dump;
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
/*    */ public class UnsupportedCompressionAlgorithmException
/*    */   extends DumpArchiveException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public UnsupportedCompressionAlgorithmException() {
/* 31 */     super("this file uses an unsupported compression algorithm.");
/*    */   }
/*    */   
/*    */   public UnsupportedCompressionAlgorithmException(String alg) {
/* 35 */     super("this file uses an unsupported compression algorithm: " + alg + ".");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\dump\UnsupportedCompressionAlgorithmException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */