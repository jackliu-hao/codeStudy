package org.h2.util;

import java.math.BigInteger;
import org.h2.api.IntervalQualifier;
import org.h2.message.DbException;
import org.h2.value.ValueInterval;

public class IntervalUtils {
   private static final BigInteger NANOS_PER_SECOND_BI = BigInteger.valueOf(1000000000L);
   private static final BigInteger NANOS_PER_MINUTE_BI = BigInteger.valueOf(60000000000L);
   private static final BigInteger NANOS_PER_HOUR_BI = BigInteger.valueOf(3600000000000L);
   public static final BigInteger NANOS_PER_DAY_BI = BigInteger.valueOf(86400000000000L);
   private static final BigInteger MONTHS_PER_YEAR_BI = BigInteger.valueOf(12L);
   private static final BigInteger HOURS_PER_DAY_BI = BigInteger.valueOf(24L);
   private static final BigInteger MINUTES_PER_DAY_BI = BigInteger.valueOf(1440L);
   private static final BigInteger MINUTES_PER_HOUR_BI = BigInteger.valueOf(60L);
   private static final BigInteger LEADING_MIN = BigInteger.valueOf(-999999999999999999L);
   private static final BigInteger LEADING_MAX = BigInteger.valueOf(999999999999999999L);

   private IntervalUtils() {
   }

   public static ValueInterval parseFormattedInterval(IntervalQualifier var0, String var1) {
      int var2 = 0;
      var2 = skipWS(var1, var2);
      if (!var1.regionMatches(true, var2, "INTERVAL", 0, 8)) {
         return parseInterval(var0, false, var1);
      } else {
         var2 = skipWS(var1, var2 + 8);
         boolean var3 = false;
         char var4 = var1.charAt(var2);
         if (var4 == '-') {
            var3 = true;
            var2 = skipWS(var1, var2 + 1);
            var4 = var1.charAt(var2);
         } else if (var4 == '+') {
            var2 = skipWS(var1, var2 + 1);
            var4 = var1.charAt(var2);
         }

         if (var4 != '\'') {
            throw new IllegalArgumentException(var1);
         } else {
            ++var2;
            int var5 = var2;

            for(int var6 = var1.length(); var2 != var6; ++var2) {
               if (var1.charAt(var2) == '\'') {
                  String var7 = var1.substring(var5, var2);
                  var2 = skipWS(var1, var2 + 1);
                  int var8;
                  if (var1.regionMatches(true, var2, "YEAR", 0, 4)) {
                     var2 += 4;
                     var8 = skipWSEnd(var1, var2);
                     if (var8 == var6) {
                        return parseInterval(IntervalQualifier.YEAR, var3, var7);
                     }

                     if (var8 > var2 && var1.regionMatches(true, var8, "TO", 0, 2)) {
                        var8 += 2;
                        var2 = skipWS(var1, var8);
                        if (var2 > var8 && var1.regionMatches(true, var2, "MONTH", 0, 5) && skipWSEnd(var1, var2 + 5) == var6) {
                           return parseInterval(IntervalQualifier.YEAR_TO_MONTH, var3, var7);
                        }
                     }
                  } else if (var1.regionMatches(true, var2, "MONTH", 0, 5) && skipWSEnd(var1, var2 + 5) == var6) {
                     return parseInterval(IntervalQualifier.MONTH, var3, var7);
                  }

                  if (var1.regionMatches(true, var2, "DAY", 0, 3)) {
                     var2 += 3;
                     var8 = skipWSEnd(var1, var2);
                     if (var8 == var6) {
                        return parseInterval(IntervalQualifier.DAY, var3, var7);
                     }

                     if (var8 > var2 && var1.regionMatches(true, var8, "TO", 0, 2)) {
                        var8 += 2;
                        var2 = skipWS(var1, var8);
                        if (var2 > var8) {
                           if (var1.regionMatches(true, var2, "HOUR", 0, 4)) {
                              if (skipWSEnd(var1, var2 + 4) == var6) {
                                 return parseInterval(IntervalQualifier.DAY_TO_HOUR, var3, var7);
                              }
                           } else if (var1.regionMatches(true, var2, "MINUTE", 0, 6)) {
                              if (skipWSEnd(var1, var2 + 6) == var6) {
                                 return parseInterval(IntervalQualifier.DAY_TO_MINUTE, var3, var7);
                              }
                           } else if (var1.regionMatches(true, var2, "SECOND", 0, 6) && skipWSEnd(var1, var2 + 6) == var6) {
                              return parseInterval(IntervalQualifier.DAY_TO_SECOND, var3, var7);
                           }
                        }
                     }
                  }

                  if (var1.regionMatches(true, var2, "HOUR", 0, 4)) {
                     var2 += 4;
                     var8 = skipWSEnd(var1, var2);
                     if (var8 == var6) {
                        return parseInterval(IntervalQualifier.HOUR, var3, var7);
                     }

                     if (var8 > var2 && var1.regionMatches(true, var8, "TO", 0, 2)) {
                        var8 += 2;
                        var2 = skipWS(var1, var8);
                        if (var2 > var8) {
                           if (var1.regionMatches(true, var2, "MINUTE", 0, 6)) {
                              if (skipWSEnd(var1, var2 + 6) == var6) {
                                 return parseInterval(IntervalQualifier.HOUR_TO_MINUTE, var3, var7);
                              }
                           } else if (var1.regionMatches(true, var2, "SECOND", 0, 6) && skipWSEnd(var1, var2 + 6) == var6) {
                              return parseInterval(IntervalQualifier.HOUR_TO_SECOND, var3, var7);
                           }
                        }
                     }
                  }

                  if (var1.regionMatches(true, var2, "MINUTE", 0, 6)) {
                     var2 += 6;
                     var8 = skipWSEnd(var1, var2);
                     if (var8 == var6) {
                        return parseInterval(IntervalQualifier.MINUTE, var3, var7);
                     }

                     if (var8 > var2 && var1.regionMatches(true, var8, "TO", 0, 2)) {
                        var8 += 2;
                        var2 = skipWS(var1, var8);
                        if (var2 > var8 && var1.regionMatches(true, var2, "SECOND", 0, 6) && skipWSEnd(var1, var2 + 6) == var6) {
                           return parseInterval(IntervalQualifier.MINUTE_TO_SECOND, var3, var7);
                        }
                     }
                  }

                  if (var1.regionMatches(true, var2, "SECOND", 0, 6) && skipWSEnd(var1, var2 + 6) == var6) {
                     return parseInterval(IntervalQualifier.SECOND, var3, var7);
                  }

                  throw new IllegalArgumentException(var1);
               }
            }

            throw new IllegalArgumentException(var1);
         }
      }
   }

   private static int skipWS(String var0, int var1) {
      for(int var2 = var0.length(); var1 != var2; ++var1) {
         if (!Character.isWhitespace(var0.charAt(var1))) {
            return var1;
         }
      }

      throw new IllegalArgumentException(var0);
   }

   private static int skipWSEnd(String var0, int var1) {
      for(int var2 = var0.length(); var1 != var2; ++var1) {
         if (!Character.isWhitespace(var0.charAt(var1))) {
            return var1;
         }
      }

      return var1;
   }

   public static ValueInterval parseInterval(IntervalQualifier var0, boolean var1, String var2) {
      long var3;
      long var5;
      int var7;
      int var8;
      switch (var0) {
         case YEAR:
         case MONTH:
         case DAY:
         case HOUR:
         case MINUTE:
            var3 = parseIntervalLeading(var2, 0, var2.length(), var1);
            var5 = 0L;
            break;
         case SECOND:
            var7 = var2.indexOf(46);
            if (var7 < 0) {
               var3 = parseIntervalLeading(var2, 0, var2.length(), var1);
               var5 = 0L;
            } else {
               var3 = parseIntervalLeading(var2, 0, var7, var1);
               var5 = (long)DateTimeUtils.parseNanos(var2, var7 + 1, var2.length());
            }
            break;
         case YEAR_TO_MONTH:
            return parseInterval2(var0, var2, '-', 11, var1);
         case DAY_TO_HOUR:
            return parseInterval2(var0, var2, ' ', 23, var1);
         case DAY_TO_MINUTE:
            var7 = var2.indexOf(32);
            if (var7 < 0) {
               var3 = parseIntervalLeading(var2, 0, var2.length(), var1);
               var5 = 0L;
            } else {
               var3 = parseIntervalLeading(var2, 0, var7, var1);
               var8 = var2.indexOf(58, var7 + 1);
               if (var8 < 0) {
                  var5 = parseIntervalRemaining(var2, var7 + 1, var2.length(), 23) * 60L;
               } else {
                  var5 = parseIntervalRemaining(var2, var7 + 1, var8, 23) * 60L + parseIntervalRemaining(var2, var8 + 1, var2.length(), 59);
               }
            }
            break;
         case DAY_TO_SECOND:
            var7 = var2.indexOf(32);
            if (var7 < 0) {
               var3 = parseIntervalLeading(var2, 0, var2.length(), var1);
               var5 = 0L;
            } else {
               var3 = parseIntervalLeading(var2, 0, var7, var1);
               var8 = var2.indexOf(58, var7 + 1);
               if (var8 < 0) {
                  var5 = parseIntervalRemaining(var2, var7 + 1, var2.length(), 23) * 3600000000000L;
               } else {
                  int var9 = var2.indexOf(58, var8 + 1);
                  if (var9 < 0) {
                     var5 = parseIntervalRemaining(var2, var7 + 1, var8, 23) * 3600000000000L + parseIntervalRemaining(var2, var8 + 1, var2.length(), 59) * 60000000000L;
                  } else {
                     var5 = parseIntervalRemaining(var2, var7 + 1, var8, 23) * 3600000000000L + parseIntervalRemaining(var2, var8 + 1, var9, 59) * 60000000000L + parseIntervalRemainingSeconds(var2, var9 + 1);
                  }
               }
            }
            break;
         case HOUR_TO_MINUTE:
            return parseInterval2(var0, var2, ':', 59, var1);
         case HOUR_TO_SECOND:
            var7 = var2.indexOf(58);
            if (var7 < 0) {
               var3 = parseIntervalLeading(var2, 0, var2.length(), var1);
               var5 = 0L;
            } else {
               var3 = parseIntervalLeading(var2, 0, var7, var1);
               var8 = var2.indexOf(58, var7 + 1);
               if (var8 < 0) {
                  var5 = parseIntervalRemaining(var2, var7 + 1, var2.length(), 59) * 60000000000L;
               } else {
                  var5 = parseIntervalRemaining(var2, var7 + 1, var8, 59) * 60000000000L + parseIntervalRemainingSeconds(var2, var8 + 1);
               }
            }
            break;
         case MINUTE_TO_SECOND:
            var7 = var2.indexOf(58);
            if (var7 < 0) {
               var3 = parseIntervalLeading(var2, 0, var2.length(), var1);
               var5 = 0L;
            } else {
               var3 = parseIntervalLeading(var2, 0, var7, var1);
               var5 = parseIntervalRemainingSeconds(var2, var7 + 1);
            }
            break;
         default:
            throw new IllegalArgumentException();
      }

      var1 = var3 < 0L;
      if (var1) {
         if (var3 != Long.MIN_VALUE) {
            var3 = -var3;
         } else {
            var3 = 0L;
         }
      }

      return ValueInterval.from(var0, var1, var3, var5);
   }

   private static ValueInterval parseInterval2(IntervalQualifier var0, String var1, char var2, int var3, boolean var4) {
      int var9 = var1.indexOf(var2, 1);
      long var5;
      long var7;
      if (var9 < 0) {
         var5 = parseIntervalLeading(var1, 0, var1.length(), var4);
         var7 = 0L;
      } else {
         var5 = parseIntervalLeading(var1, 0, var9, var4);
         var7 = parseIntervalRemaining(var1, var9 + 1, var1.length(), var3);
      }

      var4 = var5 < 0L;
      if (var4) {
         if (var5 != Long.MIN_VALUE) {
            var5 = -var5;
         } else {
            var5 = 0L;
         }
      }

      return ValueInterval.from(var0, var4, var5, var7);
   }

   private static long parseIntervalLeading(String var0, int var1, int var2, boolean var3) {
      long var4 = Long.parseLong(var0.substring(var1, var2));
      if (var4 == 0L) {
         return var3 ^ var0.charAt(var1) == '-' ? Long.MIN_VALUE : 0L;
      } else {
         return var3 ? -var4 : var4;
      }
   }

   private static long parseIntervalRemaining(String var0, int var1, int var2, int var3) {
      int var4 = StringUtils.parseUInt31(var0, var1, var2);
      if (var4 > var3) {
         throw new IllegalArgumentException(var0);
      } else {
         return (long)var4;
      }
   }

   private static long parseIntervalRemainingSeconds(String var0, int var1) {
      int var4 = var0.indexOf(46, var1 + 1);
      int var2;
      int var3;
      if (var4 < 0) {
         var2 = StringUtils.parseUInt31(var0, var1, var0.length());
         var3 = 0;
      } else {
         var2 = StringUtils.parseUInt31(var0, var1, var4);
         var3 = DateTimeUtils.parseNanos(var0, var4 + 1, var0.length());
      }

      if (var2 > 59) {
         throw new IllegalArgumentException(var0);
      } else {
         return (long)var2 * 1000000000L + (long)var3;
      }
   }

   public static StringBuilder appendInterval(StringBuilder var0, IntervalQualifier var1, boolean var2, long var3, long var5) {
      var0.append("INTERVAL '");
      if (var2) {
         var0.append('-');
      }

      long var7;
      switch (var1) {
         case YEAR:
         case MONTH:
         case DAY:
         case HOUR:
         case MINUTE:
            var0.append(var3);
            break;
         case SECOND:
            DateTimeUtils.appendNanos(var0.append(var3), (int)var5);
            break;
         case YEAR_TO_MONTH:
            var0.append(var3).append('-').append(var5);
            break;
         case DAY_TO_HOUR:
            var0.append(var3).append(' ');
            StringUtils.appendTwoDigits(var0, (int)var5);
            break;
         case DAY_TO_MINUTE:
            var0.append(var3).append(' ');
            int var10 = (int)var5;
            StringUtils.appendTwoDigits(var0, var10 / 60).append(':');
            StringUtils.appendTwoDigits(var0, var10 % 60);
            break;
         case DAY_TO_SECOND:
            var7 = var5 % 60000000000L;
            int var9 = (int)(var5 / 60000000000L);
            var0.append(var3).append(' ');
            StringUtils.appendTwoDigits(var0, var9 / 60).append(':');
            StringUtils.appendTwoDigits(var0, var9 % 60).append(':');
            StringUtils.appendTwoDigits(var0, (int)(var7 / 1000000000L));
            DateTimeUtils.appendNanos(var0, (int)(var7 % 1000000000L));
            break;
         case HOUR_TO_MINUTE:
            var0.append(var3).append(':');
            StringUtils.appendTwoDigits(var0, (int)var5);
            break;
         case HOUR_TO_SECOND:
            var0.append(var3).append(':');
            StringUtils.appendTwoDigits(var0, (int)(var5 / 60000000000L)).append(':');
            var7 = var5 % 60000000000L;
            StringUtils.appendTwoDigits(var0, (int)(var7 / 1000000000L));
            DateTimeUtils.appendNanos(var0, (int)(var7 % 1000000000L));
            break;
         case MINUTE_TO_SECOND:
            var0.append(var3).append(':');
            StringUtils.appendTwoDigits(var0, (int)(var5 / 1000000000L));
            DateTimeUtils.appendNanos(var0, (int)(var5 % 1000000000L));
      }

      return var0.append("' ").append(var1);
   }

   public static BigInteger intervalToAbsolute(ValueInterval var0) {
      BigInteger var1;
      switch (var0.getQualifier()) {
         case YEAR:
            var1 = BigInteger.valueOf(var0.getLeading()).multiply(MONTHS_PER_YEAR_BI);
            break;
         case MONTH:
            var1 = BigInteger.valueOf(var0.getLeading());
            break;
         case DAY:
            var1 = BigInteger.valueOf(var0.getLeading()).multiply(NANOS_PER_DAY_BI);
            break;
         case HOUR:
            var1 = BigInteger.valueOf(var0.getLeading()).multiply(NANOS_PER_HOUR_BI);
            break;
         case MINUTE:
            var1 = BigInteger.valueOf(var0.getLeading()).multiply(NANOS_PER_MINUTE_BI);
            break;
         case SECOND:
            var1 = intervalToAbsolute(var0, NANOS_PER_SECOND_BI);
            break;
         case YEAR_TO_MONTH:
            var1 = intervalToAbsolute(var0, MONTHS_PER_YEAR_BI);
            break;
         case DAY_TO_HOUR:
            var1 = intervalToAbsolute(var0, HOURS_PER_DAY_BI, NANOS_PER_HOUR_BI);
            break;
         case DAY_TO_MINUTE:
            var1 = intervalToAbsolute(var0, MINUTES_PER_DAY_BI, NANOS_PER_MINUTE_BI);
            break;
         case DAY_TO_SECOND:
            var1 = intervalToAbsolute(var0, NANOS_PER_DAY_BI);
            break;
         case HOUR_TO_MINUTE:
            var1 = intervalToAbsolute(var0, MINUTES_PER_HOUR_BI, NANOS_PER_MINUTE_BI);
            break;
         case HOUR_TO_SECOND:
            var1 = intervalToAbsolute(var0, NANOS_PER_HOUR_BI);
            break;
         case MINUTE_TO_SECOND:
            var1 = intervalToAbsolute(var0, NANOS_PER_MINUTE_BI);
            break;
         default:
            throw new IllegalArgumentException();
      }

      return var0.isNegative() ? var1.negate() : var1;
   }

   private static BigInteger intervalToAbsolute(ValueInterval var0, BigInteger var1, BigInteger var2) {
      return intervalToAbsolute(var0, var1).multiply(var2);
   }

   private static BigInteger intervalToAbsolute(ValueInterval var0, BigInteger var1) {
      return BigInteger.valueOf(var0.getLeading()).multiply(var1).add(BigInteger.valueOf(var0.getRemaining()));
   }

   public static ValueInterval intervalFromAbsolute(IntervalQualifier var0, BigInteger var1) {
      switch (var0) {
         case YEAR:
            return ValueInterval.from(var0, var1.signum() < 0, leadingExact(var1.divide(MONTHS_PER_YEAR_BI)), 0L);
         case MONTH:
            return ValueInterval.from(var0, var1.signum() < 0, leadingExact(var1), 0L);
         case DAY:
            return ValueInterval.from(var0, var1.signum() < 0, leadingExact(var1.divide(NANOS_PER_DAY_BI)), 0L);
         case HOUR:
            return ValueInterval.from(var0, var1.signum() < 0, leadingExact(var1.divide(NANOS_PER_HOUR_BI)), 0L);
         case MINUTE:
            return ValueInterval.from(var0, var1.signum() < 0, leadingExact(var1.divide(NANOS_PER_MINUTE_BI)), 0L);
         case SECOND:
            return intervalFromAbsolute(var0, var1, NANOS_PER_SECOND_BI);
         case YEAR_TO_MONTH:
            return intervalFromAbsolute(var0, var1, MONTHS_PER_YEAR_BI);
         case DAY_TO_HOUR:
            return intervalFromAbsolute(var0, var1.divide(NANOS_PER_HOUR_BI), HOURS_PER_DAY_BI);
         case DAY_TO_MINUTE:
            return intervalFromAbsolute(var0, var1.divide(NANOS_PER_MINUTE_BI), MINUTES_PER_DAY_BI);
         case DAY_TO_SECOND:
            return intervalFromAbsolute(var0, var1, NANOS_PER_DAY_BI);
         case HOUR_TO_MINUTE:
            return intervalFromAbsolute(var0, var1.divide(NANOS_PER_MINUTE_BI), MINUTES_PER_HOUR_BI);
         case HOUR_TO_SECOND:
            return intervalFromAbsolute(var0, var1, NANOS_PER_HOUR_BI);
         case MINUTE_TO_SECOND:
            return intervalFromAbsolute(var0, var1, NANOS_PER_MINUTE_BI);
         default:
            throw new IllegalArgumentException();
      }
   }

   private static ValueInterval intervalFromAbsolute(IntervalQualifier var0, BigInteger var1, BigInteger var2) {
      BigInteger[] var3 = var1.divideAndRemainder(var2);
      return ValueInterval.from(var0, var1.signum() < 0, leadingExact(var3[0]), Math.abs(var3[1].longValue()));
   }

   private static long leadingExact(BigInteger var0) {
      if (var0.compareTo(LEADING_MAX) <= 0 && var0.compareTo(LEADING_MIN) >= 0) {
         return Math.abs(var0.longValue());
      } else {
         throw DbException.get(22003, (String)var0.toString());
      }
   }

   public static boolean validateInterval(IntervalQualifier var0, boolean var1, long var2, long var4) {
      if (var0 == null) {
         throw new NullPointerException();
      } else if (var2 == 0L && var4 == 0L) {
         return false;
      } else {
         long var6;
         switch (var0) {
            case YEAR:
            case MONTH:
            case DAY:
            case HOUR:
            case MINUTE:
               var6 = 1L;
               break;
            case SECOND:
               var6 = 1000000000L;
               break;
            case YEAR_TO_MONTH:
               var6 = 12L;
               break;
            case DAY_TO_HOUR:
               var6 = 24L;
               break;
            case DAY_TO_MINUTE:
               var6 = 1440L;
               break;
            case DAY_TO_SECOND:
               var6 = 86400000000000L;
               break;
            case HOUR_TO_MINUTE:
               var6 = 60L;
               break;
            case HOUR_TO_SECOND:
               var6 = 3600000000000L;
               break;
            case MINUTE_TO_SECOND:
               var6 = 60000000000L;
               break;
            default:
               throw DbException.getInvalidValueException("interval", var0);
         }

         if (var2 >= 0L && var2 < 1000000000000000000L) {
            if (var4 >= 0L && var4 < var6) {
               return var1;
            } else {
               throw DbException.getInvalidValueException("interval", Long.toString(var4));
            }
         } else {
            throw DbException.getInvalidValueException("interval", Long.toString(var2));
         }
      }
   }

   public static long yearsFromInterval(IntervalQualifier var0, boolean var1, long var2, long var4) {
      if (var0 != IntervalQualifier.YEAR && var0 != IntervalQualifier.YEAR_TO_MONTH) {
         return 0L;
      } else {
         long var6 = var2;
         if (var1) {
            var6 = -var2;
         }

         return var6;
      }
   }

   public static long monthsFromInterval(IntervalQualifier var0, boolean var1, long var2, long var4) {
      long var6;
      if (var0 == IntervalQualifier.MONTH) {
         var6 = var2;
      } else {
         if (var0 != IntervalQualifier.YEAR_TO_MONTH) {
            return 0L;
         }

         var6 = var4;
      }

      if (var1) {
         var6 = -var6;
      }

      return var6;
   }

   public static long daysFromInterval(IntervalQualifier var0, boolean var1, long var2, long var4) {
      switch (var0) {
         case DAY:
         case DAY_TO_HOUR:
         case DAY_TO_MINUTE:
         case DAY_TO_SECOND:
            long var6 = var2;
            if (var1) {
               var6 = -var2;
            }

            return var6;
         case HOUR:
         case MINUTE:
         case SECOND:
         case YEAR_TO_MONTH:
         default:
            return 0L;
      }
   }

   public static long hoursFromInterval(IntervalQualifier var0, boolean var1, long var2, long var4) {
      long var6;
      switch (var0) {
         case HOUR:
         case HOUR_TO_MINUTE:
         case HOUR_TO_SECOND:
            var6 = var2;
            break;
         case MINUTE:
         case SECOND:
         case YEAR_TO_MONTH:
         default:
            return 0L;
         case DAY_TO_HOUR:
            var6 = var4;
            break;
         case DAY_TO_MINUTE:
            var6 = var4 / 60L;
            break;
         case DAY_TO_SECOND:
            var6 = var4 / 3600000000000L;
      }

      if (var1) {
         var6 = -var6;
      }

      return var6;
   }

   public static long minutesFromInterval(IntervalQualifier var0, boolean var1, long var2, long var4) {
      long var6;
      switch (var0) {
         case MINUTE:
         case MINUTE_TO_SECOND:
            var6 = var2;
            break;
         case SECOND:
         case YEAR_TO_MONTH:
         case DAY_TO_HOUR:
         default:
            return 0L;
         case DAY_TO_MINUTE:
            var6 = var4 % 60L;
            break;
         case DAY_TO_SECOND:
            var6 = var4 / 60000000000L % 60L;
            break;
         case HOUR_TO_MINUTE:
            var6 = var4;
            break;
         case HOUR_TO_SECOND:
            var6 = var4 / 60000000000L;
      }

      if (var1) {
         var6 = -var6;
      }

      return var6;
   }

   public static long nanosFromInterval(IntervalQualifier var0, boolean var1, long var2, long var4) {
      long var6;
      switch (var0) {
         case SECOND:
            var6 = var2 * 1000000000L + var4;
            break;
         case YEAR_TO_MONTH:
         case DAY_TO_HOUR:
         case DAY_TO_MINUTE:
         case HOUR_TO_MINUTE:
         default:
            return 0L;
         case DAY_TO_SECOND:
         case HOUR_TO_SECOND:
            var6 = var4 % 60000000000L;
            break;
         case MINUTE_TO_SECOND:
            var6 = var4;
      }

      if (var1) {
         var6 = -var6;
      }

      return var6;
   }
}
