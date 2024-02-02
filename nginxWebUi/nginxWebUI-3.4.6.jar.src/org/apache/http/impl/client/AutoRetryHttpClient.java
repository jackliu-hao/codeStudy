/*     */ package org.apache.http.impl.client;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.net.URI;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.client.HttpClient;
/*     */ import org.apache.http.client.ResponseHandler;
/*     */ import org.apache.http.client.ServiceUnavailableRetryStrategy;
/*     */ import org.apache.http.client.methods.HttpUriRequest;
/*     */ import org.apache.http.conn.ClientConnectionManager;
/*     */ import org.apache.http.params.HttpParams;
/*     */ import org.apache.http.protocol.HttpContext;
/*     */ import org.apache.http.util.Args;
/*     */ import org.apache.http.util.EntityUtils;
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
/*     */ @Deprecated
/*     */ @Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
/*     */ public class AutoRetryHttpClient
/*     */   implements HttpClient
/*     */ {
/*     */   private final HttpClient backend;
/*     */   private final ServiceUnavailableRetryStrategy retryStrategy;
/*  67 */   private final Log log = LogFactory.getLog(getClass());
/*     */ 
/*     */ 
/*     */   
/*     */   public AutoRetryHttpClient(HttpClient client, ServiceUnavailableRetryStrategy retryStrategy) {
/*  72 */     Args.notNull(client, "HttpClient");
/*  73 */     Args.notNull(retryStrategy, "ServiceUnavailableRetryStrategy");
/*  74 */     this.backend = client;
/*  75 */     this.retryStrategy = retryStrategy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AutoRetryHttpClient() {
/*  84 */     this(new DefaultHttpClient(), new DefaultServiceUnavailableRetryStrategy());
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
/*     */   public AutoRetryHttpClient(ServiceUnavailableRetryStrategy config) {
/*  96 */     this(new DefaultHttpClient(), config);
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
/*     */   public AutoRetryHttpClient(HttpClient client) {
/* 108 */     this(client, new DefaultServiceUnavailableRetryStrategy());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpResponse execute(HttpHost target, HttpRequest request) throws IOException {
/* 114 */     HttpContext defaultContext = null;
/* 115 */     return execute(target, request, defaultContext);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler) throws IOException {
/* 121 */     return execute(target, request, responseHandler, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException {
/* 128 */     HttpResponse resp = execute(target, request, context);
/* 129 */     return (T)responseHandler.handleResponse(resp);
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpResponse execute(HttpUriRequest request) throws IOException {
/* 134 */     HttpContext context = null;
/* 135 */     return execute(request, context);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpResponse execute(HttpUriRequest request, HttpContext context) throws IOException {
/* 141 */     URI uri = request.getURI();
/* 142 */     HttpHost httpHost = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
/*     */     
/* 144 */     return execute(httpHost, (HttpRequest)request, context);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler) throws IOException {
/* 150 */     return execute(request, responseHandler, (HttpContext)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException {
/* 157 */     HttpResponse resp = execute(request, context);
/* 158 */     return (T)responseHandler.handleResponse(resp);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpResponse execute(HttpHost target, HttpRequest request, HttpContext context) throws IOException {
/* 164 */     for (int c = 1;; c++) {
/* 165 */       HttpResponse response = this.backend.execute(target, request, context);
/*     */       try {
/* 167 */         if (this.retryStrategy.retryRequest(response, c, context)) {
/* 168 */           EntityUtils.consume(response.getEntity());
/* 169 */           long nextInterval = this.retryStrategy.getRetryInterval();
/*     */           try {
/* 171 */             this.log.trace("Wait for " + nextInterval);
/* 172 */             Thread.sleep(nextInterval);
/* 173 */           } catch (InterruptedException e) {
/* 174 */             Thread.currentThread().interrupt();
/* 175 */             throw new InterruptedIOException();
/*     */           } 
/*     */         } else {
/* 178 */           return response;
/*     */         } 
/* 180 */       } catch (RuntimeException ex) {
/*     */         try {
/* 182 */           EntityUtils.consume(response.getEntity());
/* 183 */         } catch (IOException ioex) {
/* 184 */           this.log.warn("I/O error consuming response content", ioex);
/*     */         } 
/* 186 */         throw ex;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public ClientConnectionManager getConnectionManager() {
/* 193 */     return this.backend.getConnectionManager();
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpParams getParams() {
/* 198 */     return this.backend.getParams();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\client\AutoRetryHttpClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */