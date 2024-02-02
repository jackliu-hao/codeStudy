package cn.hutool.cron.pattern.matcher;

import cn.hutool.cron.pattern.Part;
import java.time.Year;
import java.util.Calendar;
import java.util.TimeZone;

public class PatternMatcher {
   private final PartMatcher[] matchers;

   public PatternMatcher(PartMatcher secondMatcher, PartMatcher minuteMatcher, PartMatcher hourMatcher, PartMatcher dayOfMonthMatcher, PartMatcher monthMatcher, PartMatcher dayOfWeekMatcher, PartMatcher yearMatcher) {
      this.matchers = new PartMatcher[]{secondMatcher, minuteMatcher, hourMatcher, dayOfMonthMatcher, monthMatcher, dayOfWeekMatcher, yearMatcher};
   }

   public PartMatcher get(Part part) {
      return this.matchers[part.ordinal()];
   }

   public boolean match(int[] fields) {
      return this.match(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6]);
   }

   public boolean matchWeek(int dayOfWeekValue) {
      return this.matchers[5].match(dayOfWeekValue);
   }

   private boolean match(int second, int minute, int hour, int dayOfMonth, int month, int dayOfWeek, int year) {
      return (second < 0 || this.matchers[0].match(second)) && this.matchers[1].match(minute) && this.matchers[2].match(hour) && matchDayOfMonth(this.matchers[3], dayOfMonth, month, Year.isLeap((long)year)) && this.matchers[4].match(month) && this.matchers[5].match(dayOfWeek) && this.matchers[6].match(year);
   }

   private static boolean matchDayOfMonth(PartMatcher matcher, int dayOfMonth, int month, boolean isLeapYear) {
      return matcher instanceof DayOfMonthMatcher ? ((DayOfMonthMatcher)matcher).match(dayOfMonth, month, isLeapYear) : matcher.match(dayOfMonth);
   }

   public Calendar nextMatchAfter(int[] values, TimeZone zone) {
      Calendar calendar = Calendar.getInstance(zone);
      calendar.set(14, 0);
      int[] newValues = this.nextMatchValuesAfter(values);

      for(int i = 0; i < newValues.length; ++i) {
         if (i != Part.DAY_OF_WEEK.ordinal()) {
            this.setValue(calendar, Part.of(i), newValues[i]);
         }
      }

      return calendar;
   }

   private int[] nextMatchValuesAfter(int[] values) {
      int[] newValues = (int[])values.clone();
      int i = Part.YEAR.ordinal();
      int nextValue = 0;

      while(i >= 0) {
         if (i == Part.DAY_OF_WEEK.ordinal()) {
            --i;
         } else {
            nextValue = this.matchers[i].nextAfter(values[i]);
            if (nextValue > values[i]) {
               newValues[i] = nextValue;
               --i;
               break;
            }

            if (nextValue < values[i]) {
               ++i;
               nextValue = -1;
               break;
            }

            --i;
         }
      }

      if (-1 == nextValue) {
         while(i <= Part.YEAR.ordinal()) {
            if (i == Part.DAY_OF_WEEK.ordinal()) {
               ++i;
            } else {
               nextValue = this.matchers[i].nextAfter(values[i] + 1);
               if (nextValue > values[i]) {
                  newValues[i] = nextValue;
                  --i;
                  break;
               }

               ++i;
            }
         }
      }

      this.setToMin(newValues, i);
      return newValues;
   }

   private void setToMin(int[] values, int toPart) {
      for(int i = 0; i <= toPart; ++i) {
         Part part = Part.of(i);
         values[i] = this.getMin(part);
      }

   }

   private int getMin(Part part) {
      PartMatcher matcher = this.get(part);
      int min;
      if (matcher instanceof AlwaysTrueMatcher) {
         min = part.getMin();
      } else {
         if (!(matcher instanceof BoolArrayMatcher)) {
            throw new IllegalArgumentException("Invalid matcher: " + matcher.getClass().getName());
         }

         min = ((BoolArrayMatcher)matcher).getMinValue();
      }

      return min;
   }

   private Calendar setValue(Calendar calendar, Part part, int value) {
      switch (part) {
         case MONTH:
            --value;
            break;
         case DAY_OF_WEEK:
            ++value;
      }

      calendar.set(part.getCalendarField(), value);
      return calendar;
   }
}
