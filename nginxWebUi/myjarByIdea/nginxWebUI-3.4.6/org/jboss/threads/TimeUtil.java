package org.jboss.threads;

import java.time.Duration;

final class TimeUtil {
   private static final long LARGEST_SECONDS = 9223372035L;

   private TimeUtil() {
   }

   static long clampedPositiveNanos(Duration duration) {
      long seconds = Math.max(0L, duration.getSeconds());
      return seconds > 9223372035L ? Long.MAX_VALUE : Math.max(1L, seconds * 1000000000L + (long)duration.getNano());
   }
}
