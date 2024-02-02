/*    */ package com.mysql.cj.xdevapi;
/*    */ 
/*    */ import com.mysql.cj.protocol.x.StatementExecuteOk;
/*    */ import java.util.Iterator;
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
/*    */ public class UpdateResult
/*    */   implements Result
/*    */ {
/*    */   protected StatementExecuteOk ok;
/*    */   
/*    */   public UpdateResult(StatementExecuteOk ok) {
/* 49 */     this.ok = ok;
/*    */   }
/*    */ 
/*    */   
/*    */   public long getAffectedItemsCount() {
/* 54 */     return this.ok.getAffectedItemsCount();
/*    */   }
/*    */ 
/*    */   
/*    */   public int getWarningsCount() {
/* 59 */     return this.ok.getWarningsCount();
/*    */   }
/*    */ 
/*    */   
/*    */   public Iterator<Warning> getWarnings() {
/* 64 */     return this.ok.getWarnings();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\UpdateResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */