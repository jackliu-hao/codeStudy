/*     */ package org.apache.http.client.methods;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.concurrent.atomic.AtomicMarkableReference;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.client.utils.CloneUtils;
/*     */ import org.apache.http.concurrent.Cancellable;
/*     */ import org.apache.http.conn.ClientConnectionRequest;
/*     */ import org.apache.http.conn.ConnectionReleaseTrigger;
/*     */ import org.apache.http.message.AbstractHttpMessage;
/*     */ import org.apache.http.message.HeaderGroup;
/*     */ import org.apache.http.params.HttpParams;
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
/*     */ public abstract class AbstractExecutionAwareRequest
/*     */   extends AbstractHttpMessage
/*     */   implements HttpExecutionAware, AbortableHttpRequest, Cloneable, HttpRequest
/*     */ {
/*  47 */   private final AtomicMarkableReference<Cancellable> cancellableRef = new AtomicMarkableReference<Cancellable>(null, false);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void setConnectionRequest(final ClientConnectionRequest connRequest) {
/*  56 */     setCancellable(new Cancellable()
/*     */         {
/*     */           public boolean cancel()
/*     */           {
/*  60 */             connRequest.abortRequest();
/*  61 */             return true;
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void setReleaseTrigger(final ConnectionReleaseTrigger releaseTrigger) {
/*  73 */     setCancellable(new Cancellable()
/*     */         {
/*     */           public boolean cancel()
/*     */           {
/*     */             try {
/*  78 */               releaseTrigger.abortConnection();
/*  79 */               return true;
/*  80 */             } catch (IOException ex) {
/*  81 */               return false;
/*     */             } 
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void abort() {
/*  90 */     while (!this.cancellableRef.isMarked()) {
/*  91 */       Cancellable actualCancellable = this.cancellableRef.getReference();
/*  92 */       if (this.cancellableRef.compareAndSet(actualCancellable, actualCancellable, false, true) && 
/*  93 */         actualCancellable != null) {
/*  94 */         actualCancellable.cancel();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAborted() {
/* 102 */     return this.cancellableRef.isMarked();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCancellable(Cancellable cancellable) {
/* 110 */     Cancellable actualCancellable = this.cancellableRef.getReference();
/* 111 */     if (!this.cancellableRef.compareAndSet(actualCancellable, cancellable, false, false)) {
/* 112 */       cancellable.cancel();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public Object clone() throws CloneNotSupportedException {
/* 118 */     AbstractExecutionAwareRequest clone = (AbstractExecutionAwareRequest)super.clone();
/* 119 */     clone.headergroup = (HeaderGroup)CloneUtils.cloneObject(this.headergroup);
/* 120 */     clone.params = (HttpParams)CloneUtils.cloneObject(this.params);
/* 121 */     return clone;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void completed() {
/* 131 */     this.cancellableRef.set(null, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reset() {
/*     */     boolean marked;
/*     */     Cancellable actualCancellable;
/*     */     do {
/* 141 */       marked = this.cancellableRef.isMarked();
/* 142 */       actualCancellable = this.cancellableRef.getReference();
/* 143 */       if (actualCancellable == null)
/* 144 */         continue;  actualCancellable.cancel();
/*     */     }
/* 146 */     while (!this.cancellableRef.compareAndSet(actualCancellable, null, marked, false));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\client\methods\AbstractExecutionAwareRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */