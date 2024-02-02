/*    */ package org.h2.expression.function.table;
/*    */ 
/*    */ import java.util.Arrays;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.expression.Expression;
/*    */ import org.h2.expression.ExpressionWithVariableParameters;
/*    */ import org.h2.expression.function.NamedExpression;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.result.ResultInterface;
/*    */ import org.h2.util.HasSQL;
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
/*    */ public abstract class TableFunction
/*    */   implements HasSQL, NamedExpression, ExpressionWithVariableParameters
/*    */ {
/*    */   protected Expression[] args;
/*    */   private int argsCount;
/*    */   
/*    */   protected TableFunction(Expression[] paramArrayOfExpression) {
/* 28 */     this.args = paramArrayOfExpression;
/*    */   }
/*    */ 
/*    */   
/*    */   public void addParameter(Expression paramExpression) {
/* 33 */     int i = this.args.length;
/* 34 */     if (this.argsCount >= i) {
/* 35 */       this.args = Arrays.<Expression>copyOf(this.args, i * 2);
/*    */     }
/* 37 */     this.args[this.argsCount++] = paramExpression;
/*    */   }
/*    */ 
/*    */   
/*    */   public void doneWithParameters() throws DbException {
/* 42 */     if (this.args.length != this.argsCount) {
/* 43 */       this.args = Arrays.<Expression>copyOf(this.args, this.argsCount);
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public abstract ResultInterface getValue(SessionLocal paramSessionLocal);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public abstract ResultInterface getValueTemplate(SessionLocal paramSessionLocal);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void optimize(SessionLocal paramSessionLocal) {
/*    */     byte b;
/*    */     int i;
/* 72 */     for (b = 0, i = this.args.length; b < i; b++) {
/* 73 */       this.args[b] = this.args[b].optimize(paramSessionLocal);
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public abstract boolean isDeterministic();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 87 */     return Expression.writeExpressions(paramStringBuilder.append(getName()).append('('), this.args, paramInt).append(')');
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\table\TableFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */