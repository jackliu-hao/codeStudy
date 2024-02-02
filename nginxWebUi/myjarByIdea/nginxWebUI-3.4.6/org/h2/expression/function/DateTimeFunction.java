package org.h2.expression.function;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.temporal.WeekFields;
import java.util.Locale;
import org.h2.api.IntervalQualifier;
import org.h2.engine.Mode;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.util.DateTimeUtils;
import org.h2.util.IntervalUtils;
import org.h2.util.MathUtils;
import org.h2.util.StringUtils;
import org.h2.value.DataType;
import org.h2.value.ExtTypeInfo;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueDate;
import org.h2.value.ValueInteger;
import org.h2.value.ValueInterval;
import org.h2.value.ValueNumeric;
import org.h2.value.ValueTime;
import org.h2.value.ValueTimeTimeZone;
import org.h2.value.ValueTimestamp;
import org.h2.value.ValueTimestampTimeZone;

public final class DateTimeFunction extends Function1_2 {
   public static final int EXTRACT = 0;
   public static final int DATE_TRUNC = 1;
   public static final int DATEADD = 2;
   public static final int DATEDIFF = 3;
   private static final String[] NAMES = new String[]{"EXTRACT", "DATE_TRUNC", "DATEADD", "DATEDIFF"};
   public static final int YEAR = 0;
   public static final int MONTH = 1;
   public static final int DAY = 2;
   public static final int HOUR = 3;
   public static final int MINUTE = 4;
   public static final int SECOND = 5;
   public static final int TIMEZONE_HOUR = 6;
   public static final int TIMEZONE_MINUTE = 7;
   public static final int TIMEZONE_SECOND = 8;
   public static final int MILLENNIUM = 9;
   public static final int CENTURY = 10;
   public static final int DECADE = 11;
   public static final int QUARTER = 12;
   public static final int MILLISECOND = 13;
   public static final int MICROSECOND = 14;
   public static final int NANOSECOND = 15;
   public static final int DAY_OF_YEAR = 16;
   public static final int ISO_DAY_OF_WEEK = 17;
   public static final int ISO_WEEK = 18;
   public static final int ISO_WEEK_YEAR = 19;
   public static final int DAY_OF_WEEK = 20;
   public static final int WEEK = 21;
   public static final int WEEK_YEAR = 22;
   public static final int EPOCH = 23;
   public static final int DOW = 24;
   private static final int FIELDS_COUNT = 25;
   private static final String[] FIELD_NAMES = new String[]{"YEAR", "MONTH", "DAY", "HOUR", "MINUTE", "SECOND", "TIMEZONE_HOUR", "TIMEZONE_MINUTE", "TIMEZONE_SECOND", "MILLENNIUM", "CENTURY", "DECADE", "QUARTER", "MILLISECOND", "MICROSECOND", "NANOSECOND", "DAY_OF_YEAR", "ISO_DAY_OF_WEEK", "ISO_WEEK", "ISO_WEEK_YEAR", "DAY_OF_WEEK", "WEEK", "WEEK_YEAR", "EPOCH", "DOW"};
   private static final BigDecimal BD_SECONDS_PER_DAY = new BigDecimal(86400L);
   private static final BigInteger BI_SECONDS_PER_DAY = BigInteger.valueOf(86400L);
   private static final BigDecimal BD_NANOS_PER_SECOND = new BigDecimal(1000000000L);
   private static volatile WeekFields WEEK_FIELDS;
   private final int function;
   private final int field;

   public static int getField(String var0) {
      switch (StringUtils.toUpperEnglish(var0)) {
         case "YEAR":
         case "YY":
         case "YYYY":
         case "SQL_TSI_YEAR":
            return 0;
         case "MONTH":
         case "M":
         case "MM":
         case "SQL_TSI_MONTH":
            return 1;
         case "DAY":
         case "D":
         case "DD":
         case "SQL_TSI_DAY":
            return 2;
         case "HOUR":
         case "HH":
         case "SQL_TSI_HOUR":
            return 3;
         case "MINUTE":
         case "MI":
         case "N":
         case "SQL_TSI_MINUTE":
            return 4;
         case "SECOND":
         case "S":
         case "SS":
         case "SQL_TSI_SECOND":
            return 5;
         case "TIMEZONE_HOUR":
            return 6;
         case "TIMEZONE_MINUTE":
            return 7;
         case "TIMEZONE_SECOND":
            return 8;
         case "MILLENNIUM":
            return 9;
         case "CENTURY":
            return 10;
         case "DECADE":
            return 11;
         case "QUARTER":
            return 12;
         case "MILLISECOND":
         case "MILLISECONDS":
         case "MS":
            return 13;
         case "MICROSECOND":
         case "MICROSECONDS":
         case "MCS":
            return 14;
         case "NANOSECOND":
         case "NS":
            return 15;
         case "DAY_OF_YEAR":
         case "DAYOFYEAR":
         case "DY":
         case "DOY":
            return 16;
         case "ISO_DAY_OF_WEEK":
         case "ISODOW":
            return 17;
         case "ISO_WEEK":
            return 18;
         case "ISO_WEEK_YEAR":
         case "ISO_YEAR":
         case "ISOYEAR":
            return 19;
         case "DAY_OF_WEEK":
         case "DAYOFWEEK":
            return 20;
         case "WEEK":
         case "WK":
         case "WW":
         case "SQL_TSI_WEEK":
            return 21;
         case "WEEK_YEAR":
            return 22;
         case "EPOCH":
            return 23;
         case "DOW":
            return 24;
         default:
            throw DbException.getInvalidValueException("date-time field", var0);
      }
   }

   public static String getFieldName(int var0) {
      if (var0 >= 0 && var0 < 25) {
         return FIELD_NAMES[var0];
      } else {
         throw DbException.getUnsupportedException("datetime field " + var0);
      }
   }

   public DateTimeFunction(int var1, int var2, Expression var3, Expression var4) {
      super(var3, var4);
      this.function = var1;
      this.field = var2;
   }

   public Value getValue(SessionLocal var1, Value var2, Value var3) {
      Object var4;
      switch (this.function) {
         case 0:
            var4 = this.field == 23 ? extractEpoch(var1, var2) : ValueInteger.get(extractInteger(var1, var2, this.field));
            break;
         case 1:
            var4 = truncateDate(var1, this.field, var2);
            break;
         case 2:
            var4 = dateadd(var1, this.field, var2.getLong(), var3);
            break;
         case 3:
            var4 = ValueBigint.get(datediff(var1, this.field, var2, var3));
            break;
         default:
            throw DbException.getInternalError("function=" + this.function);
      }

      return (Value)var4;
   }

   private static int extractInteger(SessionLocal var0, Value var1, int var2) {
      return var1 instanceof ValueInterval ? extractInterval(var1, var2) : extractDateTime(var0, var1, var2);
   }

   private static int extractInterval(Value var0, int var1) {
      ValueInterval var2 = (ValueInterval)var0;
      IntervalQualifier var3 = var2.getQualifier();
      boolean var4 = var2.isNegative();
      long var5 = var2.getLeading();
      long var7 = var2.getRemaining();
      long var9;
      switch (var1) {
         case 0:
            var9 = IntervalUtils.yearsFromInterval(var3, var4, var5, var7);
            break;
         case 1:
            var9 = IntervalUtils.monthsFromInterval(var3, var4, var5, var7);
            break;
         case 2:
         case 16:
            var9 = IntervalUtils.daysFromInterval(var3, var4, var5, var7);
            break;
         case 3:
            var9 = IntervalUtils.hoursFromInterval(var3, var4, var5, var7);
            break;
         case 4:
            var9 = IntervalUtils.minutesFromInterval(var3, var4, var5, var7);
            break;
         case 5:
            var9 = IntervalUtils.nanosFromInterval(var3, var4, var5, var7) / 1000000000L;
            break;
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         default:
            throw DbException.getUnsupportedException("getDatePart(" + var0 + ", " + var1 + ')');
         case 13:
            var9 = IntervalUtils.nanosFromInterval(var3, var4, var5, var7) / 1000000L % 1000L;
            break;
         case 14:
            var9 = IntervalUtils.nanosFromInterval(var3, var4, var5, var7) / 1000L % 1000000L;
            break;
         case 15:
            var9 = IntervalUtils.nanosFromInterval(var3, var4, var5, var7) % 1000000000L;
      }

      return (int)var9;
   }

   static int extractDateTime(SessionLocal var0, Value var1, int var2) {
      long[] var3 = DateTimeUtils.dateAndTimeFromValue(var1, var0);
      long var4 = var3[0];
      long var6 = var3[1];
      switch (var2) {
         case 0:
            return DateTimeUtils.yearFromDateValue(var4);
         case 1:
            return DateTimeUtils.monthFromDateValue(var4);
         case 2:
            return DateTimeUtils.dayFromDateValue(var4);
         case 3:
            return (int)(var6 / 3600000000000L % 24L);
         case 4:
            return (int)(var6 / 60000000000L % 60L);
         case 5:
            return (int)(var6 / 1000000000L % 60L);
         case 6:
         case 7:
         case 8:
            int var9;
            if (var1 instanceof ValueTimestampTimeZone) {
               var9 = ((ValueTimestampTimeZone)var1).getTimeZoneOffsetSeconds();
            } else if (var1 instanceof ValueTimeTimeZone) {
               var9 = ((ValueTimeTimeZone)var1).getTimeZoneOffsetSeconds();
            } else {
               var9 = var0.currentTimeZone().getTimeZoneOffsetLocal(var4, var6);
            }

            if (var2 == 6) {
               return var9 / 3600;
            } else {
               if (var2 == 7) {
                  return var9 % 3600 / 60;
               }

               return var9 % 60;
            }
         case 9:
            return millennium(DateTimeUtils.yearFromDateValue(var4));
         case 10:
            return century(DateTimeUtils.yearFromDateValue(var4));
         case 11:
            return decade(DateTimeUtils.yearFromDateValue(var4));
         case 12:
            return (DateTimeUtils.monthFromDateValue(var4) - 1) / 3 + 1;
         case 13:
            return (int)(var6 / 1000000L % 1000L);
         case 14:
            return (int)(var6 / 1000L % 1000000L);
         case 15:
            return (int)(var6 % 1000000000L);
         case 16:
            return DateTimeUtils.getDayOfYear(var4);
         case 17:
            return DateTimeUtils.getIsoDayOfWeek(var4);
         case 18:
            return DateTimeUtils.getIsoWeekOfYear(var4);
         case 19:
            return DateTimeUtils.getIsoWeekYear(var4);
         case 21:
            return getLocalWeekOfYear(var4);
         case 22:
            WeekFields var8 = getWeekFields();
            return DateTimeUtils.getWeekYear(var4, var8.getFirstDayOfWeek().getValue(), var8.getMinimalDaysInFirstWeek());
         case 23:
         default:
            throw DbException.getUnsupportedException("EXTRACT(" + getFieldName(var2) + " FROM " + var1 + ')');
         case 24:
            if (var0.getMode().getEnum() == Mode.ModeEnum.PostgreSQL) {
               return DateTimeUtils.getSundayDayOfWeek(var4) - 1;
            }
         case 20:
            return getLocalDayOfWeek(var4);
      }
   }

   private static Value truncateDate(SessionLocal var0, int var1, Value var2) {
      long[] var3 = DateTimeUtils.dateAndTimeFromValue(var2, var0);
      long var4 = var3[0];
      long var6 = var3[1];
      int var9;
      switch (var1) {
         case 0:
            var4 = var4 & -512L | 33L;
            var6 = 0L;
            break;
         case 1:
            var4 = var4 & -32L | 1L;
            var6 = 0L;
            break;
         case 2:
            var6 = 0L;
            break;
         case 3:
            var6 = var6 / 3600000000000L * 3600000000000L;
            break;
         case 4:
            var6 = var6 / 60000000000L * 60000000000L;
            break;
         case 5:
            var6 = var6 / 1000000000L * 1000000000L;
            break;
         case 6:
         case 7:
         case 8:
         case 15:
         case 16:
         case 17:
         case 20:
         default:
            throw DbException.getUnsupportedException("DATE_TRUNC " + getFieldName(var1));
         case 9:
            var9 = DateTimeUtils.yearFromDateValue(var4);
            if (var9 > 0) {
               var9 = (var9 - 1) / 1000 * 1000 + 1;
            } else {
               var9 = var9 / 1000 * 1000 - 999;
            }

            var4 = DateTimeUtils.dateValue((long)var9, 1, 1);
            var6 = 0L;
            break;
         case 10:
            var9 = DateTimeUtils.yearFromDateValue(var4);
            if (var9 > 0) {
               var9 = (var9 - 1) / 100 * 100 + 1;
            } else {
               var9 = var9 / 100 * 100 - 99;
            }

            var4 = DateTimeUtils.dateValue((long)var9, 1, 1);
            var6 = 0L;
            break;
         case 11:
            var9 = DateTimeUtils.yearFromDateValue(var4);
            if (var9 >= 0) {
               var9 = var9 / 10 * 10;
            } else {
               var9 = (var9 - 9) / 10 * 10;
            }

            var4 = DateTimeUtils.dateValue((long)var9, 1, 1);
            var6 = 0L;
            break;
         case 12:
            var4 = DateTimeUtils.dateValue((long)DateTimeUtils.yearFromDateValue(var4), (DateTimeUtils.monthFromDateValue(var4) - 1) / 3 * 3 + 1, 1);
            var6 = 0L;
            break;
         case 13:
            var6 = var6 / 1000000L * 1000000L;
            break;
         case 14:
            var6 = var6 / 1000L * 1000L;
            break;
         case 18:
            var4 = truncateToWeek(var4, 1);
            var6 = 0L;
            break;
         case 19:
            var4 = truncateToWeekYear(var4, 1, 4);
            var6 = 0L;
            break;
         case 21:
            var4 = truncateToWeek(var4, getWeekFields().getFirstDayOfWeek().getValue());
            var6 = 0L;
            break;
         case 22:
            WeekFields var8 = getWeekFields();
            var4 = truncateToWeekYear(var4, var8.getFirstDayOfWeek().getValue(), var8.getMinimalDaysInFirstWeek());
      }

      Value var10 = DateTimeUtils.dateTimeToValue(var2, var4, var6);
      if (var0.getMode().getEnum() == Mode.ModeEnum.PostgreSQL && var10.getValueType() == 17) {
         var10 = var10.convertTo(21, var0);
      }

      return var10;
   }

   private static long truncateToWeek(long var0, int var2) {
      long var3 = DateTimeUtils.absoluteDayFromDateValue(var0);
      int var5 = DateTimeUtils.getDayOfWeekFromAbsolute(var3, var2);
      if (var5 != 1) {
         var0 = DateTimeUtils.dateValueFromAbsoluteDay(var3 - (long)var5 + 1L);
      }

      return var0;
   }

   private static long truncateToWeekYear(long var0, int var2, int var3) {
      long var4 = DateTimeUtils.absoluteDayFromDateValue(var0);
      int var6 = DateTimeUtils.yearFromDateValue(var0);
      long var7 = DateTimeUtils.getWeekYearAbsoluteStart(var6, var2, var3);
      if (var4 < var7) {
         var7 = DateTimeUtils.getWeekYearAbsoluteStart(var6 - 1, var2, var3);
      } else if (DateTimeUtils.monthFromDateValue(var0) == 12 && 24 + var3 < DateTimeUtils.dayFromDateValue(var0)) {
         long var9 = DateTimeUtils.getWeekYearAbsoluteStart(var6 + 1, var2, var3);
         if (var4 >= var9) {
            var7 = var9;
         }
      }

      return DateTimeUtils.dateValueFromAbsoluteDay(var7);
   }

   public static Value dateadd(SessionLocal var0, int var1, long var2, Value var4) {
      if (var1 == 13 || var1 == 14 || var1 == 15 || var2 <= 2147483647L && var2 >= -2147483648L) {
         long[] var5 = DateTimeUtils.dateAndTimeFromValue(var4, var0);
         long var6 = var5[0];
         long var8 = var5[1];
         int var10 = var4.getValueType();
         switch (var1) {
            case 0:
               return addYearsMonths(var1, true, var2, var4, var10, var6, var8);
            case 1:
               return addYearsMonths(var1, false, var2, var4, var10, var6, var8);
            case 3:
               var2 *= 3600000000000L;
               break;
            case 4:
               var2 *= 60000000000L;
               break;
            case 5:
            case 23:
               var2 *= 1000000000L;
               break;
            case 6:
               return addToTimeZone(var1, var2 * 3600L, var4, var10, var6, var8);
            case 7:
               return addToTimeZone(var1, var2 * 60L, var4, var10, var6, var8);
            case 8:
               return addToTimeZone(var1, var2, var4, var10, var6, var8);
            case 9:
               return addYearsMonths(var1, true, var2 * 1000L, var4, var10, var6, var8);
            case 10:
               return addYearsMonths(var1, true, var2 * 100L, var4, var10, var6, var8);
            case 11:
               return addYearsMonths(var1, true, var2 * 10L, var4, var10, var6, var8);
            case 12:
               return addYearsMonths(var1, false, var2 *= 3L, var4, var10, var6, var8);
            case 13:
               var2 *= 1000000L;
               break;
            case 14:
               var2 *= 1000L;
            case 15:
               break;
            case 18:
            case 21:
               var2 *= 7L;
            case 2:
            case 16:
            case 17:
            case 20:
            case 24:
               if (var10 != 18 && var10 != 19) {
                  var6 = DateTimeUtils.dateValueFromAbsoluteDay(DateTimeUtils.absoluteDayFromDateValue(var6) + var2);
                  return DateTimeUtils.dateTimeToValue(var4, var6, var8);
               }

               throw DbException.getInvalidValueException("DATEADD time part", getFieldName(var1));
            case 19:
            case 22:
            default:
               throw DbException.getUnsupportedException("DATEADD " + getFieldName(var1));
         }

         var8 += var2;
         if (var8 >= 86400000000000L || var8 < 0L) {
            long var11;
            if (var8 >= 86400000000000L) {
               var11 = var8 / 86400000000000L;
            } else {
               var11 = (var8 - 86400000000000L + 1L) / 86400000000000L;
            }

            var6 = DateTimeUtils.dateValueFromAbsoluteDay(DateTimeUtils.absoluteDayFromDateValue(var6) + var11);
            var8 -= var11 * 86400000000000L;
         }

         return (Value)(var10 == 17 ? ValueTimestamp.fromDateValueAndNanos(var6, var8) : DateTimeUtils.dateTimeToValue(var4, var6, var8));
      } else {
         throw DbException.getInvalidValueException("DATEADD count", var2);
      }
   }

   private static Value addYearsMonths(int var0, boolean var1, long var2, Value var4, int var5, long var6, long var8) {
      if (var5 != 18 && var5 != 19) {
         long var10 = (long)DateTimeUtils.yearFromDateValue(var6);
         long var12 = (long)DateTimeUtils.monthFromDateValue(var6);
         if (var1) {
            var10 += var2;
         } else {
            var12 += var2;
         }

         return DateTimeUtils.dateTimeToValue(var4, DateTimeUtils.dateValueFromDenormalizedDate(var10, var12, DateTimeUtils.dayFromDateValue(var6)), var8);
      } else {
         throw DbException.getInvalidValueException("DATEADD time part", getFieldName(var0));
      }
   }

   private static Value addToTimeZone(int var0, long var1, Value var3, int var4, long var5, long var7) {
      if (var4 == 21) {
         return ValueTimestampTimeZone.fromDateValueAndNanos(var5, var7, MathUtils.convertLongToInt(var1 + (long)((ValueTimestampTimeZone)var3).getTimeZoneOffsetSeconds()));
      } else if (var4 == 19) {
         return ValueTimeTimeZone.fromNanos(var7, MathUtils.convertLongToInt(var1 + (long)((ValueTimeTimeZone)var3).getTimeZoneOffsetSeconds()));
      } else {
         throw DbException.getUnsupportedException("DATEADD " + getFieldName(var0));
      }
   }

   private static long datediff(SessionLocal var0, int var1, Value var2, Value var3) {
      long[] var4 = DateTimeUtils.dateAndTimeFromValue(var2, var0);
      long var5 = var4[0];
      long var7 = DateTimeUtils.absoluteDayFromDateValue(var5);
      long[] var9 = DateTimeUtils.dateAndTimeFromValue(var3, var0);
      long var10 = var9[0];
      long var12 = DateTimeUtils.absoluteDayFromDateValue(var10);
      switch (var1) {
         case 0:
            return (long)(DateTimeUtils.yearFromDateValue(var10) - DateTimeUtils.yearFromDateValue(var5));
         case 1:
            return (long)((DateTimeUtils.yearFromDateValue(var10) - DateTimeUtils.yearFromDateValue(var5)) * 12 + DateTimeUtils.monthFromDateValue(var10) - DateTimeUtils.monthFromDateValue(var5));
         case 3:
         case 4:
         case 5:
         case 13:
         case 14:
         case 15:
         case 23:
            long var14 = var4[1];
            long var16 = var9[1];
            switch (var1) {
               case 3:
                  return (var12 - var7) * 24L + (var16 / 3600000000000L - var14 / 3600000000000L);
               case 4:
                  return (var12 - var7) * 1440L + (var16 / 60000000000L - var14 / 60000000000L);
               case 5:
               case 23:
                  return (var12 - var7) * 86400L + (var16 / 1000000000L - var14 / 1000000000L);
               case 6:
               case 7:
               case 8:
               case 9:
               case 10:
               case 11:
               case 12:
               case 16:
               case 17:
               case 18:
               case 19:
               case 20:
               case 21:
               case 22:
               default:
                  break;
               case 13:
                  return (var12 - var7) * 86400000L + (var16 / 1000000L - var14 / 1000000L);
               case 14:
                  return (var12 - var7) * 86400000000L + (var16 / 1000L - var14 / 1000L);
               case 15:
                  return (var12 - var7) * 86400000000000L + (var16 - var14);
            }
         case 2:
         case 16:
         case 17:
         case 20:
         case 24:
            return var12 - var7;
         case 6:
         case 7:
         case 8:
            int var18;
            if (var2 instanceof ValueTimestampTimeZone) {
               var18 = ((ValueTimestampTimeZone)var2).getTimeZoneOffsetSeconds();
            } else if (var2 instanceof ValueTimeTimeZone) {
               var18 = ((ValueTimeTimeZone)var2).getTimeZoneOffsetSeconds();
            } else {
               var18 = var0.currentTimeZone().getTimeZoneOffsetLocal(var5, var4[1]);
            }

            int var19;
            if (var3 instanceof ValueTimestampTimeZone) {
               var19 = ((ValueTimestampTimeZone)var3).getTimeZoneOffsetSeconds();
            } else if (var3 instanceof ValueTimeTimeZone) {
               var19 = ((ValueTimeTimeZone)var3).getTimeZoneOffsetSeconds();
            } else {
               var19 = var0.currentTimeZone().getTimeZoneOffsetLocal(var10, var9[1]);
            }

            if (var1 == 6) {
               return (long)(var19 / 3600 - var18 / 3600);
            } else {
               if (var1 == 7) {
                  return (long)(var19 / 60 - var18 / 60);
               }

               return (long)(var19 - var18);
            }
         case 9:
            return (long)(millennium(DateTimeUtils.yearFromDateValue(var10)) - millennium(DateTimeUtils.yearFromDateValue(var5)));
         case 10:
            return (long)(century(DateTimeUtils.yearFromDateValue(var10)) - century(DateTimeUtils.yearFromDateValue(var5)));
         case 11:
            return (long)(decade(DateTimeUtils.yearFromDateValue(var10)) - decade(DateTimeUtils.yearFromDateValue(var5)));
         case 12:
            return (long)((DateTimeUtils.yearFromDateValue(var10) - DateTimeUtils.yearFromDateValue(var5)) * 4 + (DateTimeUtils.monthFromDateValue(var10) - 1) / 3 - (DateTimeUtils.monthFromDateValue(var5) - 1) / 3);
         case 18:
            return weekdiff(var7, var12, 1);
         case 19:
         case 22:
         default:
            throw DbException.getUnsupportedException("DATEDIFF " + getFieldName(var1));
         case 21:
            return weekdiff(var7, var12, getWeekFields().getFirstDayOfWeek().getValue());
      }
   }

   private static long weekdiff(long var0, long var2, int var4) {
      var0 += (long)(4 - var4);
      long var5 = var0 / 7L;
      if (var0 < 0L && var5 * 7L != var0) {
         --var5;
      }

      var2 += (long)(4 - var4);
      long var7 = var2 / 7L;
      if (var2 < 0L && var7 * 7L != var2) {
         --var7;
      }

      return var7 - var5;
   }

   private static int millennium(int var0) {
      return var0 > 0 ? (var0 + 999) / 1000 : var0 / 1000;
   }

   private static int century(int var0) {
      return var0 > 0 ? (var0 + 99) / 100 : var0 / 100;
   }

   private static int decade(int var0) {
      return var0 >= 0 ? var0 / 10 : (var0 - 9) / 10;
   }

   private static int getLocalDayOfWeek(long var0) {
      return DateTimeUtils.getDayOfWeek(var0, getWeekFields().getFirstDayOfWeek().getValue());
   }

   private static int getLocalWeekOfYear(long var0) {
      WeekFields var2 = getWeekFields();
      return DateTimeUtils.getWeekOfYear(var0, var2.getFirstDayOfWeek().getValue(), var2.getMinimalDaysInFirstWeek());
   }

   private static WeekFields getWeekFields() {
      WeekFields var0 = WEEK_FIELDS;
      if (var0 == null) {
         WEEK_FIELDS = var0 = WeekFields.of(Locale.getDefault());
      }

      return var0;
   }

   private static ValueNumeric extractEpoch(SessionLocal var0, Value var1) {
      long var4;
      long var6;
      if (var1 instanceof ValueInterval) {
         ValueInterval var9 = (ValueInterval)var1;
         if (var9.getQualifier().isYearMonth()) {
            var9 = (ValueInterval)var9.convertTo(TypeInfo.TYPE_INTERVAL_YEAR_TO_MONTH);
            var4 = var9.getLeading();
            var6 = var9.getRemaining();
            BigInteger var10 = BigInteger.valueOf(var4).multiply(BigInteger.valueOf(31557600L)).add(BigInteger.valueOf(var6 * 2592000L));
            if (var9.isNegative()) {
               var10 = var10.negate();
            }

            return ValueNumeric.get(var10);
         } else {
            return ValueNumeric.get((new BigDecimal(IntervalUtils.intervalToAbsolute(var9))).divide(BD_NANOS_PER_SECOND));
         }
      } else {
         long[] var3 = DateTimeUtils.dateAndTimeFromValue(var1, var0);
         var4 = var3[0];
         var6 = var3[1];
         ValueNumeric var2;
         if (var1 instanceof ValueTime) {
            var2 = ValueNumeric.get(BigDecimal.valueOf(var6).divide(BD_NANOS_PER_SECOND));
         } else if (var1 instanceof ValueDate) {
            var2 = ValueNumeric.get(BigInteger.valueOf(DateTimeUtils.absoluteDayFromDateValue(var4)).multiply(BI_SECONDS_PER_DAY));
         } else {
            BigDecimal var8 = BigDecimal.valueOf(var6).divide(BD_NANOS_PER_SECOND).add(BigDecimal.valueOf(DateTimeUtils.absoluteDayFromDateValue(var4)).multiply(BD_SECONDS_PER_DAY));
            if (var1 instanceof ValueTimestampTimeZone) {
               var2 = ValueNumeric.get(var8.subtract(BigDecimal.valueOf((long)((ValueTimestampTimeZone)var1).getTimeZoneOffsetSeconds())));
            } else if (var1 instanceof ValueTimeTimeZone) {
               var2 = ValueNumeric.get(var8.subtract(BigDecimal.valueOf((long)((ValueTimeTimeZone)var1).getTimeZoneOffsetSeconds())));
            } else {
               var2 = ValueNumeric.get(var8);
            }
         }

         return var2;
      }
   }

   public Expression optimize(SessionLocal var1) {
      this.left = this.left.optimize(var1);
      if (this.right != null) {
         this.right = this.right.optimize(var1);
      }

      int var2;
      switch (this.function) {
         case 0:
            this.type = this.field == 23 ? TypeInfo.getTypeInfo(13, 28L, 9, (ExtTypeInfo)null) : TypeInfo.TYPE_INTEGER;
            break;
         case 1:
            this.type = this.left.getType();
            var2 = this.type.getValueType();
            if (!DataType.isDateTimeType(var2)) {
               throw DbException.getInvalidExpressionTypeException("DATE_TRUNC datetime argument", this.left);
            }

            if (var1.getMode().getEnum() == Mode.ModeEnum.PostgreSQL && var2 == 17) {
               this.type = TypeInfo.TYPE_TIMESTAMP_TZ;
            }
            break;
         case 2:
            var2 = this.right.getType().getValueType();
            if (var2 == 17) {
               switch (this.field) {
                  case 3:
                  case 4:
                  case 5:
                  case 13:
                  case 14:
                  case 15:
                  case 23:
                     var2 = 20;
                  case 6:
                  case 7:
                  case 8:
                  case 9:
                  case 10:
                  case 11:
                  case 12:
                  case 16:
                  case 17:
                  case 18:
                  case 19:
                  case 20:
                  case 21:
                  case 22:
               }
            }

            this.type = TypeInfo.getTypeInfo(var2);
            break;
         case 3:
            this.type = TypeInfo.TYPE_BIGINT;
            break;
         default:
            throw DbException.getInternalError("function=" + this.function);
      }

      return (Expression)(!this.left.isConstant() || this.right != null && !this.right.isConstant() ? this : TypedValueExpression.getTypedIfNull(this.getValue(var1), this.type));
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      var1.append(this.getName()).append('(').append(getFieldName(this.field));
      switch (this.function) {
         case 0:
            this.left.getUnenclosedSQL(var1.append(" FROM "), var2);
            break;
         case 1:
            this.left.getUnenclosedSQL(var1.append(", "), var2);
            break;
         case 2:
         case 3:
            this.left.getUnenclosedSQL(var1.append(", "), var2).append(", ");
            this.right.getUnenclosedSQL(var1, var2);
            break;
         default:
            throw DbException.getInternalError("function=" + this.function);
      }

      return var1.append(')');
   }

   public String getName() {
      return NAMES[this.function];
   }
}
