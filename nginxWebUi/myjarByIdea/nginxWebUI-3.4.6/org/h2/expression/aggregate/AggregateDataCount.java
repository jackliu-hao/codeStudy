package org.h2.expression.aggregate;

import org.h2.engine.SessionLocal;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueNull;

final class AggregateDataCount extends AggregateData {
   private final boolean all;
   private long count;

   AggregateDataCount(boolean var1) {
      this.all = var1;
   }

   void add(SessionLocal var1, Value var2) {
      if (this.all || var2 != ValueNull.INSTANCE) {
         ++this.count;
      }

   }

   Value getValue(SessionLocal var1) {
      return ValueBigint.get(this.count);
   }
}
