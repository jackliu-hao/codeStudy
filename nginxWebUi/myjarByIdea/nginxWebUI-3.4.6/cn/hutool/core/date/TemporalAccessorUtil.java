package cn.hutool.core.date;

import cn.hutool.core.date.format.GlobalCustomFormat;
import cn.hutool.core.util.StrUtil;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.UnsupportedTemporalTypeException;

public class TemporalAccessorUtil extends TemporalUtil {
   public static int get(TemporalAccessor temporalAccessor, TemporalField field) {
      return temporalAccessor.isSupported(field) ? temporalAccessor.get(field) : (int)field.range().getMinimum();
   }

   public static String format(TemporalAccessor time, DateTimeFormatter formatter) {
      if (null == time) {
         return null;
      } else if (time instanceof java.time.Month) {
         return time.toString();
      } else {
         if (null == formatter) {
            formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
         }

         try {
            return formatter.format(time);
         } catch (UnsupportedTemporalTypeException var3) {
            if (time instanceof LocalDate && var3.getMessage().contains("HourOfDay")) {
               return formatter.format(((LocalDate)time).atStartOfDay());
            } else if (time instanceof LocalTime && var3.getMessage().contains("YearOfEra")) {
               return formatter.format(((LocalTime)time).atDate(LocalDate.now()));
            } else if (time instanceof Instant) {
               return formatter.format(((Instant)time).atZone(ZoneId.systemDefault()));
            } else {
               throw var3;
            }
         }
      }
   }

   public static String format(TemporalAccessor time, String format) {
      if (null == time) {
         return null;
      } else if (time instanceof java.time.Month) {
         return time.toString();
      } else if (GlobalCustomFormat.isCustomFormat(format)) {
         return GlobalCustomFormat.format((TemporalAccessor)time, format);
      } else {
         DateTimeFormatter formatter = StrUtil.isBlank(format) ? null : DateTimeFormatter.ofPattern(format);
         return format(time, formatter);
      }
   }

   public static long toEpochMilli(TemporalAccessor temporalAccessor) {
      return temporalAccessor instanceof java.time.Month ? (long)((java.time.Month)temporalAccessor).getValue() : toInstant(temporalAccessor).toEpochMilli();
   }

   public static Instant toInstant(TemporalAccessor temporalAccessor) {
      if (null == temporalAccessor) {
         return null;
      } else {
         Instant result;
         if (temporalAccessor instanceof Instant) {
            result = (Instant)temporalAccessor;
         } else if (temporalAccessor instanceof LocalDateTime) {
            result = ((LocalDateTime)temporalAccessor).atZone(ZoneId.systemDefault()).toInstant();
         } else if (temporalAccessor instanceof ZonedDateTime) {
            result = ((ZonedDateTime)temporalAccessor).toInstant();
         } else if (temporalAccessor instanceof OffsetDateTime) {
            result = ((OffsetDateTime)temporalAccessor).toInstant();
         } else if (temporalAccessor instanceof LocalDate) {
            result = ((LocalDate)temporalAccessor).atStartOfDay(ZoneId.systemDefault()).toInstant();
         } else if (temporalAccessor instanceof LocalTime) {
            result = ((LocalTime)temporalAccessor).atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant();
         } else if (temporalAccessor instanceof OffsetTime) {
            result = ((OffsetTime)temporalAccessor).atDate(LocalDate.now()).toInstant();
         } else {
            result = toInstant(LocalDateTimeUtil.of(temporalAccessor));
         }

         return result;
      }
   }
}
