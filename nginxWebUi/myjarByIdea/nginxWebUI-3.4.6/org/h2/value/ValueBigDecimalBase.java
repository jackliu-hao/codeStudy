package org.h2.value;

import java.math.BigDecimal;
import org.h2.message.DbException;

abstract class ValueBigDecimalBase extends Value {
   final BigDecimal value;
   TypeInfo type;

   ValueBigDecimalBase(BigDecimal var1) {
      if (var1 != null) {
         if (var1.getClass() != BigDecimal.class) {
            throw DbException.get(90125, BigDecimal.class.getName(), var1.getClass().getName());
         }

         int var2 = var1.precision();
         if (var2 > 100000) {
            throw DbException.getValueTooLongException(getTypeName(this.getValueType()), var1.toString(), (long)var2);
         }
      }

      this.value = var1;
   }
}
