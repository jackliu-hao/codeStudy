package javax.mail.internet;

import com.sun.mail.util.MailLogger;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;

public class MailDateFormat extends SimpleDateFormat {
   private static final long serialVersionUID = -8148227605210628779L;
   static boolean debug = false;
   private static MailLogger logger;
   private static final Calendar cal;

   public MailDateFormat() {
      super("EEE, d MMM yyyy HH:mm:ss 'XXXXX' (z)", Locale.US);
   }

   public StringBuffer format(Date date, StringBuffer dateStrBuf, FieldPosition fieldPosition) {
      int start = dateStrBuf.length();
      super.format(date, dateStrBuf, fieldPosition);
      int pos = false;

      int pos;
      for(pos = start + 25; dateStrBuf.charAt(pos) != 'X'; ++pos) {
      }

      this.calendar.clear();
      this.calendar.setTime(date);
      int offset = this.calendar.get(15) + this.calendar.get(16);
      if (offset < 0) {
         dateStrBuf.setCharAt(pos++, '-');
         offset = -offset;
      } else {
         dateStrBuf.setCharAt(pos++, '+');
      }

      int rawOffsetInMins = offset / 60 / 1000;
      int offsetInHrs = rawOffsetInMins / 60;
      int offsetInMins = rawOffsetInMins % 60;
      dateStrBuf.setCharAt(pos++, Character.forDigit(offsetInHrs / 10, 10));
      dateStrBuf.setCharAt(pos++, Character.forDigit(offsetInHrs % 10, 10));
      dateStrBuf.setCharAt(pos++, Character.forDigit(offsetInMins / 10, 10));
      dateStrBuf.setCharAt(pos++, Character.forDigit(offsetInMins % 10, 10));
      return dateStrBuf;
   }

   public Date parse(String text, ParsePosition pos) {
      return parseDate(text.toCharArray(), pos, this.isLenient());
   }

   private static Date parseDate(char[] orig, ParsePosition pos, boolean lenient) {
      try {
         int day = true;
         int month = true;
         int year = true;
         int hours = false;
         int minutes = false;
         int seconds = 0;
         int offset = 0;
         MailDateParser p = new MailDateParser(orig, pos.getIndex());
         p.skipUntilNumber();
         int day = p.parseNumber();
         if (!p.skipIfChar('-')) {
            p.skipWhiteSpace();
         }

         int month = p.parseMonth();
         if (!p.skipIfChar('-')) {
            p.skipWhiteSpace();
         }

         int year = p.parseNumber();
         if (year < 50) {
            year += 2000;
         } else if (year < 100) {
            year += 1900;
         }

         p.skipWhiteSpace();
         int hours = p.parseNumber();
         p.skipChar(':');
         int minutes = p.parseNumber();
         if (p.skipIfChar(':')) {
            seconds = p.parseNumber();
         }

         try {
            p.skipWhiteSpace();
            offset = p.parseTimeZone();
         } catch (java.text.ParseException var12) {
            if (logger.isLoggable(Level.FINE)) {
               logger.log(Level.FINE, "No timezone? : '" + new String(orig) + "'", (Throwable)var12);
            }
         }

         pos.setIndex(p.getIndex());
         Date d = ourUTC(year, month, day, hours, minutes, seconds, offset, lenient);
         return d;
      } catch (Exception var13) {
         if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Bad date: '" + new String(orig) + "'", (Throwable)var13);
         }

         pos.setIndex(1);
         return null;
      }
   }

   private static synchronized Date ourUTC(int year, int mon, int mday, int hour, int min, int sec, int tzoffset, boolean lenient) {
      cal.clear();
      cal.setLenient(lenient);
      cal.set(1, year);
      cal.set(2, mon);
      cal.set(5, mday);
      cal.set(11, hour);
      cal.set(12, min);
      cal.add(12, tzoffset);
      cal.set(13, sec);
      return cal.getTime();
   }

   public void setCalendar(Calendar newCalendar) {
      throw new RuntimeException("Method setCalendar() shouldn't be called");
   }

   public void setNumberFormat(NumberFormat newNumberFormat) {
      throw new RuntimeException("Method setNumberFormat() shouldn't be called");
   }

   static {
      logger = new MailLogger(MailDateFormat.class, "DEBUG", debug, System.out);
      cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
   }
}
