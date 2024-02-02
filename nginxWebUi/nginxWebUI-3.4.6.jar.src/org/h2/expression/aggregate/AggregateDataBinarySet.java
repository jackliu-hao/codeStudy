/*    */ package org.h2.expression.aggregate;
/*    */ 
/*    */ import org.h2.engine.SessionLocal;
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
/*    */ abstract class AggregateDataBinarySet
/*    */   extends AggregateData
/*    */ {
/*    */   abstract void add(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2);
/*    */   
/*    */   final void add(SessionLocal paramSessionLocal, Value paramValue) {
/* 21 */     throw DbException.getInternalError();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\aggregate\AggregateDataBinarySet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */