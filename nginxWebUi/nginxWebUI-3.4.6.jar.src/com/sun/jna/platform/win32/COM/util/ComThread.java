/*     */ package com.sun.jna.platform.win32.COM.util;
/*     */ 
/*     */ import com.sun.jna.platform.win32.COM.COMUtils;
/*     */ import com.sun.jna.platform.win32.Ole32;
/*     */ import com.sun.jna.platform.win32.WinNT;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.TimeoutException;
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
/*     */ public class ComThread
/*     */ {
/*  40 */   private static ThreadLocal<Boolean> isCOMThread = new ThreadLocal<Boolean>();
/*     */   
/*     */   ExecutorService executor;
/*     */   Runnable firstTask;
/*     */   boolean requiresInitialisation;
/*     */   long timeoutMilliseconds;
/*     */   Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
/*     */   
/*     */   public ComThread(String threadName, long timeoutMilliseconds, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
/*  49 */     this(threadName, timeoutMilliseconds, uncaughtExceptionHandler, 0);
/*     */   }
/*     */   
/*     */   public ComThread(final String threadName, long timeoutMilliseconds, Thread.UncaughtExceptionHandler uncaughtExceptionHandler, final int coinitialiseExFlag) {
/*  53 */     this.requiresInitialisation = true;
/*  54 */     this.timeoutMilliseconds = timeoutMilliseconds;
/*  55 */     this.uncaughtExceptionHandler = uncaughtExceptionHandler;
/*  56 */     this.firstTask = new Runnable()
/*     */       {
/*     */ 
/*     */ 
/*     */         
/*     */         public void run()
/*     */         {
/*     */           try {
/*  64 */             WinNT.HRESULT hr = Ole32.INSTANCE.CoInitializeEx(null, coinitialiseExFlag);
/*  65 */             ComThread.isCOMThread.set(Boolean.valueOf(true));
/*  66 */             COMUtils.checkRC(hr);
/*  67 */             ComThread.this.requiresInitialisation = false;
/*  68 */           } catch (Throwable t) {
/*  69 */             ComThread.this.uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), t);
/*     */           } 
/*     */         }
/*     */       };
/*  73 */     this.executor = Executors.newSingleThreadExecutor(new ThreadFactory()
/*     */         {
/*     */           public Thread newThread(Runnable r)
/*     */           {
/*  77 */             if (!ComThread.this.requiresInitialisation)
/*     */             {
/*  79 */               throw new RuntimeException("ComThread executor has a problem.");
/*     */             }
/*  81 */             Thread thread = new Thread(r, threadName);
/*     */ 
/*     */             
/*  84 */             thread.setDaemon(true);
/*     */             
/*  86 */             thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler()
/*     */                 {
/*     */                   public void uncaughtException(Thread t, Throwable e) {
/*  89 */                     ComThread.this.requiresInitialisation = true;
/*  90 */                     ComThread.this.uncaughtExceptionHandler.uncaughtException(t, e);
/*     */                   }
/*     */                 });
/*     */             
/*  94 */             return thread;
/*     */           }
/*     */         });
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
/*     */   public void terminate(long timeoutMilliseconds) {
/*     */     try {
/* 110 */       this.executor.submit(new Runnable()
/*     */           {
/*     */             public void run() {
/* 113 */               Ole32.INSTANCE.CoUninitialize();
/*     */             }
/* 115 */           }).get(timeoutMilliseconds, TimeUnit.MILLISECONDS);
/*     */       
/* 117 */       this.executor.shutdown();
/*     */     }
/* 119 */     catch (InterruptedException e) {
/* 120 */       e.printStackTrace();
/* 121 */     } catch (ExecutionException e) {
/* 122 */       e.printStackTrace();
/* 123 */     } catch (TimeoutException e) {
/* 124 */       this.executor.shutdownNow();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void finalize() throws Throwable {
/* 130 */     if (!this.executor.isShutdown()) {
/* 131 */       terminate(100L);
/*     */     }
/*     */   }
/*     */   
/*     */   static void setComThread(boolean value) {
/* 136 */     isCOMThread.set(Boolean.valueOf(value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T execute(Callable<T> task) throws TimeoutException, InterruptedException, ExecutionException {
/* 143 */     Boolean comThread = isCOMThread.get();
/* 144 */     if (comThread == null) {
/* 145 */       comThread = Boolean.valueOf(false);
/*     */     }
/* 147 */     if (comThread.booleanValue()) {
/*     */       try {
/* 149 */         return task.call();
/* 150 */       } catch (Exception ex) {
/* 151 */         throw new ExecutionException(ex);
/*     */       } 
/*     */     }
/* 154 */     if (this.requiresInitialisation) {
/* 155 */       this.executor.execute(this.firstTask);
/*     */     }
/* 157 */     return this.executor.<T>submit(task).get(this.timeoutMilliseconds, TimeUnit.MILLISECONDS);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\CO\\util\ComThread.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */