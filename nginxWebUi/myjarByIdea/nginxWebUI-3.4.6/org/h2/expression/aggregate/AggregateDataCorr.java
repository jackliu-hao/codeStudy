package org.h2.expression.aggregate;

import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.value.Value;
import org.h2.value.ValueDouble;
import org.h2.value.ValueNull;

final class AggregateDataCorr extends AggregateDataBinarySet {
   private final AggregateType aggregateType;
   private long count;
   private double sumY;
   private double sumX;
   private double sumYX;
   private double m2y;
   private double meanY;
   private double m2x;
   private double meanX;

   AggregateDataCorr(AggregateType var1) {
      this.aggregateType = var1;
   }

   void add(SessionLocal var1, Value var2, Value var3) {
      double var4 = var2.getDouble();
      double var6 = var3.getDouble();
      this.sumY += var4;
      this.sumX += var6;
      this.sumYX += var4 * var6;
      if (++this.count == 1L) {
         this.meanY = var4;
         this.meanX = var6;
         this.m2x = this.m2y = 0.0;
      } else {
         double var8 = var4 - this.meanY;
         this.meanY += var8 / (double)this.count;
         this.m2y += var8 * (var4 - this.meanY);
         var8 = var6 - this.meanX;
         this.meanX += var8 / (double)this.count;
         this.m2x += var8 * (var6 - this.meanX);
      }

   }

   Value getValue(SessionLocal var1) {
      if (this.count < 1L) {
         return ValueNull.INSTANCE;
      } else {
         double var2;
         switch (this.aggregateType) {
            case CORR:
               if (this.m2y != 0.0 && this.m2x != 0.0) {
                  var2 = (this.sumYX - this.sumX * this.sumY / (double)this.count) / Math.sqrt(this.m2y * this.m2x);
                  break;
               }

               return ValueNull.INSTANCE;
            case REGR_SLOPE:
               if (this.m2x == 0.0) {
                  return ValueNull.INSTANCE;
               }

               var2 = (this.sumYX - this.sumX * this.sumY / (double)this.count) / this.m2x;
               break;
            case REGR_INTERCEPT:
               if (this.m2x == 0.0) {
                  return ValueNull.INSTANCE;
               }

               var2 = this.meanY - (this.sumYX - this.sumX * this.sumY / (double)this.count) / this.m2x * this.meanX;
               break;
            case REGR_R2:
               if (this.m2x == 0.0) {
                  return ValueNull.INSTANCE;
               }

               if (this.m2y == 0.0) {
                  return ValueDouble.ONE;
               }

               var2 = this.sumYX - this.sumX * this.sumY / (double)this.count;
               var2 = var2 * var2 / (this.m2y * this.m2x);
               break;
            default:
               throw DbException.getInternalError("type=" + this.aggregateType);
         }

         return ValueDouble.get(var2);
      }
   }
}
