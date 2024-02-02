/*    */ package cn.hutool.core.thread;
/*    */ 
/*    */ import java.util.concurrent.Semaphore;
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
/*    */ public class SemaphoreRunnable
/*    */   implements Runnable
/*    */ {
/*    */   private final Runnable runnable;
/*    */   private final Semaphore semaphore;
/*    */   
/*    */   public SemaphoreRunnable(Runnable runnable, Semaphore semaphore) {
/* 30 */     this.runnable = runnable;
/* 31 */     this.semaphore = semaphore;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Semaphore getSemaphore() {
/* 41 */     return this.semaphore;
/*    */   }
/*    */ 
/*    */   
/*    */   public void run() {
/* 46 */     if (null != this.semaphore)
/*    */       try {
/* 48 */         this.semaphore.acquire();
/*    */         try {
/* 50 */           this.runnable.run();
/*    */         } finally {
/* 52 */           this.semaphore.release();
/*    */         } 
/* 54 */       } catch (InterruptedException e) {
/* 55 */         Thread.currentThread().interrupt();
/*    */       }  
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\thread\SemaphoreRunnable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */