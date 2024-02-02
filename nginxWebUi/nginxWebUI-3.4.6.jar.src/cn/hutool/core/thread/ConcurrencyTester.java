/*    */ package cn.hutool.core.thread;
/*    */ 
/*    */ import cn.hutool.core.date.TimeInterval;
/*    */ import java.io.Closeable;
/*    */ import java.io.IOException;
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
/*    */ public class ConcurrencyTester
/*    */   implements Closeable
/*    */ {
/*    */   private final SyncFinisher sf;
/*    */   private final TimeInterval timeInterval;
/*    */   private long interval;
/*    */   
/*    */   public ConcurrencyTester(int threadSize) {
/* 35 */     this.sf = new SyncFinisher(threadSize);
/* 36 */     this.timeInterval = new TimeInterval();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ConcurrencyTester test(Runnable runnable) {
/* 47 */     this.sf.clearWorker();
/*    */     
/* 49 */     this.timeInterval.start();
/* 50 */     this.sf
/* 51 */       .addRepeatWorker(runnable)
/* 52 */       .setBeginAtSameTime(true)
/* 53 */       .start();
/*    */     
/* 55 */     this.interval = this.timeInterval.interval();
/* 56 */     return this;
/*    */   }
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
/*    */   public ConcurrencyTester reset() {
/* 71 */     this.sf.clearWorker();
/* 72 */     this.timeInterval.restart();
/* 73 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public long getInterval() {
/* 82 */     return this.interval;
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() throws IOException {
/* 87 */     this.sf.close();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\thread\ConcurrencyTester.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */