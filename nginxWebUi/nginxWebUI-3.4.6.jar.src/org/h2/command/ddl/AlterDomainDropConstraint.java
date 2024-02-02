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
/*    */ public class AlterDomainDropConstraint
/*    */   extends AlterDomain
/*    */ {
/*    */   private String constraintName;
/*    */   private final boolean ifConstraintExists;
/*    */   
/*    */   public AlterDomainDropConstraint(SessionLocal paramSessionLocal, Schema paramSchema, boolean paramBoolean) {
/* 27 */     super(paramSessionLocal, paramSchema);
/* 28 */     this.ifConstraintExists = paramBoolean;
/*    */   }
/*    */   
/*    */   public void setConstraintName(String paramString) {
/* 32 */     this.constraintName = paramString;
/*    */   }
/*    */ 
/*    */   
/*    */   long update(Schema paramSchema, Domain paramDomain) {
/* 37 */     Constraint constraint = paramSchema.findConstraint(this.session, this.constraintName);
/* 38 */     if (constraint == null || constraint.getConstraintType() != Constraint.Type.DOMAIN || ((ConstraintDomain)constraint)
/* 39 */       .getDomain() != paramDomain) {
/* 40 */       if (!this.ifConstraintExists) {
/* 41 */         throw DbException.get(90057, this.constraintName);
/*    */       }
/*    */     } else {
/* 44 */       this.session.getDatabase().removeSchemaObject(this.session, (SchemaObject)constraint);
/*    */     } 
/* 46 */     return 0L;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getType() {
/* 51 */     return 93;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\AlterDomainDropConstraint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */