/*    */ package com.mysql.cj.xdevapi;
/*    */ 
/*    */ import com.mysql.cj.protocol.Warning;
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
/*    */ public class WarningImpl
/*    */   implements Warning
/*    */ {
/*    */   private Warning message;
/*    */   
/*    */   public WarningImpl(Warning message) {
/* 47 */     this.message = message;
/*    */   }
/*    */   
/*    */   public int getLevel() {
/* 51 */     return this.message.getLevel();
/*    */   }
/*    */   
/*    */   public long getCode() {
/* 55 */     return this.message.getCode();
/*    */   }
/*    */   
/*    */   public String getMessage() {
/* 59 */     return this.message.getMessage();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\WarningImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */