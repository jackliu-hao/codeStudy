package ch.qos.logback.core.sift;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.util.Duration;
import java.util.Iterator;

public abstract class SiftingAppenderBase<E> extends AppenderBase<E> {
   protected AppenderTracker<E> appenderTracker;
   AppenderFactory<E> appenderFactory;
   Duration timeout = new Duration(1800000L);
   int maxAppenderCount = Integer.MAX_VALUE;
   Discriminator<E> discriminator;

   public Duration getTimeout() {
      return this.timeout;
   }

   public void setTimeout(Duration timeout) {
      this.timeout = timeout;
   }

   public int getMaxAppenderCount() {
      return this.maxAppenderCount;
   }

   public void setMaxAppenderCount(int maxAppenderCount) {
      this.maxAppenderCount = maxAppenderCount;
   }

   public void setAppenderFactory(AppenderFactory<E> appenderFactory) {
      this.appenderFactory = appenderFactory;
   }

   public void start() {
      int errors = 0;
      if (this.discriminator == null) {
         this.addError("Missing discriminator. Aborting");
         ++errors;
      }

      if (!this.discriminator.isStarted()) {
         this.addError("Discriminator has not started successfully. Aborting");
         ++errors;
      }

      if (this.appenderFactory == null) {
         this.addError("AppenderFactory has not been set. Aborting");
         ++errors;
      } else {
         this.appenderTracker = new AppenderTracker(this.context, this.appenderFactory);
         this.appenderTracker.setMaxComponents(this.maxAppenderCount);
         this.appenderTracker.setTimeout(this.timeout.getMilliseconds());
      }

      if (errors == 0) {
         super.start();
      }

   }

   public void stop() {
      Iterator var1 = this.appenderTracker.allComponents().iterator();

      while(var1.hasNext()) {
         Appender<E> appender = (Appender)var1.next();
         appender.stop();
      }

   }

   protected abstract long getTimestamp(E var1);

   protected void append(E event) {
      if (this.isStarted()) {
         String discriminatingValue = this.discriminator.getDiscriminatingValue(event);
         long timestamp = this.getTimestamp(event);
         Appender<E> appender = (Appender)this.appenderTracker.getOrCreate(discriminatingValue, timestamp);
         if (this.eventMarksEndOfLife(event)) {
            this.appenderTracker.endOfLife(discriminatingValue);
         }

         this.appenderTracker.removeStaleComponents(timestamp);
         appender.doAppend(event);
      }
   }

   protected abstract boolean eventMarksEndOfLife(E var1);

   public Discriminator<E> getDiscriminator() {
      return this.discriminator;
   }

   public void setDiscriminator(Discriminator<E> discriminator) {
      this.discriminator = discriminator;
   }

   public AppenderTracker<E> getAppenderTracker() {
      return this.appenderTracker;
   }

   public String getDiscriminatorKey() {
      return this.discriminator != null ? this.discriminator.getKey() : null;
   }
}
