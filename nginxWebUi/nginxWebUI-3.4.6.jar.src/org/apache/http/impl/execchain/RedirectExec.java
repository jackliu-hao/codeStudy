/*     */ package org.apache.http.impl.execchain;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.URI;
/*     */ import java.util.List;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.HttpEntityEnclosingRequest;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.ProtocolException;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.auth.AuthState;
/*     */ import org.apache.http.client.RedirectException;
/*     */ import org.apache.http.client.RedirectStrategy;
/*     */ import org.apache.http.client.config.RequestConfig;
/*     */ import org.apache.http.client.methods.CloseableHttpResponse;
/*     */ import org.apache.http.client.methods.HttpExecutionAware;
/*     */ import org.apache.http.client.methods.HttpRequestWrapper;
/*     */ import org.apache.http.client.methods.HttpUriRequest;
/*     */ import org.apache.http.client.protocol.HttpClientContext;
/*     */ import org.apache.http.client.utils.URIUtils;
/*     */ import org.apache.http.conn.routing.HttpRoute;
/*     */ import org.apache.http.conn.routing.HttpRoutePlanner;
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
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
/*     */ public class RedirectExec
/*     */   implements ClientExecChain
/*     */ {
/*  71 */   private final Log log = LogFactory.getLog(getClass());
/*     */ 
/*     */   
/*     */   private final ClientExecChain requestExecutor;
/*     */   
/*     */   private final RedirectStrategy redirectStrategy;
/*     */   
/*     */   private final HttpRoutePlanner routePlanner;
/*     */ 
/*     */   
/*     */   public RedirectExec(ClientExecChain requestExecutor, HttpRoutePlanner routePlanner, RedirectStrategy redirectStrategy) {
/*  82 */     Args.notNull(requestExecutor, "HTTP client request executor");
/*  83 */     Args.notNull(routePlanner, "HTTP route planner");
/*  84 */     Args.notNull(redirectStrategy, "HTTP redirect strategy");
/*  85 */     this.requestExecutor = requestExecutor;
/*  86 */     this.routePlanner = routePlanner;
/*  87 */     this.redirectStrategy = redirectStrategy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CloseableHttpResponse execute(HttpRoute route, HttpRequestWrapper request, HttpClientContext context, HttpExecutionAware execAware) throws IOException, HttpException {
/*     */     CloseableHttpResponse response;
/*  96 */     Args.notNull(route, "HTTP route");
/*  97 */     Args.notNull(request, "HTTP request");
/*  98 */     Args.notNull(context, "HTTP context");
/*     */     
/* 100 */     List<URI> redirectLocations = context.getRedirectLocations();
/* 101 */     if (redirectLocations != null) {
/* 102 */       redirectLocations.clear();
/*     */     }
/*     */     
/* 105 */     RequestConfig config = context.getRequestConfig();
/* 106 */     int maxRedirects = (config.getMaxRedirects() > 0) ? config.getMaxRedirects() : 50;
/* 107 */     HttpRoute currentRoute = route;
/* 108 */     HttpRequestWrapper currentRequest = request;
/* 109 */     int redirectCount = 0; while (true) {
/* 110 */       response = this.requestExecutor.execute(currentRoute, currentRequest, context, execAware);
/*     */       
/*     */       try {
/* 113 */         if (config.isRedirectsEnabled() && this.redirectStrategy.isRedirected(currentRequest.getOriginal(), (HttpResponse)response, (HttpContext)context)) {
/*     */           
/* 115 */           if (!RequestEntityProxy.isRepeatable((HttpRequest)currentRequest)) {
/* 116 */             if (this.log.isDebugEnabled()) {
/* 117 */               this.log.debug("Cannot redirect non-repeatable request");
/*     */             }
/* 119 */             return response;
/*     */           } 
/* 121 */           if (redirectCount >= maxRedirects) {
/* 122 */             throw new RedirectException("Maximum redirects (" + maxRedirects + ") exceeded");
/*     */           }
/* 124 */           redirectCount++;
/*     */           
/* 126 */           HttpUriRequest httpUriRequest = this.redirectStrategy.getRedirect(currentRequest.getOriginal(), (HttpResponse)response, (HttpContext)context);
/*     */           
/* 128 */           if (!httpUriRequest.headerIterator().hasNext()) {
/* 129 */             HttpRequest original = request.getOriginal();
/* 130 */             httpUriRequest.setHeaders(original.getAllHeaders());
/*     */           } 
/* 132 */           currentRequest = HttpRequestWrapper.wrap((HttpRequest)httpUriRequest);
/*     */           
/* 134 */           if (currentRequest instanceof HttpEntityEnclosingRequest) {
/* 135 */             RequestEntityProxy.enhance((HttpEntityEnclosingRequest)currentRequest);
/*     */           }
/*     */           
/* 138 */           URI uri = currentRequest.getURI();
/* 139 */           HttpHost newTarget = URIUtils.extractHost(uri);
/* 140 */           if (newTarget == null) {
/* 141 */             throw new ProtocolException("Redirect URI does not specify a valid host name: " + uri);
/*     */           }
/*     */ 
/*     */ 
/*     */           
/* 146 */           if (!currentRoute.getTargetHost().equals(newTarget)) {
/* 147 */             AuthState targetAuthState = context.getTargetAuthState();
/* 148 */             if (targetAuthState != null) {
/* 149 */               this.log.debug("Resetting target auth state");
/* 150 */               targetAuthState.reset();
/*     */             } 
/* 152 */             AuthState proxyAuthState = context.getProxyAuthState();
/* 153 */             if (proxyAuthState != null && proxyAuthState.isConnectionBased()) {
/* 154 */               this.log.debug("Resetting proxy auth state");
/* 155 */               proxyAuthState.reset();
/*     */             } 
/*     */           } 
/*     */           
/* 159 */           currentRoute = this.routePlanner.determineRoute(newTarget, (HttpRequest)currentRequest, (HttpContext)context);
/* 160 */           if (this.log.isDebugEnabled()) {
/* 161 */             this.log.debug("Redirecting to '" + uri + "' via " + currentRoute);
/*     */           }
/* 163 */           EntityUtils.consume(response.getEntity());
/* 164 */           response.close(); continue;
/*     */         } 
/* 166 */         return response;
/*     */       }
/* 168 */       catch (RuntimeException runtimeException) {
/* 169 */         response.close();
/* 170 */         throw runtimeException;
/* 171 */       } catch (IOException iOException) {
/* 172 */         response.close();
/* 173 */         throw iOException;
/* 174 */       } catch (HttpException ex) {
/*     */         break;
/*     */       } 
/*     */     }  try {
/* 178 */       EntityUtils.consume(response.getEntity());
/* 179 */     } catch (IOException ioex) {
/* 180 */       this.log.debug("I/O error while releasing connection", ioex);
/*     */     } finally {
/* 182 */       response.close();
/*     */     } 
/* 184 */     throw ex;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\execchain\RedirectExec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */