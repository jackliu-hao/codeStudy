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
/*    */ public class PropertyNotModifiableException
/*    */   extends CJException
/*    */ {
/*    */   private static final long serialVersionUID = -8001652264426656450L;
/*    */   
/*    */   public PropertyNotModifiableException() {}
/*    */   
/*    */   public PropertyNotModifiableException(String message) {
/* 41 */     super(message);
/*    */   }
/*    */   
/*    */   public PropertyNotModifiableException(String message, Throwable cause) {
/* 45 */     super(message, cause);
/*    */   }
/*    */   
/*    */   public PropertyNotModifiableException(Throwable cause) {
/* 49 */     super(cause);
/*    */   }
/*    */   
/*    */   protected PropertyNotModifiableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
/* 53 */     super(message, cause, enableSuppression, writableStackTrace);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\exceptions\PropertyNotModifiableException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */