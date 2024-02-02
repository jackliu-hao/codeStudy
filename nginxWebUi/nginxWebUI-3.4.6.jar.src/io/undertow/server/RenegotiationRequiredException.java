/*    */ package io.undertow.server;
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
/*    */ public class RenegotiationRequiredException
/*    */   extends Exception
/*    */ {
/*    */   public RenegotiationRequiredException() {}
/*    */   
/*    */   public RenegotiationRequiredException(String message) {
/* 36 */     super(message);
/*    */   }
/*    */   
/*    */   public RenegotiationRequiredException(String message, Throwable cause) {
/* 40 */     super(message, cause);
/*    */   }
/*    */   
/*    */   public RenegotiationRequiredException(Throwable cause) {
/* 44 */     super(cause);
/*    */   }
/*    */   
/*    */   public RenegotiationRequiredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
/* 48 */     super(message, cause, enableSuppression, writableStackTrace);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\RenegotiationRequiredException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */