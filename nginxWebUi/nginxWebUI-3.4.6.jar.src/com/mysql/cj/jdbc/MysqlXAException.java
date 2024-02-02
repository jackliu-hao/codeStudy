/*    */ package com.mysql.cj.jdbc;
/*    */ 
/*    */ import javax.transaction.xa.XAException;
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
/*    */ class MysqlXAException
/*    */   extends XAException
/*    */ {
/*    */   private static final long serialVersionUID = -9075817535836563004L;
/*    */   private String message;
/*    */   protected String xidAsString;
/*    */   
/*    */   public MysqlXAException(int errorCode, String message, String xidAsString) {
/* 44 */     super(errorCode);
/* 45 */     this.message = message;
/* 46 */     this.xidAsString = xidAsString;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public MysqlXAException(String message, String xidAsString) {
/* 52 */     this.message = message;
/* 53 */     this.xidAsString = xidAsString;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getMessage() {
/* 58 */     String superMessage = super.getMessage();
/* 59 */     StringBuilder returnedMessage = new StringBuilder();
/*    */     
/* 61 */     if (superMessage != null) {
/* 62 */       returnedMessage.append(superMessage);
/* 63 */       returnedMessage.append(":");
/*    */     } 
/*    */     
/* 66 */     if (this.message != null) {
/* 67 */       returnedMessage.append(this.message);
/*    */     }
/*    */     
/* 70 */     return returnedMessage.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\MysqlXAException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */