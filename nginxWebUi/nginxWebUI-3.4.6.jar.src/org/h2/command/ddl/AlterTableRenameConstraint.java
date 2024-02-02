/*    */ package org.h2.command.ddl;
/*    */ 
/*    */ import org.h2.constraint.Constraint;
/*    */ import org.h2.engine.Database;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.engine.User;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AlterTableRenameConstraint
/*    */   extends AlterTable
/*    */ {
/*    */   private String constraintName;
/*    */   private String newConstraintName;
/*    */   
/*    */   public AlterTableRenameConstraint(SessionLocal paramSessionLocal, Schema paramSchema) {
/* 30 */     super(paramSessionLocal, paramSchema);
/*    */   }
/*    */   
/*    */   public void setConstraintName(String paramString) {
/* 34 */     this.constraintName = paramString;
/*    */   }
/*    */   
/*    */   public void setNewConstraintName(String paramString) {
/* 38 */     this.newConstraintName = paramString;
/*    */   }
/*    */ 
/*    */   
/*    */   public long update(Table paramTable) {
/* 43 */     Constraint constraint = getSchema().findConstraint(this.session, this.constraintName);
/* 44 */     Database database = this.session.getDatabase();
/* 45 */     if (constraint == null || constraint.getConstraintType() == Constraint.Type.DOMAIN || constraint.getTable() != paramTable) {
/* 46 */       throw DbException.get(90057, this.constraintName);
/*    */     }
/* 48 */     if (getSchema().findConstraint(this.session, this.newConstraintName) != null || this.newConstraintName
/* 49 */       .equals(this.constraintName)) {
/* 50 */       throw DbException.get(90045, this.newConstraintName);
/*    */     }
/* 52 */     User user = this.session.getUser();
/* 53 */     Table table = constraint.getRefTable();
/* 54 */     if (table != paramTable) {
/* 55 */       user.checkTableRight(table, 32);
/*    */     }
/* 57 */     database.renameSchemaObject(this.session, (SchemaObject)constraint, this.newConstraintName);
/* 58 */     return 0L;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getType() {
/* 63 */     return 85;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\AlterTableRenameConstraint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */