/*     */ package io.undertow.server.protocol;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.server.ServerConnection;
/*     */ import io.undertow.util.WorkerUtils;
/*     */ import java.io.Closeable;
/*     */ import java.util.concurrent.RejectedExecutionException;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.XnioExecutor;
/*     */ import org.xnio.channels.ConnectedChannel;
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
/*     */ public final class ParseTimeoutUpdater
/*     */   implements Runnable, ServerConnection.CloseListener, Closeable
/*     */ {
/*     */   private final ConnectedChannel connection;
/*     */   private final long requestParseTimeout;
/*     */   private final long requestIdleTimeout;
/*     */   private volatile XnioExecutor.Key handle;
/*  44 */   private volatile long expireTime = -1L;
/*     */ 
/*     */ 
/*     */   
/*     */   private volatile boolean parsing = false;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int FUZZ_FACTOR = 50;
/*     */ 
/*     */   
/*     */   private final Runnable closeTask;
/*     */ 
/*     */ 
/*     */   
/*     */   public ParseTimeoutUpdater(ConnectedChannel channel, long requestParseTimeout, long requestIdleTimeout) {
/*  60 */     this(channel, requestParseTimeout, requestIdleTimeout, new Runnable(channel)
/*     */         {
/*     */           public void run() {
/*  63 */             IoUtils.safeClose((Closeable)channel);
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ParseTimeoutUpdater(ConnectedChannel channel, long requestParseTimeout, long requestIdleTimeout, Runnable closeTask) {
/*  75 */     this.connection = channel;
/*  76 */     this.requestParseTimeout = requestParseTimeout;
/*  77 */     this.requestIdleTimeout = requestIdleTimeout;
/*  78 */     this.closeTask = closeTask;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void connectionIdle() {
/*  84 */     this.parsing = false;
/*  85 */     handleSchedule(this.requestIdleTimeout);
/*     */   }
/*     */ 
/*     */   
/*     */   private void handleSchedule(long timeout) {
/*  90 */     if (timeout == -1L) {
/*  91 */       this.expireTime = -1L;
/*     */       
/*     */       return;
/*     */     } 
/*  95 */     long newExpireTime = System.currentTimeMillis() + timeout;
/*  96 */     long oldExpireTime = this.expireTime;
/*  97 */     this.expireTime = newExpireTime;
/*     */     
/*  99 */     if (newExpireTime < oldExpireTime && 
/* 100 */       this.handle != null) {
/* 101 */       this.handle.remove();
/* 102 */       this.handle = null;
/*     */     } 
/*     */     
/* 105 */     if (this.handle == null) {
/*     */       try {
/* 107 */         this.handle = WorkerUtils.executeAfter(this.connection.getIoThread(), this, timeout + 50L, TimeUnit.MILLISECONDS);
/* 108 */       } catch (RejectedExecutionException e) {
/* 109 */         UndertowLogger.REQUEST_LOGGER.debug("Failed to schedule parse timeout, server is probably shutting down", e);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void failedParse() {
/* 120 */     if (!this.parsing) {
/* 121 */       this.parsing = true;
/* 122 */       handleSchedule(this.requestParseTimeout);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void requestStarted() {
/* 133 */     this.expireTime = -1L;
/* 134 */     this.parsing = false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void run() {
/* 139 */     if (!this.connection.isOpen()) {
/*     */       return;
/*     */     }
/* 142 */     this.handle = null;
/* 143 */     if (this.expireTime > 0L) {
/* 144 */       long now = System.currentTimeMillis();
/* 145 */       if (this.expireTime > now) {
/* 146 */         this.handle = WorkerUtils.executeAfter(this.connection.getIoThread(), this, this.expireTime - now + 50L, TimeUnit.MILLISECONDS);
/*     */       } else {
/* 148 */         if (this.parsing) {
/* 149 */           UndertowLogger.REQUEST_LOGGER.parseRequestTimedOut(this.connection.getPeerAddress());
/*     */         } else {
/* 151 */           UndertowLogger.REQUEST_LOGGER.debugf("Timing out idle connection from %s", this.connection.getPeerAddress());
/*     */         } 
/* 153 */         this.closeTask.run();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void closed(ServerConnection connection) {
/* 160 */     close();
/*     */   }
/*     */   
/*     */   public void close() {
/* 164 */     if (this.handle != null) {
/* 165 */       this.handle.remove();
/* 166 */       this.handle = null;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\protocol\ParseTimeoutUpdater.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */