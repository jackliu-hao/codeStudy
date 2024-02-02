/*    */ package org.h2.expression.aggregate;
/*    */ 
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.value.Value;
/*    */ import org.h2.value.ValueDouble;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ final class AggregateDataCovar
/*    */   extends AggregateDataBinarySet
/*    */ {
/*    */   private final AggregateType aggregateType;
/*    */   private long count;
/*    */   private double sumY;
/*    */   private double sumX;
/*    */   private double sumYX;
/*    */   
/*    */   AggregateDataCovar(AggregateType paramAggregateType) {
/* 30 */     this.aggregateType = paramAggregateType;
/*    */   }
/*    */ 
/*    */   
/*    */   void add(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2) {
/* 35 */     double d1 = paramValue1.getDouble(), d2 = paramValue2.getDouble();
/* 36 */     this.sumY += d1;
/* 37 */     this.sumX += d2;
/* 38 */     this.sumYX += d1 * d2;
/* 39 */     this.count++;
/*    */   }
/*    */ 
/*    */   
/*    */   Value getValue(SessionLocal paramSessionLocal) {
/*    */     double d;
/* 45 */     switch (this.aggregateType) {
/*    */       case COVAR_POP:
/* 47 */         if (this.count < 1L) {
/* 48 */           return (Value)ValueNull.INSTANCE;
/*    */         }
/* 50 */         d = (this.sumYX - this.sumX * this.sumY / this.count) / this.count;
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
/* 67 */         return (Value)ValueDouble.get(d);case COVAR_SAMP: if (this.count < 2L) return (Value)ValueNull.INSTANCE;  d = (this.sumYX - this.sumX * this.sumY / this.count) / (this.count - 1L); return (Value)ValueDouble.get(d);case REGR_SXY: if (this.count < 1L) return (Value)ValueNull.INSTANCE;  d = this.sumYX - this.sumX * this.sumY / this.count; return (Value)ValueDouble.get(d);
/*    */     } 
/*    */     throw DbException.getInternalError("type=" + this.aggregateType);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\aggregate\AggregateDataCovar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */