package cn.hutool.core.date;

import java.time.temporal.ChronoUnit;

public enum DateUnit {
   MS(1L),
   SECOND(1000L),
   MINUTE(SECOND.getMillis() * 60L),
   HOUR(MINUTE.getMillis() * 60L),
   DAY(HOUR.getMillis() * 24L),
   WEEK(DAY.getMillis() * 7L);

   private final long millis;

   private DateUnit(long millis) {
      this.millis = millis;
   }

   public long getMillis() {
      return this.millis;
   }

   public ChronoUnit toChronoUnit() {
      return toChronoUnit(this);
   }

   public static DateUnit of(ChronoUnit unit) {
      switch (unit) {
         case MICROS:
            return MS;
         case SECONDS:
            return SECOND;
         case MINUTES:
            return MINUTE;
         case HOURS:
            return HOUR;
         case DAYS:
            return DAY;
         case WEEKS:
            return WEEK;
         default:
            return null;
      }
   }

   public static ChronoUnit toChronoUnit(DateUnit unit) {
      switch (unit) {
         case MS:
            return ChronoUnit.MICROS;
         case SECOND:
            return ChronoUnit.SECONDS;
         case MINUTE:
            return ChronoUnit.MINUTES;
         case HOUR:
            return ChronoUnit.HOURS;
         case DAY:
            return ChronoUnit.DAYS;
         case WEEK:
            return ChronoUnit.WEEKS;
         default:
            return null;
      }
   }
}
