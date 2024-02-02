package com.zaxxer.hikari.util;

import java.lang.reflect.Constructor;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class UtilityElf {
   private UtilityElf() {
   }

   public static String getNullIfEmpty(String text) {
      return text == null ? null : (text.trim().isEmpty() ? null : text.trim());
   }

   public static void quietlySleep(long millis) {
      try {
         Thread.sleep(millis);
      } catch (InterruptedException var3) {
         Thread.currentThread().interrupt();
      }

   }

   public static boolean safeIsAssignableFrom(Object obj, String className) {
      try {
         Class<?> clazz = Class.forName(className);
         return clazz.isAssignableFrom(obj.getClass());
      } catch (ClassNotFoundException var3) {
         return false;
      }
   }

   public static <T> T createInstance(String className, Class<T> clazz, Object... args) {
      if (className == null) {
         return null;
      } else {
         try {
            Class<?> loaded = UtilityElf.class.getClassLoader().loadClass(className);
            if (args.length == 0) {
               return clazz.cast(loaded.getDeclaredConstructor().newInstance());
            } else {
               Class<?>[] argClasses = new Class[args.length];

               for(int i = 0; i < args.length; ++i) {
                  argClasses[i] = args[i].getClass();
               }

               Constructor<?> constructor = loaded.getConstructor(argClasses);
               return clazz.cast(constructor.newInstance(args));
            }
         } catch (Exception var6) {
            throw new RuntimeException(var6);
         }
      }
   }

   public static ThreadPoolExecutor createThreadPoolExecutor(int queueSize, String threadName, ThreadFactory threadFactory, RejectedExecutionHandler policy) {
      if (threadFactory == null) {
         threadFactory = new DefaultThreadFactory(threadName, true);
      }

      LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue(queueSize);
      ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 5L, TimeUnit.SECONDS, queue, (ThreadFactory)threadFactory, policy);
      executor.allowCoreThreadTimeOut(true);
      return executor;
   }

   public static ThreadPoolExecutor createThreadPoolExecutor(BlockingQueue<Runnable> queue, String threadName, ThreadFactory threadFactory, RejectedExecutionHandler policy) {
      if (threadFactory == null) {
         threadFactory = new DefaultThreadFactory(threadName, true);
      }

      ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 5L, TimeUnit.SECONDS, queue, (ThreadFactory)threadFactory, policy);
      executor.allowCoreThreadTimeOut(true);
      return executor;
   }

   public static int getTransactionIsolation(String transactionIsolationName) {
      if (transactionIsolationName != null) {
         try {
            String upperCaseIsolationLevelName = transactionIsolationName.toUpperCase(Locale.ENGLISH);
            return IsolationLevel.valueOf(upperCaseIsolationLevelName).getLevelId();
         } catch (IllegalArgumentException var8) {
            try {
               int level = Integer.parseInt(transactionIsolationName);
               IsolationLevel[] var3 = IsolationLevel.values();
               int var4 = var3.length;

               for(int var5 = 0; var5 < var4; ++var5) {
                  IsolationLevel iso = var3[var5];
                  if (iso.getLevelId() == level) {
                     return iso.getLevelId();
                  }
               }

               throw new IllegalArgumentException("Invalid transaction isolation value: " + transactionIsolationName);
            } catch (NumberFormatException var7) {
               throw new IllegalArgumentException("Invalid transaction isolation value: " + transactionIsolationName, var7);
            }
         }
      } else {
         return -1;
      }
   }

   public static final class DefaultThreadFactory implements ThreadFactory {
      private final String threadName;
      private final boolean daemon;

      public DefaultThreadFactory(String threadName, boolean daemon) {
         this.threadName = threadName;
         this.daemon = daemon;
      }

      public Thread newThread(Runnable r) {
         Thread thread = new Thread(r, this.threadName);
         thread.setDaemon(this.daemon);
         return thread;
      }
   }
}
