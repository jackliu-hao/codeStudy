package org.h2.expression.aggregate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.h2.api.IntervalQualifier;
import org.h2.engine.SessionLocal;
import org.h2.util.IntervalUtils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueDecfloat;
import org.h2.value.ValueDouble;
import org.h2.value.ValueInterval;
import org.h2.value.ValueNull;
import org.h2.value.ValueNumeric;

final class AggregateDataAvg extends AggregateData {
   private final TypeInfo dataType;
   private long count;
   private double doubleValue;
   private BigDecimal decimalValue;
   private BigInteger integerValue;

   AggregateDataAvg(TypeInfo var1) {
      this.dataType = var1;
   }

   void add(SessionLocal var1, Value var2) {
      if (var2 != ValueNull.INSTANCE) {
         ++this.count;
         switch (this.dataType.getValueType()) {
            case 13:
            case 16:
               BigDecimal var3 = var2.getBigDecimal();
               this.decimalValue = this.decimalValue == null ? var3 : this.decimalValue.add(var3);
               break;
            case 14:
            default:
               BigInteger var4 = IntervalUtils.intervalToAbsolute((ValueInterval)var2);
               this.integerValue = this.integerValue == null ? var4 : this.integerValue.add(var4);
               break;
            case 15:
               this.doubleValue += var2.getDouble();
         }

      }
   }

   Value getValue(SessionLocal var1) {
      if (this.count == 0L) {
         return ValueNull.INSTANCE;
      } else {
         int var3 = this.dataType.getValueType();
         Object var2;
         switch (var3) {
            case 13:
               var2 = ValueNumeric.get(this.decimalValue.divide(BigDecimal.valueOf(this.count), this.dataType.getScale(), RoundingMode.HALF_DOWN));
               break;
            case 14:
            default:
               var2 = IntervalUtils.intervalFromAbsolute(IntervalQualifier.valueOf(var3 - 22), this.integerValue.divide(BigInteger.valueOf(this.count)));
               break;
            case 15:
               var2 = ValueDouble.get(this.doubleValue / (double)this.count);
               break;
            case 16:
               var2 = ValueDecfloat.divide(this.decimalValue, BigDecimal.valueOf(this.count), this.dataType);
         }

         return ((Value)var2).castTo(this.dataType, var1);
      }
   }
}
