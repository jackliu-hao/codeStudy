package org.jboss.threads;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

class DelegatingScheduledExecutorService extends DelegatingExecutorService implements ScheduledExecutorService {
   private final ScheduledExecutorService delegate;

   DelegatingScheduledExecutorService(ScheduledExecutorService delegate) {
      super(delegate);
      this.delegate = delegate;
   }

   public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
      return this.delegate.schedule(command, delay, unit);
   }

   public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
      return this.delegate.schedule(callable, delay, unit);
   }

   public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
      return this.delegate.scheduleAtFixedRate(command, initialDelay, period, unit);
   }

   public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
      return this.delegate.scheduleWithFixedDelay(command, initialDelay, delay, unit);
   }
}
