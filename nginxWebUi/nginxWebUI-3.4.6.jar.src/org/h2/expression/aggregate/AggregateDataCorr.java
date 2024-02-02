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
/*    */ final class AggregateDataCorr
/*    */   extends AggregateDataBinarySet
/*    */ {
/*    */   private final AggregateType aggregateType;
/*    */   private long count;
/*    */   private double sumY;
/*    */   private double sumX;
/*    */   private double sumYX;
/*    */   private double m2y;
/*    */   private double meanY;
/*    */   private double m2x;
/*    */   private double meanX;
/*    */   
/*    */   AggregateDataCorr(AggregateType paramAggregateType) {
/* 31 */     this.aggregateType = paramAggregateType;
/*    */   }
/*    */ 
/*    */   
/*    */   void add(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2) {
/* 36 */     double d1 = paramValue1.getDouble(), d2 = paramValue2.getDouble();
/* 37 */     this.sumY += d1;
/* 38 */     this.sumX += d2;
/* 39 */     this.sumYX += d1 * d2;
/*    */     
/* 41 */     this.meanY = d1;
/* 42 */     this.meanX = d2;
/* 43 */     this.m2x = this.m2y = 0.0D;
/*    */     
/* 45 */     double d3 = d1 - this.meanY;
/* 46 */     this.meanY += d3 / this.count;
/* 47 */     this.m2y += d3 * (d1 - this.meanY);
/* 48 */     d3 = d2 - this.meanX;
/* 49 */     this.meanX += d3 / this.count;
/* 50 */     this.m2x += d3 * (d2 - this.meanX);
/*    */   }
/*    */ 
/*    */   
/*    */   Value getValue(SessionLocal paramSessionLocal) {
/*    */     double d;
/* 56 */     if (this.count < 1L) {
/* 57 */       return (Value)ValueNull.INSTANCE;
/*    */     }
/*    */     
/* 60 */     switch (this.aggregateType) {
/*    */       case CORR:
/* 62 */         if (this.m2y == 0.0D || this.m2x == 0.0D) {
/* 63 */           return (Value)ValueNull.INSTANCE;
/*    */         }
/* 65 */         d = (this.sumYX - this.sumX * this.sumY / this.count) / Math.sqrt(this.m2y * this.m2x);
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */         
/* 93 */         return (Value)ValueDouble.get(d);case REGR_SLOPE: if (this.m2x == 0.0D) return (Value)ValueNull.INSTANCE;  d = (this.sumYX - this.sumX * this.sumY / this.count) / this.m2x; return (Value)ValueDouble.get(d);case REGR_INTERCEPT: if (this.m2x == 0.0D) return (Value)ValueNull.INSTANCE;  d = this.meanY - (this.sumYX - this.sumX * this.sumY / this.count) / this.m2x * this.meanX; return (Value)ValueDouble.get(d);case REGR_R2: if (this.m2x == 0.0D) return (Value)ValueNull.INSTANCE;  if (this.m2y == 0.0D) return (Value)ValueDouble.ONE;  d = this.sumYX - this.sumX * this.sumY / this.count; d = d * d / this.m2y * this.m2x; return (Value)ValueDouble.get(d);
/*    */     } 
/*    */     throw DbException.getInternalError("type=" + this.aggregateType);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\aggregate\AggregateDataCorr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */