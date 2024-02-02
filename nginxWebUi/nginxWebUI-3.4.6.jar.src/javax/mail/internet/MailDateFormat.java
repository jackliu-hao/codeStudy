/*     */ package javax.mail.internet;
/*     */ 
/*     */ import com.sun.mail.util.MailLogger;
/*     */ import java.text.FieldPosition;
/*     */ import java.text.NumberFormat;
/*     */ import java.text.ParseException;
/*     */ import java.text.ParsePosition;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.GregorianCalendar;
/*     */ import java.util.Locale;
/*     */ import java.util.TimeZone;
/*     */ import java.util.logging.Level;
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
/*     */ public class MailDateFormat
/*     */   extends SimpleDateFormat
/*     */ {
/*     */   private static final long serialVersionUID = -8148227605210628779L;
/*     */   
/*     */   public MailDateFormat() {
/* 144 */     super("EEE, d MMM yyyy HH:mm:ss 'XXXXX' (z)", Locale.US);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StringBuffer format(Date date, StringBuffer dateStrBuf, FieldPosition fieldPosition) {
/* 168 */     int start = dateStrBuf.length();
/* 169 */     super.format(date, dateStrBuf, fieldPosition);
/* 170 */     int pos = 0;
/*     */ 
/*     */     
/* 173 */     for (pos = start + 25; dateStrBuf.charAt(pos) != 'X'; pos++);
/*     */ 
/*     */ 
/*     */     
/* 177 */     this.calendar.clear();
/* 178 */     this.calendar.setTime(date);
/* 179 */     int offset = this.calendar.get(15) + this.calendar.get(16);
/*     */ 
/*     */     
/* 182 */     if (offset < 0) {
/* 183 */       dateStrBuf.setCharAt(pos++, '-');
/* 184 */       offset = -offset;
/*     */     } else {
/* 186 */       dateStrBuf.setCharAt(pos++, '+');
/*     */     } 
/* 188 */     int rawOffsetInMins = offset / 60 / 1000;
/* 189 */     int offsetInHrs = rawOffsetInMins / 60;
/* 190 */     int offsetInMins = rawOffsetInMins % 60;
/*     */     
/* 192 */     dateStrBuf.setCharAt(pos++, Character.forDigit(offsetInHrs / 10, 10));
/* 193 */     dateStrBuf.setCharAt(pos++, Character.forDigit(offsetInHrs % 10, 10));
/* 194 */     dateStrBuf.setCharAt(pos++, Character.forDigit(offsetInMins / 10, 10));
/* 195 */     dateStrBuf.setCharAt(pos++, Character.forDigit(offsetInMins % 10, 10));
/*     */ 
/*     */     
/* 198 */     return dateStrBuf;
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
/*     */   public Date parse(String text, ParsePosition pos) {
/* 213 */     return parseDate(text.toCharArray(), pos, isLenient());
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static boolean debug = false;
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
/* 258 */   private static MailLogger logger = new MailLogger(MailDateFormat.class, "DEBUG", debug, System.out);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Date parseDate(char[] orig, ParsePosition pos, boolean lenient) {
/*     */     try {
/* 270 */       int day = -1;
/* 271 */       int month = -1;
/* 272 */       int year = -1;
/* 273 */       int hours = 0;
/* 274 */       int minutes = 0;
/* 275 */       int seconds = 0;
/* 276 */       int offset = 0;
/*     */       
/* 278 */       MailDateParser p = new MailDateParser(orig, pos.getIndex());
/*     */ 
/*     */       
/* 281 */       p.skipUntilNumber();
/* 282 */       day = p.parseNumber();
/*     */       
/* 284 */       if (!p.skipIfChar('-')) {
/* 285 */         p.skipWhiteSpace();
/*     */       }
/*     */ 
/*     */       
/* 289 */       month = p.parseMonth();
/* 290 */       if (!p.skipIfChar('-')) {
/* 291 */         p.skipWhiteSpace();
/*     */       }
/*     */ 
/*     */       
/* 295 */       year = p.parseNumber();
/* 296 */       if (year < 50) {
/* 297 */         year += 2000;
/* 298 */       } else if (year < 100) {
/* 299 */         year += 1900;
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 305 */       p.skipWhiteSpace();
/* 306 */       hours = p.parseNumber();
/*     */ 
/*     */       
/* 309 */       p.skipChar(':');
/* 310 */       minutes = p.parseNumber();
/*     */ 
/*     */       
/* 313 */       if (p.skipIfChar(':')) {
/* 314 */         seconds = p.parseNumber();
/*     */       }
/*     */ 
/*     */ 
/*     */       
/*     */       try {
/* 320 */         p.skipWhiteSpace();
/* 321 */         offset = p.parseTimeZone();
/* 322 */       } catch (ParseException pe) {
/* 323 */         if (logger.isLoggable(Level.FINE)) {
/* 324 */           logger.log(Level.FINE, "No timezone? : '" + new String(orig) + "'", pe);
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/* 329 */       pos.setIndex(p.getIndex());
/* 330 */       Date d = ourUTC(year, month, day, hours, minutes, seconds, offset, lenient);
/*     */       
/* 332 */       return d;
/*     */     }
/* 334 */     catch (Exception e) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 340 */       if (logger.isLoggable(Level.FINE)) {
/* 341 */         logger.log(Level.FINE, "Bad date: '" + new String(orig) + "'", e);
/*     */       }
/*     */       
/* 344 */       pos.setIndex(1);
/* 345 */       return null;
/*     */     } 
/*     */   }
/*     */   
/* 349 */   private static final Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static synchronized Date ourUTC(int year, int mon, int mday, int hour, int min, int sec, int tzoffset, boolean lenient) {
/* 355 */     cal.clear();
/* 356 */     cal.setLenient(lenient);
/* 357 */     cal.set(1, year);
/* 358 */     cal.set(2, mon);
/* 359 */     cal.set(5, mday);
/* 360 */     cal.set(11, hour);
/* 361 */     cal.set(12, min);
/* 362 */     cal.add(12, tzoffset);
/* 363 */     cal.set(13, sec);
/*     */     
/* 365 */     return cal.getTime();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCalendar(Calendar newCalendar) {
/* 373 */     throw new RuntimeException("Method setCalendar() shouldn't be called");
/*     */   }
/*     */ 
/*     */   
/*     */   public void setNumberFormat(NumberFormat newNumberFormat) {
/* 378 */     throw new RuntimeException("Method setNumberFormat() shouldn't be called");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\internet\MailDateFormat.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */