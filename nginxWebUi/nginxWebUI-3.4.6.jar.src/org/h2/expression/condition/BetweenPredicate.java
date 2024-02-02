/*     */ package org.h2.expression.condition;
/*     */ 
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionVisitor;
/*     */ import org.h2.expression.TypedValueExpression;
/*     */ import org.h2.expression.ValueExpression;
/*     */ import org.h2.table.ColumnResolver;
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
/*     */ public final class BetweenPredicate
/*     */   extends Condition
/*     */ {
/*     */   private Expression left;
/*     */   private final boolean not;
/*     */   private final boolean whenOperand;
/*     */   private boolean symmetric;
/*     */   private Expression a;
/*     */   private Expression b;
/*     */   
/*     */   public BetweenPredicate(Expression paramExpression1, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, Expression paramExpression2, Expression paramExpression3) {
/*  37 */     this.left = paramExpression1;
/*  38 */     this.not = paramBoolean1;
/*  39 */     this.whenOperand = paramBoolean2;
/*  40 */     this.symmetric = paramBoolean3;
/*  41 */     this.a = paramExpression2;
/*  42 */     this.b = paramExpression3;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needParentheses() {
/*  47 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/*  52 */     return getWhenSQL(this.left.getSQL(paramStringBuilder, paramInt, 0), paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getWhenSQL(StringBuilder paramStringBuilder, int paramInt) {
/*  57 */     if (this.not) {
/*  58 */       paramStringBuilder.append(" NOT");
/*     */     }
/*  60 */     paramStringBuilder.append(" BETWEEN ");
/*  61 */     if (this.symmetric) {
/*  62 */       paramStringBuilder.append("SYMMETRIC ");
/*     */     }
/*  64 */     this.a.getSQL(paramStringBuilder, paramInt, 0).append(" AND ");
/*  65 */     return this.b.getSQL(paramStringBuilder, paramInt, 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/*  70 */     this.left = this.left.optimize(paramSessionLocal);
/*  71 */     this.a = this.a.optimize(paramSessionLocal);
/*  72 */     this.b = this.b.optimize(paramSessionLocal);
/*  73 */     TypeInfo typeInfo = this.left.getType();
/*  74 */     TypeInfo.checkComparable(typeInfo, this.a.getType());
/*  75 */     TypeInfo.checkComparable(typeInfo, this.b.getType());
/*  76 */     if (this.whenOperand) {
/*  77 */       return this;
/*     */     }
/*  79 */     Value value1 = this.left.isConstant() ? this.left.getValue(paramSessionLocal) : null;
/*  80 */     Value value2 = this.a.isConstant() ? this.a.getValue(paramSessionLocal) : null;
/*  81 */     Value value3 = this.b.isConstant() ? this.b.getValue(paramSessionLocal) : null;
/*  82 */     if (value1 != null) {
/*  83 */       if (value1 == ValueNull.INSTANCE) {
/*  84 */         return (Expression)TypedValueExpression.UNKNOWN;
/*     */       }
/*  86 */       if (value2 != null && value3 != null) {
/*  87 */         return (Expression)ValueExpression.getBoolean(getValue(paramSessionLocal, value1, value2, value3));
/*     */       }
/*     */     } 
/*  90 */     if (this.symmetric) {
/*  91 */       if (value2 == ValueNull.INSTANCE || value3 == ValueNull.INSTANCE) {
/*  92 */         return (Expression)TypedValueExpression.UNKNOWN;
/*     */       }
/*  94 */     } else if (value2 == ValueNull.INSTANCE && value3 == ValueNull.INSTANCE) {
/*  95 */       return (Expression)TypedValueExpression.UNKNOWN;
/*     */     } 
/*  97 */     if (value2 != null && value3 != null && paramSessionLocal.compareWithNull(value2, value3, false) == 0) {
/*  98 */       return (new Comparison(this.not ? 1 : 0, this.left, this.a, false)).optimize(paramSessionLocal);
/*     */     }
/* 100 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal) {
/* 105 */     Value value = this.left.getValue(paramSessionLocal);
/* 106 */     if (value == ValueNull.INSTANCE) {
/* 107 */       return (Value)ValueNull.INSTANCE;
/*     */     }
/* 109 */     return getValue(paramSessionLocal, value, this.a.getValue(paramSessionLocal), this.b.getValue(paramSessionLocal));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getWhenValue(SessionLocal paramSessionLocal, Value paramValue) {
/* 114 */     if (!this.whenOperand) {
/* 115 */       return super.getWhenValue(paramSessionLocal, paramValue);
/*     */     }
/* 117 */     if (paramValue == ValueNull.INSTANCE) {
/* 118 */       return false;
/*     */     }
/* 120 */     return getValue(paramSessionLocal, paramValue, this.a.getValue(paramSessionLocal), this.b.getValue(paramSessionLocal)).isTrue();
/*     */   }
/*     */   
/*     */   private Value getValue(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2, Value paramValue3) {
/* 124 */     int i = paramSessionLocal.compareWithNull(paramValue2, paramValue1, false);
/* 125 */     int j = paramSessionLocal.compareWithNull(paramValue1, paramValue3, false);
/* 126 */     if (i == Integer.MIN_VALUE)
/* 127 */       return (this.symmetric || j <= 0) ? (Value)ValueNull.INSTANCE : (Value)ValueBoolean.get(this.not); 
/* 128 */     if (j == Integer.MIN_VALUE) {
/* 129 */       return (this.symmetric || i <= 0) ? (Value)ValueNull.INSTANCE : (Value)ValueBoolean.get(this.not);
/*     */     }
/* 131 */     return (Value)ValueBoolean.get(this.not ^ (this.symmetric ? (((i <= 0 && j <= 0) || (i >= 0 && j >= 0))) : ((i <= 0 && j <= 0))));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isWhenConditionOperand() {
/* 138 */     return this.whenOperand;
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression getNotIfPossible(SessionLocal paramSessionLocal) {
/* 143 */     if (this.whenOperand) {
/* 144 */       return null;
/*     */     }
/* 146 */     return new BetweenPredicate(this.left, !this.not, false, this.symmetric, this.a, this.b);
/*     */   }
/*     */ 
/*     */   
/*     */   public void createIndexConditions(SessionLocal paramSessionLocal, TableFilter paramTableFilter) {
/* 151 */     if (!this.not && !this.whenOperand && !this.symmetric) {
/* 152 */       Comparison.createIndexConditions(paramTableFilter, this.a, this.left, 4);
/* 153 */       Comparison.createIndexConditions(paramTableFilter, this.left, this.b, 4);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEvaluatable(TableFilter paramTableFilter, boolean paramBoolean) {
/* 159 */     this.left.setEvaluatable(paramTableFilter, paramBoolean);
/* 160 */     this.a.setEvaluatable(paramTableFilter, paramBoolean);
/* 161 */     this.b.setEvaluatable(paramTableFilter, paramBoolean);
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateAggregate(SessionLocal paramSessionLocal, int paramInt) {
/* 166 */     this.left.updateAggregate(paramSessionLocal, paramInt);
/* 167 */     this.a.updateAggregate(paramSessionLocal, paramInt);
/* 168 */     this.b.updateAggregate(paramSessionLocal, paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public void mapColumns(ColumnResolver paramColumnResolver, int paramInt1, int paramInt2) {
/* 173 */     this.left.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/* 174 */     this.a.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/* 175 */     this.b.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 180 */     return (this.left.isEverything(paramExpressionVisitor) && this.a.isEverything(paramExpressionVisitor) && this.b.isEverything(paramExpressionVisitor));
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCost() {
/* 185 */     return this.left.getCost() + this.a.getCost() + this.b.getCost() + 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSubexpressionCount() {
/* 190 */     return 3;
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression getSubexpression(int paramInt) {
/* 195 */     switch (paramInt) {
/*     */       case 0:
/* 197 */         return this.left;
/*     */       case 1:
/* 199 */         return this.a;
/*     */       case 2:
/* 201 */         return this.b;
/*     */     } 
/* 203 */     throw new IndexOutOfBoundsException();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\condition\BetweenPredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */