package io.undertow.server.handlers.proxy;

public interface ConnectionPoolErrorHandler {
   boolean isAvailable();

   boolean handleError();

   boolean clearError();

   public static class CountingErrorHandler implements ConnectionPoolErrorHandler {
      private int count;
      private long timeout;
      private final long interval;
      private final int errorCount;
      private final int successCount;
      private final ConnectionPoolErrorHandler delegate;

      public CountingErrorHandler(int errorCount, int successCount, long interval) {
         this(errorCount, successCount, interval, new SimpleConnectionPoolErrorHandler());
      }

      public CountingErrorHandler(int errorCount, int successCount, long interval, ConnectionPoolErrorHandler delegate) {
         this.errorCount = Math.max(errorCount, 1);
         this.successCount = Math.max(successCount, 1);
         this.interval = Math.max(interval, 0L);
         this.delegate = delegate;
      }

      public boolean isAvailable() {
         return this.delegate.isAvailable();
      }

      public synchronized boolean handleError() {
         if (this.delegate.isAvailable()) {
            long time = System.currentTimeMillis();
            if (time >= this.timeout) {
               this.count = 1;
               this.timeout = time + this.interval;
            } else if (this.count++ == 1) {
               this.timeout = time + this.interval;
            }

            return this.count >= this.errorCount ? this.delegate.handleError() : true;
         } else {
            this.count = 0;
            return false;
         }
      }

      public synchronized boolean clearError() {
         if (this.delegate.isAvailable()) {
            this.count = 0;
            return true;
         } else {
            return this.count++ == this.successCount ? this.delegate.clearError() : false;
         }
      }
   }

   public static class SimpleConnectionPoolErrorHandler implements ConnectionPoolErrorHandler {
      private volatile boolean problem;

      public boolean isAvailable() {
         return !this.problem;
      }

      public boolean handleError() {
         this.problem = true;
         return false;
      }

      public boolean clearError() {
         this.problem = false;
         return true;
      }
   }
}
