/*    */ package org.h2.expression.function;
/*    */ 
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.expression.Expression;
/*    */ import org.h2.expression.OperationN;
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
/*    */ public abstract class FunctionN
/*    */   extends OperationN
/*    */   implements NamedExpression
/*    */ {
/*    */   protected FunctionN(Expression[] paramArrayOfExpression) {
/* 21 */     super(paramArrayOfExpression);
/*    */   }
/*    */ 
/*    */   
/*    */   public Value getValue(SessionLocal paramSessionLocal) {
/*    */     Value value1, value2, value3;
/* 27 */     int i = this.args.length;
/* 28 */     if (i >= 1) {
/* 29 */       value1 = this.args[0].getValue(paramSessionLocal);
/* 30 */       if (value1 == ValueNull.INSTANCE) {
/* 31 */         return (Value)ValueNull.INSTANCE;
/*    */       }
/* 33 */       if (i >= 2) {
/* 34 */         value2 = this.args[1].getValue(paramSessionLocal);
/* 35 */         if (value2 == ValueNull.INSTANCE) {
/* 36 */           return (Value)ValueNull.INSTANCE;
/*    */         }
/* 38 */         if (i >= 3) {
/* 39 */           value3 = this.args[2].getValue(paramSessionLocal);
/* 40 */           if (value3 == ValueNull.INSTANCE) {
/* 41 */             return (Value)ValueNull.INSTANCE;
/*    */           }
/*    */         } else {
/* 44 */           value3 = null;
/*    */         } 
/*    */       } else {
/* 47 */         value3 = (Value)(value2 = null);
/*    */       } 
/*    */     } else {
/* 50 */       value3 = (Value)(value2 = (Value)(value1 = null));
/*    */     } 
/* 52 */     return getValue(paramSessionLocal, value1, value2, value3);
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
/*    */ 
/*    */   
/*    */   protected Value getValue(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2, Value paramValue3) {
/* 69 */     throw DbException.getInternalError();
/*    */   }
/*    */ 
/*    */   
/*    */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 74 */     return writeExpressions(paramStringBuilder.append(getName()).append('('), this.args, paramInt).append(')');
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\FunctionN.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */