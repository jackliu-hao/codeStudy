/*    */ package org.h2.expression.condition;
/*    */ 
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.expression.Expression;
/*    */ import org.h2.expression.ExpressionVisitor;
/*    */ import org.h2.expression.ValueExpression;
/*    */ import org.h2.table.ColumnResolver;
/*    */ import org.h2.table.TableFilter;
/*    */ import org.h2.value.TypeInfo;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class SimplePredicate
/*    */   extends Condition
/*    */ {
/*    */   Expression left;
/*    */   final boolean not;
/*    */   final boolean whenOperand;
/*    */   
/*    */   SimplePredicate(Expression paramExpression, boolean paramBoolean1, boolean paramBoolean2) {
/* 36 */     this.left = paramExpression;
/* 37 */     this.not = paramBoolean1;
/* 38 */     this.whenOperand = paramBoolean2;
/*    */   }
/*    */ 
/*    */   
/*    */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 43 */     this.left = this.left.optimize(paramSessionLocal);
/* 44 */     if (!this.whenOperand && this.left.isConstant()) {
/* 45 */       return (Expression)ValueExpression.getBoolean(getValue(paramSessionLocal));
/*    */     }
/* 47 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setEvaluatable(TableFilter paramTableFilter, boolean paramBoolean) {
/* 52 */     this.left.setEvaluatable(paramTableFilter, paramBoolean);
/*    */   }
/*    */ 
/*    */   
/*    */   public final boolean needParentheses() {
/* 57 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public void updateAggregate(SessionLocal paramSessionLocal, int paramInt) {
/* 62 */     this.left.updateAggregate(paramSessionLocal, paramInt);
/*    */   }
/*    */ 
/*    */   
/*    */   public void mapColumns(ColumnResolver paramColumnResolver, int paramInt1, int paramInt2) {
/* 67 */     this.left.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 72 */     return this.left.isEverything(paramExpressionVisitor);
/*    */   }
/*    */ 
/*    */   
/*    */   public int getCost() {
/* 77 */     return this.left.getCost() + 1;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getSubexpressionCount() {
/* 82 */     return 1;
/*    */   }
/*    */ 
/*    */   
/*    */   public Expression getSubexpression(int paramInt) {
/* 87 */     if (paramInt == 0) {
/* 88 */       return this.left;
/*    */     }
/* 90 */     throw new IndexOutOfBoundsException();
/*    */   }
/*    */ 
/*    */   
/*    */   public final boolean isWhenConditionOperand() {
/* 95 */     return this.whenOperand;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\condition\SimplePredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */