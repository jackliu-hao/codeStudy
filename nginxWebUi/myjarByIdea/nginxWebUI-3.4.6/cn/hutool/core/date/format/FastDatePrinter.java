package cn.hutool.core.date.format;

import cn.hutool.core.date.DateException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FastDatePrinter extends AbstractDateBasic implements DatePrinter {
   private static final long serialVersionUID = -6305750172255764887L;
   private transient Rule[] rules;
   private transient int mMaxLengthEstimate;
   private static final int MAX_DIGITS = 10;
   private static final ConcurrentMap<TimeZoneDisplayKey, String> C_TIME_ZONE_DISPLAY_CACHE = new ConcurrentHashMap(7);

   public FastDatePrinter(String pattern, TimeZone timeZone, Locale locale) {
      super(pattern, timeZone, locale);
      this.init();
   }

   private void init() {
      List<Rule> rulesList = this.parsePattern();
      this.rules = (Rule[])rulesList.toArray(new Rule[0]);
      int len = 0;
      int i = this.rules.length;

      while(true) {
         --i;
         if (i < 0) {
            this.mMaxLengthEstimate = len;
            return;
         }

         len += this.rules[i].estimateLength();
      }
   }

   protected List<Rule> parsePattern() {
      DateFormatSymbols symbols = new DateFormatSymbols(this.locale);
      List<Rule> rules = new ArrayList();
      String[] ERAs = symbols.getEras();
      String[] months = symbols.getMonths();
      String[] shortMonths = symbols.getShortMonths();
      String[] weekdays = symbols.getWeekdays();
      String[] shortWeekdays = symbols.getShortWeekdays();
      String[] AmPmStrings = symbols.getAmPmStrings();
      int length = this.pattern.length();
      int[] indexRef = new int[1];

      for(int i = 0; i < length; ++i) {
         indexRef[0] = i;
         String token = this.parseToken(this.pattern, indexRef);
         i = indexRef[0];
         int tokenLen = token.length();
         if (tokenLen == 0) {
            break;
         }

         char c = token.charAt(0);
         Object rule;
         switch (c) {
            case '\'':
               String sub = token.substring(1);
               if (sub.length() == 1) {
                  rule = new CharacterLiteral(sub.charAt(0));
               } else {
                  rule = new StringLiteral(sub);
               }
               break;
            case '(':
            case ')':
            case '*':
            case '+':
            case ',':
            case '-':
            case '.':
            case '/':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case ':':
            case ';':
            case '<':
            case '=':
            case '>':
            case '?':
            case '@':
            case 'A':
            case 'B':
            case 'C':
            case 'I':
            case 'J':
            case 'L':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'T':
            case 'U':
            case 'V':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            case 'b':
            case 'c':
            case 'e':
            case 'f':
            case 'g':
            case 'i':
            case 'j':
            case 'l':
            case 'n':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 't':
            case 'v':
            case 'x':
            default:
               throw new IllegalArgumentException("Illegal pattern component: " + token);
            case 'D':
               rule = this.selectNumberRule(6, tokenLen);
               break;
            case 'E':
               rule = new TextField(7, tokenLen < 4 ? shortWeekdays : weekdays);
               break;
            case 'F':
               rule = this.selectNumberRule(8, tokenLen);
               break;
            case 'G':
               rule = new TextField(0, ERAs);
               break;
            case 'H':
               rule = this.selectNumberRule(11, tokenLen);
               break;
            case 'K':
               rule = this.selectNumberRule(10, tokenLen);
               break;
            case 'M':
               if (tokenLen >= 4) {
                  rule = new TextField(2, months);
               } else if (tokenLen == 3) {
                  rule = new TextField(2, shortMonths);
               } else if (tokenLen == 2) {
                  rule = FastDatePrinter.TwoDigitMonthField.INSTANCE;
               } else {
                  rule = FastDatePrinter.UnpaddedMonthField.INSTANCE;
               }
               break;
            case 'S':
               rule = this.selectNumberRule(14, tokenLen);
               break;
            case 'W':
               rule = this.selectNumberRule(4, tokenLen);
               break;
            case 'X':
               rule = FastDatePrinter.Iso8601_Rule.getRule(tokenLen);
               break;
            case 'Y':
            case 'y':
               if (tokenLen == 2) {
                  rule = FastDatePrinter.TwoDigitYearField.INSTANCE;
               } else {
                  rule = this.selectNumberRule(1, Math.max(tokenLen, 4));
               }

               if (c == 'Y') {
                  rule = new WeekYear((NumberRule)rule);
               }
               break;
            case 'Z':
               if (tokenLen == 1) {
                  rule = FastDatePrinter.TimeZoneNumberRule.INSTANCE_NO_COLON;
               } else if (tokenLen == 2) {
                  rule = FastDatePrinter.Iso8601_Rule.ISO8601_HOURS_COLON_MINUTES;
               } else {
                  rule = FastDatePrinter.TimeZoneNumberRule.INSTANCE_COLON;
               }
               break;
            case 'a':
               rule = new TextField(9, AmPmStrings);
               break;
            case 'd':
               rule = this.selectNumberRule(5, tokenLen);
               break;
            case 'h':
               rule = new TwelveHourField(this.selectNumberRule(10, tokenLen));
               break;
            case 'k':
               rule = new TwentyFourHourField(this.selectNumberRule(11, tokenLen));
               break;
            case 'm':
               rule = this.selectNumberRule(12, tokenLen);
               break;
            case 's':
               rule = this.selectNumberRule(13, tokenLen);
               break;
            case 'u':
               rule = new DayInWeekField(this.selectNumberRule(7, tokenLen));
               break;
            case 'w':
               rule = this.selectNumberRule(3, tokenLen);
               break;
            case 'z':
               if (tokenLen >= 4) {
                  rule = new TimeZoneNameRule(this.timeZone, this.locale, 1);
               } else {
                  rule = new TimeZoneNameRule(this.timeZone, this.locale, 0);
               }
         }

         rules.add(rule);
      }

      return rules;
   }

   protected String parseToken(String pattern, int[] indexRef) {
      StringBuilder buf = new StringBuilder();
      int i = indexRef[0];
      int length = pattern.length();
      char c = pattern.charAt(i);
      if (c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z') {
         buf.append(c);

         while(i + 1 < length) {
            char peek = pattern.charAt(i + 1);
            if (peek != c) {
               break;
            }

            buf.append(c);
            ++i;
         }
      } else {
         buf.append('\'');

         for(boolean inLiteral = false; i < length; ++i) {
            c = pattern.charAt(i);
            if (c == '\'') {
               if (i + 1 < length && pattern.charAt(i + 1) == '\'') {
                  ++i;
                  buf.append(c);
               } else {
                  inLiteral = !inLiteral;
               }
            } else {
               if (!inLiteral && (c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z')) {
                  --i;
                  break;
               }

               buf.append(c);
            }
         }
      }

      indexRef[0] = i;
      return buf.toString();
   }

   protected NumberRule selectNumberRule(int field, int padding) {
      switch (padding) {
         case 1:
            return new UnpaddedNumberField(field);
         case 2:
            return new TwoDigitNumberField(field);
         default:
            return new PaddedNumberField(field, padding);
      }
   }

   String format(Object obj) {
      if (obj instanceof Date) {
         return this.format((Date)obj);
      } else if (obj instanceof Calendar) {
         return this.format((Calendar)obj);
      } else if (obj instanceof Long) {
         return this.format((Long)obj);
      } else {
         throw new IllegalArgumentException("Unknown class: " + (obj == null ? "<null>" : obj.getClass().getName()));
      }
   }

   public String format(long millis) {
      Calendar c = Calendar.getInstance(this.timeZone, this.locale);
      c.setTimeInMillis(millis);
      return this.applyRulesToString(c);
   }

   public String format(Date date) {
      Calendar c = Calendar.getInstance(this.timeZone, this.locale);
      c.setTime(date);
      return this.applyRulesToString(c);
   }

   public String format(Calendar calendar) {
      return ((StringBuilder)this.format((Calendar)calendar, new StringBuilder(this.mMaxLengthEstimate))).toString();
   }

   public <B extends Appendable> B format(long millis, B buf) {
      Calendar c = Calendar.getInstance(this.timeZone, this.locale);
      c.setTimeInMillis(millis);
      return this.applyRules(c, buf);
   }

   public <B extends Appendable> B format(Date date, B buf) {
      Calendar c = Calendar.getInstance(this.timeZone, this.locale);
      c.setTime(date);
      return this.applyRules(c, buf);
   }

   public <B extends Appendable> B format(Calendar calendar, B buf) {
      if (!calendar.getTimeZone().equals(this.timeZone)) {
         calendar = (Calendar)calendar.clone();
         calendar.setTimeZone(this.timeZone);
      }

      return this.applyRules(calendar, buf);
   }

   private String applyRulesToString(Calendar c) {
      return ((StringBuilder)this.applyRules(c, new StringBuilder(this.mMaxLengthEstimate))).toString();
   }

   private <B extends Appendable> B applyRules(Calendar calendar, B buf) {
      try {
         Rule[] var3 = this.rules;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Rule rule = var3[var5];
            rule.appendTo(buf, calendar);
         }

         return buf;
      } catch (IOException var7) {
         throw new DateException(var7);
      }
   }

   public int getMaxLengthEstimate() {
      return this.mMaxLengthEstimate;
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      this.init();
   }

   private static void appendDigits(Appendable buffer, int value) throws IOException {
      buffer.append((char)(value / 10 + 48));
      buffer.append((char)(value % 10 + 48));
   }

   private static void appendFullDigits(Appendable buffer, int value, int minFieldWidth) throws IOException {
      int digit;
      if (value < 10000) {
         int nDigits = 4;
         if (value < 1000) {
            --nDigits;
            if (value < 100) {
               --nDigits;
               if (value < 10) {
                  --nDigits;
               }
            }
         }

         for(digit = minFieldWidth - nDigits; digit > 0; --digit) {
            buffer.append('0');
         }

         switch (nDigits) {
            case 4:
               buffer.append((char)(value / 1000 + 48));
               value %= 1000;
            case 3:
               if (value >= 100) {
                  buffer.append((char)(value / 100 + 48));
                  value %= 100;
               } else {
                  buffer.append('0');
               }
            case 2:
               if (value >= 10) {
                  buffer.append((char)(value / 10 + 48));
                  value %= 10;
               } else {
                  buffer.append('0');
               }
            case 1:
               buffer.append((char)(value + 48));
         }
      } else {
         char[] work = new char[10];

         for(digit = 0; value != 0; value /= 10) {
            work[digit++] = (char)(value % 10 + 48);
         }

         while(digit < minFieldWidth) {
            buffer.append('0');
            --minFieldWidth;
         }

         while(true) {
            --digit;
            if (digit < 0) {
               break;
            }

            buffer.append(work[digit]);
         }
      }

   }

   static String getTimeZoneDisplay(TimeZone tz, boolean daylight, int style, Locale locale) {
      TimeZoneDisplayKey key = new TimeZoneDisplayKey(tz, daylight, style, locale);
      String value = (String)C_TIME_ZONE_DISPLAY_CACHE.get(key);
      if (value == null) {
         value = tz.getDisplayName(daylight, style, locale);
         String prior = (String)C_TIME_ZONE_DISPLAY_CACHE.putIfAbsent(key, value);
         if (prior != null) {
            value = prior;
         }
      }

      return value;
   }

   private static class TimeZoneDisplayKey {
      private final TimeZone mTimeZone;
      private final int mStyle;
      private final Locale mLocale;

      TimeZoneDisplayKey(TimeZone timeZone, boolean daylight, int style, Locale locale) {
         this.mTimeZone = timeZone;
         if (daylight) {
            this.mStyle = style | Integer.MIN_VALUE;
         } else {
            this.mStyle = style;
         }

         this.mLocale = locale;
      }

      public int hashCode() {
         return (this.mStyle * 31 + this.mLocale.hashCode()) * 31 + this.mTimeZone.hashCode();
      }

      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (!(obj instanceof TimeZoneDisplayKey)) {
            return false;
         } else {
            TimeZoneDisplayKey other = (TimeZoneDisplayKey)obj;
            return this.mTimeZone.equals(other.mTimeZone) && this.mStyle == other.mStyle && this.mLocale.equals(other.mLocale);
         }
      }
   }

   private static class Iso8601_Rule implements Rule {
      static final Iso8601_Rule ISO8601_HOURS = new Iso8601_Rule(3);
      static final Iso8601_Rule ISO8601_HOURS_MINUTES = new Iso8601_Rule(5);
      static final Iso8601_Rule ISO8601_HOURS_COLON_MINUTES = new Iso8601_Rule(6);
      final int length;

      static Iso8601_Rule getRule(int tokenLen) {
         switch (tokenLen) {
            case 1:
               return ISO8601_HOURS;
            case 2:
               return ISO8601_HOURS_MINUTES;
            case 3:
               return ISO8601_HOURS_COLON_MINUTES;
            default:
               throw new IllegalArgumentException("invalid number of X");
         }
      }

      Iso8601_Rule(int length) {
         this.length = length;
      }

      public int estimateLength() {
         return this.length;
      }

      public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
         int offset = calendar.get(15) + calendar.get(16);
         if (offset == 0) {
            buffer.append("Z");
         } else {
            if (offset < 0) {
               buffer.append('-');
               offset = -offset;
            } else {
               buffer.append('+');
            }

            int hours = offset / 3600000;
            FastDatePrinter.appendDigits(buffer, hours);
            if (this.length >= 5) {
               if (this.length == 6) {
                  buffer.append(':');
               }

               int minutes = offset / '\uea60' - 60 * hours;
               FastDatePrinter.appendDigits(buffer, minutes);
            }
         }
      }
   }

   private static class TimeZoneNumberRule implements Rule {
      static final TimeZoneNumberRule INSTANCE_COLON = new TimeZoneNumberRule(true);
      static final TimeZoneNumberRule INSTANCE_NO_COLON = new TimeZoneNumberRule(false);
      final boolean mColon;

      TimeZoneNumberRule(boolean colon) {
         this.mColon = colon;
      }

      public int estimateLength() {
         return 5;
      }

      public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
         int offset = calendar.get(15) + calendar.get(16);
         if (offset < 0) {
            buffer.append('-');
            offset = -offset;
         } else {
            buffer.append('+');
         }

         int hours = offset / 3600000;
         FastDatePrinter.appendDigits(buffer, hours);
         if (this.mColon) {
            buffer.append(':');
         }

         int minutes = offset / '\uea60' - 60 * hours;
         FastDatePrinter.appendDigits(buffer, minutes);
      }
   }

   private static class TimeZoneNameRule implements Rule {
      private final Locale mLocale;
      private final int mStyle;
      private final String mStandard;
      private final String mDaylight;

      TimeZoneNameRule(TimeZone timeZone, Locale locale, int style) {
         this.mLocale = locale;
         this.mStyle = style;
         this.mStandard = FastDatePrinter.getTimeZoneDisplay(timeZone, false, style, locale);
         this.mDaylight = FastDatePrinter.getTimeZoneDisplay(timeZone, true, style, locale);
      }

      public int estimateLength() {
         return Math.max(this.mStandard.length(), this.mDaylight.length());
      }

      public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
         TimeZone zone = calendar.getTimeZone();
         if (calendar.get(16) != 0) {
            buffer.append(FastDatePrinter.getTimeZoneDisplay(zone, true, this.mStyle, this.mLocale));
         } else {
            buffer.append(FastDatePrinter.getTimeZoneDisplay(zone, false, this.mStyle, this.mLocale));
         }

      }
   }

   private static class WeekYear implements NumberRule {
      private final NumberRule mRule;

      WeekYear(NumberRule rule) {
         this.mRule = rule;
      }

      public int estimateLength() {
         return this.mRule.estimateLength();
      }

      public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
         this.mRule.appendTo(buffer, calendar.getWeekYear());
      }

      public void appendTo(Appendable buffer, int value) throws IOException {
         this.mRule.appendTo(buffer, value);
      }
   }

   private static class DayInWeekField implements NumberRule {
      private final NumberRule mRule;

      DayInWeekField(NumberRule rule) {
         this.mRule = rule;
      }

      public int estimateLength() {
         return this.mRule.estimateLength();
      }

      public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
         int value = calendar.get(7);
         this.mRule.appendTo(buffer, value != 1 ? value - 1 : 7);
      }

      public void appendTo(Appendable buffer, int value) throws IOException {
         this.mRule.appendTo(buffer, value);
      }
   }

   private static class TwentyFourHourField implements NumberRule {
      private final NumberRule mRule;

      TwentyFourHourField(NumberRule rule) {
         this.mRule = rule;
      }

      public int estimateLength() {
         return this.mRule.estimateLength();
      }

      public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
         int value = calendar.get(11);
         if (value == 0) {
            value = calendar.getMaximum(11) + 1;
         }

         this.mRule.appendTo(buffer, value);
      }

      public void appendTo(Appendable buffer, int value) throws IOException {
         this.mRule.appendTo(buffer, value);
      }
   }

   private static class TwelveHourField implements NumberRule {
      private final NumberRule mRule;

      TwelveHourField(NumberRule rule) {
         this.mRule = rule;
      }

      public int estimateLength() {
         return this.mRule.estimateLength();
      }

      public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
         int value = calendar.get(10);
         if (value == 0) {
            value = calendar.getLeastMaximum(10) + 1;
         }

         this.mRule.appendTo(buffer, value);
      }

      public void appendTo(Appendable buffer, int value) throws IOException {
         this.mRule.appendTo(buffer, value);
      }
   }

   private static class TwoDigitMonthField implements NumberRule {
      static final TwoDigitMonthField INSTANCE = new TwoDigitMonthField();

      TwoDigitMonthField() {
      }

      public int estimateLength() {
         return 2;
      }

      public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
         this.appendTo(buffer, calendar.get(2) + 1);
      }

      public final void appendTo(Appendable buffer, int value) throws IOException {
         FastDatePrinter.appendDigits(buffer, value);
      }
   }

   private static class TwoDigitYearField implements NumberRule {
      static final TwoDigitYearField INSTANCE = new TwoDigitYearField();

      TwoDigitYearField() {
      }

      public int estimateLength() {
         return 2;
      }

      public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
         this.appendTo(buffer, calendar.get(1) % 100);
      }

      public final void appendTo(Appendable buffer, int value) throws IOException {
         FastDatePrinter.appendDigits(buffer, value);
      }
   }

   private static class TwoDigitNumberField implements NumberRule {
      private final int mField;

      TwoDigitNumberField(int field) {
         this.mField = field;
      }

      public int estimateLength() {
         return 2;
      }

      public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
         this.appendTo(buffer, calendar.get(this.mField));
      }

      public final void appendTo(Appendable buffer, int value) throws IOException {
         if (value < 100) {
            FastDatePrinter.appendDigits(buffer, value);
         } else {
            FastDatePrinter.appendFullDigits(buffer, value, 2);
         }

      }
   }

   private static class PaddedNumberField implements NumberRule {
      private final int mField;
      private final int mSize;

      PaddedNumberField(int field, int size) {
         if (size < 3) {
            throw new IllegalArgumentException();
         } else {
            this.mField = field;
            this.mSize = size;
         }
      }

      public int estimateLength() {
         return this.mSize;
      }

      public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
         this.appendTo(buffer, calendar.get(this.mField));
      }

      public final void appendTo(Appendable buffer, int value) throws IOException {
         FastDatePrinter.appendFullDigits(buffer, value, this.mSize);
      }
   }

   private static class UnpaddedMonthField implements NumberRule {
      static final UnpaddedMonthField INSTANCE = new UnpaddedMonthField();

      UnpaddedMonthField() {
      }

      public int estimateLength() {
         return 2;
      }

      public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
         this.appendTo(buffer, calendar.get(2) + 1);
      }

      public final void appendTo(Appendable buffer, int value) throws IOException {
         if (value < 10) {
            buffer.append((char)(value + 48));
         } else {
            FastDatePrinter.appendDigits(buffer, value);
         }

      }
   }

   private static class UnpaddedNumberField implements NumberRule {
      private final int mField;

      UnpaddedNumberField(int field) {
         this.mField = field;
      }

      public int estimateLength() {
         return 4;
      }

      public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
         this.appendTo(buffer, calendar.get(this.mField));
      }

      public final void appendTo(Appendable buffer, int value) throws IOException {
         if (value < 10) {
            buffer.append((char)(value + 48));
         } else if (value < 100) {
            FastDatePrinter.appendDigits(buffer, value);
         } else {
            FastDatePrinter.appendFullDigits(buffer, value, 1);
         }

      }
   }

   private static class TextField implements Rule {
      private final int mField;
      private final String[] mValues;

      TextField(int field, String[] values) {
         this.mField = field;
         this.mValues = values;
      }

      public int estimateLength() {
         int max = 0;
         int i = this.mValues.length;

         while(true) {
            --i;
            if (i < 0) {
               return max;
            }

            int len = this.mValues[i].length();
            if (len > max) {
               max = len;
            }
         }
      }

      public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
         buffer.append(this.mValues[calendar.get(this.mField)]);
      }
   }

   private static class StringLiteral implements Rule {
      private final String mValue;

      StringLiteral(String value) {
         this.mValue = value;
      }

      public int estimateLength() {
         return this.mValue.length();
      }

      public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
         buffer.append(this.mValue);
      }
   }

   private static class CharacterLiteral implements Rule {
      private final char mValue;

      CharacterLiteral(char value) {
         this.mValue = value;
      }

      public int estimateLength() {
         return 1;
      }

      public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
         buffer.append(this.mValue);
      }
   }

   private interface NumberRule extends Rule {
      void appendTo(Appendable var1, int var2) throws IOException;
   }

   private interface Rule {
      int estimateLength();

      void appendTo(Appendable var1, Calendar var2) throws IOException;
   }
}
