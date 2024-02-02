/*    */ package org.xnio.nio;
/*    */ 
/*    */ import java.nio.channels.CancelledKeyException;
/*    */ import java.nio.channels.SelectionKey;
/*    */ import org.xnio.Bits;
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
/*    */ abstract class NioHandle
/*    */ {
/*    */   private final WorkerThread workerThread;
/*    */   private final SelectionKey selectionKey;
/*    */   
/*    */   protected NioHandle(WorkerThread workerThread, SelectionKey selectionKey) {
/* 35 */     this.workerThread = workerThread;
/* 36 */     this.selectionKey = selectionKey;
/*    */   }
/*    */   
/*    */   void resume(int ops) {
/*    */     try {
/* 41 */       if (!Bits.allAreSet(this.selectionKey.interestOps(), ops)) {
/* 42 */         this.workerThread.setOps(this.selectionKey, ops);
/*    */       }
/* 44 */     } catch (CancelledKeyException cancelledKeyException) {}
/*    */   }
/*    */   
/*    */   void wakeup(final int ops) {
/* 48 */     this.workerThread.queueTask(new Runnable() {
/*    */           public void run() {
/* 50 */             NioHandle.this.handleReady(ops);
/*    */           }
/*    */         });
/*    */     try {
/* 54 */       if (!Bits.allAreSet(this.selectionKey.interestOps(), ops)) {
/* 55 */         this.workerThread.setOps(this.selectionKey, ops);
/*    */       }
/* 57 */     } catch (CancelledKeyException cancelledKeyException) {}
/*    */   }
/*    */   
/*    */   void suspend(int ops) {
/*    */     try {
/* 62 */       if (!Bits.allAreClear(this.selectionKey.interestOps(), ops)) {
/* 63 */         this.workerThread.clearOps(this.selectionKey, ops);
/*    */       }
/* 65 */     } catch (CancelledKeyException cancelledKeyException) {}
/*    */   }
/*    */   
/*    */   boolean isResumed(int ops) {
/*    */     try {
/* 70 */       return Bits.allAreSet(this.selectionKey.interestOps(), ops);
/* 71 */     } catch (CancelledKeyException ignored) {
/* 72 */       return false;
/*    */     } 
/*    */   }
/*    */   
/*    */   abstract void handleReady(int paramInt);
/*    */   
/*    */   abstract void forceTermination();
/*    */   
/*    */   abstract void terminated();
/*    */   
/*    */   WorkerThread getWorkerThread() {
/* 83 */     return this.workerThread;
/*    */   }
/*    */   
/*    */   SelectionKey getSelectionKey() {
/* 87 */     return this.selectionKey;
/*    */   }
/*    */   
/*    */   void cancelKey(boolean block) {
/* 91 */     this.workerThread.cancelKey(this.selectionKey, block);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\nio\NioHandle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */