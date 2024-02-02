/*    */ package org.h2.expression.function;
/*    */ 
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.expression.Expression;
/*    */ import org.h2.expression.Operation2;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.value.Value;
/*    */ import org.h2.value.ValueNull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class Function2
/*    */   extends Operation2
/*    */   implements NamedExpression
/*    */ {
/*    */   protected Function2(Expression paramExpression1, Expression paramExpression2) {
/* 21 */     super(paramExpression1, paramExpression2);
/*    */   }
/*    */ 
/*    */   
/*    */   public Value getValue(SessionLocal paramSessionLocal) {
/* 26 */     Value value1 = this.left.getValue(paramSessionLocal);
/* 27 */     if (value1 == ValueNull.INSTANCE) {
/* 28 */       return (Value)ValueNull.INSTANCE;
/*    */     }
/* 30 */     Value value2 = this.right.getValue(paramSessionLocal);
/* 31 */     if (value2 == ValueNull.INSTANCE) {
/* 32 */       return (Value)ValueNull.INSTANCE;
/*    */     }
/* 34 */     return getValue(paramSessionLocal, value1, value2);
/*    */   }
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
/*    */   protected Value getValue(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2) {
/* 49 */     throw DbException.getInternalError();
/*    */   }
/*    */ 
/*    */   
/*    */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 54 */     this.left.getUnenclosedSQL(paramStringBuilder.append(getName()).append('('), paramInt).append(", ");
/* 55 */     return this.right.getUnenclosedSQL(paramStringBuilder, paramInt).append(')');
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\Function2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */