package org.h2.result;

import org.h2.value.Value;
import org.h2.value.ValueBigint;

public final class Sparse extends DefaultRow {
   private final int columnCount;
   private final int[] map;

   Sparse(int var1, int var2, int[] var3) {
      super(new Value[var2]);
      this.columnCount = var1;
      this.map = var3;
   }

   public int getColumnCount() {
      return this.columnCount;
   }

   public Value getValue(int var1) {
      if (var1 == -1) {
         return ValueBigint.get(this.getKey());
      } else {
         int var2 = this.map[var1];
         return var2 > 0 ? super.getValue(var2 - 1) : null;
      }
   }

   public void setValue(int var1, Value var2) {
      if (var1 == -1) {
         this.setKey(var2.getLong());
      }

      int var3 = this.map[var1];
      if (var3 > 0) {
         super.setValue(var3 - 1, var2);
      }

   }

   public void copyFrom(SearchRow var1) {
      this.setKey(var1.getKey());

      for(int var2 = 0; var2 < this.map.length; ++var2) {
         int var3 = this.map[var2];
         if (var3 > 0) {
            super.setValue(var3 - 1, var1.getValue(var2));
         }
      }

   }
}
