package org.h2.util;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.h2.engine.CastDataProvider;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueDate;
import org.h2.value.ValueNull;
import org.h2.value.ValueTime;
import org.h2.value.ValueTimestamp;
import org.h2.value.ValueTimestampTimeZone;

public final class LegacyDateTimeUtils {
   public static final Date PROLEPTIC_GREGORIAN_CHANGE = new Date(Long.MIN_VALUE);
   public static final TimeZone UTC = TimeZone.getTimeZone("UTC");

   private LegacyDateTimeUtils() {
   }

   public static ValueDate fromDate(CastDataProvider var0, TimeZone var1, Date var2) {
      long var3 = var2.getTime();
      return ValueDate.fromDateValue(dateValueFromLocalMillis(var3 + (long)(var1 == null ? getTimeZoneOffsetMillis(var0, var3) : var1.getOffset(var3))));
   }

   public static ValueTime fromTime(CastDataProvider var0, TimeZone var1, Time var2) {
      long var3 = var2.getTime();
      return ValueTime.fromNanos(nanosFromLocalMillis(var3 + (long)(var1 == null ? getTimeZoneOffsetMillis(var0, var3) : var1.getOffset(var3))));
   }

   public static ValueTimestamp fromTimestamp(CastDataProvider var0, TimeZone var1, Timestamp var2) {
      long var3 = var2.getTime();
      return timestampFromLocalMillis(var3 + (long)(var1 == null ? getTimeZoneOffsetMillis(var0, var3) : var1.getOffset(var3)), var2.getNanos() % 1000000);
   }

   public static ValueTimestamp fromTimestamp(CastDataProvider var0, long var1, int var3) {
      return timestampFromLocalMillis(var1 + (long)getTimeZoneOffsetMillis(var0, var1), var3);
   }

   private static ValueTimestamp timestampFromLocalMillis(long var0, int var2) {
      long var3 = dateValueFromLocalMillis(var0);
      long var5 = (long)var2 + nanosFromLocalMillis(var0);
      return ValueTimestamp.fromDateValueAndNanos(var3, var5);
   }

   public static long dateValueFromLocalMillis(long var0) {
      long var2 = var0 / 86400000L;
      if (var0 < 0L && var2 * 86400000L != var0) {
         --var2;
      }

      return DateTimeUtils.dateValueFromAbsoluteDay(var2);
   }

   public static long nanosFromLocalMillis(long var0) {
      var0 %= 86400000L;
      if (var0 < 0L) {
         var0 += 86400000L;
      }

      return var0 * 1000000L;
   }

   public static Date toDate(CastDataProvider var0, TimeZone var1, Value var2) {
      return var2 != ValueNull.INSTANCE ? new Date(getMillis(var0, var1, var2.convertToDate(var0).getDateValue(), 0L)) : null;
   }

   public static Time toTime(CastDataProvider var0, TimeZone var1, Value var2) {
      switch (var2.getValueType()) {
         case 0:
            return null;
         default:
            var2 = var2.convertTo(TypeInfo.TYPE_TIME, var0);
         case 18:
            return new Time(getMillis(var0, var1, 1008673L, ((ValueTime)var2).getNanos()));
      }
   }

   public static Timestamp toTimestamp(CastDataProvider var0, TimeZone var1, Value var2) {
      long var4;
      Timestamp var6;
      switch (var2.getValueType()) {
         case 0:
            return null;
         case 21:
            ValueTimestampTimeZone var3 = (ValueTimestampTimeZone)var2;
            var4 = var3.getTimeNanos();
            var6 = new Timestamp(DateTimeUtils.absoluteDayFromDateValue(var3.getDateValue()) * 86400000L + var4 / 1000000L - (long)(var3.getTimeZoneOffsetSeconds() * 1000));
            var6.setNanos((int)(var4 % 1000000000L));
            return var6;
         default:
            var2 = var2.convertTo(TypeInfo.TYPE_TIMESTAMP, var0);
         case 20:
            ValueTimestamp var7 = (ValueTimestamp)var2;
            var4 = var7.getTimeNanos();
            var6 = new Timestamp(getMillis(var0, var1, var7.getDateValue(), var4));
            var6.setNanos((int)(var4 % 1000000000L));
            return var6;
      }
   }

   public static long getMillis(CastDataProvider var0, TimeZone var1, long var2, long var4) {
      return (var1 == null ? (var0 != null ? var0.currentTimeZone() : DateTimeUtils.getTimeZone()) : TimeZoneProvider.ofId(var1.getID())).getEpochSecondsFromLocal(var2, var4) * 1000L + var4 / 1000000L % 1000L;
   }

   public static int getTimeZoneOffsetMillis(CastDataProvider var0, long var1) {
      long var3 = var1 / 1000L;
      if (var1 < 0L && var3 * 1000L != var1) {
         --var3;
      }

      return (var0 != null ? var0.currentTimeZone() : DateTimeUtils.getTimeZone()).getTimeZoneOffsetUTC(var3) * 1000;
   }

   public static Value legacyObjectToValue(CastDataProvider var0, Object var1) {
      if (var1 instanceof Date) {
         return fromDate(var0, (TimeZone)null, (Date)var1);
      } else if (var1 instanceof Time) {
         return fromTime(var0, (TimeZone)null, (Time)var1);
      } else if (var1 instanceof Timestamp) {
         return fromTimestamp(var0, (TimeZone)null, (Timestamp)var1);
      } else if (var1 instanceof java.util.Date) {
         return fromTimestamp(var0, ((java.util.Date)var1).getTime(), 0);
      } else if (var1 instanceof Calendar) {
         Calendar var2 = (Calendar)var1;
         long var3 = var2.getTimeInMillis();
         return timestampFromLocalMillis(var3 + (long)var2.getTimeZone().getOffset(var3), 0);
      } else {
         return null;
      }
   }

   public static <T> T valueToLegacyType(Class<T> var0, Value var1, CastDataProvider var2) {
      if (var0 == Date.class) {
         return toDate(var2, (TimeZone)null, var1);
      } else if (var0 == Time.class) {
         return toTime(var2, (TimeZone)null, var1);
      } else if (var0 == Timestamp.class) {
         return toTimestamp(var2, (TimeZone)null, var1);
      } else if (var0 == java.util.Date.class) {
         return new java.util.Date(toTimestamp(var2, (TimeZone)null, var1).getTime());
      } else if (var0 == Calendar.class) {
         GregorianCalendar var3 = new GregorianCalendar();
         var3.setGregorianChange(PROLEPTIC_GREGORIAN_CHANGE);
         var3.setTime(toTimestamp(var2, var3.getTimeZone(), var1));
         return var3;
      } else {
         return null;
      }
   }

   public static TypeInfo legacyClassToType(Class<?> var0) {
      if (Date.class.isAssignableFrom(var0)) {
         return TypeInfo.TYPE_DATE;
      } else if (Time.class.isAssignableFrom(var0)) {
         return TypeInfo.TYPE_TIME;
      } else {
         return !java.util.Date.class.isAssignableFrom(var0) && !Calendar.class.isAssignableFrom(var0) ? null : TypeInfo.TYPE_TIMESTAMP;
      }
   }
}
