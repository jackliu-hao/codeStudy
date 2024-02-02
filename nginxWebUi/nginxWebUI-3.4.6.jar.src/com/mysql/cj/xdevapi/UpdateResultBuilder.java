/*    */ package com.mysql.cj.xdevapi;
/*    */ 
/*    */ import com.mysql.cj.exceptions.ExceptionFactory;
/*    */ import com.mysql.cj.exceptions.WrongArgumentException;
/*    */ import com.mysql.cj.protocol.ProtocolEntity;
/*    */ import com.mysql.cj.protocol.ResultBuilder;
/*    */ import com.mysql.cj.protocol.x.StatementExecuteOkBuilder;
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
/*    */ public class UpdateResultBuilder<T extends Result>
/*    */   implements ResultBuilder<T>
/*    */ {
/* 46 */   protected StatementExecuteOkBuilder statementExecuteOkBuilder = new StatementExecuteOkBuilder();
/*    */ 
/*    */   
/*    */   public boolean addProtocolEntity(ProtocolEntity entity) {
/* 50 */     if (entity instanceof com.mysql.cj.protocol.x.Notice) {
/* 51 */       this.statementExecuteOkBuilder.addProtocolEntity(entity);
/* 52 */       return false;
/*    */     } 
/* 54 */     if (entity instanceof com.mysql.cj.protocol.x.StatementExecuteOk) {
/* 55 */       return true;
/*    */     }
/* 57 */     if (entity instanceof com.mysql.cj.protocol.x.Ok) {
/* 58 */       return true;
/*    */     }
/* 60 */     throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "Unexpected protocol entity " + entity);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public T build() {
/* 66 */     return (T)new UpdateResult(this.statementExecuteOkBuilder.build());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\UpdateResultBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */