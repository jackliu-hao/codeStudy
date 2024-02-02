/*     */ package org.h2.util;
/*     */ 
/*     */ import java.sql.Date;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.GregorianCalendar;
/*     */ import java.util.TimeZone;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueDate;
/*     */ import org.h2.value.ValueNull;
/*     */ import org.h2.value.ValueTime;
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
/*     */ public final class LegacyDateTimeUtils
/*     */ {
/*  37 */   public static final Date PROLEPTIC_GREGORIAN_CHANGE = new Date(Long.MIN_VALUE);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  42 */   public static final TimeZone UTC = TimeZone.getTimeZone("UTC");
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
/*     */   public static ValueDate fromDate(CastDataProvider paramCastDataProvider, TimeZone paramTimeZone, Date paramDate) {
/*  59 */     long l = paramDate.getTime();
/*  60 */     return ValueDate.fromDateValue(dateValueFromLocalMillis(l + ((paramTimeZone == null) ? 
/*  61 */           getTimeZoneOffsetMillis(paramCastDataProvider, l) : paramTimeZone.getOffset(l))));
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
/*     */   public static ValueTime fromTime(CastDataProvider paramCastDataProvider, TimeZone paramTimeZone, Time paramTime) {
/*  76 */     long l = paramTime.getTime();
/*  77 */     return ValueTime.fromNanos(nanosFromLocalMillis(l + ((paramTimeZone == null) ? 
/*  78 */           getTimeZoneOffsetMillis(paramCastDataProvider, l) : paramTimeZone.getOffset(l))));
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
/*     */   public static ValueTimestamp fromTimestamp(CastDataProvider paramCastDataProvider, TimeZone paramTimeZone, Timestamp paramTimestamp) {
/*  93 */     long l = paramTimestamp.getTime();
/*  94 */     return timestampFromLocalMillis(l + ((paramTimeZone == null) ? 
/*  95 */         getTimeZoneOffsetMillis(paramCastDataProvider, l) : paramTimeZone.getOffset(l)), paramTimestamp
/*  96 */         .getNanos() % 1000000);
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
/*     */   public static ValueTimestamp fromTimestamp(CastDataProvider paramCastDataProvider, long paramLong, int paramInt) {
/* 111 */     return timestampFromLocalMillis(paramLong + getTimeZoneOffsetMillis(paramCastDataProvider, paramLong), paramInt);
/*     */   }
/*     */   
/*     */   private static ValueTimestamp timestampFromLocalMillis(long paramLong, int paramInt) {
/* 115 */     long l1 = dateValueFromLocalMillis(paramLong);
/* 116 */     long l2 = paramInt + nanosFromLocalMillis(paramLong);
/* 117 */     return ValueTimestamp.fromDateValueAndNanos(l1, l2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long dateValueFromLocalMillis(long paramLong) {
/* 128 */     long l = paramLong / 86400000L;
/*     */     
/* 130 */     if (paramLong < 0L && l * 86400000L != paramLong) {
/* 131 */       l--;
/*     */     }
/* 133 */     return DateTimeUtils.dateValueFromAbsoluteDay(l);
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
/*     */   public static long nanosFromLocalMillis(long paramLong) {
/* 145 */     paramLong %= 86400000L;
/* 146 */     if (paramLong < 0L) {
/* 147 */       paramLong += 86400000L;
/*     */     }
/* 149 */     return paramLong * 1000000L;
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
/*     */   public static Date toDate(CastDataProvider paramCastDataProvider, TimeZone paramTimeZone, Value paramValue) {
/* 161 */     return (paramValue != ValueNull.INSTANCE) ? new Date(
/* 162 */         getMillis(paramCastDataProvider, paramTimeZone, paramValue.convertToDate(paramCastDataProvider).getDateValue(), 0L)) : null;
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
/*     */   public static Time toTime(CastDataProvider paramCastDataProvider, TimeZone paramTimeZone, Value paramValue) {
/* 174 */     switch (paramValue.getValueType()) {
/*     */       case 0:
/* 176 */         return null;
/*     */       default:
/* 178 */         paramValue = paramValue.convertTo(TypeInfo.TYPE_TIME, paramCastDataProvider); break;
/*     */       case 18:
/*     */         break;
/* 181 */     }  return new Time(
/* 182 */         getMillis(paramCastDataProvider, paramTimeZone, 1008673L, ((ValueTime)paramValue).getNanos()));
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
/*     */   public static Timestamp toTimestamp(CastDataProvider paramCastDataProvider, TimeZone paramTimeZone, Value paramValue) {
/*     */     ValueTimestamp valueTimestamp;
/* 195 */     switch (paramValue.getValueType()) {
/*     */       case 0:
/* 197 */         return null;
/*     */       default:
/* 199 */         paramValue = paramValue.convertTo(TypeInfo.TYPE_TIMESTAMP, paramCastDataProvider);
/*     */       
/*     */       case 20:
/* 202 */         valueTimestamp = (ValueTimestamp)paramValue;
/* 203 */         l = valueTimestamp.getTimeNanos();
/* 204 */         timestamp = new Timestamp(getMillis(paramCastDataProvider, paramTimeZone, valueTimestamp.getDateValue(), l));
/* 205 */         timestamp.setNanos((int)(l % 1000000000L));
/* 206 */         return timestamp;
/*     */       case 21:
/*     */         break;
/* 209 */     }  ValueTimestampTimeZone valueTimestampTimeZone = (ValueTimestampTimeZone)paramValue;
/* 210 */     long l = valueTimestampTimeZone.getTimeNanos();
/*     */     
/* 212 */     Timestamp timestamp = new Timestamp(DateTimeUtils.absoluteDayFromDateValue(valueTimestampTimeZone.getDateValue()) * 86400000L + l / 1000000L - (valueTimestampTimeZone.getTimeZoneOffsetSeconds() * 1000));
/* 213 */     timestamp.setNanos((int)(l % 1000000000L));
/* 214 */     return timestamp;
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
/*     */   public static long getMillis(CastDataProvider paramCastDataProvider, TimeZone paramTimeZone, long paramLong1, long paramLong2) {
/* 231 */     return ((paramTimeZone == null) ? ((paramCastDataProvider != null) ? paramCastDataProvider.currentTimeZone() : DateTimeUtils.getTimeZone()) : 
/* 232 */       TimeZoneProvider.ofId(paramTimeZone.getID())).getEpochSecondsFromLocal(paramLong1, paramLong2) * 1000L + paramLong2 / 1000000L % 1000L;
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
/*     */   public static int getTimeZoneOffsetMillis(CastDataProvider paramCastDataProvider, long paramLong) {
/* 244 */     long l = paramLong / 1000L;
/*     */     
/* 246 */     if (paramLong < 0L && l * 1000L != paramLong) {
/* 247 */       l--;
/*     */     }
/* 249 */     return ((paramCastDataProvider != null) ? paramCastDataProvider.currentTimeZone() : DateTimeUtils.getTimeZone())
/* 250 */       .getTimeZoneOffsetUTC(l) * 1000;
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
/*     */   public static Value legacyObjectToValue(CastDataProvider paramCastDataProvider, Object paramObject) {
/* 263 */     if (paramObject instanceof Date)
/* 264 */       return (Value)fromDate(paramCastDataProvider, null, (Date)paramObject); 
/* 265 */     if (paramObject instanceof Time)
/* 266 */       return (Value)fromTime(paramCastDataProvider, null, (Time)paramObject); 
/* 267 */     if (paramObject instanceof Timestamp)
/* 268 */       return (Value)fromTimestamp(paramCastDataProvider, (TimeZone)null, (Timestamp)paramObject); 
/* 269 */     if (paramObject instanceof Date)
/* 270 */       return (Value)fromTimestamp(paramCastDataProvider, ((Date)paramObject).getTime(), 0); 
/* 271 */     if (paramObject instanceof Calendar) {
/* 272 */       Calendar calendar = (Calendar)paramObject;
/* 273 */       long l = calendar.getTimeInMillis();
/* 274 */       return (Value)timestampFromLocalMillis(l + calendar.getTimeZone().getOffset(l), 0);
/*     */     } 
/* 276 */     return null;
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
/*     */   public static <T> T valueToLegacyType(Class<T> paramClass, Value paramValue, CastDataProvider paramCastDataProvider) {
/* 291 */     if (paramClass == Date.class)
/* 292 */       return (T)toDate(paramCastDataProvider, null, paramValue); 
/* 293 */     if (paramClass == Time.class)
/* 294 */       return (T)toTime(paramCastDataProvider, null, paramValue); 
/* 295 */     if (paramClass == Timestamp.class)
/* 296 */       return (T)toTimestamp(paramCastDataProvider, null, paramValue); 
/* 297 */     if (paramClass == Date.class)
/* 298 */       return (T)new Date(toTimestamp(paramCastDataProvider, null, paramValue).getTime()); 
/* 299 */     if (paramClass == Calendar.class) {
/* 300 */       GregorianCalendar gregorianCalendar = new GregorianCalendar();
/* 301 */       gregorianCalendar.setGregorianChange(PROLEPTIC_GREGORIAN_CHANGE);
/* 302 */       gregorianCalendar.setTime(toTimestamp(paramCastDataProvider, gregorianCalendar.getTimeZone(), paramValue));
/* 303 */       return (T)gregorianCalendar;
/*     */     } 
/* 305 */     return null;
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
/*     */   public static TypeInfo legacyClassToType(Class<?> paramClass) {
/* 317 */     if (Date.class.isAssignableFrom(paramClass))
/* 318 */       return TypeInfo.TYPE_DATE; 
/* 319 */     if (Time.class.isAssignableFrom(paramClass))
/* 320 */       return TypeInfo.TYPE_TIME; 
/* 321 */     if (Date.class.isAssignableFrom(paramClass) || Calendar.class.isAssignableFrom(paramClass)) {
/* 322 */       return TypeInfo.TYPE_TIMESTAMP;
/*     */     }
/* 324 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\LegacyDateTimeUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */