package org.jboss.threads;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public final class JBossThreadFactory implements ThreadFactory {
   private final ThreadGroup threadGroup;
   private final Boolean daemon;
   private final Integer initialPriority;
   private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
   private final Long stackSize;
   private final String namePattern;
   private final AtomicLong factoryThreadIndexSequence;
   private final long factoryIndex;
   private final AccessControlContext creatingContext;
   private static final AtomicLong globalThreadIndexSequence = new AtomicLong(1L);
   private static final AtomicLong factoryIndexSequence = new AtomicLong(1L);

   public JBossThreadFactory(ThreadGroup threadGroup, Boolean daemon, Integer initialPriority, String namePattern, Thread.UncaughtExceptionHandler uncaughtExceptionHandler, Long stackSize) {
      this.factoryThreadIndexSequence = new AtomicLong(1L);
      if (threadGroup == null) {
         SecurityManager sm = System.getSecurityManager();
         threadGroup = sm != null ? sm.getThreadGroup() : Thread.currentThread().getThreadGroup();
      }

      this.threadGroup = threadGroup;
      this.daemon = daemon;
      this.initialPriority = initialPriority;
      this.uncaughtExceptionHandler = uncaughtExceptionHandler;
      this.stackSize = stackSize;
      this.factoryIndex = factoryIndexSequence.getAndIncrement();
      if (namePattern == null) {
         namePattern = "pool-%f-thread-%t";
      }

      this.namePattern = namePattern;
      this.creatingContext = AccessController.getContext();
   }

   /** @deprecated */
   public JBossThreadFactory(ThreadGroup threadGroup, Boolean daemon, Integer initialPriority, String namePattern, Thread.UncaughtExceptionHandler uncaughtExceptionHandler, Long stackSize, AccessControlContext ignored) {
      this(threadGroup, daemon, initialPriority, namePattern, uncaughtExceptionHandler, stackSize);
   }

   public Thread newThread(Runnable target) {
      AccessControlContext context;
      return (context = this.creatingContext) != null ? (Thread)AccessController.doPrivileged(new ThreadCreateAction(target), context) : this.createThread(target);
   }

   private Thread createThread(Runnable target) {
      ThreadNameInfo nameInfo = new ThreadNameInfo(globalThreadIndexSequence.getAndIncrement(), this.factoryThreadIndexSequence.getAndIncrement(), this.factoryIndex);
      JBossThread thread;
      if (this.stackSize != null) {
         thread = new JBossThread(this.threadGroup, target, "<new>", this.stackSize);
      } else {
         thread = new JBossThread(this.threadGroup, target);
      }

      thread.setThreadNameInfo(nameInfo);
      thread.setName(nameInfo.format(thread, this.namePattern));
      if (this.initialPriority != null) {
         thread.setPriority(this.initialPriority);
      }

      if (this.daemon != null) {
         thread.setDaemon(this.daemon);
      }

      if (this.uncaughtExceptionHandler != null) {
         thread.setUncaughtExceptionHandler(this.uncaughtExceptionHandler);
      }

      JBossExecutors.clearContextClassLoader(thread);
      return thread;
   }

   private final class ThreadCreateAction implements PrivilegedAction<Thread> {
      private final Runnable target;

      private ThreadCreateAction(Runnable target) {
         this.target = target;
      }

      public Thread run() {
         return JBossThreadFactory.this.createThread(this.target);
      }

      // $FF: synthetic method
      ThreadCreateAction(Runnable x1, Object x2) {
         this(x1);
      }
   }
}
