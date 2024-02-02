/*     */ package cn.hutool.core.date.format;
/*     */ 
/*     */ import java.text.FieldPosition;
/*     */ import java.text.Format;
/*     */ import java.text.ParseException;
/*     */ import java.text.ParsePosition;
/*     */ import java.time.format.DateTimeFormatter;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FastDateFormat
/*     */   extends Format
/*     */   implements DateParser, DatePrinter
/*     */ {
/*     */   private static final long serialVersionUID = 8097890768636183236L;
/*     */   public static final int FULL = 0;
/*     */   public static final int LONG = 1;
/*     */   public static final int MEDIUM = 2;
/*     */   public static final int SHORT = 3;
/*     */   
/*  44 */   private static final FormatCache<FastDateFormat> CACHE = new FormatCache<FastDateFormat>()
/*     */     {
/*     */       protected FastDateFormat createInstance(String pattern, TimeZone timeZone, Locale locale) {
/*  47 */         return new FastDateFormat(pattern, timeZone, locale);
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */   
/*     */   private final FastDatePrinter printer;
/*     */ 
/*     */   
/*     */   private final FastDateParser parser;
/*     */ 
/*     */ 
/*     */   
/*     */   public static FastDateFormat getInstance() {
/*  61 */     return CACHE.getInstance();
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
/*     */   public static FastDateFormat getInstance(String pattern) {
/*  73 */     return CACHE.getInstance(pattern, null, null);
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
/*     */   public static FastDateFormat getInstance(String pattern, TimeZone timeZone) {
/*  86 */     return CACHE.getInstance(pattern, timeZone, null);
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
/*     */   public static FastDateFormat getInstance(String pattern, Locale locale) {
/*  99 */     return CACHE.getInstance(pattern, null, locale);
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
/*     */   public static FastDateFormat getInstance(String pattern, TimeZone timeZone, Locale locale) {
/* 113 */     return CACHE.getInstance(pattern, timeZone, locale);
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
/*     */   public static FastDateFormat getDateInstance(int style) {
/* 125 */     return CACHE.getDateInstance(style, null, null);
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
/*     */   public static FastDateFormat getDateInstance(int style, Locale locale) {
/* 137 */     return CACHE.getDateInstance(style, null, locale);
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
/*     */   public static FastDateFormat getDateInstance(int style, TimeZone timeZone) {
/* 149 */     return CACHE.getDateInstance(style, timeZone, null);
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
/*     */   public static FastDateFormat getDateInstance(int style, TimeZone timeZone, Locale locale) {
/* 162 */     return CACHE.getDateInstance(style, timeZone, locale);
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
/*     */   public static FastDateFormat getTimeInstance(int style) {
/* 174 */     return CACHE.getTimeInstance(style, null, null);
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
/*     */   public static FastDateFormat getTimeInstance(int style, Locale locale) {
/* 186 */     return CACHE.getTimeInstance(style, null, locale);
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
/*     */   public static FastDateFormat getTimeInstance(int style, TimeZone timeZone) {
/* 198 */     return CACHE.getTimeInstance(style, timeZone, null);
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
/*     */   public static FastDateFormat getTimeInstance(int style, TimeZone timeZone, Locale locale) {
/* 211 */     return CACHE.getTimeInstance(style, timeZone, locale);
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
/*     */   public static FastDateFormat getDateTimeInstance(int dateStyle, int timeStyle) {
/* 224 */     return CACHE.getDateTimeInstance(Integer.valueOf(dateStyle), Integer.valueOf(timeStyle), null, null);
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
/*     */   public static FastDateFormat getDateTimeInstance(int dateStyle, int timeStyle, Locale locale) {
/* 237 */     return CACHE.getDateTimeInstance(Integer.valueOf(dateStyle), Integer.valueOf(timeStyle), null, locale);
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
/*     */   public static FastDateFormat getDateTimeInstance(int dateStyle, int timeStyle, TimeZone timeZone) {
/* 250 */     return getDateTimeInstance(dateStyle, timeStyle, timeZone, null);
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
/*     */   public static FastDateFormat getDateTimeInstance(int dateStyle, int timeStyle, TimeZone timeZone, Locale locale) {
/* 264 */     return CACHE.getDateTimeInstance(Integer.valueOf(dateStyle), Integer.valueOf(timeStyle), timeZone, locale);
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
/*     */   protected FastDateFormat(String pattern, TimeZone timeZone, Locale locale) {
/* 277 */     this(pattern, timeZone, locale, null);
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
/*     */   protected FastDateFormat(String pattern, TimeZone timeZone, Locale locale, Date centuryStart) {
/* 290 */     this.printer = new FastDatePrinter(pattern, timeZone, locale);
/* 291 */     this.parser = new FastDateParser(pattern, timeZone, locale, centuryStart);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
/* 298 */     return toAppendTo.append(this.printer.format(obj));
/*     */   }
/*     */ 
/*     */   
/*     */   public String format(long millis) {
/* 303 */     return this.printer.format(millis);
/*     */   }
/*     */ 
/*     */   
/*     */   public String format(Date date) {
/* 308 */     return this.printer.format(date);
/*     */   }
/*     */ 
/*     */   
/*     */   public String format(Calendar calendar) {
/* 313 */     return this.printer.format(calendar);
/*     */   }
/*     */ 
/*     */   
/*     */   public <B extends Appendable> B format(long millis, B buf) {
/* 318 */     return this.printer.format(millis, buf);
/*     */   }
/*     */ 
/*     */   
/*     */   public <B extends Appendable> B format(Date date, B buf) {
/* 323 */     return this.printer.format(date, buf);
/*     */   }
/*     */ 
/*     */   
/*     */   public <B extends Appendable> B format(Calendar calendar, B buf) {
/* 328 */     return this.printer.format(calendar, buf);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Date parse(String source) throws ParseException {
/* 334 */     return this.parser.parse(source);
/*     */   }
/*     */ 
/*     */   
/*     */   public Date parse(String source, ParsePosition pos) {
/* 339 */     return this.parser.parse(source, pos);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean parse(String source, ParsePosition pos, Calendar calendar) {
/* 344 */     return this.parser.parse(source, pos, calendar);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object parseObject(String source, ParsePosition pos) {
/* 349 */     return this.parser.parseObject(source, pos);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPattern() {
/* 355 */     return this.printer.getPattern();
/*     */   }
/*     */ 
/*     */   
/*     */   public TimeZone getTimeZone() {
/* 360 */     return this.printer.getTimeZone();
/*     */   }
/*     */ 
/*     */   
/*     */   public Locale getLocale() {
/* 365 */     return this.printer.getLocale();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMaxLengthEstimate() {
/* 375 */     return this.printer.getMaxLengthEstimate();
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
/*     */   public DateTimeFormatter getDateTimeFormatter() {
/* 389 */     DateTimeFormatter formatter = DateTimeFormatter.ofPattern(getPattern());
/* 390 */     if (getLocale() != null) {
/* 391 */       formatter = formatter.withLocale(getLocale());
/*     */     }
/* 393 */     if (getTimeZone() != null) {
/* 394 */       formatter = formatter.withZone(getTimeZone().toZoneId());
/*     */     }
/* 396 */     return formatter;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 403 */     if (!(obj instanceof FastDateFormat)) {
/* 404 */       return false;
/*     */     }
/* 406 */     FastDateFormat other = (FastDateFormat)obj;
/*     */     
/* 408 */     return this.printer.equals(other.printer);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 413 */     return this.printer.hashCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 418 */     return "FastDateFormat[" + this.printer.getPattern() + "," + this.printer.getLocale() + "," + this.printer.getTimeZone().getID() + "]";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\date\format\FastDateFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */