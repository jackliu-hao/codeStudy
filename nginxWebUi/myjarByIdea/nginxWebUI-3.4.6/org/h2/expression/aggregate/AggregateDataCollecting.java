package org.h2.expression.aggregate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.value.Value;
import org.h2.value.ValueNull;
import org.h2.value.ValueRow;

final class AggregateDataCollecting extends AggregateData implements Iterable<Value> {
   private final boolean distinct;
   private final boolean orderedWithOrder;
   private final NullCollectionMode nullCollectionMode;
   Collection<Value> values;
   private Value shared;

   AggregateDataCollecting(boolean var1, boolean var2, NullCollectionMode var3) {
      this.distinct = var1;
      this.orderedWithOrder = var2;
      this.nullCollectionMode = var3;
   }

   void add(SessionLocal var1, Value var2) {
      if (this.nullCollectionMode != AggregateDataCollecting.NullCollectionMode.IGNORED || !this.isNull(var2)) {
         Object var3 = this.values;
         if (var3 == null) {
            if (this.distinct) {
               Object var4 = var1.getDatabase().getCompareMode();
               if (this.orderedWithOrder) {
                  var4 = Comparator.comparing((var0) -> {
                     return ((ValueRow)var0).getList()[0];
                  }, (Comparator)var4);
               }

               var3 = new TreeSet((Comparator)var4);
            } else {
               var3 = new ArrayList();
            }

            this.values = (Collection)var3;
         }

         if (this.nullCollectionMode != AggregateDataCollecting.NullCollectionMode.EXCLUDED || !this.isNull(var2)) {
            ((Collection)var3).add(var2);
         }
      }
   }

   private boolean isNull(Value var1) {
      return (this.orderedWithOrder ? ((ValueRow)var1).getList()[0] : var1) == ValueNull.INSTANCE;
   }

   Value getValue(SessionLocal var1) {
      return null;
   }

   int getCount() {
      return this.values != null ? this.values.size() : 0;
   }

   Value[] getArray() {
      Collection var1 = this.values;
      return var1 == null ? null : (Value[])var1.toArray(Value.EMPTY_VALUES);
   }

   public Iterator<Value> iterator() {
      return this.values != null ? this.values.iterator() : Collections.emptyIterator();
   }

   void setSharedArgument(Value var1) {
      if (this.shared == null) {
         this.shared = var1;
      } else if (!this.shared.equals(var1)) {
         throw DbException.get(90008, "Inverse distribution function argument", this.shared.getTraceSQL() + "<>" + var1.getTraceSQL());
      }

   }

   Value getSharedArgument() {
      return this.shared;
   }

   static enum NullCollectionMode {
      IGNORED,
      EXCLUDED,
      USED_OR_IMPOSSIBLE;
   }
}
