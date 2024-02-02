package org.h2.mode;

import java.util.Iterator;
import java.util.List;
import org.h2.engine.SessionLocal;
import org.h2.util.DateTimeUtils;
import org.h2.util.TimeZoneProvider;
import org.h2.value.ValueTimestamp;
import org.h2.value.ValueTimestampTimeZone;

public final class ToDateParser {
   private final SessionLocal session;
   private final String unmodifiedInputStr;
   private final String unmodifiedFormatStr;
   private final ConfigParam functionName;
   private String inputStr;
   private String formatStr;
   private boolean doyValid = false;
   private boolean absoluteDayValid = false;
   private boolean hour12Valid = false;
   private boolean timeZoneHMValid = false;
   private boolean bc;
   private long absoluteDay;
   private int year;
   private int month;
   private int day = 1;
   private int dayOfYear;
   private int hour;
   private int minute;
   private int second;
   private int nanos;
   private int hour12;
   private boolean isAM = true;
   private TimeZoneProvider timeZone;
   private int timeZoneHour;
   private int timeZoneMinute;
   private int currentYear;
   private int currentMonth;

   private ToDateParser(SessionLocal var1, ConfigParam var2, String var3, String var4) {
      this.session = var1;
      this.functionName = var2;
      this.inputStr = var3.trim();
      this.unmodifiedInputStr = this.inputStr;
      if (var4 != null && !var4.isEmpty()) {
         this.formatStr = var4.trim();
      } else {
         this.formatStr = var2.getDefaultFormatStr();
      }

      this.unmodifiedFormatStr = this.formatStr;
   }

   private static ToDateParser getTimestampParser(SessionLocal var0, ConfigParam var1, String var2, String var3) {
      ToDateParser var4 = new ToDateParser(var0, var1, var2, var3);
      parse(var4);
      return var4;
   }

   private ValueTimestamp getResultingValue() {
      long var1;
      int var3;
      if (this.absoluteDayValid) {
         var1 = DateTimeUtils.dateValueFromAbsoluteDay(this.absoluteDay);
      } else {
         var3 = this.year;
         if (var3 == 0) {
            var3 = this.getCurrentYear();
         }

         if (this.bc) {
            var3 = 1 - var3;
         }

         if (this.doyValid) {
            var1 = DateTimeUtils.dateValueFromAbsoluteDay(DateTimeUtils.absoluteDayFromYear((long)var3) + (long)this.dayOfYear - 1L);
         } else {
            int var4 = this.month;
            if (var4 == 0) {
               var4 = this.getCurrentMonth();
            }

            var1 = DateTimeUtils.dateValue((long)var3, var4, this.day);
         }
      }

      if (this.hour12Valid) {
         var3 = this.hour12 % 12;
         if (!this.isAM) {
            var3 += 12;
         }
      } else {
         var3 = this.hour;
      }

      long var6 = (long)((var3 * 60 + this.minute) * 60 + this.second) * 1000000000L + (long)this.nanos;
      return ValueTimestamp.fromDateValueAndNanos(var1, var6);
   }

   private ValueTimestampTimeZone getResultingValueWithTimeZone() {
      ValueTimestamp var1 = this.getResultingValue();
      long var2 = var1.getDateValue();
      long var4 = var1.getTimeNanos();
      int var6;
      if (this.timeZoneHMValid) {
         var6 = (this.timeZoneHour * 60 + (this.timeZoneHour >= 0 ? this.timeZoneMinute : -this.timeZoneMinute)) * 60;
      } else {
         var6 = (this.timeZone != null ? this.timeZone : this.session.currentTimeZone()).getTimeZoneOffsetLocal(var2, var4);
      }

      return ValueTimestampTimeZone.fromDateValueAndNanos(var2, var1.getTimeNanos(), var6);
   }

   String getInputStr() {
      return this.inputStr;
   }

   String getFormatStr() {
      return this.formatStr;
   }

   String getFunctionName() {
      return this.functionName.name();
   }

   private void queryCurrentYearAndMonth() {
      long var1 = this.session.currentTimestamp().getDateValue();
      this.currentYear = DateTimeUtils.yearFromDateValue(var1);
      this.currentMonth = DateTimeUtils.monthFromDateValue(var1);
   }

   int getCurrentYear() {
      if (this.currentYear == 0) {
         this.queryCurrentYearAndMonth();
      }

      return this.currentYear;
   }

   int getCurrentMonth() {
      if (this.currentMonth == 0) {
         this.queryCurrentYearAndMonth();
      }

      return this.currentMonth;
   }

   void setAbsoluteDay(int var1) {
      this.doyValid = false;
      this.absoluteDayValid = true;
      this.absoluteDay = (long)var1;
   }

   void setBC(boolean var1) {
      this.doyValid = false;
      this.absoluteDayValid = false;
      this.bc = var1;
   }

   void setYear(int var1) {
      this.doyValid = false;
      this.absoluteDayValid = false;
      this.year = var1;
   }

   void setMonth(int var1) {
      this.doyValid = false;
      this.absoluteDayValid = false;
      this.month = var1;
      if (this.year == 0) {
         this.year = 1970;
      }

   }

   void setDay(int var1) {
      this.doyValid = false;
      this.absoluteDayValid = false;
      this.day = var1;
      if (this.year == 0) {
         this.year = 1970;
      }

   }

   void setDayOfYear(int var1) {
      this.doyValid = true;
      this.absoluteDayValid = false;
      this.dayOfYear = var1;
   }

   void setHour(int var1) {
      this.hour12Valid = false;
      this.hour = var1;
   }

   void setMinute(int var1) {
      this.minute = var1;
   }

   void setSecond(int var1) {
      this.second = var1;
   }

   void setNanos(int var1) {
      this.nanos = var1;
   }

   void setAmPm(boolean var1) {
      this.hour12Valid = true;
      this.isAM = var1;
   }

   void setHour12(int var1) {
      this.hour12Valid = true;
      this.hour12 = var1;
   }

   void setTimeZone(TimeZoneProvider var1) {
      this.timeZoneHMValid = false;
      this.timeZone = var1;
   }

   void setTimeZoneHour(int var1) {
      this.timeZoneHMValid = true;
      this.timeZoneHour = var1;
   }

   void setTimeZoneMinute(int var1) {
      this.timeZoneHMValid = true;
      this.timeZoneMinute = var1;
   }

   private boolean hasToParseData() {
      return !this.formatStr.isEmpty();
   }

   private void removeFirstChar() {
      if (!this.formatStr.isEmpty()) {
         this.formatStr = this.formatStr.substring(1);
      }

      if (!this.inputStr.isEmpty()) {
         this.inputStr = this.inputStr.substring(1);
      }

   }

   private static ToDateParser parse(ToDateParser var0) {
      while(var0.hasToParseData()) {
         List var1 = ToDateTokenizer.FormatTokenEnum.getTokensInQuestion(var0.getFormatStr());
         if (var1 == null) {
            var0.removeFirstChar();
         } else {
            boolean var2 = false;
            Iterator var3 = var1.iterator();

            while(var3.hasNext()) {
               ToDateTokenizer.FormatTokenEnum var4 = (ToDateTokenizer.FormatTokenEnum)var3.next();
               if (var4.parseFormatStrWithToken(var0)) {
                  var2 = true;
                  break;
               }
            }

            if (!var2) {
               var0.removeFirstChar();
            }
         }
      }

      return var0;
   }

   void remove(String var1, String var2) {
      if (var1 != null && this.inputStr.length() >= var1.length()) {
         this.inputStr = this.inputStr.substring(var1.length());
      }

      if (var2 != null && this.formatStr.length() >= var2.length()) {
         this.formatStr = this.formatStr.substring(var2.length());
      }

   }

   public String toString() {
      int var1 = this.inputStr.length();
      int var2 = this.unmodifiedInputStr.length();
      int var3 = var2 - var1;
      int var4 = var1 <= 0 ? var1 : var1 - 1;
      int var5 = this.unmodifiedFormatStr.length();
      int var6 = var5 - this.formatStr.length();
      return String.format("\n    %s('%s', '%s')", this.functionName, this.unmodifiedInputStr, this.unmodifiedFormatStr) + String.format("\n      %s^%s ,  %s^ <-- Parsing failed at this point", String.format("%" + (this.functionName.name().length() + var3) + "s", ""), var4 <= 0 ? "" : String.format("%" + var4 + "s", ""), var6 <= 0 ? "" : String.format("%" + var6 + "s", ""));
   }

   public static ValueTimestamp toTimestamp(SessionLocal var0, String var1, String var2) {
      ToDateParser var3 = getTimestampParser(var0, ToDateParser.ConfigParam.TO_TIMESTAMP, var1, var2);
      return var3.getResultingValue();
   }

   public static ValueTimestampTimeZone toTimestampTz(SessionLocal var0, String var1, String var2) {
      ToDateParser var3 = getTimestampParser(var0, ToDateParser.ConfigParam.TO_TIMESTAMP_TZ, var1, var2);
      return var3.getResultingValueWithTimeZone();
   }

   public static ValueTimestamp toDate(SessionLocal var0, String var1, String var2) {
      ToDateParser var3 = getTimestampParser(var0, ToDateParser.ConfigParam.TO_DATE, var1, var2);
      return var3.getResultingValue();
   }

   private static enum ConfigParam {
      TO_DATE("DD MON YYYY"),
      TO_TIMESTAMP("DD MON YYYY HH:MI:SS"),
      TO_TIMESTAMP_TZ("DD MON YYYY HH:MI:SS TZR");

      private final String defaultFormatStr;

      private ConfigParam(String var3) {
         this.defaultFormatStr = var3;
      }

      String getDefaultFormatStr() {
         return this.defaultFormatStr;
      }
   }
}
