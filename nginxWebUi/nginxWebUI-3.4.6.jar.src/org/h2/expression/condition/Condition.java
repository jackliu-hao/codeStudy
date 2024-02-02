/*    */ package org.h2.expression.condition;
/*    */ 
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.expression.Expression;
/*    */ import org.h2.expression.function.CastSpecification;
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
/*    */ abstract class Condition
/*    */   extends Expression
/*    */ {
/*    */   static Expression castToBoolean(SessionLocal paramSessionLocal, Expression paramExpression) {
/* 27 */     if (paramExpression.getType().getValueType() == 8) {
/* 28 */       return paramExpression;
/*    */     }
/* 30 */     return (Expression)new CastSpecification(paramExpression, TypeInfo.TYPE_BOOLEAN);
/*    */   }
/*    */ 
/*    */   
/*    */   public TypeInfo getType() {
/* 35 */     return TypeInfo.TYPE_BOOLEAN;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\condition\Condition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */