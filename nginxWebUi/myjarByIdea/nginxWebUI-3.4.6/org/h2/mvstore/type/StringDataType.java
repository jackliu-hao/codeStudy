package org.h2.mvstore.type;

import java.nio.ByteBuffer;
import org.h2.mvstore.DataUtils;
import org.h2.mvstore.WriteBuffer;

public class StringDataType extends BasicDataType<String> {
   public static final StringDataType INSTANCE = new StringDataType();
   private static final String[] EMPTY_STRING_ARR = new String[0];

   public String[] createStorage(int var1) {
      return var1 == 0 ? EMPTY_STRING_ARR : new String[var1];
   }

   public int compare(String var1, String var2) {
      return var1.compareTo(var2);
   }

   public int binarySearch(String var1, Object var2, int var3, int var4) {
      String[] var5 = (String[])this.cast(var2);
      int var6 = 0;
      int var7 = var3 - 1;
      int var8 = var4 - 1;
      if (var8 < 0 || var8 > var7) {
         var8 = var7 >>> 1;
      }

      for(; var6 <= var7; var8 = var6 + var7 >>> 1) {
         int var9 = var1.compareTo(var5[var8]);
         if (var9 > 0) {
            var6 = var8 + 1;
         } else {
            if (var9 >= 0) {
               return var8;
            }

            var7 = var8 - 1;
         }
      }

      return -(var6 + 1);
   }

   public int getMemory(String var1) {
      return 24 + 2 * var1.length();
   }

   public String read(ByteBuffer var1) {
      return DataUtils.readString(var1);
   }

   public void write(WriteBuffer var1, String var2) {
      int var3 = var2.length();
      var1.putVarInt(var3).putStringData(var2, var3);
   }
}
