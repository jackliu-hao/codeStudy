package org.h2.value;

import java.math.BigDecimal;
import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;

public final class ValueSmallint extends Value {
   static final int PRECISION = 16;
   public static final int DECIMAL_PRECISION = 5;
   static final int DISPLAY_SIZE = 6;
   private final short value;

   private ValueSmallint(short var1) {
      this.value = var1;
   }

   public Value add(Value var1) {
      ValueSmallint var2 = (ValueSmallint)var1;
      return checkRange(this.value + var2.value);
   }

   private static ValueSmallint checkRange(int var0) {
      if ((short)var0 != var0) {
         throw DbException.get(22003, (String)Integer.toString(var0));
      } else {
         return get((short)var0);
      }
   }

   public int getSignum() {
      return Integer.signum(this.value);
   }

   public Value negate() {
      return checkRange(-this.value);
   }

   public Value subtract(Value var1) {
      ValueSmallint var2 = (ValueSmallint)var1;
      return checkRange(this.value - var2.value);
   }

   public Value multiply(Value var1) {
      ValueSmallint var2 = (ValueSmallint)var1;
      return checkRange(this.value * var2.value);
   }

   public Value divide(Value var1, TypeInfo var2) {
      ValueSmallint var3 = (ValueSmallint)var1;
      if (var3.value == 0) {
         throw DbException.get(22012, (String)this.getTraceSQL());
      } else {
         return checkRange(this.value / var3.value);
      }
   }

   public Value modulus(Value var1) {
      ValueSmallint var2 = (ValueSmallint)var1;
      if (var2.value == 0) {
         throw DbException.get(22012, (String)this.getTraceSQL());
      } else {
         return get((short)(this.value % var2.value));
      }
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      return (var2 & 4) == 0 ? var1.append("CAST(").append(this.value).append(" AS SMALLINT)") : var1.append(this.value);
   }

   public TypeInfo getType() {
      return TypeInfo.TYPE_SMALLINT;
   }

   public int getValueType() {
      return 10;
   }

   public byte[] getBytes() {
      short var1 = this.value;
      return new byte[]{(byte)(var1 >> 8), (byte)var1};
   }

   public short getShort() {
      return this.value;
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
      return Integer.compare(this.value, ((ValueSmallint)var1).value);
   }

   public String getString() {
      return Integer.toString(this.value);
   }

   public int hashCode() {
      return this.value;
   }

   public static ValueSmallint get(short var0) {
      return (ValueSmallint)Value.cache(new ValueSmallint(var0));
   }

   public boolean equals(Object var1) {
      return var1 instanceof ValueSmallint && this.value == ((ValueSmallint)var1).value;
   }
}
