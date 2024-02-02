/*      */ package cn.hutool.core.date;
/*      */ 
/*      */ import cn.hutool.core.collection.CollUtil;
/*      */ import cn.hutool.core.comparator.CompareUtil;
/*      */ import cn.hutool.core.date.format.DateParser;
/*      */ import cn.hutool.core.date.format.DatePrinter;
/*      */ import cn.hutool.core.date.format.GlobalCustomFormat;
/*      */ import cn.hutool.core.lang.Assert;
/*      */ import cn.hutool.core.lang.PatternPool;
/*      */ import cn.hutool.core.util.NumberUtil;
/*      */ import cn.hutool.core.util.ReUtil;
/*      */ import cn.hutool.core.util.StrUtil;
/*      */ import java.text.DateFormat;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.time.Instant;
/*      */ import java.time.LocalDateTime;
/*      */ import java.time.Month;
/*      */ import java.time.Year;
/*      */ import java.time.ZoneId;
/*      */ import java.time.format.DateTimeFormatter;
/*      */ import java.time.temporal.TemporalAccessor;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.Collections;
/*      */ import java.util.Date;
/*      */ import java.util.LinkedHashSet;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.TimeZone;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import java.util.function.Consumer;
/*      */ import java.util.function.Function;
/*      */ import java.util.stream.Collectors;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class DateUtil
/*      */   extends CalendarUtil
/*      */ {
/*   42 */   private static final String[] wtb = new String[] { "sun", "mon", "tue", "wed", "thu", "fri", "sat", "jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec", "gmt", "ut", "utc", "est", "edt", "cst", "cdt", "mst", "mdt", "pst", "pdt" };
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
/*      */   public static DateTime date() {
/*   54 */     return new DateTime();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime dateSecond() {
/*   64 */     return beginOfSecond(date());
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
/*      */   public static DateTime date(Date date) {
/*   76 */     if (date instanceof DateTime) {
/*   77 */       return (DateTime)date;
/*      */     }
/*   79 */     return dateNew(date);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime dateNew(Date date) {
/*   90 */     return new DateTime(date);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime date(long date) {
/*  101 */     return new DateTime(date);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime date(Calendar calendar) {
/*  112 */     return new DateTime(calendar);
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
/*      */   public static DateTime date(TemporalAccessor temporalAccessor) {
/*  124 */     return new DateTime(temporalAccessor);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long current() {
/*  133 */     return System.currentTimeMillis();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long currentSeconds() {
/*  143 */     return System.currentTimeMillis() / 1000L;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String now() {
/*  152 */     return formatDateTime(new DateTime());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String today() {
/*  161 */     return formatDate(new DateTime());
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
/*      */   public static int year(Date date) {
/*  173 */     return DateTime.of(date).year();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int quarter(Date date) {
/*  184 */     return DateTime.of(date).quarter();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Quarter quarterEnum(Date date) {
/*  195 */     return DateTime.of(date).quarterEnum();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int month(Date date) {
/*  205 */     return DateTime.of(date).month();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Month monthEnum(Date date) {
/*  215 */     return DateTime.of(date).monthEnum();
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
/*      */   public static int weekOfYear(Date date) {
/*  230 */     return DateTime.of(date).weekOfYear();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int weekOfMonth(Date date) {
/*  240 */     return DateTime.of(date).weekOfMonth();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int dayOfMonth(Date date) {
/*  250 */     return DateTime.of(date).dayOfMonth();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int dayOfYear(Date date) {
/*  261 */     return DateTime.of(date).dayOfYear();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int dayOfWeek(Date date) {
/*  271 */     return DateTime.of(date).dayOfWeek();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Week dayOfWeekEnum(Date date) {
/*  281 */     return DateTime.of(date).dayOfWeekEnum();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isWeekend(Date date) {
/*  292 */     Week week = dayOfWeekEnum(date);
/*  293 */     return (Week.SATURDAY == week || Week.SUNDAY == week);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int hour(Date date, boolean is24HourClock) {
/*  304 */     return DateTime.of(date).hour(is24HourClock);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int minute(Date date) {
/*  315 */     return DateTime.of(date).minute();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int second(Date date) {
/*  325 */     return DateTime.of(date).second();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int millisecond(Date date) {
/*  335 */     return DateTime.of(date).millisecond();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isAM(Date date) {
/*  345 */     return DateTime.of(date).isAM();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isPM(Date date) {
/*  355 */     return DateTime.of(date).isPM();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int thisYear() {
/*  362 */     return year(date());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int thisMonth() {
/*  369 */     return month(date());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Month thisMonthEnum() {
/*  376 */     return monthEnum(date());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int thisWeekOfYear() {
/*  383 */     return weekOfYear(date());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int thisWeekOfMonth() {
/*  390 */     return weekOfMonth(date());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int thisDayOfMonth() {
/*  397 */     return dayOfMonth(date());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int thisDayOfWeek() {
/*  404 */     return dayOfWeek(date());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Week thisDayOfWeekEnum() {
/*  411 */     return dayOfWeekEnum(date());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int thisHour(boolean is24HourClock) {
/*  419 */     return hour(date(), is24HourClock);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int thisMinute() {
/*  426 */     return minute(date());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int thisSecond() {
/*  433 */     return second(date());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int thisMillisecond() {
/*  440 */     return millisecond(date());
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
/*      */   public static String yearAndQuarter(Date date) {
/*  452 */     return yearAndQuarter(calendar(date));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static LinkedHashSet<String> yearAndQuarter(Date startDate, Date endDate) {
/*  463 */     if (startDate == null || endDate == null) {
/*  464 */       return new LinkedHashSet<>(0);
/*      */     }
/*  466 */     return yearAndQuarter(startDate.getTime(), endDate.getTime());
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
/*      */   public static String formatLocalDateTime(LocalDateTime localDateTime) {
/*  478 */     return LocalDateTimeUtil.formatNormal(localDateTime);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String format(LocalDateTime localDateTime, String format) {
/*  489 */     return LocalDateTimeUtil.format(localDateTime, format);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String format(Date date, String format) {
/*  500 */     if (null == date || StrUtil.isBlank(format)) {
/*  501 */       return null;
/*      */     }
/*      */ 
/*      */     
/*  505 */     if (GlobalCustomFormat.isCustomFormat(format)) {
/*  506 */       return GlobalCustomFormat.format(date, format);
/*      */     }
/*      */     
/*  509 */     TimeZone timeZone = null;
/*  510 */     if (date instanceof DateTime) {
/*  511 */       timeZone = ((DateTime)date).getTimeZone();
/*      */     }
/*  513 */     return format(date, newSimpleFormat(format, (Locale)null, timeZone));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String format(Date date, DatePrinter format) {
/*  524 */     if (null == format || null == date) {
/*  525 */       return null;
/*      */     }
/*  527 */     return format.format(date);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String format(Date date, DateFormat format) {
/*  538 */     if (null == format || null == date) {
/*  539 */       return null;
/*      */     }
/*  541 */     return format.format(date);
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
/*      */   public static String format(Date date, DateTimeFormatter format) {
/*  553 */     if (null == format || null == date) {
/*  554 */       return null;
/*      */     }
/*      */ 
/*      */     
/*  558 */     return TemporalAccessorUtil.format(date.toInstant(), format);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String formatDateTime(Date date) {
/*  569 */     if (null == date) {
/*  570 */       return null;
/*      */     }
/*  572 */     return DatePattern.NORM_DATETIME_FORMAT.format(date);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String formatDate(Date date) {
/*  583 */     if (null == date) {
/*  584 */       return null;
/*      */     }
/*  586 */     return DatePattern.NORM_DATE_FORMAT.format(date);
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
/*      */   public static String formatTime(Date date) {
/*  598 */     if (null == date) {
/*  599 */       return null;
/*      */     }
/*  601 */     return DatePattern.NORM_TIME_FORMAT.format(date);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String formatHttpDate(Date date) {
/*  612 */     if (null == date) {
/*  613 */       return null;
/*      */     }
/*  615 */     return DatePattern.HTTP_DATETIME_FORMAT.format(date);
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
/*      */   public static String formatChineseDate(Date date, boolean isUppercase, boolean withTime) {
/*  628 */     if (null == date) {
/*  629 */       return null;
/*      */     }
/*      */     
/*  632 */     if (false == isUppercase) {
/*  633 */       return (withTime ? DatePattern.CHINESE_DATE_TIME_FORMAT : DatePattern.CHINESE_DATE_FORMAT).format(date);
/*      */     }
/*      */     
/*  636 */     return CalendarUtil.formatChineseDate(CalendarUtil.calendar(date), withTime);
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
/*      */   public static LocalDateTime parseLocalDateTime(CharSequence dateStr) {
/*  650 */     return parseLocalDateTime(dateStr, "yyyy-MM-dd HH:mm:ss");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static LocalDateTime parseLocalDateTime(CharSequence dateStr, String format) {
/*  661 */     return LocalDateTimeUtil.parse(dateStr, format);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime parse(CharSequence dateStr, DateFormat dateFormat) {
/*  672 */     return new DateTime(dateStr, dateFormat);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime parse(CharSequence dateStr, DateParser parser) {
/*  683 */     return new DateTime(dateStr, parser);
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
/*      */   public static DateTime parse(CharSequence dateStr, DateParser parser, boolean lenient) {
/*  696 */     return new DateTime(dateStr, parser, lenient);
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
/*      */   public static DateTime parse(CharSequence dateStr, DateTimeFormatter formatter) {
/*  708 */     return new DateTime(dateStr, formatter);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime parse(CharSequence dateStr, String format) {
/*  719 */     return new DateTime(dateStr, format);
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
/*      */   public static DateTime parse(CharSequence dateStr, String format, Locale locale) {
/*  732 */     if (GlobalCustomFormat.isCustomFormat(format))
/*      */     {
/*  734 */       return new DateTime(GlobalCustomFormat.parse(dateStr, format));
/*      */     }
/*  736 */     return new DateTime(dateStr, newSimpleFormat(format, locale, (TimeZone)null));
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
/*      */   public static DateTime parse(String str, String... parsePatterns) throws DateException {
/*  751 */     return new DateTime(CalendarUtil.parseByPatterns(str, parsePatterns));
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
/*      */   public static DateTime parseDateTime(CharSequence dateString) {
/*  768 */     dateString = normalize(dateString);
/*  769 */     return parse(dateString, (DateParser)DatePattern.NORM_DATETIME_FORMAT);
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
/*      */   public static DateTime parseDate(CharSequence dateString) {
/*  785 */     dateString = normalize(dateString);
/*  786 */     return parse(dateString, (DateParser)DatePattern.NORM_DATE_FORMAT);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime parseTime(CharSequence timeString) {
/*  796 */     timeString = normalize(timeString);
/*  797 */     return parse(timeString, (DateParser)DatePattern.NORM_TIME_FORMAT);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime parseTimeToday(CharSequence timeString) {
/*  808 */     timeString = StrUtil.format("{} {}", new Object[] { today(), timeString });
/*  809 */     if (1 == StrUtil.count(timeString, ':'))
/*      */     {
/*  811 */       return parse(timeString, "yyyy-MM-dd HH:mm");
/*      */     }
/*      */     
/*  814 */     return parse(timeString, (DateParser)DatePattern.NORM_DATETIME_FORMAT);
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
/*      */   public static DateTime parseUTC(String utcString) {
/*  834 */     if (utcString == null) {
/*  835 */       return null;
/*      */     }
/*  837 */     int length = utcString.length();
/*  838 */     if (StrUtil.contains(utcString, 'Z')) {
/*  839 */       if (length == "yyyy-MM-dd'T'HH:mm:ss'Z'".length() - 4)
/*      */       {
/*  841 */         return parse(utcString, (DateParser)DatePattern.UTC_FORMAT);
/*      */       }
/*      */       
/*  844 */       int patternLength = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'".length();
/*      */ 
/*      */       
/*  847 */       if (length <= patternLength - 4 && length >= patternLength - 6)
/*  848 */         return parse(utcString, (DateParser)DatePattern.UTC_MS_FORMAT); 
/*      */     } else {
/*  850 */       if (StrUtil.contains(utcString, '+')) {
/*      */         
/*  852 */         utcString = utcString.replace(" +", "+");
/*  853 */         String zoneOffset = StrUtil.subAfter(utcString, '+', true);
/*  854 */         if (StrUtil.isBlank(zoneOffset)) {
/*  855 */           throw new DateException("Invalid format: [{}]", new Object[] { utcString });
/*      */         }
/*  857 */         if (false == StrUtil.contains(zoneOffset, ':')) {
/*      */           
/*  859 */           String pre = StrUtil.subBefore(utcString, '+', true);
/*  860 */           utcString = pre + "+" + zoneOffset.substring(0, 2) + ":00";
/*      */         } 
/*      */         
/*  863 */         if (StrUtil.contains(utcString, '.'))
/*      */         {
/*  865 */           return parse(utcString, (DateParser)DatePattern.UTC_MS_WITH_XXX_OFFSET_FORMAT);
/*      */         }
/*      */         
/*  868 */         return parse(utcString, (DateParser)DatePattern.UTC_WITH_XXX_OFFSET_FORMAT);
/*      */       } 
/*      */       
/*  871 */       if (length == "yyyy-MM-dd'T'HH:mm:ss".length() - 2)
/*      */       {
/*  873 */         return parse(utcString, (DateParser)DatePattern.UTC_SIMPLE_FORMAT); } 
/*  874 */       if (StrUtil.contains(utcString, '.'))
/*      */       {
/*  876 */         return parse(utcString, (DateParser)DatePattern.UTC_SIMPLE_MS_FORMAT);
/*      */       }
/*      */     } 
/*      */     
/*  880 */     throw new DateException("No format fit for date String [{}] !", new Object[] { utcString });
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
/*      */   public static DateTime parseCST(CharSequence cstString) {
/*  894 */     if (cstString == null) {
/*  895 */       return null;
/*      */     }
/*      */     
/*  898 */     return parse(cstString, (DateParser)DatePattern.JDK_DATETIME_FORMAT);
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
/*      */   public static DateTime parse(CharSequence dateCharSequence) {
/*  931 */     if (StrUtil.isBlank(dateCharSequence)) {
/*  932 */       return null;
/*      */     }
/*  934 */     String dateStr = dateCharSequence.toString();
/*      */     
/*  936 */     dateStr = StrUtil.removeAll(dateStr.trim(), new char[] { '日', '秒' });
/*  937 */     int length = dateStr.length();
/*      */     
/*  939 */     if (NumberUtil.isNumber(dateStr)) {
/*      */       
/*  941 */       if (length == "yyyyMMddHHmmss".length())
/*  942 */         return parse(dateStr, (DateParser)DatePattern.PURE_DATETIME_FORMAT); 
/*  943 */       if (length == "yyyyMMddHHmmssSSS".length())
/*  944 */         return parse(dateStr, (DateParser)DatePattern.PURE_DATETIME_MS_FORMAT); 
/*  945 */       if (length == "yyyyMMdd".length())
/*  946 */         return parse(dateStr, (DateParser)DatePattern.PURE_DATE_FORMAT); 
/*  947 */       if (length == "HHmmss".length())
/*  948 */         return parse(dateStr, (DateParser)DatePattern.PURE_TIME_FORMAT); 
/*      */     } else {
/*  950 */       if (ReUtil.isMatch(PatternPool.TIME, dateStr))
/*      */       {
/*  952 */         return parseTimeToday(dateStr); } 
/*  953 */       if (StrUtil.containsAnyIgnoreCase(dateStr, (CharSequence[])wtb))
/*      */       {
/*      */ 
/*      */ 
/*      */         
/*  958 */         return parseCST(dateStr); } 
/*  959 */       if (StrUtil.contains(dateStr, 'T'))
/*      */       {
/*  961 */         return parseUTC(dateStr);
/*      */       }
/*      */     } 
/*      */     
/*  965 */     dateStr = normalize(dateStr);
/*  966 */     if (ReUtil.isMatch(DatePattern.REGEX_NORM, dateStr)) {
/*  967 */       int indexOfDot, colonCount = StrUtil.count(dateStr, ':');
/*  968 */       switch (colonCount) {
/*      */         
/*      */         case 0:
/*  971 */           return parse(dateStr, (DateParser)DatePattern.NORM_DATE_FORMAT);
/*      */         
/*      */         case 1:
/*  974 */           return parse(dateStr, (DateParser)DatePattern.NORM_DATETIME_MINUTE_FORMAT);
/*      */         case 2:
/*  976 */           indexOfDot = StrUtil.indexOf(dateStr, '.');
/*  977 */           if (indexOfDot > 0) {
/*  978 */             int length1 = dateStr.length();
/*      */             
/*  980 */             if (length1 - indexOfDot > 4)
/*      */             {
/*  982 */               dateStr = StrUtil.subPre(dateStr, indexOfDot + 4);
/*      */             }
/*  984 */             return parse(dateStr, (DateParser)DatePattern.NORM_DATETIME_MS_FORMAT);
/*      */           } 
/*      */           
/*  987 */           return parse(dateStr, (DateParser)DatePattern.NORM_DATETIME_FORMAT);
/*      */       } 
/*      */ 
/*      */     
/*      */     } 
/*  992 */     throw new DateException("No format fit for date String [{}] !", new Object[] { dateStr });
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
/*      */   public static DateTime truncate(Date date, DateField dateField) {
/* 1008 */     return new DateTime(truncate(calendar(date), dateField));
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
/*      */   public static DateTime round(Date date, DateField dateField) {
/* 1020 */     return new DateTime(round(calendar(date), dateField));
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
/*      */   public static DateTime ceiling(Date date, DateField dateField) {
/* 1032 */     return new DateTime(ceiling(calendar(date), dateField));
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
/*      */   public static DateTime ceiling(Date date, DateField dateField, boolean truncateMillisecond) {
/* 1050 */     return new DateTime(ceiling(calendar(date), dateField, truncateMillisecond));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime beginOfSecond(Date date) {
/* 1061 */     return new DateTime(beginOfSecond(calendar(date)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime endOfSecond(Date date) {
/* 1072 */     return new DateTime(endOfSecond(calendar(date)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime beginOfHour(Date date) {
/* 1082 */     return new DateTime(beginOfHour(calendar(date)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime endOfHour(Date date) {
/* 1092 */     return new DateTime(endOfHour(calendar(date)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime beginOfMinute(Date date) {
/* 1102 */     return new DateTime(beginOfMinute(calendar(date)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime endOfMinute(Date date) {
/* 1112 */     return new DateTime(endOfMinute(calendar(date)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime beginOfDay(Date date) {
/* 1122 */     return new DateTime(beginOfDay(calendar(date)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime endOfDay(Date date) {
/* 1132 */     return new DateTime(endOfDay(calendar(date)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime beginOfWeek(Date date) {
/* 1142 */     return new DateTime(beginOfWeek(calendar(date)));
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
/*      */   public static DateTime beginOfWeek(Date date, boolean isMondayAsFirstDay) {
/* 1154 */     return new DateTime(beginOfWeek(calendar(date), isMondayAsFirstDay));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime endOfWeek(Date date) {
/* 1164 */     return new DateTime(endOfWeek(calendar(date)));
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
/*      */   public static DateTime endOfWeek(Date date, boolean isSundayAsLastDay) {
/* 1176 */     return new DateTime(endOfWeek(calendar(date), isSundayAsLastDay));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime beginOfMonth(Date date) {
/* 1186 */     return new DateTime(beginOfMonth(calendar(date)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime endOfMonth(Date date) {
/* 1196 */     return new DateTime(endOfMonth(calendar(date)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime beginOfQuarter(Date date) {
/* 1206 */     return new DateTime(beginOfQuarter(calendar(date)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime endOfQuarter(Date date) {
/* 1216 */     return new DateTime(endOfQuarter(calendar(date)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime beginOfYear(Date date) {
/* 1226 */     return new DateTime(beginOfYear(calendar(date)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime endOfYear(Date date) {
/* 1236 */     return new DateTime(endOfYear(calendar(date)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime yesterday() {
/* 1246 */     return offsetDay(new DateTime(), -1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime tomorrow() {
/* 1256 */     return offsetDay(new DateTime(), 1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime lastWeek() {
/* 1265 */     return offsetWeek(new DateTime(), -1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime nextWeek() {
/* 1275 */     return offsetWeek(new DateTime(), 1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime lastMonth() {
/* 1284 */     return offsetMonth(new DateTime(), -1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime nextMonth() {
/* 1294 */     return offsetMonth(new DateTime(), 1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime offsetMillisecond(Date date, int offset) {
/* 1305 */     return offset(date, DateField.MILLISECOND, offset);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime offsetSecond(Date date, int offset) {
/* 1316 */     return offset(date, DateField.SECOND, offset);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime offsetMinute(Date date, int offset) {
/* 1327 */     return offset(date, DateField.MINUTE, offset);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime offsetHour(Date date, int offset) {
/* 1338 */     return offset(date, DateField.HOUR_OF_DAY, offset);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime offsetDay(Date date, int offset) {
/* 1349 */     return offset(date, DateField.DAY_OF_YEAR, offset);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime offsetWeek(Date date, int offset) {
/* 1360 */     return offset(date, DateField.WEEK_OF_YEAR, offset);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime offsetMonth(Date date, int offset) {
/* 1371 */     return offset(date, DateField.MONTH, offset);
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
/*      */   public static DateTime offset(Date date, DateField dateField, int offset) {
/* 1383 */     return dateNew(date).offset(dateField, offset);
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
/*      */   public static long between(Date beginDate, Date endDate, DateUnit unit) {
/* 1397 */     return between(beginDate, endDate, unit, true);
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
/*      */   public static long between(Date beginDate, Date endDate, DateUnit unit, boolean isAbs) {
/* 1411 */     return (new DateBetween(beginDate, endDate, isAbs)).between(unit);
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
/*      */   public static long betweenMs(Date beginDate, Date endDate) {
/* 1423 */     return (new DateBetween(beginDate, endDate)).between(DateUnit.MS);
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
/*      */   public static long betweenDay(Date beginDate, Date endDate, boolean isReset) {
/* 1443 */     if (isReset) {
/* 1444 */       beginDate = beginOfDay(beginDate);
/* 1445 */       endDate = beginOfDay(endDate);
/*      */     } 
/* 1447 */     return between(beginDate, endDate, DateUnit.DAY);
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
/*      */   public static long betweenWeek(Date beginDate, Date endDate, boolean isReset) {
/* 1459 */     if (isReset) {
/* 1460 */       beginDate = beginOfDay(beginDate);
/* 1461 */       endDate = beginOfDay(endDate);
/*      */     } 
/* 1463 */     return between(beginDate, endDate, DateUnit.WEEK);
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
/*      */   public static long betweenMonth(Date beginDate, Date endDate, boolean isReset) {
/* 1477 */     return (new DateBetween(beginDate, endDate)).betweenMonth(isReset);
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
/*      */   public static long betweenYear(Date beginDate, Date endDate, boolean isReset) {
/* 1491 */     return (new DateBetween(beginDate, endDate)).betweenYear(isReset);
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
/*      */   public static String formatBetween(Date beginDate, Date endDate, BetweenFormatter.Level level) {
/* 1503 */     return formatBetween(between(beginDate, endDate, DateUnit.MS), level);
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
/*      */   public static String formatBetween(Date beginDate, Date endDate) {
/* 1515 */     return formatBetween(between(beginDate, endDate, DateUnit.MS));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String formatBetween(long betweenMs, BetweenFormatter.Level level) {
/* 1526 */     return (new BetweenFormatter(betweenMs, level)).format();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String formatBetween(long betweenMs) {
/* 1537 */     return (new BetweenFormatter(betweenMs, BetweenFormatter.Level.MILLISECOND)).format();
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
/*      */   public static boolean isIn(Date date, Date beginDate, Date endDate) {
/* 1551 */     if (date instanceof DateTime) {
/* 1552 */       return ((DateTime)date).isIn(beginDate, endDate);
/*      */     }
/* 1554 */     return (new DateTime(date)).isIn(beginDate, endDate);
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
/*      */   public static boolean isSameTime(Date date1, Date date2) {
/* 1568 */     return (date1.compareTo(date2) == 0);
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
/*      */   public static boolean isSameDay(Date date1, Date date2) {
/* 1580 */     if (date1 == null || date2 == null) {
/* 1581 */       throw new IllegalArgumentException("The date must not be null");
/*      */     }
/* 1583 */     return CalendarUtil.isSameDay(calendar(date1), calendar(date2));
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
/*      */   public static boolean isSameWeek(Date date1, Date date2, boolean isMon) {
/* 1595 */     if (date1 == null || date2 == null) {
/* 1596 */       throw new IllegalArgumentException("The date must not be null");
/*      */     }
/* 1598 */     return CalendarUtil.isSameWeek(calendar(date1), calendar(date2), isMon);
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
/*      */   public static boolean isSameMonth(Date date1, Date date2) {
/* 1610 */     if (date1 == null || date2 == null) {
/* 1611 */       throw new IllegalArgumentException("The date must not be null");
/*      */     }
/* 1613 */     return CalendarUtil.isSameMonth(calendar(date1), calendar(date2));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long spendNt(long preTime) {
/* 1624 */     return System.nanoTime() - preTime;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long spendMs(long preTime) {
/* 1634 */     return System.currentTimeMillis() - preTime;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static int toIntSecond(Date date) {
/* 1646 */     return Integer.parseInt(format(date, "yyMMddHHmm"));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static TimeInterval timer() {
/* 1656 */     return new TimeInterval();
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
/*      */   public static TimeInterval timer(boolean isNano) {
/* 1669 */     return new TimeInterval(isNano);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static StopWatch createStopWatch() {
/* 1699 */     return new StopWatch();
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
/*      */   public static StopWatch createStopWatch(String id) {
/* 1730 */     return new StopWatch(id);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int ageOfNow(String birthDay) {
/* 1740 */     return ageOfNow(parse(birthDay));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int ageOfNow(Date birthDay) {
/* 1750 */     return age(birthDay, date());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isLeapYear(int year) {
/* 1760 */     return Year.isLeap(year);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int age(Date birthday, Date dateToCompare) {
/* 1771 */     Assert.notNull(birthday, "Birthday can not be null !", new Object[0]);
/* 1772 */     if (null == dateToCompare) {
/* 1773 */       dateToCompare = date();
/*      */     }
/* 1775 */     return age(birthday.getTime(), dateToCompare.getTime());
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
/*      */   @Deprecated
/*      */   public static boolean isExpired(Date startDate, DateField dateField, int timeLength, Date endDate) {
/* 1791 */     Date offsetDate = offset(startDate, dateField, timeLength);
/* 1792 */     return offsetDate.after(endDate);
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
/*      */   @Deprecated
/*      */   public static boolean isExpired(Date startDate, Date endDate, Date checkDate) {
/* 1812 */     return (betweenMs(startDate, checkDate) > betweenMs(startDate, endDate));
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
/*      */   public static int timeToSecond(String timeStr) {
/* 1824 */     if (StrUtil.isEmpty(timeStr)) {
/* 1825 */       return 0;
/*      */     }
/*      */     
/* 1828 */     List<String> hms = StrUtil.splitTrim(timeStr, ':', 3);
/* 1829 */     int lastIndex = hms.size() - 1;
/*      */     
/* 1831 */     int result = 0;
/* 1832 */     for (int i = lastIndex; i >= 0; i--) {
/* 1833 */       result = (int)(result + Integer.parseInt(hms.get(i)) * Math.pow(60.0D, (lastIndex - i)));
/*      */     }
/* 1835 */     return result;
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
/*      */   public static String secondToTime(int seconds) {
/* 1847 */     if (seconds < 0) {
/* 1848 */       throw new IllegalArgumentException("Seconds must be a positive number!");
/*      */     }
/*      */     
/* 1851 */     int hour = seconds / 3600;
/* 1852 */     int other = seconds % 3600;
/* 1853 */     int minute = other / 60;
/* 1854 */     int second = other % 60;
/* 1855 */     StringBuilder sb = new StringBuilder();
/* 1856 */     if (hour < 10) {
/* 1857 */       sb.append("0");
/*      */     }
/* 1859 */     sb.append(hour);
/* 1860 */     sb.append(":");
/* 1861 */     if (minute < 10) {
/* 1862 */       sb.append("0");
/*      */     }
/* 1864 */     sb.append(minute);
/* 1865 */     sb.append(":");
/* 1866 */     if (second < 10) {
/* 1867 */       sb.append("0");
/*      */     }
/* 1869 */     sb.append(second);
/* 1870 */     return sb.toString();
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
/*      */   public static DateRange range(Date start, Date end, DateField unit) {
/* 1882 */     return new DateRange(start, end, unit);
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
/*      */   public static List<DateTime> rangeContains(DateRange start, DateRange end) {
/* 1895 */     List<DateTime> startDateTimes = CollUtil.newArrayList((Iterable)start);
/* 1896 */     List<DateTime> endDateTimes = CollUtil.newArrayList((Iterable)end);
/* 1897 */     return (List<DateTime>)startDateTimes.stream().filter(endDateTimes::contains).collect(Collectors.toList());
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
/*      */   public static List<DateTime> rangeNotContains(DateRange start, DateRange end) {
/* 1910 */     List<DateTime> startDateTimes = CollUtil.newArrayList((Iterable)start);
/* 1911 */     List<DateTime> endDateTimes = CollUtil.newArrayList((Iterable)end);
/* 1912 */     return (List<DateTime>)endDateTimes.stream().filter(item -> !startDateTimes.contains(item)).collect(Collectors.toList());
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
/*      */   public static <T> List<T> rangeFunc(Date start, Date end, DateField unit, Function<Date, T> func) {
/* 1927 */     if (start == null || end == null || start.after(end)) {
/* 1928 */       return Collections.emptyList();
/*      */     }
/* 1930 */     ArrayList<T> list = new ArrayList<>();
/* 1931 */     for (DateTime date : range(start, end, unit)) {
/* 1932 */       list.add(func.apply(date));
/*      */     }
/* 1934 */     return list;
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
/*      */   public static void rangeConsume(Date start, Date end, DateField unit, Consumer<Date> consumer) {
/* 1947 */     if (start == null || end == null || start.after(end)) {
/*      */       return;
/*      */     }
/* 1950 */     range(start, end, unit).forEach(consumer);
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
/*      */   public static List<DateTime> rangeToList(Date start, Date end, DateField unit) {
/* 1962 */     return CollUtil.newArrayList((Iterable)range(start, end, unit));
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
/*      */   public static List<DateTime> rangeToList(Date start, Date end, DateField unit, int step) {
/* 1976 */     return CollUtil.newArrayList((Iterable)new DateRange(start, end, unit, step));
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
/*      */   public static String getZodiac(int month, int day) {
/* 1988 */     return Zodiac.getZodiac(month, day);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getChineseZodiac(int year) {
/* 1999 */     return Zodiac.getChineseZodiac(year);
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
/*      */   public static int compare(Date date1, Date date2) {
/* 2011 */     return CompareUtil.compare(date1, date2);
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
/*      */   public static int compare(Date date1, Date date2, String format) {
/* 2025 */     if (format != null) {
/* 2026 */       if (date1 != null) {
/* 2027 */         date1 = parse(format(date1, format), format);
/*      */       }
/* 2029 */       if (date2 != null) {
/* 2030 */         date2 = parse(format(date2, format), format);
/*      */       }
/*      */     } 
/* 2033 */     return CompareUtil.compare(date1, date2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long nanosToMillis(long duration) {
/* 2044 */     return TimeUnit.NANOSECONDS.toMillis(duration);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double nanosToSeconds(long duration) {
/* 2055 */     return duration / 1.0E9D;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Instant toInstant(Date date) {
/* 2066 */     return (null == date) ? null : date.toInstant();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Instant toInstant(TemporalAccessor temporalAccessor) {
/* 2077 */     return TemporalAccessorUtil.toInstant(temporalAccessor);
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
/*      */   public static LocalDateTime toLocalDateTime(Instant instant) {
/* 2089 */     return LocalDateTimeUtil.of(instant);
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
/*      */   public static LocalDateTime toLocalDateTime(Date date) {
/* 2101 */     return LocalDateTimeUtil.of(date);
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
/*      */   public static DateTime convertTimeZone(Date date, ZoneId zoneId) {
/* 2113 */     return new DateTime(date, ZoneUtil.toTimeZone(zoneId));
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
/*      */   public static DateTime convertTimeZone(Date date, TimeZone timeZone) {
/* 2125 */     return new DateTime(date, timeZone);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int lengthOfYear(int year) {
/* 2136 */     return Year.of(year).length();
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
/*      */   public static int lengthOfMonth(int month, boolean isLeapYear) {
/* 2148 */     return Month.of(month).length(isLeapYear);
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
/*      */   public static SimpleDateFormat newSimpleFormat(String pattern) {
/* 2160 */     return newSimpleFormat(pattern, (Locale)null, (TimeZone)null);
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
/*      */   public static SimpleDateFormat newSimpleFormat(String pattern, Locale locale, TimeZone timeZone) {
/* 2174 */     if (null == locale) {
/* 2175 */       locale = Locale.getDefault(Locale.Category.FORMAT);
/*      */     }
/* 2177 */     SimpleDateFormat format = new SimpleDateFormat(pattern, locale);
/* 2178 */     if (null != timeZone) {
/* 2179 */       format.setTimeZone(timeZone);
/*      */     }
/* 2181 */     format.setLenient(false);
/* 2182 */     return format;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getShotName(TimeUnit unit) {
/* 2193 */     switch (unit) {
/*      */       case NANOSECONDS:
/* 2195 */         return "ns";
/*      */       case MICROSECONDS:
/* 2197 */         return "μs";
/*      */       case MILLISECONDS:
/* 2199 */         return "ms";
/*      */       case SECONDS:
/* 2201 */         return "s";
/*      */       case MINUTES:
/* 2203 */         return "min";
/*      */       case HOURS:
/* 2205 */         return "h";
/*      */     } 
/* 2207 */     return unit.name().toLowerCase();
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
/*      */   public static boolean isOverlap(Date realStartTime, Date realEndTime, Date startTime, Date endTime) {
/* 2228 */     return (startTime.before(realEndTime) && endTime.after(realStartTime));
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
/*      */   private static String normalize(CharSequence dateStr) {
/* 2264 */     if (StrUtil.isBlank(dateStr)) {
/* 2265 */       return StrUtil.str(dateStr);
/*      */     }
/*      */ 
/*      */     
/* 2269 */     List<String> dateAndTime = StrUtil.splitTrim(dateStr, ' ');
/* 2270 */     int size = dateAndTime.size();
/* 2271 */     if (size < 1 || size > 2)
/*      */     {
/* 2273 */       return StrUtil.str(dateStr);
/*      */     }
/*      */     
/* 2276 */     StringBuilder builder = StrUtil.builder();
/*      */ 
/*      */     
/* 2279 */     String datePart = ((String)dateAndTime.get(0)).replaceAll("[/.年月]", "-");
/* 2280 */     datePart = StrUtil.removeSuffix(datePart, "日");
/* 2281 */     builder.append(datePart);
/*      */ 
/*      */     
/* 2284 */     if (size == 2) {
/* 2285 */       builder.append(' ');
/* 2286 */       String timePart = ((String)dateAndTime.get(1)).replaceAll("[时分秒]", ":");
/* 2287 */       timePart = StrUtil.removeSuffix(timePart, ":");
/*      */       
/* 2289 */       timePart = timePart.replace(',', '.');
/* 2290 */       builder.append(timePart);
/*      */     } 
/*      */     
/* 2293 */     return builder.toString();
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\date\DateUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */