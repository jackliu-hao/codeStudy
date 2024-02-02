/*     */ package org.h2.expression.condition;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionColumn;
/*     */ import org.h2.expression.ExpressionList;
/*     */ import org.h2.expression.ValueExpression;
/*     */ import org.h2.index.IndexCondition;
/*     */ import org.h2.table.TableFilter;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueBoolean;
/*     */ import org.h2.value.ValueNull;
/*     */ import org.h2.value.ValueRow;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class NullPredicate
/*     */   extends SimplePredicate
/*     */ {
/*     */   private boolean optimized;
/*     */   
/*     */   public NullPredicate(Expression paramExpression, boolean paramBoolean1, boolean paramBoolean2) {
/*  30 */     super(paramExpression, paramBoolean1, paramBoolean2);
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/*  35 */     return getWhenSQL(this.left.getSQL(paramStringBuilder, paramInt, 0), paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getWhenSQL(StringBuilder paramStringBuilder, int paramInt) {
/*  40 */     return paramStringBuilder.append(this.not ? " IS NOT NULL" : " IS NULL");
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/*  45 */     if (this.optimized) {
/*  46 */       return this;
/*     */     }
/*  48 */     Expression expression = super.optimize(paramSessionLocal);
/*  49 */     if (expression != this) {
/*  50 */       return expression;
/*     */     }
/*  52 */     this.optimized = true;
/*  53 */     if (!this.whenOperand && this.left instanceof ExpressionList) {
/*  54 */       ExpressionList expressionList = (ExpressionList)this.left;
/*  55 */       if (!expressionList.isArray()) {
/*  56 */         byte b; int i; for (b = 0, i = expressionList.getSubexpressionCount(); b < i; b++) {
/*  57 */           if (expressionList.getSubexpression(b).isNullConstant()) {
/*  58 */             if (this.not) {
/*  59 */               return (Expression)ValueExpression.FALSE;
/*     */             }
/*  61 */             ArrayList<Expression> arrayList = new ArrayList(i - 1); int j;
/*  62 */             for (j = 0; j < b; j++) {
/*  63 */               arrayList.add(expressionList.getSubexpression(j));
/*     */             }
/*  65 */             for (j = b + 1; j < i; j++) {
/*  66 */               Expression expression1 = expressionList.getSubexpression(j);
/*  67 */               if (!expression1.isNullConstant()) {
/*  68 */                 arrayList.add(expression1);
/*     */               }
/*     */             } 
/*  71 */             this
/*  72 */               .left = (arrayList.size() == 1) ? arrayList.get(0) : (Expression)new ExpressionList(arrayList.<Expression>toArray(new Expression[0]), false);
/*     */             break;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*  78 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal) {
/*  83 */     return (Value)ValueBoolean.get(getValue(this.left.getValue(paramSessionLocal)));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getWhenValue(SessionLocal paramSessionLocal, Value paramValue) {
/*  88 */     if (!this.whenOperand) {
/*  89 */       return super.getWhenValue(paramSessionLocal, paramValue);
/*     */     }
/*  91 */     return getValue(paramValue);
/*     */   }
/*     */   
/*     */   private boolean getValue(Value paramValue) {
/*  95 */     if (paramValue.getType().getValueType() == 41) {
/*  96 */       for (Value value : ((ValueRow)paramValue).getList()) {
/*  97 */         if ((((value != ValueNull.INSTANCE) ? 1 : 0) ^ this.not) != 0) {
/*  98 */           return false;
/*     */         }
/*     */       } 
/* 101 */       return true;
/*     */     } 
/* 103 */     return ((paramValue == ValueNull.INSTANCE)) ^ this.not;
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression getNotIfPossible(SessionLocal paramSessionLocal) {
/* 108 */     if (this.whenOperand) {
/* 109 */       return null;
/*     */     }
/* 111 */     Expression expression = optimize(paramSessionLocal);
/* 112 */     if (expression != this) {
/* 113 */       return expression.getNotIfPossible(paramSessionLocal);
/*     */     }
/* 115 */     switch (this.left.getType().getValueType()) {
/*     */       case -1:
/*     */       case 41:
/* 118 */         return null;
/*     */     } 
/* 120 */     return new NullPredicate(this.left, !this.not, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public void createIndexConditions(SessionLocal paramSessionLocal, TableFilter paramTableFilter) {
/* 125 */     if (this.not || this.whenOperand || !paramTableFilter.getTable().isQueryComparable()) {
/*     */       return;
/*     */     }
/* 128 */     if (this.left instanceof ExpressionColumn) {
/* 129 */       createNullIndexCondition(paramTableFilter, (ExpressionColumn)this.left);
/* 130 */     } else if (this.left instanceof ExpressionList) {
/* 131 */       ExpressionList expressionList = (ExpressionList)this.left;
/* 132 */       if (!expressionList.isArray()) {
/* 133 */         byte b; int i; for (b = 0, i = expressionList.getSubexpressionCount(); b < i; b++) {
/* 134 */           Expression expression = expressionList.getSubexpression(b);
/* 135 */           if (expression instanceof ExpressionColumn) {
/* 136 */             createNullIndexCondition(paramTableFilter, (ExpressionColumn)expression);
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void createNullIndexCondition(TableFilter paramTableFilter, ExpressionColumn paramExpressionColumn) {
/* 148 */     if (paramTableFilter == paramExpressionColumn.getTableFilter() && paramExpressionColumn.getType().getValueType() != 41)
/* 149 */       paramTableFilter.addIndexCondition(IndexCondition.get(6, paramExpressionColumn, (Expression)ValueExpression.NULL)); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\condition\NullPredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */