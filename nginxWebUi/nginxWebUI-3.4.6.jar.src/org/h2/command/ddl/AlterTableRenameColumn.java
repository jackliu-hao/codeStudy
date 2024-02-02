/*    */ package org.h2.command.ddl;
/*    */ 
/*    */ import org.h2.constraint.ConstraintReferential;
/*    */ import org.h2.engine.Database;
/*    */ import org.h2.engine.DbObject;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.schema.Schema;
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
/*    */ public class AlterTableRenameColumn
/*    */   extends AlterTable
/*    */ {
/*    */   private boolean ifExists;
/*    */   private String oldName;
/*    */   private String newName;
/*    */   
/*    */   public AlterTableRenameColumn(SessionLocal paramSessionLocal, Schema paramSchema) {
/* 28 */     super(paramSessionLocal, paramSchema);
/*    */   }
/*    */   
/*    */   public void setIfExists(boolean paramBoolean) {
/* 32 */     this.ifExists = paramBoolean;
/*    */   }
/*    */   
/*    */   public void setOldColumnName(String paramString) {
/* 36 */     this.oldName = paramString;
/*    */   }
/*    */   
/*    */   public void setNewColumnName(String paramString) {
/* 40 */     this.newName = paramString;
/*    */   }
/*    */ 
/*    */   
/*    */   public long update(Table paramTable) {
/* 45 */     Column column = paramTable.getColumn(this.oldName, this.ifExists);
/* 46 */     if (column == null) {
/* 47 */       return 0L;
/*    */     }
/* 49 */     paramTable.checkSupportAlter();
/* 50 */     paramTable.renameColumn(column, this.newName);
/* 51 */     paramTable.setModified();
/* 52 */     Database database = this.session.getDatabase();
/* 53 */     database.updateMeta(this.session, (DbObject)paramTable);
/*    */ 
/*    */     
/* 56 */     for (DbObject dbObject : paramTable.getChildren()) {
/* 57 */       if (dbObject instanceof ConstraintReferential) {
/* 58 */         ConstraintReferential constraintReferential = (ConstraintReferential)dbObject;
/* 59 */         constraintReferential.updateOnTableColumnRename();
/*    */       } 
/*    */     } 
/*    */     
/* 63 */     for (DbObject dbObject : paramTable.getChildren()) {
/* 64 */       if (dbObject.getCreateSQL() != null) {
/* 65 */         database.updateMeta(this.session, dbObject);
/*    */       }
/*    */     } 
/* 68 */     return 0L;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getType() {
/* 73 */     return 16;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\AlterTableRenameColumn.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */