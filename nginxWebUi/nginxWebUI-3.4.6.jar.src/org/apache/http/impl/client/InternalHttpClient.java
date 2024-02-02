/*     */ package org.apache.http.impl.client;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.auth.AuthSchemeProvider;
/*     */ import org.apache.http.auth.AuthState;
/*     */ import org.apache.http.client.ClientProtocolException;
/*     */ import org.apache.http.client.CookieStore;
/*     */ import org.apache.http.client.CredentialsProvider;
/*     */ import org.apache.http.client.config.RequestConfig;
/*     */ import org.apache.http.client.methods.CloseableHttpResponse;
/*     */ import org.apache.http.client.methods.Configurable;
/*     */ import org.apache.http.client.methods.HttpExecutionAware;
/*     */ import org.apache.http.client.methods.HttpRequestWrapper;
/*     */ import org.apache.http.client.params.HttpClientParamConfig;
/*     */ import org.apache.http.client.protocol.HttpClientContext;
/*     */ import org.apache.http.config.Lookup;
/*     */ import org.apache.http.conn.ClientConnectionManager;
/*     */ import org.apache.http.conn.ClientConnectionRequest;
/*     */ import org.apache.http.conn.HttpClientConnectionManager;
/*     */ import org.apache.http.conn.ManagedClientConnection;
/*     */ import org.apache.http.conn.routing.HttpRoute;
/*     */ import org.apache.http.conn.routing.HttpRoutePlanner;
/*     */ import org.apache.http.conn.scheme.SchemeRegistry;
/*     */ import org.apache.http.cookie.CookieSpecProvider;
/*     */ import org.apache.http.impl.execchain.ClientExecChain;
/*     */ import org.apache.http.params.HttpParams;
/*     */ import org.apache.http.params.HttpParamsNames;
/*     */ import org.apache.http.protocol.BasicHttpContext;
/*     */ import org.apache.http.protocol.HttpContext;
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
/*     */ @Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
/*     */ class InternalHttpClient
/*     */   extends CloseableHttpClient
/*     */   implements Configurable
/*     */ {
/*  80 */   private final Log log = LogFactory.getLog(getClass());
/*     */ 
/*     */   
/*     */   private final ClientExecChain execChain;
/*     */   
/*     */   private final HttpClientConnectionManager connManager;
/*     */   
/*     */   private final HttpRoutePlanner routePlanner;
/*     */   
/*     */   private final Lookup<CookieSpecProvider> cookieSpecRegistry;
/*     */   
/*     */   private final Lookup<AuthSchemeProvider> authSchemeRegistry;
/*     */   
/*     */   private final CookieStore cookieStore;
/*     */   
/*     */   private final CredentialsProvider credentialsProvider;
/*     */   
/*     */   private final RequestConfig defaultConfig;
/*     */   
/*     */   private final List<Closeable> closeables;
/*     */ 
/*     */   
/*     */   public InternalHttpClient(ClientExecChain execChain, HttpClientConnectionManager connManager, HttpRoutePlanner routePlanner, Lookup<CookieSpecProvider> cookieSpecRegistry, Lookup<AuthSchemeProvider> authSchemeRegistry, CookieStore cookieStore, CredentialsProvider credentialsProvider, RequestConfig defaultConfig, List<Closeable> closeables) {
/* 103 */     Args.notNull(execChain, "HTTP client exec chain");
/* 104 */     Args.notNull(connManager, "HTTP connection manager");
/* 105 */     Args.notNull(routePlanner, "HTTP route planner");
/* 106 */     this.execChain = execChain;
/* 107 */     this.connManager = connManager;
/* 108 */     this.routePlanner = routePlanner;
/* 109 */     this.cookieSpecRegistry = cookieSpecRegistry;
/* 110 */     this.authSchemeRegistry = authSchemeRegistry;
/* 111 */     this.cookieStore = cookieStore;
/* 112 */     this.credentialsProvider = credentialsProvider;
/* 113 */     this.defaultConfig = defaultConfig;
/* 114 */     this.closeables = closeables;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private HttpRoute determineRoute(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
/* 121 */     HttpHost host = target;
/* 122 */     if (host == null) {
/* 123 */       host = (HttpHost)request.getParams().getParameter("http.default-host");
/*     */     }
/* 125 */     return this.routePlanner.determineRoute(host, request, context);
/*     */   }
/*     */   
/*     */   private void setupContext(HttpClientContext context) {
/* 129 */     if (context.getAttribute("http.auth.target-scope") == null) {
/* 130 */       context.setAttribute("http.auth.target-scope", new AuthState());
/*     */     }
/* 132 */     if (context.getAttribute("http.auth.proxy-scope") == null) {
/* 133 */       context.setAttribute("http.auth.proxy-scope", new AuthState());
/*     */     }
/* 135 */     if (context.getAttribute("http.authscheme-registry") == null) {
/* 136 */       context.setAttribute("http.authscheme-registry", this.authSchemeRegistry);
/*     */     }
/* 138 */     if (context.getAttribute("http.cookiespec-registry") == null) {
/* 139 */       context.setAttribute("http.cookiespec-registry", this.cookieSpecRegistry);
/*     */     }
/* 141 */     if (context.getAttribute("http.cookie-store") == null) {
/* 142 */       context.setAttribute("http.cookie-store", this.cookieStore);
/*     */     }
/* 144 */     if (context.getAttribute("http.auth.credentials-provider") == null) {
/* 145 */       context.setAttribute("http.auth.credentials-provider", this.credentialsProvider);
/*     */     }
/* 147 */     if (context.getAttribute("http.request-config") == null) {
/* 148 */       context.setAttribute("http.request-config", this.defaultConfig);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected CloseableHttpResponse doExecute(HttpHost target, HttpRequest request, HttpContext context) throws IOException, ClientProtocolException {
/* 157 */     Args.notNull(request, "HTTP request");
/* 158 */     HttpExecutionAware execAware = null;
/* 159 */     if (request instanceof HttpExecutionAware) {
/* 160 */       execAware = (HttpExecutionAware)request;
/*     */     }
/*     */     try {
/* 163 */       HttpRequestWrapper wrapper = HttpRequestWrapper.wrap(request, target);
/* 164 */       HttpClientContext localcontext = HttpClientContext.adapt((context != null) ? context : (HttpContext)new BasicHttpContext());
/*     */       
/* 166 */       RequestConfig config = null;
/* 167 */       if (request instanceof Configurable) {
/* 168 */         config = ((Configurable)request).getConfig();
/*     */       }
/* 170 */       if (config == null) {
/* 171 */         HttpParams params = request.getParams();
/* 172 */         if (params instanceof HttpParamsNames) {
/* 173 */           if (!((HttpParamsNames)params).getNames().isEmpty()) {
/* 174 */             config = HttpClientParamConfig.getRequestConfig(params, this.defaultConfig);
/*     */           }
/*     */         } else {
/* 177 */           config = HttpClientParamConfig.getRequestConfig(params, this.defaultConfig);
/*     */         } 
/*     */       } 
/* 180 */       if (config != null) {
/* 181 */         localcontext.setRequestConfig(config);
/*     */       }
/* 183 */       setupContext(localcontext);
/* 184 */       HttpRoute route = determineRoute(target, (HttpRequest)wrapper, (HttpContext)localcontext);
/* 185 */       return this.execChain.execute(route, wrapper, localcontext, execAware);
/* 186 */     } catch (HttpException httpException) {
/* 187 */       throw new ClientProtocolException(httpException);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public RequestConfig getConfig() {
/* 193 */     return this.defaultConfig;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 198 */     if (this.closeables != null) {
/* 199 */       for (Closeable closeable : this.closeables) {
/*     */         try {
/* 201 */           closeable.close();
/* 202 */         } catch (IOException ex) {
/* 203 */           this.log.error(ex.getMessage(), ex);
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpParams getParams() {
/* 211 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ClientConnectionManager getConnectionManager() {
/* 217 */     return new ClientConnectionManager()
/*     */       {
/*     */         public void shutdown()
/*     */         {
/* 221 */           InternalHttpClient.this.connManager.shutdown();
/*     */         }
/*     */ 
/*     */ 
/*     */         
/*     */         public ClientConnectionRequest requestConnection(HttpRoute route, Object state) {
/* 227 */           throw new UnsupportedOperationException();
/*     */         }
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         public void releaseConnection(ManagedClientConnection conn, long validDuration, TimeUnit timeUnit) {
/* 234 */           throw new UnsupportedOperationException();
/*     */         }
/*     */ 
/*     */         
/*     */         public SchemeRegistry getSchemeRegistry() {
/* 239 */           throw new UnsupportedOperationException();
/*     */         }
/*     */ 
/*     */         
/*     */         public void closeIdleConnections(long idletime, TimeUnit timeUnit) {
/* 244 */           InternalHttpClient.this.connManager.closeIdleConnections(idletime, timeUnit);
/*     */         }
/*     */ 
/*     */         
/*     */         public void closeExpiredConnections() {
/* 249 */           InternalHttpClient.this.connManager.closeExpiredConnections();
/*     */         }
/*     */       };
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\client\InternalHttpClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */