package org.h2.mvstore.type;

import java.nio.ByteBuffer;
import org.h2.mvstore.DataUtils;
import org.h2.mvstore.WriteBuffer;

public class LongDataType extends BasicDataType<Long> {
   public static final LongDataType INSTANCE = new LongDataType();
   private static final Long[] EMPTY_LONG_ARR = new Long[0];

   private LongDataType() {
   }

   public int getMemory(Long var1) {
      return 8;
   }

   public void write(WriteBuffer var1, Long var2) {
      var1.putVarLong(var2);
   }

   public Long read(ByteBuffer var1) {
      return DataUtils.readVarLong(var1);
   }

   public Long[] createStorage(int var1) {
      return var1 == 0 ? EMPTY_LONG_ARR : new Long[var1];
   }

   public int compare(Long var1, Long var2) {
      return Long.compare(var1, var2);
   }

   public int binarySearch(Long var1, Object var2, int var3, int var4) {
      long var5 = var1;
      Long[] var7 = (Long[])this.cast(var2);
      byte var8 = 0;
      int var9 = var3 - 1;
      int var10 = var4 - 1;
      if (var10 < 0 || var10 > var9) {
         var10 = var9 >>> 1;
      }

      return binarySearch(var5, var7, var8, var9, var10);
   }

   private static int binarySearch(long var0, Long[] var2, int var3, int var4, int var5) {
      for(; var3 <= var4; var5 = var3 + var4 >>> 1) {
         long var6 = var2[var5];
         if (var0 > var6) {
            var3 = var5 + 1;
         } else {
            if (var0 >= var6) {
               return var5;
            }

            var4 = var5 - 1;
         }
      }

      return -(var3 + 1);
   }
}
