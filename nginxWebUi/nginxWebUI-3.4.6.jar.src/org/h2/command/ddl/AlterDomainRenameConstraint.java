/*    */ package org.h2.command.ddl;
/*    */ 
/*    */ import org.h2.constraint.Constraint;
/*    */ import org.h2.constraint.ConstraintDomain;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.schema.Domain;
/*    */ import org.h2.schema.Schema;
/*    */ import org.h2.schema.SchemaObject;
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
/*    */ public class AlterDomainRenameConstraint
/*    */   extends AlterDomain
/*    */ {
/*    */   private String constraintName;
/*    */   private String newConstraintName;
/*    */   
/*    */   public AlterDomainRenameConstraint(SessionLocal paramSessionLocal, Schema paramSchema) {
/* 28 */     super(paramSessionLocal, paramSchema);
/*    */   }
/*    */   
/*    */   public void setConstraintName(String paramString) {
/* 32 */     this.constraintName = paramString;
/*    */   }
/*    */   
/*    */   public void setNewConstraintName(String paramString) {
/* 36 */     this.newConstraintName = paramString;
/*    */   }
/*    */ 
/*    */   
/*    */   long update(Schema paramSchema, Domain paramDomain) {
/* 41 */     Constraint constraint = getSchema().findConstraint(this.session, this.constraintName);
/* 42 */     if (constraint == null || constraint.getConstraintType() != Constraint.Type.DOMAIN || ((ConstraintDomain)constraint)
/* 43 */       .getDomain() != paramDomain) {
/* 44 */       throw DbException.get(90057, this.constraintName);
/*    */     }
/* 46 */     if (getSchema().findConstraint(this.session, this.newConstraintName) != null || this.newConstraintName
/* 47 */       .equals(this.constraintName)) {
/* 48 */       throw DbException.get(90045, this.newConstraintName);
/*    */     }
/* 50 */     this.session.getDatabase().renameSchemaObject(this.session, (SchemaObject)constraint, this.newConstraintName);
/* 51 */     return 0L;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getType() {
/* 56 */     return 101;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\AlterDomainRenameConstraint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */