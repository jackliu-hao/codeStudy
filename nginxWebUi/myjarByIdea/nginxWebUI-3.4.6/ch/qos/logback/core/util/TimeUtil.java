package ch.qos.logback.core.util;

import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
   public static long computeStartOfNextSecond(long now) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(new Date(now));
      cal.set(14, 0);
      cal.add(13, 1);
      return cal.getTime().getTime();
   }

   public static long computeStartOfNextMinute(long now) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(new Date(now));
      cal.set(14, 0);
      cal.set(13, 0);
      cal.add(12, 1);
      return cal.getTime().getTime();
   }

   public static long computeStartOfNextHour(long now) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(new Date(now));
      cal.set(14, 0);
      cal.set(13, 0);
      cal.set(12, 0);
      cal.add(10, 1);
      return cal.getTime().getTime();
   }

   public static long computeStartOfNextDay(long now) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(new Date(now));
      cal.add(5, 1);
      cal.set(14, 0);
      cal.set(13, 0);
      cal.set(12, 0);
      cal.set(11, 0);
      return cal.getTime().getTime();
   }

   public static long computeStartOfNextWeek(long now) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(new Date(now));
      cal.set(7, cal.getFirstDayOfWeek());
      cal.set(11, 0);
      cal.set(12, 0);
      cal.set(13, 0);
      cal.set(14, 0);
      cal.add(3, 1);
      return cal.getTime().getTime();
   }

   public static long computeStartOfNextMonth(long now) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(new Date(now));
      cal.set(5, 1);
      cal.set(11, 0);
      cal.set(12, 0);
      cal.set(13, 0);
      cal.set(14, 0);
      cal.add(2, 1);
      return cal.getTime().getTime();
   }
}
