package org.h2.mvstore.type;

import java.nio.ByteBuffer;
import org.h2.mvstore.DataUtils;
import org.h2.mvstore.WriteBuffer;

public abstract class BasicDataType<T> implements DataType<T> {
   public abstract int getMemory(T var1);

   public abstract void write(WriteBuffer var1, T var2);

   public abstract T read(ByteBuffer var1);

   public int compare(T var1, T var2) {
      throw DataUtils.newUnsupportedOperationException("Can not compare");
   }

   public boolean isMemoryEstimationAllowed() {
      return true;
   }

   public int binarySearch(T var1, Object var2, int var3, int var4) {
      Object[] var5 = this.cast(var2);
      int var6 = 0;
      int var7 = var3 - 1;
      int var8 = var4 - 1;
      if (var8 < 0 || var8 > var7) {
         var8 = var7 >>> 1;
      }

      for(; var6 <= var7; var8 = var6 + var7 >>> 1) {
         int var9 = this.compare(var1, var5[var8]);
         if (var9 > 0) {
            var6 = var8 + 1;
         } else {
            if (var9 >= 0) {
               return var8;
            }

            var7 = var8 - 1;
         }
      }

      return ~var6;
   }

   public void write(WriteBuffer var1, Object var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var4) {
         this.write(var1, this.cast(var2)[var4]);
      }

   }

   public void read(ByteBuffer var1, Object var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var4) {
         this.cast(var2)[var4] = this.read(var1);
      }

   }

   public int hashCode() {
      return this.getClass().getName().hashCode();
   }

   public boolean equals(Object var1) {
      return var1 != null && this.getClass().equals(var1.getClass());
   }

   protected final T[] cast(Object var1) {
      return (Object[])((Object[])var1);
   }
}
