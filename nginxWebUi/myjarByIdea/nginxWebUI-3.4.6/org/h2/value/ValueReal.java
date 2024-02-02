package org.h2.value;

import java.math.BigDecimal;
import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;

public final class ValueReal extends Value {
   static final int PRECISION = 24;
   static final int DECIMAL_PRECISION = 7;
   static final int DISPLAY_SIZE = 15;
   public static final int ZERO_BITS = 0;
   public static final ValueReal ZERO = new ValueReal(0.0F);
   public static final ValueReal ONE = new ValueReal(1.0F);
   private static final ValueReal NAN = new ValueReal(Float.NaN);
   private final float value;

   private ValueReal(float var1) {
      this.value = var1;
   }

   public Value add(Value var1) {
      return get(this.value + ((ValueReal)var1).value);
   }

   public Value subtract(Value var1) {
      return get(this.value - ((ValueReal)var1).value);
   }

   public Value negate() {
      return get(-this.value);
   }

   public Value multiply(Value var1) {
      return get(this.value * ((ValueReal)var1).value);
   }

   public Value divide(Value var1, TypeInfo var2) {
      ValueReal var3 = (ValueReal)var1;
      if ((double)var3.value == 0.0) {
         throw DbException.get(22012, (String)this.getTraceSQL());
      } else {
         return get(this.value / var3.value);
      }
   }

   public Value modulus(Value var1) {
      ValueReal var2 = (ValueReal)var1;
      if (var2.value == 0.0F) {
         throw DbException.get(22012, (String)this.getTraceSQL());
      } else {
         return get(this.value % var2.value);
      }
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      return (var2 & 4) == 0 ? this.getSQL(var1.append("CAST(")).append(" AS REAL)") : this.getSQL(var1);
   }

   private StringBuilder getSQL(StringBuilder var1) {
      if (this.value == Float.POSITIVE_INFINITY) {
         return var1.append("'Infinity'");
      } else if (this.value == Float.NEGATIVE_INFINITY) {
         return var1.append("'-Infinity'");
      } else {
         return Float.isNaN(this.value) ? var1.append("'NaN'") : var1.append(this.value);
      }
   }

   public TypeInfo getType() {
      return TypeInfo.TYPE_REAL;
   }

   public int getValueType() {
      return 14;
   }

   public int compareTypeSafe(Value var1, CompareMode var2, CastDataProvider var3) {
      return Float.compare(this.value, ((ValueReal)var1).value);
   }

   public int getSignum() {
      return this.value != 0.0F && !Float.isNaN(this.value) ? (this.value < 0.0F ? -1 : 1) : 0;
   }

   public BigDecimal getBigDecimal() {
      if (Float.isFinite(this.value)) {
         return new BigDecimal(Float.toString(this.value));
      } else {
         throw DbException.get(22018, (String)Float.toString(this.value));
      }
   }

   public float getFloat() {
      return this.value;
   }

   public double getDouble() {
      return (double)this.value;
   }

   public String getString() {
      return Float.toString(this.value);
   }

   public int hashCode() {
      return Float.floatToRawIntBits(this.value);
   }

   public static ValueReal get(float var0) {
      if (var0 == 1.0F) {
         return ONE;
      } else if (var0 == 0.0F) {
         return ZERO;
      } else {
         return Float.isNaN(var0) ? NAN : (ValueReal)Value.cache(new ValueReal(var0));
      }
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof ValueReal)) {
         return false;
      } else {
         return this.compareTypeSafe((ValueReal)var1, (CompareMode)null, (CastDataProvider)null) == 0;
      }
   }
}
