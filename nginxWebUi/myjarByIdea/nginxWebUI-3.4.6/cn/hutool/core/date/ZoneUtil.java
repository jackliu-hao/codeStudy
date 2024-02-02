package cn.hutool.core.date;

import java.time.ZoneId;
import java.util.TimeZone;

public class ZoneUtil {
   public static TimeZone toTimeZone(ZoneId zoneId) {
      return null == zoneId ? TimeZone.getDefault() : TimeZone.getTimeZone(zoneId);
   }

   public static ZoneId toZoneId(TimeZone timeZone) {
      return null == timeZone ? ZoneId.systemDefault() : timeZone.toZoneId();
   }
}
