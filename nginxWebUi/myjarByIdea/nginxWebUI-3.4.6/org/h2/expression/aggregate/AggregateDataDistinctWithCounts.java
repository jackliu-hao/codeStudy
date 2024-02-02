package org.h2.expression.aggregate;

import java.util.TreeMap;
import org.h2.engine.SessionLocal;
import org.h2.value.Value;
import org.h2.value.ValueNull;

final class AggregateDataDistinctWithCounts extends AggregateData {
   private final boolean ignoreNulls;
   private final int maxDistinctCount;
   private TreeMap<Value, LongDataCounter> values;

   AggregateDataDistinctWithCounts(boolean var1, int var2) {
      this.ignoreNulls = var1;
      this.maxDistinctCount = var2;
   }

   void add(SessionLocal var1, Value var2) {
      if (!this.ignoreNulls || var2 != ValueNull.INSTANCE) {
         if (this.values == null) {
            this.values = new TreeMap(var1.getDatabase().getCompareMode());
         }

         LongDataCounter var3 = (LongDataCounter)this.values.get(var2);
         if (var3 == null) {
            if (this.values.size() >= this.maxDistinctCount) {
               return;
            }

            var3 = new LongDataCounter();
            this.values.put(var2, var3);
         }

         ++var3.count;
      }
   }

   Value getValue(SessionLocal var1) {
      return null;
   }

   TreeMap<Value, LongDataCounter> getValues() {
      return this.values;
   }
}
