package org.h2.value;

import java.math.BigDecimal;
import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;

public final class ValueDouble extends Value {
   static final int PRECISION = 53;
   public static final int DECIMAL_PRECISION = 17;
   public static final int DISPLAY_SIZE = 24;
   public static final long ZERO_BITS = 0L;
   public static final ValueDouble ZERO = new ValueDouble(0.0);
   public static final ValueDouble ONE = new ValueDouble(1.0);
   private static final ValueDouble NAN = new ValueDouble(Double.NaN);
   private final double value;

   private ValueDouble(double var1) {
      this.value = var1;
   }

   public Value add(Value var1) {
      return get(this.value + ((ValueDouble)var1).value);
   }

   public Value subtract(Value var1) {
      return get(this.value - ((ValueDouble)var1).value);
   }

   public Value negate() {
      return get(-this.value);
   }

   public Value multiply(Value var1) {
      return get(this.value * ((ValueDouble)var1).value);
   }

   public Value divide(Value var1, TypeInfo var2) {
      ValueDouble var3 = (ValueDouble)var1;
      if (var3.value == 0.0) {
         throw DbException.get(22012, (String)this.getTraceSQL());
      } else {
         return get(this.value / var3.value);
      }
   }

   public ValueDouble modulus(Value var1) {
      ValueDouble var2 = (ValueDouble)var1;
      if (var2.value == 0.0) {
         throw DbException.get(22012, (String)this.getTraceSQL());
      } else {
         return get(this.value % var2.value);
      }
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      return (var2 & 4) == 0 ? this.getSQL(var1.append("CAST(")).append(" AS DOUBLE PRECISION)") : this.getSQL(var1);
   }

   private StringBuilder getSQL(StringBuilder var1) {
      if (this.value == Double.POSITIVE_INFINITY) {
         return var1.append("'Infinity'");
      } else if (this.value == Double.NEGATIVE_INFINITY) {
         return var1.append("'-Infinity'");
      } else {
         return Double.isNaN(this.value) ? var1.append("'NaN'") : var1.append(this.value);
      }
   }

   public TypeInfo getType() {
      return TypeInfo.TYPE_DOUBLE;
   }

   public int getValueType() {
      return 15;
   }

   public int compareTypeSafe(Value var1, CompareMode var2, CastDataProvider var3) {
      return Double.compare(this.value, ((ValueDouble)var1).value);
   }

   public int getSignum() {
      return this.value != 0.0 && !Double.isNaN(this.value) ? (this.value < 0.0 ? -1 : 1) : 0;
   }

   public BigDecimal getBigDecimal() {
      if (Double.isFinite(this.value)) {
         return BigDecimal.valueOf(this.value);
      } else {
         throw DbException.get(22018, (String)Double.toString(this.value));
      }
   }

   public float getFloat() {
      return (float)this.value;
   }

   public double getDouble() {
      return this.value;
   }

   public String getString() {
      return Double.toString(this.value);
   }

   public int hashCode() {
      long var1 = Double.doubleToRawLongBits(this.value);
      return (int)(var1 ^ var1 >>> 32);
   }

   public static ValueDouble get(double var0) {
      if (var0 == 1.0) {
         return ONE;
      } else if (var0 == 0.0) {
         return ZERO;
      } else {
         return Double.isNaN(var0) ? NAN : (ValueDouble)Value.cache(new ValueDouble(var0));
      }
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof ValueDouble)) {
         return false;
      } else {
         return this.compareTypeSafe((ValueDouble)var1, (CompareMode)null, (CastDataProvider)null) == 0;
      }
   }
}
