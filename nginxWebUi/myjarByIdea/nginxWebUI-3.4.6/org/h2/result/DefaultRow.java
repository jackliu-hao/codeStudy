package org.h2.result;

import org.h2.value.Value;
import org.h2.value.ValueBigint;

public class DefaultRow extends Row {
   public static final int MEMORY_CALCULATE = -1;
   protected final Value[] data;
   private int memory;

   DefaultRow(int var1) {
      this.data = new Value[var1];
      this.memory = -1;
   }

   public DefaultRow(Value[] var1) {
      this.data = var1;
      this.memory = -1;
   }

   public DefaultRow(Value[] var1, int var2) {
      this.data = var1;
      this.memory = var2;
   }

   public Value getValue(int var1) {
      return (Value)(var1 == -1 ? ValueBigint.get(this.key) : this.data[var1]);
   }

   public void setValue(int var1, Value var2) {
      if (var1 == -1) {
         this.key = var2.getLong();
      } else {
         this.data[var1] = var2;
      }

   }

   public int getColumnCount() {
      return this.data.length;
   }

   public int getMemory() {
      return this.memory != -1 ? this.memory : (this.memory = this.calculateMemory());
   }

   public String toString() {
      StringBuilder var1 = (new StringBuilder("( /* key:")).append(this.key).append(" */ ");
      int var2 = 0;

      for(int var3 = this.data.length; var2 < var3; ++var2) {
         if (var2 > 0) {
            var1.append(", ");
         }

         Value var4 = this.data[var2];
         var1.append(var4 == null ? "null" : var4.getTraceSQL());
      }

      return var1.append(')').toString();
   }

   protected int calculateMemory() {
      int var1 = 64 + this.data.length * 8;
      Value[] var2 = this.data;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Value var5 = var2[var4];
         if (var5 != null) {
            var1 += var5.getMemory();
         }
      }

      return var1;
   }

   public Value[] getValueList() {
      return this.data;
   }

   public boolean hasSharedData(Row var1) {
      return var1 instanceof DefaultRow && this.data == ((DefaultRow)var1).data;
   }

   public void copyFrom(SearchRow var1) {
      this.setKey(var1.getKey());

      for(int var2 = 0; var2 < this.getColumnCount(); ++var2) {
         this.setValue(var2, var1.getValue(var2));
      }

   }
}
