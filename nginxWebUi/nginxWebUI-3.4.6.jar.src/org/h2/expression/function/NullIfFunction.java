/*    */ package org.h2.expression.function;
/*    */ 
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.expression.Expression;
/*    */ import org.h2.expression.TypedValueExpression;
/*    */ import org.h2.value.TypeInfo;
/*    */ import org.h2.value.Value;
/*    */ import org.h2.value.ValueNull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class NullIfFunction
/*    */   extends Function2
/*    */ {
/*    */   public NullIfFunction(Expression paramExpression1, Expression paramExpression2) {
/* 21 */     super(paramExpression1, paramExpression2);
/*    */   }
/*    */   
/*    */   public Value getValue(SessionLocal paramSessionLocal) {
/*    */     ValueNull valueNull;
/* 26 */     Value value = this.left.getValue(paramSessionLocal);
/* 27 */     if (paramSessionLocal.compareWithNull(value, this.right.getValue(paramSessionLocal), true) == 0) {
/* 28 */       valueNull = ValueNull.INSTANCE;
/*    */     }
/* 30 */     return (Value)valueNull;
/*    */   }
/*    */ 
/*    */   
/*    */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 35 */     this.left = this.left.optimize(paramSessionLocal);
/* 36 */     this.right = this.right.optimize(paramSessionLocal);
/* 37 */     this.type = this.left.getType();
/* 38 */     TypeInfo.checkComparable(this.type, this.right.getType());
/* 39 */     if (this.left.isConstant() && this.right.isConstant()) {
/* 40 */       return (Expression)TypedValueExpression.getTypedIfNull(getValue(paramSessionLocal), this.type);
/*    */     }
/* 42 */     return (Expression)this;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getName() {
/* 47 */     return "NULLIF";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\NullIfFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */