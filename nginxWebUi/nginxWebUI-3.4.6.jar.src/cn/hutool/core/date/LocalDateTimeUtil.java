/*     */ package cn.hutool.core.date;
/*     */ 
/*     */ import cn.hutool.core.date.format.GlobalCustomFormat;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import cn.hutool.core.util.ReUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.time.DayOfWeek;
/*     */ import java.time.Duration;
/*     */ import java.time.Instant;
/*     */ import java.time.LocalDate;
/*     */ import java.time.LocalDateTime;
/*     */ import java.time.LocalTime;
/*     */ import java.time.Period;
/*     */ import java.time.ZoneId;
/*     */ import java.time.ZonedDateTime;
/*     */ import java.time.chrono.ChronoLocalDateTime;
/*     */ import java.time.format.DateTimeFormatter;
/*     */ import java.time.format.DateTimeFormatterBuilder;
/*     */ import java.time.temporal.ChronoField;
/*     */ import java.time.temporal.ChronoUnit;
/*     */ import java.time.temporal.TemporalAccessor;
/*     */ import java.time.temporal.TemporalUnit;
/*     */ import java.time.temporal.WeekFields;
/*     */ import java.util.Date;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LocalDateTimeUtil
/*     */ {
/*     */   public static LocalDateTime now() {
/*  32 */     return LocalDateTime.now();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static LocalDateTime of(Instant instant) {
/*  42 */     return of(instant, ZoneId.systemDefault());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static LocalDateTime ofUTC(Instant instant) {
/*  52 */     return of(instant, ZoneId.of("UTC"));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static LocalDateTime of(ZonedDateTime zonedDateTime) {
/*  62 */     if (null == zonedDateTime) {
/*  63 */       return null;
/*     */     }
/*  65 */     return zonedDateTime.toLocalDateTime();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static LocalDateTime of(Instant instant, ZoneId zoneId) {
/*  76 */     if (null == instant) {
/*  77 */       return null;
/*     */     }
/*     */     
/*  80 */     return LocalDateTime.ofInstant(instant, (ZoneId)ObjectUtil.defaultIfNull(zoneId, ZoneId::systemDefault));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static LocalDateTime of(Instant instant, TimeZone timeZone) {
/*  91 */     if (null == instant) {
/*  92 */       return null;
/*     */     }
/*     */     
/*  95 */     return of(instant, ((TimeZone)ObjectUtil.defaultIfNull(timeZone, TimeZone::getDefault)).toZoneId());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static LocalDateTime of(long epochMilli) {
/* 107 */     return of(Instant.ofEpochMilli(epochMilli));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static LocalDateTime ofUTC(long epochMilli) {
/* 117 */     return ofUTC(Instant.ofEpochMilli(epochMilli));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static LocalDateTime of(long epochMilli, ZoneId zoneId) {
/* 128 */     return of(Instant.ofEpochMilli(epochMilli), zoneId);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static LocalDateTime of(long epochMilli, TimeZone timeZone) {
/* 139 */     return of(Instant.ofEpochMilli(epochMilli), timeZone);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static LocalDateTime of(Date date) {
/* 149 */     if (null == date) {
/* 150 */       return null;
/*     */     }
/*     */     
/* 153 */     if (date instanceof DateTime) {
/* 154 */       return of(date.toInstant(), ((DateTime)date).getZoneId());
/*     */     }
/* 156 */     return of(date.toInstant());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static LocalDateTime of(TemporalAccessor temporalAccessor) {
/* 166 */     if (null == temporalAccessor) {
/* 167 */       return null;
/*     */     }
/*     */     
/* 170 */     if (temporalAccessor instanceof LocalDate)
/* 171 */       return ((LocalDate)temporalAccessor).atStartOfDay(); 
/* 172 */     if (temporalAccessor instanceof Instant) {
/* 173 */       return LocalDateTime.ofInstant((Instant)temporalAccessor, ZoneId.systemDefault());
/*     */     }
/*     */     
/* 176 */     return LocalDateTime.of(
/* 177 */         TemporalAccessorUtil.get(temporalAccessor, ChronoField.YEAR), 
/* 178 */         TemporalAccessorUtil.get(temporalAccessor, ChronoField.MONTH_OF_YEAR), 
/* 179 */         TemporalAccessorUtil.get(temporalAccessor, ChronoField.DAY_OF_MONTH), 
/* 180 */         TemporalAccessorUtil.get(temporalAccessor, ChronoField.HOUR_OF_DAY), 
/* 181 */         TemporalAccessorUtil.get(temporalAccessor, ChronoField.MINUTE_OF_HOUR), 
/* 182 */         TemporalAccessorUtil.get(temporalAccessor, ChronoField.SECOND_OF_MINUTE), 
/* 183 */         TemporalAccessorUtil.get(temporalAccessor, ChronoField.NANO_OF_SECOND));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static LocalDate ofDate(TemporalAccessor temporalAccessor) {
/* 195 */     if (null == temporalAccessor) {
/* 196 */       return null;
/*     */     }
/*     */     
/* 199 */     if (temporalAccessor instanceof LocalDateTime)
/* 200 */       return ((LocalDateTime)temporalAccessor).toLocalDate(); 
/* 201 */     if (temporalAccessor instanceof Instant) {
/* 202 */       return of(temporalAccessor).toLocalDate();
/*     */     }
/*     */     
/* 205 */     return LocalDate.of(
/* 206 */         TemporalAccessorUtil.get(temporalAccessor, ChronoField.YEAR), 
/* 207 */         TemporalAccessorUtil.get(temporalAccessor, ChronoField.MONTH_OF_YEAR), 
/* 208 */         TemporalAccessorUtil.get(temporalAccessor, ChronoField.DAY_OF_MONTH));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static LocalDateTime parse(CharSequence text) {
/* 220 */     return parse(text, (DateTimeFormatter)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static LocalDateTime parse(CharSequence text, DateTimeFormatter formatter) {
/* 232 */     if (null == text) {
/* 233 */       return null;
/*     */     }
/* 235 */     if (null == formatter) {
/* 236 */       return LocalDateTime.parse(text);
/*     */     }
/*     */     
/* 239 */     return of(formatter.parse(text));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static LocalDateTime parse(CharSequence text, String format) {
/* 250 */     if (null == text) {
/* 251 */       return null;
/*     */     }
/*     */     
/* 254 */     if (GlobalCustomFormat.isCustomFormat(format)) {
/* 255 */       return of(GlobalCustomFormat.parse(text, format));
/*     */     }
/*     */     
/* 258 */     DateTimeFormatter formatter = null;
/* 259 */     if (StrUtil.isNotBlank(format))
/*     */     {
/*     */ 
/*     */ 
/*     */       
/* 264 */       if (StrUtil.startWithIgnoreEquals(format, "yyyyMMddHHmmss")) {
/* 265 */         String fraction = StrUtil.removePrefix(format, "yyyyMMddHHmmss");
/* 266 */         if (ReUtil.isMatch("[S]{1,2}", fraction))
/*     */         {
/* 268 */           text = text + StrUtil.repeat('0', 3 - fraction.length());
/*     */         }
/*     */ 
/*     */ 
/*     */         
/* 273 */         formatter = (new DateTimeFormatterBuilder()).appendPattern("yyyyMMddHHmmss").appendValue(ChronoField.MILLI_OF_SECOND, 3).toFormatter();
/*     */       } else {
/* 275 */         formatter = DateTimeFormatter.ofPattern(format);
/*     */       } 
/*     */     }
/*     */     
/* 279 */     return parse(text, formatter);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static LocalDate parseDate(CharSequence text) {
/* 290 */     return parseDate(text, (DateTimeFormatter)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static LocalDate parseDate(CharSequence text, DateTimeFormatter formatter) {
/* 302 */     if (null == text) {
/* 303 */       return null;
/*     */     }
/* 305 */     if (null == formatter) {
/* 306 */       return LocalDate.parse(text);
/*     */     }
/*     */     
/* 309 */     return ofDate(formatter.parse(text));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static LocalDate parseDate(CharSequence text, String format) {
/* 320 */     if (null == text) {
/* 321 */       return null;
/*     */     }
/* 323 */     return parseDate(text, DateTimeFormatter.ofPattern(format));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String formatNormal(LocalDateTime time) {
/* 334 */     return format(time, DatePattern.NORM_DATETIME_FORMATTER);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String format(LocalDateTime time, DateTimeFormatter formatter) {
/* 345 */     return TemporalAccessorUtil.format(time, formatter);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String format(LocalDateTime time, String format) {
/* 356 */     return TemporalAccessorUtil.format(time, format);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String formatNormal(LocalDate date) {
/* 367 */     return format(date, DatePattern.NORM_DATE_FORMATTER);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String format(LocalDate date, DateTimeFormatter formatter) {
/* 379 */     return TemporalAccessorUtil.format(date, formatter);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String format(LocalDate date, String format) {
/* 391 */     if (null == date) {
/* 392 */       return null;
/*     */     }
/* 394 */     return format(date, DateTimeFormatter.ofPattern(format));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static LocalDateTime offset(LocalDateTime time, long number, TemporalUnit field) {
/* 406 */     return TemporalUtil.<LocalDateTime>offset(time, number, field);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Duration between(LocalDateTime startTimeInclude, LocalDateTime endTimeExclude) {
/* 420 */     return TemporalUtil.between(startTimeInclude, endTimeExclude);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long between(LocalDateTime startTimeInclude, LocalDateTime endTimeExclude, ChronoUnit unit) {
/* 435 */     return TemporalUtil.between(startTimeInclude, endTimeExclude, unit);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Period betweenPeriod(LocalDate startTimeInclude, LocalDate endTimeExclude) {
/* 449 */     return Period.between(startTimeInclude, endTimeExclude);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static LocalDateTime beginOfDay(LocalDateTime time) {
/* 459 */     return time.with(LocalTime.MIN);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static LocalDateTime endOfDay(LocalDateTime time) {
/* 469 */     return endOfDay(time, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static LocalDateTime endOfDay(LocalDateTime time, boolean truncateMillisecond) {
/* 485 */     if (truncateMillisecond) {
/* 486 */       return time.with(LocalTime.of(23, 59, 59));
/*     */     }
/* 488 */     return time.with(LocalTime.MAX);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long toEpochMilli(TemporalAccessor temporalAccessor) {
/* 500 */     return TemporalAccessorUtil.toEpochMilli(temporalAccessor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isWeekend(LocalDateTime localDateTime) {
/* 511 */     return isWeekend(localDateTime.toLocalDate());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isWeekend(LocalDate localDate) {
/* 522 */     DayOfWeek dayOfWeek = localDate.getDayOfWeek();
/* 523 */     return (DayOfWeek.SATURDAY == dayOfWeek || DayOfWeek.SUNDAY == dayOfWeek);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Week dayOfWeek(LocalDate localDate) {
/* 534 */     return Week.of(localDate.getDayOfWeek());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isOverlap(ChronoLocalDateTime<?> realStartTime, ChronoLocalDateTime<?> realEndTime, ChronoLocalDateTime<?> startTime, ChronoLocalDateTime<?> endTime) {
/* 554 */     return (startTime.isBefore(realEndTime) && endTime.isAfter(realStartTime));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int weekOfYear(TemporalAccessor date) {
/* 572 */     return TemporalAccessorUtil.get(date, WeekFields.ISO.weekOfYear());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\date\LocalDateTimeUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */