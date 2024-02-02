/*     */ package org.apache.http.impl.execchain;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.ConnectionReuseStrategy;
/*     */ import org.apache.http.HttpClientConnection;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.HttpEntityEnclosingRequest;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpRequestInterceptor;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.auth.AuthProtocolState;
/*     */ import org.apache.http.auth.AuthState;
/*     */ import org.apache.http.client.AuthenticationStrategy;
/*     */ import org.apache.http.client.NonRepeatableRequestException;
/*     */ import org.apache.http.client.UserTokenHandler;
/*     */ import org.apache.http.client.config.RequestConfig;
/*     */ import org.apache.http.client.methods.CloseableHttpResponse;
/*     */ import org.apache.http.client.methods.HttpExecutionAware;
/*     */ import org.apache.http.client.methods.HttpRequestWrapper;
/*     */ import org.apache.http.client.protocol.HttpClientContext;
/*     */ import org.apache.http.concurrent.Cancellable;
/*     */ import org.apache.http.conn.ConnectionKeepAliveStrategy;
/*     */ import org.apache.http.conn.ConnectionRequest;
/*     */ import org.apache.http.conn.HttpClientConnectionManager;
/*     */ import org.apache.http.conn.routing.BasicRouteDirector;
/*     */ import org.apache.http.conn.routing.HttpRoute;
/*     */ import org.apache.http.conn.routing.HttpRouteDirector;
/*     */ import org.apache.http.conn.routing.RouteInfo;
/*     */ import org.apache.http.conn.routing.RouteTracker;
/*     */ import org.apache.http.entity.BufferedHttpEntity;
/*     */ import org.apache.http.impl.auth.HttpAuthenticator;
/*     */ import org.apache.http.impl.conn.ConnectionShutdownException;
/*     */ import org.apache.http.message.BasicHttpRequest;
/*     */ import org.apache.http.protocol.HttpContext;
/*     */ import org.apache.http.protocol.HttpProcessor;
/*     */ import org.apache.http.protocol.HttpRequestExecutor;
/*     */ import org.apache.http.protocol.ImmutableHttpProcessor;
/*     */ import org.apache.http.protocol.RequestTargetHost;
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
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
/*     */ public class MainClientExec
/*     */   implements ClientExecChain
/*     */ {
/*  91 */   private final Log log = LogFactory.getLog(getClass());
/*     */ 
/*     */   
/*     */   private final HttpRequestExecutor requestExecutor;
/*     */   
/*     */   private final HttpClientConnectionManager connManager;
/*     */   
/*     */   private final ConnectionReuseStrategy reuseStrategy;
/*     */   
/*     */   private final ConnectionKeepAliveStrategy keepAliveStrategy;
/*     */   
/*     */   private final HttpProcessor proxyHttpProcessor;
/*     */   
/*     */   private final AuthenticationStrategy targetAuthStrategy;
/*     */   
/*     */   private final AuthenticationStrategy proxyAuthStrategy;
/*     */   
/*     */   private final HttpAuthenticator authenticator;
/*     */   
/*     */   private final UserTokenHandler userTokenHandler;
/*     */   
/*     */   private final HttpRouteDirector routeDirector;
/*     */ 
/*     */   
/*     */   public MainClientExec(HttpRequestExecutor requestExecutor, HttpClientConnectionManager connManager, ConnectionReuseStrategy reuseStrategy, ConnectionKeepAliveStrategy keepAliveStrategy, HttpProcessor proxyHttpProcessor, AuthenticationStrategy targetAuthStrategy, AuthenticationStrategy proxyAuthStrategy, UserTokenHandler userTokenHandler) {
/* 116 */     Args.notNull(requestExecutor, "HTTP request executor");
/* 117 */     Args.notNull(connManager, "Client connection manager");
/* 118 */     Args.notNull(reuseStrategy, "Connection reuse strategy");
/* 119 */     Args.notNull(keepAliveStrategy, "Connection keep alive strategy");
/* 120 */     Args.notNull(proxyHttpProcessor, "Proxy HTTP processor");
/* 121 */     Args.notNull(targetAuthStrategy, "Target authentication strategy");
/* 122 */     Args.notNull(proxyAuthStrategy, "Proxy authentication strategy");
/* 123 */     Args.notNull(userTokenHandler, "User token handler");
/* 124 */     this.authenticator = new HttpAuthenticator();
/* 125 */     this.routeDirector = (HttpRouteDirector)new BasicRouteDirector();
/* 126 */     this.requestExecutor = requestExecutor;
/* 127 */     this.connManager = connManager;
/* 128 */     this.reuseStrategy = reuseStrategy;
/* 129 */     this.keepAliveStrategy = keepAliveStrategy;
/* 130 */     this.proxyHttpProcessor = proxyHttpProcessor;
/* 131 */     this.targetAuthStrategy = targetAuthStrategy;
/* 132 */     this.proxyAuthStrategy = proxyAuthStrategy;
/* 133 */     this.userTokenHandler = userTokenHandler;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MainClientExec(HttpRequestExecutor requestExecutor, HttpClientConnectionManager connManager, ConnectionReuseStrategy reuseStrategy, ConnectionKeepAliveStrategy keepAliveStrategy, AuthenticationStrategy targetAuthStrategy, AuthenticationStrategy proxyAuthStrategy, UserTokenHandler userTokenHandler) {
/* 144 */     this(requestExecutor, connManager, reuseStrategy, keepAliveStrategy, (HttpProcessor)new ImmutableHttpProcessor(new HttpRequestInterceptor[] { (HttpRequestInterceptor)new RequestTargetHost() }, ), targetAuthStrategy, proxyAuthStrategy, userTokenHandler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CloseableHttpResponse execute(HttpRoute route, HttpRequestWrapper request, HttpClientContext context, HttpExecutionAware execAware) throws IOException, HttpException {
/*     */     HttpClientConnection managedConn;
/* 155 */     Args.notNull(route, "HTTP route");
/* 156 */     Args.notNull(request, "HTTP request");
/* 157 */     Args.notNull(context, "HTTP context");
/*     */     
/* 159 */     AuthState targetAuthState = context.getTargetAuthState();
/* 160 */     if (targetAuthState == null) {
/* 161 */       targetAuthState = new AuthState();
/* 162 */       context.setAttribute("http.auth.target-scope", targetAuthState);
/*     */     } 
/* 164 */     AuthState proxyAuthState = context.getProxyAuthState();
/* 165 */     if (proxyAuthState == null) {
/* 166 */       proxyAuthState = new AuthState();
/* 167 */       context.setAttribute("http.auth.proxy-scope", proxyAuthState);
/*     */     } 
/*     */     
/* 170 */     if (request instanceof HttpEntityEnclosingRequest) {
/* 171 */       RequestEntityProxy.enhance((HttpEntityEnclosingRequest)request);
/*     */     }
/*     */     
/* 174 */     Object userToken = context.getUserToken();
/*     */     
/* 176 */     ConnectionRequest connRequest = this.connManager.requestConnection(route, userToken);
/* 177 */     if (execAware != null) {
/* 178 */       if (execAware.isAborted()) {
/* 179 */         connRequest.cancel();
/* 180 */         throw new RequestAbortedException("Request aborted");
/*     */       } 
/* 182 */       execAware.setCancellable((Cancellable)connRequest);
/*     */     } 
/*     */     
/* 185 */     RequestConfig config = context.getRequestConfig();
/*     */ 
/*     */     
/*     */     try {
/* 189 */       int timeout = config.getConnectionRequestTimeout();
/* 190 */       managedConn = connRequest.get((timeout > 0) ? timeout : 0L, TimeUnit.MILLISECONDS);
/* 191 */     } catch (InterruptedException interrupted) {
/* 192 */       Thread.currentThread().interrupt();
/* 193 */       throw new RequestAbortedException("Request aborted", interrupted);
/* 194 */     } catch (ExecutionException ex) {
/* 195 */       Throwable cause = ex.getCause();
/* 196 */       if (cause == null) {
/* 197 */         cause = ex;
/*     */       }
/* 199 */       throw new RequestAbortedException("Request execution failed", cause);
/*     */     } 
/*     */     
/* 202 */     context.setAttribute("http.connection", managedConn);
/*     */     
/* 204 */     if (config.isStaleConnectionCheckEnabled())
/*     */     {
/* 206 */       if (managedConn.isOpen()) {
/* 207 */         this.log.debug("Stale connection check");
/* 208 */         if (managedConn.isStale()) {
/* 209 */           this.log.debug("Stale connection detected");
/* 210 */           managedConn.close();
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/* 215 */     ConnectionHolder connHolder = new ConnectionHolder(this.log, this.connManager, managedConn); try {
/*     */       HttpResponse response;
/* 217 */       if (execAware != null) {
/* 218 */         execAware.setCancellable(connHolder);
/*     */       }
/*     */ 
/*     */       
/* 222 */       int execCount = 1;
/*     */       while (true) {
/* 224 */         if (execCount > 1 && !RequestEntityProxy.isRepeatable((HttpRequest)request)) {
/* 225 */           throw new NonRepeatableRequestException("Cannot retry request with a non-repeatable request entity.");
/*     */         }
/*     */ 
/*     */         
/* 229 */         if (execAware != null && execAware.isAborted()) {
/* 230 */           throw new RequestAbortedException("Request aborted");
/*     */         }
/*     */         
/* 233 */         if (!managedConn.isOpen()) {
/* 234 */           this.log.debug("Opening connection " + route);
/*     */           try {
/* 236 */             establishRoute(proxyAuthState, managedConn, route, (HttpRequest)request, context);
/* 237 */           } catch (TunnelRefusedException ex) {
/* 238 */             if (this.log.isDebugEnabled()) {
/* 239 */               this.log.debug(ex.getMessage());
/*     */             }
/* 241 */             HttpResponse httpResponse = ex.getResponse();
/*     */             break;
/*     */           } 
/*     */         } 
/* 245 */         int timeout = config.getSocketTimeout();
/* 246 */         if (timeout >= 0) {
/* 247 */           managedConn.setSocketTimeout(timeout);
/*     */         }
/*     */         
/* 250 */         if (execAware != null && execAware.isAborted()) {
/* 251 */           throw new RequestAbortedException("Request aborted");
/*     */         }
/*     */         
/* 254 */         if (this.log.isDebugEnabled()) {
/* 255 */           this.log.debug("Executing request " + request.getRequestLine());
/*     */         }
/*     */         
/* 258 */         if (!request.containsHeader("Authorization")) {
/* 259 */           if (this.log.isDebugEnabled()) {
/* 260 */             this.log.debug("Target auth state: " + targetAuthState.getState());
/*     */           }
/* 262 */           this.authenticator.generateAuthResponse((HttpRequest)request, targetAuthState, (HttpContext)context);
/*     */         } 
/* 264 */         if (!request.containsHeader("Proxy-Authorization") && !route.isTunnelled()) {
/* 265 */           if (this.log.isDebugEnabled()) {
/* 266 */             this.log.debug("Proxy auth state: " + proxyAuthState.getState());
/*     */           }
/* 268 */           this.authenticator.generateAuthResponse((HttpRequest)request, proxyAuthState, (HttpContext)context);
/*     */         } 
/*     */         
/* 271 */         context.setAttribute("http.request", request);
/* 272 */         response = this.requestExecutor.execute((HttpRequest)request, managedConn, (HttpContext)context);
/*     */ 
/*     */         
/* 275 */         if (this.reuseStrategy.keepAlive(response, (HttpContext)context)) {
/*     */           
/* 277 */           long duration = this.keepAliveStrategy.getKeepAliveDuration(response, (HttpContext)context);
/* 278 */           if (this.log.isDebugEnabled()) {
/*     */             String s;
/* 280 */             if (duration > 0L) {
/* 281 */               s = "for " + duration + " " + TimeUnit.MILLISECONDS;
/*     */             } else {
/* 283 */               s = "indefinitely";
/*     */             } 
/* 285 */             this.log.debug("Connection can be kept alive " + s);
/*     */           } 
/* 287 */           connHolder.setValidFor(duration, TimeUnit.MILLISECONDS);
/* 288 */           connHolder.markReusable();
/*     */         } else {
/* 290 */           connHolder.markNonReusable();
/*     */         } 
/*     */         
/* 293 */         if (needAuthentication(targetAuthState, proxyAuthState, route, response, context)) {
/*     */ 
/*     */           
/* 296 */           HttpEntity httpEntity = response.getEntity();
/* 297 */           if (connHolder.isReusable()) {
/* 298 */             EntityUtils.consume(httpEntity);
/*     */           } else {
/* 300 */             managedConn.close();
/* 301 */             if (proxyAuthState.getState() == AuthProtocolState.SUCCESS && proxyAuthState.isConnectionBased()) {
/*     */               
/* 303 */               this.log.debug("Resetting proxy auth state");
/* 304 */               proxyAuthState.reset();
/*     */             } 
/* 306 */             if (targetAuthState.getState() == AuthProtocolState.SUCCESS && targetAuthState.isConnectionBased()) {
/*     */               
/* 308 */               this.log.debug("Resetting target auth state");
/* 309 */               targetAuthState.reset();
/*     */             } 
/*     */           } 
/*     */           
/* 313 */           HttpRequest original = request.getOriginal();
/* 314 */           if (!original.containsHeader("Authorization")) {
/* 315 */             request.removeHeaders("Authorization");
/*     */           }
/* 317 */           if (!original.containsHeader("Proxy-Authorization")) {
/* 318 */             request.removeHeaders("Proxy-Authorization");
/*     */           }
/*     */           
/*     */           execCount++;
/*     */         } 
/*     */         break;
/*     */       } 
/* 325 */       if (userToken == null) {
/* 326 */         userToken = this.userTokenHandler.getUserToken((HttpContext)context);
/* 327 */         context.setAttribute("http.user-token", userToken);
/*     */       } 
/* 329 */       if (userToken != null) {
/* 330 */         connHolder.setState(userToken);
/*     */       }
/*     */ 
/*     */       
/* 334 */       HttpEntity entity = response.getEntity();
/* 335 */       if (entity == null || !entity.isStreaming()) {
/*     */         
/* 337 */         connHolder.releaseConnection();
/* 338 */         return new HttpResponseProxy(response, null);
/*     */       } 
/* 340 */       return new HttpResponseProxy(response, connHolder);
/* 341 */     } catch (ConnectionShutdownException ex) {
/* 342 */       InterruptedIOException ioex = new InterruptedIOException("Connection has been shut down");
/*     */       
/* 344 */       ioex.initCause((Throwable)ex);
/* 345 */       throw ioex;
/* 346 */     } catch (HttpException ex) {
/* 347 */       connHolder.abortConnection();
/* 348 */       throw ex;
/* 349 */     } catch (IOException ex) {
/* 350 */       connHolder.abortConnection();
/* 351 */       if (proxyAuthState.isConnectionBased()) {
/* 352 */         proxyAuthState.reset();
/*     */       }
/* 354 */       if (targetAuthState.isConnectionBased()) {
/* 355 */         targetAuthState.reset();
/*     */       }
/* 357 */       throw ex;
/* 358 */     } catch (RuntimeException ex) {
/* 359 */       connHolder.abortConnection();
/* 360 */       if (proxyAuthState.isConnectionBased()) {
/* 361 */         proxyAuthState.reset();
/*     */       }
/* 363 */       if (targetAuthState.isConnectionBased()) {
/* 364 */         targetAuthState.reset();
/*     */       }
/* 366 */       throw ex;
/* 367 */     } catch (Error error) {
/* 368 */       this.connManager.shutdown();
/* 369 */       throw error;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void establishRoute(AuthState proxyAuthState, HttpClientConnection managedConn, HttpRoute route, HttpRequest request, HttpClientContext context) throws HttpException, IOException {
/*     */     int step;
/* 382 */     RequestConfig config = context.getRequestConfig();
/* 383 */     int timeout = config.getConnectTimeout();
/* 384 */     RouteTracker tracker = new RouteTracker(route); do {
/*     */       HttpHost proxy; boolean secure; int hop;
/*     */       boolean bool1;
/* 387 */       HttpRoute fact = tracker.toRoute();
/* 388 */       step = this.routeDirector.nextStep((RouteInfo)route, (RouteInfo)fact);
/*     */       
/* 390 */       switch (step) {
/*     */         
/*     */         case 1:
/* 393 */           this.connManager.connect(managedConn, route, (timeout > 0) ? timeout : 0, (HttpContext)context);
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 398 */           tracker.connectTarget(route.isSecure());
/*     */           break;
/*     */         case 2:
/* 401 */           this.connManager.connect(managedConn, route, (timeout > 0) ? timeout : 0, (HttpContext)context);
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 406 */           proxy = route.getProxyHost();
/* 407 */           tracker.connectProxy(proxy, (route.isSecure() && !route.isTunnelled()));
/*     */           break;
/*     */         case 3:
/* 410 */           secure = createTunnelToTarget(proxyAuthState, managedConn, route, request, context);
/*     */           
/* 412 */           this.log.debug("Tunnel to target created.");
/* 413 */           tracker.tunnelTarget(secure);
/*     */           break;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         case 4:
/* 421 */           hop = fact.getHopCount() - 1;
/* 422 */           bool1 = createTunnelToProxy(route, hop, context);
/* 423 */           this.log.debug("Tunnel to proxy created.");
/* 424 */           tracker.tunnelProxy(route.getHopTarget(hop), bool1);
/*     */           break;
/*     */         
/*     */         case 5:
/* 428 */           this.connManager.upgrade(managedConn, route, (HttpContext)context);
/* 429 */           tracker.layerProtocol(route.isSecure());
/*     */           break;
/*     */         
/*     */         case -1:
/* 433 */           throw new HttpException("Unable to establish route: planned = " + route + "; current = " + fact);
/*     */         
/*     */         case 0:
/* 436 */           this.connManager.routeComplete(managedConn, route, (HttpContext)context);
/*     */           break;
/*     */         default:
/* 439 */           throw new IllegalStateException("Unknown step indicator " + step + " from RouteDirector.");
/*     */       } 
/*     */ 
/*     */     
/* 443 */     } while (step > 0);
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
/*     */   private boolean createTunnelToTarget(AuthState proxyAuthState, HttpClientConnection managedConn, HttpRoute route, HttpRequest request, HttpClientContext context) throws HttpException, IOException {
/* 461 */     RequestConfig config = context.getRequestConfig();
/* 462 */     int timeout = config.getConnectTimeout();
/*     */     
/* 464 */     HttpHost target = route.getTargetHost();
/* 465 */     HttpHost proxy = route.getProxyHost();
/* 466 */     HttpResponse response = null;
/*     */     
/* 468 */     String authority = target.toHostString();
/* 469 */     BasicHttpRequest basicHttpRequest = new BasicHttpRequest("CONNECT", authority, request.getProtocolVersion());
/*     */     
/* 471 */     this.requestExecutor.preProcess((HttpRequest)basicHttpRequest, this.proxyHttpProcessor, (HttpContext)context);
/*     */     
/* 473 */     while (response == null) {
/* 474 */       if (!managedConn.isOpen()) {
/* 475 */         this.connManager.connect(managedConn, route, (timeout > 0) ? timeout : 0, (HttpContext)context);
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 482 */       basicHttpRequest.removeHeaders("Proxy-Authorization");
/* 483 */       this.authenticator.generateAuthResponse((HttpRequest)basicHttpRequest, proxyAuthState, (HttpContext)context);
/*     */       
/* 485 */       response = this.requestExecutor.execute((HttpRequest)basicHttpRequest, managedConn, (HttpContext)context);
/* 486 */       this.requestExecutor.postProcess(response, this.proxyHttpProcessor, (HttpContext)context);
/*     */       
/* 488 */       int i = response.getStatusLine().getStatusCode();
/* 489 */       if (i < 200) {
/* 490 */         throw new HttpException("Unexpected response to CONNECT request: " + response.getStatusLine());
/*     */       }
/*     */ 
/*     */       
/* 494 */       if (config.isAuthenticationEnabled() && 
/* 495 */         this.authenticator.isAuthenticationRequested(proxy, response, this.proxyAuthStrategy, proxyAuthState, (HttpContext)context))
/*     */       {
/* 497 */         if (this.authenticator.handleAuthChallenge(proxy, response, this.proxyAuthStrategy, proxyAuthState, (HttpContext)context)) {
/*     */ 
/*     */           
/* 500 */           if (this.reuseStrategy.keepAlive(response, (HttpContext)context)) {
/* 501 */             this.log.debug("Connection kept alive");
/*     */             
/* 503 */             HttpEntity entity = response.getEntity();
/* 504 */             EntityUtils.consume(entity);
/*     */           } else {
/* 506 */             managedConn.close();
/*     */           } 
/* 508 */           response = null;
/*     */         } 
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 514 */     int status = response.getStatusLine().getStatusCode();
/*     */     
/* 516 */     if (status > 299) {
/*     */ 
/*     */       
/* 519 */       HttpEntity entity = response.getEntity();
/* 520 */       if (entity != null) {
/* 521 */         response.setEntity((HttpEntity)new BufferedHttpEntity(entity));
/*     */       }
/*     */       
/* 524 */       managedConn.close();
/* 525 */       throw new TunnelRefusedException("CONNECT refused by proxy: " + response.getStatusLine(), response);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 533 */     return false;
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
/*     */   private boolean createTunnelToProxy(HttpRoute route, int hop, HttpClientContext context) throws HttpException {
/* 555 */     throw new HttpException("Proxy chains are not supported.");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean needAuthentication(AuthState targetAuthState, AuthState proxyAuthState, HttpRoute route, HttpResponse response, HttpClientContext context) {
/* 564 */     RequestConfig config = context.getRequestConfig();
/* 565 */     if (config.isAuthenticationEnabled()) {
/* 566 */       HttpHost target = context.getTargetHost();
/* 567 */       if (target == null) {
/* 568 */         target = route.getTargetHost();
/*     */       }
/* 570 */       if (target.getPort() < 0) {
/* 571 */         target = new HttpHost(target.getHostName(), route.getTargetHost().getPort(), target.getSchemeName());
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 576 */       boolean targetAuthRequested = this.authenticator.isAuthenticationRequested(target, response, this.targetAuthStrategy, targetAuthState, (HttpContext)context);
/*     */ 
/*     */       
/* 579 */       HttpHost proxy = route.getProxyHost();
/*     */       
/* 581 */       if (proxy == null) {
/* 582 */         proxy = route.getTargetHost();
/*     */       }
/* 584 */       boolean proxyAuthRequested = this.authenticator.isAuthenticationRequested(proxy, response, this.proxyAuthStrategy, proxyAuthState, (HttpContext)context);
/*     */ 
/*     */       
/* 587 */       if (targetAuthRequested) {
/* 588 */         return this.authenticator.handleAuthChallenge(target, response, this.targetAuthStrategy, targetAuthState, (HttpContext)context);
/*     */       }
/*     */       
/* 591 */       if (proxyAuthRequested) {
/* 592 */         return this.authenticator.handleAuthChallenge(proxy, response, this.proxyAuthStrategy, proxyAuthState, (HttpContext)context);
/*     */       }
/*     */     } 
/*     */     
/* 596 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\execchain\MainClientExec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */