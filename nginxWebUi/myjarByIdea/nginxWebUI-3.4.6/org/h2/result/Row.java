package org.h2.result;

import java.util.Arrays;
import org.h2.value.Value;

public abstract class Row extends SearchRow {
   public static Row get(Value[] var0, int var1) {
      return new DefaultRow(var0, var1);
   }

   public static Row get(Value[] var0, int var1, long var2) {
      DefaultRow var4 = new DefaultRow(var0, var1);
      var4.setKey(var2);
      return var4;
   }

   public abstract Value[] getValueList();

   public boolean hasSameValues(Row var1) {
      return Arrays.equals(this.getValueList(), var1.getValueList());
   }

   public boolean hasSharedData(Row var1) {
      return false;
   }
}
