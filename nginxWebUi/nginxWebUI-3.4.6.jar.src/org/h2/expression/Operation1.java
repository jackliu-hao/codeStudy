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
/*    */ public abstract class Operation1
/*    */   extends Expression
/*    */ {
/*    */   protected Expression arg;
/*    */   protected TypeInfo type;
/*    */   
/*    */   protected Operation1(Expression paramExpression) {
/* 29 */     this.arg = paramExpression;
/*    */   }
/*    */ 
/*    */   
/*    */   public TypeInfo getType() {
/* 34 */     return this.type;
/*    */   }
/*    */ 
/*    */   
/*    */   public void mapColumns(ColumnResolver paramColumnResolver, int paramInt1, int paramInt2) {
/* 39 */     this.arg.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/*    */   }
/*    */ 
/*    */   
/*    */   public void setEvaluatable(TableFilter paramTableFilter, boolean paramBoolean) {
/* 44 */     this.arg.setEvaluatable(paramTableFilter, paramBoolean);
/*    */   }
/*    */ 
/*    */   
/*    */   public void updateAggregate(SessionLocal paramSessionLocal, int paramInt) {
/* 49 */     this.arg.updateAggregate(paramSessionLocal, paramInt);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 54 */     return this.arg.isEverything(paramExpressionVisitor);
/*    */   }
/*    */ 
/*    */   
/*    */   public int getCost() {
/* 59 */     return this.arg.getCost() + 1;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getSubexpressionCount() {
/* 64 */     return 1;
/*    */   }
/*    */ 
/*    */   
/*    */   public Expression getSubexpression(int paramInt) {
/* 69 */     if (paramInt == 0) {
/* 70 */       return this.arg;
/*    */     }
/* 72 */     throw new IndexOutOfBoundsException();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\Operation1.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */