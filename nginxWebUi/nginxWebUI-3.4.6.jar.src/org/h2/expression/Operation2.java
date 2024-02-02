/*    */ package org.h2.expression;
/*    */ 
/*    */ import org.h2.engine.SessionLocal;
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
/*    */ 
/*    */ public abstract class Operation2
/*    */   extends Expression
/*    */ {
/*    */   protected Expression left;
/*    */   protected Expression right;
/*    */   protected TypeInfo type;
/*    */   
/*    */   protected Operation2(Expression paramExpression1, Expression paramExpression2) {
/* 34 */     this.left = paramExpression1;
/* 35 */     this.right = paramExpression2;
/*    */   }
/*    */ 
/*    */   
/*    */   public TypeInfo getType() {
/* 40 */     return this.type;
/*    */   }
/*    */ 
/*    */   
/*    */   public void mapColumns(ColumnResolver paramColumnResolver, int paramInt1, int paramInt2) {
/* 45 */     this.left.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/* 46 */     this.right.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/*    */   }
/*    */ 
/*    */   
/*    */   public void setEvaluatable(TableFilter paramTableFilter, boolean paramBoolean) {
/* 51 */     this.left.setEvaluatable(paramTableFilter, paramBoolean);
/* 52 */     this.right.setEvaluatable(paramTableFilter, paramBoolean);
/*    */   }
/*    */ 
/*    */   
/*    */   public void updateAggregate(SessionLocal paramSessionLocal, int paramInt) {
/* 57 */     this.left.updateAggregate(paramSessionLocal, paramInt);
/* 58 */     this.right.updateAggregate(paramSessionLocal, paramInt);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 63 */     return (this.left.isEverything(paramExpressionVisitor) && this.right.isEverything(paramExpressionVisitor));
/*    */   }
/*    */ 
/*    */   
/*    */   public int getCost() {
/* 68 */     return this.left.getCost() + this.right.getCost() + 1;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getSubexpressionCount() {
/* 73 */     return 2;
/*    */   }
/*    */ 
/*    */   
/*    */   public Expression getSubexpression(int paramInt) {
/* 78 */     switch (paramInt) {
/*    */       case 0:
/* 80 */         return this.left;
/*    */       case 1:
/* 82 */         return this.right;
/*    */     } 
/* 84 */     throw new IndexOutOfBoundsException();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\Operation2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */