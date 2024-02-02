package org.h2.value;

import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;
import org.h2.util.DateTimeUtils;

public final class ValueTimestampTimeZone extends Value {
   public static final int DEFAULT_PRECISION = 32;
   public static final int MAXIMUM_PRECISION = 35;
   private final long dateValue;
   private final long timeNanos;
   private final int timeZoneOffsetSeconds;

   private ValueTimestampTimeZone(long var1, long var3, int var5) {
      if (var1 >= -511999999967L && var1 <= 512000000415L) {
         if (var3 >= 0L && var3 < 86400000000000L) {
            if (var5 >= -64800 && var5 <= 64800) {
               this.dateValue = var1;
               this.timeNanos = var3;
               this.timeZoneOffsetSeconds = var5;
            } else {
               throw new IllegalArgumentException("timeZoneOffsetSeconds out of range " + var5);
            }
         } else {
            throw new IllegalArgumentException("timeNanos out of range " + var3);
         }
      } else {
         throw new IllegalArgumentException("dateValue out of range " + var1);
      }
   }

   public static ValueTimestampTimeZone fromDateValueAndNanos(long var0, long var2, int var4) {
      return (ValueTimestampTimeZone)Value.cache(new ValueTimestampTimeZone(var0, var2, var4));
   }

   public static ValueTimestampTimeZone parse(String var0, CastDataProvider var1) {
      try {
         return (ValueTimestampTimeZone)DateTimeUtils.parseTimestamp(var0, var1, true);
      } catch (Exception var3) {
         throw DbException.get(22007, var3, "TIMESTAMP WITH TIME ZONE", var0);
      }
   }

   public long getDateValue() {
      return this.dateValue;
   }

   public long getTimeNanos() {
      return this.timeNanos;
   }

   public int getTimeZoneOffsetSeconds() {
      return this.timeZoneOffsetSeconds;
   }

   public TypeInfo getType() {
      return TypeInfo.TYPE_TIMESTAMP_TZ;
   }

   public int getValueType() {
      return 21;
   }

   public int getMemory() {
      return 40;
   }

   public String getString() {
      return this.toString(new StringBuilder(35), false).toString();
   }

   public String getISOString() {
      return this.toString(new StringBuilder(35), true).toString();
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      return this.toString(var1.append("TIMESTAMP WITH TIME ZONE '"), false).append('\'');
   }

   private StringBuilder toString(StringBuilder var1, boolean var2) {
      DateTimeUtils.appendDate(var1, this.dateValue).append((char)(var2 ? 'T' : ' '));
      DateTimeUtils.appendTime(var1, this.timeNanos);
      return DateTimeUtils.appendTimeZone(var1, this.timeZoneOffsetSeconds);
   }

   public int compareTypeSafe(Value var1, CompareMode var2, CastDataProvider var3) {
      ValueTimestampTimeZone var4 = (ValueTimestampTimeZone)var1;
      long var5 = this.dateValue;
      long var7 = this.timeNanos - (long)this.timeZoneOffsetSeconds * 1000000000L;
      if (var7 < 0L) {
         var7 += 86400000000000L;
         var5 = DateTimeUtils.decrementDateValue(var5);
      } else if (var7 >= 86400000000000L) {
         var7 -= 86400000000000L;
         var5 = DateTimeUtils.incrementDateValue(var5);
      }

      long var9 = var4.dateValue;
      long var11 = var4.timeNanos - (long)var4.timeZoneOffsetSeconds * 1000000000L;
      if (var11 < 0L) {
         var11 += 86400000000000L;
         var9 = DateTimeUtils.decrementDateValue(var9);
      } else if (var11 >= 86400000000000L) {
         var11 -= 86400000000000L;
         var9 = DateTimeUtils.incrementDateValue(var9);
      }

      int var13 = Long.compare(var5, var9);
      return var13 != 0 ? var13 : Long.compare(var7, var11);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ValueTimestampTimeZone)) {
         return false;
      } else {
         ValueTimestampTimeZone var2 = (ValueTimestampTimeZone)var1;
         return this.dateValue == var2.dateValue && this.timeNanos == var2.timeNanos && this.timeZoneOffsetSeconds == var2.timeZoneOffsetSeconds;
      }
   }

   public int hashCode() {
      return (int)(this.dateValue ^ this.dateValue >>> 32 ^ this.timeNanos ^ this.timeNanos >>> 32 ^ (long)this.timeZoneOffsetSeconds);
   }
}
