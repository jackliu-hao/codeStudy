/*      */ package org.h2.util;
/*      */ 
/*      */ import java.time.Instant;
/*      */ import org.h2.engine.CastDataProvider;
/*      */ import org.h2.message.DbException;
/*      */ import org.h2.value.TypeInfo;
/*      */ import org.h2.value.Value;
/*      */ import org.h2.value.ValueDate;
/*      */ import org.h2.value.ValueTime;
/*      */ import org.h2.value.ValueTimeTimeZone;
/*      */ import org.h2.value.ValueTimestamp;
/*      */ import org.h2.value.ValueTimestampTimeZone;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class DateTimeUtils
/*      */ {
/*      */   public static final long MILLIS_PER_DAY = 86400000L;
/*      */   public static final long SECONDS_PER_DAY = 86400L;
/*      */   public static final long NANOS_PER_SECOND = 1000000000L;
/*      */   public static final long NANOS_PER_MINUTE = 60000000000L;
/*      */   public static final long NANOS_PER_HOUR = 3600000000000L;
/*      */   public static final long NANOS_PER_DAY = 86400000000000L;
/*      */   public static final int SHIFT_YEAR = 9;
/*      */   public static final int SHIFT_MONTH = 5;
/*      */   public static final int EPOCH_DATE_VALUE = 1008673;
/*      */   public static final long MIN_DATE_VALUE = -511999999967L;
/*      */   public static final long MAX_DATE_VALUE = 512000000415L;
/*   86 */   private static final int[] NORMAL_DAYS_PER_MONTH = new int[] { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   92 */   private static final int[] FRACTIONAL_SECONDS_TABLE = new int[] { 1000000000, 100000000, 10000000, 1000000, 100000, 10000, 1000, 100, 10, 1 };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static volatile TimeZoneProvider LOCAL;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void resetCalendar() {
/*  106 */     LOCAL = null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static TimeZoneProvider getTimeZone() {
/*  115 */     TimeZoneProvider timeZoneProvider = LOCAL;
/*  116 */     if (timeZoneProvider == null) {
/*  117 */       LOCAL = timeZoneProvider = TimeZoneProvider.getDefault();
/*      */     }
/*  119 */     return timeZoneProvider;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ValueTimestampTimeZone currentTimestamp(TimeZoneProvider paramTimeZoneProvider) {
/*  130 */     return currentTimestamp(paramTimeZoneProvider, Instant.now());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ValueTimestampTimeZone currentTimestamp(TimeZoneProvider paramTimeZoneProvider, Instant paramInstant) {
/*  148 */     long l = paramInstant.getEpochSecond();
/*  149 */     int i = paramTimeZoneProvider.getTimeZoneOffsetUTC(l);
/*  150 */     l += i;
/*  151 */     return ValueTimestampTimeZone.fromDateValueAndNanos(dateValueFromAbsoluteDay(l / 86400L), l % 86400L * 1000000000L + paramInstant
/*  152 */         .getNano(), i);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long parseDateValue(String paramString, int paramInt1, int paramInt2) {
/*      */     int j, k, m;
/*  166 */     if (paramString.charAt(paramInt1) == '+')
/*      */     {
/*  168 */       paramInt1++;
/*      */     }
/*      */     
/*  171 */     int i = paramString.indexOf('-', paramInt1 + 1);
/*      */     
/*  173 */     if (i > 0) {
/*      */       
/*  175 */       j = i + 1;
/*  176 */       k = paramString.indexOf('-', j);
/*  177 */       if (k <= j) {
/*  178 */         throw new IllegalArgumentException(paramString);
/*      */       }
/*  180 */       m = k + 1;
/*      */     } else {
/*      */       
/*  183 */       k = m = paramInt2 - 2;
/*  184 */       i = j = k - 2;
/*      */       
/*  186 */       if (i < paramInt1 + 3) {
/*  187 */         throw new IllegalArgumentException(paramString);
/*      */       }
/*      */     } 
/*  190 */     int n = Integer.parseInt(paramString.substring(paramInt1, i));
/*  191 */     int i1 = StringUtils.parseUInt31(paramString, j, k);
/*  192 */     int i2 = StringUtils.parseUInt31(paramString, m, paramInt2);
/*  193 */     if (!isValidDate(n, i1, i2)) {
/*  194 */       throw new IllegalArgumentException(n + "-" + i1 + "-" + i2);
/*      */     }
/*  196 */     return dateValue(n, i1, i2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long parseTimeNanos(String paramString, int paramInt1, int paramInt2) {
/*      */     byte b;
/*      */     boolean bool;
/*  211 */     int m, n, i1, i2, k = paramString.indexOf(':', paramInt1);
/*      */     
/*  213 */     if (k > 0) {
/*  214 */       m = k + 1;
/*  215 */       n = paramString.indexOf(':', m);
/*  216 */       if (n >= m) {
/*      */         
/*  218 */         i1 = n + 1;
/*  219 */         i2 = paramString.indexOf('.', i1);
/*      */       } else {
/*      */         
/*  222 */         n = paramInt2;
/*  223 */         i1 = i2 = -1;
/*      */       } 
/*      */     } else {
/*  226 */       int i3 = paramString.indexOf('.', paramInt1);
/*  227 */       if (i3 < 0) {
/*      */         
/*  229 */         k = m = paramInt1 + 2;
/*  230 */         n = m + 2;
/*  231 */         int i4 = paramInt2 - paramInt1;
/*  232 */         if (i4 == 6) {
/*  233 */           i1 = n;
/*  234 */           i2 = -1;
/*  235 */         } else if (i4 == 4) {
/*  236 */           i1 = i2 = -1;
/*      */         } else {
/*  238 */           throw new IllegalArgumentException(paramString);
/*      */         } 
/*  240 */       } else if (i3 >= paramInt1 + 6) {
/*      */         
/*  242 */         if (i3 - paramInt1 != 6) {
/*  243 */           throw new IllegalArgumentException(paramString);
/*      */         }
/*  245 */         k = m = paramInt1 + 2;
/*  246 */         n = i1 = m + 2;
/*  247 */         i2 = i3;
/*      */       } else {
/*      */         
/*  250 */         k = i3;
/*  251 */         m = k + 1;
/*  252 */         n = paramString.indexOf('.', m);
/*  253 */         if (n <= m) {
/*  254 */           throw new IllegalArgumentException(paramString);
/*      */         }
/*  256 */         i1 = n + 1;
/*  257 */         i2 = paramString.indexOf('.', i1);
/*      */       } 
/*      */     } 
/*  260 */     int i = StringUtils.parseUInt31(paramString, paramInt1, k);
/*  261 */     if (i >= 24) {
/*  262 */       throw new IllegalArgumentException(paramString);
/*      */     }
/*  264 */     int j = StringUtils.parseUInt31(paramString, m, n);
/*  265 */     if (i1 > 0) {
/*  266 */       if (i2 < 0) {
/*  267 */         b = StringUtils.parseUInt31(paramString, i1, paramInt2);
/*  268 */         bool = false;
/*      */       } else {
/*  270 */         b = StringUtils.parseUInt31(paramString, i1, i2);
/*  271 */         bool = parseNanos(paramString, i2 + 1, paramInt2);
/*      */       } 
/*      */     } else {
/*  274 */       b = bool = false;
/*      */     } 
/*  276 */     if (j >= 60 || b >= 60) {
/*  277 */       throw new IllegalArgumentException(paramString);
/*      */     }
/*  279 */     return ((i * 60L + j) * 60L + b) * 1000000000L + bool;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static int parseNanos(String paramString, int paramInt1, int paramInt2) {
/*  291 */     if (paramInt1 >= paramInt2) {
/*  292 */       throw new IllegalArgumentException(paramString);
/*      */     }
/*  294 */     int i = 0, j = 100000000;
/*      */     while (true) {
/*  296 */       char c = paramString.charAt(paramInt1);
/*  297 */       if (c < '0' || c > '9') {
/*  298 */         throw new IllegalArgumentException(paramString);
/*      */       }
/*  300 */       i += j * (c - 48);
/*      */ 
/*      */       
/*  303 */       j /= 10;
/*  304 */       if (++paramInt1 >= paramInt2) {
/*  305 */         return i;
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Value parseTimestamp(String paramString, CastDataProvider paramCastDataProvider, boolean paramBoolean) {
/*      */     int j;
/*      */     long l2;
/*  322 */     int i = paramString.indexOf(' ');
/*  323 */     if (i < 0) {
/*      */       
/*  325 */       i = paramString.indexOf('T');
/*  326 */       if (i < 0 && paramCastDataProvider != null && (paramCastDataProvider.getMode()).allowDB2TimestampFormat)
/*      */       {
/*  328 */         i = paramString.indexOf('-', paramString.indexOf('-', paramString.indexOf('-') + 1) + 1);
/*      */       }
/*      */     } 
/*      */     
/*  332 */     if (i < 0) {
/*  333 */       i = paramString.length();
/*  334 */       j = -1;
/*      */     } else {
/*  336 */       j = i + 1;
/*      */     } 
/*  338 */     long l1 = parseDateValue(paramString, 0, i);
/*      */     
/*  340 */     TimeZoneProvider timeZoneProvider = null;
/*  341 */     if (j < 0) {
/*  342 */       l2 = 0L;
/*      */     } else {
/*  344 */       int k; i++;
/*      */       
/*  346 */       if (paramString.endsWith("Z")) {
/*  347 */         timeZoneProvider = TimeZoneProvider.UTC;
/*  348 */         k = paramString.length() - 1;
/*      */       } else {
/*  350 */         int m = paramString.indexOf('+', i);
/*  351 */         if (m < 0) {
/*  352 */           m = paramString.indexOf('-', i);
/*      */         }
/*  354 */         if (m >= 0) {
/*      */           
/*  356 */           int n = paramString.indexOf('[', m + 1);
/*  357 */           if (n < 0) {
/*  358 */             n = paramString.length();
/*      */           }
/*  360 */           timeZoneProvider = TimeZoneProvider.ofId(paramString.substring(m, n));
/*  361 */           if (paramString.charAt(m - 1) == ' ') {
/*  362 */             m--;
/*      */           }
/*  364 */           k = m;
/*      */         } else {
/*  366 */           m = paramString.indexOf(' ', i);
/*  367 */           if (m > 0) {
/*  368 */             timeZoneProvider = TimeZoneProvider.ofId(paramString.substring(m + 1));
/*  369 */             k = m;
/*      */           } else {
/*  371 */             k = paramString.length();
/*      */           } 
/*      */         } 
/*      */       } 
/*  375 */       l2 = parseTimeNanos(paramString, i, k);
/*      */     } 
/*  377 */     if (paramBoolean) {
/*      */       boolean bool;
/*  379 */       if (timeZoneProvider == null) {
/*  380 */         timeZoneProvider = (paramCastDataProvider != null) ? paramCastDataProvider.currentTimeZone() : getTimeZone();
/*      */       }
/*  382 */       if (timeZoneProvider != TimeZoneProvider.UTC) {
/*  383 */         bool = timeZoneProvider.getTimeZoneOffsetUTC(timeZoneProvider.getEpochSecondsFromLocal(l1, l2));
/*      */       } else {
/*  385 */         bool = false;
/*      */       } 
/*  387 */       return (Value)ValueTimestampTimeZone.fromDateValueAndNanos(l1, l2, bool);
/*  388 */     }  if (timeZoneProvider != null) {
/*  389 */       long l = timeZoneProvider.getEpochSecondsFromLocal(l1, l2);
/*  390 */       l += ((paramCastDataProvider != null) ? paramCastDataProvider.currentTimeZone() : getTimeZone())
/*  391 */         .getTimeZoneOffsetUTC(l);
/*  392 */       l1 = dateValueFromLocalSeconds(l);
/*  393 */       l2 = l2 % 1000000000L + nanosFromLocalSeconds(l);
/*      */     } 
/*  395 */     return (Value)ValueTimestamp.fromDateValueAndNanos(l1, l2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ValueTimeTimeZone parseTimeWithTimeZone(String paramString, CastDataProvider paramCastDataProvider) {
/*      */     int i;
/*      */     TimeZoneProvider timeZoneProvider;
/*  410 */     if (paramString.endsWith("Z")) {
/*  411 */       timeZoneProvider = TimeZoneProvider.UTC;
/*  412 */       i = paramString.length() - 1;
/*      */     } else {
/*  414 */       int j = paramString.indexOf('+', 1);
/*  415 */       if (j < 0) {
/*  416 */         j = paramString.indexOf('-', 1);
/*      */       }
/*  418 */       if (j >= 0) {
/*  419 */         timeZoneProvider = TimeZoneProvider.ofId(paramString.substring(j));
/*  420 */         if (paramString.charAt(j - 1) == ' ') {
/*  421 */           j--;
/*      */         }
/*  423 */         i = j;
/*      */       } else {
/*  425 */         j = paramString.indexOf(' ', 1);
/*  426 */         if (j > 0) {
/*  427 */           timeZoneProvider = TimeZoneProvider.ofId(paramString.substring(j + 1));
/*  428 */           i = j;
/*      */         } else {
/*  430 */           throw DbException.get(22007, new String[] { "TIME WITH TIME ZONE", paramString });
/*      */         } 
/*      */       } 
/*  433 */       if (!timeZoneProvider.hasFixedOffset()) {
/*  434 */         throw DbException.get(22007, new String[] { "TIME WITH TIME ZONE", paramString });
/*      */       }
/*      */     } 
/*  437 */     return ValueTimeTimeZone.fromNanos(parseTimeNanos(paramString, 0, i), timeZoneProvider.getTimeZoneOffsetUTC(0L));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long getEpochSeconds(long paramLong1, long paramLong2, int paramInt) {
/*  452 */     return absoluteDayFromDateValue(paramLong1) * 86400L + paramLong2 / 1000000000L - paramInt;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long[] dateAndTimeFromValue(Value paramValue, CastDataProvider paramCastDataProvider) {
/*  465 */     long l1 = 1008673L;
/*  466 */     long l2 = 0L;
/*  467 */     if (paramValue instanceof ValueTimestamp) {
/*  468 */       ValueTimestamp valueTimestamp = (ValueTimestamp)paramValue;
/*  469 */       l1 = valueTimestamp.getDateValue();
/*  470 */       l2 = valueTimestamp.getTimeNanos();
/*  471 */     } else if (paramValue instanceof ValueDate) {
/*  472 */       l1 = ((ValueDate)paramValue).getDateValue();
/*  473 */     } else if (paramValue instanceof ValueTime) {
/*  474 */       l2 = ((ValueTime)paramValue).getNanos();
/*  475 */     } else if (paramValue instanceof ValueTimestampTimeZone) {
/*  476 */       ValueTimestampTimeZone valueTimestampTimeZone = (ValueTimestampTimeZone)paramValue;
/*  477 */       l1 = valueTimestampTimeZone.getDateValue();
/*  478 */       l2 = valueTimestampTimeZone.getTimeNanos();
/*  479 */     } else if (paramValue instanceof ValueTimeTimeZone) {
/*  480 */       l2 = ((ValueTimeTimeZone)paramValue).getNanos();
/*      */     } else {
/*  482 */       ValueTimestamp valueTimestamp = (ValueTimestamp)paramValue.convertTo(TypeInfo.TYPE_TIMESTAMP, paramCastDataProvider);
/*  483 */       l1 = valueTimestamp.getDateValue();
/*  484 */       l2 = valueTimestamp.getTimeNanos();
/*      */     } 
/*  486 */     return new long[] { l1, l2 };
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Value dateTimeToValue(Value paramValue, long paramLong1, long paramLong2) {
/*  503 */     switch (paramValue.getValueType())
/*      */     { case 17:
/*  505 */         return (Value)ValueDate.fromDateValue(paramLong1);
/*      */       case 18:
/*  507 */         return (Value)ValueTime.fromNanos(paramLong2);
/*      */       case 19:
/*  509 */         return (Value)ValueTimeTimeZone.fromNanos(paramLong2, ((ValueTimeTimeZone)paramValue).getTimeZoneOffsetSeconds());
/*      */       
/*      */       default:
/*  512 */         return (Value)ValueTimestamp.fromDateValueAndNanos(paramLong1, paramLong2);
/*      */       case 21:
/*  514 */         break; }  return (Value)ValueTimestampTimeZone.fromDateValueAndNanos(paramLong1, paramLong2, ((ValueTimestampTimeZone)paramValue)
/*  515 */         .getTimeZoneOffsetSeconds());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int getDayOfWeek(long paramLong, int paramInt) {
/*  530 */     return getDayOfWeekFromAbsolute(absoluteDayFromDateValue(paramLong), paramInt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int getDayOfWeekFromAbsolute(long paramLong, int paramInt) {
/*  541 */     return (paramLong >= 0L) ? ((int)((paramLong - paramInt + 11L) % 7L) + 1) : ((int)((paramLong - paramInt - 2L) % 7L) + 7);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int getDayOfYear(long paramLong) {
/*  553 */     int i = monthFromDateValue(paramLong);
/*  554 */     int j = (367 * i - 362) / 12 + dayFromDateValue(paramLong);
/*  555 */     if (i > 2) {
/*  556 */       j--;
/*  557 */       long l = yearFromDateValue(paramLong);
/*  558 */       if ((l & 0x3L) != 0L || (l % 100L == 0L && l % 400L != 0L)) {
/*  559 */         j--;
/*      */       }
/*      */     } 
/*  562 */     return j;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int getIsoDayOfWeek(long paramLong) {
/*  574 */     return getDayOfWeek(paramLong, 1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int getIsoWeekOfYear(long paramLong) {
/*  587 */     return getWeekOfYear(paramLong, 1, 4);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int getIsoWeekYear(long paramLong) {
/*  600 */     return getWeekYear(paramLong, 1, 4);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int getSundayDayOfWeek(long paramLong) {
/*  612 */     return getDayOfWeek(paramLong, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int getWeekOfYear(long paramLong, int paramInt1, int paramInt2) {
/*  628 */     long l1 = absoluteDayFromDateValue(paramLong);
/*  629 */     int i = yearFromDateValue(paramLong);
/*  630 */     long l2 = getWeekYearAbsoluteStart(i, paramInt1, paramInt2);
/*  631 */     if (l1 - l2 < 0L) {
/*  632 */       l2 = getWeekYearAbsoluteStart(i - 1, paramInt1, paramInt2);
/*  633 */     } else if (monthFromDateValue(paramLong) == 12 && 24 + paramInt2 < dayFromDateValue(paramLong) && 
/*  634 */       l1 >= getWeekYearAbsoluteStart(i + 1, paramInt1, paramInt2)) {
/*  635 */       return 1;
/*      */     } 
/*      */     
/*  638 */     return (int)((l1 - l2) / 7L) + 1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long getWeekYearAbsoluteStart(int paramInt1, int paramInt2, int paramInt3) {
/*  653 */     long l1 = absoluteDayFromYear(paramInt1);
/*  654 */     int i = 8 - getDayOfWeekFromAbsolute(l1, paramInt2);
/*  655 */     long l2 = l1 + i;
/*  656 */     if (i >= paramInt3) {
/*  657 */       l2 -= 7L;
/*      */     }
/*  659 */     return l2;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int getWeekYear(long paramLong, int paramInt1, int paramInt2) {
/*  675 */     long l1 = absoluteDayFromDateValue(paramLong);
/*  676 */     int i = yearFromDateValue(paramLong);
/*  677 */     long l2 = getWeekYearAbsoluteStart(i, paramInt1, paramInt2);
/*  678 */     if (l1 < l2)
/*  679 */       return i - 1; 
/*  680 */     if (monthFromDateValue(paramLong) == 12 && 24 + paramInt2 < dayFromDateValue(paramLong) && 
/*  681 */       l1 >= getWeekYearAbsoluteStart(i + 1, paramInt1, paramInt2)) {
/*  682 */       return i + 1;
/*      */     }
/*      */     
/*  685 */     return i;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int getDaysInMonth(int paramInt1, int paramInt2) {
/*  696 */     if (paramInt2 != 2) {
/*  697 */       return NORMAL_DAYS_PER_MONTH[paramInt2];
/*      */     }
/*  699 */     return ((paramInt1 & 0x3) == 0 && (paramInt1 % 100 != 0 || paramInt1 % 400 == 0)) ? 29 : 28;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isValidDate(int paramInt1, int paramInt2, int paramInt3) {
/*  711 */     return (paramInt2 >= 1 && paramInt2 <= 12 && paramInt3 >= 1 && paramInt3 <= getDaysInMonth(paramInt1, paramInt2));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int yearFromDateValue(long paramLong) {
/*  721 */     return (int)(paramLong >>> 9L);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int monthFromDateValue(long paramLong) {
/*  731 */     return (int)(paramLong >>> 5L) & 0xF;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int dayFromDateValue(long paramLong) {
/*  741 */     return (int)(paramLong & 0x1FL);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long dateValue(long paramLong, int paramInt1, int paramInt2) {
/*  753 */     return paramLong << 9L | (paramInt1 << 5) | paramInt2;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long dateValueFromDenormalizedDate(long paramLong1, long paramLong2, int paramInt) {
/*  770 */     long l1 = paramLong2 - 1L;
/*  771 */     long l2 = l1 / 12L;
/*  772 */     if (l1 < 0L && l2 * 12L != l1) {
/*  773 */       l2--;
/*      */     }
/*  775 */     int i = (int)(paramLong1 + l2);
/*  776 */     int j = (int)(paramLong2 - l2 * 12L);
/*  777 */     if (paramInt < 1) {
/*  778 */       paramInt = 1;
/*      */     } else {
/*  780 */       int k = getDaysInMonth(i, j);
/*  781 */       if (paramInt > k) {
/*  782 */         paramInt = k;
/*      */       }
/*      */     } 
/*  785 */     return dateValue(i, j, paramInt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long dateValueFromLocalSeconds(long paramLong) {
/*  795 */     long l = paramLong / 86400L;
/*      */     
/*  797 */     if (paramLong < 0L && l * 86400L != paramLong) {
/*  798 */       l--;
/*      */     }
/*  800 */     return dateValueFromAbsoluteDay(l);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long nanosFromLocalSeconds(long paramLong) {
/*  810 */     paramLong %= 86400L;
/*  811 */     if (paramLong < 0L) {
/*  812 */       paramLong += 86400L;
/*      */     }
/*  814 */     return paramLong * 1000000000L;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long normalizeNanosOfDay(long paramLong) {
/*  824 */     paramLong %= 86400000000000L;
/*  825 */     if (paramLong < 0L) {
/*  826 */       paramLong += 86400000000000L;
/*      */     }
/*  828 */     return paramLong;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long absoluteDayFromYear(long paramLong) {
/*  839 */     long l = 365L * paramLong - 719528L;
/*  840 */     if (paramLong >= 0L) {
/*  841 */       l += (paramLong + 3L) / 4L - (paramLong + 99L) / 100L + (paramLong + 399L) / 400L;
/*      */     } else {
/*  843 */       l -= paramLong / -4L - paramLong / -100L + paramLong / -400L;
/*      */     } 
/*  845 */     return l;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long absoluteDayFromDateValue(long paramLong) {
/*  855 */     return absoluteDay(yearFromDateValue(paramLong), monthFromDateValue(paramLong), dayFromDateValue(paramLong));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static long absoluteDay(long paramLong, int paramInt1, int paramInt2) {
/*  867 */     long l = absoluteDayFromYear(paramLong) + ((367 * paramInt1 - 362) / 12) + paramInt2 - 1L;
/*  868 */     if (paramInt1 > 2) {
/*  869 */       l--;
/*  870 */       if ((paramLong & 0x3L) != 0L || (paramLong % 100L == 0L && paramLong % 400L != 0L)) {
/*  871 */         l--;
/*      */       }
/*      */     } 
/*  874 */     return l;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long dateValueFromAbsoluteDay(long paramLong) {
/*  884 */     long l1 = paramLong + 719468L;
/*  885 */     long l2 = 0L;
/*  886 */     if (l1 < 0L) {
/*  887 */       l2 = (l1 + 1L) / 146097L - 1L;
/*  888 */       l1 -= l2 * 146097L;
/*  889 */       l2 *= 400L;
/*      */     } 
/*  891 */     long l3 = (400L * l1 + 591L) / 146097L;
/*  892 */     int i = (int)(l1 - 365L * l3 + l3 / 4L - l3 / 100L + l3 / 400L);
/*  893 */     if (i < 0) {
/*  894 */       l3--;
/*  895 */       i = (int)(l1 - 365L * l3 + l3 / 4L - l3 / 100L + l3 / 400L);
/*      */     } 
/*  897 */     l3 += l2;
/*  898 */     int j = (i * 5 + 2) / 153;
/*  899 */     i -= (j * 306 + 5) / 10 - 1;
/*  900 */     if (j >= 10) {
/*  901 */       l3++;
/*  902 */       j -= 12;
/*      */     } 
/*  904 */     return dateValue(l3, j + 3, i);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long incrementDateValue(long paramLong) {
/*  915 */     int i = dayFromDateValue(paramLong);
/*  916 */     if (i < 28) {
/*  917 */       return paramLong + 1L;
/*      */     }
/*  919 */     int j = yearFromDateValue(paramLong);
/*  920 */     int k = monthFromDateValue(paramLong);
/*  921 */     if (i < getDaysInMonth(j, k)) {
/*  922 */       return paramLong + 1L;
/*      */     }
/*  924 */     if (k < 12) {
/*  925 */       k++;
/*      */     } else {
/*  927 */       k = 1;
/*  928 */       j++;
/*      */     } 
/*  930 */     return dateValue(j, k, 1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long decrementDateValue(long paramLong) {
/*  941 */     if (dayFromDateValue(paramLong) > 1) {
/*  942 */       return paramLong - 1L;
/*      */     }
/*  944 */     int i = yearFromDateValue(paramLong);
/*  945 */     int j = monthFromDateValue(paramLong);
/*  946 */     if (j > 1) {
/*  947 */       j--;
/*      */     } else {
/*  949 */       j = 12;
/*  950 */       i--;
/*      */     } 
/*  952 */     return dateValue(i, j, getDaysInMonth(i, j));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static StringBuilder appendDate(StringBuilder paramStringBuilder, long paramLong) {
/*  963 */     int i = yearFromDateValue(paramLong);
/*  964 */     if (i < 1000 && i > -1000) {
/*  965 */       if (i < 0) {
/*  966 */         paramStringBuilder.append('-');
/*  967 */         i = -i;
/*      */       } 
/*  969 */       StringUtils.appendZeroPadded(paramStringBuilder, 4, i);
/*      */     } else {
/*  971 */       paramStringBuilder.append(i);
/*      */     } 
/*  973 */     StringUtils.appendTwoDigits(paramStringBuilder.append('-'), monthFromDateValue(paramLong)).append('-');
/*  974 */     return StringUtils.appendTwoDigits(paramStringBuilder, dayFromDateValue(paramLong));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static StringBuilder appendTime(StringBuilder paramStringBuilder, long paramLong) {
/*  985 */     if (paramLong < 0L) {
/*  986 */       paramStringBuilder.append('-');
/*  987 */       paramLong = -paramLong;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  995 */     long l = -paramLong / -1000000000L;
/*  996 */     paramLong -= l * 1000000000L;
/*  997 */     int i = (int)(l / 60L);
/*  998 */     l -= (i * 60);
/*  999 */     int j = i / 60;
/* 1000 */     i -= j * 60;
/* 1001 */     StringUtils.appendTwoDigits(paramStringBuilder, j).append(':');
/* 1002 */     StringUtils.appendTwoDigits(paramStringBuilder, i).append(':');
/* 1003 */     StringUtils.appendTwoDigits(paramStringBuilder, (int)l);
/* 1004 */     return appendNanos(paramStringBuilder, (int)paramLong);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static StringBuilder appendNanos(StringBuilder paramStringBuilder, int paramInt) {
/* 1015 */     if (paramInt > 0) {
/* 1016 */       paramStringBuilder.append('.');
/* 1017 */       for (byte b = 1; paramInt < FRACTIONAL_SECONDS_TABLE[b]; b++) {
/* 1018 */         paramStringBuilder.append('0');
/*      */       }
/* 1020 */       if (paramInt % 1000 == 0) {
/* 1021 */         paramInt /= 1000;
/* 1022 */         if (paramInt % 1000 == 0) {
/* 1023 */           paramInt /= 1000;
/*      */         }
/*      */       } 
/* 1026 */       if (paramInt % 10 == 0) {
/* 1027 */         paramInt /= 10;
/* 1028 */         if (paramInt % 10 == 0) {
/* 1029 */           paramInt /= 10;
/*      */         }
/*      */       } 
/* 1032 */       paramStringBuilder.append(paramInt);
/*      */     } 
/* 1034 */     return paramStringBuilder;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static StringBuilder appendTimeZone(StringBuilder paramStringBuilder, int paramInt) {
/* 1045 */     if (paramInt < 0) {
/* 1046 */       paramStringBuilder.append('-');
/* 1047 */       paramInt = -paramInt;
/*      */     } else {
/* 1049 */       paramStringBuilder.append('+');
/*      */     } 
/* 1051 */     int i = paramInt / 3600;
/* 1052 */     StringUtils.appendTwoDigits(paramStringBuilder, i);
/* 1053 */     paramInt -= i * 3600;
/* 1054 */     if (paramInt != 0) {
/* 1055 */       i = paramInt / 60;
/* 1056 */       StringUtils.appendTwoDigits(paramStringBuilder.append(':'), i);
/* 1057 */       paramInt -= i * 60;
/* 1058 */       if (paramInt != 0) {
/* 1059 */         StringUtils.appendTwoDigits(paramStringBuilder.append(':'), paramInt);
/*      */       }
/*      */     } 
/* 1062 */     return paramStringBuilder;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String timeZoneNameFromOffsetSeconds(int paramInt) {
/* 1073 */     if (paramInt == 0) {
/* 1074 */       return "UTC";
/*      */     }
/* 1076 */     StringBuilder stringBuilder = new StringBuilder(12);
/* 1077 */     stringBuilder.append("GMT");
/* 1078 */     if (paramInt < 0) {
/* 1079 */       stringBuilder.append('-');
/* 1080 */       paramInt = -paramInt;
/*      */     } else {
/* 1082 */       stringBuilder.append('+');
/*      */     } 
/* 1084 */     StringUtils.appendTwoDigits(stringBuilder, paramInt / 3600).append(':');
/* 1085 */     paramInt %= 3600;
/* 1086 */     StringUtils.appendTwoDigits(stringBuilder, paramInt / 60);
/* 1087 */     paramInt %= 60;
/* 1088 */     if (paramInt != 0) {
/* 1089 */       stringBuilder.append(':');
/* 1090 */       StringUtils.appendTwoDigits(stringBuilder, paramInt);
/*      */     } 
/* 1092 */     return stringBuilder.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long convertScale(long paramLong1, int paramInt, long paramLong2) {
/* 1105 */     if (paramInt >= 9) {
/* 1106 */       return paramLong1;
/*      */     }
/* 1108 */     int i = FRACTIONAL_SECONDS_TABLE[paramInt];
/* 1109 */     long l1 = paramLong1 % i;
/* 1110 */     if (l1 >= (i >>> 1)) {
/* 1111 */       paramLong1 += i;
/*      */     }
/* 1113 */     long l2 = paramLong1 - l1;
/* 1114 */     if (l2 >= paramLong2) {
/* 1115 */       l2 = paramLong2 - i;
/*      */     }
/* 1117 */     return l2;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ValueTimestampTimeZone timestampTimeZoneAtOffset(long paramLong1, long paramLong2, int paramInt1, int paramInt2) {
/* 1131 */     paramLong2 += (paramInt2 - paramInt1) * 1000000000L;
/*      */     
/* 1133 */     if (paramLong2 < 0L) {
/* 1134 */       paramLong2 += 86400000000000L;
/* 1135 */       paramLong1 = decrementDateValue(paramLong1);
/* 1136 */       if (paramLong2 < 0L) {
/* 1137 */         paramLong2 += 86400000000000L;
/* 1138 */         paramLong1 = decrementDateValue(paramLong1);
/*      */       } 
/* 1140 */     } else if (paramLong2 >= 86400000000000L) {
/* 1141 */       paramLong2 -= 86400000000000L;
/* 1142 */       paramLong1 = incrementDateValue(paramLong1);
/* 1143 */       if (paramLong2 >= 86400000000000L) {
/* 1144 */         paramLong2 -= 86400000000000L;
/* 1145 */         paramLong1 = incrementDateValue(paramLong1);
/*      */       } 
/*      */     } 
/* 1148 */     return ValueTimestampTimeZone.fromDateValueAndNanos(paramLong1, paramLong2, paramInt2);
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\DateTimeUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */