/*     */ package org.xnio.conduits;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.XnioIoThread;
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
/*     */ public abstract class AbstractSynchronizedSinkConduit<D extends SinkConduit>
/*     */   extends AbstractSynchronizedConduit<D>
/*     */   implements SinkConduit
/*     */ {
/*     */   protected AbstractSynchronizedSinkConduit(D next) {
/*  32 */     super(next);
/*     */   }
/*     */   
/*     */   protected AbstractSynchronizedSinkConduit(D next, Object lock) {
/*  36 */     super(next, lock);
/*     */   }
/*     */   
/*     */   public void terminateWrites() throws IOException {
/*  40 */     synchronized (this.lock) {
/*  41 */       ((SinkConduit)this.next).terminateWrites();
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isWriteShutdown() {
/*  46 */     synchronized (this.lock) {
/*  47 */       return ((SinkConduit)this.next).isWriteShutdown();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void resumeWrites() {
/*  52 */     synchronized (this.lock) {
/*  53 */       ((SinkConduit)this.next).resumeWrites();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void suspendWrites() {
/*  58 */     synchronized (this.lock) {
/*  59 */       ((SinkConduit)this.next).suspendWrites();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void wakeupWrites() {
/*  64 */     synchronized (this.lock) {
/*  65 */       ((SinkConduit)this.next).wakeupWrites();
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isWriteResumed() {
/*  70 */     synchronized (this.lock) {
/*  71 */       return ((SinkConduit)this.next).isWriteResumed();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void awaitWritable() throws IOException {
/*  76 */     synchronized (this.lock) {
/*  77 */       ((SinkConduit)this.next).awaitWritable();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
/*  82 */     synchronized (this.lock) {
/*  83 */       ((SinkConduit)this.next).awaitWritable(time, timeUnit);
/*     */     } 
/*     */   }
/*     */   
/*     */   public XnioIoThread getWriteThread() {
/*  88 */     return ((SinkConduit)this.next).getWriteThread();
/*     */   }
/*     */   
/*     */   public void setWriteReadyHandler(WriteReadyHandler handler) {
/*  92 */     synchronized (this.lock) {
/*  93 */       ((SinkConduit)this.next).setWriteReadyHandler(handler);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void truncateWrites() throws IOException {
/*  98 */     synchronized (this.lock) {
/*  99 */       ((SinkConduit)this.next).truncateWrites();
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean flush() throws IOException {
/* 104 */     synchronized (this.lock) {
/* 105 */       return ((SinkConduit)this.next).flush();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\AbstractSynchronizedSinkConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */