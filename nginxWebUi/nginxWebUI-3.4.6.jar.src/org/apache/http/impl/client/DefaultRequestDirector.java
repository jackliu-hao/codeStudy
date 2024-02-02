/*      */ package org.apache.http.impl.client;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.InterruptedIOException;
/*      */ import java.net.URI;
/*      */ import java.net.URISyntaxException;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import org.apache.commons.logging.Log;
/*      */ import org.apache.commons.logging.LogFactory;
/*      */ import org.apache.http.ConnectionReuseStrategy;
/*      */ import org.apache.http.HttpClientConnection;
/*      */ import org.apache.http.HttpEntity;
/*      */ import org.apache.http.HttpEntityEnclosingRequest;
/*      */ import org.apache.http.HttpException;
/*      */ import org.apache.http.HttpHost;
/*      */ import org.apache.http.HttpRequest;
/*      */ import org.apache.http.HttpResponse;
/*      */ import org.apache.http.NoHttpResponseException;
/*      */ import org.apache.http.ProtocolException;
/*      */ import org.apache.http.ProtocolVersion;
/*      */ import org.apache.http.auth.AuthProtocolState;
/*      */ import org.apache.http.auth.AuthScheme;
/*      */ import org.apache.http.auth.AuthState;
/*      */ import org.apache.http.auth.Credentials;
/*      */ import org.apache.http.auth.UsernamePasswordCredentials;
/*      */ import org.apache.http.client.AuthenticationHandler;
/*      */ import org.apache.http.client.AuthenticationStrategy;
/*      */ import org.apache.http.client.HttpRequestRetryHandler;
/*      */ import org.apache.http.client.NonRepeatableRequestException;
/*      */ import org.apache.http.client.RedirectException;
/*      */ import org.apache.http.client.RedirectHandler;
/*      */ import org.apache.http.client.RedirectStrategy;
/*      */ import org.apache.http.client.RequestDirector;
/*      */ import org.apache.http.client.UserTokenHandler;
/*      */ import org.apache.http.client.methods.AbortableHttpRequest;
/*      */ import org.apache.http.client.methods.HttpUriRequest;
/*      */ import org.apache.http.client.params.HttpClientParams;
/*      */ import org.apache.http.client.utils.URIUtils;
/*      */ import org.apache.http.conn.BasicManagedEntity;
/*      */ import org.apache.http.conn.ClientConnectionManager;
/*      */ import org.apache.http.conn.ClientConnectionRequest;
/*      */ import org.apache.http.conn.ConnectionKeepAliveStrategy;
/*      */ import org.apache.http.conn.ConnectionReleaseTrigger;
/*      */ import org.apache.http.conn.ManagedClientConnection;
/*      */ import org.apache.http.conn.routing.BasicRouteDirector;
/*      */ import org.apache.http.conn.routing.HttpRoute;
/*      */ import org.apache.http.conn.routing.HttpRoutePlanner;
/*      */ import org.apache.http.conn.routing.RouteInfo;
/*      */ import org.apache.http.conn.scheme.Scheme;
/*      */ import org.apache.http.entity.BufferedHttpEntity;
/*      */ import org.apache.http.impl.auth.BasicScheme;
/*      */ import org.apache.http.impl.conn.ConnectionShutdownException;
/*      */ import org.apache.http.message.BasicHttpRequest;
/*      */ import org.apache.http.params.HttpConnectionParams;
/*      */ import org.apache.http.params.HttpParams;
/*      */ import org.apache.http.params.HttpProtocolParams;
/*      */ import org.apache.http.protocol.HttpContext;
/*      */ import org.apache.http.protocol.HttpProcessor;
/*      */ import org.apache.http.protocol.HttpRequestExecutor;
/*      */ import org.apache.http.util.Args;
/*      */ import org.apache.http.util.EntityUtils;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ @Deprecated
/*      */ public class DefaultRequestDirector
/*      */   implements RequestDirector
/*      */ {
/*      */   private final Log log;
/*      */   protected final ClientConnectionManager connManager;
/*      */   protected final HttpRoutePlanner routePlanner;
/*      */   protected final ConnectionReuseStrategy reuseStrategy;
/*      */   protected final ConnectionKeepAliveStrategy keepAliveStrategy;
/*      */   protected final HttpRequestExecutor requestExec;
/*      */   protected final HttpProcessor httpProcessor;
/*      */   protected final HttpRequestRetryHandler retryHandler;
/*      */   protected final RedirectHandler redirectHandler;
/*      */   protected final RedirectStrategy redirectStrategy;
/*      */   protected final AuthenticationHandler targetAuthHandler;
/*      */   protected final AuthenticationStrategy targetAuthStrategy;
/*      */   protected final AuthenticationHandler proxyAuthHandler;
/*      */   protected final AuthenticationStrategy proxyAuthStrategy;
/*      */   protected final UserTokenHandler userTokenHandler;
/*      */   protected final HttpParams params;
/*      */   protected ManagedClientConnection managedConn;
/*      */   protected final AuthState targetAuthState;
/*      */   protected final AuthState proxyAuthState;
/*      */   private final HttpAuthenticator authenticator;
/*      */   private int execCount;
/*      */   private int redirectCount;
/*      */   private final int maxRedirects;
/*      */   private HttpHost virtualHost;
/*      */   
/*      */   public DefaultRequestDirector(HttpRequestExecutor requestExec, ClientConnectionManager conman, ConnectionReuseStrategy reustrat, ConnectionKeepAliveStrategy kastrat, HttpRoutePlanner rouplan, HttpProcessor httpProcessor, HttpRequestRetryHandler retryHandler, RedirectHandler redirectHandler, AuthenticationHandler targetAuthHandler, AuthenticationHandler proxyAuthHandler, UserTokenHandler userTokenHandler, HttpParams params) {
/*  213 */     this(LogFactory.getLog(DefaultRequestDirector.class), requestExec, conman, reustrat, kastrat, rouplan, httpProcessor, retryHandler, new DefaultRedirectStrategyAdaptor(redirectHandler), new AuthenticationStrategyAdaptor(targetAuthHandler), new AuthenticationStrategyAdaptor(proxyAuthHandler), userTokenHandler, params);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DefaultRequestDirector(Log log, HttpRequestExecutor requestExec, ClientConnectionManager conman, ConnectionReuseStrategy reustrat, ConnectionKeepAliveStrategy kastrat, HttpRoutePlanner rouplan, HttpProcessor httpProcessor, HttpRequestRetryHandler retryHandler, RedirectStrategy redirectStrategy, AuthenticationHandler targetAuthHandler, AuthenticationHandler proxyAuthHandler, UserTokenHandler userTokenHandler, HttpParams params) {
/*  237 */     this(LogFactory.getLog(DefaultRequestDirector.class), requestExec, conman, reustrat, kastrat, rouplan, httpProcessor, retryHandler, redirectStrategy, new AuthenticationStrategyAdaptor(targetAuthHandler), new AuthenticationStrategyAdaptor(proxyAuthHandler), userTokenHandler, params);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public DefaultRequestDirector(Log log, HttpRequestExecutor requestExec, ClientConnectionManager conman, ConnectionReuseStrategy reustrat, ConnectionKeepAliveStrategy kastrat, HttpRoutePlanner rouplan, HttpProcessor httpProcessor, HttpRequestRetryHandler retryHandler, RedirectStrategy redirectStrategy, AuthenticationStrategy targetAuthStrategy, AuthenticationStrategy proxyAuthStrategy, UserTokenHandler userTokenHandler, HttpParams params) {
/*  264 */     Args.notNull(log, "Log");
/*  265 */     Args.notNull(requestExec, "Request executor");
/*  266 */     Args.notNull(conman, "Client connection manager");
/*  267 */     Args.notNull(reustrat, "Connection reuse strategy");
/*  268 */     Args.notNull(kastrat, "Connection keep alive strategy");
/*  269 */     Args.notNull(rouplan, "Route planner");
/*  270 */     Args.notNull(httpProcessor, "HTTP protocol processor");
/*  271 */     Args.notNull(retryHandler, "HTTP request retry handler");
/*  272 */     Args.notNull(redirectStrategy, "Redirect strategy");
/*  273 */     Args.notNull(targetAuthStrategy, "Target authentication strategy");
/*  274 */     Args.notNull(proxyAuthStrategy, "Proxy authentication strategy");
/*  275 */     Args.notNull(userTokenHandler, "User token handler");
/*  276 */     Args.notNull(params, "HTTP parameters");
/*  277 */     this.log = log;
/*  278 */     this.authenticator = new HttpAuthenticator(log);
/*  279 */     this.requestExec = requestExec;
/*  280 */     this.connManager = conman;
/*  281 */     this.reuseStrategy = reustrat;
/*  282 */     this.keepAliveStrategy = kastrat;
/*  283 */     this.routePlanner = rouplan;
/*  284 */     this.httpProcessor = httpProcessor;
/*  285 */     this.retryHandler = retryHandler;
/*  286 */     this.redirectStrategy = redirectStrategy;
/*  287 */     this.targetAuthStrategy = targetAuthStrategy;
/*  288 */     this.proxyAuthStrategy = proxyAuthStrategy;
/*  289 */     this.userTokenHandler = userTokenHandler;
/*  290 */     this.params = params;
/*      */     
/*  292 */     if (redirectStrategy instanceof DefaultRedirectStrategyAdaptor) {
/*  293 */       this.redirectHandler = ((DefaultRedirectStrategyAdaptor)redirectStrategy).getHandler();
/*      */     } else {
/*  295 */       this.redirectHandler = null;
/*      */     } 
/*  297 */     if (targetAuthStrategy instanceof AuthenticationStrategyAdaptor) {
/*  298 */       this.targetAuthHandler = ((AuthenticationStrategyAdaptor)targetAuthStrategy).getHandler();
/*      */     } else {
/*  300 */       this.targetAuthHandler = null;
/*      */     } 
/*  302 */     if (proxyAuthStrategy instanceof AuthenticationStrategyAdaptor) {
/*  303 */       this.proxyAuthHandler = ((AuthenticationStrategyAdaptor)proxyAuthStrategy).getHandler();
/*      */     } else {
/*  305 */       this.proxyAuthHandler = null;
/*      */     } 
/*      */     
/*  308 */     this.managedConn = null;
/*      */     
/*  310 */     this.execCount = 0;
/*  311 */     this.redirectCount = 0;
/*  312 */     this.targetAuthState = new AuthState();
/*  313 */     this.proxyAuthState = new AuthState();
/*  314 */     this.maxRedirects = this.params.getIntParameter("http.protocol.max-redirects", 100);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private RequestWrapper wrapRequest(HttpRequest request) throws ProtocolException {
/*  320 */     if (request instanceof HttpEntityEnclosingRequest) {
/*  321 */       return new EntityEnclosingRequestWrapper((HttpEntityEnclosingRequest)request);
/*      */     }
/*      */     
/*  324 */     return new RequestWrapper(request);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void rewriteRequestURI(RequestWrapper request, HttpRoute route) throws ProtocolException {
/*      */     try {
/*  335 */       URI uri = request.getURI();
/*  336 */       if (route.getProxyHost() != null && !route.isTunnelled()) {
/*      */         
/*  338 */         if (!uri.isAbsolute()) {
/*  339 */           HttpHost target = route.getTargetHost();
/*  340 */           uri = URIUtils.rewriteURI(uri, target, URIUtils.DROP_FRAGMENT_AND_NORMALIZE);
/*      */         } else {
/*  342 */           uri = URIUtils.rewriteURI(uri);
/*      */         }
/*      */       
/*      */       }
/*  346 */       else if (uri.isAbsolute()) {
/*  347 */         uri = URIUtils.rewriteURI(uri, null, URIUtils.DROP_FRAGMENT_AND_NORMALIZE);
/*      */       } else {
/*  349 */         uri = URIUtils.rewriteURI(uri);
/*      */       } 
/*      */       
/*  352 */       request.setURI(uri);
/*      */     }
/*  354 */     catch (URISyntaxException ex) {
/*  355 */       throw new ProtocolException("Invalid URI: " + request.getRequestLine().getUri(), ex);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpResponse execute(HttpHost targetHost, HttpRequest request, HttpContext context) throws HttpException, IOException {
/*  367 */     context.setAttribute("http.auth.target-scope", this.targetAuthState);
/*  368 */     context.setAttribute("http.auth.proxy-scope", this.proxyAuthState);
/*      */     
/*  370 */     HttpHost target = targetHost;
/*      */     
/*  372 */     HttpRequest orig = request;
/*  373 */     RequestWrapper origWrapper = wrapRequest(orig);
/*  374 */     origWrapper.setParams(this.params);
/*  375 */     HttpRoute origRoute = determineRoute(target, (HttpRequest)origWrapper, context);
/*      */     
/*  377 */     this.virtualHost = (HttpHost)origWrapper.getParams().getParameter("http.virtual-host");
/*      */ 
/*      */     
/*  380 */     if (this.virtualHost != null && this.virtualHost.getPort() == -1) {
/*  381 */       HttpHost host = (target != null) ? target : origRoute.getTargetHost();
/*  382 */       int port = host.getPort();
/*  383 */       if (port != -1) {
/*  384 */         this.virtualHost = new HttpHost(this.virtualHost.getHostName(), port, this.virtualHost.getSchemeName());
/*      */       }
/*      */     } 
/*      */     
/*  388 */     RoutedRequest roureq = new RoutedRequest(origWrapper, origRoute);
/*      */     
/*  390 */     boolean reuse = false;
/*  391 */     boolean done = false;
/*      */     try {
/*  393 */       HttpResponse response = null;
/*  394 */       while (!done) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  400 */         RequestWrapper wrapper = roureq.getRequest();
/*  401 */         HttpRoute route = roureq.getRoute();
/*  402 */         response = null;
/*      */ 
/*      */         
/*  405 */         Object userToken = context.getAttribute("http.user-token");
/*      */ 
/*      */         
/*  408 */         if (this.managedConn == null) {
/*  409 */           ClientConnectionRequest connRequest = this.connManager.requestConnection(route, userToken);
/*      */           
/*  411 */           if (orig instanceof AbortableHttpRequest) {
/*  412 */             ((AbortableHttpRequest)orig).setConnectionRequest(connRequest);
/*      */           }
/*      */           
/*  415 */           long timeout = HttpClientParams.getConnectionManagerTimeout(this.params);
/*      */           try {
/*  417 */             this.managedConn = connRequest.getConnection(timeout, TimeUnit.MILLISECONDS);
/*  418 */           } catch (InterruptedException interrupted) {
/*  419 */             Thread.currentThread().interrupt();
/*  420 */             throw new InterruptedIOException();
/*      */           } 
/*      */           
/*  423 */           if (HttpConnectionParams.isStaleCheckingEnabled(this.params))
/*      */           {
/*  425 */             if (this.managedConn.isOpen()) {
/*  426 */               this.log.debug("Stale connection check");
/*  427 */               if (this.managedConn.isStale()) {
/*  428 */                 this.log.debug("Stale connection detected");
/*  429 */                 this.managedConn.close();
/*      */               } 
/*      */             } 
/*      */           }
/*      */         } 
/*      */         
/*  435 */         if (orig instanceof AbortableHttpRequest) {
/*  436 */           ((AbortableHttpRequest)orig).setReleaseTrigger((ConnectionReleaseTrigger)this.managedConn);
/*      */         }
/*      */         
/*      */         try {
/*  440 */           tryConnect(roureq, context);
/*  441 */         } catch (TunnelRefusedException ex) {
/*  442 */           if (this.log.isDebugEnabled()) {
/*  443 */             this.log.debug(ex.getMessage());
/*      */           }
/*  445 */           response = ex.getResponse();
/*      */           
/*      */           break;
/*      */         } 
/*  449 */         String userinfo = wrapper.getURI().getUserInfo();
/*  450 */         if (userinfo != null) {
/*  451 */           this.targetAuthState.update((AuthScheme)new BasicScheme(), (Credentials)new UsernamePasswordCredentials(userinfo));
/*      */         }
/*      */ 
/*      */ 
/*      */         
/*  456 */         if (this.virtualHost != null) {
/*  457 */           target = this.virtualHost;
/*      */         } else {
/*  459 */           URI requestURI = wrapper.getURI();
/*  460 */           if (requestURI.isAbsolute()) {
/*  461 */             target = URIUtils.extractHost(requestURI);
/*      */           }
/*      */         } 
/*  464 */         if (target == null) {
/*  465 */           target = route.getTargetHost();
/*      */         }
/*      */ 
/*      */         
/*  469 */         wrapper.resetHeaders();
/*      */         
/*  471 */         rewriteRequestURI(wrapper, route);
/*      */ 
/*      */         
/*  474 */         context.setAttribute("http.target_host", target);
/*  475 */         context.setAttribute("http.route", route);
/*  476 */         context.setAttribute("http.connection", this.managedConn);
/*      */ 
/*      */         
/*  479 */         this.requestExec.preProcess((HttpRequest)wrapper, this.httpProcessor, context);
/*      */         
/*  481 */         response = tryExecute(roureq, context);
/*  482 */         if (response == null) {
/*      */           continue;
/*      */         }
/*      */ 
/*      */ 
/*      */         
/*  488 */         response.setParams(this.params);
/*  489 */         this.requestExec.postProcess(response, this.httpProcessor, context);
/*      */ 
/*      */ 
/*      */         
/*  493 */         reuse = this.reuseStrategy.keepAlive(response, context);
/*  494 */         if (reuse) {
/*      */           
/*  496 */           long duration = this.keepAliveStrategy.getKeepAliveDuration(response, context);
/*  497 */           if (this.log.isDebugEnabled()) {
/*      */             String s;
/*  499 */             if (duration > 0L) {
/*  500 */               s = "for " + duration + " " + TimeUnit.MILLISECONDS;
/*      */             } else {
/*  502 */               s = "indefinitely";
/*      */             } 
/*  504 */             this.log.debug("Connection can be kept alive " + s);
/*      */           } 
/*  506 */           this.managedConn.setIdleDuration(duration, TimeUnit.MILLISECONDS);
/*      */         } 
/*      */         
/*  509 */         RoutedRequest followup = handleResponse(roureq, response, context);
/*  510 */         if (followup == null) {
/*  511 */           done = true;
/*      */         } else {
/*  513 */           if (reuse) {
/*      */             
/*  515 */             HttpEntity entity = response.getEntity();
/*  516 */             EntityUtils.consume(entity);
/*      */ 
/*      */             
/*  519 */             this.managedConn.markReusable();
/*      */           } else {
/*  521 */             this.managedConn.close();
/*  522 */             if (this.proxyAuthState.getState().compareTo((Enum)AuthProtocolState.CHALLENGED) > 0 && this.proxyAuthState.getAuthScheme() != null && this.proxyAuthState.getAuthScheme().isConnectionBased()) {
/*      */ 
/*      */               
/*  525 */               this.log.debug("Resetting proxy auth state");
/*  526 */               this.proxyAuthState.reset();
/*      */             } 
/*  528 */             if (this.targetAuthState.getState().compareTo((Enum)AuthProtocolState.CHALLENGED) > 0 && this.targetAuthState.getAuthScheme() != null && this.targetAuthState.getAuthScheme().isConnectionBased()) {
/*      */ 
/*      */               
/*  531 */               this.log.debug("Resetting target auth state");
/*  532 */               this.targetAuthState.reset();
/*      */             } 
/*      */           } 
/*      */           
/*  536 */           if (!followup.getRoute().equals(roureq.getRoute())) {
/*  537 */             releaseConnection();
/*      */           }
/*  539 */           roureq = followup;
/*      */         } 
/*      */         
/*  542 */         if (this.managedConn != null) {
/*  543 */           if (userToken == null) {
/*  544 */             userToken = this.userTokenHandler.getUserToken(context);
/*  545 */             context.setAttribute("http.user-token", userToken);
/*      */           } 
/*  547 */           if (userToken != null) {
/*  548 */             this.managedConn.setState(userToken);
/*      */           }
/*      */         } 
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  556 */       if (response == null || response.getEntity() == null || !response.getEntity().isStreaming()) {
/*      */ 
/*      */         
/*  559 */         if (reuse) {
/*  560 */           this.managedConn.markReusable();
/*      */         }
/*  562 */         releaseConnection();
/*      */       } else {
/*      */         
/*  565 */         HttpEntity entity = response.getEntity();
/*  566 */         BasicManagedEntity basicManagedEntity = new BasicManagedEntity(entity, this.managedConn, reuse);
/*  567 */         response.setEntity((HttpEntity)basicManagedEntity);
/*      */       } 
/*      */       
/*  570 */       return response;
/*      */     }
/*  572 */     catch (ConnectionShutdownException ex) {
/*  573 */       InterruptedIOException ioex = new InterruptedIOException("Connection has been shut down");
/*      */       
/*  575 */       ioex.initCause((Throwable)ex);
/*  576 */       throw ioex;
/*  577 */     } catch (HttpException ex) {
/*  578 */       abortConnection();
/*  579 */       throw ex;
/*  580 */     } catch (IOException ex) {
/*  581 */       abortConnection();
/*  582 */       throw ex;
/*  583 */     } catch (RuntimeException ex) {
/*  584 */       abortConnection();
/*  585 */       throw ex;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void tryConnect(RoutedRequest req, HttpContext context) throws HttpException, IOException {
/*  595 */     HttpRoute route = req.getRoute();
/*  596 */     RequestWrapper requestWrapper = req.getRequest();
/*      */     
/*  598 */     int connectCount = 0;
/*      */     while (true) {
/*  600 */       context.setAttribute("http.request", requestWrapper);
/*      */       
/*  602 */       connectCount++;
/*      */       try {
/*  604 */         if (!this.managedConn.isOpen()) {
/*  605 */           this.managedConn.open(route, context, this.params);
/*      */         } else {
/*  607 */           this.managedConn.setSocketTimeout(HttpConnectionParams.getSoTimeout(this.params));
/*      */         } 
/*  609 */         establishRoute(route, context);
/*      */       }
/*  611 */       catch (IOException ex) {
/*      */         try {
/*  613 */           this.managedConn.close();
/*  614 */         } catch (IOException ignore) {}
/*      */         
/*  616 */         if (this.retryHandler.retryRequest(ex, connectCount, context)) {
/*  617 */           if (this.log.isInfoEnabled()) {
/*  618 */             this.log.info("I/O exception (" + ex.getClass().getName() + ") caught when connecting to " + route + ": " + ex.getMessage());
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  623 */             if (this.log.isDebugEnabled()) {
/*  624 */               this.log.debug(ex.getMessage(), ex);
/*      */             }
/*  626 */             this.log.info("Retrying connect to " + route);
/*      */           }  continue;
/*      */         } 
/*  629 */         throw ex;
/*      */       } 
/*      */       break;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private HttpResponse tryExecute(RoutedRequest req, HttpContext context) throws HttpException, IOException {
/*  640 */     RequestWrapper wrapper = req.getRequest();
/*  641 */     HttpRoute route = req.getRoute();
/*  642 */     HttpResponse response = null;
/*      */     
/*  644 */     Exception retryReason = null;
/*      */     
/*      */     while (true) {
/*  647 */       this.execCount++;
/*      */       
/*  649 */       wrapper.incrementExecCount();
/*  650 */       if (!wrapper.isRepeatable()) {
/*  651 */         this.log.debug("Cannot retry non-repeatable request");
/*  652 */         if (retryReason != null) {
/*  653 */           throw new NonRepeatableRequestException("Cannot retry request with a non-repeatable request entity.  The cause lists the reason the original request failed.", retryReason);
/*      */         }
/*      */ 
/*      */         
/*  657 */         throw new NonRepeatableRequestException("Cannot retry request with a non-repeatable request entity.");
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*      */       try {
/*  663 */         if (!this.managedConn.isOpen())
/*      */         {
/*      */           
/*  666 */           if (!route.isTunnelled()) {
/*  667 */             this.log.debug("Reopening the direct connection.");
/*  668 */             this.managedConn.open(route, context, this.params);
/*      */           } else {
/*      */             
/*  671 */             this.log.debug("Proxied connection. Need to start over.");
/*      */             
/*      */             break;
/*      */           } 
/*      */         }
/*  676 */         if (this.log.isDebugEnabled()) {
/*  677 */           this.log.debug("Attempt " + this.execCount + " to execute request");
/*      */         }
/*  679 */         response = this.requestExec.execute((HttpRequest)wrapper, (HttpClientConnection)this.managedConn, context);
/*      */       
/*      */       }
/*  682 */       catch (IOException ex) {
/*  683 */         this.log.debug("Closing the connection.");
/*      */         try {
/*  685 */           this.managedConn.close();
/*  686 */         } catch (IOException ignore) {}
/*      */         
/*  688 */         if (this.retryHandler.retryRequest(ex, wrapper.getExecCount(), context)) {
/*  689 */           if (this.log.isInfoEnabled()) {
/*  690 */             this.log.info("I/O exception (" + ex.getClass().getName() + ") caught when processing request to " + route + ": " + ex.getMessage());
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  696 */           if (this.log.isDebugEnabled()) {
/*  697 */             this.log.debug(ex.getMessage(), ex);
/*      */           }
/*  699 */           if (this.log.isInfoEnabled()) {
/*  700 */             this.log.info("Retrying request to " + route);
/*      */           }
/*  702 */           retryReason = ex; continue;
/*      */         } 
/*  704 */         if (ex instanceof NoHttpResponseException) {
/*  705 */           NoHttpResponseException updatedex = new NoHttpResponseException(route.getTargetHost().toHostString() + " failed to respond");
/*      */           
/*  707 */           updatedex.setStackTrace(ex.getStackTrace());
/*  708 */           throw updatedex;
/*      */         } 
/*  710 */         throw ex;
/*      */       } 
/*      */       
/*      */       break;
/*      */     } 
/*  715 */     return response;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void releaseConnection() {
/*      */     try {
/*  728 */       this.managedConn.releaseConnection();
/*  729 */     } catch (IOException ignored) {
/*  730 */       this.log.debug("IOException releasing connection", ignored);
/*      */     } 
/*  732 */     this.managedConn = null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected HttpRoute determineRoute(HttpHost targetHost, HttpRequest request, HttpContext context) throws HttpException {
/*  756 */     return this.routePlanner.determineRoute((targetHost != null) ? targetHost : (HttpHost)request.getParams().getParameter("http.default-host"), request, context);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void establishRoute(HttpRoute route, HttpContext context) throws HttpException, IOException {
/*      */     int step;
/*  775 */     BasicRouteDirector basicRouteDirector = new BasicRouteDirector(); do {
/*      */       boolean secure; int hop;
/*      */       boolean bool1;
/*  778 */       HttpRoute fact = this.managedConn.getRoute();
/*  779 */       step = basicRouteDirector.nextStep((RouteInfo)route, (RouteInfo)fact);
/*      */       
/*  781 */       switch (step) {
/*      */         
/*      */         case 1:
/*      */         case 2:
/*  785 */           this.managedConn.open(route, context, this.params);
/*      */           break;
/*      */         
/*      */         case 3:
/*  789 */           secure = createTunnelToTarget(route, context);
/*  790 */           this.log.debug("Tunnel to target created.");
/*  791 */           this.managedConn.tunnelTarget(secure, this.params);
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 4:
/*  799 */           hop = fact.getHopCount() - 1;
/*  800 */           bool1 = createTunnelToProxy(route, hop, context);
/*  801 */           this.log.debug("Tunnel to proxy created.");
/*  802 */           this.managedConn.tunnelProxy(route.getHopTarget(hop), bool1, this.params);
/*      */           break;
/*      */ 
/*      */ 
/*      */         
/*      */         case 5:
/*  808 */           this.managedConn.layerProtocol(context, this.params);
/*      */           break;
/*      */         
/*      */         case -1:
/*  812 */           throw new HttpException("Unable to establish route: planned = " + route + "; current = " + fact);
/*      */         
/*      */         case 0:
/*      */           break;
/*      */         
/*      */         default:
/*  818 */           throw new IllegalStateException("Unknown step indicator " + step + " from RouteDirector.");
/*      */       } 
/*      */ 
/*      */     
/*  822 */     } while (step > 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean createTunnelToTarget(HttpRoute route, HttpContext context) throws HttpException, IOException {
/*  850 */     HttpHost proxy = route.getProxyHost();
/*  851 */     HttpHost target = route.getTargetHost();
/*  852 */     HttpResponse response = null;
/*      */     
/*      */     while (true) {
/*  855 */       if (!this.managedConn.isOpen()) {
/*  856 */         this.managedConn.open(route, context, this.params);
/*      */       }
/*      */       
/*  859 */       HttpRequest connect = createConnectRequest(route, context);
/*  860 */       connect.setParams(this.params);
/*      */ 
/*      */       
/*  863 */       context.setAttribute("http.target_host", target);
/*  864 */       context.setAttribute("http.route", route);
/*  865 */       context.setAttribute("http.proxy_host", proxy);
/*  866 */       context.setAttribute("http.connection", this.managedConn);
/*  867 */       context.setAttribute("http.request", connect);
/*      */       
/*  869 */       this.requestExec.preProcess(connect, this.httpProcessor, context);
/*      */       
/*  871 */       response = this.requestExec.execute(connect, (HttpClientConnection)this.managedConn, context);
/*      */       
/*  873 */       response.setParams(this.params);
/*  874 */       this.requestExec.postProcess(response, this.httpProcessor, context);
/*      */       
/*  876 */       int i = response.getStatusLine().getStatusCode();
/*  877 */       if (i < 200) {
/*  878 */         throw new HttpException("Unexpected response to CONNECT request: " + response.getStatusLine());
/*      */       }
/*      */ 
/*      */       
/*  882 */       if (HttpClientParams.isAuthenticating(this.params)) {
/*  883 */         if (this.authenticator.isAuthenticationRequested(proxy, response, this.proxyAuthStrategy, this.proxyAuthState, context))
/*      */         {
/*  885 */           if (this.authenticator.authenticate(proxy, response, this.proxyAuthStrategy, this.proxyAuthState, context)) {
/*      */ 
/*      */             
/*  888 */             if (this.reuseStrategy.keepAlive(response, context)) {
/*  889 */               this.log.debug("Connection kept alive");
/*      */               
/*  891 */               HttpEntity entity = response.getEntity();
/*  892 */               EntityUtils.consume(entity); continue;
/*      */             } 
/*  894 */             this.managedConn.close();
/*      */ 
/*      */             
/*      */             continue;
/*      */           } 
/*      */         }
/*      */         
/*      */         break;
/*      */       } 
/*      */     } 
/*      */     
/*  905 */     int status = response.getStatusLine().getStatusCode();
/*      */     
/*  907 */     if (status > 299) {
/*      */ 
/*      */       
/*  910 */       HttpEntity entity = response.getEntity();
/*  911 */       if (entity != null) {
/*  912 */         response.setEntity((HttpEntity)new BufferedHttpEntity(entity));
/*      */       }
/*      */       
/*  915 */       this.managedConn.close();
/*  916 */       throw new TunnelRefusedException("CONNECT refused by proxy: " + response.getStatusLine(), response);
/*      */     } 
/*      */ 
/*      */     
/*  920 */     this.managedConn.markReusable();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  926 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean createTunnelToProxy(HttpRoute route, int hop, HttpContext context) throws HttpException, IOException {
/*  962 */     throw new HttpException("Proxy chains are not supported.");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected HttpRequest createConnectRequest(HttpRoute route, HttpContext context) {
/*  982 */     HttpHost target = route.getTargetHost();
/*      */     
/*  984 */     String host = target.getHostName();
/*  985 */     int port = target.getPort();
/*  986 */     if (port < 0) {
/*  987 */       Scheme scheme = this.connManager.getSchemeRegistry().getScheme(target.getSchemeName());
/*      */       
/*  989 */       port = scheme.getDefaultPort();
/*      */     } 
/*      */     
/*  992 */     StringBuilder buffer = new StringBuilder(host.length() + 6);
/*  993 */     buffer.append(host);
/*  994 */     buffer.append(':');
/*  995 */     buffer.append(Integer.toString(port));
/*      */     
/*  997 */     String authority = buffer.toString();
/*  998 */     ProtocolVersion ver = HttpProtocolParams.getVersion(this.params);
/*  999 */     return (HttpRequest)new BasicHttpRequest("CONNECT", authority, ver);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected RoutedRequest handleResponse(RoutedRequest roureq, HttpResponse response, HttpContext context) throws HttpException, IOException {
/* 1024 */     HttpRoute route = roureq.getRoute();
/* 1025 */     RequestWrapper request = roureq.getRequest();
/*      */     
/* 1027 */     HttpParams params = request.getParams();
/*      */     
/* 1029 */     if (HttpClientParams.isAuthenticating(params)) {
/* 1030 */       HttpHost target = (HttpHost)context.getAttribute("http.target_host");
/* 1031 */       if (target == null) {
/* 1032 */         target = route.getTargetHost();
/*      */       }
/* 1034 */       if (target.getPort() < 0) {
/* 1035 */         Scheme scheme = this.connManager.getSchemeRegistry().getScheme(target);
/* 1036 */         target = new HttpHost(target.getHostName(), scheme.getDefaultPort(), target.getSchemeName());
/*      */       } 
/*      */       
/* 1039 */       boolean targetAuthRequested = this.authenticator.isAuthenticationRequested(target, response, this.targetAuthStrategy, this.targetAuthState, context);
/*      */ 
/*      */       
/* 1042 */       HttpHost proxy = route.getProxyHost();
/*      */       
/* 1044 */       if (proxy == null) {
/* 1045 */         proxy = route.getTargetHost();
/*      */       }
/* 1047 */       boolean proxyAuthRequested = this.authenticator.isAuthenticationRequested(proxy, response, this.proxyAuthStrategy, this.proxyAuthState, context);
/*      */ 
/*      */       
/* 1050 */       if (targetAuthRequested && 
/* 1051 */         this.authenticator.authenticate(target, response, this.targetAuthStrategy, this.targetAuthState, context))
/*      */       {
/*      */         
/* 1054 */         return roureq;
/*      */       }
/*      */       
/* 1057 */       if (proxyAuthRequested && 
/* 1058 */         this.authenticator.authenticate(proxy, response, this.proxyAuthStrategy, this.proxyAuthState, context))
/*      */       {
/*      */         
/* 1061 */         return roureq;
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 1066 */     if (HttpClientParams.isRedirecting(params) && this.redirectStrategy.isRedirected((HttpRequest)request, response, context)) {
/*      */ 
/*      */       
/* 1069 */       if (this.redirectCount >= this.maxRedirects) {
/* 1070 */         throw new RedirectException("Maximum redirects (" + this.maxRedirects + ") exceeded");
/*      */       }
/*      */       
/* 1073 */       this.redirectCount++;
/*      */ 
/*      */       
/* 1076 */       this.virtualHost = null;
/*      */       
/* 1078 */       HttpUriRequest redirect = this.redirectStrategy.getRedirect((HttpRequest)request, response, context);
/* 1079 */       HttpRequest orig = request.getOriginal();
/* 1080 */       redirect.setHeaders(orig.getAllHeaders());
/*      */       
/* 1082 */       URI uri = redirect.getURI();
/* 1083 */       HttpHost newTarget = URIUtils.extractHost(uri);
/* 1084 */       if (newTarget == null) {
/* 1085 */         throw new ProtocolException("Redirect URI does not specify a valid host name: " + uri);
/*      */       }
/*      */ 
/*      */       
/* 1089 */       if (!route.getTargetHost().equals(newTarget)) {
/* 1090 */         this.log.debug("Resetting target auth state");
/* 1091 */         this.targetAuthState.reset();
/* 1092 */         AuthScheme authScheme = this.proxyAuthState.getAuthScheme();
/* 1093 */         if (authScheme != null && authScheme.isConnectionBased()) {
/* 1094 */           this.log.debug("Resetting proxy auth state");
/* 1095 */           this.proxyAuthState.reset();
/*      */         } 
/*      */       } 
/*      */       
/* 1099 */       RequestWrapper wrapper = wrapRequest((HttpRequest)redirect);
/* 1100 */       wrapper.setParams(params);
/*      */       
/* 1102 */       HttpRoute newRoute = determineRoute(newTarget, (HttpRequest)wrapper, context);
/* 1103 */       RoutedRequest newRequest = new RoutedRequest(wrapper, newRoute);
/*      */       
/* 1105 */       if (this.log.isDebugEnabled()) {
/* 1106 */         this.log.debug("Redirecting to '" + uri + "' via " + newRoute);
/*      */       }
/*      */       
/* 1109 */       return newRequest;
/*      */     } 
/*      */     
/* 1112 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void abortConnection() {
/* 1122 */     ManagedClientConnection mcc = this.managedConn;
/* 1123 */     if (mcc != null) {
/*      */ 
/*      */       
/* 1126 */       this.managedConn = null;
/*      */       try {
/* 1128 */         mcc.abortConnection();
/* 1129 */       } catch (IOException ex) {
/* 1130 */         if (this.log.isDebugEnabled()) {
/* 1131 */           this.log.debug(ex.getMessage(), ex);
/*      */         }
/*      */       } 
/*      */       
/*      */       try {
/* 1136 */         mcc.releaseConnection();
/* 1137 */       } catch (IOException ignored) {
/* 1138 */         this.log.debug("Error releasing connection", ignored);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\client\DefaultRequestDirector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */