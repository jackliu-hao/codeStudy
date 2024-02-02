/*     */ package org.apache.http.impl.execchain;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.ConnectionReuseStrategy;
/*     */ import org.apache.http.HttpClientConnection;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpRequestInterceptor;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.ProtocolException;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.client.config.RequestConfig;
/*     */ import org.apache.http.client.methods.CloseableHttpResponse;
/*     */ import org.apache.http.client.methods.HttpExecutionAware;
/*     */ import org.apache.http.client.methods.HttpRequestWrapper;
/*     */ import org.apache.http.client.methods.HttpUriRequest;
/*     */ import org.apache.http.client.protocol.HttpClientContext;
/*     */ import org.apache.http.client.protocol.RequestClientConnControl;
/*     */ import org.apache.http.client.utils.URIUtils;
/*     */ import org.apache.http.concurrent.Cancellable;
/*     */ import org.apache.http.conn.ConnectionKeepAliveStrategy;
/*     */ import org.apache.http.conn.ConnectionRequest;
/*     */ import org.apache.http.conn.HttpClientConnectionManager;
/*     */ import org.apache.http.conn.routing.HttpRoute;
/*     */ import org.apache.http.impl.conn.ConnectionShutdownException;
/*     */ import org.apache.http.protocol.HttpContext;
/*     */ import org.apache.http.protocol.HttpProcessor;
/*     */ import org.apache.http.protocol.HttpRequestExecutor;
/*     */ import org.apache.http.protocol.ImmutableHttpProcessor;
/*     */ import org.apache.http.protocol.RequestContent;
/*     */ import org.apache.http.protocol.RequestTargetHost;
/*     */ import org.apache.http.protocol.RequestUserAgent;
/*     */ import org.apache.http.util.Args;
/*     */ import org.apache.http.util.VersionInfo;
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
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
/*     */ public class MinimalClientExec
/*     */   implements ClientExecChain
/*     */ {
/*  87 */   private final Log log = LogFactory.getLog(getClass());
/*     */   
/*     */   private final HttpRequestExecutor requestExecutor;
/*     */   
/*     */   private final HttpClientConnectionManager connManager;
/*     */   
/*     */   private final ConnectionReuseStrategy reuseStrategy;
/*     */   
/*     */   private final ConnectionKeepAliveStrategy keepAliveStrategy;
/*     */   
/*     */   private final HttpProcessor httpProcessor;
/*     */   
/*     */   public MinimalClientExec(HttpRequestExecutor requestExecutor, HttpClientConnectionManager connManager, ConnectionReuseStrategy reuseStrategy, ConnectionKeepAliveStrategy keepAliveStrategy) {
/* 100 */     Args.notNull(requestExecutor, "HTTP request executor");
/* 101 */     Args.notNull(connManager, "Client connection manager");
/* 102 */     Args.notNull(reuseStrategy, "Connection reuse strategy");
/* 103 */     Args.notNull(keepAliveStrategy, "Connection keep alive strategy");
/* 104 */     this.httpProcessor = (HttpProcessor)new ImmutableHttpProcessor(new HttpRequestInterceptor[] { (HttpRequestInterceptor)new RequestContent(), (HttpRequestInterceptor)new RequestTargetHost(), (HttpRequestInterceptor)new RequestClientConnControl(), (HttpRequestInterceptor)new RequestUserAgent(VersionInfo.getUserAgent("Apache-HttpClient", "org.apache.http.client", getClass())) });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 110 */     this.requestExecutor = requestExecutor;
/* 111 */     this.connManager = connManager;
/* 112 */     this.reuseStrategy = reuseStrategy;
/* 113 */     this.keepAliveStrategy = keepAliveStrategy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void rewriteRequestURI(HttpRequestWrapper request, HttpRoute route, boolean normalizeUri) throws ProtocolException {
/*     */     try {
/* 121 */       URI uri = request.getURI();
/* 122 */       if (uri != null) {
/*     */         
/* 124 */         if (uri.isAbsolute()) {
/* 125 */           uri = URIUtils.rewriteURI(uri, null, normalizeUri ? URIUtils.DROP_FRAGMENT_AND_NORMALIZE : URIUtils.DROP_FRAGMENT);
/*     */         } else {
/* 127 */           uri = URIUtils.rewriteURI(uri);
/*     */         } 
/* 129 */         request.setURI(uri);
/*     */       } 
/* 131 */     } catch (URISyntaxException ex) {
/* 132 */       throw new ProtocolException("Invalid URI: " + request.getRequestLine().getUri(), ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CloseableHttpResponse execute(HttpRoute route, HttpRequestWrapper request, HttpClientContext context, HttpExecutionAware execAware) throws IOException, HttpException {
/*     */     HttpClientConnection managedConn;
/* 142 */     Args.notNull(route, "HTTP route");
/* 143 */     Args.notNull(request, "HTTP request");
/* 144 */     Args.notNull(context, "HTTP context");
/*     */     
/* 146 */     rewriteRequestURI(request, route, context.getRequestConfig().isNormalizeUri());
/*     */     
/* 148 */     ConnectionRequest connRequest = this.connManager.requestConnection(route, null);
/* 149 */     if (execAware != null) {
/* 150 */       if (execAware.isAborted()) {
/* 151 */         connRequest.cancel();
/* 152 */         throw new RequestAbortedException("Request aborted");
/*     */       } 
/* 154 */       execAware.setCancellable((Cancellable)connRequest);
/*     */     } 
/*     */     
/* 157 */     RequestConfig config = context.getRequestConfig();
/*     */ 
/*     */     
/*     */     try {
/* 161 */       int timeout = config.getConnectionRequestTimeout();
/* 162 */       managedConn = connRequest.get((timeout > 0) ? timeout : 0L, TimeUnit.MILLISECONDS);
/* 163 */     } catch (InterruptedException interrupted) {
/* 164 */       Thread.currentThread().interrupt();
/* 165 */       throw new RequestAbortedException("Request aborted", interrupted);
/* 166 */     } catch (ExecutionException ex) {
/* 167 */       Throwable cause = ex.getCause();
/* 168 */       if (cause == null) {
/* 169 */         cause = ex;
/*     */       }
/* 171 */       throw new RequestAbortedException("Request execution failed", cause);
/*     */     } 
/*     */     
/* 174 */     ConnectionHolder releaseTrigger = new ConnectionHolder(this.log, this.connManager, managedConn);
/*     */     try {
/* 176 */       if (execAware != null) {
/* 177 */         if (execAware.isAborted()) {
/* 178 */           releaseTrigger.close();
/* 179 */           throw new RequestAbortedException("Request aborted");
/*     */         } 
/* 181 */         execAware.setCancellable(releaseTrigger);
/*     */       } 
/*     */       
/* 184 */       if (!managedConn.isOpen()) {
/* 185 */         int i = config.getConnectTimeout();
/* 186 */         this.connManager.connect(managedConn, route, (i > 0) ? i : 0, (HttpContext)context);
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 191 */         this.connManager.routeComplete(managedConn, route, (HttpContext)context);
/*     */       } 
/* 193 */       int timeout = config.getSocketTimeout();
/* 194 */       if (timeout >= 0) {
/* 195 */         managedConn.setSocketTimeout(timeout);
/*     */       }
/*     */       
/* 198 */       HttpHost target = null;
/* 199 */       HttpRequest original = request.getOriginal();
/* 200 */       if (original instanceof HttpUriRequest) {
/* 201 */         URI uri = ((HttpUriRequest)original).getURI();
/* 202 */         if (uri.isAbsolute()) {
/* 203 */           target = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
/*     */         }
/*     */       } 
/* 206 */       if (target == null) {
/* 207 */         target = route.getTargetHost();
/*     */       }
/*     */       
/* 210 */       context.setAttribute("http.target_host", target);
/* 211 */       context.setAttribute("http.request", request);
/* 212 */       context.setAttribute("http.connection", managedConn);
/* 213 */       context.setAttribute("http.route", route);
/*     */       
/* 215 */       this.httpProcessor.process((HttpRequest)request, (HttpContext)context);
/* 216 */       HttpResponse response = this.requestExecutor.execute((HttpRequest)request, managedConn, (HttpContext)context);
/* 217 */       this.httpProcessor.process(response, (HttpContext)context);
/*     */ 
/*     */       
/* 220 */       if (this.reuseStrategy.keepAlive(response, (HttpContext)context)) {
/*     */         
/* 222 */         long duration = this.keepAliveStrategy.getKeepAliveDuration(response, (HttpContext)context);
/* 223 */         releaseTrigger.setValidFor(duration, TimeUnit.MILLISECONDS);
/* 224 */         releaseTrigger.markReusable();
/*     */       } else {
/* 226 */         releaseTrigger.markNonReusable();
/*     */       } 
/*     */ 
/*     */       
/* 230 */       HttpEntity entity = response.getEntity();
/* 231 */       if (entity == null || !entity.isStreaming()) {
/*     */         
/* 233 */         releaseTrigger.releaseConnection();
/* 234 */         return new HttpResponseProxy(response, null);
/*     */       } 
/* 236 */       return new HttpResponseProxy(response, releaseTrigger);
/* 237 */     } catch (ConnectionShutdownException ex) {
/* 238 */       InterruptedIOException ioex = new InterruptedIOException("Connection has been shut down");
/*     */       
/* 240 */       ioex.initCause((Throwable)ex);
/* 241 */       throw ioex;
/* 242 */     } catch (HttpException ex) {
/* 243 */       releaseTrigger.abortConnection();
/* 244 */       throw ex;
/* 245 */     } catch (IOException ex) {
/* 246 */       releaseTrigger.abortConnection();
/* 247 */       throw ex;
/* 248 */     } catch (RuntimeException ex) {
/* 249 */       releaseTrigger.abortConnection();
/* 250 */       throw ex;
/* 251 */     } catch (Error error) {
/* 252 */       this.connManager.shutdown();
/* 253 */       throw error;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\execchain\MinimalClientExec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */