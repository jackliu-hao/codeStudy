package org.jboss.threads;

final class ThreadLocalResettingRunnable extends DelegatingRunnable {
   ThreadLocalResettingRunnable(Runnable delegate) {
      super(delegate);
   }

   public void run() {
      try {
         super.run();
      } finally {
         ThreadLocalResettingRunnable.Resetter.run();
      }

   }

   public String toString() {
      return "Thread-local resetting Runnable";
   }

   static final class Resetter {
      private static final long threadLocalMapOffs;
      private static final long inheritableThreadLocalMapOffs;

      static void run() {
         Thread thread = Thread.currentThread();
         JBossExecutors.unsafe.putObject(thread, threadLocalMapOffs, (Object)null);
         JBossExecutors.unsafe.putObject(thread, inheritableThreadLocalMapOffs, (Object)null);
      }

      static {
         try {
            threadLocalMapOffs = JBossExecutors.unsafe.objectFieldOffset(Thread.class.getDeclaredField("threadLocals"));
            inheritableThreadLocalMapOffs = JBossExecutors.unsafe.objectFieldOffset(Thread.class.getDeclaredField("inheritableThreadLocals"));
         } catch (NoSuchFieldException var1) {
            throw new NoSuchFieldError(var1.getMessage());
         }
      }
   }
}
