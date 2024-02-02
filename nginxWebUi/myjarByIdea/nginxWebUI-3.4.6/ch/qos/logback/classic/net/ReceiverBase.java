package ch.qos.logback.classic.net;

import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;

public abstract class ReceiverBase extends ContextAwareBase implements LifeCycle {
   private boolean started;

   public final void start() {
      if (!this.isStarted()) {
         if (this.getContext() == null) {
            throw new IllegalStateException("context not set");
         } else {
            if (this.shouldStart()) {
               this.getContext().getScheduledExecutorService().execute(this.getRunnableTask());
               this.started = true;
            }

         }
      }
   }

   public final void stop() {
      if (this.isStarted()) {
         try {
            this.onStop();
         } catch (RuntimeException var2) {
            this.addError("on stop: " + var2, var2);
         }

         this.started = false;
      }
   }

   public final boolean isStarted() {
      return this.started;
   }

   protected abstract boolean shouldStart();

   protected abstract void onStop();

   protected abstract Runnable getRunnableTask();
}
