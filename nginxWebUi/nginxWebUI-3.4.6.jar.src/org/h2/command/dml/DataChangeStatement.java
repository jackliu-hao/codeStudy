/*    */ package org.h2.command.dml;
/*    */ 
/*    */ import org.h2.command.Prepared;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.result.ResultInterface;
/*    */ import org.h2.result.ResultTarget;
/*    */ import org.h2.table.DataChangeDeltaTable;
/*    */ import org.h2.table.Table;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class DataChangeStatement
/*    */   extends Prepared
/*    */ {
/*    */   protected DataChangeStatement(SessionLocal paramSessionLocal) {
/* 27 */     super(paramSessionLocal);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public abstract String getStatementName();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public abstract Table getTable();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final boolean isTransactional() {
/* 46 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public final ResultInterface queryMeta() {
/* 51 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isCacheable() {
/* 56 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public final long update() {
/* 61 */     return update(null, null);
/*    */   }
/*    */   
/*    */   public abstract long update(ResultTarget paramResultTarget, DataChangeDeltaTable.ResultOption paramResultOption);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\dml\DataChangeStatement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */