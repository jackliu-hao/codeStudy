package cn.hutool.cron.pattern;

import cn.hutool.core.date.Week;
import java.time.LocalDateTime;
import java.util.Calendar;

class PatternUtil {
   static int[] getFields(LocalDateTime dateTime, boolean isMatchSecond) {
      int second = isMatchSecond ? dateTime.getSecond() : -1;
      int minute = dateTime.getMinute();
      int hour = dateTime.getHour();
      int dayOfMonth = dateTime.getDayOfMonth();
      int month = dateTime.getMonthValue();
      int dayOfWeek = Week.of(dateTime.getDayOfWeek()).getValue() - 1;
      int year = dateTime.getYear();
      return new int[]{second, minute, hour, dayOfMonth, month, dayOfWeek, year};
   }

   static int[] getFields(Calendar calendar, boolean isMatchSecond) {
      int second = isMatchSecond ? calendar.get(13) : -1;
      int minute = calendar.get(12);
      int hour = calendar.get(11);
      int dayOfMonth = calendar.get(5);
      int month = calendar.get(2) + 1;
      int dayOfWeek = calendar.get(7) - 1;
      int year = calendar.get(1);
      return new int[]{second, minute, hour, dayOfMonth, month, dayOfWeek, year};
   }
}
