/*     */ package org.apache.http.impl.client;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.apache.http.ConnectionReuseStrategy;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.client.ClientProtocolException;
/*     */ import org.apache.http.client.config.RequestConfig;
/*     */ import org.apache.http.client.methods.CloseableHttpResponse;
/*     */ import org.apache.http.client.methods.Configurable;
/*     */ import org.apache.http.client.methods.HttpExecutionAware;
/*     */ import org.apache.http.client.methods.HttpRequestWrapper;
/*     */ import org.apache.http.client.protocol.HttpClientContext;
/*     */ import org.apache.http.conn.ClientConnectionManager;
/*     */ import org.apache.http.conn.ClientConnectionRequest;
/*     */ import org.apache.http.conn.HttpClientConnectionManager;
/*     */ import org.apache.http.conn.ManagedClientConnection;
/*     */ import org.apache.http.conn.routing.HttpRoute;
/*     */ import org.apache.http.conn.scheme.SchemeRegistry;
/*     */ import org.apache.http.impl.DefaultConnectionReuseStrategy;
/*     */ import org.apache.http.impl.execchain.MinimalClientExec;
/*     */ import org.apache.http.params.BasicHttpParams;
/*     */ import org.apache.http.params.HttpParams;
/*     */ import org.apache.http.protocol.BasicHttpContext;
/*     */ import org.apache.http.protocol.HttpContext;
/*     */ import org.apache.http.protocol.HttpRequestExecutor;
/*     */ import org.apache.http.util.Args;
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
/*     */ @Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
/*     */ class MinimalHttpClient
/*     */   extends CloseableHttpClient
/*     */ {
/*     */   private final HttpClientConnectionManager connManager;
/*     */   private final MinimalClientExec requestExecutor;
/*     */   private final HttpParams params;
/*     */   
/*     */   public MinimalHttpClient(HttpClientConnectionManager connManager) {
/*  76 */     this.connManager = (HttpClientConnectionManager)Args.notNull(connManager, "HTTP connection manager");
/*  77 */     this.requestExecutor = new MinimalClientExec(new HttpRequestExecutor(), connManager, (ConnectionReuseStrategy)DefaultConnectionReuseStrategy.INSTANCE, DefaultConnectionKeepAliveStrategy.INSTANCE);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  82 */     this.params = (HttpParams)new BasicHttpParams();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected CloseableHttpResponse doExecute(HttpHost target, HttpRequest request, HttpContext context) throws IOException, ClientProtocolException {
/*  90 */     Args.notNull(target, "Target host");
/*  91 */     Args.notNull(request, "HTTP request");
/*  92 */     HttpExecutionAware execAware = null;
/*  93 */     if (request instanceof HttpExecutionAware) {
/*  94 */       execAware = (HttpExecutionAware)request;
/*     */     }
/*     */     try {
/*  97 */       HttpRequestWrapper wrapper = HttpRequestWrapper.wrap(request);
/*  98 */       HttpClientContext localcontext = HttpClientContext.adapt((context != null) ? context : (HttpContext)new BasicHttpContext());
/*     */       
/* 100 */       HttpRoute route = new HttpRoute(target);
/* 101 */       RequestConfig config = null;
/* 102 */       if (request instanceof Configurable) {
/* 103 */         config = ((Configurable)request).getConfig();
/*     */       }
/* 105 */       if (config != null) {
/* 106 */         localcontext.setRequestConfig(config);
/*     */       }
/* 108 */       return this.requestExecutor.execute(route, wrapper, localcontext, execAware);
/* 109 */     } catch (HttpException httpException) {
/* 110 */       throw new ClientProtocolException(httpException);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpParams getParams() {
/* 116 */     return this.params;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 121 */     this.connManager.shutdown();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ClientConnectionManager getConnectionManager() {
/* 127 */     return new ClientConnectionManager()
/*     */       {
/*     */         public void shutdown()
/*     */         {
/* 131 */           MinimalHttpClient.this.connManager.shutdown();
/*     */         }
/*     */ 
/*     */ 
/*     */         
/*     */         public ClientConnectionRequest requestConnection(HttpRoute route, Object state) {
/* 137 */           throw new UnsupportedOperationException();
/*     */         }
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         public void releaseConnection(ManagedClientConnection conn, long validDuration, TimeUnit timeUnit) {
/* 144 */           throw new UnsupportedOperationException();
/*     */         }
/*     */ 
/*     */         
/*     */         public SchemeRegistry getSchemeRegistry() {
/* 149 */           throw new UnsupportedOperationException();
/*     */         }
/*     */ 
/*     */         
/*     */         public void closeIdleConnections(long idletime, TimeUnit timeUnit) {
/* 154 */           MinimalHttpClient.this.connManager.closeIdleConnections(idletime, timeUnit);
/*     */         }
/*     */ 
/*     */         
/*     */         public void closeExpiredConnections() {
/* 159 */           MinimalHttpClient.this.connManager.closeExpiredConnections();
/*     */         }
/*     */       };
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\client\MinimalHttpClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */