/*     */ package org.h2.util;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import org.h2.api.IntervalQualifier;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.value.ValueInterval;
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
/*     */ public class IntervalUtils
/*     */ {
/*  25 */   private static final BigInteger NANOS_PER_SECOND_BI = BigInteger.valueOf(1000000000L);
/*     */   
/*  27 */   private static final BigInteger NANOS_PER_MINUTE_BI = BigInteger.valueOf(60000000000L);
/*     */   
/*  29 */   private static final BigInteger NANOS_PER_HOUR_BI = BigInteger.valueOf(3600000000000L);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  34 */   public static final BigInteger NANOS_PER_DAY_BI = BigInteger.valueOf(86400000000000L);
/*     */   
/*  36 */   private static final BigInteger MONTHS_PER_YEAR_BI = BigInteger.valueOf(12L);
/*     */   
/*  38 */   private static final BigInteger HOURS_PER_DAY_BI = BigInteger.valueOf(24L);
/*     */   
/*  40 */   private static final BigInteger MINUTES_PER_DAY_BI = BigInteger.valueOf(1440L);
/*     */   
/*  42 */   private static final BigInteger MINUTES_PER_HOUR_BI = BigInteger.valueOf(60L);
/*     */   
/*  44 */   private static final BigInteger LEADING_MIN = BigInteger.valueOf(-999999999999999999L);
/*     */   
/*  46 */   private static final BigInteger LEADING_MAX = BigInteger.valueOf(999999999999999999L);
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
/*     */   public static ValueInterval parseFormattedInterval(IntervalQualifier paramIntervalQualifier, String paramString) {
/*  63 */     int i = 0;
/*  64 */     i = skipWS(paramString, i);
/*  65 */     if (!paramString.regionMatches(true, i, "INTERVAL", 0, 8)) {
/*  66 */       return parseInterval(paramIntervalQualifier, false, paramString);
/*     */     }
/*  68 */     i = skipWS(paramString, i + 8);
/*  69 */     boolean bool = false;
/*  70 */     char c = paramString.charAt(i);
/*  71 */     if (c == '-') {
/*  72 */       bool = true;
/*  73 */       i = skipWS(paramString, i + 1);
/*  74 */       c = paramString.charAt(i);
/*  75 */     } else if (c == '+') {
/*  76 */       i = skipWS(paramString, i + 1);
/*  77 */       c = paramString.charAt(i);
/*     */     } 
/*  79 */     if (c != '\'') {
/*  80 */       throw new IllegalArgumentException(paramString);
/*     */     }
/*  82 */     int j = ++i;
/*  83 */     int k = paramString.length();
/*     */     while (true) {
/*  85 */       if (i == k) {
/*  86 */         throw new IllegalArgumentException(paramString);
/*     */       }
/*  88 */       if (paramString.charAt(i) == '\'') {
/*     */         break;
/*     */       }
/*  91 */       i++;
/*     */     } 
/*  93 */     String str = paramString.substring(j, i);
/*  94 */     i = skipWS(paramString, i + 1);
/*  95 */     if (paramString.regionMatches(true, i, "YEAR", 0, 4)) {
/*  96 */       i += 4;
/*  97 */       int m = skipWSEnd(paramString, i);
/*  98 */       if (m == k) {
/*  99 */         return parseInterval(IntervalQualifier.YEAR, bool, str);
/*     */       }
/* 101 */       if (m > i && paramString.regionMatches(true, m, "TO", 0, 2)) {
/* 102 */         m += 2;
/* 103 */         i = skipWS(paramString, m);
/* 104 */         if (i > m && paramString.regionMatches(true, i, "MONTH", 0, 5) && 
/* 105 */           skipWSEnd(paramString, i + 5) == k) {
/* 106 */           return parseInterval(IntervalQualifier.YEAR_TO_MONTH, bool, str);
/*     */         }
/*     */       }
/*     */     
/* 110 */     } else if (paramString.regionMatches(true, i, "MONTH", 0, 5) && 
/* 111 */       skipWSEnd(paramString, i + 5) == k) {
/* 112 */       return parseInterval(IntervalQualifier.MONTH, bool, str);
/*     */     } 
/*     */     
/* 115 */     if (paramString.regionMatches(true, i, "DAY", 0, 3)) {
/* 116 */       i += 3;
/* 117 */       int m = skipWSEnd(paramString, i);
/* 118 */       if (m == k) {
/* 119 */         return parseInterval(IntervalQualifier.DAY, bool, str);
/*     */       }
/* 121 */       if (m > i && paramString.regionMatches(true, m, "TO", 0, 2)) {
/* 122 */         m += 2;
/* 123 */         i = skipWS(paramString, m);
/* 124 */         if (i > m) {
/* 125 */           if (paramString.regionMatches(true, i, "HOUR", 0, 4)) {
/* 126 */             if (skipWSEnd(paramString, i + 4) == k) {
/* 127 */               return parseInterval(IntervalQualifier.DAY_TO_HOUR, bool, str);
/*     */             }
/* 129 */           } else if (paramString.regionMatches(true, i, "MINUTE", 0, 6)) {
/* 130 */             if (skipWSEnd(paramString, i + 6) == k) {
/* 131 */               return parseInterval(IntervalQualifier.DAY_TO_MINUTE, bool, str);
/*     */             }
/* 133 */           } else if (paramString.regionMatches(true, i, "SECOND", 0, 6) && 
/* 134 */             skipWSEnd(paramString, i + 6) == k) {
/* 135 */             return parseInterval(IntervalQualifier.DAY_TO_SECOND, bool, str);
/*     */           } 
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 141 */     if (paramString.regionMatches(true, i, "HOUR", 0, 4)) {
/* 142 */       i += 4;
/* 143 */       int m = skipWSEnd(paramString, i);
/* 144 */       if (m == k) {
/* 145 */         return parseInterval(IntervalQualifier.HOUR, bool, str);
/*     */       }
/* 147 */       if (m > i && paramString.regionMatches(true, m, "TO", 0, 2)) {
/* 148 */         m += 2;
/* 149 */         i = skipWS(paramString, m);
/* 150 */         if (i > m) {
/* 151 */           if (paramString.regionMatches(true, i, "MINUTE", 0, 6)) {
/* 152 */             if (skipWSEnd(paramString, i + 6) == k) {
/* 153 */               return parseInterval(IntervalQualifier.HOUR_TO_MINUTE, bool, str);
/*     */             }
/* 155 */           } else if (paramString.regionMatches(true, i, "SECOND", 0, 6) && 
/* 156 */             skipWSEnd(paramString, i + 6) == k) {
/* 157 */             return parseInterval(IntervalQualifier.HOUR_TO_SECOND, bool, str);
/*     */           } 
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 163 */     if (paramString.regionMatches(true, i, "MINUTE", 0, 6)) {
/* 164 */       i += 6;
/* 165 */       int m = skipWSEnd(paramString, i);
/* 166 */       if (m == k) {
/* 167 */         return parseInterval(IntervalQualifier.MINUTE, bool, str);
/*     */       }
/* 169 */       if (m > i && paramString.regionMatches(true, m, "TO", 0, 2)) {
/* 170 */         m += 2;
/* 171 */         i = skipWS(paramString, m);
/* 172 */         if (i > m && paramString.regionMatches(true, i, "SECOND", 0, 6) && 
/* 173 */           skipWSEnd(paramString, i + 6) == k) {
/* 174 */           return parseInterval(IntervalQualifier.MINUTE_TO_SECOND, bool, str);
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 179 */     if (paramString.regionMatches(true, i, "SECOND", 0, 6) && 
/* 180 */       skipWSEnd(paramString, i + 6) == k) {
/* 181 */       return parseInterval(IntervalQualifier.SECOND, bool, str);
/*     */     }
/*     */     
/* 184 */     throw new IllegalArgumentException(paramString);
/*     */   }
/*     */   
/*     */   private static int skipWS(String paramString, int paramInt) {
/* 188 */     for (int i = paramString.length();; paramInt++) {
/* 189 */       if (paramInt == i) {
/* 190 */         throw new IllegalArgumentException(paramString);
/*     */       }
/* 192 */       if (!Character.isWhitespace(paramString.charAt(paramInt))) {
/* 193 */         return paramInt;
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private static int skipWSEnd(String paramString, int paramInt) {
/* 199 */     for (int i = paramString.length();; paramInt++) {
/* 200 */       if (paramInt == i) {
/* 201 */         return paramInt;
/*     */       }
/* 203 */       if (!Character.isWhitespace(paramString.charAt(paramInt))) {
/* 204 */         return paramInt;
/*     */       }
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
/*     */   public static ValueInterval parseInterval(IntervalQualifier paramIntervalQualifier, boolean paramBoolean, String paramString) {
/*     */     long l1, l2;
/*     */     int i, j, k;
/* 222 */     switch (paramIntervalQualifier) {
/*     */       case YEAR:
/*     */       case MONTH:
/*     */       case DAY:
/*     */       case HOUR:
/*     */       case MINUTE:
/* 228 */         l1 = parseIntervalLeading(paramString, 0, paramString.length(), paramBoolean);
/* 229 */         l2 = 0L;
/*     */         break;
/*     */       case SECOND:
/* 232 */         i = paramString.indexOf('.');
/* 233 */         if (i < 0) {
/* 234 */           l1 = parseIntervalLeading(paramString, 0, paramString.length(), paramBoolean);
/* 235 */           l2 = 0L; break;
/*     */         } 
/* 237 */         l1 = parseIntervalLeading(paramString, 0, i, paramBoolean);
/* 238 */         l2 = DateTimeUtils.parseNanos(paramString, i + 1, paramString.length());
/*     */         break;
/*     */ 
/*     */       
/*     */       case YEAR_TO_MONTH:
/* 243 */         return parseInterval2(paramIntervalQualifier, paramString, '-', 11, paramBoolean);
/*     */       case DAY_TO_HOUR:
/* 245 */         return parseInterval2(paramIntervalQualifier, paramString, ' ', 23, paramBoolean);
/*     */       case DAY_TO_MINUTE:
/* 247 */         i = paramString.indexOf(' ');
/* 248 */         if (i < 0) {
/* 249 */           l1 = parseIntervalLeading(paramString, 0, paramString.length(), paramBoolean);
/* 250 */           l2 = 0L; break;
/*     */         } 
/* 252 */         l1 = parseIntervalLeading(paramString, 0, i, paramBoolean);
/* 253 */         j = paramString.indexOf(':', i + 1);
/* 254 */         if (j < 0) {
/* 255 */           l2 = parseIntervalRemaining(paramString, i + 1, paramString.length(), 23) * 60L;
/*     */           break;
/*     */         } 
/* 258 */         l2 = parseIntervalRemaining(paramString, i + 1, j, 23) * 60L + parseIntervalRemaining(paramString, j + 1, paramString.length(), 59);
/*     */         break;
/*     */ 
/*     */ 
/*     */       
/*     */       case DAY_TO_SECOND:
/* 264 */         i = paramString.indexOf(' ');
/* 265 */         if (i < 0) {
/* 266 */           l1 = parseIntervalLeading(paramString, 0, paramString.length(), paramBoolean);
/* 267 */           l2 = 0L; break;
/*     */         } 
/* 269 */         l1 = parseIntervalLeading(paramString, 0, i, paramBoolean);
/* 270 */         j = paramString.indexOf(':', i + 1);
/* 271 */         if (j < 0) {
/* 272 */           l2 = parseIntervalRemaining(paramString, i + 1, paramString.length(), 23) * 3600000000000L; break;
/*     */         } 
/* 274 */         k = paramString.indexOf(':', j + 1);
/* 275 */         if (k < 0) {
/*     */           
/* 277 */           l2 = parseIntervalRemaining(paramString, i + 1, j, 23) * 3600000000000L + parseIntervalRemaining(paramString, j + 1, paramString.length(), 59) * 60000000000L;
/*     */           
/*     */           break;
/*     */         } 
/* 281 */         l2 = parseIntervalRemaining(paramString, i + 1, j, 23) * 3600000000000L + parseIntervalRemaining(paramString, j + 1, k, 59) * 60000000000L + parseIntervalRemainingSeconds(paramString, k + 1);
/*     */         break;
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       case HOUR_TO_MINUTE:
/* 288 */         return parseInterval2(paramIntervalQualifier, paramString, ':', 59, paramBoolean);
/*     */       case HOUR_TO_SECOND:
/* 290 */         i = paramString.indexOf(':');
/* 291 */         if (i < 0) {
/* 292 */           l1 = parseIntervalLeading(paramString, 0, paramString.length(), paramBoolean);
/* 293 */           l2 = 0L; break;
/*     */         } 
/* 295 */         l1 = parseIntervalLeading(paramString, 0, i, paramBoolean);
/* 296 */         j = paramString.indexOf(':', i + 1);
/* 297 */         if (j < 0) {
/* 298 */           l2 = parseIntervalRemaining(paramString, i + 1, paramString.length(), 59) * 60000000000L;
/*     */           break;
/*     */         } 
/* 301 */         l2 = parseIntervalRemaining(paramString, i + 1, j, 59) * 60000000000L + parseIntervalRemainingSeconds(paramString, j + 1);
/*     */         break;
/*     */ 
/*     */ 
/*     */       
/*     */       case MINUTE_TO_SECOND:
/* 307 */         i = paramString.indexOf(':');
/* 308 */         if (i < 0) {
/* 309 */           l1 = parseIntervalLeading(paramString, 0, paramString.length(), paramBoolean);
/* 310 */           l2 = 0L; break;
/*     */         } 
/* 312 */         l1 = parseIntervalLeading(paramString, 0, i, paramBoolean);
/* 313 */         l2 = parseIntervalRemainingSeconds(paramString, i + 1);
/*     */         break;
/*     */ 
/*     */       
/*     */       default:
/* 318 */         throw new IllegalArgumentException();
/*     */     } 
/* 320 */     paramBoolean = (l1 < 0L);
/* 321 */     if (paramBoolean) {
/* 322 */       if (l1 != Long.MIN_VALUE) {
/* 323 */         l1 = -l1;
/*     */       } else {
/* 325 */         l1 = 0L;
/*     */       } 
/*     */     }
/* 328 */     return ValueInterval.from(paramIntervalQualifier, paramBoolean, l1, l2);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static ValueInterval parseInterval2(IntervalQualifier paramIntervalQualifier, String paramString, char paramChar, int paramInt, boolean paramBoolean) {
/*     */     long l1, l2;
/* 335 */     int i = paramString.indexOf(paramChar, 1);
/* 336 */     if (i < 0) {
/* 337 */       l1 = parseIntervalLeading(paramString, 0, paramString.length(), paramBoolean);
/* 338 */       l2 = 0L;
/*     */     } else {
/* 340 */       l1 = parseIntervalLeading(paramString, 0, i, paramBoolean);
/* 341 */       l2 = parseIntervalRemaining(paramString, i + 1, paramString.length(), paramInt);
/*     */     } 
/* 343 */     paramBoolean = (l1 < 0L);
/* 344 */     if (paramBoolean) {
/* 345 */       if (l1 != Long.MIN_VALUE) {
/* 346 */         l1 = -l1;
/*     */       } else {
/* 348 */         l1 = 0L;
/*     */       } 
/*     */     }
/* 351 */     return ValueInterval.from(paramIntervalQualifier, paramBoolean, l1, l2);
/*     */   }
/*     */   
/*     */   private static long parseIntervalLeading(String paramString, int paramInt1, int paramInt2, boolean paramBoolean) {
/* 355 */     long l = Long.parseLong(paramString.substring(paramInt1, paramInt2));
/* 356 */     if (l == 0L) {
/* 357 */       return ((paramBoolean ^ ((paramString.charAt(paramInt1) == '-') ? 1 : 0)) != 0) ? Long.MIN_VALUE : 0L;
/*     */     }
/* 359 */     return paramBoolean ? -l : l;
/*     */   }
/*     */   
/*     */   private static long parseIntervalRemaining(String paramString, int paramInt1, int paramInt2, int paramInt3) {
/* 363 */     int i = StringUtils.parseUInt31(paramString, paramInt1, paramInt2);
/* 364 */     if (i > paramInt3) {
/* 365 */       throw new IllegalArgumentException(paramString);
/*     */     }
/* 367 */     return i;
/*     */   }
/*     */ 
/*     */   
/*     */   private static long parseIntervalRemainingSeconds(String paramString, int paramInt) {
/* 372 */     int i, j, k = paramString.indexOf('.', paramInt + 1);
/* 373 */     if (k < 0) {
/* 374 */       i = StringUtils.parseUInt31(paramString, paramInt, paramString.length());
/* 375 */       j = 0;
/*     */     } else {
/* 377 */       i = StringUtils.parseUInt31(paramString, paramInt, k);
/* 378 */       j = DateTimeUtils.parseNanos(paramString, k + 1, paramString.length());
/*     */     } 
/* 380 */     if (i > 59) {
/* 381 */       throw new IllegalArgumentException(paramString);
/*     */     }
/* 383 */     return i * 1000000000L + j;
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
/*     */   public static StringBuilder appendInterval(StringBuilder paramStringBuilder, IntervalQualifier paramIntervalQualifier, boolean paramBoolean, long paramLong1, long paramLong2) {
/*     */     int i;
/*     */     long l;
/*     */     int j;
/* 404 */     paramStringBuilder.append("INTERVAL '");
/* 405 */     if (paramBoolean) {
/* 406 */       paramStringBuilder.append('-');
/*     */     }
/* 408 */     switch (paramIntervalQualifier) {
/*     */       case YEAR:
/*     */       case MONTH:
/*     */       case DAY:
/*     */       case HOUR:
/*     */       case MINUTE:
/* 414 */         paramStringBuilder.append(paramLong1);
/*     */         break;
/*     */       case SECOND:
/* 417 */         DateTimeUtils.appendNanos(paramStringBuilder.append(paramLong1), (int)paramLong2);
/*     */         break;
/*     */       case YEAR_TO_MONTH:
/* 420 */         paramStringBuilder.append(paramLong1).append('-').append(paramLong2);
/*     */         break;
/*     */       case DAY_TO_HOUR:
/* 423 */         paramStringBuilder.append(paramLong1).append(' ');
/* 424 */         StringUtils.appendTwoDigits(paramStringBuilder, (int)paramLong2);
/*     */         break;
/*     */       case DAY_TO_MINUTE:
/* 427 */         paramStringBuilder.append(paramLong1).append(' ');
/* 428 */         i = (int)paramLong2;
/* 429 */         StringUtils.appendTwoDigits(paramStringBuilder, i / 60).append(':');
/* 430 */         StringUtils.appendTwoDigits(paramStringBuilder, i % 60);
/*     */         break;
/*     */       
/*     */       case DAY_TO_SECOND:
/* 434 */         l = paramLong2 % 60000000000L;
/* 435 */         j = (int)(paramLong2 / 60000000000L);
/* 436 */         paramStringBuilder.append(paramLong1).append(' ');
/* 437 */         StringUtils.appendTwoDigits(paramStringBuilder, j / 60).append(':');
/* 438 */         StringUtils.appendTwoDigits(paramStringBuilder, j % 60).append(':');
/* 439 */         StringUtils.appendTwoDigits(paramStringBuilder, (int)(l / 1000000000L));
/* 440 */         DateTimeUtils.appendNanos(paramStringBuilder, (int)(l % 1000000000L));
/*     */         break;
/*     */       
/*     */       case HOUR_TO_MINUTE:
/* 444 */         paramStringBuilder.append(paramLong1).append(':');
/* 445 */         StringUtils.appendTwoDigits(paramStringBuilder, (int)paramLong2);
/*     */         break;
/*     */       case HOUR_TO_SECOND:
/* 448 */         paramStringBuilder.append(paramLong1).append(':');
/* 449 */         StringUtils.appendTwoDigits(paramStringBuilder, (int)(paramLong2 / 60000000000L)).append(':');
/* 450 */         l = paramLong2 % 60000000000L;
/* 451 */         StringUtils.appendTwoDigits(paramStringBuilder, (int)(l / 1000000000L));
/* 452 */         DateTimeUtils.appendNanos(paramStringBuilder, (int)(l % 1000000000L));
/*     */         break;
/*     */       
/*     */       case MINUTE_TO_SECOND:
/* 456 */         paramStringBuilder.append(paramLong1).append(':');
/* 457 */         StringUtils.appendTwoDigits(paramStringBuilder, (int)(paramLong2 / 1000000000L));
/* 458 */         DateTimeUtils.appendNanos(paramStringBuilder, (int)(paramLong2 % 1000000000L));
/*     */         break;
/*     */     } 
/* 461 */     return paramStringBuilder.append("' ").append(paramIntervalQualifier);
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
/*     */   public static BigInteger intervalToAbsolute(ValueInterval paramValueInterval) {
/*     */     BigInteger bigInteger;
/* 474 */     switch (paramValueInterval.getQualifier()) {
/*     */       case YEAR:
/* 476 */         bigInteger = BigInteger.valueOf(paramValueInterval.getLeading()).multiply(MONTHS_PER_YEAR_BI);
/*     */         break;
/*     */       case MONTH:
/* 479 */         bigInteger = BigInteger.valueOf(paramValueInterval.getLeading());
/*     */         break;
/*     */       case DAY:
/* 482 */         bigInteger = BigInteger.valueOf(paramValueInterval.getLeading()).multiply(NANOS_PER_DAY_BI);
/*     */         break;
/*     */       case HOUR:
/* 485 */         bigInteger = BigInteger.valueOf(paramValueInterval.getLeading()).multiply(NANOS_PER_HOUR_BI);
/*     */         break;
/*     */       case MINUTE:
/* 488 */         bigInteger = BigInteger.valueOf(paramValueInterval.getLeading()).multiply(NANOS_PER_MINUTE_BI);
/*     */         break;
/*     */       case SECOND:
/* 491 */         bigInteger = intervalToAbsolute(paramValueInterval, NANOS_PER_SECOND_BI);
/*     */         break;
/*     */       case YEAR_TO_MONTH:
/* 494 */         bigInteger = intervalToAbsolute(paramValueInterval, MONTHS_PER_YEAR_BI);
/*     */         break;
/*     */       case DAY_TO_HOUR:
/* 497 */         bigInteger = intervalToAbsolute(paramValueInterval, HOURS_PER_DAY_BI, NANOS_PER_HOUR_BI);
/*     */         break;
/*     */       case DAY_TO_MINUTE:
/* 500 */         bigInteger = intervalToAbsolute(paramValueInterval, MINUTES_PER_DAY_BI, NANOS_PER_MINUTE_BI);
/*     */         break;
/*     */       case DAY_TO_SECOND:
/* 503 */         bigInteger = intervalToAbsolute(paramValueInterval, NANOS_PER_DAY_BI);
/*     */         break;
/*     */       case HOUR_TO_MINUTE:
/* 506 */         bigInteger = intervalToAbsolute(paramValueInterval, MINUTES_PER_HOUR_BI, NANOS_PER_MINUTE_BI);
/*     */         break;
/*     */       case HOUR_TO_SECOND:
/* 509 */         bigInteger = intervalToAbsolute(paramValueInterval, NANOS_PER_HOUR_BI);
/*     */         break;
/*     */       case MINUTE_TO_SECOND:
/* 512 */         bigInteger = intervalToAbsolute(paramValueInterval, NANOS_PER_MINUTE_BI);
/*     */         break;
/*     */       default:
/* 515 */         throw new IllegalArgumentException();
/*     */     } 
/* 517 */     return paramValueInterval.isNegative() ? bigInteger.negate() : bigInteger;
/*     */   }
/*     */ 
/*     */   
/*     */   private static BigInteger intervalToAbsolute(ValueInterval paramValueInterval, BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
/* 522 */     return intervalToAbsolute(paramValueInterval, paramBigInteger1).multiply(paramBigInteger2);
/*     */   }
/*     */   
/*     */   private static BigInteger intervalToAbsolute(ValueInterval paramValueInterval, BigInteger paramBigInteger) {
/* 526 */     return BigInteger.valueOf(paramValueInterval.getLeading()).multiply(paramBigInteger)
/* 527 */       .add(BigInteger.valueOf(paramValueInterval.getRemaining()));
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
/*     */   public static ValueInterval intervalFromAbsolute(IntervalQualifier paramIntervalQualifier, BigInteger paramBigInteger) {
/* 541 */     switch (paramIntervalQualifier) {
/*     */       case YEAR:
/* 543 */         return ValueInterval.from(paramIntervalQualifier, (paramBigInteger.signum() < 0), 
/* 544 */             leadingExact(paramBigInteger.divide(MONTHS_PER_YEAR_BI)), 0L);
/*     */       case MONTH:
/* 546 */         return ValueInterval.from(paramIntervalQualifier, (paramBigInteger.signum() < 0), leadingExact(paramBigInteger), 0L);
/*     */       case DAY:
/* 548 */         return ValueInterval.from(paramIntervalQualifier, (paramBigInteger.signum() < 0), 
/* 549 */             leadingExact(paramBigInteger.divide(NANOS_PER_DAY_BI)), 0L);
/*     */       case HOUR:
/* 551 */         return ValueInterval.from(paramIntervalQualifier, (paramBigInteger.signum() < 0), 
/* 552 */             leadingExact(paramBigInteger.divide(NANOS_PER_HOUR_BI)), 0L);
/*     */       case MINUTE:
/* 554 */         return ValueInterval.from(paramIntervalQualifier, (paramBigInteger.signum() < 0), 
/* 555 */             leadingExact(paramBigInteger.divide(NANOS_PER_MINUTE_BI)), 0L);
/*     */       case SECOND:
/* 557 */         return intervalFromAbsolute(paramIntervalQualifier, paramBigInteger, NANOS_PER_SECOND_BI);
/*     */       case YEAR_TO_MONTH:
/* 559 */         return intervalFromAbsolute(paramIntervalQualifier, paramBigInteger, MONTHS_PER_YEAR_BI);
/*     */       case DAY_TO_HOUR:
/* 561 */         return intervalFromAbsolute(paramIntervalQualifier, paramBigInteger.divide(NANOS_PER_HOUR_BI), HOURS_PER_DAY_BI);
/*     */       case DAY_TO_MINUTE:
/* 563 */         return intervalFromAbsolute(paramIntervalQualifier, paramBigInteger.divide(NANOS_PER_MINUTE_BI), MINUTES_PER_DAY_BI);
/*     */       case DAY_TO_SECOND:
/* 565 */         return intervalFromAbsolute(paramIntervalQualifier, paramBigInteger, NANOS_PER_DAY_BI);
/*     */       case HOUR_TO_MINUTE:
/* 567 */         return intervalFromAbsolute(paramIntervalQualifier, paramBigInteger.divide(NANOS_PER_MINUTE_BI), MINUTES_PER_HOUR_BI);
/*     */       case HOUR_TO_SECOND:
/* 569 */         return intervalFromAbsolute(paramIntervalQualifier, paramBigInteger, NANOS_PER_HOUR_BI);
/*     */       case MINUTE_TO_SECOND:
/* 571 */         return intervalFromAbsolute(paramIntervalQualifier, paramBigInteger, NANOS_PER_MINUTE_BI);
/*     */     } 
/* 573 */     throw new IllegalArgumentException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static ValueInterval intervalFromAbsolute(IntervalQualifier paramIntervalQualifier, BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
/* 579 */     BigInteger[] arrayOfBigInteger = paramBigInteger1.divideAndRemainder(paramBigInteger2);
/* 580 */     return ValueInterval.from(paramIntervalQualifier, (paramBigInteger1.signum() < 0), leadingExact(arrayOfBigInteger[0]), Math.abs(arrayOfBigInteger[1].longValue()));
/*     */   }
/*     */   
/*     */   private static long leadingExact(BigInteger paramBigInteger) {
/* 584 */     if (paramBigInteger.compareTo(LEADING_MAX) > 0 || paramBigInteger.compareTo(LEADING_MIN) < 0) {
/* 585 */       throw DbException.get(22003, paramBigInteger.toString());
/*     */     }
/* 587 */     return Math.abs(paramBigInteger.longValue());
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
/*     */   public static boolean validateInterval(IntervalQualifier paramIntervalQualifier, boolean paramBoolean, long paramLong1, long paramLong2) {
/*     */     long l;
/* 605 */     if (paramIntervalQualifier == null) {
/* 606 */       throw new NullPointerException();
/*     */     }
/* 608 */     if (paramLong1 == 0L && paramLong2 == 0L) {
/* 609 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 613 */     switch (paramIntervalQualifier) {
/*     */       case YEAR:
/*     */       case MONTH:
/*     */       case DAY:
/*     */       case HOUR:
/*     */       case MINUTE:
/* 619 */         l = 1L;
/*     */         break;
/*     */       case SECOND:
/* 622 */         l = 1000000000L;
/*     */         break;
/*     */       case YEAR_TO_MONTH:
/* 625 */         l = 12L;
/*     */         break;
/*     */       case DAY_TO_HOUR:
/* 628 */         l = 24L;
/*     */         break;
/*     */       case DAY_TO_MINUTE:
/* 631 */         l = 1440L;
/*     */         break;
/*     */       case DAY_TO_SECOND:
/* 634 */         l = 86400000000000L;
/*     */         break;
/*     */       case HOUR_TO_MINUTE:
/* 637 */         l = 60L;
/*     */         break;
/*     */       case HOUR_TO_SECOND:
/* 640 */         l = 3600000000000L;
/*     */         break;
/*     */       case MINUTE_TO_SECOND:
/* 643 */         l = 60000000000L;
/*     */         break;
/*     */       default:
/* 646 */         throw DbException.getInvalidValueException("interval", paramIntervalQualifier);
/*     */     } 
/* 648 */     if (paramLong1 < 0L || paramLong1 >= 1000000000000000000L) {
/* 649 */       throw DbException.getInvalidValueException("interval", Long.toString(paramLong1));
/*     */     }
/* 651 */     if (paramLong2 < 0L || paramLong2 >= l) {
/* 652 */       throw DbException.getInvalidValueException("interval", Long.toString(paramLong2));
/*     */     }
/* 654 */     return paramBoolean;
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
/*     */   public static long yearsFromInterval(IntervalQualifier paramIntervalQualifier, boolean paramBoolean, long paramLong1, long paramLong2) {
/* 671 */     if (paramIntervalQualifier == IntervalQualifier.YEAR || paramIntervalQualifier == IntervalQualifier.YEAR_TO_MONTH) {
/* 672 */       long l = paramLong1;
/* 673 */       if (paramBoolean) {
/* 674 */         l = -l;
/*     */       }
/* 676 */       return l;
/*     */     } 
/* 678 */     return 0L;
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
/*     */   public static long monthsFromInterval(IntervalQualifier paramIntervalQualifier, boolean paramBoolean, long paramLong1, long paramLong2) {
/*     */     long l;
/* 698 */     if (paramIntervalQualifier == IntervalQualifier.MONTH) {
/* 699 */       l = paramLong1;
/* 700 */     } else if (paramIntervalQualifier == IntervalQualifier.YEAR_TO_MONTH) {
/* 701 */       l = paramLong2;
/*     */     } else {
/* 703 */       return 0L;
/*     */     } 
/* 705 */     if (paramBoolean) {
/* 706 */       l = -l;
/*     */     }
/* 708 */     return l;
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
/*     */   public static long daysFromInterval(IntervalQualifier paramIntervalQualifier, boolean paramBoolean, long paramLong1, long paramLong2) {
/*     */     long l;
/* 725 */     switch (paramIntervalQualifier) {
/*     */       case DAY:
/*     */       case DAY_TO_HOUR:
/*     */       case DAY_TO_MINUTE:
/*     */       case DAY_TO_SECOND:
/* 730 */         l = paramLong1;
/* 731 */         if (paramBoolean) {
/* 732 */           l = -l;
/*     */         }
/* 734 */         return l;
/*     */     } 
/* 736 */     return 0L;
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
/*     */   public static long hoursFromInterval(IntervalQualifier paramIntervalQualifier, boolean paramBoolean, long paramLong1, long paramLong2) {
/*     */     long l;
/* 755 */     switch (paramIntervalQualifier) {
/*     */       case HOUR:
/*     */       case HOUR_TO_MINUTE:
/*     */       case HOUR_TO_SECOND:
/* 759 */         l = paramLong1;
/*     */         break;
/*     */       case DAY_TO_HOUR:
/* 762 */         l = paramLong2;
/*     */         break;
/*     */       case DAY_TO_MINUTE:
/* 765 */         l = paramLong2 / 60L;
/*     */         break;
/*     */       case DAY_TO_SECOND:
/* 768 */         l = paramLong2 / 3600000000000L;
/*     */         break;
/*     */       default:
/* 771 */         return 0L;
/*     */     } 
/* 773 */     if (paramBoolean) {
/* 774 */       l = -l;
/*     */     }
/* 776 */     return l;
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
/*     */   public static long minutesFromInterval(IntervalQualifier paramIntervalQualifier, boolean paramBoolean, long paramLong1, long paramLong2) {
/*     */     long l;
/* 795 */     switch (paramIntervalQualifier) {
/*     */       case MINUTE:
/*     */       case MINUTE_TO_SECOND:
/* 798 */         l = paramLong1;
/*     */         break;
/*     */       case DAY_TO_MINUTE:
/* 801 */         l = paramLong2 % 60L;
/*     */         break;
/*     */       case DAY_TO_SECOND:
/* 804 */         l = paramLong2 / 60000000000L % 60L;
/*     */         break;
/*     */       case HOUR_TO_MINUTE:
/* 807 */         l = paramLong2;
/*     */         break;
/*     */       case HOUR_TO_SECOND:
/* 810 */         l = paramLong2 / 60000000000L;
/*     */         break;
/*     */       default:
/* 813 */         return 0L;
/*     */     } 
/* 815 */     if (paramBoolean) {
/* 816 */       l = -l;
/*     */     }
/* 818 */     return l;
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
/*     */   public static long nanosFromInterval(IntervalQualifier paramIntervalQualifier, boolean paramBoolean, long paramLong1, long paramLong2) {
/*     */     long l;
/* 836 */     switch (paramIntervalQualifier) {
/*     */       case SECOND:
/* 838 */         l = paramLong1 * 1000000000L + paramLong2;
/*     */         break;
/*     */       case DAY_TO_SECOND:
/*     */       case HOUR_TO_SECOND:
/* 842 */         l = paramLong2 % 60000000000L;
/*     */         break;
/*     */       case MINUTE_TO_SECOND:
/* 845 */         l = paramLong2;
/*     */         break;
/*     */       default:
/* 848 */         return 0L;
/*     */     } 
/* 850 */     if (paramBoolean) {
/* 851 */       l = -l;
/*     */     }
/* 853 */     return l;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\IntervalUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */