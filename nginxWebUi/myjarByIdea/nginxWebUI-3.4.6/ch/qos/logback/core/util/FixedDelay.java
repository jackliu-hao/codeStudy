package ch.qos.logback.core.util;

public class FixedDelay implements DelayStrategy {
   private final long subsequentDelay;
   private long nextDelay;

   public FixedDelay(long initialDelay, long subsequentDelay) {
      this.nextDelay = initialDelay;
      this.subsequentDelay = subsequentDelay;
   }

   public FixedDelay(int delay) {
      this((long)delay, (long)delay);
   }

   public long nextDelay() {
      long delay = this.nextDelay;
      this.nextDelay = this.subsequentDelay;
      return delay;
   }
}
