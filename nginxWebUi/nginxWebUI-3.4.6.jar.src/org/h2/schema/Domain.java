/*     */ package org.h2.schema;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import org.h2.constraint.Constraint;
/*     */ import org.h2.constraint.ConstraintDomain;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ValueExpression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.table.ColumnTemplate;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.util.Utils;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Domain
/*     */   extends SchemaObject
/*     */   implements ColumnTemplate
/*     */ {
/*     */   private TypeInfo type;
/*     */   private Domain domain;
/*     */   private Expression defaultExpression;
/*     */   private Expression onUpdateExpression;
/*     */   private ArrayList<ConstraintDomain> constraints;
/*     */   
/*     */   public Domain(Schema paramSchema, int paramInt, String paramString) {
/*  42 */     super(paramSchema, paramInt, paramString, 8);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCreateSQLForCopy(Table paramTable, String paramString) {
/*  47 */     throw DbException.getInternalError(toString());
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDropSQL() {
/*  52 */     StringBuilder stringBuilder = new StringBuilder("DROP DOMAIN IF EXISTS ");
/*  53 */     return getSQL(stringBuilder, 0).toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCreateSQL() {
/*  58 */     StringBuilder stringBuilder = getSQL(new StringBuilder("CREATE DOMAIN "), 0).append(" AS ");
/*  59 */     if (this.domain != null) {
/*  60 */       this.domain.getSQL(stringBuilder, 0);
/*     */     } else {
/*  62 */       this.type.getSQL(stringBuilder, 0);
/*     */     } 
/*  64 */     if (this.defaultExpression != null) {
/*  65 */       this.defaultExpression.getUnenclosedSQL(stringBuilder.append(" DEFAULT "), 0);
/*     */     }
/*  67 */     if (this.onUpdateExpression != null) {
/*  68 */       this.onUpdateExpression.getUnenclosedSQL(stringBuilder.append(" ON UPDATE "), 0);
/*     */     }
/*  70 */     return stringBuilder.toString();
/*     */   }
/*     */   
/*     */   public void setDataType(TypeInfo paramTypeInfo) {
/*  74 */     this.type = paramTypeInfo;
/*     */   }
/*     */   
/*     */   public TypeInfo getDataType() {
/*  78 */     return this.type;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDomain(Domain paramDomain) {
/*  83 */     this.domain = paramDomain;
/*     */   }
/*     */ 
/*     */   
/*     */   public Domain getDomain() {
/*  88 */     return this.domain;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDefaultExpression(SessionLocal paramSessionLocal, Expression paramExpression) {
/*     */     ValueExpression valueExpression;
/*  94 */     if (paramExpression != null) {
/*  95 */       paramExpression = paramExpression.optimize(paramSessionLocal);
/*  96 */       if (paramExpression.isConstant()) {
/*  97 */         valueExpression = ValueExpression.get(paramExpression.getValue(paramSessionLocal));
/*     */       }
/*     */     } 
/* 100 */     this.defaultExpression = (Expression)valueExpression;
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression getDefaultExpression() {
/* 105 */     return this.defaultExpression;
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression getEffectiveDefaultExpression() {
/* 110 */     return (this.defaultExpression != null) ? this.defaultExpression : ((this.domain != null) ? this.domain
/* 111 */       .getEffectiveDefaultExpression() : null);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDefaultSQL() {
/* 116 */     return (this.defaultExpression == null) ? null : this.defaultExpression
/* 117 */       .getUnenclosedSQL(new StringBuilder(), 0).toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setOnUpdateExpression(SessionLocal paramSessionLocal, Expression paramExpression) {
/*     */     ValueExpression valueExpression;
/* 123 */     if (paramExpression != null) {
/* 124 */       paramExpression = paramExpression.optimize(paramSessionLocal);
/* 125 */       if (paramExpression.isConstant()) {
/* 126 */         valueExpression = ValueExpression.get(paramExpression.getValue(paramSessionLocal));
/*     */       }
/*     */     } 
/* 129 */     this.onUpdateExpression = (Expression)valueExpression;
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression getOnUpdateExpression() {
/* 134 */     return this.onUpdateExpression;
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression getEffectiveOnUpdateExpression() {
/* 139 */     return (this.onUpdateExpression != null) ? this.onUpdateExpression : ((this.domain != null) ? this.domain
/* 140 */       .getEffectiveOnUpdateExpression() : null);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getOnUpdateSQL() {
/* 145 */     return (this.onUpdateExpression == null) ? null : this.onUpdateExpression
/* 146 */       .getUnenclosedSQL(new StringBuilder(), 0).toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public void prepareExpressions(SessionLocal paramSessionLocal) {
/* 151 */     if (this.defaultExpression != null) {
/* 152 */       this.defaultExpression = this.defaultExpression.optimize(paramSessionLocal);
/*     */     }
/* 154 */     if (this.onUpdateExpression != null) {
/* 155 */       this.onUpdateExpression = this.onUpdateExpression.optimize(paramSessionLocal);
/*     */     }
/* 157 */     if (this.domain != null) {
/* 158 */       this.domain.prepareExpressions(paramSessionLocal);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addConstraint(ConstraintDomain paramConstraintDomain) {
/* 168 */     if (this.constraints == null) {
/* 169 */       this.constraints = Utils.newSmallArrayList();
/*     */     }
/* 171 */     if (!this.constraints.contains(paramConstraintDomain)) {
/* 172 */       this.constraints.add(paramConstraintDomain);
/*     */     }
/*     */   }
/*     */   
/*     */   public ArrayList<ConstraintDomain> getConstraints() {
/* 177 */     return this.constraints;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeConstraint(Constraint paramConstraint) {
/* 186 */     if (this.constraints != null) {
/* 187 */       this.constraints.remove(paramConstraint);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/* 193 */     return 12;
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeChildrenAndResources(SessionLocal paramSessionLocal) {
/* 198 */     if (this.constraints != null && !this.constraints.isEmpty()) {
/* 199 */       for (ConstraintDomain constraintDomain : (ConstraintDomain[])this.constraints.<ConstraintDomain>toArray(new ConstraintDomain[0])) {
/* 200 */         this.database.removeSchemaObject(paramSessionLocal, (SchemaObject)constraintDomain);
/*     */       }
/* 202 */       this.constraints = null;
/*     */     } 
/* 204 */     this.database.removeMeta(paramSessionLocal, getId());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void checkConstraints(SessionLocal paramSessionLocal, Value paramValue) {
/* 214 */     if (this.constraints != null) {
/* 215 */       for (ConstraintDomain constraintDomain : this.constraints) {
/* 216 */         constraintDomain.check(paramSessionLocal, paramValue);
/*     */       }
/*     */     }
/* 219 */     if (this.domain != null)
/* 220 */       this.domain.checkConstraints(paramSessionLocal, paramValue); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\schema\Domain.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */