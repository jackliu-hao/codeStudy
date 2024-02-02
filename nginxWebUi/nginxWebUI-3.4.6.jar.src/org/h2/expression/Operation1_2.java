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
/*    */ public abstract class Operation1_2
/*    */   extends Expression
/*    */ {
/*    */   protected Expression left;
/*    */   protected Expression right;
/*    */   protected TypeInfo type;
/*    */   
/*    */   protected Operation1_2(Expression paramExpression1, Expression paramExpression2) {
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
/* 46 */     if (this.right != null) {
/* 47 */       this.right.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void setEvaluatable(TableFilter paramTableFilter, boolean paramBoolean) {
/* 53 */     this.left.setEvaluatable(paramTableFilter, paramBoolean);
/* 54 */     if (this.right != null) {
/* 55 */       this.right.setEvaluatable(paramTableFilter, paramBoolean);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void updateAggregate(SessionLocal paramSessionLocal, int paramInt) {
/* 61 */     this.left.updateAggregate(paramSessionLocal, paramInt);
/* 62 */     if (this.right != null) {
/* 63 */       this.right.updateAggregate(paramSessionLocal, paramInt);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 69 */     return (this.left.isEverything(paramExpressionVisitor) && (this.right == null || this.right.isEverything(paramExpressionVisitor)));
/*    */   }
/*    */ 
/*    */   
/*    */   public int getCost() {
/* 74 */     int i = this.left.getCost() + 1;
/* 75 */     if (this.right != null) {
/* 76 */       i += this.right.getCost();
/*    */     }
/* 78 */     return i;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getSubexpressionCount() {
/* 83 */     return (this.right != null) ? 2 : 1;
/*    */   }
/*    */ 
/*    */   
/*    */   public Expression getSubexpression(int paramInt) {
/* 88 */     if (paramInt == 0) {
/* 89 */       return this.left;
/*    */     }
/* 91 */     if (paramInt == 1 && this.right != null) {
/* 92 */       return this.right;
/*    */     }
/* 94 */     throw new IndexOutOfBoundsException();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\Operation1_2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */