package org.h2.value;

import java.math.BigDecimal;
import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;

public final class ValueTinyint extends Value {
   static final int PRECISION = 8;
   public static final int DECIMAL_PRECISION = 3;
   static final int DISPLAY_SIZE = 4;
   private final byte value;

   private ValueTinyint(byte var1) {
      this.value = var1;
   }

   public Value add(Value var1) {
      ValueTinyint var2 = (ValueTinyint)var1;
      return checkRange(this.value + var2.value);
   }

   private static ValueTinyint checkRange(int var0) {
      if ((byte)var0 != var0) {
         throw DbException.get(22003, (String)Integer.toString(var0));
      } else {
         return get((byte)var0);
      }
   }

   public int getSignum() {
      return Integer.signum(this.value);
   }

   public Value negate() {
      return checkRange(-this.value);
   }

   public Value subtract(Value var1) {
      ValueTinyint var2 = (ValueTinyint)var1;
      return checkRange(this.value - var2.value);
   }

   public Value multiply(Value var1) {
      ValueTinyint var2 = (ValueTinyint)var1;
      return checkRange(this.value * var2.value);
   }

   public Value divide(Value var1, TypeInfo var2) {
      ValueTinyint var3 = (ValueTinyint)var1;
      if (var3.value == 0) {
         throw DbException.get(22012, (String)this.getTraceSQL());
      } else {
         return checkRange(this.value / var3.value);
      }
   }

   public Value modulus(Value var1) {
      ValueTinyint var2 = (ValueTinyint)var1;
      if (var2.value == 0) {
         throw DbException.get(22012, (String)this.getTraceSQL());
      } else {
         return get((byte)(this.value % var2.value));
      }
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      return (var2 & 4) == 0 ? var1.append("CAST(").append(this.value).append(" AS TINYINT)") : var1.append(this.value);
   }

   public TypeInfo getType() {
      return TypeInfo.TYPE_TINYINT;
   }

   public int getValueType() {
      return 9;
   }

   public byte[] getBytes() {
      return new byte[]{this.value};
   }

   public byte getByte() {
      return this.value;
   }

   public short getShort() {
      return (short)this.value;
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
      return Integer.compare(this.value, ((ValueTinyint)var1).value);
   }

   public String getString() {
      return Integer.toString(this.value);
   }

   public int hashCode() {
      return this.value;
   }

   public static ValueTinyint get(byte var0) {
      return (ValueTinyint)Value.cache(new ValueTinyint(var0));
   }

   public boolean equals(Object var1) {
      return var1 instanceof ValueTinyint && this.value == ((ValueTinyint)var1).value;
   }
}
