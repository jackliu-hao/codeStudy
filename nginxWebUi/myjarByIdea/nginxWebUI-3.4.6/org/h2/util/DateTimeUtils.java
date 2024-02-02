package org.h2.util;

import java.time.Instant;
import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueDate;
import org.h2.value.ValueTime;
import org.h2.value.ValueTimeTimeZone;
import org.h2.value.ValueTimestamp;
import org.h2.value.ValueTimestampTimeZone;

public class DateTimeUtils {
   public static final long MILLIS_PER_DAY = 86400000L;
   public static final long SECONDS_PER_DAY = 86400L;
   public static final long NANOS_PER_SECOND = 1000000000L;
   public static final long NANOS_PER_MINUTE = 60000000000L;
   public static final long NANOS_PER_HOUR = 3600000000000L;
   public static final long NANOS_PER_DAY = 86400000000000L;
   public static final int SHIFT_YEAR = 9;
   public static final int SHIFT_MONTH = 5;
   public static final int EPOCH_DATE_VALUE = 1008673;
   public static final long MIN_DATE_VALUE = -511999999967L;
   public static final long MAX_DATE_VALUE = 512000000415L;
   private static final int[] NORMAL_DAYS_PER_MONTH = new int[]{0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
   private static final int[] FRACTIONAL_SECONDS_TABLE = new int[]{1000000000, 100000000, 10000000, 1000000, 100000, 10000, 1000, 100, 10, 1};
   private static volatile TimeZoneProvider LOCAL;

   private DateTimeUtils() {
   }

   public static void resetCalendar() {
      LOCAL = null;
   }

   public static TimeZoneProvider getTimeZone() {
      TimeZoneProvider var0 = LOCAL;
      if (var0 == null) {
         LOCAL = var0 = TimeZoneProvider.getDefault();
      }

      return var0;
   }

   public static ValueTimestampTimeZone currentTimestamp(TimeZoneProvider var0) {
      return currentTimestamp(var0, Instant.now());
   }

   public static ValueTimestampTimeZone currentTimestamp(TimeZoneProvider var0, Instant var1) {
      long var2 = var1.getEpochSecond();
      int var4 = var0.getTimeZoneOffsetUTC(var2);
      var2 += (long)var4;
      return ValueTimestampTimeZone.fromDateValueAndNanos(dateValueFromAbsoluteDay(var2 / 86400L), var2 % 86400L * 1000000000L + (long)var1.getNano(), var4);
   }

   public static long parseDateValue(String var0, int var1, int var2) {
      if (var0.charAt(var1) == '+') {
         ++var1;
      }

      int var3 = var0.indexOf(45, var1 + 1);
      int var4;
      int var5;
      int var6;
      if (var3 > 0) {
         var4 = var3 + 1;
         var5 = var0.indexOf(45, var4);
         if (var5 <= var4) {
            throw new IllegalArgumentException(var0);
         }

         var6 = var5 + 1;
      } else {
         var5 = var6 = var2 - 2;
         var3 = var4 = var5 - 2;
         if (var3 < var1 + 3) {
            throw new IllegalArgumentException(var0);
         }
      }

      int var7 = Integer.parseInt(var0.substring(var1, var3));
      int var8 = StringUtils.parseUInt31(var0, var4, var5);
      int var9 = StringUtils.parseUInt31(var0, var6, var2);
      if (!isValidDate(var7, var8, var9)) {
         throw new IllegalArgumentException(var7 + "-" + var8 + "-" + var9);
      } else {
         return dateValue((long)var7, var8, var9);
      }
   }

   public static long parseTimeNanos(String var0, int var1, int var2) {
      int var7 = var0.indexOf(58, var1);
      int var8;
      int var9;
      int var10;
      int var11;
      if (var7 > 0) {
         var8 = var7 + 1;
         var9 = var0.indexOf(58, var8);
         if (var9 >= var8) {
            var10 = var9 + 1;
            var11 = var0.indexOf(46, var10);
         } else {
            var9 = var2;
            var11 = -1;
            var10 = -1;
         }
      } else {
         int var12 = var0.indexOf(46, var1);
         if (var12 < 0) {
            var7 = var8 = var1 + 2;
            var9 = var8 + 2;
            int var13 = var2 - var1;
            if (var13 == 6) {
               var10 = var9;
               var11 = -1;
            } else {
               if (var13 != 4) {
                  throw new IllegalArgumentException(var0);
               }

               var11 = -1;
               var10 = -1;
            }
         } else if (var12 >= var1 + 6) {
            if (var12 - var1 != 6) {
               throw new IllegalArgumentException(var0);
            }

            var7 = var8 = var1 + 2;
            var9 = var10 = var8 + 2;
            var11 = var12;
         } else {
            var7 = var12;
            var8 = var12 + 1;
            var9 = var0.indexOf(46, var8);
            if (var9 <= var8) {
               throw new IllegalArgumentException(var0);
            }

            var10 = var9 + 1;
            var11 = var0.indexOf(46, var10);
         }
      }

      int var3 = StringUtils.parseUInt31(var0, var1, var7);
      if (var3 >= 24) {
         throw new IllegalArgumentException(var0);
      } else {
         int var4 = StringUtils.parseUInt31(var0, var8, var9);
         int var5;
         int var6;
         if (var10 > 0) {
            if (var11 < 0) {
               var5 = StringUtils.parseUInt31(var0, var10, var2);
               var6 = 0;
            } else {
               var5 = StringUtils.parseUInt31(var0, var10, var11);
               var6 = parseNanos(var0, var11 + 1, var2);
            }
         } else {
            var6 = 0;
            var5 = 0;
         }

         if (var4 < 60 && var5 < 60) {
            return (((long)var3 * 60L + (long)var4) * 60L + (long)var5) * 1000000000L + (long)var6;
         } else {
            throw new IllegalArgumentException(var0);
         }
      }
   }

   static int parseNanos(String var0, int var1, int var2) {
      if (var1 >= var2) {
         throw new IllegalArgumentException(var0);
      } else {
         int var3 = 0;
         int var4 = 100000000;

         do {
            char var5 = var0.charAt(var1);
            if (var5 < '0' || var5 > '9') {
               throw new IllegalArgumentException(var0);
            }

            var3 += var4 * (var5 - 48);
            var4 /= 10;
            ++var1;
         } while(var1 < var2);

         return var3;
      }
   }

   public static Value parseTimestamp(String var0, CastDataProvider var1, boolean var2) {
      int var3 = var0.indexOf(32);
      if (var3 < 0) {
         var3 = var0.indexOf(84);
         if (var3 < 0 && var1 != null && var1.getMode().allowDB2TimestampFormat) {
            var3 = var0.indexOf(45, var0.indexOf(45, var0.indexOf(45) + 1) + 1);
         }
      }

      int var4;
      if (var3 < 0) {
         var3 = var0.length();
         var4 = -1;
      } else {
         var4 = var3 + 1;
      }

      long var5 = parseDateValue(var0, 0, var3);
      TimeZoneProvider var9 = null;
      long var7;
      int var10;
      if (var4 < 0) {
         var7 = 0L;
      } else {
         ++var3;
         if (var0.endsWith("Z")) {
            var9 = TimeZoneProvider.UTC;
            var10 = var0.length() - 1;
         } else {
            int var11 = var0.indexOf(43, var3);
            if (var11 < 0) {
               var11 = var0.indexOf(45, var3);
            }

            if (var11 >= 0) {
               int var12 = var0.indexOf(91, var11 + 1);
               if (var12 < 0) {
                  var12 = var0.length();
               }

               var9 = TimeZoneProvider.ofId(var0.substring(var11, var12));
               if (var0.charAt(var11 - 1) == ' ') {
                  --var11;
               }

               var10 = var11;
            } else {
               var11 = var0.indexOf(32, var3);
               if (var11 > 0) {
                  var9 = TimeZoneProvider.ofId(var0.substring(var11 + 1));
                  var10 = var11;
               } else {
                  var10 = var0.length();
               }
            }
         }

         var7 = parseTimeNanos(var0, var3, var10);
      }

      if (var2) {
         if (var9 == null) {
            var9 = var1 != null ? var1.currentTimeZone() : getTimeZone();
         }

         if (var9 != TimeZoneProvider.UTC) {
            var10 = var9.getTimeZoneOffsetUTC(var9.getEpochSecondsFromLocal(var5, var7));
         } else {
            var10 = 0;
         }

         return ValueTimestampTimeZone.fromDateValueAndNanos(var5, var7, var10);
      } else {
         if (var9 != null) {
            long var13 = var9.getEpochSecondsFromLocal(var5, var7);
            var13 += (long)(var1 != null ? var1.currentTimeZone() : getTimeZone()).getTimeZoneOffsetUTC(var13);
            var5 = dateValueFromLocalSeconds(var13);
            var7 = var7 % 1000000000L + nanosFromLocalSeconds(var13);
         }

         return ValueTimestamp.fromDateValueAndNanos(var5, var7);
      }
   }

   public static ValueTimeTimeZone parseTimeWithTimeZone(String var0, CastDataProvider var1) {
      int var2;
      TimeZoneProvider var3;
      if (var0.endsWith("Z")) {
         var3 = TimeZoneProvider.UTC;
         var2 = var0.length() - 1;
      } else {
         int var4 = var0.indexOf(43, 1);
         if (var4 < 0) {
            var4 = var0.indexOf(45, 1);
         }

         if (var4 >= 0) {
            var3 = TimeZoneProvider.ofId(var0.substring(var4));
            if (var0.charAt(var4 - 1) == ' ') {
               --var4;
            }

            var2 = var4;
         } else {
            var4 = var0.indexOf(32, 1);
            if (var4 <= 0) {
               throw DbException.get(22007, (String[])("TIME WITH TIME ZONE", var0));
            }

            var3 = TimeZoneProvider.ofId(var0.substring(var4 + 1));
            var2 = var4;
         }

         if (!var3.hasFixedOffset()) {
            throw DbException.get(22007, (String[])("TIME WITH TIME ZONE", var0));
         }
      }

      return ValueTimeTimeZone.fromNanos(parseTimeNanos(var0, 0, var2), var3.getTimeZoneOffsetUTC(0L));
   }

   public static long getEpochSeconds(long var0, long var2, int var4) {
      return absoluteDayFromDateValue(var0) * 86400L + var2 / 1000000000L - (long)var4;
   }

   public static long[] dateAndTimeFromValue(Value var0, CastDataProvider var1) {
      long var2 = 1008673L;
      long var4 = 0L;
      ValueTimestamp var6;
      if (var0 instanceof ValueTimestamp) {
         var6 = (ValueTimestamp)var0;
         var2 = var6.getDateValue();
         var4 = var6.getTimeNanos();
      } else if (var0 instanceof ValueDate) {
         var2 = ((ValueDate)var0).getDateValue();
      } else if (var0 instanceof ValueTime) {
         var4 = ((ValueTime)var0).getNanos();
      } else if (var0 instanceof ValueTimestampTimeZone) {
         ValueTimestampTimeZone var7 = (ValueTimestampTimeZone)var0;
         var2 = var7.getDateValue();
         var4 = var7.getTimeNanos();
      } else if (var0 instanceof ValueTimeTimeZone) {
         var4 = ((ValueTimeTimeZone)var0).getNanos();
      } else {
         var6 = (ValueTimestamp)var0.convertTo(TypeInfo.TYPE_TIMESTAMP, var1);
         var2 = var6.getDateValue();
         var4 = var6.getTimeNanos();
      }

      return new long[]{var2, var4};
   }

   public static Value dateTimeToValue(Value var0, long var1, long var3) {
      switch (var0.getValueType()) {
         case 17:
            return ValueDate.fromDateValue(var1);
         case 18:
            return ValueTime.fromNanos(var3);
         case 19:
            return ValueTimeTimeZone.fromNanos(var3, ((ValueTimeTimeZone)var0).getTimeZoneOffsetSeconds());
         case 20:
         default:
            return ValueTimestamp.fromDateValueAndNanos(var1, var3);
         case 21:
            return ValueTimestampTimeZone.fromDateValueAndNanos(var1, var3, ((ValueTimestampTimeZone)var0).getTimeZoneOffsetSeconds());
      }
   }

   public static int getDayOfWeek(long var0, int var2) {
      return getDayOfWeekFromAbsolute(absoluteDayFromDateValue(var0), var2);
   }

   public static int getDayOfWeekFromAbsolute(long var0, int var2) {
      return var0 >= 0L ? (int)((var0 - (long)var2 + 11L) % 7L) + 1 : (int)((var0 - (long)var2 - 2L) % 7L) + 7;
   }

   public static int getDayOfYear(long var0) {
      int var2 = monthFromDateValue(var0);
      int var3 = (367 * var2 - 362) / 12 + dayFromDateValue(var0);
      if (var2 > 2) {
         --var3;
         long var4 = (long)yearFromDateValue(var0);
         if ((var4 & 3L) != 0L || var4 % 100L == 0L && var4 % 400L != 0L) {
            --var3;
         }
      }

      return var3;
   }

   public static int getIsoDayOfWeek(long var0) {
      return getDayOfWeek(var0, 1);
   }

   public static int getIsoWeekOfYear(long var0) {
      return getWeekOfYear(var0, 1, 4);
   }

   public static int getIsoWeekYear(long var0) {
      return getWeekYear(var0, 1, 4);
   }

   public static int getSundayDayOfWeek(long var0) {
      return getDayOfWeek(var0, 0);
   }

   public static int getWeekOfYear(long var0, int var2, int var3) {
      long var4 = absoluteDayFromDateValue(var0);
      int var6 = yearFromDateValue(var0);
      long var7 = getWeekYearAbsoluteStart(var6, var2, var3);
      if (var4 - var7 < 0L) {
         var7 = getWeekYearAbsoluteStart(var6 - 1, var2, var3);
      } else if (monthFromDateValue(var0) == 12 && 24 + var3 < dayFromDateValue(var0) && var4 >= getWeekYearAbsoluteStart(var6 + 1, var2, var3)) {
         return 1;
      }

      return (int)((var4 - var7) / 7L) + 1;
   }

   public static long getWeekYearAbsoluteStart(int var0, int var1, int var2) {
      long var3 = absoluteDayFromYear((long)var0);
      int var5 = 8 - getDayOfWeekFromAbsolute(var3, var1);
      long var6 = var3 + (long)var5;
      if (var5 >= var2) {
         var6 -= 7L;
      }

      return var6;
   }

   public static int getWeekYear(long var0, int var2, int var3) {
      long var4 = absoluteDayFromDateValue(var0);
      int var6 = yearFromDateValue(var0);
      long var7 = getWeekYearAbsoluteStart(var6, var2, var3);
      if (var4 < var7) {
         return var6 - 1;
      } else {
         return monthFromDateValue(var0) == 12 && 24 + var3 < dayFromDateValue(var0) && var4 >= getWeekYearAbsoluteStart(var6 + 1, var2, var3) ? var6 + 1 : var6;
      }
   }

   public static int getDaysInMonth(int var0, int var1) {
      if (var1 != 2) {
         return NORMAL_DAYS_PER_MONTH[var1];
      } else {
         return (var0 & 3) != 0 || var0 % 100 == 0 && var0 % 400 != 0 ? 28 : 29;
      }
   }

   public static boolean isValidDate(int var0, int var1, int var2) {
      return var1 >= 1 && var1 <= 12 && var2 >= 1 && var2 <= getDaysInMonth(var0, var1);
   }

   public static int yearFromDateValue(long var0) {
      return (int)(var0 >>> 9);
   }

   public static int monthFromDateValue(long var0) {
      return (int)(var0 >>> 5) & 15;
   }

   public static int dayFromDateValue(long var0) {
      return (int)(var0 & 31L);
   }

   public static long dateValue(long var0, int var2, int var3) {
      return var0 << 9 | (long)(var2 << 5) | (long)var3;
   }

   public static long dateValueFromDenormalizedDate(long var0, long var2, int var4) {
      long var5 = var2 - 1L;
      long var7 = var5 / 12L;
      if (var5 < 0L && var7 * 12L != var5) {
         --var7;
      }

      int var9 = (int)(var0 + var7);
      int var10 = (int)(var2 - var7 * 12L);
      if (var4 < 1) {
         var4 = 1;
      } else {
         int var11 = getDaysInMonth(var9, var10);
         if (var4 > var11) {
            var4 = var11;
         }
      }

      return dateValue((long)var9, var10, var4);
   }

   public static long dateValueFromLocalSeconds(long var0) {
      long var2 = var0 / 86400L;
      if (var0 < 0L && var2 * 86400L != var0) {
         --var2;
      }

      return dateValueFromAbsoluteDay(var2);
   }

   public static long nanosFromLocalSeconds(long var0) {
      var0 %= 86400L;
      if (var0 < 0L) {
         var0 += 86400L;
      }

      return var0 * 1000000000L;
   }

   public static long normalizeNanosOfDay(long var0) {
      var0 %= 86400000000000L;
      if (var0 < 0L) {
         var0 += 86400000000000L;
      }

      return var0;
   }

   public static long absoluteDayFromYear(long var0) {
      long var2 = 365L * var0 - 719528L;
      if (var0 >= 0L) {
         var2 += (var0 + 3L) / 4L - (var0 + 99L) / 100L + (var0 + 399L) / 400L;
      } else {
         var2 -= var0 / -4L - var0 / -100L + var0 / -400L;
      }

      return var2;
   }

   public static long absoluteDayFromDateValue(long var0) {
      return absoluteDay((long)yearFromDateValue(var0), monthFromDateValue(var0), dayFromDateValue(var0));
   }

   static long absoluteDay(long var0, int var2, int var3) {
      long var4 = absoluteDayFromYear(var0) + (long)((367 * var2 - 362) / 12) + (long)var3 - 1L;
      if (var2 > 2) {
         --var4;
         if ((var0 & 3L) != 0L || var0 % 100L == 0L && var0 % 400L != 0L) {
            --var4;
         }
      }

      return var4;
   }

   public static long dateValueFromAbsoluteDay(long var0) {
      long var2 = var0 + 719468L;
      long var4 = 0L;
      if (var2 < 0L) {
         var4 = (var2 + 1L) / 146097L - 1L;
         var2 -= var4 * 146097L;
         var4 *= 400L;
      }

      long var6 = (400L * var2 + 591L) / 146097L;
      int var8 = (int)(var2 - (365L * var6 + var6 / 4L - var6 / 100L + var6 / 400L));
      if (var8 < 0) {
         --var6;
         var8 = (int)(var2 - (365L * var6 + var6 / 4L - var6 / 100L + var6 / 400L));
      }

      var6 += var4;
      int var9 = (var8 * 5 + 2) / 153;
      var8 -= (var9 * 306 + 5) / 10 - 1;
      if (var9 >= 10) {
         ++var6;
         var9 -= 12;
      }

      return dateValue(var6, var9 + 3, var8);
   }

   public static long incrementDateValue(long var0) {
      int var2 = dayFromDateValue(var0);
      if (var2 < 28) {
         return var0 + 1L;
      } else {
         int var3 = yearFromDateValue(var0);
         int var4 = monthFromDateValue(var0);
         if (var2 < getDaysInMonth(var3, var4)) {
            return var0 + 1L;
         } else {
            if (var4 < 12) {
               ++var4;
            } else {
               var4 = 1;
               ++var3;
            }

            return dateValue((long)var3, var4, 1);
         }
      }
   }

   public static long decrementDateValue(long var0) {
      if (dayFromDateValue(var0) > 1) {
         return var0 - 1L;
      } else {
         int var2 = yearFromDateValue(var0);
         int var3 = monthFromDateValue(var0);
         if (var3 > 1) {
            --var3;
         } else {
            var3 = 12;
            --var2;
         }

         return dateValue((long)var2, var3, getDaysInMonth(var2, var3));
      }
   }

   public static StringBuilder appendDate(StringBuilder var0, long var1) {
      int var3 = yearFromDateValue(var1);
      if (var3 < 1000 && var3 > -1000) {
         if (var3 < 0) {
            var0.append('-');
            var3 = -var3;
         }

         StringUtils.appendZeroPadded(var0, 4, (long)var3);
      } else {
         var0.append(var3);
      }

      StringUtils.appendTwoDigits(var0.append('-'), monthFromDateValue(var1)).append('-');
      return StringUtils.appendTwoDigits(var0, dayFromDateValue(var1));
   }

   public static StringBuilder appendTime(StringBuilder var0, long var1) {
      if (var1 < 0L) {
         var0.append('-');
         var1 = -var1;
      }

      long var3 = -var1 / -1000000000L;
      var1 -= var3 * 1000000000L;
      int var5 = (int)(var3 / 60L);
      var3 -= (long)(var5 * 60);
      int var6 = var5 / 60;
      var5 -= var6 * 60;
      StringUtils.appendTwoDigits(var0, var6).append(':');
      StringUtils.appendTwoDigits(var0, var5).append(':');
      StringUtils.appendTwoDigits(var0, (int)var3);
      return appendNanos(var0, (int)var1);
   }

   static StringBuilder appendNanos(StringBuilder var0, int var1) {
      if (var1 > 0) {
         var0.append('.');

         for(int var2 = 1; var1 < FRACTIONAL_SECONDS_TABLE[var2]; ++var2) {
            var0.append('0');
         }

         if (var1 % 1000 == 0) {
            var1 /= 1000;
            if (var1 % 1000 == 0) {
               var1 /= 1000;
            }
         }

         if (var1 % 10 == 0) {
            var1 /= 10;
            if (var1 % 10 == 0) {
               var1 /= 10;
            }
         }

         var0.append(var1);
      }

      return var0;
   }

   public static StringBuilder appendTimeZone(StringBuilder var0, int var1) {
      if (var1 < 0) {
         var0.append('-');
         var1 = -var1;
      } else {
         var0.append('+');
      }

      int var2 = var1 / 3600;
      StringUtils.appendTwoDigits(var0, var2);
      var1 -= var2 * 3600;
      if (var1 != 0) {
         var2 = var1 / 60;
         StringUtils.appendTwoDigits(var0.append(':'), var2);
         var1 -= var2 * 60;
         if (var1 != 0) {
            StringUtils.appendTwoDigits(var0.append(':'), var1);
         }
      }

      return var0;
   }

   public static String timeZoneNameFromOffsetSeconds(int var0) {
      if (var0 == 0) {
         return "UTC";
      } else {
         StringBuilder var1 = new StringBuilder(12);
         var1.append("GMT");
         if (var0 < 0) {
            var1.append('-');
            var0 = -var0;
         } else {
            var1.append('+');
         }

         StringUtils.appendTwoDigits(var1, var0 / 3600).append(':');
         var0 %= 3600;
         StringUtils.appendTwoDigits(var1, var0 / 60);
         var0 %= 60;
         if (var0 != 0) {
            var1.append(':');
            StringUtils.appendTwoDigits(var1, var0);
         }

         return var1.toString();
      }
   }

   public static long convertScale(long var0, int var2, long var3) {
      if (var2 >= 9) {
         return var0;
      } else {
         int var5 = FRACTIONAL_SECONDS_TABLE[var2];
         long var6 = var0 % (long)var5;
         if (var6 >= (long)(var5 >>> 1)) {
            var0 += (long)var5;
         }

         long var8 = var0 - var6;
         if (var8 >= var3) {
            var8 = var3 - (long)var5;
         }

         return var8;
      }
   }

   public static ValueTimestampTimeZone timestampTimeZoneAtOffset(long var0, long var2, int var4, int var5) {
      var2 += (long)(var5 - var4) * 1000000000L;
      if (var2 < 0L) {
         var2 += 86400000000000L;
         var0 = decrementDateValue(var0);
         if (var2 < 0L) {
            var2 += 86400000000000L;
            var0 = decrementDateValue(var0);
         }
      } else if (var2 >= 86400000000000L) {
         var2 -= 86400000000000L;
         var0 = incrementDateValue(var0);
         if (var2 >= 86400000000000L) {
            var2 -= 86400000000000L;
            var0 = incrementDateValue(var0);
         }
      }

      return ValueTimestampTimeZone.fromDateValueAndNanos(var0, var2, var5);
   }
}
