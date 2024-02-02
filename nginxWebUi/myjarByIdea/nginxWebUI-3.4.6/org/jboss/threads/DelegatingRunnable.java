package org.jboss.threads;

class DelegatingRunnable implements Runnable {
   private final Runnable delegate;

   DelegatingRunnable(Runnable delegate) {
      this.delegate = delegate;
   }

   public void run() {
      this.delegate.run();
   }

   public String toString() {
      return String.format("%s -> %s", super.toString(), this.delegate);
   }
}
