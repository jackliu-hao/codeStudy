/*    */ package io.undertow.server.protocol.framed;
/*    */ 
/*    */ import java.util.concurrent.Executor;
/*    */ import java.util.concurrent.Executors;
/*    */ import java.util.concurrent.LinkedBlockingQueue;
/*    */ import java.util.concurrent.ThreadFactory;
/*    */ import java.util.concurrent.ThreadPoolExecutor;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import java.util.concurrent.atomic.AtomicLong;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class ShutdownFallbackExecutor
/*    */ {
/* 33 */   private static volatile Executor EXECUTOR = null;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static void execute(Runnable runnable) {
/* 39 */     if (EXECUTOR == null) {
/* 40 */       synchronized (ShutdownFallbackExecutor.class) {
/* 41 */         if (EXECUTOR == null) {
/* 42 */           EXECUTOR = new ThreadPoolExecutor(0, 1, 10L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new ShutdownFallbackThreadFactory());
/*    */         }
/*    */       } 
/*    */     }
/*    */ 
/*    */ 
/*    */     
/* 49 */     EXECUTOR.execute(runnable);
/*    */   }
/*    */   
/*    */   static final class ShutdownFallbackThreadFactory implements ThreadFactory {
/* 53 */     private final AtomicLong count = new AtomicLong();
/* 54 */     private final ThreadFactory threadFactory = Executors.defaultThreadFactory();
/*    */ 
/*    */     
/*    */     public Thread newThread(Runnable r) {
/* 58 */       Thread thread = this.threadFactory.newThread(r);
/* 59 */       thread.setName("undertow-shutdown-" + this.count.getAndIncrement());
/* 60 */       thread.setDaemon(true);
/* 61 */       return thread;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\protocol\framed\ShutdownFallbackExecutor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */