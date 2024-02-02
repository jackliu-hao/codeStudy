package org.jboss.threads;

class ContextClassLoaderSavingRunnable implements Runnable {
   private final ClassLoader loader;
   private final Runnable delegate;

   ContextClassLoaderSavingRunnable(ClassLoader loader, Runnable delegate) {
      this.loader = loader;
      this.delegate = delegate;
   }

   public void run() {
      Thread currentThread = Thread.currentThread();
      ClassLoader old = JBossExecutors.getAndSetContextClassLoader(currentThread, this.loader);

      try {
         this.delegate.run();
      } finally {
         JBossExecutors.setContextClassLoader(currentThread, old);
      }

   }

   public String toString() {
      return "Context class loader saving " + this.delegate.toString();
   }
}
