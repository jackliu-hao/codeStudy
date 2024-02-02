/*    */ package org.h2.expression.aggregate;
/*    */ 
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.value.Value;
/*    */ import org.h2.value.ValueBigint;
/*    */ import org.h2.value.ValueNull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class AggregateDataCount
/*    */   extends AggregateData
/*    */ {
/*    */   private final boolean all;
/*    */   private long count;
/*    */   
/*    */   AggregateDataCount(boolean paramBoolean) {
/* 23 */     this.all = paramBoolean;
/*    */   }
/*    */ 
/*    */   
/*    */   void add(SessionLocal paramSessionLocal, Value paramValue) {
/* 28 */     if (this.all || paramValue != ValueNull.INSTANCE) {
/* 29 */       this.count++;
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   Value getValue(SessionLocal paramSessionLocal) {
/* 35 */     return (Value)ValueBigint.get(this.count);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\aggregate\AggregateDataCount.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */