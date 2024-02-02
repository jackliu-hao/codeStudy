/*      */ package cn.hutool.core.date;
/*      */ 
/*      */ import cn.hutool.core.date.format.DateParser;
/*      */ import cn.hutool.core.date.format.DatePrinter;
/*      */ import cn.hutool.core.date.format.FastDateFormat;
/*      */ import cn.hutool.core.date.format.GlobalCustomFormat;
/*      */ import cn.hutool.core.lang.Assert;
/*      */ import cn.hutool.core.util.ObjectUtil;
/*      */ import cn.hutool.core.util.StrUtil;
/*      */ import cn.hutool.core.util.SystemPropsUtil;
/*      */ import java.sql.Date;
/*      */ import java.sql.Timestamp;
/*      */ import java.text.DateFormat;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.time.Instant;
/*      */ import java.time.LocalDateTime;
/*      */ import java.time.ZoneId;
/*      */ import java.time.ZonedDateTime;
/*      */ import java.time.format.DateTimeFormatter;
/*      */ import java.time.temporal.TemporalAccessor;
/*      */ import java.util.Calendar;
/*      */ import java.util.Date;
/*      */ import java.util.Locale;
/*      */ import java.util.TimeZone;
/*      */ import java.util.function.Supplier;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class DateTime
/*      */   extends Date
/*      */ {
/*      */   private static final long serialVersionUID = -5395712593979185936L;
/*      */   private static boolean useJdkToStringStyle = false;
/*      */   
/*      */   public static void setUseJdkToStringStyle(boolean customUseJdkToStringStyle) {
/*   48 */     useJdkToStringStyle = customUseJdkToStringStyle;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean mutable = true;
/*      */ 
/*      */ 
/*      */   
/*   58 */   private Week firstDayOfWeek = Week.MONDAY;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private TimeZone timeZone;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int minimalDaysInFirstWeek;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime of(long timeMillis) {
/*   77 */     return new DateTime(timeMillis);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime of(Date date) {
/*   87 */     if (date instanceof DateTime) {
/*   88 */       return (DateTime)date;
/*      */     }
/*   90 */     return new DateTime(date);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime of(Calendar calendar) {
/*  100 */     return new DateTime(calendar);
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
/*      */   public static DateTime of(String dateStr, String format) {
/*  112 */     return new DateTime(dateStr, format);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static DateTime now() {
/*  121 */     return new DateTime();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DateTime() {
/*  130 */     this(TimeZone.getDefault());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DateTime(TimeZone timeZone) {
/*  140 */     this(System.currentTimeMillis(), timeZone);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DateTime(Date date) {
/*  149 */     this(date, (date instanceof DateTime) ? ((DateTime)date).timeZone : 
/*      */         
/*  151 */         TimeZone.getDefault());
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
/*      */   public DateTime(Date date, TimeZone timeZone) {
/*  163 */     this(((Date)ObjectUtil.defaultIfNull(date, Date::new)).getTime(), timeZone);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DateTime(Calendar calendar) {
/*  172 */     this(calendar.getTime(), calendar.getTimeZone());
/*  173 */     setFirstDayOfWeek(Week.of(calendar.getFirstDayOfWeek()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DateTime(Instant instant) {
/*  183 */     this(instant.toEpochMilli());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DateTime(Instant instant, ZoneId zoneId) {
/*  194 */     this(instant.toEpochMilli(), ZoneUtil.toTimeZone(zoneId));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DateTime(TemporalAccessor temporalAccessor) {
/*  204 */     this(TemporalAccessorUtil.toInstant(temporalAccessor));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DateTime(ZonedDateTime zonedDateTime) {
/*  214 */     this(zonedDateTime.toInstant(), zonedDateTime.getZone());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DateTime(long timeMillis) {
/*  224 */     this(timeMillis, TimeZone.getDefault());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DateTime(long timeMillis, TimeZone timeZone) {
/*  235 */     super(timeMillis);
/*  236 */     this.timeZone = (TimeZone)ObjectUtil.defaultIfNull(timeZone, TimeZone::getDefault);
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
/*      */   public DateTime(CharSequence dateStr) {
/*  268 */     this(DateUtil.parse(dateStr));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DateTime(CharSequence dateStr, String format) {
/*  279 */     this(GlobalCustomFormat.isCustomFormat(format) ? 
/*  280 */         GlobalCustomFormat.parse(dateStr, format) : 
/*  281 */         parse(dateStr, DateUtil.newSimpleFormat(format)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DateTime(CharSequence dateStr, DateFormat dateFormat) {
/*  292 */     this(parse(dateStr, dateFormat), dateFormat.getTimeZone());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DateTime(CharSequence dateStr, DateTimeFormatter formatter) {
/*  303 */     this(TemporalAccessorUtil.toInstant(formatter.parse(dateStr)), formatter.getZone());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DateTime(CharSequence dateStr, DateParser dateParser) {
/*  314 */     this(dateStr, dateParser, SystemPropsUtil.getBoolean(SystemPropsUtil.HUTOOL_DATE_LENIENT, true));
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
/*      */   public DateTime(CharSequence dateStr, DateParser dateParser, boolean lenient) {
/*  326 */     this(parse(dateStr, dateParser, lenient));
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
/*      */   public DateTime offset(DateField datePart, int offset) {
/*  342 */     if (DateField.ERA == datePart) {
/*  343 */       throw new IllegalArgumentException("ERA is not support offset!");
/*      */     }
/*      */     
/*  346 */     Calendar cal = toCalendar();
/*      */     
/*  348 */     cal.add(datePart.getValue(), offset);
/*      */     
/*  350 */     DateTime dt = this.mutable ? this : (DateTime)ObjectUtil.clone(this);
/*  351 */     return dt.setTimeInternal(cal.getTimeInMillis());
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
/*      */   public DateTime offsetNew(DateField datePart, int offset) {
/*  364 */     Calendar cal = toCalendar();
/*      */     
/*  366 */     cal.add(datePart.getValue(), offset);
/*      */     
/*  368 */     return ((DateTime)ObjectUtil.clone(this)).setTimeInternal(cal.getTimeInMillis());
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
/*      */   public int getField(DateField field) {
/*  382 */     return getField(field.getValue());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getField(int field) {
/*  393 */     return toCalendar().get(field);
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
/*      */   public DateTime setField(DateField field, int value) {
/*  405 */     return setField(field.getValue(), value);
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
/*      */   public DateTime setField(int field, int value) {
/*  417 */     Calendar calendar = toCalendar();
/*  418 */     calendar.set(field, value);
/*      */     
/*  420 */     DateTime dt = this;
/*  421 */     if (false == this.mutable) {
/*  422 */       dt = (DateTime)ObjectUtil.clone(this);
/*      */     }
/*  424 */     return dt.setTimeInternal(calendar.getTimeInMillis());
/*      */   }
/*      */ 
/*      */   
/*      */   public void setTime(long time) {
/*  429 */     if (this.mutable) {
/*  430 */       super.setTime(time);
/*      */     } else {
/*  432 */       throw new DateException("This is not a mutable object !");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int year() {
/*  442 */     return getField(DateField.YEAR);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int quarter() {
/*  451 */     return month() / 3 + 1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Quarter quarterEnum() {
/*  460 */     return Quarter.of(quarter());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int month() {
/*  469 */     return getField(DateField.MONTH);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int monthBaseOne() {
/*  479 */     return month() + 1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int monthStartFromOne() {
/*  489 */     return month() + 1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Month monthEnum() {
/*  498 */     return Month.of(month());
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
/*      */   public int weekOfYear() {
/*  512 */     return getField(DateField.WEEK_OF_YEAR);
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
/*      */   public int weekOfMonth() {
/*  525 */     return getField(DateField.WEEK_OF_MONTH);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int dayOfMonth() {
/*  534 */     return getField(DateField.DAY_OF_MONTH);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int dayOfYear() {
/*  544 */     return getField(DateField.DAY_OF_YEAR);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int dayOfWeek() {
/*  553 */     return getField(DateField.DAY_OF_WEEK);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int dayOfWeekInMonth() {
/*  562 */     return getField(DateField.DAY_OF_WEEK_IN_MONTH);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Week dayOfWeekEnum() {
/*  571 */     return Week.of(dayOfWeek());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int hour(boolean is24HourClock) {
/*  581 */     return getField(is24HourClock ? DateField.HOUR_OF_DAY : DateField.HOUR);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int minute() {
/*  591 */     return getField(DateField.MINUTE);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int second() {
/*  600 */     return getField(DateField.SECOND);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int millisecond() {
/*  609 */     return getField(DateField.MILLISECOND);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isAM() {
/*  618 */     return (0 == getField(DateField.AM_PM));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isPM() {
/*  627 */     return (1 == getField(DateField.AM_PM));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isWeekend() {
/*  637 */     int dayOfWeek = dayOfWeek();
/*  638 */     return (7 == dayOfWeek || 1 == dayOfWeek);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isLeapYear() {
/*  649 */     return DateUtil.isLeapYear(year());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Calendar toCalendar() {
/*  658 */     return toCalendar(Locale.getDefault(Locale.Category.FORMAT));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Calendar toCalendar(Locale locale) {
/*  668 */     return toCalendar(this.timeZone, locale);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Calendar toCalendar(TimeZone zone) {
/*  678 */     return toCalendar(zone, Locale.getDefault(Locale.Category.FORMAT));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Calendar toCalendar(TimeZone zone, Locale locale) {
/*  689 */     if (null == locale) {
/*  690 */       locale = Locale.getDefault(Locale.Category.FORMAT);
/*      */     }
/*  692 */     Calendar cal = (null != zone) ? Calendar.getInstance(zone, locale) : Calendar.getInstance(locale);
/*      */     
/*  694 */     cal.setFirstDayOfWeek(this.firstDayOfWeek.getValue());
/*      */     
/*  696 */     if (this.minimalDaysInFirstWeek > 0) {
/*  697 */       cal.setMinimalDaysInFirstWeek(this.minimalDaysInFirstWeek);
/*      */     }
/*  699 */     cal.setTime(this);
/*  700 */     return cal;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Date toJdkDate() {
/*  711 */     return new Date(getTime());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Timestamp toTimestamp() {
/*  720 */     return new Timestamp(getTime());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Date toSqlDate() {
/*  729 */     return new Date(getTime());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public LocalDateTime toLocalDateTime() {
/*  739 */     return LocalDateTimeUtil.of(this);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DateBetween between(Date date) {
/*  749 */     return new DateBetween(this, date);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long between(Date date, DateUnit unit) {
/*  760 */     return (new DateBetween(this, date)).between(unit);
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
/*      */   public String between(Date date, DateUnit unit, BetweenFormatter.Level formatLevel) {
/*  772 */     return (new DateBetween(this, date)).toString(unit, formatLevel);
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
/*      */   public boolean isIn(Date beginDate, Date endDate) {
/*  785 */     long beginMills = beginDate.getTime();
/*  786 */     long endMills = endDate.getTime();
/*  787 */     long thisMills = getTime();
/*      */     
/*  789 */     return (thisMills >= Math.min(beginMills, endMills) && thisMills <= Math.max(beginMills, endMills));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isBefore(Date date) {
/*  800 */     if (null == date) {
/*  801 */       throw new NullPointerException("Date to compare is null !");
/*      */     }
/*  803 */     return (compareTo(date) < 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isBeforeOrEquals(Date date) {
/*  814 */     if (null == date) {
/*  815 */       throw new NullPointerException("Date to compare is null !");
/*      */     }
/*  817 */     return (compareTo(date) <= 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isAfter(Date date) {
/*  828 */     if (null == date) {
/*  829 */       throw new NullPointerException("Date to compare is null !");
/*      */     }
/*  831 */     return (compareTo(date) > 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isAfterOrEquals(Date date) {
/*  842 */     if (null == date) {
/*  843 */       throw new NullPointerException("Date to compare is null !");
/*      */     }
/*  845 */     return (compareTo(date) >= 0);
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
/*      */   public boolean isMutable() {
/*  861 */     return this.mutable;
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
/*      */   public DateTime setMutable(boolean mutable) {
/*  877 */     this.mutable = mutable;
/*  878 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Week getFirstDayOfWeek() {
/*  887 */     return this.firstDayOfWeek;
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
/*      */   public DateTime setFirstDayOfWeek(Week firstDayOfWeek) {
/*  901 */     this.firstDayOfWeek = firstDayOfWeek;
/*  902 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TimeZone getTimeZone() {
/*  912 */     return this.timeZone;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ZoneId getZoneId() {
/*  922 */     return this.timeZone.toZoneId();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DateTime setTimeZone(TimeZone timeZone) {
/*  933 */     this.timeZone = (TimeZone)ObjectUtil.defaultIfNull(timeZone, TimeZone::getDefault);
/*  934 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DateTime setMinimalDaysInFirstWeek(int minimalDaysInFirstWeek) {
/*  945 */     this.minimalDaysInFirstWeek = minimalDaysInFirstWeek;
/*  946 */     return this;
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
/*      */   public String toString() {
/*  961 */     if (useJdkToStringStyle) {
/*  962 */       return super.toString();
/*      */     }
/*  964 */     return toString(this.timeZone);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toStringDefaultTimeZone() {
/*  975 */     return toString(TimeZone.getDefault());
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
/*      */   public String toString(TimeZone timeZone) {
/*  987 */     if (null != timeZone) {
/*  988 */       return toString(DateUtil.newSimpleFormat("yyyy-MM-dd HH:mm:ss", null, timeZone));
/*      */     }
/*  990 */     return toString((DatePrinter)DatePattern.NORM_DATETIME_FORMAT);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toDateStr() {
/* 1000 */     if (null != this.timeZone) {
/* 1001 */       return toString(DateUtil.newSimpleFormat("yyyy-MM-dd", null, this.timeZone));
/*      */     }
/* 1003 */     return toString((DatePrinter)DatePattern.NORM_DATE_FORMAT);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toTimeStr() {
/* 1013 */     if (null != this.timeZone) {
/* 1014 */       return toString(DateUtil.newSimpleFormat("HH:mm:ss", null, this.timeZone));
/*      */     }
/* 1016 */     return toString((DatePrinter)DatePattern.NORM_TIME_FORMAT);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toString(String format) {
/* 1026 */     if (null != this.timeZone) {
/* 1027 */       return toString(DateUtil.newSimpleFormat(format, null, this.timeZone));
/*      */     }
/* 1029 */     return toString((DatePrinter)FastDateFormat.getInstance(format));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toString(DatePrinter format) {
/* 1039 */     return format.format(this);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toString(DateFormat format) {
/* 1049 */     return format.format(this);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toMsStr() {
/* 1056 */     return toString((DatePrinter)DatePattern.NORM_DATETIME_MS_FORMAT);
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
/*      */   private static Date parse(CharSequence dateStr, DateFormat dateFormat) {
/* 1068 */     Assert.notBlank(dateStr, "Date String must be not blank !", new Object[0]);
/*      */     try {
/* 1070 */       return dateFormat.parse(dateStr.toString());
/* 1071 */     } catch (Exception e) {
/*      */       String pattern;
/* 1073 */       if (dateFormat instanceof SimpleDateFormat) {
/* 1074 */         pattern = ((SimpleDateFormat)dateFormat).toPattern();
/*      */       } else {
/* 1076 */         pattern = dateFormat.toString();
/*      */       } 
/* 1078 */       throw new DateException(StrUtil.format("Parse [{}] with format [{}] error!", new Object[] { dateStr, pattern }), e);
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
/*      */   private static Calendar parse(CharSequence dateStr, DateParser parser, boolean lenient) {
/* 1091 */     Assert.notNull(parser, "Parser or DateFromat must be not null !", new Object[0]);
/* 1092 */     Assert.notBlank(dateStr, "Date String must be not blank !", new Object[0]);
/*      */     
/* 1094 */     Calendar calendar = CalendarUtil.parse(dateStr, lenient, parser);
/* 1095 */     if (null == calendar) {
/* 1096 */       throw new DateException("Parse [{}] with format [{}] error!", new Object[] { dateStr, parser.getPattern() });
/*      */     }
/*      */ 
/*      */     
/* 1100 */     calendar.setFirstDayOfWeek(Week.MONDAY.getValue());
/* 1101 */     return calendar;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private DateTime setTimeInternal(long time) {
/* 1111 */     super.setTime(time);
/* 1112 */     return this;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\date\DateTime.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */