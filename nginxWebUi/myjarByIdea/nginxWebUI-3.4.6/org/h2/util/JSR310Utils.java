package org.h2.util;

import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.h2.api.IntervalQualifier;
import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;
import org.h2.value.DataType;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueDate;
import org.h2.value.ValueInterval;
import org.h2.value.ValueTime;
import org.h2.value.ValueTimeTimeZone;
import org.h2.value.ValueTimestamp;
import org.h2.value.ValueTimestampTimeZone;

public class JSR310Utils {
   private static final long MIN_DATE_VALUE = -511999999455L;
   private static final long MAX_DATE_VALUE = 511999999903L;
   private static final long MIN_INSTANT_SECOND = -31557014167219200L;
   private static final long MAX_INSTANT_SECOND = 31556889864403199L;

   private JSR310Utils() {
   }

   public static LocalDate valueToLocalDate(Value var0, CastDataProvider var1) {
      long var2 = var0.convertToDate(var1).getDateValue();
      if (var2 > 511999999903L) {
         return LocalDate.MAX;
      } else {
         return var2 < -511999999455L ? LocalDate.MIN : LocalDate.of(DateTimeUtils.yearFromDateValue(var2), DateTimeUtils.monthFromDateValue(var2), DateTimeUtils.dayFromDateValue(var2));
      }
   }

   public static LocalTime valueToLocalTime(Value var0, CastDataProvider var1) {
      return LocalTime.ofNanoOfDay(((ValueTime)var0.convertTo(TypeInfo.TYPE_TIME, var1)).getNanos());
   }

   public static LocalDateTime valueToLocalDateTime(Value var0, CastDataProvider var1) {
      ValueTimestamp var2 = (ValueTimestamp)var0.convertTo(TypeInfo.TYPE_TIMESTAMP, var1);
      return localDateTimeFromDateNanos(var2.getDateValue(), var2.getTimeNanos());
   }

   public static Instant valueToInstant(Value var0, CastDataProvider var1) {
      ValueTimestampTimeZone var2 = (ValueTimestampTimeZone)var0.convertTo(TypeInfo.TYPE_TIMESTAMP_TZ, var1);
      long var3 = var2.getTimeNanos();
      long var5 = DateTimeUtils.absoluteDayFromDateValue(var2.getDateValue()) * 86400L + var3 / 1000000000L - (long)var2.getTimeZoneOffsetSeconds();
      if (var5 > 31556889864403199L) {
         return Instant.MAX;
      } else {
         return var5 < -31557014167219200L ? Instant.MIN : Instant.ofEpochSecond(var5, var3 % 1000000000L);
      }
   }

   public static OffsetDateTime valueToOffsetDateTime(Value var0, CastDataProvider var1) {
      ValueTimestampTimeZone var2 = (ValueTimestampTimeZone)var0.convertTo(TypeInfo.TYPE_TIMESTAMP_TZ, var1);
      return OffsetDateTime.of(localDateTimeFromDateNanos(var2.getDateValue(), var2.getTimeNanos()), ZoneOffset.ofTotalSeconds(var2.getTimeZoneOffsetSeconds()));
   }

   public static ZonedDateTime valueToZonedDateTime(Value var0, CastDataProvider var1) {
      ValueTimestampTimeZone var2 = (ValueTimestampTimeZone)var0.convertTo(TypeInfo.TYPE_TIMESTAMP_TZ, var1);
      return ZonedDateTime.of(localDateTimeFromDateNanos(var2.getDateValue(), var2.getTimeNanos()), ZoneOffset.ofTotalSeconds(var2.getTimeZoneOffsetSeconds()));
   }

   public static OffsetTime valueToOffsetTime(Value var0, CastDataProvider var1) {
      ValueTimeTimeZone var2 = (ValueTimeTimeZone)var0.convertTo(TypeInfo.TYPE_TIME_TZ, var1);
      return OffsetTime.of(LocalTime.ofNanoOfDay(var2.getNanos()), ZoneOffset.ofTotalSeconds(var2.getTimeZoneOffsetSeconds()));
   }

   public static Period valueToPeriod(Value var0) {
      if (!(var0 instanceof ValueInterval)) {
         var0 = var0.convertTo(TypeInfo.TYPE_INTERVAL_YEAR_TO_MONTH);
      }

      if (!DataType.isYearMonthIntervalType(var0.getValueType())) {
         throw DbException.get(22018, (Throwable)null, var0.getString());
      } else {
         ValueInterval var1 = (ValueInterval)var0;
         IntervalQualifier var2 = var1.getQualifier();
         boolean var3 = var1.isNegative();
         long var4 = var1.getLeading();
         long var6 = var1.getRemaining();
         int var8 = Value.convertToInt(IntervalUtils.yearsFromInterval(var2, var3, var4, var6), (Object)null);
         int var9 = Value.convertToInt(IntervalUtils.monthsFromInterval(var2, var3, var4, var6), (Object)null);
         return Period.of(var8, var9, 0);
      }
   }

   public static Duration valueToDuration(Value var0) {
      if (!(var0 instanceof ValueInterval)) {
         var0 = var0.convertTo(TypeInfo.TYPE_INTERVAL_DAY_TO_SECOND);
      }

      if (DataType.isYearMonthIntervalType(var0.getValueType())) {
         throw DbException.get(22018, (Throwable)null, var0.getString());
      } else {
         BigInteger[] var1 = IntervalUtils.intervalToAbsolute((ValueInterval)var0).divideAndRemainder(BigInteger.valueOf(1000000000L));
         return Duration.ofSeconds(var1[0].longValue(), var1[1].longValue());
      }
   }

   public static ValueDate localDateToValue(LocalDate var0) {
      return ValueDate.fromDateValue(DateTimeUtils.dateValue((long)var0.getYear(), var0.getMonthValue(), var0.getDayOfMonth()));
   }

   public static ValueTime localTimeToValue(LocalTime var0) {
      return ValueTime.fromNanos(var0.toNanoOfDay());
   }

   public static ValueTimestamp localDateTimeToValue(LocalDateTime var0) {
      LocalDate var1 = var0.toLocalDate();
      return ValueTimestamp.fromDateValueAndNanos(DateTimeUtils.dateValue((long)var1.getYear(), var1.getMonthValue(), var1.getDayOfMonth()), var0.toLocalTime().toNanoOfDay());
   }

   public static ValueTimestampTimeZone instantToValue(Instant var0) {
      long var1 = var0.getEpochSecond();
      int var3 = var0.getNano();
      long var4 = var1 / 86400L;
      if (var1 < 0L && var4 * 86400L != var1) {
         --var4;
      }

      long var6 = (var1 - var4 * 86400L) * 1000000000L + (long)var3;
      return ValueTimestampTimeZone.fromDateValueAndNanos(DateTimeUtils.dateValueFromAbsoluteDay(var4), var6, 0);
   }

   public static ValueTimestampTimeZone offsetDateTimeToValue(OffsetDateTime var0) {
      LocalDateTime var1 = var0.toLocalDateTime();
      LocalDate var2 = var1.toLocalDate();
      return ValueTimestampTimeZone.fromDateValueAndNanos(DateTimeUtils.dateValue((long)var2.getYear(), var2.getMonthValue(), var2.getDayOfMonth()), var1.toLocalTime().toNanoOfDay(), var0.getOffset().getTotalSeconds());
   }

   public static ValueTimestampTimeZone zonedDateTimeToValue(ZonedDateTime var0) {
      LocalDateTime var1 = var0.toLocalDateTime();
      LocalDate var2 = var1.toLocalDate();
      return ValueTimestampTimeZone.fromDateValueAndNanos(DateTimeUtils.dateValue((long)var2.getYear(), var2.getMonthValue(), var2.getDayOfMonth()), var1.toLocalTime().toNanoOfDay(), var0.getOffset().getTotalSeconds());
   }

   public static ValueTimeTimeZone offsetTimeToValue(OffsetTime var0) {
      return ValueTimeTimeZone.fromNanos(var0.toLocalTime().toNanoOfDay(), var0.getOffset().getTotalSeconds());
   }

   private static LocalDateTime localDateTimeFromDateNanos(long var0, long var2) {
      if (var0 > 511999999903L) {
         return LocalDateTime.MAX;
      } else {
         return var0 < -511999999455L ? LocalDateTime.MIN : LocalDateTime.of(LocalDate.of(DateTimeUtils.yearFromDateValue(var0), DateTimeUtils.monthFromDateValue(var0), DateTimeUtils.dayFromDateValue(var0)), LocalTime.ofNanoOfDay(var2));
      }
   }

   public static ValueInterval periodToValue(Period var0) {
      int var1 = var0.getDays();
      if (var1 != 0) {
         throw DbException.getInvalidValueException("Period.days", var1);
      } else {
         int var2 = var0.getYears();
         int var3 = var0.getMonths();
         boolean var5 = false;
         long var6 = 0L;
         long var8 = 0L;
         IntervalQualifier var4;
         if (var2 == 0) {
            if ((long)var3 == 0L) {
               var4 = IntervalQualifier.YEAR_TO_MONTH;
            } else {
               var4 = IntervalQualifier.MONTH;
               var6 = (long)var3;
               if (var6 < 0L) {
                  var6 = -var6;
                  var5 = true;
               }
            }
         } else if ((long)var3 == 0L) {
            var4 = IntervalQualifier.YEAR;
            var6 = (long)var2;
            if (var6 < 0L) {
               var6 = -var6;
               var5 = true;
            }
         } else {
            var4 = IntervalQualifier.YEAR_TO_MONTH;
            var6 = (long)(var2 * 12 + var3);
            if (var6 < 0L) {
               var6 = -var6;
               var5 = true;
            }

            var8 = var6 % 12L;
            var6 /= 12L;
         }

         return ValueInterval.from(var4, var5, var6, var8);
      }
   }

   public static ValueInterval durationToValue(Duration var0) {
      long var1 = var0.getSeconds();
      int var3 = var0.getNano();
      boolean var4 = var1 < 0L;
      var1 = Math.abs(var1);
      if (var4 && var3 != 0) {
         var3 = 1000000000 - var3;
         --var1;
      }

      return ValueInterval.from(IntervalQualifier.SECOND, var4, var1, (long)var3);
   }
}
