package org.h2.expression.aggregate;

import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.value.Value;
import org.h2.value.ValueDouble;
import org.h2.value.ValueNull;

final class AggregateDataCovar extends AggregateDataBinarySet {
   private final AggregateType aggregateType;
   private long count;
   private double sumY;
   private double sumX;
   private double sumYX;

   AggregateDataCovar(AggregateType var1) {
      this.aggregateType = var1;
   }

   void add(SessionLocal var1, Value var2, Value var3) {
      double var4 = var2.getDouble();
      double var6 = var3.getDouble();
      this.sumY += var4;
      this.sumX += var6;
      this.sumYX += var4 * var6;
      ++this.count;
   }

   Value getValue(SessionLocal var1) {
      double var2;
      switch (this.aggregateType) {
         case COVAR_POP:
            if (this.count < 1L) {
               return ValueNull.INSTANCE;
            }

            var2 = (this.sumYX - this.sumX * this.sumY / (double)this.count) / (double)this.count;
            break;
         case COVAR_SAMP:
            if (this.count < 2L) {
               return ValueNull.INSTANCE;
            }

            var2 = (this.sumYX - this.sumX * this.sumY / (double)this.count) / (double)(this.count - 1L);
            break;
         case REGR_SXY:
            if (this.count < 1L) {
               return ValueNull.INSTANCE;
            }

            var2 = this.sumYX - this.sumX * this.sumY / (double)this.count;
            break;
         default:
            throw DbException.getInternalError("type=" + this.aggregateType);
      }

      return ValueDouble.get(var2);
   }
}
