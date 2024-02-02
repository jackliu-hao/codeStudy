/*    */ package org.h2.command.ddl;
/*    */ 
/*    */ import org.h2.engine.Database;
/*    */ import org.h2.engine.DbObject;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.schema.Schema;
/*    */ import org.h2.schema.SchemaObject;
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
/*    */ public class AlterTableRename
/*    */   extends AlterTable
/*    */ {
/*    */   private String newTableName;
/*    */   private boolean hidden;
/*    */   
/*    */   public AlterTableRename(SessionLocal paramSessionLocal, Schema paramSchema) {
/* 26 */     super(paramSessionLocal, paramSchema);
/*    */   }
/*    */   
/*    */   public void setNewTableName(String paramString) {
/* 30 */     this.newTableName = paramString;
/*    */   }
/*    */ 
/*    */   
/*    */   public long update(Table paramTable) {
/* 35 */     Database database = this.session.getDatabase();
/* 36 */     Table table = getSchema().findTableOrView(this.session, this.newTableName);
/* 37 */     if (table != null && this.hidden && this.newTableName.equals(paramTable.getName())) {
/* 38 */       if (!table.isHidden()) {
/* 39 */         table.setHidden(this.hidden);
/* 40 */         paramTable.setHidden(true);
/* 41 */         database.updateMeta(this.session, (DbObject)paramTable);
/*    */       } 
/* 43 */       return 0L;
/*    */     } 
/* 45 */     if (table != null || this.newTableName.equals(paramTable.getName())) {
/* 46 */       throw DbException.get(42101, this.newTableName);
/*    */     }
/* 48 */     if (paramTable.isTemporary()) {
/* 49 */       throw DbException.getUnsupportedException("temp table");
/*    */     }
/* 51 */     database.renameSchemaObject(this.session, (SchemaObject)paramTable, this.newTableName);
/* 52 */     return 0L;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getType() {
/* 57 */     return 15;
/*    */   }
/*    */   
/*    */   public void setHidden(boolean paramBoolean) {
/* 61 */     this.hidden = paramBoolean;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\AlterTableRename.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */