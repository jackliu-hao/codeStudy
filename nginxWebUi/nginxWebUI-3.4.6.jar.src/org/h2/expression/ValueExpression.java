/*     */ package org.h2.expression;
/*     */ 
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.index.IndexCondition;
/*     */ import org.h2.table.TableFilter;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueBoolean;
/*     */ import org.h2.value.ValueNull;
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
/*     */ public class ValueExpression
/*     */   extends Operation0
/*     */ {
/*  25 */   public static final ValueExpression NULL = new ValueExpression((Value)ValueNull.INSTANCE);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  32 */   public static final ValueExpression DEFAULT = new ValueExpression((Value)ValueNull.INSTANCE);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  37 */   public static final ValueExpression TRUE = new ValueExpression((Value)ValueBoolean.TRUE);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  42 */   public static final ValueExpression FALSE = new ValueExpression((Value)ValueBoolean.FALSE);
/*     */ 
/*     */   
/*     */   final Value value;
/*     */ 
/*     */ 
/*     */   
/*     */   ValueExpression(Value paramValue) {
/*  50 */     this.value = paramValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueExpression get(Value paramValue) {
/*  60 */     if (paramValue == ValueNull.INSTANCE) {
/*  61 */       return NULL;
/*     */     }
/*  63 */     if (paramValue.getValueType() == 8) {
/*  64 */       return getBoolean(paramValue.getBoolean());
/*     */     }
/*  66 */     return new ValueExpression(paramValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueExpression getBoolean(Value paramValue) {
/*  76 */     if (paramValue == ValueNull.INSTANCE) {
/*  77 */       return TypedValueExpression.UNKNOWN;
/*     */     }
/*  79 */     return getBoolean(paramValue.getBoolean());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueExpression getBoolean(boolean paramBoolean) {
/*  89 */     return paramBoolean ? TRUE : FALSE;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal) {
/*  94 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/*  99 */     return this.value.getType();
/*     */   }
/*     */ 
/*     */   
/*     */   public void createIndexConditions(SessionLocal paramSessionLocal, TableFilter paramTableFilter) {
/* 104 */     if (this.value.getValueType() == 8 && !this.value.getBoolean()) {
/* 105 */       paramTableFilter.addIndexCondition(IndexCondition.get(9, null, this));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression getNotIfPossible(SessionLocal paramSessionLocal) {
/* 111 */     if (this.value == ValueNull.INSTANCE) {
/* 112 */       return TypedValueExpression.UNKNOWN;
/*     */     }
/* 114 */     return getBoolean(!this.value.getBoolean());
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isConstant() {
/* 119 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isNullConstant() {
/* 124 */     return (this == NULL);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isValueSet() {
/* 129 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 134 */     if (this == DEFAULT) {
/* 135 */       paramStringBuilder.append("DEFAULT");
/*     */     } else {
/* 137 */       this.value.getSQL(paramStringBuilder, paramInt);
/*     */     } 
/* 139 */     return paramStringBuilder;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 144 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCost() {
/* 149 */     return 0;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\ValueExpression.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */