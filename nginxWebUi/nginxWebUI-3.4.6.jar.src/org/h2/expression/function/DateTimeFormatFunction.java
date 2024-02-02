/*     */ package org.h2.expression.function;
/*     */ 
/*     */ import java.time.Instant;
/*     */ import java.time.LocalDate;
/*     */ import java.time.LocalDateTime;
/*     */ import java.time.LocalTime;
/*     */ import java.time.OffsetDateTime;
/*     */ import java.time.ZoneId;
/*     */ import java.time.ZoneOffset;
/*     */ import java.time.ZonedDateTime;
/*     */ import java.time.format.DateTimeFormatter;
/*     */ import java.time.temporal.ChronoField;
/*     */ import java.time.temporal.TemporalAccessor;
/*     */ import java.time.temporal.TemporalQueries;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.TypedValueExpression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.util.JSR310Utils;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueTime;
/*     */ import org.h2.value.ValueTimestampTimeZone;
/*     */ import org.h2.value.ValueVarchar;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class DateTimeFormatFunction
/*     */   extends FunctionN
/*     */ {
/*     */   public static final int FORMATDATETIME = 0;
/*     */   public static final int PARSEDATETIME = 1;
/*     */   
/*     */   private static final class CacheKey
/*     */   {
/*     */     private final String format;
/*     */     private final String locale;
/*     */     private final String timeZone;
/*     */     
/*     */     CacheKey(String param1String1, String param1String2, String param1String3) {
/*  49 */       this.format = param1String1;
/*  50 */       this.locale = param1String2;
/*  51 */       this.timeZone = param1String3;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public int hashCode() {
/*  57 */       int i = 1;
/*  58 */       i = 31 * i + this.format.hashCode();
/*  59 */       i = 31 * i + ((this.locale == null) ? 0 : this.locale.hashCode());
/*  60 */       i = 31 * i + ((this.timeZone == null) ? 0 : this.timeZone.hashCode());
/*  61 */       return i;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object param1Object) {
/*  66 */       if (this == param1Object)
/*  67 */         return true; 
/*  68 */       if (!(param1Object instanceof CacheKey)) {
/*  69 */         return false;
/*     */       }
/*  71 */       CacheKey cacheKey = (CacheKey)param1Object;
/*  72 */       return (this.format.equals(cacheKey.format) && Objects.equals(this.locale, cacheKey.locale) && 
/*  73 */         Objects.equals(this.timeZone, cacheKey.timeZone));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static final class CacheValue
/*     */   {
/*     */     final DateTimeFormatter formatter;
/*     */     
/*     */     final ZoneId zoneId;
/*     */     
/*     */     CacheValue(DateTimeFormatter param1DateTimeFormatter, ZoneId param1ZoneId) {
/*  85 */       this.formatter = param1DateTimeFormatter;
/*  86 */       this.zoneId = param1ZoneId;
/*     */     }
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
/* 101 */   private static final String[] NAMES = new String[] { "FORMATDATETIME", "PARSEDATETIME" };
/*     */ 
/*     */ 
/*     */   
/* 105 */   private static final LinkedHashMap<CacheKey, CacheValue> CACHE = new LinkedHashMap<CacheKey, CacheValue>()
/*     */     {
/*     */       private static final long serialVersionUID = 1L;
/*     */ 
/*     */       
/*     */       protected boolean removeEldestEntry(Map.Entry<DateTimeFormatFunction.CacheKey, DateTimeFormatFunction.CacheValue> param1Entry) {
/* 111 */         return (size() > 100);
/*     */       }
/*     */     };
/*     */   
/*     */   private final int function;
/*     */ 
/*     */   
/*     */   public DateTimeFormatFunction(int paramInt) {
/* 119 */     super(new Expression[4]);
/* 120 */     this.function = paramInt;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2, Value paramValue3) {
/* 125 */     String str2, str3, str1 = paramValue2.getString();
/* 126 */     if (paramValue3 != null) {
/* 127 */       str2 = paramValue3.getString();
/* 128 */       str3 = (this.args.length > 3) ? this.args[3].getValue(paramSessionLocal).getString() : null;
/*     */     } else {
/* 130 */       str3 = (String)(str2 = null);
/*     */     } 
/* 132 */     switch (this.function) {
/*     */       case 0:
/* 134 */         paramValue1 = ValueVarchar.get(formatDateTime(paramSessionLocal, paramValue1, str1, str2, str3));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 142 */         return paramValue1;
/*     */       case 1:
/*     */         return (Value)parseDateTime(paramSessionLocal, paramValue1.getString(), str1, str2, str3);
/*     */     } 
/*     */     throw DbException.getInternalError("function=" + this.function);
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
/*     */   public static String formatDateTime(SessionLocal paramSessionLocal, Value paramValue, String paramString1, String paramString2, String paramString3) {
/*     */     ZonedDateTime zonedDateTime;
/* 162 */     CacheValue cacheValue = getDateFormat(paramString1, paramString2, paramString3);
/* 163 */     ZoneId zoneId = cacheValue.zoneId;
/*     */     
/* 165 */     if (paramValue instanceof ValueTimestampTimeZone) {
/* 166 */       ZoneId zoneId1; OffsetDateTime offsetDateTime = JSR310Utils.valueToOffsetDateTime(paramValue, (CastDataProvider)paramSessionLocal);
/*     */       
/* 168 */       if (zoneId != null) {
/* 169 */         zoneId1 = zoneId;
/*     */       } else {
/* 171 */         ZoneOffset zoneOffset = offsetDateTime.getOffset();
/* 172 */         zoneId1 = ZoneId.ofOffset((zoneOffset.getTotalSeconds() == 0) ? "UTC" : "GMT", zoneOffset);
/*     */       } 
/* 174 */       zonedDateTime = offsetDateTime.atZoneSameInstant(zoneId1);
/*     */     } else {
/* 176 */       LocalDateTime localDateTime = JSR310Utils.valueToLocalDateTime(paramValue, (CastDataProvider)paramSessionLocal);
/* 177 */       zonedDateTime = localDateTime.atZone((zoneId != null) ? zoneId : ZoneId.of(paramSessionLocal.currentTimeZone().getId()));
/*     */     } 
/* 179 */     return cacheValue.formatter.format(zonedDateTime);
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
/*     */   public static ValueTimestampTimeZone parseDateTime(SessionLocal paramSessionLocal, String paramString1, String paramString2, String paramString3, String paramString4) {
/* 199 */     CacheValue cacheValue = getDateFormat(paramString2, paramString3, paramString4);
/*     */     try {
/*     */       ValueTimestampTimeZone valueTimestampTimeZone;
/* 202 */       TemporalAccessor temporalAccessor = cacheValue.formatter.parse(paramString1);
/* 203 */       ZoneId zoneId = temporalAccessor.<ZoneId>query(TemporalQueries.zoneId());
/* 204 */       if (temporalAccessor.isSupported(ChronoField.OFFSET_SECONDS)) {
/* 205 */         valueTimestampTimeZone = JSR310Utils.offsetDateTimeToValue(OffsetDateTime.from(temporalAccessor));
/*     */       }
/* 207 */       else if (temporalAccessor.isSupported(ChronoField.INSTANT_SECONDS)) {
/* 208 */         Instant instant = Instant.from(temporalAccessor);
/* 209 */         if (zoneId == null) {
/* 210 */           zoneId = cacheValue.zoneId;
/*     */         }
/* 212 */         if (zoneId != null) {
/* 213 */           valueTimestampTimeZone = JSR310Utils.zonedDateTimeToValue(instant.atZone(zoneId));
/*     */         } else {
/* 215 */           valueTimestampTimeZone = JSR310Utils.offsetDateTimeToValue(instant.atOffset(ZoneOffset.ofTotalSeconds(paramSessionLocal
/* 216 */                   .currentTimeZone().getTimeZoneOffsetUTC(instant.getEpochSecond()))));
/*     */         } 
/*     */       } else {
/* 219 */         LocalDate localDate = temporalAccessor.<LocalDate>query(TemporalQueries.localDate());
/* 220 */         LocalTime localTime = temporalAccessor.<LocalTime>query(TemporalQueries.localTime());
/* 221 */         if (zoneId == null) {
/* 222 */           zoneId = cacheValue.zoneId;
/*     */         }
/* 224 */         if (localDate != null) {
/*     */           
/* 226 */           LocalDateTime localDateTime = (localTime != null) ? LocalDateTime.of(localDate, localTime) : localDate.atStartOfDay();
/*     */ 
/*     */ 
/*     */           
/* 230 */           valueTimestampTimeZone = (zoneId != null) ? JSR310Utils.zonedDateTimeToValue(localDateTime.atZone(zoneId)) : (ValueTimestampTimeZone)JSR310Utils.localDateTimeToValue(localDateTime).convertTo(21, (CastDataProvider)paramSessionLocal);
/*     */ 
/*     */         
/*     */         }
/*     */         else {
/*     */ 
/*     */           
/* 237 */           valueTimestampTimeZone = (zoneId != null) ? JSR310Utils.zonedDateTimeToValue(JSR310Utils.valueToInstant((Value)paramSessionLocal.currentTimestamp(), (CastDataProvider)paramSessionLocal).atZone(zoneId).with(localTime)) : (ValueTimestampTimeZone)ValueTime.fromNanos(localTime.toNanoOfDay()).convertTo(21, (CastDataProvider)paramSessionLocal);
/*     */         } 
/*     */       } 
/*     */       
/* 241 */       return valueTimestampTimeZone;
/* 242 */     } catch (RuntimeException runtimeException) {
/* 243 */       throw DbException.get(90014, runtimeException, new String[] { paramString1 });
/*     */     } 
/*     */   }
/*     */   
/*     */   private static CacheValue getDateFormat(String paramString1, String paramString2, String paramString3) {
/* 248 */     Exception exception = null;
/* 249 */     if (paramString1.length() <= 100) {
/*     */       try {
/*     */         CacheValue cacheValue;
/* 252 */         CacheKey cacheKey = new CacheKey(paramString1, paramString2, paramString3);
/* 253 */         synchronized (CACHE) {
/* 254 */           cacheValue = CACHE.get(cacheKey);
/* 255 */           if (cacheValue == null) {
/*     */             DateTimeFormatter dateTimeFormatter; ZoneId zoneId;
/* 257 */             if (paramString2 == null) {
/* 258 */               dateTimeFormatter = DateTimeFormatter.ofPattern(paramString1);
/*     */             } else {
/* 260 */               dateTimeFormatter = DateTimeFormatter.ofPattern(paramString1, new Locale(paramString2));
/*     */             } 
/*     */             
/* 263 */             if (paramString3 != null) {
/* 264 */               zoneId = getZoneId(paramString3);
/* 265 */               dateTimeFormatter.withZone(zoneId);
/*     */             } else {
/* 267 */               zoneId = null;
/*     */             } 
/* 269 */             cacheValue = new CacheValue(dateTimeFormatter, zoneId);
/* 270 */             CACHE.put(cacheKey, cacheValue);
/*     */           } 
/*     */         } 
/* 273 */         return cacheValue;
/* 274 */       } catch (Exception exception1) {
/* 275 */         exception = exception1;
/*     */       } 
/*     */     }
/* 278 */     throw DbException.get(90014, exception, new String[] { paramString1 + '/' + paramString2 });
/*     */   }
/*     */   
/*     */   private static ZoneId getZoneId(String paramString) {
/*     */     try {
/* 283 */       return ZoneId.of(paramString, ZoneId.SHORT_IDS);
/* 284 */     } catch (RuntimeException runtimeException) {
/* 285 */       throw DbException.getInvalidValueException("TIME ZONE", paramString);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 291 */     boolean bool = optimizeArguments(paramSessionLocal, true);
/* 292 */     switch (this.function) {
/*     */       case 0:
/* 294 */         this.type = TypeInfo.TYPE_VARCHAR;
/*     */         break;
/*     */       case 1:
/* 297 */         this.type = TypeInfo.TYPE_TIMESTAMP_TZ;
/*     */         break;
/*     */       default:
/* 300 */         throw DbException.getInternalError("function=" + this.function);
/*     */     } 
/* 302 */     if (bool) {
/* 303 */       return (Expression)TypedValueExpression.getTypedIfNull(getValue(paramSessionLocal), this.type);
/*     */     }
/* 305 */     return (Expression)this;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 310 */     return NAMES[this.function];
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\DateTimeFormatFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */