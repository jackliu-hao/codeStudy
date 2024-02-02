package org.h2.value;

import java.math.BigDecimal;
import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;
import org.h2.util.Bits;

public final class ValueInteger extends Value {
   public static final int PRECISION = 32;
   public static final int DECIMAL_PRECISION = 10;
   public static final int DISPLAY_SIZE = 11;
   private static final int STATIC_SIZE = 128;
   private static final int DYNAMIC_SIZE = 256;
   private static final ValueInteger[] STATIC_CACHE = new ValueInteger[128];
   private static final ValueInteger[] DYNAMIC_CACHE = new ValueInteger[256];
   private final int value;

   private ValueInteger(int var1) {
      this.value = var1;
   }

   public static ValueInteger get(int var0) {
      if (var0 >= 0 && var0 < 128) {
         return STATIC_CACHE[var0];
      } else {
         ValueInteger var1 = DYNAMIC_CACHE[var0 & 255];
         if (var1 == null || var1.value != var0) {
            var1 = new ValueInteger(var0);
            DYNAMIC_CACHE[var0 & 255] = var1;
         }

         return var1;
      }
   }

   public Value add(Value var1) {
      ValueInteger var2 = (ValueInteger)var1;
      return checkRange((long)this.value + (long)var2.value);
   }

   private static ValueInteger checkRange(long var0) {
      if ((long)((int)var0) != var0) {
         throw DbException.get(22003, (String)Long.toString(var0));
      } else {
         return get((int)var0);
      }
   }

   public int getSignum() {
      return Integer.signum(this.value);
   }

   public Value negate() {
      return checkRange(-((long)this.value));
   }

   public Value subtract(Value var1) {
      ValueInteger var2 = (ValueInteger)var1;
      return checkRange((long)this.value - (long)var2.value);
   }

   public Value multiply(Value var1) {
      ValueInteger var2 = (ValueInteger)var1;
      return checkRange((long)this.value * (long)var2.value);
   }

   public Value divide(Value var1, TypeInfo var2) {
      int var3 = ((ValueInteger)var1).value;
      if (var3 == 0) {
         throw DbException.get(22012, (String)this.getTraceSQL());
      } else {
         int var4 = this.value;
         if (var4 == Integer.MIN_VALUE && var3 == -1) {
            throw DbException.get(22003, (String)"2147483648");
         } else {
            return get(var4 / var3);
         }
      }
   }

   public Value modulus(Value var1) {
      ValueInteger var2 = (ValueInteger)var1;
      if (var2.value == 0) {
         throw DbException.get(22012, (String)this.getTraceSQL());
      } else {
         return get(this.value % var2.value);
      }
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      return var1.append(this.value);
   }

   public TypeInfo getType() {
      return TypeInfo.TYPE_INTEGER;
   }

   public int getValueType() {
      return 11;
   }

   public byte[] getBytes() {
      byte[] var1 = new byte[4];
      Bits.writeInt(var1, 0, this.getInt());
      return var1;
   }

   public int getInt() {
      return this.value;
   }

   public long getLong() {
      return (long)this.value;
   }

   public BigDecimal getBigDecimal() {
      return BigDecimal.valueOf((long)this.value);
   }

   public float getFloat() {
      return (float)this.value;
   }

   public double getDouble() {
      return (double)this.value;
   }

   public int compareTypeSafe(Value var1, CompareMode var2, CastDataProvider var3) {
      return Integer.compare(this.value, ((ValueInteger)var1).value);
   }

   public String getString() {
      return Integer.toString(this.value);
   }

   public int hashCode() {
      return this.value;
   }

   public boolean equals(Object var1) {
      return var1 instanceof ValueInteger && this.value == ((ValueInteger)var1).value;
   }

   static {
      for(int var0 = 0; var0 < 128; ++var0) {
         STATIC_CACHE[var0] = new ValueInteger(var0);
      }

   }
}
