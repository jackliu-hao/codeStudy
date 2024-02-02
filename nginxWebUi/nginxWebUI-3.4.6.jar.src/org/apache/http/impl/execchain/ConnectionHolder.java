/*     */ package org.apache.http.impl.execchain;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.http.HttpClientConnection;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.concurrent.Cancellable;
/*     */ import org.apache.http.conn.ConnectionReleaseTrigger;
/*     */ import org.apache.http.conn.HttpClientConnectionManager;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Contract(threading = ThreadingBehavior.SAFE)
/*     */ class ConnectionHolder
/*     */   implements ConnectionReleaseTrigger, Cancellable, Closeable
/*     */ {
/*     */   private final Log log;
/*     */   private final HttpClientConnectionManager manager;
/*     */   private final HttpClientConnection managedConn;
/*     */   private final AtomicBoolean released;
/*     */   private volatile boolean reusable;
/*     */   private volatile Object state;
/*     */   private volatile long validDuration;
/*     */   private volatile TimeUnit timeUnit;
/*     */   
/*     */   public ConnectionHolder(Log log, HttpClientConnectionManager manager, HttpClientConnection managedConn) {
/*  66 */     this.log = log;
/*  67 */     this.manager = manager;
/*  68 */     this.managedConn = managedConn;
/*  69 */     this.released = new AtomicBoolean(false);
/*     */   }
/*     */   
/*     */   public boolean isReusable() {
/*  73 */     return this.reusable;
/*     */   }
/*     */   
/*     */   public void markReusable() {
/*  77 */     this.reusable = true;
/*     */   }
/*     */   
/*     */   public void markNonReusable() {
/*  81 */     this.reusable = false;
/*     */   }
/*     */   
/*     */   public void setState(Object state) {
/*  85 */     this.state = state;
/*     */   }
/*     */   
/*     */   public void setValidFor(long duration, TimeUnit timeUnit) {
/*  89 */     synchronized (this.managedConn) {
/*  90 */       this.validDuration = duration;
/*  91 */       this.timeUnit = timeUnit;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void releaseConnection(boolean reusable) {
/*  96 */     if (this.released.compareAndSet(false, true)) {
/*  97 */       synchronized (this.managedConn) {
/*  98 */         if (reusable) {
/*  99 */           this.manager.releaseConnection(this.managedConn, this.state, this.validDuration, this.timeUnit);
/*     */         } else {
/*     */           
/*     */           try {
/* 103 */             this.managedConn.close();
/* 104 */             this.log.debug("Connection discarded");
/* 105 */           } catch (IOException ex) {
/* 106 */             if (this.log.isDebugEnabled()) {
/* 107 */               this.log.debug(ex.getMessage(), ex);
/*     */             }
/*     */           } finally {
/* 110 */             this.manager.releaseConnection(this.managedConn, null, 0L, TimeUnit.MILLISECONDS);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void releaseConnection() {
/* 120 */     releaseConnection(this.reusable);
/*     */   }
/*     */ 
/*     */   
/*     */   public void abortConnection() {
/* 125 */     if (this.released.compareAndSet(false, true)) {
/* 126 */       synchronized (this.managedConn) {
/*     */         try {
/* 128 */           this.managedConn.shutdown();
/* 129 */           this.log.debug("Connection discarded");
/* 130 */         } catch (IOException ex) {
/* 131 */           if (this.log.isDebugEnabled()) {
/* 132 */             this.log.debug(ex.getMessage(), ex);
/*     */           }
/*     */         } finally {
/* 135 */           this.manager.releaseConnection(this.managedConn, null, 0L, TimeUnit.MILLISECONDS);
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean cancel() {
/* 144 */     boolean alreadyReleased = this.released.get();
/* 145 */     this.log.debug("Cancelling request execution");
/* 146 */     abortConnection();
/* 147 */     return !alreadyReleased;
/*     */   }
/*     */   
/*     */   public boolean isReleased() {
/* 151 */     return this.released.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 156 */     releaseConnection(false);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\execchain\ConnectionHolder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */