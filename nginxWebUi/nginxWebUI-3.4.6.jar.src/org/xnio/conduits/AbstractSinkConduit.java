/*    */ package org.xnio.conduits;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import org.xnio.XnioIoThread;
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
/*    */ 
/*    */ 
/*    */ public abstract class AbstractSinkConduit<D extends SinkConduit>
/*    */   extends AbstractConduit<D>
/*    */   implements SinkConduit
/*    */ {
/*    */   protected AbstractSinkConduit(D next) {
/* 38 */     super(next);
/*    */   }
/*    */   
/*    */   public void terminateWrites() throws IOException {
/* 42 */     ((SinkConduit)this.next).terminateWrites();
/*    */   }
/*    */   
/*    */   public boolean isWriteShutdown() {
/* 46 */     return ((SinkConduit)this.next).isWriteShutdown();
/*    */   }
/*    */   
/*    */   public void resumeWrites() {
/* 50 */     ((SinkConduit)this.next).resumeWrites();
/*    */   }
/*    */   
/*    */   public void suspendWrites() {
/* 54 */     ((SinkConduit)this.next).suspendWrites();
/*    */   }
/*    */   
/*    */   public void wakeupWrites() {
/* 58 */     ((SinkConduit)this.next).wakeupWrites();
/*    */   }
/*    */   
/*    */   public boolean isWriteResumed() {
/* 62 */     return ((SinkConduit)this.next).isWriteResumed();
/*    */   }
/*    */   
/*    */   public void awaitWritable() throws IOException {
/* 66 */     ((SinkConduit)this.next).awaitWritable();
/*    */   }
/*    */   
/*    */   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
/* 70 */     ((SinkConduit)this.next).awaitWritable(time, timeUnit);
/*    */   }
/*    */   
/*    */   public XnioIoThread getWriteThread() {
/* 74 */     return ((SinkConduit)this.next).getWriteThread();
/*    */   }
/*    */   
/*    */   public void setWriteReadyHandler(WriteReadyHandler handler) {
/* 78 */     ((SinkConduit)this.next).setWriteReadyHandler(handler);
/*    */   }
/*    */   
/*    */   public void truncateWrites() throws IOException {
/* 82 */     ((SinkConduit)this.next).truncateWrites();
/*    */   }
/*    */   
/*    */   public boolean flush() throws IOException {
/* 86 */     return ((SinkConduit)this.next).flush();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\AbstractSinkConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */