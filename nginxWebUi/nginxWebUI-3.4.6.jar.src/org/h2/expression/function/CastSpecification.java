/*     */ package org.h2.expression.function;
/*     */ 
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.TypedValueExpression;
/*     */ import org.h2.schema.Domain;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueNull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class CastSpecification
/*     */   extends Function1
/*     */ {
/*     */   private Domain domain;
/*     */   
/*     */   public CastSpecification(Expression paramExpression, Column paramColumn) {
/*  26 */     super(paramExpression);
/*  27 */     this.type = paramColumn.getType();
/*  28 */     this.domain = paramColumn.getDomain();
/*     */   }
/*     */   
/*     */   public CastSpecification(Expression paramExpression, TypeInfo paramTypeInfo) {
/*  32 */     super(paramExpression);
/*  33 */     this.type = paramTypeInfo;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal) {
/*  38 */     Value value = this.arg.getValue(paramSessionLocal).castTo(this.type, (CastDataProvider)paramSessionLocal);
/*  39 */     if (this.domain != null) {
/*  40 */       this.domain.checkConstraints(paramSessionLocal, value);
/*     */     }
/*  42 */     return value;
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/*  47 */     this.arg = this.arg.optimize(paramSessionLocal);
/*  48 */     if (this.arg.isConstant()) {
/*  49 */       Value value = getValue(paramSessionLocal);
/*  50 */       if (value == ValueNull.INSTANCE || canOptimizeCast(this.arg.getType().getValueType(), this.type.getValueType())) {
/*  51 */         return (Expression)TypedValueExpression.get(value, this.type);
/*     */       }
/*     */     } 
/*  54 */     return (Expression)this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isConstant() {
/*  59 */     return (this.arg instanceof org.h2.expression.ValueExpression && canOptimizeCast(this.arg.getType().getValueType(), this.type.getValueType()));
/*     */   }
/*     */   
/*     */   private static boolean canOptimizeCast(int paramInt1, int paramInt2) {
/*  63 */     switch (paramInt1) {
/*     */       case 18:
/*  65 */         switch (paramInt2) {
/*     */           case 19:
/*     */           case 20:
/*     */           case 21:
/*  69 */             return false;
/*     */         } 
/*     */         break;
/*     */       case 19:
/*  73 */         switch (paramInt2) {
/*     */           case 18:
/*     */           case 20:
/*     */           case 21:
/*  77 */             return false;
/*     */         } 
/*     */         break;
/*     */       case 17:
/*  81 */         if (paramInt2 == 21) {
/*  82 */           return false;
/*     */         }
/*     */         break;
/*     */       case 20:
/*  86 */         switch (paramInt2) {
/*     */           case 19:
/*     */           case 21:
/*  89 */             return false;
/*     */         } 
/*     */         break;
/*     */       case 21:
/*  93 */         switch (paramInt2) {
/*     */           case 17:
/*     */           case 18:
/*     */           case 20:
/*  97 */             return false;
/*     */         }  break;
/*     */     } 
/* 100 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 105 */     paramStringBuilder.append("CAST(");
/* 106 */     this.arg.getUnenclosedSQL(paramStringBuilder, (this.arg instanceof org.h2.expression.ValueExpression) ? (paramInt | 0x4) : paramInt).append(" AS ");
/* 107 */     return ((this.domain != null) ? this.domain : this.type).getSQL(paramStringBuilder, paramInt).append(')');
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 112 */     return "CAST";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\CastSpecification.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */