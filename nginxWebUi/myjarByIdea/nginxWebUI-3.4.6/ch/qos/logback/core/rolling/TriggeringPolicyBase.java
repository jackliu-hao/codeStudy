package ch.qos.logback.core.rolling;

import ch.qos.logback.core.spi.ContextAwareBase;

public abstract class TriggeringPolicyBase<E> extends ContextAwareBase implements TriggeringPolicy<E> {
   private boolean start;

   public void start() {
      this.start = true;
   }

   public void stop() {
      this.start = false;
   }

   public boolean isStarted() {
      return this.start;
   }
}
