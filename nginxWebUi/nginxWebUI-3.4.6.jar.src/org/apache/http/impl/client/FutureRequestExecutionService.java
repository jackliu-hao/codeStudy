/*     */ package org.apache.http.impl.client;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.client.HttpClient;
/*     */ import org.apache.http.client.ResponseHandler;
/*     */ import org.apache.http.client.methods.HttpUriRequest;
/*     */ import org.apache.http.concurrent.FutureCallback;
/*     */ import org.apache.http.protocol.HttpContext;
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
/*     */ public class FutureRequestExecutionService
/*     */   implements Closeable
/*     */ {
/*     */   private final HttpClient httpclient;
/*     */   private final ExecutorService executorService;
/*  51 */   private final FutureRequestExecutionMetrics metrics = new FutureRequestExecutionMetrics();
/*  52 */   private final AtomicBoolean closed = new AtomicBoolean(false);
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
/*     */   public FutureRequestExecutionService(HttpClient httpclient, ExecutorService executorService) {
/*  70 */     this.httpclient = httpclient;
/*  71 */     this.executorService = executorService;
/*     */   }
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
/*     */   public <T> HttpRequestFutureTask<T> execute(HttpUriRequest request, HttpContext context, ResponseHandler<T> responseHandler) {
/*  89 */     return execute(request, context, responseHandler, null);
/*     */   }
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
/*     */   public <T> HttpRequestFutureTask<T> execute(HttpUriRequest request, HttpContext context, ResponseHandler<T> responseHandler, FutureCallback<T> callback) {
/* 113 */     if (this.closed.get()) {
/* 114 */       throw new IllegalStateException("Close has been called on this httpclient instance.");
/*     */     }
/* 116 */     this.metrics.getScheduledConnections().incrementAndGet();
/* 117 */     HttpRequestTaskCallable<T> callable = new HttpRequestTaskCallable<T>(this.httpclient, request, context, responseHandler, callback, this.metrics);
/*     */     
/* 119 */     HttpRequestFutureTask<T> httpRequestFutureTask = new HttpRequestFutureTask<T>(request, callable);
/*     */     
/* 121 */     this.executorService.execute(httpRequestFutureTask);
/*     */     
/* 123 */     return httpRequestFutureTask;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FutureRequestExecutionMetrics metrics() {
/* 131 */     return this.metrics;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 136 */     this.closed.set(true);
/* 137 */     this.executorService.shutdownNow();
/* 138 */     if (this.httpclient instanceof Closeable)
/* 139 */       ((Closeable)this.httpclient).close(); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\client\FutureRequestExecutionService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */