/*    */ package com.google.protobuf;
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
/*    */ public class ServiceException
/*    */   extends Exception
/*    */ {
/*    */   private static final long serialVersionUID = -1219262335729891920L;
/*    */   
/*    */   public ServiceException(String message) {
/* 42 */     super(message);
/*    */   }
/*    */   
/*    */   public ServiceException(Throwable cause) {
/* 46 */     super(cause);
/*    */   }
/*    */   
/*    */   public ServiceException(String message, Throwable cause) {
/* 50 */     super(message, cause);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\ServiceException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */