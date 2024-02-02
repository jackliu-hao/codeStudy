/*    */ package org.h2.command.ddl;
/*    */ 
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.schema.Schema;
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
/*    */ public abstract class AlterTable
/*    */   extends SchemaCommand
/*    */ {
/*    */   String tableName;
/*    */   boolean ifTableExists;
/*    */   
/*    */   AlterTable(SessionLocal paramSessionLocal, Schema paramSchema) {
/* 25 */     super(paramSessionLocal, paramSchema);
/*    */   }
/*    */   
/*    */   public final void setTableName(String paramString) {
/* 29 */     this.tableName = paramString;
/*    */   }
/*    */   
/*    */   public final void setIfTableExists(boolean paramBoolean) {
/* 33 */     this.ifTableExists = paramBoolean;
/*    */   }
/*    */ 
/*    */   
/*    */   public final long update() {
/* 38 */     Table table = getSchema().findTableOrView(this.session, this.tableName);
/* 39 */     if (table == null) {
/* 40 */       if (this.ifTableExists) {
/* 41 */         return 0L;
/*    */       }
/* 43 */       throw DbException.get(42102, this.tableName);
/*    */     } 
/* 45 */     this.session.getUser().checkTableRight(table, 32);
/* 46 */     return update(table);
/*    */   }
/*    */   
/*    */   abstract long update(Table paramTable);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\AlterTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */