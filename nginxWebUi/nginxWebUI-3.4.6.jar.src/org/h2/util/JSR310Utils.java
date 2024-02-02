/*     */ package org.h2.util;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import java.time.Duration;
/*     */ import java.time.Instant;
/*     */ import java.time.LocalDate;
/*     */ import java.time.LocalDateTime;
/*     */ import java.time.LocalTime;
/*     */ import java.time.OffsetDateTime;
/*     */ import java.time.OffsetTime;
/*     */ import java.time.Period;
/*     */ import java.time.ZoneOffset;
/*     */ import java.time.ZonedDateTime;
/*     */ import org.h2.api.IntervalQualifier;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.value.DataType;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueDate;
/*     */ import org.h2.value.ValueInterval;
/*     */ import org.h2.value.ValueTime;
/*     */ import org.h2.value.ValueTimeTimeZone;
/*     */ import org.h2.value.ValueTimestamp;
/*     */ import org.h2.value.ValueTimestampTimeZone;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JSR310Utils
/*     */ {
/*     */   private static final long MIN_DATE_VALUE = -511999999455L;
/*     */   private static final long MAX_DATE_VALUE = 511999999903L;
/*     */   private static final long MIN_INSTANT_SECOND = -31557014167219200L;
/*     */   private static final long MAX_INSTANT_SECOND = 31556889864403199L;
/*     */   
/*     */   public static LocalDate valueToLocalDate(Value paramValue, CastDataProvider paramCastDataProvider) {
/*  76 */     long l = paramValue.convertToDate(paramCastDataProvider).getDateValue();
/*  77 */     if (l > 511999999903L)
/*  78 */       return LocalDate.MAX; 
/*  79 */     if (l < -511999999455L) {
/*  80 */       return LocalDate.MIN;
/*     */     }
/*  82 */     return LocalDate.of(DateTimeUtils.yearFromDateValue(l), DateTimeUtils.monthFromDateValue(l), 
/*  83 */         DateTimeUtils.dayFromDateValue(l));
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
/*     */   public static LocalTime valueToLocalTime(Value paramValue, CastDataProvider paramCastDataProvider) {
/*  98 */     return LocalTime.ofNanoOfDay(((ValueTime)paramValue.convertTo(TypeInfo.TYPE_TIME, paramCastDataProvider)).getNanos());
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
/*     */   public static LocalDateTime valueToLocalDateTime(Value paramValue, CastDataProvider paramCastDataProvider) {
/* 113 */     ValueTimestamp valueTimestamp = (ValueTimestamp)paramValue.convertTo(TypeInfo.TYPE_TIMESTAMP, paramCastDataProvider);
/* 114 */     return localDateTimeFromDateNanos(valueTimestamp.getDateValue(), valueTimestamp.getTimeNanos());
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
/*     */   public static Instant valueToInstant(Value paramValue, CastDataProvider paramCastDataProvider) {
/* 130 */     ValueTimestampTimeZone valueTimestampTimeZone = (ValueTimestampTimeZone)paramValue.convertTo(TypeInfo.TYPE_TIMESTAMP_TZ, paramCastDataProvider);
/* 131 */     long l1 = valueTimestampTimeZone.getTimeNanos();
/*     */ 
/*     */ 
/*     */     
/* 135 */     long l2 = DateTimeUtils.absoluteDayFromDateValue(valueTimestampTimeZone.getDateValue()) * 86400L + l1 / 1000000000L - valueTimestampTimeZone.getTimeZoneOffsetSeconds();
/* 136 */     if (l2 > 31556889864403199L)
/* 137 */       return Instant.MAX; 
/* 138 */     if (l2 < -31557014167219200L) {
/* 139 */       return Instant.MIN;
/*     */     }
/* 141 */     return Instant.ofEpochSecond(l2, l1 % 1000000000L);
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
/*     */   public static OffsetDateTime valueToOffsetDateTime(Value paramValue, CastDataProvider paramCastDataProvider) {
/* 156 */     ValueTimestampTimeZone valueTimestampTimeZone = (ValueTimestampTimeZone)paramValue.convertTo(TypeInfo.TYPE_TIMESTAMP_TZ, paramCastDataProvider);
/* 157 */     return OffsetDateTime.of(localDateTimeFromDateNanos(valueTimestampTimeZone.getDateValue(), valueTimestampTimeZone.getTimeNanos()), 
/* 158 */         ZoneOffset.ofTotalSeconds(valueTimestampTimeZone.getTimeZoneOffsetSeconds()));
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
/*     */   public static ZonedDateTime valueToZonedDateTime(Value paramValue, CastDataProvider paramCastDataProvider) {
/* 173 */     ValueTimestampTimeZone valueTimestampTimeZone = (ValueTimestampTimeZone)paramValue.convertTo(TypeInfo.TYPE_TIMESTAMP_TZ, paramCastDataProvider);
/* 174 */     return ZonedDateTime.of(localDateTimeFromDateNanos(valueTimestampTimeZone.getDateValue(), valueTimestampTimeZone.getTimeNanos()), 
/* 175 */         ZoneOffset.ofTotalSeconds(valueTimestampTimeZone.getTimeZoneOffsetSeconds()));
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
/*     */   public static OffsetTime valueToOffsetTime(Value paramValue, CastDataProvider paramCastDataProvider) {
/* 190 */     ValueTimeTimeZone valueTimeTimeZone = (ValueTimeTimeZone)paramValue.convertTo(TypeInfo.TYPE_TIME_TZ, paramCastDataProvider);
/* 191 */     return OffsetTime.of(LocalTime.ofNanoOfDay(valueTimeTimeZone.getNanos()), 
/* 192 */         ZoneOffset.ofTotalSeconds(valueTimeTimeZone.getTimeZoneOffsetSeconds()));
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
/*     */   public static Period valueToPeriod(Value paramValue) {
/* 205 */     if (!(paramValue instanceof ValueInterval)) {
/* 206 */       paramValue = paramValue.convertTo(TypeInfo.TYPE_INTERVAL_YEAR_TO_MONTH);
/*     */     }
/* 208 */     if (!DataType.isYearMonthIntervalType(paramValue.getValueType())) {
/* 209 */       throw DbException.get(22018, (Throwable)null, new String[] { paramValue.getString() });
/*     */     }
/* 211 */     ValueInterval valueInterval = (ValueInterval)paramValue;
/* 212 */     IntervalQualifier intervalQualifier = valueInterval.getQualifier();
/* 213 */     boolean bool = valueInterval.isNegative();
/* 214 */     long l1 = valueInterval.getLeading();
/* 215 */     long l2 = valueInterval.getRemaining();
/* 216 */     int i = Value.convertToInt(IntervalUtils.yearsFromInterval(intervalQualifier, bool, l1, l2), null);
/* 217 */     int j = Value.convertToInt(IntervalUtils.monthsFromInterval(intervalQualifier, bool, l1, l2), null);
/* 218 */     return Period.of(i, j, 0);
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
/*     */   public static Duration valueToDuration(Value paramValue) {
/* 231 */     if (!(paramValue instanceof ValueInterval)) {
/* 232 */       paramValue = paramValue.convertTo(TypeInfo.TYPE_INTERVAL_DAY_TO_SECOND);
/*     */     }
/* 234 */     if (DataType.isYearMonthIntervalType(paramValue.getValueType())) {
/* 235 */       throw DbException.get(22018, (Throwable)null, new String[] { paramValue.getString() });
/*     */     }
/*     */     
/* 238 */     BigInteger[] arrayOfBigInteger = IntervalUtils.intervalToAbsolute((ValueInterval)paramValue).divideAndRemainder(BigInteger.valueOf(1000000000L));
/* 239 */     return Duration.ofSeconds(arrayOfBigInteger[0].longValue(), arrayOfBigInteger[1].longValue());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueDate localDateToValue(LocalDate paramLocalDate) {
/* 250 */     return ValueDate.fromDateValue(
/* 251 */         DateTimeUtils.dateValue(paramLocalDate.getYear(), paramLocalDate.getMonthValue(), paramLocalDate.getDayOfMonth()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueTime localTimeToValue(LocalTime paramLocalTime) {
/* 262 */     return ValueTime.fromNanos(paramLocalTime.toNanoOfDay());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueTimestamp localDateTimeToValue(LocalDateTime paramLocalDateTime) {
/* 273 */     LocalDate localDate = paramLocalDateTime.toLocalDate();
/* 274 */     return ValueTimestamp.fromDateValueAndNanos(
/* 275 */         DateTimeUtils.dateValue(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth()), paramLocalDateTime
/* 276 */         .toLocalTime().toNanoOfDay());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueTimestampTimeZone instantToValue(Instant paramInstant) {
/* 287 */     long l1 = paramInstant.getEpochSecond();
/* 288 */     int i = paramInstant.getNano();
/* 289 */     long l2 = l1 / 86400L;
/*     */     
/* 291 */     if (l1 < 0L && l2 * 86400L != l1) {
/* 292 */       l2--;
/*     */     }
/* 294 */     long l3 = (l1 - l2 * 86400L) * 1000000000L + i;
/* 295 */     return ValueTimestampTimeZone.fromDateValueAndNanos(DateTimeUtils.dateValueFromAbsoluteDay(l2), l3, 0);
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
/*     */   public static ValueTimestampTimeZone offsetDateTimeToValue(OffsetDateTime paramOffsetDateTime) {
/* 307 */     LocalDateTime localDateTime = paramOffsetDateTime.toLocalDateTime();
/* 308 */     LocalDate localDate = localDateTime.toLocalDate();
/* 309 */     return ValueTimestampTimeZone.fromDateValueAndNanos(
/* 310 */         DateTimeUtils.dateValue(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth()), localDateTime
/* 311 */         .toLocalTime().toNanoOfDay(), paramOffsetDateTime
/* 312 */         .getOffset().getTotalSeconds());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueTimestampTimeZone zonedDateTimeToValue(ZonedDateTime paramZonedDateTime) {
/* 323 */     LocalDateTime localDateTime = paramZonedDateTime.toLocalDateTime();
/* 324 */     LocalDate localDate = localDateTime.toLocalDate();
/* 325 */     return ValueTimestampTimeZone.fromDateValueAndNanos(
/* 326 */         DateTimeUtils.dateValue(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth()), localDateTime
/* 327 */         .toLocalTime().toNanoOfDay(), paramZonedDateTime
/* 328 */         .getOffset().getTotalSeconds());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueTimeTimeZone offsetTimeToValue(OffsetTime paramOffsetTime) {
/* 339 */     return ValueTimeTimeZone.fromNanos(paramOffsetTime.toLocalTime().toNanoOfDay(), paramOffsetTime
/* 340 */         .getOffset().getTotalSeconds());
/*     */   }
/*     */   
/*     */   private static LocalDateTime localDateTimeFromDateNanos(long paramLong1, long paramLong2) {
/* 344 */     if (paramLong1 > 511999999903L)
/* 345 */       return LocalDateTime.MAX; 
/* 346 */     if (paramLong1 < -511999999455L) {
/* 347 */       return LocalDateTime.MIN;
/*     */     }
/* 349 */     return LocalDateTime.of(LocalDate.of(DateTimeUtils.yearFromDateValue(paramLong1), 
/* 350 */           DateTimeUtils.monthFromDateValue(paramLong1), DateTimeUtils.dayFromDateValue(paramLong1)), 
/* 351 */         LocalTime.ofNanoOfDay(paramLong2));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueInterval periodToValue(Period paramPeriod) {
/*     */     IntervalQualifier intervalQualifier;
/* 362 */     int i = paramPeriod.getDays();
/* 363 */     if (i != 0) {
/* 364 */       throw DbException.getInvalidValueException("Period.days", Integer.valueOf(i));
/*     */     }
/* 366 */     int j = paramPeriod.getYears();
/* 367 */     int k = paramPeriod.getMonths();
/*     */     
/* 369 */     boolean bool = false;
/* 370 */     long l1 = 0L, l2 = 0L;
/* 371 */     if (j == 0) {
/* 372 */       if (k == 0L) {
/*     */         
/* 374 */         intervalQualifier = IntervalQualifier.YEAR_TO_MONTH;
/*     */       } else {
/* 376 */         intervalQualifier = IntervalQualifier.MONTH;
/* 377 */         l1 = k;
/* 378 */         if (l1 < 0L) {
/* 379 */           l1 = -l1;
/* 380 */           bool = true;
/*     */         }
/*     */       
/*     */       } 
/* 384 */     } else if (k == 0L) {
/* 385 */       intervalQualifier = IntervalQualifier.YEAR;
/* 386 */       l1 = j;
/* 387 */       if (l1 < 0L) {
/* 388 */         l1 = -l1;
/* 389 */         bool = true;
/*     */       } 
/*     */     } else {
/* 392 */       intervalQualifier = IntervalQualifier.YEAR_TO_MONTH;
/* 393 */       l1 = (j * 12 + k);
/* 394 */       if (l1 < 0L) {
/* 395 */         l1 = -l1;
/* 396 */         bool = true;
/*     */       } 
/* 398 */       l2 = l1 % 12L;
/* 399 */       l1 /= 12L;
/*     */     } 
/*     */     
/* 402 */     return ValueInterval.from(intervalQualifier, bool, l1, l2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueInterval durationToValue(Duration paramDuration) {
/* 413 */     long l = paramDuration.getSeconds();
/* 414 */     int i = paramDuration.getNano();
/* 415 */     boolean bool = (l < 0L) ? true : false;
/* 416 */     l = Math.abs(l);
/* 417 */     if (bool && i != 0) {
/* 418 */       i = 1000000000 - i;
/* 419 */       l--;
/*     */     } 
/* 421 */     return ValueInterval.from(IntervalQualifier.SECOND, bool, l, i);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\JSR310Utils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */