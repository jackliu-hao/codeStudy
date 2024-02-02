package org.h2.result;

import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueNull;

public class SimpleRowValue extends SearchRow {
   private int index;
   private final int virtualColumnCount;
   private Value data;

   public SimpleRowValue(int var1) {
      this.virtualColumnCount = var1;
   }

   public SimpleRowValue(int var1, int var2) {
      this.virtualColumnCount = var1;
      this.index = var2;
   }

   public int getColumnCount() {
      return this.virtualColumnCount;
   }

   public Value getValue(int var1) {
      if (var1 == -1) {
         return ValueBigint.get(this.getKey());
      } else {
         return var1 == this.index ? this.data : null;
      }
   }

   public void setValue(int var1, Value var2) {
      if (var1 == -1) {
         this.setKey(var2.getLong());
      }

      this.index = var1;
      this.data = var2;
   }

   public String toString() {
      return "( /* " + this.key + " */ " + (this.data == null ? "null" : this.data.getTraceSQL()) + " )";
   }

   public int getMemory() {
      return 40 + (this.data == null ? 0 : this.data.getMemory());
   }

   public boolean isNull(int var1) {
      return var1 != this.index || this.data == null || this.data == ValueNull.INSTANCE;
   }

   public void copyFrom(SearchRow var1) {
      this.setKey(var1.getKey());
      this.setValue(this.index, var1.getValue(this.index));
   }
}
