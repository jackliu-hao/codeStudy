/*    */ package org.apache.http.impl.auth;
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
/*    */ public class UnsupportedDigestAlgorithmException
/*    */   extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 319558534317118022L;
/*    */   
/*    */   public UnsupportedDigestAlgorithmException() {}
/*    */   
/*    */   public UnsupportedDigestAlgorithmException(String message) {
/* 53 */     super(message);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public UnsupportedDigestAlgorithmException(String message, Throwable cause) {
/* 64 */     super(message, cause);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\auth\UnsupportedDigestAlgorithmException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */