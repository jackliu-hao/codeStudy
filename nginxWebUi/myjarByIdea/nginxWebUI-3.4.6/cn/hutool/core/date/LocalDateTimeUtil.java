package cn.hutool.core.date;

import cn.hutool.core.date.format.GlobalCustomFormat;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalUnit;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.TimeZone;
import java.util.function.Supplier;

public class LocalDateTimeUtil {
   public static LocalDateTime now() {
      return LocalDateTime.now();
   }

   public static LocalDateTime of(Instant instant) {
      return of(instant, ZoneId.systemDefault());
   }

   public static LocalDateTime ofUTC(Instant instant) {
      return of(instant, ZoneId.of("UTC"));
   }

   public static LocalDateTime of(ZonedDateTime zonedDateTime) {
      return null == zonedDateTime ? null : zonedDateTime.toLocalDateTime();
   }

   public static LocalDateTime of(Instant instant, ZoneId zoneId) {
      return null == instant ? null : LocalDateTime.ofInstant(instant, (ZoneId)ObjectUtil.defaultIfNull(zoneId, (Supplier)(ZoneId::systemDefault)));
   }

   public static LocalDateTime of(Instant instant, TimeZone timeZone) {
      return null == instant ? null : of(instant, ((TimeZone)ObjectUtil.defaultIfNull(timeZone, (Supplier)(TimeZone::getDefault))).toZoneId());
   }

   public static LocalDateTime of(long epochMilli) {
      return of(Instant.ofEpochMilli(epochMilli));
   }

   public static LocalDateTime ofUTC(long epochMilli) {
      return ofUTC(Instant.ofEpochMilli(epochMilli));
   }

   public static LocalDateTime of(long epochMilli, ZoneId zoneId) {
      return of(Instant.ofEpochMilli(epochMilli), zoneId);
   }

   public static LocalDateTime of(long epochMilli, TimeZone timeZone) {
      return of(Instant.ofEpochMilli(epochMilli), timeZone);
   }

   public static LocalDateTime of(Date date) {
      if (null == date) {
         return null;
      } else {
         return date instanceof DateTime ? of(date.toInstant(), ((DateTime)date).getZoneId()) : of(date.toInstant());
      }
   }

   public static LocalDateTime of(TemporalAccessor temporalAccessor) {
      if (null == temporalAccessor) {
         return null;
      } else if (temporalAccessor instanceof LocalDate) {
         return ((LocalDate)temporalAccessor).atStartOfDay();
      } else {
         return temporalAccessor instanceof Instant ? LocalDateTime.ofInstant((Instant)temporalAccessor, ZoneId.systemDefault()) : LocalDateTime.of(TemporalAccessorUtil.get(temporalAccessor, ChronoField.YEAR), TemporalAccessorUtil.get(temporalAccessor, ChronoField.MONTH_OF_YEAR), TemporalAccessorUtil.get(temporalAccessor, ChronoField.DAY_OF_MONTH), TemporalAccessorUtil.get(temporalAccessor, ChronoField.HOUR_OF_DAY), TemporalAccessorUtil.get(temporalAccessor, ChronoField.MINUTE_OF_HOUR), TemporalAccessorUtil.get(temporalAccessor, ChronoField.SECOND_OF_MINUTE), TemporalAccessorUtil.get(temporalAccessor, ChronoField.NANO_OF_SECOND));
      }
   }

   public static LocalDate ofDate(TemporalAccessor temporalAccessor) {
      if (null == temporalAccessor) {
         return null;
      } else if (temporalAccessor instanceof LocalDateTime) {
         return ((LocalDateTime)temporalAccessor).toLocalDate();
      } else {
         return temporalAccessor instanceof Instant ? of(temporalAccessor).toLocalDate() : LocalDate.of(TemporalAccessorUtil.get(temporalAccessor, ChronoField.YEAR), TemporalAccessorUtil.get(temporalAccessor, ChronoField.MONTH_OF_YEAR), TemporalAccessorUtil.get(temporalAccessor, ChronoField.DAY_OF_MONTH));
      }
   }

   public static LocalDateTime parse(CharSequence text) {
      return parse(text, (DateTimeFormatter)null);
   }

   public static LocalDateTime parse(CharSequence text, DateTimeFormatter formatter) {
      if (null == text) {
         return null;
      } else {
         return null == formatter ? LocalDateTime.parse(text) : of(formatter.parse(text));
      }
   }

   public static LocalDateTime parse(CharSequence text, String format) {
      if (null == text) {
         return null;
      } else if (GlobalCustomFormat.isCustomFormat(format)) {
         return of(GlobalCustomFormat.parse((CharSequence)text, format));
      } else {
         DateTimeFormatter formatter = null;
         if (StrUtil.isNotBlank(format)) {
            if (StrUtil.startWithIgnoreEquals(format, "yyyyMMddHHmmss")) {
               String fraction = StrUtil.removePrefix(format, "yyyyMMddHHmmss");
               if (ReUtil.isMatch((String)"[S]{1,2}", fraction)) {
                  text = text + StrUtil.repeat('0', 3 - fraction.length());
               }

               formatter = (new DateTimeFormatterBuilder()).appendPattern("yyyyMMddHHmmss").appendValue(ChronoField.MILLI_OF_SECOND, 3).toFormatter();
            } else {
               formatter = DateTimeFormatter.ofPattern(format);
            }
         }

         return parse((CharSequence)text, (DateTimeFormatter)formatter);
      }
   }

   public static LocalDate parseDate(CharSequence text) {
      return parseDate(text, (DateTimeFormatter)null);
   }

   public static LocalDate parseDate(CharSequence text, DateTimeFormatter formatter) {
      if (null == text) {
         return null;
      } else {
         return null == formatter ? LocalDate.parse(text) : ofDate(formatter.parse(text));
      }
   }

   public static LocalDate parseDate(CharSequence text, String format) {
      return null == text ? null : parseDate(text, DateTimeFormatter.ofPattern(format));
   }

   public static String formatNormal(LocalDateTime time) {
      return format(time, DatePattern.NORM_DATETIME_FORMATTER);
   }

   public static String format(LocalDateTime time, DateTimeFormatter formatter) {
      return TemporalAccessorUtil.format(time, (DateTimeFormatter)formatter);
   }

   public static String format(LocalDateTime time, String format) {
      return TemporalAccessorUtil.format(time, (String)format);
   }

   public static String formatNormal(LocalDate date) {
      return format(date, DatePattern.NORM_DATE_FORMATTER);
   }

   public static String format(LocalDate date, DateTimeFormatter formatter) {
      return TemporalAccessorUtil.format(date, (DateTimeFormatter)formatter);
   }

   public static String format(LocalDate date, String format) {
      return null == date ? null : format(date, DateTimeFormatter.ofPattern(format));
   }

   public static LocalDateTime offset(LocalDateTime time, long number, TemporalUnit field) {
      return (LocalDateTime)TemporalUtil.offset(time, number, field);
   }

   public static Duration between(LocalDateTime startTimeInclude, LocalDateTime endTimeExclude) {
      return TemporalUtil.between(startTimeInclude, endTimeExclude);
   }

   public static long between(LocalDateTime startTimeInclude, LocalDateTime endTimeExclude, ChronoUnit unit) {
      return TemporalUtil.between(startTimeInclude, endTimeExclude, unit);
   }

   public static Period betweenPeriod(LocalDate startTimeInclude, LocalDate endTimeExclude) {
      return Period.between(startTimeInclude, endTimeExclude);
   }

   public static LocalDateTime beginOfDay(LocalDateTime time) {
      return time.with(LocalTime.MIN);
   }

   public static LocalDateTime endOfDay(LocalDateTime time) {
      return endOfDay(time, false);
   }

   public static LocalDateTime endOfDay(LocalDateTime time, boolean truncateMillisecond) {
      return truncateMillisecond ? time.with(LocalTime.of(23, 59, 59)) : time.with(LocalTime.MAX);
   }

   public static long toEpochMilli(TemporalAccessor temporalAccessor) {
      return TemporalAccessorUtil.toEpochMilli(temporalAccessor);
   }

   public static boolean isWeekend(LocalDateTime localDateTime) {
      return isWeekend(localDateTime.toLocalDate());
   }

   public static boolean isWeekend(LocalDate localDate) {
      DayOfWeek dayOfWeek = localDate.getDayOfWeek();
      return DayOfWeek.SATURDAY == dayOfWeek || DayOfWeek.SUNDAY == dayOfWeek;
   }

   public static Week dayOfWeek(LocalDate localDate) {
      return Week.of(localDate.getDayOfWeek());
   }

   public static boolean isOverlap(ChronoLocalDateTime<?> realStartTime, ChronoLocalDateTime<?> realEndTime, ChronoLocalDateTime<?> startTime, ChronoLocalDateTime<?> endTime) {
      return startTime.isBefore(realEndTime) && endTime.isAfter(realStartTime);
   }

   public static int weekOfYear(TemporalAccessor date) {
      return TemporalAccessorUtil.get(date, WeekFields.ISO.weekOfYear());
   }
}
