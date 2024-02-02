/*     */ package org.h2.expression;
/*     */ 
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.condition.Comparison;
/*     */ import org.h2.message.DbException;
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
/*     */ 
/*     */ public final class Parameter
/*     */   extends Operation0
/*     */   implements ParameterInterface
/*     */ {
/*     */   private Value value;
/*     */   private Column column;
/*     */   private final int index;
/*     */   
/*     */   public Parameter(int paramInt) {
/*  28 */     this.index = paramInt;
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/*  33 */     return paramStringBuilder.append('?').append(this.index + 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setValue(Value paramValue, boolean paramBoolean) {
/*  40 */     this.value = paramValue;
/*     */   }
/*     */   
/*     */   public void setValue(Value paramValue) {
/*  44 */     this.value = paramValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value getParamValue() {
/*  49 */     if (this.value == null)
/*     */     {
/*  51 */       return (Value)ValueNull.INSTANCE;
/*     */     }
/*  53 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal) {
/*  58 */     return getParamValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/*  63 */     if (this.value != null) {
/*  64 */       return this.value.getType();
/*     */     }
/*  66 */     if (this.column != null) {
/*  67 */       return this.column.getType();
/*     */     }
/*  69 */     return TypeInfo.TYPE_UNKNOWN;
/*     */   }
/*     */ 
/*     */   
/*     */   public void checkSet() {
/*  74 */     if (this.value == null) {
/*  75 */       throw DbException.get(90012, "#" + (this.index + 1));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/*  81 */     if ((paramSessionLocal.getDatabase().getMode()).treatEmptyStringsAsNull && 
/*  82 */       this.value instanceof org.h2.value.ValueVarchar && this.value.getString().isEmpty()) {
/*  83 */       this.value = (Value)ValueNull.INSTANCE;
/*     */     }
/*     */     
/*  86 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isValueSet() {
/*  91 */     return (this.value != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/*  96 */     switch (paramExpressionVisitor.getType()) {
/*     */       case 0:
/*  98 */         return (this.value != null);
/*     */     } 
/* 100 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getCost() {
/* 106 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression getNotIfPossible(SessionLocal paramSessionLocal) {
/* 111 */     return (Expression)new Comparison(0, this, ValueExpression.FALSE, false);
/*     */   }
/*     */   
/*     */   public void setColumn(Column paramColumn) {
/* 115 */     this.column = paramColumn;
/*     */   }
/*     */   
/*     */   public int getIndex() {
/* 119 */     return this.index;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\Parameter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */