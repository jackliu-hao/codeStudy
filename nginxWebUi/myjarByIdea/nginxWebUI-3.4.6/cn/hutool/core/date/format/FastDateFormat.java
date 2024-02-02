package cn.hutool.core.date.format;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class FastDateFormat extends Format implements DateParser, DatePrinter {
   private static final long serialVersionUID = 8097890768636183236L;
   public static final int FULL = 0;
   public static final int LONG = 1;
   public static final int MEDIUM = 2;
   public static final int SHORT = 3;
   private static final FormatCache<FastDateFormat> CACHE = new FormatCache<FastDateFormat>() {
      protected FastDateFormat createInstance(String pattern, TimeZone timeZone, Locale locale) {
         return new FastDateFormat(pattern, timeZone, locale);
      }
   };
   private final FastDatePrinter printer;
   private final FastDateParser parser;

   public static FastDateFormat getInstance() {
      return (FastDateFormat)CACHE.getInstance();
   }

   public static FastDateFormat getInstance(String pattern) {
      return (FastDateFormat)CACHE.getInstance(pattern, (TimeZone)null, (Locale)null);
   }

   public static FastDateFormat getInstance(String pattern, TimeZone timeZone) {
      return (FastDateFormat)CACHE.getInstance(pattern, timeZone, (Locale)null);
   }

   public static FastDateFormat getInstance(String pattern, Locale locale) {
      return (FastDateFormat)CACHE.getInstance(pattern, (TimeZone)null, locale);
   }

   public static FastDateFormat getInstance(String pattern, TimeZone timeZone, Locale locale) {
      return (FastDateFormat)CACHE.getInstance(pattern, timeZone, locale);
   }

   public static FastDateFormat getDateInstance(int style) {
      return (FastDateFormat)CACHE.getDateInstance(style, (TimeZone)null, (Locale)null);
   }

   public static FastDateFormat getDateInstance(int style, Locale locale) {
      return (FastDateFormat)CACHE.getDateInstance(style, (TimeZone)null, locale);
   }

   public static FastDateFormat getDateInstance(int style, TimeZone timeZone) {
      return (FastDateFormat)CACHE.getDateInstance(style, timeZone, (Locale)null);
   }

   public static FastDateFormat getDateInstance(int style, TimeZone timeZone, Locale locale) {
      return (FastDateFormat)CACHE.getDateInstance(style, timeZone, locale);
   }

   public static FastDateFormat getTimeInstance(int style) {
      return (FastDateFormat)CACHE.getTimeInstance(style, (TimeZone)null, (Locale)null);
   }

   public static FastDateFormat getTimeInstance(int style, Locale locale) {
      return (FastDateFormat)CACHE.getTimeInstance(style, (TimeZone)null, locale);
   }

   public static FastDateFormat getTimeInstance(int style, TimeZone timeZone) {
      return (FastDateFormat)CACHE.getTimeInstance(style, timeZone, (Locale)null);
   }

   public static FastDateFormat getTimeInstance(int style, TimeZone timeZone, Locale locale) {
      return (FastDateFormat)CACHE.getTimeInstance(style, timeZone, locale);
   }

   public static FastDateFormat getDateTimeInstance(int dateStyle, int timeStyle) {
      return (FastDateFormat)CACHE.getDateTimeInstance(dateStyle, timeStyle, (TimeZone)null, (Locale)null);
   }

   public static FastDateFormat getDateTimeInstance(int dateStyle, int timeStyle, Locale locale) {
      return (FastDateFormat)CACHE.getDateTimeInstance(dateStyle, timeStyle, (TimeZone)null, locale);
   }

   public static FastDateFormat getDateTimeInstance(int dateStyle, int timeStyle, TimeZone timeZone) {
      return getDateTimeInstance(dateStyle, timeStyle, timeZone, (Locale)null);
   }

   public static FastDateFormat getDateTimeInstance(int dateStyle, int timeStyle, TimeZone timeZone, Locale locale) {
      return (FastDateFormat)CACHE.getDateTimeInstance(dateStyle, timeStyle, timeZone, locale);
   }

   protected FastDateFormat(String pattern, TimeZone timeZone, Locale locale) {
      this(pattern, timeZone, locale, (Date)null);
   }

   protected FastDateFormat(String pattern, TimeZone timeZone, Locale locale, Date centuryStart) {
      this.printer = new FastDatePrinter(pattern, timeZone, locale);
      this.parser = new FastDateParser(pattern, timeZone, locale, centuryStart);
   }

   public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
      return toAppendTo.append(this.printer.format(obj));
   }

   public String format(long millis) {
      return this.printer.format(millis);
   }

   public String format(Date date) {
      return this.printer.format(date);
   }

   public String format(Calendar calendar) {
      return this.printer.format(calendar);
   }

   public <B extends Appendable> B format(long millis, B buf) {
      return this.printer.format(millis, buf);
   }

   public <B extends Appendable> B format(Date date, B buf) {
      return this.printer.format(date, buf);
   }

   public <B extends Appendable> B format(Calendar calendar, B buf) {
      return this.printer.format(calendar, buf);
   }

   public Date parse(String source) throws ParseException {
      return this.parser.parse(source);
   }

   public Date parse(String source, ParsePosition pos) {
      return this.parser.parse(source, pos);
   }

   public boolean parse(String source, ParsePosition pos, Calendar calendar) {
      return this.parser.parse(source, pos, calendar);
   }

   public Object parseObject(String source, ParsePosition pos) {
      return this.parser.parseObject(source, pos);
   }

   public String getPattern() {
      return this.printer.getPattern();
   }

   public TimeZone getTimeZone() {
      return this.printer.getTimeZone();
   }

   public Locale getLocale() {
      return this.printer.getLocale();
   }

   public int getMaxLengthEstimate() {
      return this.printer.getMaxLengthEstimate();
   }

   public DateTimeFormatter getDateTimeFormatter() {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(this.getPattern());
      if (this.getLocale() != null) {
         formatter = formatter.withLocale(this.getLocale());
      }

      if (this.getTimeZone() != null) {
         formatter = formatter.withZone(this.getTimeZone().toZoneId());
      }

      return formatter;
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof FastDateFormat)) {
         return false;
      } else {
         FastDateFormat other = (FastDateFormat)obj;
         return this.printer.equals(other.printer);
      }
   }

   public int hashCode() {
      return this.printer.hashCode();
   }

   public String toString() {
      return "FastDateFormat[" + this.printer.getPattern() + "," + this.printer.getLocale() + "," + this.printer.getTimeZone().getID() + "]";
   }
}
