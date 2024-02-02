/*    */ package com.mysql.cj.xdevapi;
/*    */ 
/*    */ import com.mysql.cj.protocol.x.StatementExecuteOk;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class InsertResultImpl
/*    */   extends UpdateResult
/*    */   implements InsertResult
/*    */ {
/*    */   public InsertResultImpl(StatementExecuteOk ok) {
/* 46 */     super(ok);
/*    */   }
/*    */ 
/*    */   
/*    */   public Long getAutoIncrementValue() {
/* 51 */     return this.ok.getLastInsertId();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\InsertResultImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */