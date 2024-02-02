package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class RelativeTimeConverter extends ClassicConverter {
   long lastTimestamp = -1L;
   String timesmapCache = null;

   public String convert(ILoggingEvent event) {
      long now = event.getTimeStamp();
      synchronized(this) {
         if (now != this.lastTimestamp) {
            this.lastTimestamp = now;
            this.timesmapCache = Long.toString(now - event.getLoggerContextVO().getBirthTime());
         }

         return this.timesmapCache;
      }
   }
}
