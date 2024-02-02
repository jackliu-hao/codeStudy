/*    */ package com.sun.mail.iap;
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
/*    */ public class ProtocolException
/*    */   extends Exception
/*    */ {
/* 48 */   protected transient Response response = null;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static final long serialVersionUID = -4360500807971797439L;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ProtocolException() {}
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ProtocolException(String message) {
/* 64 */     super(message);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ProtocolException(String message, Throwable cause) {
/* 74 */     super(message, cause);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ProtocolException(Response r) {
/* 81 */     super(r.toString());
/* 82 */     this.response = r;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Response getResponse() {
/* 89 */     return this.response;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\iap\ProtocolException.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */