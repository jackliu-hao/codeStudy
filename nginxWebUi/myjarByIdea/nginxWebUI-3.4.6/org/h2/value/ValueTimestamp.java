package org.h2.value;

import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;
import org.h2.util.DateTimeUtils;

public final class ValueTimestamp extends Value {
   public static final int DEFAULT_PRECISION = 26;
   public static final int MAXIMUM_PRECISION = 29;
   public static final int DEFAULT_SCALE = 6;
   public static final int MAXIMUM_SCALE = 9;
   private final long dateValue;
   private final long timeNanos;

   private ValueTimestamp(long var1, long var3) {
      if (var1 >= -511999999967L && var1 <= 512000000415L) {
         if (var3 >= 0L && var3 < 86400000000000L) {
            this.dateValue = var1;
            this.timeNanos = var3;
         } else {
            throw new IllegalArgumentException("timeNanos out of range " + var3);
         }
      } else {
         throw new IllegalArgumentException("dateValue out of range " + var1);
      }
   }

   public static ValueTimestamp fromDateValueAndNanos(long var0, long var2) {
      return (ValueTimestamp)Value.cache(new ValueTimestamp(var0, var2));
   }

   public static ValueTimestamp parse(String var0, CastDataProvider var1) {
      try {
         return (ValueTimestamp)DateTimeUtils.parseTimestamp(var0, var1, false);
      } catch (Exception var3) {
         throw DbException.get(22007, var3, "TIMESTAMP", var0);
      }
   }

   public long getDateValue() {
      return this.dateValue;
   }

   public long getTimeNanos() {
      return this.timeNanos;
   }

   public TypeInfo getType() {
      return TypeInfo.TYPE_TIMESTAMP;
   }

   public int getValueType() {
      return 20;
   }

   public int getMemory() {
      return 32;
   }

   public String getString() {
      return this.toString(new StringBuilder(29), false).toString();
   }

   public String getISOString() {
      return this.toString(new StringBuilder(29), true).toString();
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      return this.toString(var1.append("TIMESTAMP '"), false).append('\'');
   }

   private StringBuilder toString(StringBuilder var1, boolean var2) {
      DateTimeUtils.appendDate(var1, this.dateValue).append((char)(var2 ? 'T' : ' '));
      return DateTimeUtils.appendTime(var1, this.timeNanos);
   }

   public int compareTypeSafe(Value var1, CompareMode var2, CastDataProvider var3) {
      ValueTimestamp var4 = (ValueTimestamp)var1;
      int var5 = Long.compare(this.dateValue, var4.dateValue);
      return var5 != 0 ? var5 : Long.compare(this.timeNanos, var4.timeNanos);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ValueTimestamp)) {
         return false;
      } else {
         ValueTimestamp var2 = (ValueTimestamp)var1;
         return this.dateValue == var2.dateValue && this.timeNanos == var2.timeNanos;
      }
   }

   public int hashCode() {
      return (int)(this.dateValue ^ this.dateValue >>> 32 ^ this.timeNanos ^ this.timeNanos >>> 32);
   }

   public Value add(Value var1) {
      ValueTimestamp var2 = (ValueTimestamp)var1;
      long var3 = DateTimeUtils.absoluteDayFromDateValue(this.dateValue) + DateTimeUtils.absoluteDayFromDateValue(var2.dateValue);
      long var5 = this.timeNanos + var2.timeNanos;
      if (var5 >= 86400000000000L) {
         var5 -= 86400000000000L;
         ++var3;
      }

      return fromDateValueAndNanos(DateTimeUtils.dateValueFromAbsoluteDay(var3), var5);
   }

   public Value subtract(Value var1) {
      ValueTimestamp var2 = (ValueTimestamp)var1;
      long var3 = DateTimeUtils.absoluteDayFromDateValue(this.dateValue) - DateTimeUtils.absoluteDayFromDateValue(var2.dateValue);
      long var5 = this.timeNanos - var2.timeNanos;
      if (var5 < 0L) {
         var5 += 86400000000000L;
         --var3;
      }

      return fromDateValueAndNanos(DateTimeUtils.dateValueFromAbsoluteDay(var3), var5);
   }
}
