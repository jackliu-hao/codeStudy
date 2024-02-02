package cn.hutool.core.date;

import cn.hutool.core.date.format.DateParser;
import cn.hutool.core.date.format.DatePrinter;
import cn.hutool.core.date.format.FastDateFormat;
import cn.hutool.core.date.format.GlobalCustomFormat;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.SystemPropsUtil;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Locale.Category;
import java.util.function.Supplier;

public class DateTime extends Date {
   private static final long serialVersionUID = -5395712593979185936L;
   private static boolean useJdkToStringStyle = false;
   private boolean mutable;
   private Week firstDayOfWeek;
   private TimeZone timeZone;
   private int minimalDaysInFirstWeek;

   public static void setUseJdkToStringStyle(boolean customUseJdkToStringStyle) {
      useJdkToStringStyle = customUseJdkToStringStyle;
   }

   public static DateTime of(long timeMillis) {
      return new DateTime(timeMillis);
   }

   public static DateTime of(Date date) {
      return date instanceof DateTime ? (DateTime)date : new DateTime(date);
   }

   public static DateTime of(Calendar calendar) {
      return new DateTime(calendar);
   }

   public static DateTime of(String dateStr, String format) {
      return new DateTime(dateStr, format);
   }

   public static DateTime now() {
      return new DateTime();
   }

   public DateTime() {
      this(TimeZone.getDefault());
   }

   public DateTime(TimeZone timeZone) {
      this(System.currentTimeMillis(), timeZone);
   }

   public DateTime(Date date) {
      this(date, date instanceof DateTime ? ((DateTime)date).timeZone : TimeZone.getDefault());
   }

   public DateTime(Date date, TimeZone timeZone) {
      this(((Date)ObjectUtil.defaultIfNull(date, (Supplier)(Date::new))).getTime(), timeZone);
   }

   public DateTime(Calendar calendar) {
      this(calendar.getTime(), calendar.getTimeZone());
      this.setFirstDayOfWeek(Week.of(calendar.getFirstDayOfWeek()));
   }

   public DateTime(Instant instant) {
      this(instant.toEpochMilli());
   }

   public DateTime(Instant instant, ZoneId zoneId) {
      this(instant.toEpochMilli(), ZoneUtil.toTimeZone(zoneId));
   }

   public DateTime(TemporalAccessor temporalAccessor) {
      this(TemporalAccessorUtil.toInstant(temporalAccessor));
   }

   public DateTime(ZonedDateTime zonedDateTime) {
      this(zonedDateTime.toInstant(), zonedDateTime.getZone());
   }

   public DateTime(long timeMillis) {
      this(timeMillis, TimeZone.getDefault());
   }

   public DateTime(long timeMillis, TimeZone timeZone) {
      super(timeMillis);
      this.mutable = true;
      this.firstDayOfWeek = Week.MONDAY;
      this.timeZone = (TimeZone)ObjectUtil.defaultIfNull(timeZone, (Supplier)(TimeZone::getDefault));
   }

   public DateTime(CharSequence dateStr) {
      this((Date)DateUtil.parse(dateStr));
   }

   public DateTime(CharSequence dateStr, String format) {
      this(GlobalCustomFormat.isCustomFormat(format) ? GlobalCustomFormat.parse(dateStr, format) : parse(dateStr, DateUtil.newSimpleFormat(format)));
   }

   public DateTime(CharSequence dateStr, DateFormat dateFormat) {
      this(parse(dateStr, dateFormat), dateFormat.getTimeZone());
   }

   public DateTime(CharSequence dateStr, DateTimeFormatter formatter) {
      this(TemporalAccessorUtil.toInstant(formatter.parse(dateStr)), formatter.getZone());
   }

   public DateTime(CharSequence dateStr, DateParser dateParser) {
      this(dateStr, dateParser, SystemPropsUtil.getBoolean(SystemPropsUtil.HUTOOL_DATE_LENIENT, true));
   }

   public DateTime(CharSequence dateStr, DateParser dateParser, boolean lenient) {
      this(parse(dateStr, dateParser, lenient));
   }

   public DateTime offset(DateField datePart, int offset) {
      if (DateField.ERA == datePart) {
         throw new IllegalArgumentException("ERA is not support offset!");
      } else {
         Calendar cal = this.toCalendar();
         cal.add(datePart.getValue(), offset);
         DateTime dt = this.mutable ? this : (DateTime)ObjectUtil.clone(this);
         return dt.setTimeInternal(cal.getTimeInMillis());
      }
   }

   public DateTime offsetNew(DateField datePart, int offset) {
      Calendar cal = this.toCalendar();
      cal.add(datePart.getValue(), offset);
      return ((DateTime)ObjectUtil.clone(this)).setTimeInternal(cal.getTimeInMillis());
   }

   public int getField(DateField field) {
      return this.getField(field.getValue());
   }

   public int getField(int field) {
      return this.toCalendar().get(field);
   }

   public DateTime setField(DateField field, int value) {
      return this.setField(field.getValue(), value);
   }

   public DateTime setField(int field, int value) {
      Calendar calendar = this.toCalendar();
      calendar.set(field, value);
      DateTime dt = this;
      if (!this.mutable) {
         dt = (DateTime)ObjectUtil.clone(this);
      }

      return dt.setTimeInternal(calendar.getTimeInMillis());
   }

   public void setTime(long time) {
      if (this.mutable) {
         super.setTime(time);
      } else {
         throw new DateException("This is not a mutable object !");
      }
   }

   public int year() {
      return this.getField(DateField.YEAR);
   }

   public int quarter() {
      return this.month() / 3 + 1;
   }

   public Quarter quarterEnum() {
      return Quarter.of(this.quarter());
   }

   public int month() {
      return this.getField(DateField.MONTH);
   }

   public int monthBaseOne() {
      return this.month() + 1;
   }

   public int monthStartFromOne() {
      return this.month() + 1;
   }

   public Month monthEnum() {
      return Month.of(this.month());
   }

   public int weekOfYear() {
      return this.getField(DateField.WEEK_OF_YEAR);
   }

   public int weekOfMonth() {
      return this.getField(DateField.WEEK_OF_MONTH);
   }

   public int dayOfMonth() {
      return this.getField(DateField.DAY_OF_MONTH);
   }

   public int dayOfYear() {
      return this.getField(DateField.DAY_OF_YEAR);
   }

   public int dayOfWeek() {
      return this.getField(DateField.DAY_OF_WEEK);
   }

   public int dayOfWeekInMonth() {
      return this.getField(DateField.DAY_OF_WEEK_IN_MONTH);
   }

   public Week dayOfWeekEnum() {
      return Week.of(this.dayOfWeek());
   }

   public int hour(boolean is24HourClock) {
      return this.getField(is24HourClock ? DateField.HOUR_OF_DAY : DateField.HOUR);
   }

   public int minute() {
      return this.getField(DateField.MINUTE);
   }

   public int second() {
      return this.getField(DateField.SECOND);
   }

   public int millisecond() {
      return this.getField(DateField.MILLISECOND);
   }

   public boolean isAM() {
      return 0 == this.getField(DateField.AM_PM);
   }

   public boolean isPM() {
      return 1 == this.getField(DateField.AM_PM);
   }

   public boolean isWeekend() {
      int dayOfWeek = this.dayOfWeek();
      return 7 == dayOfWeek || 1 == dayOfWeek;
   }

   public boolean isLeapYear() {
      return DateUtil.isLeapYear(this.year());
   }

   public Calendar toCalendar() {
      return this.toCalendar(Locale.getDefault(Category.FORMAT));
   }

   public Calendar toCalendar(Locale locale) {
      return this.toCalendar(this.timeZone, locale);
   }

   public Calendar toCalendar(TimeZone zone) {
      return this.toCalendar(zone, Locale.getDefault(Category.FORMAT));
   }

   public Calendar toCalendar(TimeZone zone, Locale locale) {
      if (null == locale) {
         locale = Locale.getDefault(Category.FORMAT);
      }

      Calendar cal = null != zone ? Calendar.getInstance(zone, locale) : Calendar.getInstance(locale);
      cal.setFirstDayOfWeek(this.firstDayOfWeek.getValue());
      if (this.minimalDaysInFirstWeek > 0) {
         cal.setMinimalDaysInFirstWeek(this.minimalDaysInFirstWeek);
      }

      cal.setTime(this);
      return cal;
   }

   public Date toJdkDate() {
      return new Date(this.getTime());
   }

   public Timestamp toTimestamp() {
      return new Timestamp(this.getTime());
   }

   public java.sql.Date toSqlDate() {
      return new java.sql.Date(this.getTime());
   }

   public LocalDateTime toLocalDateTime() {
      return LocalDateTimeUtil.of((Date)this);
   }

   public DateBetween between(Date date) {
      return new DateBetween(this, date);
   }

   public long between(Date date, DateUnit unit) {
      return (new DateBetween(this, date)).between(unit);
   }

   public String between(Date date, DateUnit unit, BetweenFormatter.Level formatLevel) {
      return (new DateBetween(this, date)).toString(unit, formatLevel);
   }

   public boolean isIn(Date beginDate, Date endDate) {
      long beginMills = beginDate.getTime();
      long endMills = endDate.getTime();
      long thisMills = this.getTime();
      return thisMills >= Math.min(beginMills, endMills) && thisMills <= Math.max(beginMills, endMills);
   }

   public boolean isBefore(Date date) {
      if (null == date) {
         throw new NullPointerException("Date to compare is null !");
      } else {
         return this.compareTo(date) < 0;
      }
   }

   public boolean isBeforeOrEquals(Date date) {
      if (null == date) {
         throw new NullPointerException("Date to compare is null !");
      } else {
         return this.compareTo(date) <= 0;
      }
   }

   public boolean isAfter(Date date) {
      if (null == date) {
         throw new NullPointerException("Date to compare is null !");
      } else {
         return this.compareTo(date) > 0;
      }
   }

   public boolean isAfterOrEquals(Date date) {
      if (null == date) {
         throw new NullPointerException("Date to compare is null !");
      } else {
         return this.compareTo(date) >= 0;
      }
   }

   public boolean isMutable() {
      return this.mutable;
   }

   public DateTime setMutable(boolean mutable) {
      this.mutable = mutable;
      return this;
   }

   public Week getFirstDayOfWeek() {
      return this.firstDayOfWeek;
   }

   public DateTime setFirstDayOfWeek(Week firstDayOfWeek) {
      this.firstDayOfWeek = firstDayOfWeek;
      return this;
   }

   public TimeZone getTimeZone() {
      return this.timeZone;
   }

   public ZoneId getZoneId() {
      return this.timeZone.toZoneId();
   }

   public DateTime setTimeZone(TimeZone timeZone) {
      this.timeZone = (TimeZone)ObjectUtil.defaultIfNull(timeZone, (Supplier)(TimeZone::getDefault));
      return this;
   }

   public DateTime setMinimalDaysInFirstWeek(int minimalDaysInFirstWeek) {
      this.minimalDaysInFirstWeek = minimalDaysInFirstWeek;
      return this;
   }

   public String toString() {
      return useJdkToStringStyle ? super.toString() : this.toString(this.timeZone);
   }

   public String toStringDefaultTimeZone() {
      return this.toString(TimeZone.getDefault());
   }

   public String toString(TimeZone timeZone) {
      return null != timeZone ? this.toString((DateFormat)DateUtil.newSimpleFormat("yyyy-MM-dd HH:mm:ss", (Locale)null, timeZone)) : this.toString((DatePrinter)DatePattern.NORM_DATETIME_FORMAT);
   }

   public String toDateStr() {
      return null != this.timeZone ? this.toString((DateFormat)DateUtil.newSimpleFormat("yyyy-MM-dd", (Locale)null, this.timeZone)) : this.toString((DatePrinter)DatePattern.NORM_DATE_FORMAT);
   }

   public String toTimeStr() {
      return null != this.timeZone ? this.toString((DateFormat)DateUtil.newSimpleFormat("HH:mm:ss", (Locale)null, this.timeZone)) : this.toString((DatePrinter)DatePattern.NORM_TIME_FORMAT);
   }

   public String toString(String format) {
      return null != this.timeZone ? this.toString((DateFormat)DateUtil.newSimpleFormat(format, (Locale)null, this.timeZone)) : this.toString((DatePrinter)FastDateFormat.getInstance(format));
   }

   public String toString(DatePrinter format) {
      return format.format((Date)this);
   }

   public String toString(DateFormat format) {
      return format.format(this);
   }

   public String toMsStr() {
      return this.toString((DatePrinter)DatePattern.NORM_DATETIME_MS_FORMAT);
   }

   private static Date parse(CharSequence dateStr, DateFormat dateFormat) {
      Assert.notBlank(dateStr, "Date String must be not blank !");

      try {
         return dateFormat.parse(dateStr.toString());
      } catch (Exception var4) {
         String pattern;
         if (dateFormat instanceof SimpleDateFormat) {
            pattern = ((SimpleDateFormat)dateFormat).toPattern();
         } else {
            pattern = dateFormat.toString();
         }

         throw new DateException(StrUtil.format("Parse [{}] with format [{}] error!", new Object[]{dateStr, pattern}), var4);
      }
   }

   private static Calendar parse(CharSequence dateStr, DateParser parser, boolean lenient) {
      Assert.notNull(parser, "Parser or DateFromat must be not null !");
      Assert.notBlank(dateStr, "Date String must be not blank !");
      Calendar calendar = CalendarUtil.parse(dateStr, lenient, parser);
      if (null == calendar) {
         throw new DateException("Parse [{}] with format [{}] error!", new Object[]{dateStr, parser.getPattern()});
      } else {
         calendar.setFirstDayOfWeek(Week.MONDAY.getValue());
         return calendar;
      }
   }

   private DateTime setTimeInternal(long time) {
      super.setTime(time);
      return this;
   }
}
