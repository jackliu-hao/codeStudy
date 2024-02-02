/*    */ package com.mysql.cj.exceptions;
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
/*    */ public class PasswordExpiredException
/*    */   extends CJException
/*    */ {
/*    */   private static final long serialVersionUID = -3807215681364413250L;
/*    */   
/*    */   public PasswordExpiredException() {
/* 44 */     setVendorCode(1820);
/*    */   }
/*    */   
/*    */   public PasswordExpiredException(String message) {
/* 48 */     super(message);
/* 49 */     setVendorCode(1820);
/*    */   }
/*    */   
/*    */   public PasswordExpiredException(String message, Throwable cause) {
/* 53 */     super(message, cause);
/* 54 */     setVendorCode(1820);
/*    */   }
/*    */   
/*    */   public PasswordExpiredException(Throwable cause) {
/* 58 */     super(cause);
/* 59 */     setVendorCode(1820);
/*    */   }
/*    */   
/*    */   protected PasswordExpiredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
/* 63 */     super(message, cause, enableSuppression, writableStackTrace);
/* 64 */     setVendorCode(1820);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\exceptions\PasswordExpiredException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */