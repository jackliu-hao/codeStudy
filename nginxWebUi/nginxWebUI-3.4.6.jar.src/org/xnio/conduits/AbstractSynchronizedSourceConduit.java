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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbstractSynchronizedSourceConduit<D extends SourceConduit>
/*     */   extends AbstractSynchronizedConduit<D>
/*     */   implements SourceConduit
/*     */ {
/*     */   protected AbstractSynchronizedSourceConduit(D next) {
/*  40 */     super(next);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected AbstractSynchronizedSourceConduit(D next, Object lock) {
/*  50 */     super(next, lock);
/*     */   }
/*     */   
/*     */   public void terminateReads() throws IOException {
/*  54 */     synchronized (this.lock) {
/*  55 */       ((SourceConduit)this.next).terminateReads();
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isReadShutdown() {
/*  60 */     synchronized (this.lock) {
/*  61 */       return ((SourceConduit)this.next).isReadShutdown();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void resumeReads() {
/*  66 */     synchronized (this.lock) {
/*  67 */       ((SourceConduit)this.next).resumeReads();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void suspendReads() {
/*  72 */     synchronized (this.lock) {
/*  73 */       ((SourceConduit)this.next).suspendReads();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void wakeupReads() {
/*  78 */     synchronized (this.lock) {
/*  79 */       ((SourceConduit)this.next).wakeupReads();
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isReadResumed() {
/*  84 */     synchronized (this.lock) {
/*  85 */       return ((SourceConduit)this.next).isReadResumed();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void awaitReadable() throws IOException {
/*  90 */     synchronized (this.lock) {
/*  91 */       ((SourceConduit)this.next).awaitReadable();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
/*  96 */     synchronized (this.lock) {
/*  97 */       ((SourceConduit)this.next).awaitReadable(time, timeUnit);
/*     */     } 
/*     */   }
/*     */   
/*     */   public XnioIoThread getReadThread() {
/* 102 */     return ((SourceConduit)this.next).getReadThread();
/*     */   }
/*     */   
/*     */   public void setReadReadyHandler(ReadReadyHandler handler) {
/* 106 */     synchronized (this.lock) {
/* 107 */       ((SourceConduit)this.next).setReadReadyHandler(handler);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\AbstractSynchronizedSourceConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */