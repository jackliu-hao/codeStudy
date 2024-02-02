/*    */ package org.h2.command.ddl;
/*    */ 
/*    */ import org.h2.constraint.Constraint;
/*    */ import org.h2.constraint.ConstraintActionType;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AlterTableDropConstraint
/*    */   extends AlterTable
/*    */ {
/*    */   private String constraintName;
/*    */   private final boolean ifExists;
/*    */   private ConstraintActionType dropAction;
/*    */   
/*    */   public AlterTableDropConstraint(SessionLocal paramSessionLocal, Schema paramSchema, boolean paramBoolean) {
/* 30 */     super(paramSessionLocal, paramSchema);
/* 31 */     this.ifExists = paramBoolean;
/* 32 */     this.dropAction = (paramSessionLocal.getDatabase().getSettings()).dropRestrict ? ConstraintActionType.RESTRICT : ConstraintActionType.CASCADE;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setConstraintName(String paramString) {
/* 37 */     this.constraintName = paramString;
/*    */   }
/*    */   
/*    */   public void setDropAction(ConstraintActionType paramConstraintActionType) {
/* 41 */     this.dropAction = paramConstraintActionType;
/*    */   }
/*    */ 
/*    */   
/*    */   public long update(Table paramTable) {
/* 46 */     Constraint constraint = getSchema().findConstraint(this.session, this.constraintName);
/*    */     Constraint.Type type;
/* 48 */     if (constraint == null || (type = constraint.getConstraintType()) == Constraint.Type.DOMAIN || constraint
/* 49 */       .getTable() != paramTable) {
/* 50 */       if (!this.ifExists) {
/* 51 */         throw DbException.get(90057, this.constraintName);
/*    */       }
/*    */     } else {
/* 54 */       Table table = constraint.getRefTable();
/* 55 */       if (table != paramTable) {
/* 56 */         this.session.getUser().checkTableRight(table, 32);
/*    */       }
/* 58 */       if (type == Constraint.Type.PRIMARY_KEY || type == Constraint.Type.UNIQUE) {
/* 59 */         for (Constraint constraint1 : constraint.getTable().getConstraints()) {
/* 60 */           if (constraint1.getReferencedConstraint() == constraint) {
/* 61 */             if (this.dropAction == ConstraintActionType.RESTRICT) {
/* 62 */               throw DbException.get(90152, new String[] { constraint
/* 63 */                     .getTraceSQL(), constraint1.getTraceSQL() });
/*    */             }
/* 65 */             Table table1 = constraint1.getTable();
/* 66 */             if (table1 != paramTable && table1 != table) {
/* 67 */               this.session.getUser().checkTableRight(table1, 32);
/*    */             }
/*    */           } 
/*    */         } 
/*    */       }
/* 72 */       this.session.getDatabase().removeSchemaObject(this.session, (SchemaObject)constraint);
/*    */     } 
/* 74 */     return 0L;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getType() {
/* 79 */     return 14;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\AlterTableDropConstraint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */