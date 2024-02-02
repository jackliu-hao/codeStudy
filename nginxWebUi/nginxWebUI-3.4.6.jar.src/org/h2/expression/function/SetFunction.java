/*    */ package org.h2.expression.function;
/*    */ 
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.expression.Expression;
/*    */ import org.h2.expression.ExpressionVisitor;
/*    */ import org.h2.expression.Variable;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.value.Value;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class SetFunction
/*    */   extends Function2
/*    */ {
/*    */   public SetFunction(Expression paramExpression1, Expression paramExpression2) {
/* 22 */     super(paramExpression1, paramExpression2);
/*    */   }
/*    */ 
/*    */   
/*    */   public Value getValue(SessionLocal paramSessionLocal) {
/* 27 */     Variable variable = (Variable)this.left;
/* 28 */     Value value = this.right.getValue(paramSessionLocal);
/* 29 */     paramSessionLocal.setVariable(variable.getName(), value);
/* 30 */     return value;
/*    */   }
/*    */ 
/*    */   
/*    */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 35 */     this.left = this.left.optimize(paramSessionLocal);
/* 36 */     this.right = this.right.optimize(paramSessionLocal);
/* 37 */     this.type = this.right.getType();
/* 38 */     if (!(this.left instanceof Variable)) {
/* 39 */       throw DbException.get(90137, this.left.getTraceSQL());
/*    */     }
/* 41 */     return (Expression)this;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getName() {
/* 46 */     return "SET";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 51 */     if (!super.isEverything(paramExpressionVisitor)) {
/* 52 */       return false;
/*    */     }
/* 54 */     switch (paramExpressionVisitor.getType()) {
/*    */       case 2:
/*    */       case 5:
/*    */       case 8:
/* 58 */         return false;
/*    */     } 
/* 60 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\SetFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */