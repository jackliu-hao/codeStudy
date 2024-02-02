package ch.qos.logback.core.sift;

import ch.qos.logback.core.spi.ContextAwareBase;

public abstract class AbstractDiscriminator<E> extends ContextAwareBase implements Discriminator<E> {
   protected boolean started;

   public void start() {
      this.started = true;
   }

   public void stop() {
      this.started = false;
   }

   public boolean isStarted() {
      return this.started;
   }
}
