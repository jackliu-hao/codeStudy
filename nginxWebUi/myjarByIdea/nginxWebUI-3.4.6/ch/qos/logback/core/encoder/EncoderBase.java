package ch.qos.logback.core.encoder;

import ch.qos.logback.core.spi.ContextAwareBase;

public abstract class EncoderBase<E> extends ContextAwareBase implements Encoder<E> {
   protected boolean started;

   public boolean isStarted() {
      return this.started;
   }

   public void start() {
      this.started = true;
   }

   public void stop() {
      this.started = false;
   }
}
