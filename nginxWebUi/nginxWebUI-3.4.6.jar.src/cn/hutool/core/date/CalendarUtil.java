/*     */ package cn.hutool.core.date;
/*     */ 
/*     */ import cn.hutool.core.comparator.CompareUtil;
/*     */ import cn.hutool.core.convert.NumberChineseFormatter;
/*     */ import cn.hutool.core.date.format.DateParser;
/*     */ import cn.hutool.core.date.format.FastDateParser;
/*     */ import cn.hutool.core.date.format.GlobalCustomFormat;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.text.ParsePosition;
/*     */ import java.time.Instant;
/*     */ import java.time.LocalDateTime;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Locale;
/*     */ import java.util.TimeZone;
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
/*     */ public class CalendarUtil
/*     */ {
/*     */   public static Calendar calendar() {
/*  35 */     return Calendar.getInstance();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Calendar calendar(Date date) {
/*  45 */     if (date instanceof DateTime) {
/*  46 */       return ((DateTime)date).toCalendar();
/*     */     }
/*  48 */     return calendar(date.getTime());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Calendar calendar(long millis) {
/*  59 */     return calendar(millis, TimeZone.getDefault());
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
/*     */   public static Calendar calendar(long millis, TimeZone timeZone) {
/*  71 */     Calendar cal = Calendar.getInstance(timeZone);
/*  72 */     cal.setTimeInMillis(millis);
/*  73 */     return cal;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isAM(Calendar calendar) {
/*  83 */     return (0 == calendar.get(9));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isPM(Calendar calendar) {
/*  93 */     return (1 == calendar.get(9));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Calendar truncate(Calendar calendar, DateField dateField) {
/* 104 */     return DateModifier.modify(calendar, dateField.getValue(), DateModifier.ModifyType.TRUNCATE);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Calendar round(Calendar calendar, DateField dateField) {
/* 115 */     return DateModifier.modify(calendar, dateField.getValue(), DateModifier.ModifyType.ROUND);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Calendar ceiling(Calendar calendar, DateField dateField) {
/* 126 */     return DateModifier.modify(calendar, dateField.getValue(), DateModifier.ModifyType.CEILING);
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
/*     */   public static Calendar ceiling(Calendar calendar, DateField dateField, boolean truncateMillisecond) {
/* 143 */     return DateModifier.modify(calendar, dateField.getValue(), DateModifier.ModifyType.CEILING, truncateMillisecond);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Calendar beginOfSecond(Calendar calendar) {
/* 154 */     return truncate(calendar, DateField.SECOND);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Calendar endOfSecond(Calendar calendar) {
/* 165 */     return ceiling(calendar, DateField.SECOND);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Calendar beginOfHour(Calendar calendar) {
/* 175 */     return truncate(calendar, DateField.HOUR_OF_DAY);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Calendar endOfHour(Calendar calendar) {
/* 185 */     return ceiling(calendar, DateField.HOUR_OF_DAY);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Calendar beginOfMinute(Calendar calendar) {
/* 195 */     return truncate(calendar, DateField.MINUTE);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Calendar endOfMinute(Calendar calendar) {
/* 205 */     return ceiling(calendar, DateField.MINUTE);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Calendar beginOfDay(Calendar calendar) {
/* 215 */     return truncate(calendar, DateField.DAY_OF_MONTH);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Calendar endOfDay(Calendar calendar) {
/* 225 */     return ceiling(calendar, DateField.DAY_OF_MONTH);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Calendar beginOfWeek(Calendar calendar) {
/* 235 */     return beginOfWeek(calendar, true);
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
/*     */   public static Calendar beginOfWeek(Calendar calendar, boolean isMondayAsFirstDay) {
/* 247 */     calendar.setFirstDayOfWeek(isMondayAsFirstDay ? 2 : 1);
/*     */     
/* 249 */     return truncate(calendar, DateField.WEEK_OF_MONTH);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Calendar endOfWeek(Calendar calendar) {
/* 259 */     return endOfWeek(calendar, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Calendar endOfWeek(Calendar calendar, boolean isSundayAsLastDay) {
/* 270 */     calendar.setFirstDayOfWeek(isSundayAsLastDay ? 2 : 1);
/*     */     
/* 272 */     return ceiling(calendar, DateField.WEEK_OF_MONTH);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Calendar beginOfMonth(Calendar calendar) {
/* 282 */     return truncate(calendar, DateField.MONTH);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Calendar endOfMonth(Calendar calendar) {
/* 292 */     return ceiling(calendar, DateField.MONTH);
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
/*     */   public static Calendar beginOfQuarter(Calendar calendar) {
/* 304 */     calendar.set(2, calendar.get(DateField.MONTH.getValue()) / 3 * 3);
/* 305 */     calendar.set(5, 1);
/* 306 */     return beginOfDay(calendar);
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
/*     */   public static Calendar endOfQuarter(Calendar calendar) {
/* 318 */     int year = calendar.get(1);
/* 319 */     int month = calendar.get(DateField.MONTH.getValue()) / 3 * 3 + 2;
/*     */     
/* 321 */     Calendar resultCal = Calendar.getInstance(calendar.getTimeZone());
/* 322 */     resultCal.set(year, month, Month.of(month).getLastDay(DateUtil.isLeapYear(year)));
/*     */     
/* 324 */     return endOfDay(resultCal);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Calendar beginOfYear(Calendar calendar) {
/* 334 */     return truncate(calendar, DateField.YEAR);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Calendar endOfYear(Calendar calendar) {
/* 344 */     return ceiling(calendar, DateField.YEAR);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isSameDay(Calendar cal1, Calendar cal2) {
/* 355 */     if (cal1 == null || cal2 == null) {
/* 356 */       throw new IllegalArgumentException("The date must not be null");
/*     */     }
/* 358 */     return (cal1.get(6) == cal2.get(6) && cal1
/* 359 */       .get(1) == cal2.get(1) && cal1
/* 360 */       .get(0) == cal2.get(0));
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
/*     */   public static boolean isSameWeek(Calendar cal1, Calendar cal2, boolean isMon) {
/* 373 */     if (cal1 == null || cal2 == null) {
/* 374 */       throw new IllegalArgumentException("The date must not be null");
/*     */     }
/*     */ 
/*     */     
/* 378 */     cal1 = (Calendar)cal1.clone();
/* 379 */     cal2 = (Calendar)cal2.clone();
/*     */ 
/*     */ 
/*     */     
/* 383 */     if (isMon) {
/* 384 */       cal1.setFirstDayOfWeek(2);
/* 385 */       cal1.set(7, 2);
/* 386 */       cal2.setFirstDayOfWeek(2);
/* 387 */       cal2.set(7, 2);
/*     */     } else {
/* 389 */       cal1.setFirstDayOfWeek(1);
/* 390 */       cal1.set(7, 1);
/* 391 */       cal2.setFirstDayOfWeek(1);
/* 392 */       cal2.set(7, 1);
/*     */     } 
/* 394 */     return isSameDay(cal1, cal2);
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
/*     */   public static boolean isSameMonth(Calendar cal1, Calendar cal2) {
/* 406 */     if (cal1 == null || cal2 == null) {
/* 407 */       throw new IllegalArgumentException("The date must not be null");
/*     */     }
/* 409 */     return (cal1.get(1) == cal2.get(1) && cal1
/* 410 */       .get(2) == cal2.get(2));
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
/*     */   public static boolean isSameInstant(Calendar date1, Calendar date2) {
/* 424 */     if (null == date1) {
/* 425 */       return (null == date2);
/*     */     }
/* 427 */     if (null == date2) {
/* 428 */       return false;
/*     */     }
/*     */     
/* 431 */     return (date1.getTimeInMillis() == date2.getTimeInMillis());
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
/*     */   public static LinkedHashSet<String> yearAndQuarter(long startDate, long endDate) {
/* 443 */     LinkedHashSet<String> quarters = new LinkedHashSet<>();
/* 444 */     Calendar cal = calendar(startDate);
/* 445 */     while (startDate <= endDate) {
/*     */       
/* 447 */       quarters.add(yearAndQuarter(cal));
/*     */       
/* 449 */       cal.add(2, 3);
/* 450 */       startDate = cal.getTimeInMillis();
/*     */     } 
/*     */     
/* 453 */     return quarters;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String yearAndQuarter(Calendar cal) {
/* 464 */     return StrUtil.builder().append(cal.get(1)).append(cal.get(2) / 3 + 1).toString();
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
/*     */   public static int getBeginValue(Calendar calendar, DateField dateField) {
/* 477 */     return getBeginValue(calendar, dateField.getValue());
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
/*     */   public static int getBeginValue(Calendar calendar, int dateField) {
/* 490 */     if (7 == dateField) {
/* 491 */       return calendar.getFirstDayOfWeek();
/*     */     }
/* 493 */     return calendar.getActualMinimum(dateField);
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
/*     */   public static int getEndValue(Calendar calendar, DateField dateField) {
/* 506 */     return getEndValue(calendar, dateField.getValue());
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
/*     */   public static int getEndValue(Calendar calendar, int dateField) {
/* 519 */     if (7 == dateField) {
/* 520 */       return (calendar.getFirstDayOfWeek() + 6) % 7;
/*     */     }
/* 522 */     return calendar.getActualMaximum(dateField);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Instant toInstant(Calendar calendar) {
/* 533 */     return (null == calendar) ? null : calendar.toInstant();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static LocalDateTime toLocalDateTime(Calendar calendar) {
/* 544 */     return LocalDateTime.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId());
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
/*     */   public static int compare(Calendar calendar1, Calendar calendar2) {
/* 556 */     return CompareUtil.compare(calendar1, calendar2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int age(Calendar birthday, Calendar dateToCompare) {
/* 567 */     return age(birthday.getTimeInMillis(), dateToCompare.getTimeInMillis());
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
/*     */   public static String formatChineseDate(Calendar calendar, boolean withTime) {
/* 584 */     StringBuilder result = StrUtil.builder();
/*     */ 
/*     */     
/* 587 */     String year = String.valueOf(calendar.get(1));
/* 588 */     int length = year.length();
/* 589 */     for (int i = 0; i < length; i++) {
/* 590 */       result.append(NumberChineseFormatter.numberCharToChinese(year.charAt(i), false));
/*     */     }
/* 592 */     result.append('年');
/*     */ 
/*     */     
/* 595 */     int month = calendar.get(2) + 1;
/* 596 */     result.append(NumberChineseFormatter.formatThousand(month, false));
/* 597 */     result.append('月');
/*     */ 
/*     */     
/* 600 */     int day = calendar.get(5);
/* 601 */     result.append(NumberChineseFormatter.formatThousand(day, false));
/* 602 */     result.append('日');
/*     */ 
/*     */     
/* 605 */     String temp = result.toString().replace('零', '〇');
/* 606 */     result.delete(0, result.length());
/* 607 */     result.append(temp);
/*     */ 
/*     */     
/* 610 */     if (withTime) {
/*     */       
/* 612 */       int hour = calendar.get(11);
/* 613 */       result.append(NumberChineseFormatter.formatThousand(hour, false));
/* 614 */       result.append('时');
/*     */       
/* 616 */       int minute = calendar.get(12);
/* 617 */       result.append(NumberChineseFormatter.formatThousand(minute, false));
/* 618 */       result.append('分');
/*     */       
/* 620 */       int second = calendar.get(13);
/* 621 */       result.append(NumberChineseFormatter.formatThousand(second, false));
/* 622 */       result.append('秒');
/*     */     } 
/*     */     
/* 625 */     return result.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static int age(long birthday, long dateToCompare) {
/* 636 */     if (birthday > dateToCompare) {
/* 637 */       throw new IllegalArgumentException("Birthday is after dateToCompare!");
/*     */     }
/*     */     
/* 640 */     Calendar cal = Calendar.getInstance();
/* 641 */     cal.setTimeInMillis(dateToCompare);
/*     */     
/* 643 */     int year = cal.get(1);
/* 644 */     int month = cal.get(2);
/* 645 */     int dayOfMonth = cal.get(5);
/* 646 */     boolean isLastDayOfMonth = (dayOfMonth == cal.getActualMaximum(5));
/*     */     
/* 648 */     cal.setTimeInMillis(birthday);
/* 649 */     int age = year - cal.get(1);
/*     */     
/* 651 */     int monthBirth = cal.get(2);
/* 652 */     if (month == monthBirth) {
/*     */       
/* 654 */       int dayOfMonthBirth = cal.get(5);
/* 655 */       boolean isLastDayOfMonthBirth = (dayOfMonthBirth == cal.getActualMaximum(5));
/* 656 */       if ((false == isLastDayOfMonth || false == isLastDayOfMonthBirth) && dayOfMonth < dayOfMonthBirth)
/*     */       {
/* 658 */         age--;
/*     */       }
/* 660 */     } else if (month < monthBirth) {
/*     */       
/* 662 */       age--;
/*     */     } 
/*     */     
/* 665 */     return age;
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
/*     */   public static Calendar parseByPatterns(String str, String... parsePatterns) throws DateException {
/* 681 */     return parseByPatterns(str, null, parsePatterns);
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
/*     */   public static Calendar parseByPatterns(String str, Locale locale, String... parsePatterns) throws DateException {
/* 698 */     return parseByPatterns(str, locale, true, parsePatterns);
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
/*     */   public static Calendar parseByPatterns(String str, Locale locale, boolean lenient, String... parsePatterns) throws DateException {
/* 717 */     if (str == null || parsePatterns == null) {
/* 718 */       throw new IllegalArgumentException("Date and Patterns must not be null");
/*     */     }
/*     */     
/* 721 */     TimeZone tz = TimeZone.getDefault();
/* 722 */     Locale lcl = (Locale)ObjectUtil.defaultIfNull(locale, Locale.getDefault());
/* 723 */     ParsePosition pos = new ParsePosition(0);
/* 724 */     Calendar calendar = Calendar.getInstance(tz, lcl);
/* 725 */     calendar.setLenient(lenient);
/*     */     
/* 727 */     for (String parsePattern : parsePatterns) {
/* 728 */       if (GlobalCustomFormat.isCustomFormat(parsePattern)) {
/* 729 */         Date parse = GlobalCustomFormat.parse(str, parsePattern);
/* 730 */         if (null != parse) {
/*     */ 
/*     */           
/* 733 */           calendar.setTime(parse);
/* 734 */           return calendar;
/*     */         } 
/*     */       } else {
/* 737 */         FastDateParser fdp = new FastDateParser(parsePattern, tz, lcl);
/* 738 */         calendar.clear();
/*     */         try {
/* 740 */           if (fdp.parse(str, pos, calendar) && pos.getIndex() == str.length()) {
/* 741 */             return calendar;
/*     */           }
/* 743 */         } catch (IllegalArgumentException illegalArgumentException) {}
/*     */ 
/*     */         
/* 746 */         pos.setIndex(0);
/*     */       } 
/*     */     } 
/* 749 */     throw new DateException("Unable to parse the date: {}", new Object[] { str });
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
/*     */   public static Calendar parse(CharSequence str, boolean lenient, DateParser parser) {
/* 762 */     Calendar calendar = Calendar.getInstance(parser.getTimeZone(), parser.getLocale());
/* 763 */     calendar.clear();
/* 764 */     calendar.setLenient(lenient);
/*     */     
/* 766 */     return parser.parse(StrUtil.str(str), new ParsePosition(0), calendar) ? calendar : null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\date\CalendarUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */