package cn.hutool.cron;

import java.util.TimeZone;

public class CronConfig {
   protected TimeZone timezone = TimeZone.getDefault();
   protected boolean matchSecond;

   public CronConfig setTimeZone(TimeZone timezone) {
      this.timezone = timezone;
      return this;
   }

   public TimeZone getTimeZone() {
      return this.timezone;
   }

   public boolean isMatchSecond() {
      return this.matchSecond;
   }

   public CronConfig setMatchSecond(boolean isMatchSecond) {
      this.matchSecond = isMatchSecond;
      return this;
   }
}
