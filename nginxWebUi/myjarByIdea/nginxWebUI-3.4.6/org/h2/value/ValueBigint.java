package org.h2.value;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;
import org.h2.util.Bits;

public final class ValueBigint extends Value {
   public static final ValueBigint MIN = get(Long.MIN_VALUE);
   public static final ValueBigint MAX = get(Long.MAX_VALUE);
   public static final BigInteger MAX_BI = BigInteger.valueOf(Long.MAX_VALUE);
   static final int PRECISION = 64;
   public static final int DECIMAL_PRECISION = 19;
   public static final int DISPLAY_SIZE = 20;
   private static final int STATIC_SIZE = 100;
   private static final ValueBigint[] STATIC_CACHE = new ValueBigint[100];
   private final long value;

   private ValueBigint(long var1) {
      this.value = var1;
   }

   public Value add(Value var1) {
      long var2 = this.value;
      long var4 = ((ValueBigint)var1).value;
      long var6 = var2 + var4;
      if (((var2 ^ var6) & (var4 ^ var6)) < 0L) {
         throw this.getOverflow();
      } else {
         return get(var6);
      }
   }

   public int getSignum() {
      return Long.signum(this.value);
   }

   public Value negate() {
      if (this.value == Long.MIN_VALUE) {
         throw this.getOverflow();
      } else {
         return get(-this.value);
      }
   }

   private DbException getOverflow() {
      return DbException.get(22003, (String)Long.toString(this.value));
   }

   public Value subtract(Value var1) {
      long var2 = this.value;
      long var4 = ((ValueBigint)var1).value;
      long var6 = var2 - var4;
      if (((var2 ^ var4) & (var2 ^ var6)) < 0L) {
         throw this.getOverflow();
      } else {
         return get(var6);
      }
   }

   public Value multiply(Value var1) {
      long var2 = this.value;
      long var4 = ((ValueBigint)var1).value;
      long var6 = var2 * var4;
      if ((Math.abs(var2) | Math.abs(var4)) >>> 31 == 0L || var4 == 0L || var6 / var4 == var2 && (var2 != Long.MIN_VALUE || var4 != -1L)) {
         return get(var6);
      } else {
         throw this.getOverflow();
      }
   }

   public Value divide(Value var1, TypeInfo var2) {
      long var3 = ((ValueBigint)var1).value;
      if (var3 == 0L) {
         throw DbException.get(22012, (String)this.getTraceSQL());
      } else {
         long var5 = this.value;
         if (var5 == Long.MIN_VALUE && var3 == -1L) {
            throw this.getOverflow();
         } else {
            return get(var5 / var3);
         }
      }
   }

   public Value modulus(Value var1) {
      ValueBigint var2 = (ValueBigint)var1;
      if (var2.value == 0L) {
         throw DbException.get(22012, (String)this.getTraceSQL());
      } else {
         return get(this.value % var2.value);
      }
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      return (var2 & 4) == 0 && this.value == (long)((int)this.value) ? var1.append("CAST(").append(this.value).append(" AS BIGINT)") : var1.append(this.value);
   }

   public TypeInfo getType() {
      return TypeInfo.TYPE_BIGINT;
   }

   public int getValueType() {
      return 12;
   }

   public byte[] getBytes() {
      byte[] var1 = new byte[8];
      Bits.writeLong(var1, 0, this.getLong());
      return var1;
   }

   public long getLong() {
      return this.value;
   }

   public BigDecimal getBigDecimal() {
      return BigDecimal.valueOf(this.value);
   }

   public float getFloat() {
      return (float)this.value;
   }

   public double getDouble() {
      return (double)this.value;
   }

   public int compareTypeSafe(Value var1, CompareMode var2, CastDataProvider var3) {
      return Long.compare(this.value, ((ValueBigint)var1).value);
   }

   public String getString() {
      return Long.toString(this.value);
   }

   public int hashCode() {
      return (int)(this.value ^ this.value >> 32);
   }

   public static ValueBigint get(long var0) {
      return var0 >= 0L && var0 < 100L ? STATIC_CACHE[(int)var0] : (ValueBigint)Value.cache(new ValueBigint(var0));
   }

   public boolean equals(Object var1) {
      return var1 instanceof ValueBigint && this.value == ((ValueBigint)var1).value;
   }

   static {
      for(int var0 = 0; var0 < 100; ++var0) {
         STATIC_CACHE[var0] = new ValueBigint((long)var0);
      }

   }
}
