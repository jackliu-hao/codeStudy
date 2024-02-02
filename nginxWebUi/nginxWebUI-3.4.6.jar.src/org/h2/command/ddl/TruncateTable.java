/*    */ package org.h2.command.ddl;
/*    */ 
/*    */ import org.h2.engine.DbObject;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.schema.Sequence;
/*    */ import org.h2.table.Column;
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
/*    */ public class TruncateTable
/*    */   extends DefineCommand
/*    */ {
/*    */   private Table table;
/*    */   private boolean restart;
/*    */   
/*    */   public TruncateTable(SessionLocal paramSessionLocal) {
/* 28 */     super(paramSessionLocal);
/*    */   }
/*    */   
/*    */   public void setTable(Table paramTable) {
/* 32 */     this.table = paramTable;
/*    */   }
/*    */   
/*    */   public void setRestart(boolean paramBoolean) {
/* 36 */     this.restart = paramBoolean;
/*    */   }
/*    */ 
/*    */   
/*    */   public long update() {
/* 41 */     if (!this.table.canTruncate()) {
/* 42 */       throw DbException.get(90106, this.table.getTraceSQL());
/*    */     }
/* 44 */     this.session.getUser().checkTableRight(this.table, 2);
/* 45 */     this.table.lock(this.session, 2);
/* 46 */     long l = this.table.truncate(this.session);
/* 47 */     if (this.restart) {
/* 48 */       for (Column column : this.table.getColumns()) {
/* 49 */         Sequence sequence = column.getSequence();
/* 50 */         if (sequence != null) {
/* 51 */           sequence.modify(Long.valueOf(sequence.getStartValue()), null, null, null, null, null, null);
/* 52 */           this.session.getDatabase().updateMeta(this.session, (DbObject)sequence);
/*    */         } 
/*    */       } 
/*    */     }
/* 56 */     return l;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getType() {
/* 61 */     return 53;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\TruncateTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */