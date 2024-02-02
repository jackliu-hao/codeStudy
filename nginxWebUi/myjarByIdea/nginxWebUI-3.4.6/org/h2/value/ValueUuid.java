package org.h2.value;

import java.util.UUID;
import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;
import org.h2.util.Bits;
import org.h2.util.MathUtils;
import org.h2.util.StringUtils;

public final class ValueUuid extends Value {
   static final int PRECISION = 16;
   static final int DISPLAY_SIZE = 36;
   private final long high;
   private final long low;

   private ValueUuid(long var1, long var3) {
      this.high = var1;
      this.low = var3;
   }

   public int hashCode() {
      return (int)(this.high >>> 32 ^ this.high ^ this.low >>> 32 ^ this.low);
   }

   public static ValueUuid getNewRandom() {
      long var0 = MathUtils.secureRandomLong();
      long var2 = MathUtils.secureRandomLong();
      var0 = var0 & -61441L | 16384L;
      var2 = var2 & 4611686018427387903L | Long.MIN_VALUE;
      return new ValueUuid(var0, var2);
   }

   public static ValueUuid get(byte[] var0) {
      int var1 = var0.length;
      if (var1 != 16) {
         throw DbException.get(22018, (String)("UUID requires 16 bytes, got " + var1));
      } else {
         return get(Bits.readLong(var0, 0), Bits.readLong(var0, 8));
      }
   }

   public static ValueUuid get(long var0, long var2) {
      return (ValueUuid)Value.cache(new ValueUuid(var0, var2));
   }

   public static ValueUuid get(UUID var0) {
      return get(var0.getMostSignificantBits(), var0.getLeastSignificantBits());
   }

   public static ValueUuid get(String var0) {
      long var1 = 0L;
      long var3 = 0L;
      int var5 = 0;
      int var6 = 0;

      for(int var7 = var0.length(); var6 < var7; ++var6) {
         char var8 = var0.charAt(var6);
         if (var8 >= '0' && var8 <= '9') {
            var1 = var1 << 4 | (long)(var8 - 48);
         } else if (var8 >= 'a' && var8 <= 'f') {
            var1 = var1 << 4 | (long)(var8 - 87);
         } else {
            if (var8 == '-') {
               continue;
            }

            if (var8 < 'A' || var8 > 'F') {
               if (var8 > ' ') {
                  throw DbException.get(22018, (String)var0);
               }
               continue;
            }

            var1 = var1 << 4 | (long)(var8 - 55);
         }

         ++var5;
         if (var5 == 16) {
            var3 = var1;
            var1 = 0L;
         }
      }

      if (var5 != 32) {
         throw DbException.get(22018, (String)var0);
      } else {
         return get(var3, var1);
      }
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      return this.addString(var1.append("UUID '")).append('\'');
   }

   public TypeInfo getType() {
      return TypeInfo.TYPE_UUID;
   }

   public int getMemory() {
      return 32;
   }

   public int getValueType() {
      return 39;
   }

   public String getString() {
      return this.addString(new StringBuilder(36)).toString();
   }

   public byte[] getBytes() {
      return Bits.uuidToBytes(this.high, this.low);
   }

   private StringBuilder addString(StringBuilder var1) {
      StringUtils.appendHex(var1, this.high >> 32, 4).append('-');
      StringUtils.appendHex(var1, this.high >> 16, 2).append('-');
      StringUtils.appendHex(var1, this.high, 2).append('-');
      StringUtils.appendHex(var1, this.low >> 48, 2).append('-');
      return StringUtils.appendHex(var1, this.low, 6);
   }

   public int compareTypeSafe(Value var1, CompareMode var2, CastDataProvider var3) {
      if (var1 == this) {
         return 0;
      } else {
         ValueUuid var4 = (ValueUuid)var1;
         int var5 = Long.compareUnsigned(this.high, var4.high);
         return var5 != 0 ? var5 : Long.compareUnsigned(this.low, var4.low);
      }
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof ValueUuid)) {
         return false;
      } else {
         ValueUuid var2 = (ValueUuid)var1;
         return this.high == var2.high && this.low == var2.low;
      }
   }

   public UUID getUuid() {
      return new UUID(this.high, this.low);
   }

   public long getHigh() {
      return this.high;
   }

   public long getLow() {
      return this.low;
   }

   public long charLength() {
      return 36L;
   }

   public long octetLength() {
      return 16L;
   }
}
