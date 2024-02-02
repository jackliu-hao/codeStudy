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
/*    */ public class FeatureNotAvailableException
/*    */   extends CJException
/*    */ {
/*    */   private static final long serialVersionUID = -6649508222074639690L;
/*    */   
/*    */   public FeatureNotAvailableException() {}
/*    */   
/*    */   public FeatureNotAvailableException(String message) {
/* 41 */     super(message);
/*    */   }
/*    */   
/*    */   public FeatureNotAvailableException(String message, Throwable cause) {
/* 45 */     super(message, cause);
/*    */   }
/*    */   
/*    */   public FeatureNotAvailableException(Throwable cause) {
/* 49 */     super(cause);
/*    */   }
/*    */   
/*    */   public FeatureNotAvailableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
/* 53 */     super(message, cause, enableSuppression, writableStackTrace);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\exceptions\FeatureNotAvailableException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */