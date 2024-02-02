package org.h2.expression.aggregate;

import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.value.Value;
import org.h2.value.ValueDouble;
import org.h2.value.ValueNull;

final class AggregateDataStdVar extends AggregateData {
   private final AggregateType aggregateType;
   private long count;
   private double m2;
   private double mean;

   AggregateDataStdVar(AggregateType var1) {
      this.aggregateType = var1;
   }

   void add(SessionLocal var1, Value var2) {
      if (var2 != ValueNull.INSTANCE) {
         double var3 = var2.getDouble();
         if (++this.count == 1L) {
            this.mean = var3;
            this.m2 = 0.0;
         } else {
            double var5 = var3 - this.mean;
            this.mean += var5 / (double)this.count;
            this.m2 += var5 * (var3 - this.mean);
         }

      }
   }

   Value getValue(SessionLocal var1) {
      double var2;
      switch (this.aggregateType) {
         case STDDEV_SAMP:
         case VAR_SAMP:
            if (this.count < 2L) {
               return ValueNull.INSTANCE;
            }

            var2 = this.m2 / (double)(this.count - 1L);
            if (this.aggregateType == AggregateType.STDDEV_SAMP) {
               var2 = Math.sqrt(var2);
            }
            break;
         case STDDEV_POP:
         case VAR_POP:
            if (this.count < 1L) {
               return ValueNull.INSTANCE;
            }

            var2 = this.m2 / (double)this.count;
            if (this.aggregateType == AggregateType.STDDEV_POP) {
               var2 = Math.sqrt(var2);
            }
            break;
         case REGR_SXX:
         case REGR_SYY:
            if (this.count < 1L) {
               return ValueNull.INSTANCE;
            }

            var2 = this.m2;
            break;
         default:
            throw DbException.getInternalError("type=" + this.aggregateType);
      }

      return ValueDouble.get(var2);
   }
}
