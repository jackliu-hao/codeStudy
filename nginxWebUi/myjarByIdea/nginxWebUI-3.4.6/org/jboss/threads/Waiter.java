package org.jboss.threads;

final class Waiter {
   private volatile Thread thread;
   private Waiter next;

   Waiter(Waiter next) {
      this.next = next;
   }

   Thread getThread() {
      return this.thread;
   }

   Waiter setThread(Thread thread) {
      this.thread = thread;
      return this;
   }

   Waiter getNext() {
      return this.next;
   }

   Waiter setNext(Waiter next) {
      this.next = next;
      return this;
   }
}
