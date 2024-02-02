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
/*    */ 
/*    */ 
/*    */ final class AggregateDataStdVar
/*    */   extends AggregateData
/*    */ {
/*    */   private final AggregateType aggregateType;
/*    */   private long count;
/*    */   private double m2;
/*    */   private double mean;
/*    */   
/*    */   AggregateDataStdVar(AggregateType paramAggregateType) {
/* 31 */     this.aggregateType = paramAggregateType;
/*    */   }
/*    */ 
/*    */   
/*    */   void add(SessionLocal paramSessionLocal, Value paramValue) {
/* 36 */     if (paramValue == ValueNull.INSTANCE) {
/*    */       return;
/*    */     }
/*    */ 
/*    */ 
/*    */     
/* 42 */     double d = paramValue.getDouble();
/* 43 */     if (++this.count == 1L) {
/* 44 */       this.mean = d;
/* 45 */       this.m2 = 0.0D;
/*    */     } else {
/* 47 */       double d1 = d - this.mean;
/* 48 */       this.mean += d1 / this.count;
/* 49 */       this.m2 += d1 * (d - this.mean);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   Value getValue(SessionLocal paramSessionLocal) {
/*    */     double d;
/* 56 */     switch (this.aggregateType) {
/*    */       case STDDEV_SAMP:
/*    */       case VAR_SAMP:
/* 59 */         if (this.count < 2L) {
/* 60 */           return (Value)ValueNull.INSTANCE;
/*    */         }
/* 62 */         d = this.m2 / (this.count - 1L);
/* 63 */         if (this.aggregateType == AggregateType.STDDEV_SAMP) {
/* 64 */           d = Math.sqrt(d);
/*    */         }
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */         
/* 87 */         return (Value)ValueDouble.get(d);case STDDEV_POP: case VAR_POP: if (this.count < 1L) return (Value)ValueNull.INSTANCE;  d = this.m2 / this.count; if (this.aggregateType == AggregateType.STDDEV_POP) d = Math.sqrt(d);  return (Value)ValueDouble.get(d);case REGR_SXX: case REGR_SYY: if (this.count < 1L) return (Value)ValueNull.INSTANCE;  d = this.m2; return (Value)ValueDouble.get(d);
/*    */     } 
/*    */     throw DbException.getInternalError("type=" + this.aggregateType);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\aggregate\AggregateDataStdVar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */