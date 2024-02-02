package org.jboss.threads;

import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
import org.wildfly.common.Assert;
import org.wildfly.common.function.ExceptionBiConsumer;
import org.wildfly.common.function.ExceptionBiFunction;
import org.wildfly.common.function.ExceptionConsumer;
import org.wildfly.common.function.ExceptionFunction;
import org.wildfly.common.function.ExceptionObjIntConsumer;
import org.wildfly.common.function.ExceptionObjLongConsumer;
import org.wildfly.common.function.ExceptionRunnable;
import org.wildfly.common.function.ExceptionSupplier;
import org.wildfly.common.function.Functions;

public class JBossThread extends Thread {
   private static final RuntimePermission MODIFY_THREAD_PERMISSION = new RuntimePermission("modifyThread");
   private volatile InterruptHandler interruptHandler;
   private ThreadNameInfo threadNameInfo;
   private List<Runnable> exitHandlers;
   private static final int STATE_MAYBE_INTERRUPTED = 0;
   private static final int STATE_INTERRUPT_DEFERRED = 1;
   private static final int STATE_INTERRUPT_PENDING = 2;
   private static final int STATE_INTERRUPT_IN_PROGRESS = 3;
   private final AtomicInteger stateRef = new AtomicInteger();

   public JBossThread(Runnable target) {
      super(target);
   }

   public JBossThread(Runnable target, String name) {
      super(target, name);
   }

   public JBossThread(ThreadGroup group, Runnable target) throws SecurityException {
      super(group, target);
   }

   public JBossThread(ThreadGroup group, Runnable target, String name) throws SecurityException {
      super(group, target, name);
   }

   public JBossThread(ThreadGroup group, Runnable target, String name, long stackSize) throws SecurityException {
      super(group, target, name, stackSize);
   }

   public void interrupt() {
      boolean differentThread = Thread.currentThread() != this;
      if (differentThread) {
         this.checkAccess();
      }

      if (!this.isInterrupted()) {
         AtomicInteger stateRef = this.stateRef;

         int oldVal;
         byte newVal;
         do {
            oldVal = stateRef.get();
            if (oldVal == 2 || oldVal == 3) {
               Messages.msg.tracef("Interrupting thread \"%s\" (already interrupted)", this);
               return;
            }

            if (oldVal == 1) {
               newVal = 2;
            } else {
               newVal = 3;
            }
         } while(!stateRef.compareAndSet(oldVal, newVal));

         if (newVal == 3) {
            try {
               this.doInterrupt();
            } finally {
               stateRef.set(0);
               if (differentThread) {
                  LockSupport.unpark(this);
               }

            }
         } else {
            Messages.intMsg.tracef("Interrupting thread \"%s\" (deferred)", this);
         }

      }
   }

   private void doInterrupt() {
      if (!this.isInterrupted()) {
         Messages.msg.tracef("Interrupting thread \"%s\"", this);
         boolean var9 = false;

         try {
            var9 = true;
            super.interrupt();
            var9 = false;
         } finally {
            if (var9) {
               InterruptHandler interruptHandler = this.interruptHandler;
               if (interruptHandler != null) {
                  try {
                     interruptHandler.handleInterrupt(this);
                  } catch (Throwable var10) {
                     Messages.msg.interruptHandlerThrew(var10, interruptHandler);
                  }
               }

            }
         }

         InterruptHandler interruptHandler = this.interruptHandler;
         if (interruptHandler != null) {
            try {
               interruptHandler.handleInterrupt(this);
            } catch (Throwable var11) {
               Messages.msg.interruptHandlerThrew(var11, interruptHandler);
            }
         }

      }
   }

   public boolean isInterrupted() {
      return this == Thread.currentThread() ? super.isInterrupted() : super.isInterrupted() || this.stateRef.get() == 2;
   }

   public static void executeWithInterruptDeferred(Runnable task) {
      JBossThread thread = currentThread();
      if (registerDeferral(thread)) {
         try {
            task.run();
         } finally {
            unregisterDeferral(thread);
         }
      } else {
         task.run();
      }

   }

   public static <T> T executeWithInterruptDeferred(Callable<T> action) throws Exception {
      JBossThread thread = currentThread();
      if (registerDeferral(thread)) {
         Object var2;
         try {
            var2 = action.call();
         } finally {
            unregisterDeferral(thread);
         }

         return var2;
      } else {
         return action.call();
      }
   }

   public static <T> T executeWithInterruptDeferred(PrivilegedAction<T> action) {
      JBossThread thread = currentThread();
      if (registerDeferral(thread)) {
         Object var2;
         try {
            var2 = action.run();
         } finally {
            unregisterDeferral(thread);
         }

         return var2;
      } else {
         return action.run();
      }
   }

   public static <T> T executeWithInterruptDeferred(PrivilegedExceptionAction<T> action) throws Exception {
      JBossThread thread = currentThread();
      if (registerDeferral(thread)) {
         Object var2;
         try {
            var2 = action.run();
         } finally {
            unregisterDeferral(thread);
         }

         return var2;
      } else {
         return action.run();
      }
   }

   public static <T, U, R, E extends Exception> R applyInterruptDeferredEx(ExceptionBiFunction<T, U, R, E> function, T param1, U param2) throws E {
      JBossThread thread = currentThread();
      if (registerDeferral(thread)) {
         Object var4;
         try {
            var4 = function.apply(param1, param2);
         } finally {
            unregisterDeferral(thread);
         }

         return var4;
      } else {
         return function.apply(param1, param2);
      }
   }

   public static <T, R, E extends Exception> R applyInterruptDeferredEx(ExceptionFunction<T, R, E> function, T param) throws E {
      return applyInterruptDeferredEx(Functions.exceptionFunctionBiFunction(), function, param);
   }

   public static <T, E extends Exception> T getInterruptDeferredEx(ExceptionSupplier<T, E> supplier) throws E {
      return applyInterruptDeferredEx(Functions.exceptionFunctionBiFunction(), Functions.exceptionSupplierFunction(), supplier);
   }

   public static <T, E extends Exception> void acceptInterruptDeferredEx(ExceptionObjLongConsumer<T, E> consumer, T param1, long param2) throws E {
      JBossThread thread = currentThread();
      if (registerDeferral(thread)) {
         try {
            consumer.accept(param1, param2);
         } finally {
            unregisterDeferral(thread);
         }
      } else {
         consumer.accept(param1, param2);
      }

   }

   public static <T, E extends Exception> void acceptInterruptDeferredEx(ExceptionObjIntConsumer<T, E> consumer, T param1, int param2) throws E {
      JBossThread thread = currentThread();
      if (registerDeferral(thread)) {
         try {
            consumer.accept(param1, param2);
         } finally {
            unregisterDeferral(thread);
         }
      } else {
         consumer.accept(param1, param2);
      }

   }

   public static <T, U, E extends Exception> void acceptInterruptDeferredEx(ExceptionBiConsumer<T, U, E> consumer, T param1, U param2) throws E {
      JBossThread thread = currentThread();
      if (registerDeferral(thread)) {
         try {
            consumer.accept(param1, param2);
         } finally {
            unregisterDeferral(thread);
         }
      } else {
         consumer.accept(param1, param2);
      }

   }

   public static <T, E extends Exception> void acceptInterruptDeferredEx(ExceptionConsumer<T, E> consumer, T param) throws E {
      acceptInterruptDeferredEx(Functions.exceptionConsumerBiConsumer(), consumer, param);
   }

   public static <E extends Exception> void runInterruptDeferredEx(ExceptionRunnable<E> runnable) throws E {
      acceptInterruptDeferredEx(Functions.exceptionConsumerBiConsumer(), Functions.exceptionRunnableConsumer(), runnable);
   }

   public static <T, U, R, E extends Exception> R applyInterruptResumedEx(ExceptionBiFunction<T, U, R, E> function, T param1, U param2) throws E {
      JBossThread thread = currentThread();
      if (unregisterDeferral(thread)) {
         Object var4;
         try {
            var4 = function.apply(param1, param2);
         } finally {
            registerDeferral(thread);
         }

         return var4;
      } else {
         return function.apply(param1, param2);
      }
   }

   public static <T, R, E extends Exception> R applyInterruptResumedEx(ExceptionFunction<T, R, E> function, T param) throws E {
      return applyInterruptResumedEx(Functions.exceptionFunctionBiFunction(), function, param);
   }

   public static <T, E extends Exception> T getInterruptResumedEx(ExceptionSupplier<T, E> supplier) throws E {
      return applyInterruptResumedEx(Functions.exceptionFunctionBiFunction(), Functions.exceptionSupplierFunction(), supplier);
   }

   public static <T, E extends Exception> void acceptInterruptResumedEx(ExceptionObjLongConsumer<T, E> consumer, T param1, long param2) throws E {
      JBossThread thread = currentThread();
      if (unregisterDeferral(thread)) {
         try {
            consumer.accept(param1, param2);
         } finally {
            registerDeferral(thread);
         }
      } else {
         consumer.accept(param1, param2);
      }

   }

   public static <T, E extends Exception> void acceptInterruptResumedEx(ExceptionObjIntConsumer<T, E> consumer, T param1, int param2) throws E {
      JBossThread thread = currentThread();
      if (unregisterDeferral(thread)) {
         try {
            consumer.accept(param1, param2);
         } finally {
            registerDeferral(thread);
         }
      } else {
         consumer.accept(param1, param2);
      }

   }

   public static <T, U, E extends Exception> void acceptInterruptResumedEx(ExceptionBiConsumer<T, U, E> consumer, T param1, U param2) throws E {
      JBossThread thread = currentThread();
      if (unregisterDeferral(thread)) {
         try {
            consumer.accept(param1, param2);
         } finally {
            registerDeferral(thread);
         }
      } else {
         consumer.accept(param1, param2);
      }

   }

   public static <T, E extends Exception> void acceptInterruptResumedEx(ExceptionConsumer<T, E> consumer, T param) throws E {
      acceptInterruptResumedEx(Functions.exceptionConsumerBiConsumer(), consumer, param);
   }

   public static <E extends Exception> void runInterruptResumedEx(ExceptionRunnable<E> runnable) throws E {
      acceptInterruptResumedEx(Functions.exceptionConsumerBiConsumer(), Functions.exceptionRunnableConsumer(), runnable);
   }

   private static boolean unregisterDeferral(JBossThread thread) {
      if (thread == null) {
         return false;
      } else {
         AtomicInteger stateRef = thread.stateRef;

         int oldVal;
         byte newVal;
         do {
            oldVal = stateRef.get();
            if (oldVal == 0 || oldVal == 3) {
               return false;
            }

            if (oldVal == 1) {
               newVal = 0;
            } else {
               if (oldVal != 2) {
                  throw Assert.unreachableCode();
               }

               newVal = 3;
            }
         } while(!stateRef.compareAndSet(oldVal, newVal));

         if (newVal == 3) {
            try {
               thread.doInterrupt();
            } finally {
               stateRef.set(0);
            }
         }

         return true;
      }
   }

   private static boolean registerDeferral(JBossThread thread) {
      if (thread == null) {
         return false;
      } else {
         AtomicInteger stateRef = thread.stateRef;

         int oldVal;
         int newVal;
         do {
            for(oldVal = stateRef.get(); oldVal == 3; oldVal = stateRef.get()) {
               LockSupport.park();
            }

            if (oldVal != 0) {
               if (oldVal != 1 && oldVal != 2) {
                  throw Assert.unreachableCode();
               }

               return false;
            }

            newVal = Thread.interrupted() ? 1 : 2;
         } while(!stateRef.compareAndSet(oldVal, newVal));

         if (newVal == 1 && Thread.interrupted()) {
            stateRef.set(2);
         }

         return true;
      }
   }

   public void run() {
      Messages.msg.tracef("Thread \"%s\" starting execution", this);
      boolean var17 = false;

      try {
         var17 = true;
         super.run();
         var17 = false;
      } finally {
         if (var17) {
            Messages.msg.tracef("Thread \"%s\" exiting", this);
            List exitHandlers = this.exitHandlers;
            if (exitHandlers != null) {
               Iterator var8 = exitHandlers.iterator();

               while(var8.hasNext()) {
                  Runnable exitHandler = (Runnable)var8.next();

                  try {
                     exitHandler.run();
                  } catch (Throwable var19) {
                     Throwable t = var19;

                     try {
                        this.getUncaughtExceptionHandler().uncaughtException(this, t);
                     } catch (Throwable var18) {
                     }
                  }
               }
            }

         }
      }

      Messages.msg.tracef("Thread \"%s\" exiting", this);
      List<Runnable> exitHandlers = this.exitHandlers;
      if (exitHandlers != null) {
         Iterator var2 = exitHandlers.iterator();

         while(var2.hasNext()) {
            Runnable exitHandler = (Runnable)var2.next();

            try {
               exitHandler.run();
            } catch (Throwable var21) {
               Throwable t = var21;

               try {
                  this.getUncaughtExceptionHandler().uncaughtException(this, t);
               } catch (Throwable var20) {
               }
            }
         }
      }

   }

   public static boolean onExit(Runnable hook) throws SecurityException {
      SecurityManager sm = System.getSecurityManager();
      if (sm != null) {
         sm.checkPermission(MODIFY_THREAD_PERMISSION);
      }

      JBossThread thread = currentThread();
      if (thread != null && hook != null) {
         List<Runnable> exitHandlers = thread.exitHandlers;
         if (exitHandlers == null) {
            exitHandlers = new ArrayList();
            thread.exitHandlers = (List)exitHandlers;
         }

         ((List)exitHandlers).add(new ContextClassLoaderSavingRunnable(JBossExecutors.getContextClassLoader(thread), hook));
         return true;
      } else {
         return false;
      }
   }

   public static JBossThread currentThread() {
      Thread thread = Thread.currentThread();
      return thread instanceof JBossThread ? (JBossThread)thread : null;
   }

   public void start() {
      super.start();
      Messages.msg.tracef("Started thread \"%s\"", this);
   }

   public void setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler eh) {
      super.setUncaughtExceptionHandler(eh);
      Messages.msg.tracef("Changed uncaught exception handler for \"%s\" to %s", this, eh);
   }

   public static InterruptHandler getAndSetInterruptHandler(InterruptHandler newInterruptHandler) {
      JBossThread thread = currentThread();
      if (thread == null) {
         throw Messages.msg.noInterruptHandlers();
      } else {
         InterruptHandler var2;
         try {
            var2 = thread.interruptHandler;
         } finally {
            thread.interruptHandler = newInterruptHandler;
         }

         return var2;
      }
   }

   public static <T, U, R, E extends Exception> R applyWithInterruptHandler(InterruptHandler interruptHandler, ExceptionBiFunction<T, U, R, E> function, T param1, U param2) throws E {
      JBossThread thread = currentThread();
      if (thread == null) {
         return function.apply(param1, param2);
      } else {
         InterruptHandler old = thread.interruptHandler;
         thread.interruptHandler = interruptHandler;

         Object var6;
         try {
            var6 = function.apply(param1, param2);
         } finally {
            thread.interruptHandler = old;
         }

         return var6;
      }
   }

   public static <T, R, E extends Exception> R applyWithInterruptHandler(InterruptHandler interruptHandler, ExceptionFunction<T, R, E> function, T param1) throws E {
      return applyWithInterruptHandler(interruptHandler, Functions.exceptionFunctionBiFunction(), function, param1);
   }

   public static <R, E extends Exception> R getWithInterruptHandler(InterruptHandler interruptHandler, ExceptionSupplier<R, E> function) throws E {
      return applyWithInterruptHandler(interruptHandler, Functions.exceptionFunctionBiFunction(), Functions.exceptionSupplierFunction(), function);
   }

   public static <T, E extends Exception> void acceptWithInterruptHandler(InterruptHandler interruptHandler, ExceptionObjLongConsumer<T, E> function, T param1, long param2) throws E {
      JBossThread thread = currentThread();
      if (thread == null) {
         function.accept(param1, param2);
      } else {
         InterruptHandler old = thread.interruptHandler;
         thread.interruptHandler = interruptHandler;

         try {
            function.accept(param1, param2);
         } finally {
            thread.interruptHandler = old;
         }

      }
   }

   public static <T, E extends Exception> void acceptWithInterruptHandler(InterruptHandler interruptHandler, ExceptionObjIntConsumer<T, E> function, T param1, int param2) throws E {
      JBossThread thread = currentThread();
      if (thread == null) {
         function.accept(param1, param2);
      } else {
         InterruptHandler old = thread.interruptHandler;
         thread.interruptHandler = interruptHandler;

         try {
            function.accept(param1, param2);
         } finally {
            thread.interruptHandler = old;
         }

      }
   }

   public static <T, U, E extends Exception> void acceptWithInterruptHandler(InterruptHandler interruptHandler, ExceptionBiConsumer<T, U, E> function, T param1, U param2) throws E {
      JBossThread thread = currentThread();
      if (thread == null) {
         function.accept(param1, param2);
      } else {
         InterruptHandler old = thread.interruptHandler;
         thread.interruptHandler = interruptHandler;

         try {
            function.accept(param1, param2);
         } finally {
            thread.interruptHandler = old;
         }

      }
   }

   public static <T, E extends Exception> void acceptWithInterruptHandler(InterruptHandler interruptHandler, ExceptionConsumer<T, E> function, T param1) throws E {
      acceptWithInterruptHandler(interruptHandler, Functions.exceptionConsumerBiConsumer(), function, param1);
   }

   public static <E extends Exception> void runWithInterruptHandler(InterruptHandler interruptHandler, ExceptionRunnable<E> function) throws E {
      acceptWithInterruptHandler(interruptHandler, Functions.exceptionConsumerBiConsumer(), Functions.exceptionRunnableConsumer(), function);
   }

   ThreadNameInfo getThreadNameInfo() {
      return this.threadNameInfo;
   }

   void setThreadNameInfo(ThreadNameInfo threadNameInfo) throws SecurityException {
      this.checkAccess();
      this.threadNameInfo = threadNameInfo;
   }

   static {
      Version.getVersionString();
   }
}
