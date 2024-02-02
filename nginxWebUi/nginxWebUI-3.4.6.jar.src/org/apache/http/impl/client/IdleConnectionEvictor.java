/*     */ package org.apache.http.impl.client;
/*     */ 
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.apache.http.conn.HttpClientConnectionManager;
/*     */ import org.apache.http.util.Args;
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
/*     */ public final class IdleConnectionEvictor
/*     */ {
/*     */   private final HttpClientConnectionManager connectionManager;
/*     */   private final ThreadFactory threadFactory;
/*     */   private final Thread thread;
/*     */   private final long sleepTimeMs;
/*     */   private final long maxIdleTimeMs;
/*     */   private volatile Exception exception;
/*     */   
/*     */   public IdleConnectionEvictor(final HttpClientConnectionManager connectionManager, ThreadFactory threadFactory, long sleepTime, TimeUnit sleepTimeUnit, long maxIdleTime, TimeUnit maxIdleTimeUnit) {
/*  57 */     this.connectionManager = (HttpClientConnectionManager)Args.notNull(connectionManager, "Connection manager");
/*  58 */     this.threadFactory = (threadFactory != null) ? threadFactory : new DefaultThreadFactory();
/*  59 */     this.sleepTimeMs = (sleepTimeUnit != null) ? sleepTimeUnit.toMillis(sleepTime) : sleepTime;
/*  60 */     this.maxIdleTimeMs = (maxIdleTimeUnit != null) ? maxIdleTimeUnit.toMillis(maxIdleTime) : maxIdleTime;
/*  61 */     this.thread = this.threadFactory.newThread(new Runnable()
/*     */         {
/*     */           public void run() {
/*     */             try {
/*  65 */               while (!Thread.currentThread().isInterrupted()) {
/*  66 */                 Thread.sleep(IdleConnectionEvictor.this.sleepTimeMs);
/*  67 */                 connectionManager.closeExpiredConnections();
/*  68 */                 if (IdleConnectionEvictor.this.maxIdleTimeMs > 0L) {
/*  69 */                   connectionManager.closeIdleConnections(IdleConnectionEvictor.this.maxIdleTimeMs, TimeUnit.MILLISECONDS);
/*     */                 }
/*     */               } 
/*  72 */             } catch (Exception ex) {
/*  73 */               IdleConnectionEvictor.this.exception = ex;
/*     */             } 
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IdleConnectionEvictor(HttpClientConnectionManager connectionManager, long sleepTime, TimeUnit sleepTimeUnit, long maxIdleTime, TimeUnit maxIdleTimeUnit) {
/*  84 */     this(connectionManager, null, sleepTime, sleepTimeUnit, maxIdleTime, maxIdleTimeUnit);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public IdleConnectionEvictor(HttpClientConnectionManager connectionManager, long maxIdleTime, TimeUnit maxIdleTimeUnit) {
/*  90 */     this(connectionManager, null, (maxIdleTime > 0L) ? maxIdleTime : 5L, (maxIdleTimeUnit != null) ? maxIdleTimeUnit : TimeUnit.SECONDS, maxIdleTime, maxIdleTimeUnit);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void start() {
/*  96 */     this.thread.start();
/*     */   }
/*     */   
/*     */   public void shutdown() {
/* 100 */     this.thread.interrupt();
/*     */   }
/*     */   
/*     */   public boolean isRunning() {
/* 104 */     return this.thread.isAlive();
/*     */   }
/*     */   
/*     */   public void awaitTermination(long time, TimeUnit timeUnit) throws InterruptedException {
/* 108 */     this.thread.join(((timeUnit != null) ? timeUnit : TimeUnit.MILLISECONDS).toMillis(time));
/*     */   }
/*     */   
/*     */   static class DefaultThreadFactory
/*     */     implements ThreadFactory
/*     */   {
/*     */     public Thread newThread(Runnable r) {
/* 115 */       Thread t = new Thread(r, "Connection evictor");
/* 116 */       t.setDaemon(true);
/* 117 */       return t;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\client\IdleConnectionEvictor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */