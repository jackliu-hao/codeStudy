package ch.qos.logback.core.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CachingDateFormatter {
   long lastTimestamp = -1L;
   String cachedStr = null;
   final SimpleDateFormat sdf;

   public CachingDateFormatter(String pattern) {
      this.sdf = new SimpleDateFormat(pattern);
   }

   public final String format(long now) {
      synchronized(this) {
         if (now != this.lastTimestamp) {
            this.lastTimestamp = now;
            this.cachedStr = this.sdf.format(new Date(now));
         }

         return this.cachedStr;
      }
   }

   public void setTimeZone(TimeZone tz) {
      this.sdf.setTimeZone(tz);
   }
}
