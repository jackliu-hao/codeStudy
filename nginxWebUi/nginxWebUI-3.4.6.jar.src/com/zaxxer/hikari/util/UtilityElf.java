/*     */ package com.zaxxer.hikari.util;
/*     */ 
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.util.Locale;
/*     */ import java.util.concurrent.BlockingQueue;
/*     */ import java.util.concurrent.LinkedBlockingQueue;
/*     */ import java.util.concurrent.RejectedExecutionHandler;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import java.util.concurrent.ThreadPoolExecutor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class UtilityElf
/*     */ {
/*     */   public static String getNullIfEmpty(String text) {
/*  43 */     return (text == null) ? null : (text.trim().isEmpty() ? null : text.trim());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void quietlySleep(long millis) {
/*     */     try {
/*  54 */       Thread.sleep(millis);
/*     */     }
/*  56 */     catch (InterruptedException e) {
/*     */       
/*  58 */       Thread.currentThread().interrupt();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean safeIsAssignableFrom(Object obj, String className) {
/*     */     try {
/*  70 */       Class<?> clazz = Class.forName(className);
/*  71 */       return clazz.isAssignableFrom(obj.getClass());
/*  72 */     } catch (ClassNotFoundException ignored) {
/*  73 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T createInstance(String className, Class<T> clazz, Object... args) {
/*  89 */     if (className == null) {
/*  90 */       return null;
/*     */     }
/*     */     
/*     */     try {
/*  94 */       Class<?> loaded = UtilityElf.class.getClassLoader().loadClass(className);
/*  95 */       if (args.length == 0) {
/*  96 */         return clazz.cast(loaded.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]));
/*     */       }
/*     */       
/*  99 */       Class<?>[] argClasses = new Class[args.length];
/* 100 */       for (int i = 0; i < args.length; i++) {
/* 101 */         argClasses[i] = args[i].getClass();
/*     */       }
/* 103 */       Constructor<?> constructor = loaded.getConstructor(argClasses);
/* 104 */       return clazz.cast(constructor.newInstance(args));
/*     */     }
/* 106 */     catch (Exception e) {
/* 107 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ThreadPoolExecutor createThreadPoolExecutor(int queueSize, String threadName, ThreadFactory threadFactory, RejectedExecutionHandler policy) {
/* 122 */     if (threadFactory == null) {
/* 123 */       threadFactory = new DefaultThreadFactory(threadName, true);
/*     */     }
/*     */     
/* 126 */     LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(queueSize);
/* 127 */     ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 5L, TimeUnit.SECONDS, queue, threadFactory, policy);
/* 128 */     executor.allowCoreThreadTimeOut(true);
/* 129 */     return executor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ThreadPoolExecutor createThreadPoolExecutor(BlockingQueue<Runnable> queue, String threadName, ThreadFactory threadFactory, RejectedExecutionHandler policy) {
/* 143 */     if (threadFactory == null) {
/* 144 */       threadFactory = new DefaultThreadFactory(threadName, true);
/*     */     }
/*     */     
/* 147 */     ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 5L, TimeUnit.SECONDS, queue, threadFactory, policy);
/* 148 */     executor.allowCoreThreadTimeOut(true);
/* 149 */     return executor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getTransactionIsolation(String transactionIsolationName) {
/* 164 */     if (transactionIsolationName != null) {
/*     */       
/*     */       try {
/* 167 */         String upperCaseIsolationLevelName = transactionIsolationName.toUpperCase(Locale.ENGLISH);
/* 168 */         return IsolationLevel.valueOf(upperCaseIsolationLevelName).getLevelId();
/* 169 */       } catch (IllegalArgumentException e) {
/*     */         
/*     */         try {
/* 172 */           int level = Integer.parseInt(transactionIsolationName);
/* 173 */           for (IsolationLevel iso : IsolationLevel.values()) {
/* 174 */             if (iso.getLevelId() == level) {
/* 175 */               return iso.getLevelId();
/*     */             }
/*     */           } 
/*     */           
/* 179 */           throw new IllegalArgumentException("Invalid transaction isolation value: " + transactionIsolationName);
/*     */         }
/* 181 */         catch (NumberFormatException nfe) {
/* 182 */           throw new IllegalArgumentException("Invalid transaction isolation value: " + transactionIsolationName, nfe);
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/* 187 */     return -1;
/*     */   }
/*     */   
/*     */   public static final class DefaultThreadFactory
/*     */     implements ThreadFactory {
/*     */     private final String threadName;
/*     */     private final boolean daemon;
/*     */     
/*     */     public DefaultThreadFactory(String threadName, boolean daemon) {
/* 196 */       this.threadName = threadName;
/* 197 */       this.daemon = daemon;
/*     */     }
/*     */ 
/*     */     
/*     */     public Thread newThread(Runnable r) {
/* 202 */       Thread thread = new Thread(r, this.threadName);
/* 203 */       thread.setDaemon(this.daemon);
/* 204 */       return thread;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\zaxxer\hikar\\util\UtilityElf.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */