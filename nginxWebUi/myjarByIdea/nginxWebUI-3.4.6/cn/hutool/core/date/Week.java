package cn.hutool.core.date;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import java.time.DayOfWeek;

public enum Week {
   SUNDAY(1),
   MONDAY(2),
   TUESDAY(3),
   WEDNESDAY(4),
   THURSDAY(5),
   FRIDAY(6),
   SATURDAY(7);

   private static final String[] ALIASES = new String[]{"sun", "mon", "tue", "wed", "thu", "fri", "sat"};
   private static final Week[] ENUMS = values();
   private final int value;

   private Week(int value) {
      this.value = value;
   }

   public int getValue() {
      return this.value;
   }

   public int getIso8601Value() {
      int iso8601IntValue = this.getValue() - 1;
      if (0 == iso8601IntValue) {
         iso8601IntValue = 7;
      }

      return iso8601IntValue;
   }

   public String toChinese() {
      return this.toChinese("星期");
   }

   public String toChinese(String weekNamePre) {
      switch (this) {
         case SUNDAY:
            return weekNamePre + "日";
         case MONDAY:
            return weekNamePre + "一";
         case TUESDAY:
            return weekNamePre + "二";
         case WEDNESDAY:
            return weekNamePre + "三";
         case THURSDAY:
            return weekNamePre + "四";
         case FRIDAY:
            return weekNamePre + "五";
         case SATURDAY:
            return weekNamePre + "六";
         default:
            return null;
      }
   }

   public DayOfWeek toJdkDayOfWeek() {
      return DayOfWeek.of(this.getIso8601Value());
   }

   public static Week of(int calendarWeekIntValue) {
      return calendarWeekIntValue <= ENUMS.length && calendarWeekIntValue >= 1 ? ENUMS[calendarWeekIntValue - 1] : null;
   }

   public static Week of(String name) throws IllegalArgumentException {
      Assert.notBlank(name);
      Week of = of(ArrayUtil.indexOfIgnoreCase(ALIASES, name) + 1);
      if (null == of) {
         of = valueOf(name.toUpperCase());
      }

      return of;
   }

   public static Week of(DayOfWeek dayOfWeek) {
      Assert.notNull(dayOfWeek);
      int week = dayOfWeek.getValue() + 1;
      if (8 == week) {
         week = 1;
      }

      return of(week);
   }
}
