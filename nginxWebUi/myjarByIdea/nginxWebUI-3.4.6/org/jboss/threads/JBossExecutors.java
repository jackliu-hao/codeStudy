package org.jboss.threads;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import org.jboss.logging.Logger;
import org.wildfly.common.Assert;
import sun.misc.Unsafe;

public final class JBossExecutors {
   private static final Logger THREAD_ERROR_LOGGER = Logger.getLogger("org.jboss.threads.errors");
   private static final RuntimePermission COPY_CONTEXT_CLASSLOADER_PERMISSION = new RuntimePermission("copyClassLoader");
   private static final ExecutorService REJECTING_EXECUTOR_SERVICE;
   private static final ExecutorService DISCARDING_EXECUTOR_SERVICE;
   private static final RejectedExecutionHandler ABORT_POLICY;
   private static final RejectedExecutionHandler CALLER_RUNS_POLICY;
   private static final RejectedExecutionHandler DISCARD_OLDEST_POLICY;
   private static final RejectedExecutionHandler DISCARD_POLICY;
   private static final Runnable TCCL_RESETTER;
   private static final Runnable NULL_RUNNABLE;
   static final ClassLoader SAFE_CL;
   static final Unsafe unsafe;
   static final long contextClassLoaderOffs;
   private static final Thread.UncaughtExceptionHandler LOGGING_HANDLER;

   private JBossExecutors() {
   }

   public static Executor directExecutor() {
      return SimpleDirectExecutor.INSTANCE;
   }

   public static Executor rejectingExecutor() {
      return RejectingExecutor.INSTANCE;
   }

   public static Executor rejectingExecutor(String message) {
      return new RejectingExecutor(message);
   }

   public static ExecutorService rejectingExecutorService() {
      return REJECTING_EXECUTOR_SERVICE;
   }

   public static ExecutorService rejectingExecutorService(String message) {
      return protectedExecutorService(rejectingExecutor(message));
   }

   public static Executor discardingExecutor() {
      return DiscardingExecutor.INSTANCE;
   }

   public static ExecutorService discardingExecutorService() {
      return DISCARDING_EXECUTOR_SERVICE;
   }

   public static Executor contextClassLoaderExecutor(Executor delegate, final ClassLoader taskClassLoader) {
      return new DelegatingExecutor(delegate) {
         public void execute(Runnable command) {
            super.execute(new ContextClassLoaderSavingRunnable(taskClassLoader, command));
         }
      };
   }

   public static RejectedExecutionHandler abortPolicy() {
      return ABORT_POLICY;
   }

   public static RejectedExecutionHandler callerRunsPolicy() {
      return CALLER_RUNS_POLICY;
   }

   public static RejectedExecutionHandler discardOldestPolicy() {
      return DISCARD_OLDEST_POLICY;
   }

   public static RejectedExecutionHandler discardPolicy() {
      return DISCARD_POLICY;
   }

   public static RejectedExecutionHandler handoffPolicy(Executor target) {
      return new HandoffRejectedExecutionHandler(target);
   }

   public static ExecutorService protectedExecutorService(Executor target) {
      return new DelegatingExecutorService(target);
   }

   public static ScheduledExecutorService protectedScheduledExecutorService(ScheduledExecutorService target) {
      return new DelegatingScheduledExecutorService(target);
   }

   public static ThreadFactory resettingThreadFactory(final ThreadFactory delegate) throws SecurityException {
      return new ThreadFactory() {
         public Thread newThread(Runnable r) {
            return delegate.newThread(new ThreadLocalResettingRunnable(r));
         }
      };
   }

   public static Runnable nullRunnable() {
      return NULL_RUNNABLE;
   }

   public static Runnable contextClassLoaderResetter() {
      return TCCL_RESETTER;
   }

   public static Runnable classLoaderPreservingTask(Runnable delegate) throws SecurityException {
      SecurityManager sm = System.getSecurityManager();
      if (sm != null) {
         sm.checkPermission(COPY_CONTEXT_CLASSLOADER_PERMISSION);
      }

      return classLoaderPreservingTaskUnchecked(delegate);
   }

   static Runnable classLoaderPreservingTaskUnchecked(Runnable delegate) {
      Assert.checkNotNullParam("delegate", delegate);
      return new ContextClassLoaderSavingRunnable(getContextClassLoader(Thread.currentThread()), delegate);
   }

   static ClassLoader getContextClassLoader(Thread thread) {
      return (ClassLoader)unsafe.getObject(thread, contextClassLoaderOffs);
   }

   static ClassLoader getAndSetContextClassLoader(Thread thread, ClassLoader newClassLoader) {
      ClassLoader var2;
      try {
         var2 = getContextClassLoader(thread);
      } finally {
         setContextClassLoader(thread, newClassLoader);
      }

      return var2;
   }

   static void setContextClassLoader(Thread thread, ClassLoader classLoader) {
      unsafe.putObject(thread, contextClassLoaderOffs, classLoader);
   }

   static void clearContextClassLoader(Thread thread) {
      unsafe.putObject(thread, contextClassLoaderOffs, SAFE_CL);
   }

   public static Thread.UncaughtExceptionHandler loggingExceptionHandler(Logger log) {
      return new LoggingUncaughtExceptionHandler(log);
   }

   public static Thread.UncaughtExceptionHandler loggingExceptionHandler(String categoryName) {
      return new LoggingUncaughtExceptionHandler(Logger.getLogger(categoryName));
   }

   public static Thread.UncaughtExceptionHandler loggingExceptionHandler() {
      return LOGGING_HANDLER;
   }

   static {
      REJECTING_EXECUTOR_SERVICE = new DelegatingExecutorService(RejectingExecutor.INSTANCE);
      DISCARDING_EXECUTOR_SERVICE = new DelegatingExecutorService(DiscardingExecutor.INSTANCE);
      ABORT_POLICY = new ThreadPoolExecutor.AbortPolicy();
      CALLER_RUNS_POLICY = new ThreadPoolExecutor.CallerRunsPolicy();
      DISCARD_OLDEST_POLICY = new ThreadPoolExecutor.DiscardOldestPolicy();
      DISCARD_POLICY = new ThreadPoolExecutor.DiscardPolicy();
      TCCL_RESETTER = new Runnable() {
         public void run() {
            Thread.currentThread().setContextClassLoader((ClassLoader)null);
         }

         public String toString() {
            return "ContextClassLoader-resetting Runnable";
         }
      };
      NULL_RUNNABLE = NullRunnable.getInstance();
      ClassLoader safeClassLoader = JBossExecutors.class.getClassLoader();
      if (safeClassLoader == null) {
         safeClassLoader = ClassLoader.getSystemClassLoader();
      }

      if (safeClassLoader == null) {
         safeClassLoader = new ClassLoader() {
         };
      }

      SAFE_CL = safeClassLoader;
      unsafe = (Unsafe)AccessController.doPrivileged(new PrivilegedAction<Unsafe>() {
         public Unsafe run() {
            try {
               Field field = Unsafe.class.getDeclaredField("theUnsafe");
               field.setAccessible(true);
               return (Unsafe)field.get((Object)null);
            } catch (IllegalAccessException var2) {
               throw new IllegalAccessError(var2.getMessage());
            } catch (NoSuchFieldException var3) {
               throw new NoSuchFieldError(var3.getMessage());
            }
         }
      });

      try {
         contextClassLoaderOffs = unsafe.objectFieldOffset(Thread.class.getDeclaredField("contextClassLoader"));
      } catch (NoSuchFieldException var1) {
         throw new NoSuchFieldError(var1.getMessage());
      }

      LOGGING_HANDLER = loggingExceptionHandler(THREAD_ERROR_LOGGER);
   }
}
