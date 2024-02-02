/*      */ package org.h2.expression.function;
/*      */ 
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import java.time.temporal.WeekFields;
/*      */ import java.util.Locale;
/*      */ import org.h2.api.IntervalQualifier;
/*      */ import org.h2.engine.CastDataProvider;
/*      */ import org.h2.engine.Mode;
/*      */ import org.h2.engine.SessionLocal;
/*      */ import org.h2.expression.Expression;
/*      */ import org.h2.expression.TypedValueExpression;
/*      */ import org.h2.message.DbException;
/*      */ import org.h2.util.DateTimeUtils;
/*      */ import org.h2.util.IntervalUtils;
/*      */ import org.h2.util.MathUtils;
/*      */ import org.h2.util.StringUtils;
/*      */ import org.h2.value.DataType;
/*      */ import org.h2.value.TypeInfo;
/*      */ import org.h2.value.Value;
/*      */ import org.h2.value.ValueBigint;
/*      */ import org.h2.value.ValueInteger;
/*      */ import org.h2.value.ValueInterval;
/*      */ import org.h2.value.ValueNumeric;
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
/*      */ public final class DateTimeFunction
/*      */   extends Function1_2
/*      */ {
/*      */   public static final int EXTRACT = 0;
/*      */   public static final int DATE_TRUNC = 1;
/*      */   public static final int DATEADD = 2;
/*      */   public static final int DATEDIFF = 3;
/*   67 */   private static final String[] NAMES = new String[] { "EXTRACT", "DATE_TRUNC", "DATEADD", "DATEDIFF" };
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int YEAR = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int MONTH = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int DAY = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int HOUR = 3;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int MINUTE = 4;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int SECOND = 5;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TIMEZONE_HOUR = 6;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TIMEZONE_MINUTE = 7;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int TIMEZONE_SECOND = 8;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int MILLENNIUM = 9;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int CENTURY = 10;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int DECADE = 11;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int QUARTER = 12;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int MILLISECOND = 13;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int MICROSECOND = 14;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int NANOSECOND = 15;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int DAY_OF_YEAR = 16;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int ISO_DAY_OF_WEEK = 17;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int ISO_WEEK = 18;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int ISO_WEEK_YEAR = 19;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int DAY_OF_WEEK = 20;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int WEEK = 21;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int WEEK_YEAR = 22;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int EPOCH = 23;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int DOW = 24;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final int FIELDS_COUNT = 25;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  202 */   private static final String[] FIELD_NAMES = new String[] { "YEAR", "MONTH", "DAY", "HOUR", "MINUTE", "SECOND", "TIMEZONE_HOUR", "TIMEZONE_MINUTE", "TIMEZONE_SECOND", "MILLENNIUM", "CENTURY", "DECADE", "QUARTER", "MILLISECOND", "MICROSECOND", "NANOSECOND", "DAY_OF_YEAR", "ISO_DAY_OF_WEEK", "ISO_WEEK", "ISO_WEEK_YEAR", "DAY_OF_WEEK", "WEEK", "WEEK_YEAR", "EPOCH", "DOW" };
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
/*  215 */   private static final BigDecimal BD_SECONDS_PER_DAY = new BigDecimal(86400L);
/*      */   
/*  217 */   private static final BigInteger BI_SECONDS_PER_DAY = BigInteger.valueOf(86400L);
/*      */   
/*  219 */   private static final BigDecimal BD_NANOS_PER_SECOND = new BigDecimal(1000000000L);
/*      */ 
/*      */ 
/*      */   
/*      */   private static volatile WeekFields WEEK_FIELDS;
/*      */ 
/*      */ 
/*      */   
/*      */   private final int function;
/*      */ 
/*      */ 
/*      */   
/*      */   private final int field;
/*      */ 
/*      */ 
/*      */   
/*      */   public static int getField(String paramString) {
/*  236 */     switch (StringUtils.toUpperEnglish(paramString)) {
/*      */       case "YEAR":
/*      */       case "YY":
/*      */       case "YYYY":
/*      */       case "SQL_TSI_YEAR":
/*  241 */         return 0;
/*      */       case "MONTH":
/*      */       case "M":
/*      */       case "MM":
/*      */       case "SQL_TSI_MONTH":
/*  246 */         return 1;
/*      */       case "DAY":
/*      */       case "D":
/*      */       case "DD":
/*      */       case "SQL_TSI_DAY":
/*  251 */         return 2;
/*      */       case "HOUR":
/*      */       case "HH":
/*      */       case "SQL_TSI_HOUR":
/*  255 */         return 3;
/*      */       case "MINUTE":
/*      */       case "MI":
/*      */       case "N":
/*      */       case "SQL_TSI_MINUTE":
/*  260 */         return 4;
/*      */       case "SECOND":
/*      */       case "S":
/*      */       case "SS":
/*      */       case "SQL_TSI_SECOND":
/*  265 */         return 5;
/*      */       case "TIMEZONE_HOUR":
/*  267 */         return 6;
/*      */       case "TIMEZONE_MINUTE":
/*  269 */         return 7;
/*      */       case "TIMEZONE_SECOND":
/*  271 */         return 8;
/*      */       case "MILLENNIUM":
/*  273 */         return 9;
/*      */       case "CENTURY":
/*  275 */         return 10;
/*      */       case "DECADE":
/*  277 */         return 11;
/*      */       case "QUARTER":
/*  279 */         return 12;
/*      */       case "MILLISECOND":
/*      */       case "MILLISECONDS":
/*      */       case "MS":
/*  283 */         return 13;
/*      */       case "MICROSECOND":
/*      */       case "MICROSECONDS":
/*      */       case "MCS":
/*  287 */         return 14;
/*      */       case "NANOSECOND":
/*      */       case "NS":
/*  290 */         return 15;
/*      */       case "DAY_OF_YEAR":
/*      */       case "DAYOFYEAR":
/*      */       case "DY":
/*      */       case "DOY":
/*  295 */         return 16;
/*      */       case "ISO_DAY_OF_WEEK":
/*      */       case "ISODOW":
/*  298 */         return 17;
/*      */       case "ISO_WEEK":
/*  300 */         return 18;
/*      */       case "ISO_WEEK_YEAR":
/*      */       case "ISO_YEAR":
/*      */       case "ISOYEAR":
/*  304 */         return 19;
/*      */       case "DAY_OF_WEEK":
/*      */       case "DAYOFWEEK":
/*  307 */         return 20;
/*      */       case "WEEK":
/*      */       case "WK":
/*      */       case "WW":
/*      */       case "SQL_TSI_WEEK":
/*  312 */         return 21;
/*      */       case "WEEK_YEAR":
/*  314 */         return 22;
/*      */       case "EPOCH":
/*  316 */         return 23;
/*      */       case "DOW":
/*  318 */         return 24;
/*      */     } 
/*  320 */     throw DbException.getInvalidValueException("date-time field", paramString);
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
/*      */   public static String getFieldName(int paramInt) {
/*  332 */     if (paramInt < 0 || paramInt >= 25) {
/*  333 */       throw DbException.getUnsupportedException("datetime field " + paramInt);
/*      */     }
/*  335 */     return FIELD_NAMES[paramInt];
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public DateTimeFunction(int paramInt1, int paramInt2, Expression paramExpression1, Expression paramExpression2) {
/*  341 */     super(paramExpression1, paramExpression2);
/*  342 */     this.function = paramInt1;
/*  343 */     this.field = paramInt2;
/*      */   }
/*      */ 
/*      */   
/*      */   public Value getValue(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2) {
/*  348 */     switch (this.function) {
/*      */       case 0:
/*  350 */         paramValue1 = (this.field == 23) ? (Value)extractEpoch(paramSessionLocal, paramValue1) : (Value)ValueInteger.get(extractInteger(paramSessionLocal, paramValue1, this.field));
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
/*  364 */         return paramValue1;case 1: paramValue1 = truncateDate(paramSessionLocal, this.field, paramValue1); return paramValue1;case 2: paramValue1 = dateadd(paramSessionLocal, this.field, paramValue1.getLong(), paramValue2); return paramValue1;
/*      */       case 3:
/*      */         return (Value)ValueBigint.get(datediff(paramSessionLocal, this.field, paramValue1, paramValue2));
/*      */     } 
/*      */     throw DbException.getInternalError("function=" + this.function);
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
/*      */   private static int extractInteger(SessionLocal paramSessionLocal, Value paramValue, int paramInt) {
/*  380 */     return (paramValue instanceof ValueInterval) ? extractInterval(paramValue, paramInt) : extractDateTime(paramSessionLocal, paramValue, paramInt);
/*      */   }
/*      */   
/*      */   private static int extractInterval(Value paramValue, int paramInt) { long l3;
/*  384 */     ValueInterval valueInterval = (ValueInterval)paramValue;
/*  385 */     IntervalQualifier intervalQualifier = valueInterval.getQualifier();
/*  386 */     boolean bool = valueInterval.isNegative();
/*  387 */     long l1 = valueInterval.getLeading(), l2 = valueInterval.getRemaining();
/*      */     
/*  389 */     switch (paramInt) {
/*      */       case 0:
/*  391 */         l3 = IntervalUtils.yearsFromInterval(intervalQualifier, bool, l1, l2);
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
/*  421 */         return (int)l3;case 1: l3 = IntervalUtils.monthsFromInterval(intervalQualifier, bool, l1, l2); return (int)l3;case 2: case 16: l3 = IntervalUtils.daysFromInterval(intervalQualifier, bool, l1, l2); return (int)l3;case 3: l3 = IntervalUtils.hoursFromInterval(intervalQualifier, bool, l1, l2); return (int)l3;case 4: l3 = IntervalUtils.minutesFromInterval(intervalQualifier, bool, l1, l2); return (int)l3;case 5: l3 = IntervalUtils.nanosFromInterval(intervalQualifier, bool, l1, l2) / 1000000000L; return (int)l3;case 13: l3 = IntervalUtils.nanosFromInterval(intervalQualifier, bool, l1, l2) / 1000000L % 1000L; return (int)l3;case 14: l3 = IntervalUtils.nanosFromInterval(intervalQualifier, bool, l1, l2) / 1000L % 1000000L; return (int)l3;case 15: l3 = IntervalUtils.nanosFromInterval(intervalQualifier, bool, l1, l2) % 1000000000L; return (int)l3;
/*      */     } 
/*      */     throw DbException.getUnsupportedException("getDatePart(" + paramValue + ", " + paramInt + ')'); } static int extractDateTime(SessionLocal paramSessionLocal, Value paramValue, int paramInt) { WeekFields weekFields;
/*      */     int i;
/*  425 */     long[] arrayOfLong = DateTimeUtils.dateAndTimeFromValue(paramValue, (CastDataProvider)paramSessionLocal);
/*  426 */     long l1 = arrayOfLong[0];
/*  427 */     long l2 = arrayOfLong[1];
/*  428 */     switch (paramInt) {
/*      */       case 0:
/*  430 */         return DateTimeUtils.yearFromDateValue(l1);
/*      */       case 1:
/*  432 */         return DateTimeUtils.monthFromDateValue(l1);
/*      */       case 2:
/*  434 */         return DateTimeUtils.dayFromDateValue(l1);
/*      */       case 3:
/*  436 */         return (int)(l2 / 3600000000000L % 24L);
/*      */       case 4:
/*  438 */         return (int)(l2 / 60000000000L % 60L);
/*      */       case 5:
/*  440 */         return (int)(l2 / 1000000000L % 60L);
/*      */       case 13:
/*  442 */         return (int)(l2 / 1000000L % 1000L);
/*      */       case 14:
/*  444 */         return (int)(l2 / 1000L % 1000000L);
/*      */       case 15:
/*  446 */         return (int)(l2 % 1000000000L);
/*      */       case 9:
/*  448 */         return millennium(DateTimeUtils.yearFromDateValue(l1));
/*      */       case 10:
/*  450 */         return century(DateTimeUtils.yearFromDateValue(l1));
/*      */       case 11:
/*  452 */         return decade(DateTimeUtils.yearFromDateValue(l1));
/*      */       case 16:
/*  454 */         return DateTimeUtils.getDayOfYear(l1);
/*      */       case 24:
/*  456 */         if (paramSessionLocal.getMode().getEnum() == Mode.ModeEnum.PostgreSQL) {
/*  457 */           return DateTimeUtils.getSundayDayOfWeek(l1) - 1;
/*      */         }
/*      */       
/*      */       case 20:
/*  461 */         return getLocalDayOfWeek(l1);
/*      */       case 21:
/*  463 */         return getLocalWeekOfYear(l1);
/*      */       case 22:
/*  465 */         weekFields = getWeekFields();
/*  466 */         return DateTimeUtils.getWeekYear(l1, weekFields.getFirstDayOfWeek().getValue(), weekFields
/*  467 */             .getMinimalDaysInFirstWeek());
/*      */       
/*      */       case 12:
/*  470 */         return (DateTimeUtils.monthFromDateValue(l1) - 1) / 3 + 1;
/*      */       case 19:
/*  472 */         return DateTimeUtils.getIsoWeekYear(l1);
/*      */       case 18:
/*  474 */         return DateTimeUtils.getIsoWeekOfYear(l1);
/*      */       case 17:
/*  476 */         return DateTimeUtils.getIsoDayOfWeek(l1);
/*      */       
/*      */       case 6:
/*      */       case 7:
/*      */       case 8:
/*  481 */         if (paramValue instanceof ValueTimestampTimeZone) {
/*  482 */           i = ((ValueTimestampTimeZone)paramValue).getTimeZoneOffsetSeconds();
/*  483 */         } else if (paramValue instanceof ValueTimeTimeZone) {
/*  484 */           i = ((ValueTimeTimeZone)paramValue).getTimeZoneOffsetSeconds();
/*      */         } else {
/*  486 */           i = paramSessionLocal.currentTimeZone().getTimeZoneOffsetLocal(l1, l2);
/*      */         } 
/*  488 */         if (paramInt == 6)
/*  489 */           return i / 3600; 
/*  490 */         if (paramInt == 7) {
/*  491 */           return i % 3600 / 60;
/*      */         }
/*  493 */         return i % 60;
/*      */     } 
/*      */ 
/*      */     
/*  497 */     throw DbException.getUnsupportedException("EXTRACT(" + getFieldName(paramInt) + " FROM " + paramValue + ')'); }
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
/*      */   private static Value truncateDate(SessionLocal paramSessionLocal, int paramInt, Value paramValue) {
/*      */     WeekFields weekFields;
/*      */     int i;
/*  513 */     long[] arrayOfLong = DateTimeUtils.dateAndTimeFromValue(paramValue, (CastDataProvider)paramSessionLocal);
/*  514 */     long l1 = arrayOfLong[0];
/*  515 */     long l2 = arrayOfLong[1];
/*  516 */     switch (paramInt) {
/*      */       case 14:
/*  518 */         l2 = l2 / 1000L * 1000L;
/*      */         break;
/*      */       case 13:
/*  521 */         l2 = l2 / 1000000L * 1000000L;
/*      */         break;
/*      */       case 5:
/*  524 */         l2 = l2 / 1000000000L * 1000000000L;
/*      */         break;
/*      */       case 4:
/*  527 */         l2 = l2 / 60000000000L * 60000000000L;
/*      */         break;
/*      */       case 3:
/*  530 */         l2 = l2 / 3600000000000L * 3600000000000L;
/*      */         break;
/*      */       case 2:
/*  533 */         l2 = 0L;
/*      */         break;
/*      */       case 18:
/*  536 */         l1 = truncateToWeek(l1, 1);
/*  537 */         l2 = 0L;
/*      */         break;
/*      */       case 21:
/*  540 */         l1 = truncateToWeek(l1, getWeekFields().getFirstDayOfWeek().getValue());
/*  541 */         l2 = 0L;
/*      */         break;
/*      */       case 19:
/*  544 */         l1 = truncateToWeekYear(l1, 1, 4);
/*  545 */         l2 = 0L;
/*      */         break;
/*      */       case 22:
/*  548 */         weekFields = getWeekFields();
/*  549 */         l1 = truncateToWeekYear(l1, weekFields.getFirstDayOfWeek().getValue(), weekFields
/*  550 */             .getMinimalDaysInFirstWeek());
/*      */         break;
/*      */       
/*      */       case 1:
/*  554 */         l1 = l1 & 0xFFFFFFFFFFFFFFE0L | 0x1L;
/*  555 */         l2 = 0L;
/*      */         break;
/*      */       case 12:
/*  558 */         l1 = DateTimeUtils.dateValue(DateTimeUtils.yearFromDateValue(l1), (
/*  559 */             DateTimeUtils.monthFromDateValue(l1) - 1) / 3 * 3 + 1, 1);
/*  560 */         l2 = 0L;
/*      */         break;
/*      */       case 0:
/*  563 */         l1 = l1 & 0xFFFFFFFFFFFFFE00L | 0x21L;
/*  564 */         l2 = 0L;
/*      */         break;
/*      */       case 11:
/*  567 */         i = DateTimeUtils.yearFromDateValue(l1);
/*  568 */         if (i >= 0) {
/*  569 */           i = i / 10 * 10;
/*      */         } else {
/*  571 */           i = (i - 9) / 10 * 10;
/*      */         } 
/*  573 */         l1 = DateTimeUtils.dateValue(i, 1, 1);
/*  574 */         l2 = 0L;
/*      */         break;
/*      */       
/*      */       case 10:
/*  578 */         i = DateTimeUtils.yearFromDateValue(l1);
/*  579 */         if (i > 0) {
/*  580 */           i = (i - 1) / 100 * 100 + 1;
/*      */         } else {
/*  582 */           i = i / 100 * 100 - 99;
/*      */         } 
/*  584 */         l1 = DateTimeUtils.dateValue(i, 1, 1);
/*  585 */         l2 = 0L;
/*      */         break;
/*      */       
/*      */       case 9:
/*  589 */         i = DateTimeUtils.yearFromDateValue(l1);
/*  590 */         if (i > 0) {
/*  591 */           i = (i - 1) / 1000 * 1000 + 1;
/*      */         } else {
/*  593 */           i = i / 1000 * 1000 - 999;
/*      */         } 
/*  595 */         l1 = DateTimeUtils.dateValue(i, 1, 1);
/*  596 */         l2 = 0L;
/*      */         break;
/*      */       
/*      */       default:
/*  600 */         throw DbException.getUnsupportedException("DATE_TRUNC " + getFieldName(paramInt));
/*      */     } 
/*  602 */     Value value = DateTimeUtils.dateTimeToValue(paramValue, l1, l2);
/*  603 */     if (paramSessionLocal.getMode().getEnum() == Mode.ModeEnum.PostgreSQL && value.getValueType() == 17) {
/*  604 */       value = value.convertTo(21, (CastDataProvider)paramSessionLocal);
/*      */     }
/*  606 */     return value;
/*      */   }
/*      */   
/*      */   private static long truncateToWeek(long paramLong, int paramInt) {
/*  610 */     long l = DateTimeUtils.absoluteDayFromDateValue(paramLong);
/*  611 */     int i = DateTimeUtils.getDayOfWeekFromAbsolute(l, paramInt);
/*  612 */     if (i != 1) {
/*  613 */       paramLong = DateTimeUtils.dateValueFromAbsoluteDay(l - i + 1L);
/*      */     }
/*  615 */     return paramLong;
/*      */   }
/*      */   
/*      */   private static long truncateToWeekYear(long paramLong, int paramInt1, int paramInt2) {
/*  619 */     long l1 = DateTimeUtils.absoluteDayFromDateValue(paramLong);
/*  620 */     int i = DateTimeUtils.yearFromDateValue(paramLong);
/*  621 */     long l2 = DateTimeUtils.getWeekYearAbsoluteStart(i, paramInt1, paramInt2);
/*  622 */     if (l1 < l2) {
/*  623 */       l2 = DateTimeUtils.getWeekYearAbsoluteStart(i - 1, paramInt1, paramInt2);
/*  624 */     } else if (DateTimeUtils.monthFromDateValue(paramLong) == 12 && 24 + paramInt2 < 
/*  625 */       DateTimeUtils.dayFromDateValue(paramLong)) {
/*  626 */       long l = DateTimeUtils.getWeekYearAbsoluteStart(i + 1, paramInt1, paramInt2);
/*  627 */       if (l1 >= l) {
/*  628 */         l2 = l;
/*      */       }
/*      */     } 
/*  631 */     return DateTimeUtils.dateValueFromAbsoluteDay(l2);
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
/*      */   public static Value dateadd(SessionLocal paramSessionLocal, int paramInt, long paramLong, Value paramValue) {
/*  648 */     if (paramInt != 13 && paramInt != 14 && paramInt != 15 && (paramLong > 2147483647L || paramLong < -2147483648L))
/*      */     {
/*  650 */       throw DbException.getInvalidValueException("DATEADD count", Long.valueOf(paramLong));
/*      */     }
/*  652 */     long[] arrayOfLong = DateTimeUtils.dateAndTimeFromValue(paramValue, (CastDataProvider)paramSessionLocal);
/*  653 */     long l1 = arrayOfLong[0];
/*  654 */     long l2 = arrayOfLong[1];
/*  655 */     int i = paramValue.getValueType();
/*  656 */     switch (paramInt) {
/*      */       case 9:
/*  658 */         return addYearsMonths(paramInt, true, paramLong * 1000L, paramValue, i, l1, l2);
/*      */       case 10:
/*  660 */         return addYearsMonths(paramInt, true, paramLong * 100L, paramValue, i, l1, l2);
/*      */       case 11:
/*  662 */         return addYearsMonths(paramInt, true, paramLong * 10L, paramValue, i, l1, l2);
/*      */       case 0:
/*  664 */         return addYearsMonths(paramInt, true, paramLong, paramValue, i, l1, l2);
/*      */       case 12:
/*  666 */         return addYearsMonths(paramInt, false, paramLong *= 3L, paramValue, i, l1, l2);
/*      */       case 1:
/*  668 */         return addYearsMonths(paramInt, false, paramLong, paramValue, i, l1, l2);
/*      */       case 18:
/*      */       case 21:
/*  671 */         paramLong *= 7L;
/*      */       
/*      */       case 2:
/*      */       case 16:
/*      */       case 17:
/*      */       case 20:
/*      */       case 24:
/*  678 */         if (i == 18 || i == 19) {
/*  679 */           throw DbException.getInvalidValueException("DATEADD time part", getFieldName(paramInt));
/*      */         }
/*      */         
/*  682 */         l1 = DateTimeUtils.dateValueFromAbsoluteDay(DateTimeUtils.absoluteDayFromDateValue(l1) + paramLong);
/*  683 */         return DateTimeUtils.dateTimeToValue(paramValue, l1, l2);
/*      */       case 3:
/*  685 */         paramLong *= 3600000000000L;
/*      */         break;
/*      */       case 4:
/*  688 */         paramLong *= 60000000000L;
/*      */         break;
/*      */       case 5:
/*      */       case 23:
/*  692 */         paramLong *= 1000000000L;
/*      */         break;
/*      */       case 13:
/*  695 */         paramLong *= 1000000L;
/*      */         break;
/*      */       case 14:
/*  698 */         paramLong *= 1000L;
/*      */         break;
/*      */       case 15:
/*      */         break;
/*      */       case 6:
/*  703 */         return addToTimeZone(paramInt, paramLong * 3600L, paramValue, i, l1, l2);
/*      */       case 7:
/*  705 */         return addToTimeZone(paramInt, paramLong * 60L, paramValue, i, l1, l2);
/*      */       case 8:
/*  707 */         return addToTimeZone(paramInt, paramLong, paramValue, i, l1, l2);
/*      */       default:
/*  709 */         throw DbException.getUnsupportedException("DATEADD " + getFieldName(paramInt));
/*      */     } 
/*  711 */     l2 += paramLong;
/*  712 */     if (l2 >= 86400000000000L || l2 < 0L) {
/*      */       long l;
/*  714 */       if (l2 >= 86400000000000L) {
/*  715 */         l = l2 / 86400000000000L;
/*      */       } else {
/*  717 */         l = (l2 - 86400000000000L + 1L) / 86400000000000L;
/*      */       } 
/*  719 */       l1 = DateTimeUtils.dateValueFromAbsoluteDay(DateTimeUtils.absoluteDayFromDateValue(l1) + l);
/*  720 */       l2 -= l * 86400000000000L;
/*      */     } 
/*  722 */     if (i == 17) {
/*  723 */       return (Value)ValueTimestamp.fromDateValueAndNanos(l1, l2);
/*      */     }
/*  725 */     return DateTimeUtils.dateTimeToValue(paramValue, l1, l2);
/*      */   }
/*      */ 
/*      */   
/*      */   private static Value addYearsMonths(int paramInt1, boolean paramBoolean, long paramLong1, Value paramValue, int paramInt2, long paramLong2, long paramLong3) {
/*  730 */     if (paramInt2 == 18 || paramInt2 == 19) {
/*  731 */       throw DbException.getInvalidValueException("DATEADD time part", getFieldName(paramInt1));
/*      */     }
/*  733 */     long l1 = DateTimeUtils.yearFromDateValue(paramLong2);
/*  734 */     long l2 = DateTimeUtils.monthFromDateValue(paramLong2);
/*  735 */     if (paramBoolean) {
/*  736 */       l1 += paramLong1;
/*      */     } else {
/*  738 */       l2 += paramLong1;
/*      */     } 
/*  740 */     return DateTimeUtils.dateTimeToValue(paramValue, 
/*  741 */         DateTimeUtils.dateValueFromDenormalizedDate(l1, l2, DateTimeUtils.dayFromDateValue(paramLong2)), paramLong3);
/*      */   }
/*      */ 
/*      */   
/*      */   private static Value addToTimeZone(int paramInt1, long paramLong1, Value paramValue, int paramInt2, long paramLong2, long paramLong3) {
/*  746 */     if (paramInt2 == 21)
/*  747 */       return (Value)ValueTimestampTimeZone.fromDateValueAndNanos(paramLong2, paramLong3, 
/*  748 */           MathUtils.convertLongToInt(paramLong1 + ((ValueTimestampTimeZone)paramValue).getTimeZoneOffsetSeconds())); 
/*  749 */     if (paramInt2 == 19) {
/*  750 */       return (Value)ValueTimeTimeZone.fromNanos(paramLong3, 
/*  751 */           MathUtils.convertLongToInt(paramLong1 + ((ValueTimeTimeZone)paramValue).getTimeZoneOffsetSeconds()));
/*      */     }
/*  753 */     throw DbException.getUnsupportedException("DATEADD " + getFieldName(paramInt1));
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
/*      */ 
/*      */ 
/*      */   
/*      */   private static long datediff(SessionLocal paramSessionLocal, int paramInt, Value paramValue1, Value paramValue2) {
/*      */     long l5, l6;
/*      */     int i, j;
/*  776 */     long[] arrayOfLong1 = DateTimeUtils.dateAndTimeFromValue(paramValue1, (CastDataProvider)paramSessionLocal);
/*  777 */     long l1 = arrayOfLong1[0];
/*  778 */     long l2 = DateTimeUtils.absoluteDayFromDateValue(l1);
/*  779 */     long[] arrayOfLong2 = DateTimeUtils.dateAndTimeFromValue(paramValue2, (CastDataProvider)paramSessionLocal);
/*  780 */     long l3 = arrayOfLong2[0];
/*  781 */     long l4 = DateTimeUtils.absoluteDayFromDateValue(l3);
/*  782 */     switch (paramInt) {
/*      */       case 3:
/*      */       case 4:
/*      */       case 5:
/*      */       case 13:
/*      */       case 14:
/*      */       case 15:
/*      */       case 23:
/*  790 */         l5 = arrayOfLong1[1];
/*  791 */         l6 = arrayOfLong2[1];
/*  792 */         switch (paramInt) {
/*      */           case 15:
/*  794 */             return (l4 - l2) * 86400000000000L + l6 - l5;
/*      */           case 14:
/*  796 */             return (l4 - l2) * 86400000000L + l6 / 1000L - l5 / 1000L;
/*      */           case 13:
/*  798 */             return (l4 - l2) * 86400000L + l6 / 1000000L - l5 / 1000000L;
/*      */           case 5:
/*      */           case 23:
/*  801 */             return (l4 - l2) * 86400L + l6 / 1000000000L - l5 / 1000000000L;
/*      */           
/*      */           case 4:
/*  804 */             return (l4 - l2) * 1440L + l6 / 60000000000L - l5 / 60000000000L;
/*      */           
/*      */           case 3:
/*  807 */             return (l4 - l2) * 24L + l6 / 3600000000000L - l5 / 3600000000000L;
/*      */         } 
/*      */       
/*      */       
/*      */       case 2:
/*      */       case 16:
/*      */       case 17:
/*      */       case 20:
/*      */       case 24:
/*  816 */         return l4 - l2;
/*      */       case 21:
/*  818 */         return weekdiff(l2, l4, getWeekFields().getFirstDayOfWeek().getValue());
/*      */       case 18:
/*  820 */         return weekdiff(l2, l4, 1);
/*      */       case 1:
/*  822 */         return ((DateTimeUtils.yearFromDateValue(l3) - DateTimeUtils.yearFromDateValue(l1)) * 12 + 
/*  823 */           DateTimeUtils.monthFromDateValue(l3) - DateTimeUtils.monthFromDateValue(l1));
/*      */       case 12:
/*  825 */         return ((DateTimeUtils.yearFromDateValue(l3) - DateTimeUtils.yearFromDateValue(l1)) * 4 + (
/*  826 */           DateTimeUtils.monthFromDateValue(l3) - 1) / 3 - (
/*  827 */           DateTimeUtils.monthFromDateValue(l1) - 1) / 3);
/*      */       case 9:
/*  829 */         return (millennium(DateTimeUtils.yearFromDateValue(l3)) - 
/*  830 */           millennium(DateTimeUtils.yearFromDateValue(l1)));
/*      */       case 10:
/*  832 */         return (century(DateTimeUtils.yearFromDateValue(l3)) - 
/*  833 */           century(DateTimeUtils.yearFromDateValue(l1)));
/*      */       case 11:
/*  835 */         return (decade(DateTimeUtils.yearFromDateValue(l3)) - 
/*  836 */           decade(DateTimeUtils.yearFromDateValue(l1)));
/*      */       case 0:
/*  838 */         return (DateTimeUtils.yearFromDateValue(l3) - DateTimeUtils.yearFromDateValue(l1));
/*      */       
/*      */       case 6:
/*      */       case 7:
/*      */       case 8:
/*  843 */         if (paramValue1 instanceof ValueTimestampTimeZone) {
/*  844 */           i = ((ValueTimestampTimeZone)paramValue1).getTimeZoneOffsetSeconds();
/*  845 */         } else if (paramValue1 instanceof ValueTimeTimeZone) {
/*  846 */           i = ((ValueTimeTimeZone)paramValue1).getTimeZoneOffsetSeconds();
/*      */         } else {
/*  848 */           i = paramSessionLocal.currentTimeZone().getTimeZoneOffsetLocal(l1, arrayOfLong1[1]);
/*      */         } 
/*      */         
/*  851 */         if (paramValue2 instanceof ValueTimestampTimeZone) {
/*  852 */           j = ((ValueTimestampTimeZone)paramValue2).getTimeZoneOffsetSeconds();
/*  853 */         } else if (paramValue2 instanceof ValueTimeTimeZone) {
/*  854 */           j = ((ValueTimeTimeZone)paramValue2).getTimeZoneOffsetSeconds();
/*      */         } else {
/*  856 */           j = paramSessionLocal.currentTimeZone().getTimeZoneOffsetLocal(l3, arrayOfLong2[1]);
/*      */         } 
/*  858 */         if (paramInt == 6)
/*  859 */           return (j / 3600 - i / 3600); 
/*  860 */         if (paramInt == 7) {
/*  861 */           return (j / 60 - i / 60);
/*      */         }
/*  863 */         return (j - i);
/*      */     } 
/*      */ 
/*      */     
/*  867 */     throw DbException.getUnsupportedException("DATEDIFF " + getFieldName(paramInt));
/*      */   }
/*      */ 
/*      */   
/*      */   private static long weekdiff(long paramLong1, long paramLong2, int paramInt) {
/*  872 */     paramLong1 += (4 - paramInt);
/*  873 */     long l1 = paramLong1 / 7L;
/*  874 */     if (paramLong1 < 0L && l1 * 7L != paramLong1) {
/*  875 */       l1--;
/*      */     }
/*  877 */     paramLong2 += (4 - paramInt);
/*  878 */     long l2 = paramLong2 / 7L;
/*  879 */     if (paramLong2 < 0L && l2 * 7L != paramLong2) {
/*  880 */       l2--;
/*      */     }
/*  882 */     return l2 - l1;
/*      */   }
/*      */   
/*      */   private static int millennium(int paramInt) {
/*  886 */     return (paramInt > 0) ? ((paramInt + 999) / 1000) : (paramInt / 1000);
/*      */   }
/*      */   
/*      */   private static int century(int paramInt) {
/*  890 */     return (paramInt > 0) ? ((paramInt + 99) / 100) : (paramInt / 100);
/*      */   }
/*      */   
/*      */   private static int decade(int paramInt) {
/*  894 */     return (paramInt >= 0) ? (paramInt / 10) : ((paramInt - 9) / 10);
/*      */   }
/*      */   
/*      */   private static int getLocalDayOfWeek(long paramLong) {
/*  898 */     return DateTimeUtils.getDayOfWeek(paramLong, getWeekFields().getFirstDayOfWeek().getValue());
/*      */   }
/*      */   
/*      */   private static int getLocalWeekOfYear(long paramLong) {
/*  902 */     WeekFields weekFields = getWeekFields();
/*  903 */     return DateTimeUtils.getWeekOfYear(paramLong, weekFields.getFirstDayOfWeek().getValue(), weekFields
/*  904 */         .getMinimalDaysInFirstWeek());
/*      */   }
/*      */   
/*      */   private static WeekFields getWeekFields() {
/*  908 */     WeekFields weekFields = WEEK_FIELDS;
/*  909 */     if (weekFields == null) {
/*  910 */       WEEK_FIELDS = weekFields = WeekFields.of(Locale.getDefault());
/*      */     }
/*  912 */     return weekFields;
/*      */   }
/*      */   
/*      */   private static ValueNumeric extractEpoch(SessionLocal paramSessionLocal, Value paramValue) {
/*      */     ValueNumeric valueNumeric;
/*  917 */     if (paramValue instanceof ValueInterval) {
/*  918 */       ValueInterval valueInterval = (ValueInterval)paramValue;
/*  919 */       if (valueInterval.getQualifier().isYearMonth()) {
/*  920 */         valueInterval = (ValueInterval)valueInterval.convertTo(TypeInfo.TYPE_INTERVAL_YEAR_TO_MONTH);
/*  921 */         long l3 = valueInterval.getLeading();
/*  922 */         long l4 = valueInterval.getRemaining();
/*      */         
/*  924 */         BigInteger bigInteger = BigInteger.valueOf(l3).multiply(BigInteger.valueOf(31557600L)).add(BigInteger.valueOf(l4 * 2592000L));
/*  925 */         if (valueInterval.isNegative()) {
/*  926 */           bigInteger = bigInteger.negate();
/*      */         }
/*  928 */         return ValueNumeric.get(bigInteger);
/*      */       } 
/*  930 */       return 
/*  931 */         ValueNumeric.get((new BigDecimal(IntervalUtils.intervalToAbsolute(valueInterval))).divide(BD_NANOS_PER_SECOND));
/*      */     } 
/*      */     
/*  934 */     long[] arrayOfLong = DateTimeUtils.dateAndTimeFromValue(paramValue, (CastDataProvider)paramSessionLocal);
/*  935 */     long l1 = arrayOfLong[0];
/*  936 */     long l2 = arrayOfLong[1];
/*  937 */     if (paramValue instanceof org.h2.value.ValueTime) {
/*  938 */       valueNumeric = ValueNumeric.get(BigDecimal.valueOf(l2).divide(BD_NANOS_PER_SECOND));
/*  939 */     } else if (paramValue instanceof org.h2.value.ValueDate) {
/*  940 */       valueNumeric = ValueNumeric.get(BigInteger.valueOf(DateTimeUtils.absoluteDayFromDateValue(l1))
/*  941 */           .multiply(BI_SECONDS_PER_DAY));
/*      */     } else {
/*      */       
/*  944 */       BigDecimal bigDecimal = BigDecimal.valueOf(l2).divide(BD_NANOS_PER_SECOND).add(BigDecimal.valueOf(DateTimeUtils.absoluteDayFromDateValue(l1))
/*  945 */           .multiply(BD_SECONDS_PER_DAY));
/*  946 */       if (paramValue instanceof ValueTimestampTimeZone) {
/*  947 */         valueNumeric = ValueNumeric.get(bigDecimal
/*  948 */             .subtract(BigDecimal.valueOf(((ValueTimestampTimeZone)paramValue).getTimeZoneOffsetSeconds())));
/*  949 */       } else if (paramValue instanceof ValueTimeTimeZone) {
/*      */         
/*  951 */         valueNumeric = ValueNumeric.get(bigDecimal.subtract(BigDecimal.valueOf(((ValueTimeTimeZone)paramValue).getTimeZoneOffsetSeconds())));
/*      */       } else {
/*  953 */         valueNumeric = ValueNumeric.get(bigDecimal);
/*      */       } 
/*      */     } 
/*  956 */     return valueNumeric;
/*      */   }
/*      */   
/*      */   public Expression optimize(SessionLocal paramSessionLocal) {
/*      */     int i;
/*  961 */     this.left = this.left.optimize(paramSessionLocal);
/*  962 */     if (this.right != null) {
/*  963 */       this.right = this.right.optimize(paramSessionLocal);
/*      */     }
/*  965 */     switch (this.function) {
/*      */       case 0:
/*  967 */         this.type = (this.field == 23) ? TypeInfo.getTypeInfo(13, 28L, 9, null) : TypeInfo.TYPE_INTEGER;
/*      */         break;
/*      */ 
/*      */       
/*      */       case 1:
/*  972 */         this.type = this.left.getType();
/*  973 */         i = this.type.getValueType();
/*      */         
/*  975 */         if (!DataType.isDateTimeType(i))
/*  976 */           throw DbException.getInvalidExpressionTypeException("DATE_TRUNC datetime argument", this.left); 
/*  977 */         if (paramSessionLocal.getMode().getEnum() == Mode.ModeEnum.PostgreSQL && i == 17) {
/*  978 */           this.type = TypeInfo.TYPE_TIMESTAMP_TZ;
/*      */         }
/*      */         break;
/*      */       
/*      */       case 2:
/*  983 */         i = this.right.getType().getValueType();
/*  984 */         if (i == 17)
/*  985 */           switch (this.field) {
/*      */             case 3:
/*      */             case 4:
/*      */             case 5:
/*      */             case 13:
/*      */             case 14:
/*      */             case 15:
/*      */             case 23:
/*  993 */               i = 20;
/*      */               break;
/*      */           }  
/*  996 */         this.type = TypeInfo.getTypeInfo(i);
/*      */         break;
/*      */       
/*      */       case 3:
/* 1000 */         this.type = TypeInfo.TYPE_BIGINT;
/*      */         break;
/*      */       default:
/* 1003 */         throw DbException.getInternalError("function=" + this.function);
/*      */     } 
/* 1005 */     if (this.left.isConstant() && (this.right == null || this.right.isConstant())) {
/* 1006 */       return (Expression)TypedValueExpression.getTypedIfNull(getValue(paramSessionLocal), this.type);
/*      */     }
/* 1008 */     return (Expression)this;
/*      */   }
/*      */ 
/*      */   
/*      */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 1013 */     paramStringBuilder.append(getName()).append('(').append(getFieldName(this.field));
/* 1014 */     switch (this.function) {
/*      */       case 0:
/* 1016 */         this.left.getUnenclosedSQL(paramStringBuilder.append(" FROM "), paramInt);
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
/* 1029 */         return paramStringBuilder.append(')');case 1: this.left.getUnenclosedSQL(paramStringBuilder.append(", "), paramInt); return paramStringBuilder.append(')');case 2: case 3: this.left.getUnenclosedSQL(paramStringBuilder.append(", "), paramInt).append(", "); this.right.getUnenclosedSQL(paramStringBuilder, paramInt); return paramStringBuilder.append(')');
/*      */     } 
/*      */     throw DbException.getInternalError("function=" + this.function);
/*      */   }
/*      */   public String getName() {
/* 1034 */     return NAMES[this.function];
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\DateTimeFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */