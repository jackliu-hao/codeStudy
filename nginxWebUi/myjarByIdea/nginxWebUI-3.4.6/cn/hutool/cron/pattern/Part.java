package cn.hutool.cron.pattern;

import cn.hutool.core.date.Month;
import cn.hutool.core.date.Week;
import cn.hutool.core.lang.Assert;
import cn.hutool.cron.CronException;

public enum Part {
   SECOND(13, 0, 59),
   MINUTE(12, 0, 59),
   HOUR(11, 0, 23),
   DAY_OF_MONTH(5, 1, 31),
   MONTH(2, Month.JANUARY.getValueBaseOne(), Month.DECEMBER.getValueBaseOne()),
   DAY_OF_WEEK(7, Week.SUNDAY.ordinal(), Week.SATURDAY.ordinal()),
   YEAR(1, 1970, 2099);

   private static final Part[] ENUMS = values();
   private final int calendarField;
   private final int min;
   private final int max;

   private Part(int calendarField, int min, int max) {
      this.calendarField = calendarField;
      if (min > max) {
         this.min = max;
         this.max = min;
      } else {
         this.min = min;
         this.max = max;
      }

   }

   public int getCalendarField() {
      return this.calendarField;
   }

   public int getMin() {
      return this.min;
   }

   public int getMax() {
      return this.max;
   }

   public int checkValue(int value) throws CronException {
      Assert.checkBetween(value, this.min, this.max, () -> {
         return new CronException("Value {} out of range: [{} , {}]", new Object[]{value, this.min, this.max});
      });
      return value;
   }

   public static Part of(int i) {
      return ENUMS[i];
   }
}
