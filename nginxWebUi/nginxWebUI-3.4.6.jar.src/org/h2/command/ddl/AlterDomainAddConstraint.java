/*     */ package org.h2.command.ddl;
/*     */ 
/*     */ import org.h2.constraint.ConstraintDomain;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.schema.Domain;
/*     */ import org.h2.schema.Schema;
/*     */ import org.h2.schema.SchemaObject;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AlterDomainAddConstraint
/*     */   extends AlterDomain
/*     */ {
/*     */   private String constraintName;
/*     */   private Expression checkExpression;
/*     */   private String comment;
/*     */   private boolean checkExisting;
/*     */   private final boolean ifNotExists;
/*     */   
/*     */   public AlterDomainAddConstraint(SessionLocal paramSessionLocal, Schema paramSchema, boolean paramBoolean) {
/*  30 */     super(paramSessionLocal, paramSchema);
/*  31 */     this.ifNotExists = paramBoolean;
/*     */   }
/*     */   
/*     */   private String generateConstraintName(Domain paramDomain) {
/*  35 */     if (this.constraintName == null) {
/*  36 */       this.constraintName = getSchema().getUniqueDomainConstraintName(this.session, paramDomain);
/*     */     }
/*  38 */     return this.constraintName;
/*     */   }
/*     */ 
/*     */   
/*     */   long update(Schema paramSchema, Domain paramDomain) {
/*     */     try {
/*  44 */       return tryUpdate(paramSchema, paramDomain);
/*     */     } finally {
/*  46 */       getSchema().freeUniqueName(this.constraintName);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int tryUpdate(Schema paramSchema, Domain paramDomain) {
/*  58 */     if (this.constraintName != null && paramSchema.findConstraint(this.session, this.constraintName) != null) {
/*  59 */       if (this.ifNotExists) {
/*  60 */         return 0;
/*     */       }
/*  62 */       throw DbException.get(90045, this.constraintName);
/*     */     } 
/*  64 */     Database database = this.session.getDatabase();
/*  65 */     database.lockMeta(this.session);
/*     */     
/*  67 */     int i = getObjectId();
/*  68 */     String str = generateConstraintName(paramDomain);
/*  69 */     ConstraintDomain constraintDomain = new ConstraintDomain(paramSchema, i, str, paramDomain);
/*  70 */     constraintDomain.setExpression(this.session, this.checkExpression);
/*  71 */     if (this.checkExisting) {
/*  72 */       constraintDomain.checkExistingData(this.session);
/*     */     }
/*  74 */     constraintDomain.setComment(this.comment);
/*  75 */     database.addSchemaObject(this.session, (SchemaObject)constraintDomain);
/*  76 */     paramDomain.addConstraint(constraintDomain);
/*  77 */     return 0;
/*     */   }
/*     */   
/*     */   public void setConstraintName(String paramString) {
/*  81 */     this.constraintName = paramString;
/*     */   }
/*     */   
/*     */   public String getConstraintName() {
/*  85 */     return this.constraintName;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/*  90 */     return 92;
/*     */   }
/*     */   
/*     */   public void setCheckExpression(Expression paramExpression) {
/*  94 */     this.checkExpression = paramExpression;
/*     */   }
/*     */   
/*     */   public void setComment(String paramString) {
/*  98 */     this.comment = paramString;
/*     */   }
/*     */   
/*     */   public void setCheckExisting(boolean paramBoolean) {
/* 102 */     this.checkExisting = paramBoolean;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\AlterDomainAddConstraint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */