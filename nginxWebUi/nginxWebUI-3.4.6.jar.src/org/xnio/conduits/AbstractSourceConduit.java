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
/*    */ public abstract class AbstractSourceConduit<D extends SourceConduit>
/*    */   extends AbstractConduit<D>
/*    */   implements SourceConduit
/*    */ {
/*    */   protected AbstractSourceConduit(D next) {
/* 38 */     super(next);
/*    */   }
/*    */   
/*    */   public void terminateReads() throws IOException {
/* 42 */     ((SourceConduit)this.next).terminateReads();
/*    */   }
/*    */   
/*    */   public boolean isReadShutdown() {
/* 46 */     return ((SourceConduit)this.next).isReadShutdown();
/*    */   }
/*    */   
/*    */   public void resumeReads() {
/* 50 */     ((SourceConduit)this.next).resumeReads();
/*    */   }
/*    */   
/*    */   public void suspendReads() {
/* 54 */     ((SourceConduit)this.next).suspendReads();
/*    */   }
/*    */   
/*    */   public void wakeupReads() {
/* 58 */     ((SourceConduit)this.next).wakeupReads();
/*    */   }
/*    */   
/*    */   public boolean isReadResumed() {
/* 62 */     return ((SourceConduit)this.next).isReadResumed();
/*    */   }
/*    */   
/*    */   public void awaitReadable() throws IOException {
/* 66 */     ((SourceConduit)this.next).awaitReadable();
/*    */   }
/*    */   
/*    */   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
/* 70 */     ((SourceConduit)this.next).awaitReadable(time, timeUnit);
/*    */   }
/*    */   
/*    */   public XnioIoThread getReadThread() {
/* 74 */     return ((SourceConduit)this.next).getReadThread();
/*    */   }
/*    */   
/*    */   public void setReadReadyHandler(ReadReadyHandler handler) {
/* 78 */     ((SourceConduit)this.next).setReadReadyHandler(handler);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\AbstractSourceConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */