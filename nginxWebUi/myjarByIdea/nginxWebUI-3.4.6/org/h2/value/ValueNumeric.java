package org.h2.value;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;

public final class ValueNumeric extends ValueBigDecimalBase {
   public static final ValueNumeric ZERO;
   public static final ValueNumeric ONE;
   public static final int DEFAULT_SCALE = 0;
   public static final int MAXIMUM_SCALE = 100000;

   private ValueNumeric(BigDecimal var1) {
      super(var1);
      if (var1 == null) {
         throw new IllegalArgumentException("null");
      } else {
         int var2 = var1.scale();
         if (var2 < 0 || var2 > 100000) {
            throw DbException.get(90151, Integer.toString(var2), "0", "100000");
         }
      }
   }

   public String getString() {
      return this.value.toPlainString();
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      String var3 = this.getString();
      return (var2 & 4) == 0 && var3.indexOf(46) < 0 && this.value.compareTo(MAX_LONG_DECIMAL) <= 0 && this.value.compareTo(MIN_LONG_DECIMAL) >= 0 ? var1.append("CAST(").append(this.value).append(" AS NUMERIC(").append(this.value.precision()).append("))") : var1.append(var3);
   }

   public TypeInfo getType() {
      TypeInfo var1 = this.type;
      if (var1 == null) {
         this.type = var1 = new TypeInfo(13, (long)this.value.precision(), this.value.scale(), (ExtTypeInfo)null);
      }

      return var1;
   }

   public int getValueType() {
      return 13;
   }

   public Value add(Value var1) {
      return get(this.value.add(((ValueNumeric)var1).value));
   }

   public Value subtract(Value var1) {
      return get(this.value.subtract(((ValueNumeric)var1).value));
   }

   public Value negate() {
      return get(this.value.negate());
   }

   public Value multiply(Value var1) {
      return get(this.value.multiply(((ValueNumeric)var1).value));
   }

   public Value divide(Value var1, TypeInfo var2) {
      BigDecimal var3 = ((ValueNumeric)var1).value;
      if (var3.signum() == 0) {
         throw DbException.get(22012, (String)this.getTraceSQL());
      } else {
         return get(this.value.divide(var3, var2.getScale(), RoundingMode.HALF_DOWN));
      }
   }

   public Value modulus(Value var1) {
      ValueNumeric var2 = (ValueNumeric)var1;
      if (var2.value.signum() == 0) {
         throw DbException.get(22012, (String)this.getTraceSQL());
      } else {
         return get(this.value.remainder(var2.value));
      }
   }

   public int compareTypeSafe(Value var1, CompareMode var2, CastDataProvider var3) {
      return this.value.compareTo(((ValueNumeric)var1).value);
   }

   public int getSignum() {
      return this.value.signum();
   }

   public BigDecimal getBigDecimal() {
      return this.value;
   }

   public float getFloat() {
      return this.value.floatValue();
   }

   public double getDouble() {
      return this.value.doubleValue();
   }

   public int hashCode() {
      return this.getClass().hashCode() * 31 + this.value.hashCode();
   }

   public boolean equals(Object var1) {
      return var1 instanceof ValueNumeric && this.value.equals(((ValueNumeric)var1).value);
   }

   public int getMemory() {
      return this.value.precision() + 120;
   }

   public static ValueNumeric get(BigDecimal var0) {
      if (BigDecimal.ZERO.equals(var0)) {
         return ZERO;
      } else {
         return BigDecimal.ONE.equals(var0) ? ONE : (ValueNumeric)Value.cache(new ValueNumeric(var0));
      }
   }

   public static ValueNumeric getAnyScale(BigDecimal var0) {
      if (var0.scale() < 0) {
         var0 = var0.setScale(0, RoundingMode.UNNECESSARY);
      }

      return get(var0);
   }

   public static ValueNumeric get(BigInteger var0) {
      if (var0.signum() == 0) {
         return ZERO;
      } else {
         return BigInteger.ONE.equals(var0) ? ONE : (ValueNumeric)Value.cache(new ValueNumeric(new BigDecimal(var0)));
      }
   }

   public static BigDecimal setScale(BigDecimal var0, int var1) {
      if (var1 >= 0 && var1 <= 100000) {
         return var0.setScale(var1, RoundingMode.HALF_UP);
      } else {
         throw DbException.getInvalidValueException("scale", var1);
      }
   }

   static {
      ZERO = new ValueNumeric(BigDecimal.ZERO);
      ONE = new ValueNumeric(BigDecimal.ONE);
   }
}
