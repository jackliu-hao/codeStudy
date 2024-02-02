package cn.hutool.core.date;

import cn.hutool.core.comparator.CompareUtil;
import cn.hutool.core.convert.NumberChineseFormatter;
import cn.hutool.core.date.format.DateParser;
import cn.hutool.core.date.format.FastDateParser;
import cn.hutool.core.date.format.GlobalCustomFormat;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import java.text.ParsePosition;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.TimeZone;

public class CalendarUtil {
   public static Calendar calendar() {
      return Calendar.getInstance();
   }

   public static Calendar calendar(Date date) {
      return date instanceof DateTime ? ((DateTime)date).toCalendar() : calendar(date.getTime());
   }

   public static Calendar calendar(long millis) {
      return calendar(millis, TimeZone.getDefault());
   }

   public static Calendar calendar(long millis, TimeZone timeZone) {
      Calendar cal = Calendar.getInstance(timeZone);
      cal.setTimeInMillis(millis);
      return cal;
   }

   public static boolean isAM(Calendar calendar) {
      return 0 == calendar.get(9);
   }

   public static boolean isPM(Calendar calendar) {
      return 1 == calendar.get(9);
   }

   public static Calendar truncate(Calendar calendar, DateField dateField) {
      return DateModifier.modify(calendar, dateField.getValue(), DateModifier.ModifyType.TRUNCATE);
   }

   public static Calendar round(Calendar calendar, DateField dateField) {
      return DateModifier.modify(calendar, dateField.getValue(), DateModifier.ModifyType.ROUND);
   }

   public static Calendar ceiling(Calendar calendar, DateField dateField) {
      return DateModifier.modify(calendar, dateField.getValue(), DateModifier.ModifyType.CEILING);
   }

   public static Calendar ceiling(Calendar calendar, DateField dateField, boolean truncateMillisecond) {
      return DateModifier.modify(calendar, dateField.getValue(), DateModifier.ModifyType.CEILING, truncateMillisecond);
   }

   public static Calendar beginOfSecond(Calendar calendar) {
      return truncate(calendar, DateField.SECOND);
   }

   public static Calendar endOfSecond(Calendar calendar) {
      return ceiling(calendar, DateField.SECOND);
   }

   public static Calendar beginOfHour(Calendar calendar) {
      return truncate(calendar, DateField.HOUR_OF_DAY);
   }

   public static Calendar endOfHour(Calendar calendar) {
      return ceiling(calendar, DateField.HOUR_OF_DAY);
   }

   public static Calendar beginOfMinute(Calendar calendar) {
      return truncate(calendar, DateField.MINUTE);
   }

   public static Calendar endOfMinute(Calendar calendar) {
      return ceiling(calendar, DateField.MINUTE);
   }

   public static Calendar beginOfDay(Calendar calendar) {
      return truncate(calendar, DateField.DAY_OF_MONTH);
   }

   public static Calendar endOfDay(Calendar calendar) {
      return ceiling(calendar, DateField.DAY_OF_MONTH);
   }

   public static Calendar beginOfWeek(Calendar calendar) {
      return beginOfWeek(calendar, true);
   }

   public static Calendar beginOfWeek(Calendar calendar, boolean isMondayAsFirstDay) {
      calendar.setFirstDayOfWeek(isMondayAsFirstDay ? 2 : 1);
      return truncate(calendar, DateField.WEEK_OF_MONTH);
   }

   public static Calendar endOfWeek(Calendar calendar) {
      return endOfWeek(calendar, true);
   }

   public static Calendar endOfWeek(Calendar calendar, boolean isSundayAsLastDay) {
      calendar.setFirstDayOfWeek(isSundayAsLastDay ? 2 : 1);
      return ceiling(calendar, DateField.WEEK_OF_MONTH);
   }

   public static Calendar beginOfMonth(Calendar calendar) {
      return truncate(calendar, DateField.MONTH);
   }

   public static Calendar endOfMonth(Calendar calendar) {
      return ceiling(calendar, DateField.MONTH);
   }

   public static Calendar beginOfQuarter(Calendar calendar) {
      calendar.set(2, calendar.get(DateField.MONTH.getValue()) / 3 * 3);
      calendar.set(5, 1);
      return beginOfDay(calendar);
   }

   public static Calendar endOfQuarter(Calendar calendar) {
      int year = calendar.get(1);
      int month = calendar.get(DateField.MONTH.getValue()) / 3 * 3 + 2;
      Calendar resultCal = Calendar.getInstance(calendar.getTimeZone());
      resultCal.set(year, month, Month.of(month).getLastDay(DateUtil.isLeapYear(year)));
      return endOfDay(resultCal);
   }

   public static Calendar beginOfYear(Calendar calendar) {
      return truncate(calendar, DateField.YEAR);
   }

   public static Calendar endOfYear(Calendar calendar) {
      return ceiling(calendar, DateField.YEAR);
   }

   public static boolean isSameDay(Calendar cal1, Calendar cal2) {
      if (cal1 != null && cal2 != null) {
         return cal1.get(6) == cal2.get(6) && cal1.get(1) == cal2.get(1) && cal1.get(0) == cal2.get(0);
      } else {
         throw new IllegalArgumentException("The date must not be null");
      }
   }

   public static boolean isSameWeek(Calendar cal1, Calendar cal2, boolean isMon) {
      if (cal1 != null && cal2 != null) {
         cal1 = (Calendar)cal1.clone();
         cal2 = (Calendar)cal2.clone();
         if (isMon) {
            cal1.setFirstDayOfWeek(2);
            cal1.set(7, 2);
            cal2.setFirstDayOfWeek(2);
            cal2.set(7, 2);
         } else {
            cal1.setFirstDayOfWeek(1);
            cal1.set(7, 1);
            cal2.setFirstDayOfWeek(1);
            cal2.set(7, 1);
         }

         return isSameDay(cal1, cal2);
      } else {
         throw new IllegalArgumentException("The date must not be null");
      }
   }

   public static boolean isSameMonth(Calendar cal1, Calendar cal2) {
      if (cal1 != null && cal2 != null) {
         return cal1.get(1) == cal2.get(1) && cal1.get(2) == cal2.get(2);
      } else {
         throw new IllegalArgumentException("The date must not be null");
      }
   }

   public static boolean isSameInstant(Calendar date1, Calendar date2) {
      if (null == date1) {
         return null == date2;
      } else if (null == date2) {
         return false;
      } else {
         return date1.getTimeInMillis() == date2.getTimeInMillis();
      }
   }

   public static LinkedHashSet<String> yearAndQuarter(long startDate, long endDate) {
      LinkedHashSet<String> quarters = new LinkedHashSet();

      for(Calendar cal = calendar(startDate); startDate <= endDate; startDate = cal.getTimeInMillis()) {
         quarters.add(yearAndQuarter(cal));
         cal.add(2, 3);
      }

      return quarters;
   }

   public static String yearAndQuarter(Calendar cal) {
      return StrUtil.builder().append(cal.get(1)).append(cal.get(2) / 3 + 1).toString();
   }

   public static int getBeginValue(Calendar calendar, DateField dateField) {
      return getBeginValue(calendar, dateField.getValue());
   }

   public static int getBeginValue(Calendar calendar, int dateField) {
      return 7 == dateField ? calendar.getFirstDayOfWeek() : calendar.getActualMinimum(dateField);
   }

   public static int getEndValue(Calendar calendar, DateField dateField) {
      return getEndValue(calendar, dateField.getValue());
   }

   public static int getEndValue(Calendar calendar, int dateField) {
      return 7 == dateField ? (calendar.getFirstDayOfWeek() + 6) % 7 : calendar.getActualMaximum(dateField);
   }

   public static Instant toInstant(Calendar calendar) {
      return null == calendar ? null : calendar.toInstant();
   }

   public static LocalDateTime toLocalDateTime(Calendar calendar) {
      return LocalDateTime.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId());
   }

   public static int compare(Calendar calendar1, Calendar calendar2) {
      return CompareUtil.compare(calendar1, calendar2);
   }

   public static int age(Calendar birthday, Calendar dateToCompare) {
      return age(birthday.getTimeInMillis(), dateToCompare.getTimeInMillis());
   }

   public static String formatChineseDate(Calendar calendar, boolean withTime) {
      StringBuilder result = StrUtil.builder();
      String year = String.valueOf(calendar.get(1));
      int length = year.length();

      int month;
      for(month = 0; month < length; ++month) {
         result.append(NumberChineseFormatter.numberCharToChinese(year.charAt(month), false));
      }

      result.append('年');
      month = calendar.get(2) + 1;
      result.append(NumberChineseFormatter.formatThousand(month, false));
      result.append('月');
      int day = calendar.get(5);
      result.append(NumberChineseFormatter.formatThousand(day, false));
      result.append('日');
      String temp = result.toString().replace('零', '〇');
      result.delete(0, result.length());
      result.append(temp);
      if (withTime) {
         int hour = calendar.get(11);
         result.append(NumberChineseFormatter.formatThousand(hour, false));
         result.append('时');
         int minute = calendar.get(12);
         result.append(NumberChineseFormatter.formatThousand(minute, false));
         result.append('分');
         int second = calendar.get(13);
         result.append(NumberChineseFormatter.formatThousand(second, false));
         result.append('秒');
      }

      return result.toString();
   }

   protected static int age(long birthday, long dateToCompare) {
      if (birthday > dateToCompare) {
         throw new IllegalArgumentException("Birthday is after dateToCompare!");
      } else {
         Calendar cal = Calendar.getInstance();
         cal.setTimeInMillis(dateToCompare);
         int year = cal.get(1);
         int month = cal.get(2);
         int dayOfMonth = cal.get(5);
         boolean isLastDayOfMonth = dayOfMonth == cal.getActualMaximum(5);
         cal.setTimeInMillis(birthday);
         int age = year - cal.get(1);
         int monthBirth = cal.get(2);
         if (month == monthBirth) {
            int dayOfMonthBirth = cal.get(5);
            boolean isLastDayOfMonthBirth = dayOfMonthBirth == cal.getActualMaximum(5);
            if ((!isLastDayOfMonth || !isLastDayOfMonthBirth) && dayOfMonth < dayOfMonthBirth) {
               --age;
            }
         } else if (month < monthBirth) {
            --age;
         }

         return age;
      }
   }

   public static Calendar parseByPatterns(String str, String... parsePatterns) throws DateException {
      return parseByPatterns(str, (Locale)null, parsePatterns);
   }

   public static Calendar parseByPatterns(String str, Locale locale, String... parsePatterns) throws DateException {
      return parseByPatterns(str, locale, true, parsePatterns);
   }

   public static Calendar parseByPatterns(String str, Locale locale, boolean lenient, String... parsePatterns) throws DateException {
      if (str != null && parsePatterns != null) {
         TimeZone tz = TimeZone.getDefault();
         Locale lcl = (Locale)ObjectUtil.defaultIfNull(locale, (Object)Locale.getDefault());
         ParsePosition pos = new ParsePosition(0);
         Calendar calendar = Calendar.getInstance(tz, lcl);
         calendar.setLenient(lenient);
         String[] var8 = parsePatterns;
         int var9 = parsePatterns.length;

         for(int var10 = 0; var10 < var9; ++var10) {
            String parsePattern = var8[var10];
            if (GlobalCustomFormat.isCustomFormat(parsePattern)) {
               Date parse = GlobalCustomFormat.parse(str, parsePattern);
               if (null != parse) {
                  calendar.setTime(parse);
                  return calendar;
               }
            } else {
               FastDateParser fdp = new FastDateParser(parsePattern, tz, lcl);
               calendar.clear();

               try {
                  if (fdp.parse(str, pos, calendar) && pos.getIndex() == str.length()) {
                     return calendar;
                  }
               } catch (IllegalArgumentException var14) {
               }

               pos.setIndex(0);
            }
         }

         throw new DateException("Unable to parse the date: {}", new Object[]{str});
      } else {
         throw new IllegalArgumentException("Date and Patterns must not be null");
      }
   }

   public static Calendar parse(CharSequence str, boolean lenient, DateParser parser) {
      Calendar calendar = Calendar.getInstance(parser.getTimeZone(), parser.getLocale());
      calendar.clear();
      calendar.setLenient(lenient);
      return parser.parse(StrUtil.str(str), new ParsePosition(0), calendar) ? calendar : null;
   }
}
