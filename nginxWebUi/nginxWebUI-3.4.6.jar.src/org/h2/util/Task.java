/*     */ package org.h2.util;
/*     */ 
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class Task
/*     */   implements Runnable
/*     */ {
/*  16 */   private static final AtomicInteger counter = new AtomicInteger();
/*     */ 
/*     */ 
/*     */   
/*     */   public volatile boolean stop;
/*     */ 
/*     */ 
/*     */   
/*     */   private volatile Object result;
/*     */ 
/*     */ 
/*     */   
/*     */   private volatile boolean finished;
/*     */ 
/*     */   
/*     */   private Thread thread;
/*     */ 
/*     */   
/*     */   private volatile Exception ex;
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void call() throws Exception;
/*     */ 
/*     */ 
/*     */   
/*     */   public void run() {
/*     */     try {
/*  44 */       call();
/*  45 */     } catch (Exception exception) {
/*  46 */       this.ex = exception;
/*     */     } 
/*  48 */     this.finished = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Task execute() {
/*  57 */     return execute(getClass().getName() + ":" + counter.getAndIncrement());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Task execute(String paramString) {
/*  67 */     this.thread = new Thread(this, paramString);
/*  68 */     this.thread.setDaemon(true);
/*  69 */     this.thread.start();
/*  70 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object get() {
/*  81 */     Exception exception = getException();
/*  82 */     if (exception != null) {
/*  83 */       throw new RuntimeException(exception);
/*     */     }
/*  85 */     return this.result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isFinished() {
/*  94 */     return this.finished;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Exception getException() {
/* 103 */     join();
/* 104 */     if (this.ex != null) {
/* 105 */       return this.ex;
/*     */     }
/* 107 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void join() {
/* 115 */     this.stop = true;
/* 116 */     if (this.thread == null) {
/* 117 */       throw new IllegalStateException("Thread not started");
/*     */     }
/*     */     try {
/* 120 */       this.thread.join();
/* 121 */     } catch (InterruptedException interruptedException) {}
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\Task.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */