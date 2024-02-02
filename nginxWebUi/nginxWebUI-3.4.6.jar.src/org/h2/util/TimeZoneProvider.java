/*     */ package org.h2.util;
/*     */ 
/*     */ import java.time.Instant;
/*     */ import java.time.LocalDateTime;
/*     */ import java.time.ZoneId;
/*     */ import java.time.ZoneOffset;
/*     */ import java.time.ZonedDateTime;
/*     */ import java.time.format.DateTimeFormatter;
/*     */ import java.time.zone.ZoneRules;
/*     */ import java.util.Locale;
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
/*     */ public abstract class TimeZoneProvider
/*     */ {
/*  25 */   public static final TimeZoneProvider UTC = new Simple(0);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static TimeZoneProvider[] CACHE;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int CACHE_SIZE = 32;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static TimeZoneProvider ofOffset(int paramInt) {
/*  45 */     if (paramInt == 0) {
/*  46 */       return UTC;
/*     */     }
/*  48 */     if (paramInt < -64800 || paramInt > 64800) {
/*  49 */       throw new IllegalArgumentException("Time zone offset " + paramInt + " seconds is out of range");
/*     */     }
/*  51 */     return new Simple(paramInt);
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
/*     */   public static TimeZoneProvider ofId(String paramString) throws RuntimeException {
/*  64 */     int i = paramString.length();
/*  65 */     if (i == 1 && paramString.charAt(0) == 'Z') {
/*  66 */       return UTC;
/*     */     }
/*  68 */     byte b = 0;
/*  69 */     if (paramString.startsWith("GMT") || paramString.startsWith("UTC")) {
/*  70 */       if (i == 3) {
/*  71 */         return UTC;
/*     */       }
/*  73 */       b = 3;
/*     */     } 
/*  75 */     if (i > b) {
/*  76 */       boolean bool = false;
/*  77 */       char c = paramString.charAt(b);
/*  78 */       if (i > b + 1) {
/*  79 */         if (c == '+') {
/*  80 */           c = paramString.charAt(++b);
/*  81 */         } else if (c == '-') {
/*  82 */           bool = true;
/*  83 */           c = paramString.charAt(++b);
/*     */         } 
/*     */       }
/*  86 */       if (b != 3 && c >= '0' && c <= '9') {
/*  87 */         int k = c - 48;
/*  88 */         if (++b < i) {
/*  89 */           c = paramString.charAt(b);
/*  90 */           if (c >= '0' && c <= '9') {
/*  91 */             k = k * 10 + c - 48;
/*  92 */             b++;
/*     */           } 
/*     */         } 
/*  95 */         if (b == i) {
/*  96 */           int m = k * 3600;
/*  97 */           return ofOffset(bool ? -m : m);
/*     */         } 
/*  99 */         if (paramString.charAt(b) == ':' && 
/* 100 */           ++b < i) {
/* 101 */           c = paramString.charAt(b);
/* 102 */           if (c >= '0' && c <= '9') {
/* 103 */             int m = c - 48;
/* 104 */             if (++b < i) {
/* 105 */               c = paramString.charAt(b);
/* 106 */               if (c >= '0' && c <= '9') {
/* 107 */                 m = m * 10 + c - 48;
/* 108 */                 b++;
/*     */               } 
/*     */             } 
/* 111 */             if (b == i) {
/* 112 */               int n = (k * 60 + m) * 60;
/* 113 */               return ofOffset(bool ? -n : n);
/*     */             } 
/* 115 */             if (paramString.charAt(b) == ':' && 
/* 116 */               ++b < i) {
/* 117 */               c = paramString.charAt(b);
/* 118 */               if (c >= '0' && c <= '9') {
/* 119 */                 int n = c - 48;
/* 120 */                 if (++b < i) {
/* 121 */                   c = paramString.charAt(b);
/* 122 */                   if (c >= '0' && c <= '9') {
/* 123 */                     n = n * 10 + c - 48;
/* 124 */                     b++;
/*     */                   } 
/*     */                 } 
/* 127 */                 if (b == i) {
/* 128 */                   int i1 = (k * 60 + m) * 60 + n;
/* 129 */                   return ofOffset(bool ? -i1 : i1);
/*     */                 } 
/*     */               } 
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 138 */       if (b > 0) {
/* 139 */         throw new IllegalArgumentException(paramString);
/*     */       }
/*     */     } 
/* 142 */     int j = paramString.hashCode() & 0x1F;
/* 143 */     TimeZoneProvider[] arrayOfTimeZoneProvider = CACHE;
/* 144 */     if (arrayOfTimeZoneProvider != null) {
/* 145 */       TimeZoneProvider timeZoneProvider = arrayOfTimeZoneProvider[j];
/* 146 */       if (timeZoneProvider != null && timeZoneProvider.getId().equals(paramString)) {
/* 147 */         return timeZoneProvider;
/*     */       }
/*     */     } 
/* 150 */     WithTimeZone withTimeZone = new WithTimeZone(ZoneId.of(paramString, ZoneId.SHORT_IDS));
/* 151 */     if (arrayOfTimeZoneProvider == null) {
/* 152 */       CACHE = arrayOfTimeZoneProvider = new TimeZoneProvider[32];
/*     */     }
/* 154 */     arrayOfTimeZoneProvider[j] = withTimeZone;
/* 155 */     return withTimeZone;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static TimeZoneProvider getDefault() {
/*     */     ZoneOffset zoneOffset;
/* 164 */     ZoneId zoneId = ZoneId.systemDefault();
/*     */     
/* 166 */     if (zoneId instanceof ZoneOffset) {
/* 167 */       zoneOffset = (ZoneOffset)zoneId;
/*     */     } else {
/* 169 */       ZoneRules zoneRules = zoneId.getRules();
/* 170 */       if (!zoneRules.isFixedOffset()) {
/* 171 */         return new WithTimeZone(zoneId);
/*     */       }
/* 173 */       zoneOffset = zoneRules.getOffset(Instant.EPOCH);
/*     */     } 
/* 175 */     return ofOffset(zoneOffset.getTotalSeconds());
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
/*     */   public abstract int getTimeZoneOffsetUTC(long paramLong);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract int getTimeZoneOffsetLocal(long paramLong1, long paramLong2);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract long getEpochSecondsFromLocal(long paramLong1, long paramLong2);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract String getId();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract String getShortId(long paramLong);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasFixedOffset() {
/* 236 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private static final class Simple
/*     */     extends TimeZoneProvider
/*     */   {
/*     */     private final int offset;
/*     */     
/*     */     private volatile String id;
/*     */ 
/*     */     
/*     */     Simple(int param1Int) {
/* 249 */       this.offset = param1Int;
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 254 */       return this.offset + 129607;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object param1Object) {
/* 259 */       if (this == param1Object) {
/* 260 */         return true;
/*     */       }
/* 262 */       if (param1Object == null || param1Object.getClass() != Simple.class) {
/* 263 */         return false;
/*     */       }
/* 265 */       return (this.offset == ((Simple)param1Object).offset);
/*     */     }
/*     */ 
/*     */     
/*     */     public int getTimeZoneOffsetUTC(long param1Long) {
/* 270 */       return this.offset;
/*     */     }
/*     */ 
/*     */     
/*     */     public int getTimeZoneOffsetLocal(long param1Long1, long param1Long2) {
/* 275 */       return this.offset;
/*     */     }
/*     */ 
/*     */     
/*     */     public long getEpochSecondsFromLocal(long param1Long1, long param1Long2) {
/* 280 */       return DateTimeUtils.getEpochSeconds(param1Long1, param1Long2, this.offset);
/*     */     }
/*     */ 
/*     */     
/*     */     public String getId() {
/* 285 */       String str = this.id;
/* 286 */       if (str == null) {
/* 287 */         this.id = str = DateTimeUtils.timeZoneNameFromOffsetSeconds(this.offset);
/*     */       }
/* 289 */       return str;
/*     */     }
/*     */ 
/*     */     
/*     */     public String getShortId(long param1Long) {
/* 294 */       return getId();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean hasFixedOffset() {
/* 299 */       return true;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 304 */       return "TimeZoneProvider " + getId();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final class WithTimeZone
/*     */     extends TimeZoneProvider
/*     */   {
/*     */     static final long SECONDS_PER_PERIOD = 12622780800L;
/*     */ 
/*     */ 
/*     */     
/*     */     static final long SECONDS_PER_YEAR = 31556952L;
/*     */ 
/*     */     
/*     */     private static volatile DateTimeFormatter TIME_ZONE_FORMATTER;
/*     */ 
/*     */     
/*     */     private final ZoneId zoneId;
/*     */ 
/*     */ 
/*     */     
/*     */     WithTimeZone(ZoneId param1ZoneId) {
/* 329 */       this.zoneId = param1ZoneId;
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 334 */       return this.zoneId.hashCode() + 951689;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object param1Object) {
/* 339 */       if (this == param1Object) {
/* 340 */         return true;
/*     */       }
/* 342 */       if (param1Object == null || param1Object.getClass() != WithTimeZone.class) {
/* 343 */         return false;
/*     */       }
/* 345 */       return this.zoneId.equals(((WithTimeZone)param1Object).zoneId);
/*     */     }
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
/*     */     public int getTimeZoneOffsetUTC(long param1Long) {
/* 362 */       if (param1Long > 31556889832715999L) {
/* 363 */         param1Long -= 12622780800L;
/* 364 */       } else if (param1Long < -31557014135532000L) {
/* 365 */         param1Long += 12622780800L;
/*     */       } 
/* 367 */       return this.zoneId.getRules().getOffset(Instant.ofEpochSecond(param1Long)).getTotalSeconds();
/*     */     }
/*     */ 
/*     */     
/*     */     public int getTimeZoneOffsetLocal(long param1Long1, long param1Long2) {
/* 372 */       int i = (int)(param1Long2 / 1000000000L);
/* 373 */       int j = i / 60;
/* 374 */       i -= j * 60;
/* 375 */       int k = j / 60;
/* 376 */       j -= k * 60;
/* 377 */       return ZonedDateTime.of(LocalDateTime.of(yearForCalendar(DateTimeUtils.yearFromDateValue(param1Long1)), 
/* 378 */             DateTimeUtils.monthFromDateValue(param1Long1), DateTimeUtils.dayFromDateValue(param1Long1), k, j, i), this.zoneId)
/* 379 */         .getOffset().getTotalSeconds();
/*     */     }
/*     */ 
/*     */     
/*     */     public long getEpochSecondsFromLocal(long param1Long1, long param1Long2) {
/* 384 */       int i = (int)(param1Long2 / 1000000000L);
/* 385 */       int j = i / 60;
/* 386 */       i -= j * 60;
/* 387 */       int k = j / 60;
/* 388 */       j -= k * 60;
/* 389 */       int m = DateTimeUtils.yearFromDateValue(param1Long1);
/* 390 */       int n = yearForCalendar(m);
/*     */ 
/*     */ 
/*     */       
/* 394 */       long l = ZonedDateTime.of(LocalDateTime.of(n, DateTimeUtils.monthFromDateValue(param1Long1), DateTimeUtils.dayFromDateValue(param1Long1), k, j, i), this.zoneId).toOffsetDateTime().toEpochSecond();
/* 395 */       return l + (m - n) * 31556952L;
/*     */     }
/*     */ 
/*     */     
/*     */     public String getId() {
/* 400 */       return this.zoneId.getId();
/*     */     }
/*     */ 
/*     */     
/*     */     public String getShortId(long param1Long) {
/* 405 */       DateTimeFormatter dateTimeFormatter = TIME_ZONE_FORMATTER;
/* 406 */       if (dateTimeFormatter == null) {
/* 407 */         TIME_ZONE_FORMATTER = dateTimeFormatter = DateTimeFormatter.ofPattern("z", Locale.ENGLISH);
/*     */       }
/* 409 */       return ZonedDateTime.ofInstant(Instant.ofEpochSecond(param1Long), this.zoneId).format(dateTimeFormatter);
/*     */     }
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
/*     */     private static int yearForCalendar(int param1Int) {
/* 426 */       if (param1Int > 999999999) {
/* 427 */         param1Int -= 400;
/* 428 */       } else if (param1Int < -999999999) {
/* 429 */         param1Int += 400;
/*     */       } 
/* 431 */       return param1Int;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 436 */       return "TimeZoneProvider " + this.zoneId.getId();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\TimeZoneProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */