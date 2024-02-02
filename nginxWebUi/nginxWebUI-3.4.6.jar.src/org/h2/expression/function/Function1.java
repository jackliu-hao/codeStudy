/*    */ package org.h2.expression.function;
/*    */ 
/*    */ import org.h2.expression.Expression;
/*    */ import org.h2.expression.Operation1;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class Function1
/*    */   extends Operation1
/*    */   implements NamedExpression
/*    */ {
/*    */   protected Function1(Expression paramExpression) {
/* 17 */     super(paramExpression);
/*    */   }
/*    */ 
/*    */   
/*    */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 22 */     return this.arg.getUnenclosedSQL(paramStringBuilder.append(getName()).append('('), paramInt).append(')');
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\Function1.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */