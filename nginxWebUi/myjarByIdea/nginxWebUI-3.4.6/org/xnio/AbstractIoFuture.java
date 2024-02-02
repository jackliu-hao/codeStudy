package org.xnio;

import java.io.IOException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import org.xnio._private.Messages;

public abstract class AbstractIoFuture<T> implements IoFuture<T> {
   private final AtomicReference<State<T>> stateRef;
   private static final State<?> ST_INITIAL = new InitialState();
   private static final State<?> ST_CANCELLED = new CancelledState();

   protected AbstractIoFuture() {
      this.stateRef = new AtomicReference(ST_INITIAL);
   }

   public IoFuture.Status getStatus() {
      return this.getState().getStatus();
   }

   private State<T> getState() {
      return (State)this.stateRef.get();
   }

   private boolean compareAndSetState(State<T> expect, State<T> update) {
      return this.stateRef.compareAndSet(expect, update);
   }

   public IoFuture.Status await() {
      Thread thread = Thread.currentThread();

      State state;
      State withWaiter;
      do {
         state = this.getState();
         if (state.getStatus() != IoFuture.Status.WAITING) {
            return state.getStatus();
         }

         Xnio.checkBlockingAllowed();
         withWaiter = state.withWaiter(thread);
      } while(!this.compareAndSetState(state, withWaiter));

      boolean intr = Thread.interrupted();

      IoFuture.Status var5;
      try {
         do {
            LockSupport.park(this);
            if (Thread.interrupted()) {
               intr = true;
            }

            state = this.getState();
         } while(state.getStatus() == IoFuture.Status.WAITING);

         var5 = state.getStatus();
      } finally {
         if (intr) {
            thread.interrupt();
         }

      }

      return var5;
   }

   public IoFuture.Status await(long time, TimeUnit timeUnit) {
      if (time < 0L) {
         time = 0L;
      }

      long duration = timeUnit.toNanos(time);
      long now = System.nanoTime();
      Thread thread = Thread.currentThread();

      State state;
      State withWaiter;
      do {
         state = this.getState();
         if (state.getStatus() != IoFuture.Status.WAITING || duration == 0L) {
            return state.getStatus();
         }

         Xnio.checkBlockingAllowed();
         withWaiter = state.withWaiter(thread);
      } while(!this.compareAndSetState(state, withWaiter));

      boolean intr = Thread.interrupted();

      IoFuture.Status var14;
      try {
         do {
            LockSupport.parkNanos(this, duration);
            if (Thread.interrupted()) {
               intr = true;
            }

            state = this.getState();
            long tick;
            duration -= (tick = System.nanoTime()) - now;
            now = tick;
         } while(state.getStatus() == IoFuture.Status.WAITING && duration > 0L);

         var14 = state.getStatus();
      } finally {
         if (intr) {
            thread.interrupt();
         }

      }

      return var14;
   }

   public IoFuture.Status awaitInterruptibly() throws InterruptedException {
      Thread thread = Thread.currentThread();

      State state;
      State withWaiter;
      do {
         state = this.getState();
         if (state.getStatus() != IoFuture.Status.WAITING) {
            return state.getStatus();
         }

         Xnio.checkBlockingAllowed();
         if (Thread.interrupted()) {
            throw new InterruptedException();
         }

         withWaiter = state.withWaiter(thread);
      } while(!this.compareAndSetState(state, withWaiter));

      do {
         LockSupport.park(this);
         if (Thread.interrupted()) {
            throw new InterruptedException();
         }

         state = this.getState();
      } while(state.getStatus() == IoFuture.Status.WAITING);

      return state.getStatus();
   }

   public IoFuture.Status awaitInterruptibly(long time, TimeUnit timeUnit) throws InterruptedException {
      if (time < 0L) {
         time = 0L;
      }

      long duration = timeUnit.toNanos(time);
      long now = System.nanoTime();
      Thread thread = Thread.currentThread();

      State state;
      State withWaiter;
      do {
         state = this.getState();
         if (state.getStatus() != IoFuture.Status.WAITING || duration == 0L) {
            return state.getStatus();
         }

         Xnio.checkBlockingAllowed();
         if (Thread.interrupted()) {
            throw new InterruptedException();
         }

         withWaiter = state.withWaiter(thread);
      } while(!this.compareAndSetState(state, withWaiter));

      do {
         LockSupport.parkNanos(this, duration);
         if (Thread.interrupted()) {
            throw new InterruptedException();
         }

         state = this.getState();
         long tick;
         duration -= (tick = System.nanoTime()) - now;
         now = tick;
      } while(state.getStatus() == IoFuture.Status.WAITING && duration > 0L);

      return state.getStatus();
   }

   public T get() throws IOException, CancellationException {
      switch (this.await()) {
         case DONE:
            return this.getState().getResult();
         case FAILED:
            throw this.getState().getException();
         case CANCELLED:
            throw Messages.futureMsg.opCancelled();
         default:
            throw new IllegalStateException();
      }
   }

   public T getInterruptibly() throws IOException, InterruptedException, CancellationException {
      switch (this.awaitInterruptibly()) {
         case DONE:
            return this.getState().getResult();
         case FAILED:
            throw this.getState().getException();
         case CANCELLED:
            throw Messages.futureMsg.opCancelled();
         default:
            throw new IllegalStateException();
      }
   }

   public IOException getException() throws IllegalStateException {
      return this.getState().getException();
   }

   public <A> IoFuture<T> addNotifier(IoFuture.Notifier<? super T, A> notifier, A attachment) {
      State oldState;
      State newState;
      do {
         oldState = this.getState();
         newState = oldState.withNotifier(this.getNotifierExecutor(), this, notifier, attachment);
      } while(!this.compareAndSetState(oldState, newState));

      return this;
   }

   protected boolean setException(IOException exception) {
      State<T> oldState = this.getState();
      if (oldState.getStatus() != IoFuture.Status.WAITING) {
         return false;
      } else {
         State<T> newState = new FailedState(exception);

         do {
            if (this.compareAndSetState(oldState, newState)) {
               oldState.notifyFailed(this, exception);
               return true;
            }

            oldState = this.getState();
         } while(oldState.getStatus() == IoFuture.Status.WAITING);

         return false;
      }
   }

   protected boolean setResult(T result) {
      State<T> oldState = this.getState();
      if (oldState.getStatus() != IoFuture.Status.WAITING) {
         return false;
      } else {
         State<T> newState = new CompleteState(result);

         do {
            if (this.compareAndSetState(oldState, newState)) {
               oldState.notifyDone(this, result);
               return true;
            }

            oldState = this.getState();
         } while(oldState.getStatus() == IoFuture.Status.WAITING);

         return false;
      }
   }

   protected boolean setCancelled() {
      State<T> oldState = this.getState();
      if (oldState.getStatus() != IoFuture.Status.WAITING) {
         return false;
      } else {
         State<T> newState = ST_CANCELLED;

         do {
            if (this.compareAndSetState(oldState, newState)) {
               oldState.notifyCancelled(this);
               return true;
            }

            oldState = this.getState();
         } while(oldState.getStatus() == IoFuture.Status.WAITING);

         return false;
      }
   }

   public IoFuture<T> cancel() {
      while(true) {
         State<T> state = this.getState();
         if (state.getStatus() == IoFuture.Status.WAITING && !state.cancelRequested()) {
            if (!this.compareAndSetState(state, new CancelRequestedState(state))) {
               continue;
            }

            state.cancel();
            return this;
         }

         return this;
      }
   }

   protected void addCancelHandler(Cancellable cancellable) {
      while(true) {
         State<T> oldState = this.getState();
         if (oldState.getStatus() == IoFuture.Status.WAITING && !oldState.cancelRequested()) {
            State<T> newState = oldState.withCancelHandler(cancellable);
            if (oldState == newState) {
               return;
            }

            if (!this.compareAndSetState(oldState, newState)) {
               continue;
            }

            return;
         }

         try {
            cancellable.cancel();
         } catch (Throwable var5) {
         }

         return;
      }
   }

   protected void runNotifier(Runnable runnable) {
      this.getNotifierExecutor().execute(runnable);
   }

   protected Executor getNotifierExecutor() {
      return IoUtils.directExecutor();
   }

   static class NotifierRunnable<T, A> implements Runnable {
      private final IoFuture.Notifier<? super T, A> notifier;
      private final IoFuture<T> future;
      private final A attachment;

      NotifierRunnable(IoFuture.Notifier<? super T, A> notifier, IoFuture<T> future, A attachment) {
         this.notifier = notifier;
         this.future = future;
         this.attachment = attachment;
      }

      public void run() {
         try {
            this.notifier.notify(this.future, this.attachment);
         } catch (Throwable var2) {
            Messages.futureMsg.notifierFailed(var2, this.notifier, this.attachment);
         }

      }
   }

   static final class CancelRequestedState<T> extends State<T> {
      final State<T> next;

      CancelRequestedState(State<T> next) {
         this.next = next;
      }

      IoFuture.Status getStatus() {
         return IoFuture.Status.WAITING;
      }

      void notifyDone(AbstractIoFuture<T> future, T result) {
         this.next.notifyDone(future, result);
      }

      void notifyFailed(AbstractIoFuture<T> future, IOException exception) {
         this.next.notifyFailed(future, exception);
      }

      void notifyCancelled(AbstractIoFuture<T> future) {
         this.next.notifyCancelled(future);
      }

      void cancel() {
      }

      boolean cancelRequested() {
         return true;
      }
   }

   static final class CancellableState<T> extends State<T> {
      final State<T> next;
      final Cancellable cancellable;

      CancellableState(State<T> next, Cancellable cancellable) {
         this.next = next;
         this.cancellable = cancellable;
      }

      IoFuture.Status getStatus() {
         return IoFuture.Status.WAITING;
      }

      void notifyDone(AbstractIoFuture<T> future, T result) {
         this.next.notifyDone(future, result);
      }

      void notifyFailed(AbstractIoFuture<T> future, IOException exception) {
         this.next.notifyFailed(future, exception);
      }

      void notifyCancelled(AbstractIoFuture<T> future) {
         this.next.notifyCancelled(future);
      }

      void cancel() {
         try {
            this.cancellable.cancel();
         } catch (Throwable var2) {
         }

         this.next.cancel();
      }

      boolean cancelRequested() {
         return this.next.cancelRequested();
      }
   }

   static final class WaiterState<T> extends State<T> {
      final State<T> next;
      final Thread waiter;

      WaiterState(State<T> next, Thread waiter) {
         this.next = next;
         this.waiter = waiter;
      }

      IoFuture.Status getStatus() {
         return IoFuture.Status.WAITING;
      }

      void notifyDone(AbstractIoFuture<T> future, T result) {
         LockSupport.unpark(this.waiter);
         this.next.notifyDone(future, result);
      }

      void notifyFailed(AbstractIoFuture<T> future, IOException exception) {
         LockSupport.unpark(this.waiter);
         this.next.notifyFailed(future, exception);
      }

      void notifyCancelled(AbstractIoFuture<T> future) {
         LockSupport.unpark(this.waiter);
         this.next.notifyCancelled(future);
      }

      void cancel() {
         this.next.cancel();
      }

      boolean cancelRequested() {
         return this.next.cancelRequested();
      }
   }

   static final class NotifierState<T, A> extends State<T> {
      final State<T> next;
      final IoFuture.Notifier<? super T, A> notifier;
      final A attachment;

      NotifierState(State<T> next, IoFuture.Notifier<? super T, A> notifier, A attachment) {
         this.next = next;
         this.notifier = notifier;
         this.attachment = attachment;
      }

      IoFuture.Status getStatus() {
         return IoFuture.Status.WAITING;
      }

      void notifyDone(AbstractIoFuture<T> future, T result) {
         this.doNotify(future);
         this.next.notifyDone(future, result);
      }

      void notifyFailed(AbstractIoFuture<T> future, IOException exception) {
         this.doNotify(future);
         this.next.notifyFailed(future, exception);
      }

      void notifyCancelled(AbstractIoFuture<T> future) {
         this.doNotify(future);
         this.next.notifyCancelled(future);
      }

      void cancel() {
         this.next.cancel();
      }

      private void doNotify(AbstractIoFuture<T> future) {
         future.runNotifier(new NotifierRunnable(this.notifier, future, this.attachment));
      }

      boolean cancelRequested() {
         return this.next.cancelRequested();
      }
   }

   static final class CancelledState<T> extends State<T> {
      IoFuture.Status getStatus() {
         return IoFuture.Status.CANCELLED;
      }

      void notifyDone(AbstractIoFuture<T> future, T result) {
      }

      void notifyFailed(AbstractIoFuture<T> future, IOException exception) {
      }

      void notifyCancelled(AbstractIoFuture<T> future) {
      }

      void cancel() {
      }

      State<T> withCancelHandler(Cancellable cancellable) {
         try {
            cancellable.cancel();
         } catch (Throwable var3) {
         }

         return this;
      }

      <A> State<T> withNotifier(Executor executor, AbstractIoFuture<T> future, IoFuture.Notifier<? super T, A> notifier, A attachment) {
         future.runNotifier(new NotifierRunnable(notifier, future, attachment));
         return this;
      }

      State<T> withWaiter(Thread thread) {
         return this;
      }

      boolean cancelRequested() {
         return true;
      }
   }

   static final class FailedState<T> extends State<T> {
      private final IOException exception;

      FailedState(IOException exception) {
         this.exception = exception;
      }

      IoFuture.Status getStatus() {
         return IoFuture.Status.FAILED;
      }

      void notifyDone(AbstractIoFuture<T> future, T result) {
      }

      void notifyFailed(AbstractIoFuture<T> future, IOException exception) {
      }

      void notifyCancelled(AbstractIoFuture<T> future) {
      }

      void cancel() {
      }

      State<T> withCancelHandler(Cancellable cancellable) {
         return this;
      }

      State<T> withWaiter(Thread thread) {
         return this;
      }

      <A> State<T> withNotifier(Executor executor, AbstractIoFuture<T> future, IoFuture.Notifier<? super T, A> notifier, A attachment) {
         future.runNotifier(new NotifierRunnable(notifier, future, attachment));
         return this;
      }

      IOException getException() {
         return this.exception;
      }

      boolean cancelRequested() {
         return false;
      }
   }

   static final class CompleteState<T> extends State<T> {
      private final T result;

      CompleteState(T result) {
         this.result = result;
      }

      IoFuture.Status getStatus() {
         return IoFuture.Status.DONE;
      }

      void notifyDone(AbstractIoFuture<T> future, T result) {
      }

      void notifyFailed(AbstractIoFuture<T> future, IOException exception) {
      }

      void notifyCancelled(AbstractIoFuture<T> future) {
      }

      void cancel() {
      }

      State<T> withCancelHandler(Cancellable cancellable) {
         return this;
      }

      State<T> withWaiter(Thread thread) {
         return this;
      }

      <A> State<T> withNotifier(Executor executor, AbstractIoFuture<T> future, IoFuture.Notifier<? super T, A> notifier, A attachment) {
         future.runNotifier(new NotifierRunnable(notifier, future, attachment));
         return this;
      }

      T getResult() {
         return this.result;
      }

      boolean cancelRequested() {
         return false;
      }
   }

   static final class InitialState<T> extends State<T> {
      IoFuture.Status getStatus() {
         return IoFuture.Status.WAITING;
      }

      void notifyDone(AbstractIoFuture<T> future, T result) {
      }

      void notifyFailed(AbstractIoFuture<T> future, IOException exception) {
      }

      void notifyCancelled(AbstractIoFuture<T> future) {
      }

      void cancel() {
      }

      boolean cancelRequested() {
         return false;
      }
   }

   abstract static class State<T> {
      abstract IoFuture.Status getStatus();

      abstract void notifyDone(AbstractIoFuture<T> var1, T var2);

      abstract void notifyFailed(AbstractIoFuture<T> var1, IOException var2);

      abstract void notifyCancelled(AbstractIoFuture<T> var1);

      abstract void cancel();

      abstract boolean cancelRequested();

      State<T> withWaiter(Thread thread) {
         return new WaiterState(this, thread);
      }

      <A> State<T> withNotifier(Executor executor, AbstractIoFuture<T> future, IoFuture.Notifier<? super T, A> notifier, A attachment) {
         return new NotifierState(this, notifier, attachment);
      }

      State<T> withCancelHandler(Cancellable cancellable) {
         return new CancellableState(this, cancellable);
      }

      T getResult() {
         throw new IllegalStateException();
      }

      IOException getException() {
         throw new IllegalStateException();
      }
   }
}
