package org.h2.value;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.h2.api.Interval;
import org.h2.api.IntervalQualifier;
import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;
import org.h2.util.DateTimeUtils;
import org.h2.util.IntervalUtils;

public final class ValueInterval extends Value {
   public static final int DEFAULT_PRECISION = 2;
   public static final int MAXIMUM_PRECISION = 18;
   public static final int DEFAULT_SCALE = 6;
   public static final int MAXIMUM_SCALE = 9;
   private static final long[] MULTIPLIERS = new long[]{1000000000L, 12L, 24L, 1440L, 86400000000000L, 60L, 3600000000000L, 60000000000L};
   private final int valueType;
   private final boolean negative;
   private final long leading;
   private final long remaining;

   public static ValueInterval from(IntervalQualifier var0, boolean var1, long var2, long var4) {
      var1 = IntervalUtils.validateInterval(var0, var1, var2, var4);
      return (ValueInterval)Value.cache(new ValueInterval(var0.ordinal() + 22, var1, var2, var4));
   }

   public static int getDisplaySize(int var0, int var1, int var2) {
      switch (var0) {
         case 22:
         case 25:
            return 17 + var1;
         case 23:
            return 18 + var1;
         case 24:
            return 16 + var1;
         case 26:
            return 19 + var1;
         case 27:
            return var2 > 0 ? 20 + var1 + var2 : 19 + var1;
         case 28:
            return 29 + var1;
         case 29:
            return 27 + var1;
         case 30:
            return 32 + var1;
         case 31:
            return var2 > 0 ? 36 + var1 + var2 : 35 + var1;
         case 32:
            return 30 + var1;
         case 33:
            return var2 > 0 ? 34 + var1 + var2 : 33 + var1;
         case 34:
            return var2 > 0 ? 33 + var1 + var2 : 32 + var1;
         default:
            throw DbException.getUnsupportedException(Integer.toString(var0));
      }
   }

   private ValueInterval(int var1, boolean var2, long var3, long var5) {
      this.valueType = var1;
      this.negative = var2;
      this.leading = var3;
      this.remaining = var5;
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      return IntervalUtils.appendInterval(var1, this.getQualifier(), this.negative, this.leading, this.remaining);
   }

   public TypeInfo getType() {
      return TypeInfo.getTypeInfo(this.valueType);
   }

   public int getValueType() {
      return this.valueType;
   }

   public int getMemory() {
      return 48;
   }

   boolean checkPrecision(long var1) {
      if (var1 < 18L) {
         long var3 = this.leading;
         long var5 = 1L;

         for(long var7 = 0L; var3 >= var5; var5 *= 10L) {
            if (++var7 > var1) {
               return false;
            }
         }
      }

      return true;
   }

   ValueInterval setPrecisionAndScale(TypeInfo var1, Object var2) {
      int var3 = var1.getScale();
      ValueInterval var4 = this;
      if (var3 < 9) {
         label36: {
            long var5;
            switch (this.valueType) {
               case 27:
                  var5 = 1000000000L;
                  break;
               case 28:
               case 29:
               case 30:
               case 32:
               default:
                  break label36;
               case 31:
                  var5 = 86400000000000L;
                  break;
               case 33:
                  var5 = 3600000000000L;
                  break;
               case 34:
                  var5 = 60000000000L;
            }

            long var7 = this.leading;
            long var9 = DateTimeUtils.convertScale(this.remaining, var3, var7 == 999999999999999999L ? var5 : Long.MAX_VALUE);
            if (var9 != this.remaining) {
               if (var9 >= var5) {
                  ++var7;
                  var9 -= var5;
               }

               var4 = from(this.getQualifier(), this.isNegative(), var7, var9);
            }
         }
      }

      if (!var4.checkPrecision(var1.getPrecision())) {
         throw var4.getValueTooLongException(var1, var2);
      } else {
         return var4;
      }
   }

   public String getString() {
      return IntervalUtils.appendInterval(new StringBuilder(), this.getQualifier(), this.negative, this.leading, this.remaining).toString();
   }

   public long getLong() {
      long var1 = this.leading;
      if (this.valueType >= 27 && this.remaining != 0L && this.remaining >= MULTIPLIERS[this.valueType - 27] >> 1) {
         ++var1;
      }

      return this.negative ? -var1 : var1;
   }

   public BigDecimal getBigDecimal() {
      if (this.valueType >= 27 && this.remaining != 0L) {
         BigDecimal var1 = BigDecimal.valueOf(MULTIPLIERS[this.valueType - 27]);
         BigDecimal var2 = BigDecimal.valueOf(this.leading).add(BigDecimal.valueOf(this.remaining).divide(var1, var1.precision(), RoundingMode.HALF_DOWN)).stripTrailingZeros();
         return this.negative ? var2.negate() : var2;
      } else {
         return BigDecimal.valueOf(this.negative ? -this.leading : this.leading);
      }
   }

   public float getFloat() {
      if (this.valueType >= 27 && this.remaining != 0L) {
         return this.getBigDecimal().floatValue();
      } else {
         return this.negative ? (float)(-this.leading) : (float)this.leading;
      }
   }

   public double getDouble() {
      if (this.valueType >= 27 && this.remaining != 0L) {
         return this.getBigDecimal().doubleValue();
      } else {
         return this.negative ? (double)(-this.leading) : (double)this.leading;
      }
   }

   public Interval getInterval() {
      return new Interval(this.getQualifier(), this.negative, this.leading, this.remaining);
   }

   public IntervalQualifier getQualifier() {
      return IntervalQualifier.valueOf(this.valueType - 22);
   }

   public boolean isNegative() {
      return this.negative;
   }

   public long getLeading() {
      return this.leading;
   }

   public long getRemaining() {
      return this.remaining;
   }

   public int hashCode() {
      int var2 = 1;
      var2 = 31 * var2 + this.valueType;
      var2 = 31 * var2 + (this.negative ? 1231 : 1237);
      var2 = 31 * var2 + (int)(this.leading ^ this.leading >>> 32);
      var2 = 31 * var2 + (int)(this.remaining ^ this.remaining >>> 32);
      return var2;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ValueInterval)) {
         return false;
      } else {
         ValueInterval var2 = (ValueInterval)var1;
         return this.valueType == var2.valueType && this.negative == var2.negative && this.leading == var2.leading && this.remaining == var2.remaining;
      }
   }

   public int compareTypeSafe(Value var1, CompareMode var2, CastDataProvider var3) {
      ValueInterval var4 = (ValueInterval)var1;
      if (this.negative != var4.negative) {
         return this.negative ? -1 : 1;
      } else {
         int var5 = Long.compare(this.leading, var4.leading);
         if (var5 == 0) {
            var5 = Long.compare(this.remaining, var4.remaining);
         }

         return this.negative ? -var5 : var5;
      }
   }

   public int getSignum() {
      return this.negative ? -1 : (this.leading == 0L && this.remaining == 0L ? 0 : 1);
   }

   public Value add(Value var1) {
      return IntervalUtils.intervalFromAbsolute(this.getQualifier(), IntervalUtils.intervalToAbsolute(this).add(IntervalUtils.intervalToAbsolute((ValueInterval)var1)));
   }

   public Value subtract(Value var1) {
      return IntervalUtils.intervalFromAbsolute(this.getQualifier(), IntervalUtils.intervalToAbsolute(this).subtract(IntervalUtils.intervalToAbsolute((ValueInterval)var1)));
   }

   public Value negate() {
      return (Value)(this.leading == 0L && this.remaining == 0L ? this : Value.cache(new ValueInterval(this.valueType, !this.negative, this.leading, this.remaining)));
   }
}
