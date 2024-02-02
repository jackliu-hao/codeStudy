/*     */ package org.h2.expression;
/*     */ 
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.table.ColumnResolver;
/*     */ import org.h2.table.TableFilter;
/*     */ import org.h2.value.ExtTypeInfo;
/*     */ import org.h2.value.ExtTypeInfoRow;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Typed;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueArray;
/*     */ import org.h2.value.ValueRow;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ExpressionList
/*     */   extends Expression
/*     */ {
/*     */   private final Expression[] list;
/*     */   private final boolean isArray;
/*     */   private TypeInfo type;
/*     */   
/*     */   public ExpressionList(Expression[] paramArrayOfExpression, boolean paramBoolean) {
/*  28 */     this.list = paramArrayOfExpression;
/*  29 */     this.isArray = paramBoolean;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal) {
/*  34 */     Value[] arrayOfValue = new Value[this.list.length];
/*  35 */     for (byte b = 0; b < this.list.length; b++) {
/*  36 */       arrayOfValue[b] = this.list[b].getValue(paramSessionLocal);
/*     */     }
/*  38 */     return this.isArray ? (Value)ValueArray.get((TypeInfo)this.type.getExtTypeInfo(), arrayOfValue, (CastDataProvider)paramSessionLocal) : (Value)ValueRow.get(this.type, arrayOfValue);
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/*  43 */     return this.type;
/*     */   }
/*     */ 
/*     */   
/*     */   public void mapColumns(ColumnResolver paramColumnResolver, int paramInt1, int paramInt2) {
/*  48 */     for (Expression expression : this.list) {
/*  49 */       expression.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/*  55 */     boolean bool = true;
/*  56 */     int i = this.list.length;
/*  57 */     for (byte b = 0; b < i; b++) {
/*  58 */       Expression expression = this.list[b].optimize(paramSessionLocal);
/*  59 */       if (!expression.isConstant()) {
/*  60 */         bool = false;
/*     */       }
/*  62 */       this.list[b] = expression;
/*     */     } 
/*  64 */     initializeType();
/*  65 */     if (bool) {
/*  66 */       return ValueExpression.get(getValue(paramSessionLocal));
/*     */     }
/*  68 */     return this;
/*     */   }
/*     */   
/*     */   void initializeType() {
/*  72 */     this
/*  73 */       .type = this.isArray ? TypeInfo.getTypeInfo(40, this.list.length, 0, (ExtTypeInfo)TypeInfo.getHigherType((Typed[])this.list)) : TypeInfo.getTypeInfo(41, 0L, 0, (ExtTypeInfo)new ExtTypeInfoRow((Typed[])this.list));
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEvaluatable(TableFilter paramTableFilter, boolean paramBoolean) {
/*  78 */     for (Expression expression : this.list) {
/*  79 */       expression.setEvaluatable(paramTableFilter, paramBoolean);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/*  85 */     return this.isArray ? 
/*  86 */       writeExpressions(paramStringBuilder.append("ARRAY ["), this.list, paramInt).append(']') : 
/*  87 */       writeExpressions(paramStringBuilder.append("ROW ("), this.list, paramInt).append(')');
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateAggregate(SessionLocal paramSessionLocal, int paramInt) {
/*  92 */     for (Expression expression : this.list) {
/*  93 */       expression.updateAggregate(paramSessionLocal, paramInt);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/*  99 */     for (Expression expression : this.list) {
/* 100 */       if (!expression.isEverything(paramExpressionVisitor)) {
/* 101 */         return false;
/*     */       }
/*     */     } 
/* 104 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCost() {
/* 109 */     int i = 1;
/* 110 */     for (Expression expression : this.list) {
/* 111 */       i += expression.getCost();
/*     */     }
/* 113 */     return i;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isConstant() {
/* 118 */     for (Expression expression : this.list) {
/* 119 */       if (!expression.isConstant()) {
/* 120 */         return false;
/*     */       }
/*     */     } 
/* 123 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSubexpressionCount() {
/* 128 */     return this.list.length;
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression getSubexpression(int paramInt) {
/* 133 */     return this.list[paramInt];
/*     */   }
/*     */   
/*     */   public boolean isArray() {
/* 137 */     return this.isArray;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\ExpressionList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */