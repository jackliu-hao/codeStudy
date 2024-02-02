/*    */ package io.undertow.util;
/*    */ 
/*    */ import io.undertow.UndertowLogger;
/*    */ import java.util.LinkedList;
/*    */ import java.util.List;
/*    */ import java.util.concurrent.Executor;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Deprecated
/*    */ public class PipeliningExecutor
/*    */   implements Executor
/*    */ {
/*    */   private final Executor executor;
/* 37 */   private static final ThreadLocal<LinkedList<Runnable>> THREAD_QUEUE = new ThreadLocal<>();
/*    */   
/*    */   public PipeliningExecutor(Executor executor) {
/* 40 */     this.executor = executor;
/*    */   }
/*    */ 
/*    */   
/*    */   public void execute(final Runnable command) {
/* 45 */     List<Runnable> queue = THREAD_QUEUE.get();
/* 46 */     if (queue != null) {
/* 47 */       queue.add(command);
/*    */     } else {
/* 49 */       this.executor.execute(new Runnable()
/*    */           {
/*    */             public void run() {
/* 52 */               LinkedList<Runnable> queue = PipeliningExecutor.THREAD_QUEUE.get();
/* 53 */               if (queue == null) {
/* 54 */                 PipeliningExecutor.THREAD_QUEUE.set(queue = new LinkedList<>());
/*    */               }
/*    */               try {
/* 57 */                 command.run();
/* 58 */               } catch (Throwable t) {
/* 59 */                 UndertowLogger.REQUEST_LOGGER.debugf(t, "Task %s failed", command);
/*    */               } 
/* 61 */               Runnable runnable = queue.poll();
/* 62 */               while (runnable != null) {
/*    */                 try {
/* 64 */                   runnable.run();
/* 65 */                 } catch (Throwable t) {
/* 66 */                   UndertowLogger.REQUEST_LOGGER.debugf(t, "Task %s failed", command);
/*    */                 } 
/* 68 */                 runnable = queue.poll();
/*    */               } 
/*    */             }
/*    */           });
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\PipeliningExecutor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */