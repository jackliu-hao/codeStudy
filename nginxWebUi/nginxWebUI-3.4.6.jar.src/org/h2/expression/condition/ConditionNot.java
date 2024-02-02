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
/*     */ import org.h2.value.ValueNull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ConditionNot
/*     */   extends Condition
/*     */ {
/*     */   private Expression condition;
/*     */   
/*     */   public ConditionNot(Expression paramExpression) {
/*  26 */     this.condition = paramExpression;
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression getNotIfPossible(SessionLocal paramSessionLocal) {
/*  31 */     return castToBoolean(paramSessionLocal, this.condition.optimize(paramSessionLocal));
/*     */   }
/*     */ 
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal) {
/*  36 */     Value value = this.condition.getValue(paramSessionLocal);
/*  37 */     if (value == ValueNull.INSTANCE) {
/*  38 */       return value;
/*     */     }
/*  40 */     return value.convertToBoolean().negate();
/*     */   }
/*     */ 
/*     */   
/*     */   public void mapColumns(ColumnResolver paramColumnResolver, int paramInt1, int paramInt2) {
/*  45 */     this.condition.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/*  50 */     Expression expression1 = this.condition.getNotIfPossible(paramSessionLocal);
/*  51 */     if (expression1 != null) {
/*  52 */       return expression1.optimize(paramSessionLocal);
/*     */     }
/*  54 */     Expression expression2 = this.condition.optimize(paramSessionLocal);
/*  55 */     if (expression2.isConstant()) {
/*  56 */       Value value = expression2.getValue(paramSessionLocal);
/*  57 */       if (value == ValueNull.INSTANCE) {
/*  58 */         return (Expression)TypedValueExpression.UNKNOWN;
/*     */       }
/*  60 */       return (Expression)ValueExpression.getBoolean(!value.getBoolean());
/*     */     } 
/*  62 */     this.condition = expression2;
/*  63 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEvaluatable(TableFilter paramTableFilter, boolean paramBoolean) {
/*  68 */     this.condition.setEvaluatable(paramTableFilter, paramBoolean);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needParentheses() {
/*  73 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/*  78 */     return this.condition.getSQL(paramStringBuilder.append("NOT "), paramInt, 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateAggregate(SessionLocal paramSessionLocal, int paramInt) {
/*  83 */     this.condition.updateAggregate(paramSessionLocal, paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/*  88 */     return this.condition.isEverything(paramExpressionVisitor);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCost() {
/*  93 */     return this.condition.getCost();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSubexpressionCount() {
/*  98 */     return 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression getSubexpression(int paramInt) {
/* 103 */     if (paramInt == 0) {
/* 104 */       return this.condition;
/*     */     }
/* 106 */     throw new IndexOutOfBoundsException();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\condition\ConditionNot.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */