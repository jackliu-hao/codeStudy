/*     */ package org.apache.http.impl.client;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.UndeclaredThrowableException;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.ConnectionReuseStrategy;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpRequestInterceptor;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.HttpResponseInterceptor;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.auth.AuthSchemeFactory;
/*     */ import org.apache.http.auth.AuthSchemeRegistry;
/*     */ import org.apache.http.client.AuthenticationHandler;
/*     */ import org.apache.http.client.AuthenticationStrategy;
/*     */ import org.apache.http.client.BackoffManager;
/*     */ import org.apache.http.client.ClientProtocolException;
/*     */ import org.apache.http.client.ConnectionBackoffStrategy;
/*     */ import org.apache.http.client.CookieStore;
/*     */ import org.apache.http.client.CredentialsProvider;
/*     */ import org.apache.http.client.HttpRequestRetryHandler;
/*     */ import org.apache.http.client.RedirectHandler;
/*     */ import org.apache.http.client.RedirectStrategy;
/*     */ import org.apache.http.client.RequestDirector;
/*     */ import org.apache.http.client.UserTokenHandler;
/*     */ import org.apache.http.client.config.RequestConfig;
/*     */ import org.apache.http.client.methods.CloseableHttpResponse;
/*     */ import org.apache.http.client.params.HttpClientParamConfig;
/*     */ import org.apache.http.conn.ClientConnectionManager;
/*     */ import org.apache.http.conn.ClientConnectionManagerFactory;
/*     */ import org.apache.http.conn.ConnectionKeepAliveStrategy;
/*     */ import org.apache.http.conn.routing.HttpRoute;
/*     */ import org.apache.http.conn.routing.HttpRoutePlanner;
/*     */ import org.apache.http.conn.scheme.SchemeRegistry;
/*     */ import org.apache.http.cookie.CookieSpecFactory;
/*     */ import org.apache.http.cookie.CookieSpecRegistry;
/*     */ import org.apache.http.impl.DefaultConnectionReuseStrategy;
/*     */ import org.apache.http.impl.auth.BasicSchemeFactory;
/*     */ import org.apache.http.impl.auth.DigestSchemeFactory;
/*     */ import org.apache.http.impl.auth.KerberosSchemeFactory;
/*     */ import org.apache.http.impl.auth.NTLMSchemeFactory;
/*     */ import org.apache.http.impl.auth.SPNegoSchemeFactory;
/*     */ import org.apache.http.impl.conn.BasicClientConnectionManager;
/*     */ import org.apache.http.impl.conn.DefaultHttpRoutePlanner;
/*     */ import org.apache.http.impl.conn.SchemeRegistryFactory;
/*     */ import org.apache.http.impl.cookie.BestMatchSpecFactory;
/*     */ import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
/*     */ import org.apache.http.impl.cookie.IgnoreSpecFactory;
/*     */ import org.apache.http.impl.cookie.NetscapeDraftSpecFactory;
/*     */ import org.apache.http.impl.cookie.RFC2109SpecFactory;
/*     */ import org.apache.http.impl.cookie.RFC2965SpecFactory;
/*     */ import org.apache.http.params.HttpParams;
/*     */ import org.apache.http.protocol.BasicHttpContext;
/*     */ import org.apache.http.protocol.BasicHttpProcessor;
/*     */ import org.apache.http.protocol.DefaultedHttpContext;
/*     */ import org.apache.http.protocol.HttpContext;
/*     */ import org.apache.http.protocol.HttpProcessor;
/*     */ import org.apache.http.protocol.HttpRequestExecutor;
/*     */ import org.apache.http.protocol.ImmutableHttpProcessor;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ public abstract class AbstractHttpClient
/*     */   extends CloseableHttpClient
/*     */ {
/* 201 */   private final Log log = LogFactory.getLog(getClass());
/*     */   
/*     */   private HttpParams defaultParams;
/*     */   
/*     */   private HttpRequestExecutor requestExec;
/*     */   
/*     */   private ClientConnectionManager connManager;
/*     */   
/*     */   private ConnectionReuseStrategy reuseStrategy;
/*     */   
/*     */   private ConnectionKeepAliveStrategy keepAliveStrategy;
/*     */   
/*     */   private CookieSpecRegistry supportedCookieSpecs;
/*     */   
/*     */   private AuthSchemeRegistry supportedAuthSchemes;
/*     */   
/*     */   private BasicHttpProcessor mutableProcessor;
/*     */   
/*     */   private ImmutableHttpProcessor protocolProcessor;
/*     */   
/*     */   private HttpRequestRetryHandler retryHandler;
/*     */   private RedirectStrategy redirectStrategy;
/*     */   private AuthenticationStrategy targetAuthStrategy;
/*     */   private AuthenticationStrategy proxyAuthStrategy;
/*     */   private CookieStore cookieStore;
/*     */   private CredentialsProvider credsProvider;
/*     */   private HttpRoutePlanner routePlanner;
/*     */   private UserTokenHandler userTokenHandler;
/*     */   private ConnectionBackoffStrategy connectionBackoffStrategy;
/*     */   private BackoffManager backoffManager;
/*     */   
/*     */   protected AbstractHttpClient(ClientConnectionManager conman, HttpParams params) {
/* 233 */     this.defaultParams = params;
/* 234 */     this.connManager = conman;
/*     */   }
/*     */ 
/*     */   
/*     */   protected abstract HttpParams createHttpParams();
/*     */ 
/*     */   
/*     */   protected abstract BasicHttpProcessor createHttpProcessor();
/*     */ 
/*     */   
/*     */   protected HttpContext createHttpContext() {
/* 245 */     BasicHttpContext basicHttpContext = new BasicHttpContext();
/* 246 */     basicHttpContext.setAttribute("http.scheme-registry", getConnectionManager().getSchemeRegistry());
/*     */ 
/*     */     
/* 249 */     basicHttpContext.setAttribute("http.authscheme-registry", getAuthSchemes());
/*     */ 
/*     */     
/* 252 */     basicHttpContext.setAttribute("http.cookiespec-registry", getCookieSpecs());
/*     */ 
/*     */     
/* 255 */     basicHttpContext.setAttribute("http.cookie-store", getCookieStore());
/*     */ 
/*     */     
/* 258 */     basicHttpContext.setAttribute("http.auth.credentials-provider", getCredentialsProvider());
/*     */ 
/*     */     
/* 261 */     return (HttpContext)basicHttpContext;
/*     */   }
/*     */   
/*     */   protected ClientConnectionManager createClientConnectionManager() {
/*     */     BasicClientConnectionManager basicClientConnectionManager;
/* 266 */     SchemeRegistry registry = SchemeRegistryFactory.createDefault();
/*     */     
/* 268 */     ClientConnectionManager connManager = null;
/* 269 */     HttpParams params = getParams();
/*     */     
/* 271 */     ClientConnectionManagerFactory factory = null;
/*     */     
/* 273 */     String className = (String)params.getParameter("http.connection-manager.factory-class-name");
/*     */     
/* 275 */     ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
/* 276 */     if (className != null) {
/*     */       try {
/*     */         Class<?> clazz;
/* 279 */         if (contextLoader != null) {
/* 280 */           clazz = Class.forName(className, true, contextLoader);
/*     */         } else {
/* 282 */           clazz = Class.forName(className);
/*     */         } 
/* 284 */         factory = (ClientConnectionManagerFactory)clazz.newInstance();
/* 285 */       } catch (ClassNotFoundException ex) {
/* 286 */         throw new IllegalStateException("Invalid class name: " + className);
/* 287 */       } catch (IllegalAccessException ex) {
/* 288 */         throw new IllegalAccessError(ex.getMessage());
/* 289 */       } catch (InstantiationException ex) {
/* 290 */         throw new InstantiationError(ex.getMessage());
/*     */       } 
/*     */     }
/* 293 */     if (factory != null) {
/* 294 */       connManager = factory.newInstance(params, registry);
/*     */     } else {
/* 296 */       basicClientConnectionManager = new BasicClientConnectionManager(registry);
/*     */     } 
/*     */     
/* 299 */     return (ClientConnectionManager)basicClientConnectionManager;
/*     */   }
/*     */ 
/*     */   
/*     */   protected AuthSchemeRegistry createAuthSchemeRegistry() {
/* 304 */     AuthSchemeRegistry registry = new AuthSchemeRegistry();
/* 305 */     registry.register("Basic", (AuthSchemeFactory)new BasicSchemeFactory());
/*     */ 
/*     */     
/* 308 */     registry.register("Digest", (AuthSchemeFactory)new DigestSchemeFactory());
/*     */ 
/*     */     
/* 311 */     registry.register("NTLM", (AuthSchemeFactory)new NTLMSchemeFactory());
/*     */ 
/*     */     
/* 314 */     registry.register("Negotiate", (AuthSchemeFactory)new SPNegoSchemeFactory());
/*     */ 
/*     */     
/* 317 */     registry.register("Kerberos", (AuthSchemeFactory)new KerberosSchemeFactory());
/*     */ 
/*     */     
/* 320 */     return registry;
/*     */   }
/*     */ 
/*     */   
/*     */   protected CookieSpecRegistry createCookieSpecRegistry() {
/* 325 */     CookieSpecRegistry registry = new CookieSpecRegistry();
/* 326 */     registry.register("default", (CookieSpecFactory)new BestMatchSpecFactory());
/*     */ 
/*     */     
/* 329 */     registry.register("best-match", (CookieSpecFactory)new BestMatchSpecFactory());
/*     */ 
/*     */     
/* 332 */     registry.register("compatibility", (CookieSpecFactory)new BrowserCompatSpecFactory());
/*     */ 
/*     */     
/* 335 */     registry.register("netscape", (CookieSpecFactory)new NetscapeDraftSpecFactory());
/*     */ 
/*     */     
/* 338 */     registry.register("rfc2109", (CookieSpecFactory)new RFC2109SpecFactory());
/*     */ 
/*     */     
/* 341 */     registry.register("rfc2965", (CookieSpecFactory)new RFC2965SpecFactory());
/*     */ 
/*     */     
/* 344 */     registry.register("ignoreCookies", (CookieSpecFactory)new IgnoreSpecFactory());
/*     */ 
/*     */     
/* 347 */     return registry;
/*     */   }
/*     */   
/*     */   protected HttpRequestExecutor createRequestExecutor() {
/* 351 */     return new HttpRequestExecutor();
/*     */   }
/*     */   
/*     */   protected ConnectionReuseStrategy createConnectionReuseStrategy() {
/* 355 */     return (ConnectionReuseStrategy)new DefaultConnectionReuseStrategy();
/*     */   }
/*     */   
/*     */   protected ConnectionKeepAliveStrategy createConnectionKeepAliveStrategy() {
/* 359 */     return new DefaultConnectionKeepAliveStrategy();
/*     */   }
/*     */   
/*     */   protected HttpRequestRetryHandler createHttpRequestRetryHandler() {
/* 363 */     return new DefaultHttpRequestRetryHandler();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected RedirectHandler createRedirectHandler() {
/* 371 */     return new DefaultRedirectHandler();
/*     */   }
/*     */   
/*     */   protected AuthenticationStrategy createTargetAuthenticationStrategy() {
/* 375 */     return new TargetAuthenticationStrategy();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected AuthenticationHandler createTargetAuthenticationHandler() {
/* 383 */     return new DefaultTargetAuthenticationHandler();
/*     */   }
/*     */   
/*     */   protected AuthenticationStrategy createProxyAuthenticationStrategy() {
/* 387 */     return new ProxyAuthenticationStrategy();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected AuthenticationHandler createProxyAuthenticationHandler() {
/* 395 */     return new DefaultProxyAuthenticationHandler();
/*     */   }
/*     */   
/*     */   protected CookieStore createCookieStore() {
/* 399 */     return new BasicCookieStore();
/*     */   }
/*     */   
/*     */   protected CredentialsProvider createCredentialsProvider() {
/* 403 */     return new BasicCredentialsProvider();
/*     */   }
/*     */   
/*     */   protected HttpRoutePlanner createHttpRoutePlanner() {
/* 407 */     return (HttpRoutePlanner)new DefaultHttpRoutePlanner(getConnectionManager().getSchemeRegistry());
/*     */   }
/*     */   
/*     */   protected UserTokenHandler createUserTokenHandler() {
/* 411 */     return new DefaultUserTokenHandler();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final synchronized HttpParams getParams() {
/* 417 */     if (this.defaultParams == null) {
/* 418 */       this.defaultParams = createHttpParams();
/*     */     }
/* 420 */     return this.defaultParams;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void setParams(HttpParams params) {
/* 430 */     this.defaultParams = params;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final synchronized ClientConnectionManager getConnectionManager() {
/* 436 */     if (this.connManager == null) {
/* 437 */       this.connManager = createClientConnectionManager();
/*     */     }
/* 439 */     return this.connManager;
/*     */   }
/*     */ 
/*     */   
/*     */   public final synchronized HttpRequestExecutor getRequestExecutor() {
/* 444 */     if (this.requestExec == null) {
/* 445 */       this.requestExec = createRequestExecutor();
/*     */     }
/* 447 */     return this.requestExec;
/*     */   }
/*     */ 
/*     */   
/*     */   public final synchronized AuthSchemeRegistry getAuthSchemes() {
/* 452 */     if (this.supportedAuthSchemes == null) {
/* 453 */       this.supportedAuthSchemes = createAuthSchemeRegistry();
/*     */     }
/* 455 */     return this.supportedAuthSchemes;
/*     */   }
/*     */   
/*     */   public synchronized void setAuthSchemes(AuthSchemeRegistry registry) {
/* 459 */     this.supportedAuthSchemes = registry;
/*     */   }
/*     */   
/*     */   public final synchronized ConnectionBackoffStrategy getConnectionBackoffStrategy() {
/* 463 */     return this.connectionBackoffStrategy;
/*     */   }
/*     */   
/*     */   public synchronized void setConnectionBackoffStrategy(ConnectionBackoffStrategy strategy) {
/* 467 */     this.connectionBackoffStrategy = strategy;
/*     */   }
/*     */   
/*     */   public final synchronized CookieSpecRegistry getCookieSpecs() {
/* 471 */     if (this.supportedCookieSpecs == null) {
/* 472 */       this.supportedCookieSpecs = createCookieSpecRegistry();
/*     */     }
/* 474 */     return this.supportedCookieSpecs;
/*     */   }
/*     */   
/*     */   public final synchronized BackoffManager getBackoffManager() {
/* 478 */     return this.backoffManager;
/*     */   }
/*     */   
/*     */   public synchronized void setBackoffManager(BackoffManager manager) {
/* 482 */     this.backoffManager = manager;
/*     */   }
/*     */   
/*     */   public synchronized void setCookieSpecs(CookieSpecRegistry registry) {
/* 486 */     this.supportedCookieSpecs = registry;
/*     */   }
/*     */   
/*     */   public final synchronized ConnectionReuseStrategy getConnectionReuseStrategy() {
/* 490 */     if (this.reuseStrategy == null) {
/* 491 */       this.reuseStrategy = createConnectionReuseStrategy();
/*     */     }
/* 493 */     return this.reuseStrategy;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void setReuseStrategy(ConnectionReuseStrategy strategy) {
/* 498 */     this.reuseStrategy = strategy;
/*     */   }
/*     */ 
/*     */   
/*     */   public final synchronized ConnectionKeepAliveStrategy getConnectionKeepAliveStrategy() {
/* 503 */     if (this.keepAliveStrategy == null) {
/* 504 */       this.keepAliveStrategy = createConnectionKeepAliveStrategy();
/*     */     }
/* 506 */     return this.keepAliveStrategy;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void setKeepAliveStrategy(ConnectionKeepAliveStrategy strategy) {
/* 511 */     this.keepAliveStrategy = strategy;
/*     */   }
/*     */ 
/*     */   
/*     */   public final synchronized HttpRequestRetryHandler getHttpRequestRetryHandler() {
/* 516 */     if (this.retryHandler == null) {
/* 517 */       this.retryHandler = createHttpRequestRetryHandler();
/*     */     }
/* 519 */     return this.retryHandler;
/*     */   }
/*     */   
/*     */   public synchronized void setHttpRequestRetryHandler(HttpRequestRetryHandler handler) {
/* 523 */     this.retryHandler = handler;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public final synchronized RedirectHandler getRedirectHandler() {
/* 531 */     return createRedirectHandler();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public synchronized void setRedirectHandler(RedirectHandler handler) {
/* 539 */     this.redirectStrategy = new DefaultRedirectStrategyAdaptor(handler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final synchronized RedirectStrategy getRedirectStrategy() {
/* 546 */     if (this.redirectStrategy == null) {
/* 547 */       this.redirectStrategy = new DefaultRedirectStrategy();
/*     */     }
/* 549 */     return this.redirectStrategy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void setRedirectStrategy(RedirectStrategy strategy) {
/* 556 */     this.redirectStrategy = strategy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public final synchronized AuthenticationHandler getTargetAuthenticationHandler() {
/* 564 */     return createTargetAuthenticationHandler();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public synchronized void setTargetAuthenticationHandler(AuthenticationHandler handler) {
/* 572 */     this.targetAuthStrategy = new AuthenticationStrategyAdaptor(handler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final synchronized AuthenticationStrategy getTargetAuthenticationStrategy() {
/* 579 */     if (this.targetAuthStrategy == null) {
/* 580 */       this.targetAuthStrategy = createTargetAuthenticationStrategy();
/*     */     }
/* 582 */     return this.targetAuthStrategy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void setTargetAuthenticationStrategy(AuthenticationStrategy strategy) {
/* 589 */     this.targetAuthStrategy = strategy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public final synchronized AuthenticationHandler getProxyAuthenticationHandler() {
/* 597 */     return createProxyAuthenticationHandler();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public synchronized void setProxyAuthenticationHandler(AuthenticationHandler handler) {
/* 605 */     this.proxyAuthStrategy = new AuthenticationStrategyAdaptor(handler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final synchronized AuthenticationStrategy getProxyAuthenticationStrategy() {
/* 612 */     if (this.proxyAuthStrategy == null) {
/* 613 */       this.proxyAuthStrategy = createProxyAuthenticationStrategy();
/*     */     }
/* 615 */     return this.proxyAuthStrategy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void setProxyAuthenticationStrategy(AuthenticationStrategy strategy) {
/* 622 */     this.proxyAuthStrategy = strategy;
/*     */   }
/*     */   
/*     */   public final synchronized CookieStore getCookieStore() {
/* 626 */     if (this.cookieStore == null) {
/* 627 */       this.cookieStore = createCookieStore();
/*     */     }
/* 629 */     return this.cookieStore;
/*     */   }
/*     */   
/*     */   public synchronized void setCookieStore(CookieStore cookieStore) {
/* 633 */     this.cookieStore = cookieStore;
/*     */   }
/*     */   
/*     */   public final synchronized CredentialsProvider getCredentialsProvider() {
/* 637 */     if (this.credsProvider == null) {
/* 638 */       this.credsProvider = createCredentialsProvider();
/*     */     }
/* 640 */     return this.credsProvider;
/*     */   }
/*     */   
/*     */   public synchronized void setCredentialsProvider(CredentialsProvider credsProvider) {
/* 644 */     this.credsProvider = credsProvider;
/*     */   }
/*     */   
/*     */   public final synchronized HttpRoutePlanner getRoutePlanner() {
/* 648 */     if (this.routePlanner == null) {
/* 649 */       this.routePlanner = createHttpRoutePlanner();
/*     */     }
/* 651 */     return this.routePlanner;
/*     */   }
/*     */   
/*     */   public synchronized void setRoutePlanner(HttpRoutePlanner routePlanner) {
/* 655 */     this.routePlanner = routePlanner;
/*     */   }
/*     */   
/*     */   public final synchronized UserTokenHandler getUserTokenHandler() {
/* 659 */     if (this.userTokenHandler == null) {
/* 660 */       this.userTokenHandler = createUserTokenHandler();
/*     */     }
/* 662 */     return this.userTokenHandler;
/*     */   }
/*     */   
/*     */   public synchronized void setUserTokenHandler(UserTokenHandler handler) {
/* 666 */     this.userTokenHandler = handler;
/*     */   }
/*     */   
/*     */   protected final synchronized BasicHttpProcessor getHttpProcessor() {
/* 670 */     if (this.mutableProcessor == null) {
/* 671 */       this.mutableProcessor = createHttpProcessor();
/*     */     }
/* 673 */     return this.mutableProcessor;
/*     */   }
/*     */   
/*     */   private synchronized HttpProcessor getProtocolProcessor() {
/* 677 */     if (this.protocolProcessor == null) {
/*     */       
/* 679 */       BasicHttpProcessor proc = getHttpProcessor();
/*     */       
/* 681 */       int reqc = proc.getRequestInterceptorCount();
/* 682 */       HttpRequestInterceptor[] reqinterceptors = new HttpRequestInterceptor[reqc];
/* 683 */       for (int i = 0; i < reqc; i++) {
/* 684 */         reqinterceptors[i] = proc.getRequestInterceptor(i);
/*     */       }
/* 686 */       int resc = proc.getResponseInterceptorCount();
/* 687 */       HttpResponseInterceptor[] resinterceptors = new HttpResponseInterceptor[resc];
/* 688 */       for (int j = 0; j < resc; j++) {
/* 689 */         resinterceptors[j] = proc.getResponseInterceptor(j);
/*     */       }
/* 691 */       this.protocolProcessor = new ImmutableHttpProcessor(reqinterceptors, resinterceptors);
/*     */     } 
/* 693 */     return (HttpProcessor)this.protocolProcessor;
/*     */   }
/*     */   
/*     */   public synchronized int getResponseInterceptorCount() {
/* 697 */     return getHttpProcessor().getResponseInterceptorCount();
/*     */   }
/*     */   
/*     */   public synchronized HttpResponseInterceptor getResponseInterceptor(int index) {
/* 701 */     return getHttpProcessor().getResponseInterceptor(index);
/*     */   }
/*     */   
/*     */   public synchronized HttpRequestInterceptor getRequestInterceptor(int index) {
/* 705 */     return getHttpProcessor().getRequestInterceptor(index);
/*     */   }
/*     */   
/*     */   public synchronized int getRequestInterceptorCount() {
/* 709 */     return getHttpProcessor().getRequestInterceptorCount();
/*     */   }
/*     */   
/*     */   public synchronized void addResponseInterceptor(HttpResponseInterceptor itcp) {
/* 713 */     getHttpProcessor().addInterceptor(itcp);
/* 714 */     this.protocolProcessor = null;
/*     */   }
/*     */   
/*     */   public synchronized void addResponseInterceptor(HttpResponseInterceptor itcp, int index) {
/* 718 */     getHttpProcessor().addInterceptor(itcp, index);
/* 719 */     this.protocolProcessor = null;
/*     */   }
/*     */   
/*     */   public synchronized void clearResponseInterceptors() {
/* 723 */     getHttpProcessor().clearResponseInterceptors();
/* 724 */     this.protocolProcessor = null;
/*     */   }
/*     */   
/*     */   public synchronized void removeResponseInterceptorByClass(Class<? extends HttpResponseInterceptor> clazz) {
/* 728 */     getHttpProcessor().removeResponseInterceptorByClass(clazz);
/* 729 */     this.protocolProcessor = null;
/*     */   }
/*     */   
/*     */   public synchronized void addRequestInterceptor(HttpRequestInterceptor itcp) {
/* 733 */     getHttpProcessor().addInterceptor(itcp);
/* 734 */     this.protocolProcessor = null;
/*     */   }
/*     */   
/*     */   public synchronized void addRequestInterceptor(HttpRequestInterceptor itcp, int index) {
/* 738 */     getHttpProcessor().addInterceptor(itcp, index);
/* 739 */     this.protocolProcessor = null;
/*     */   }
/*     */   
/*     */   public synchronized void clearRequestInterceptors() {
/* 743 */     getHttpProcessor().clearRequestInterceptors();
/* 744 */     this.protocolProcessor = null;
/*     */   }
/*     */   
/*     */   public synchronized void removeRequestInterceptorByClass(Class<? extends HttpRequestInterceptor> clazz) {
/* 748 */     getHttpProcessor().removeRequestInterceptorByClass(clazz);
/* 749 */     this.protocolProcessor = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final CloseableHttpResponse doExecute(HttpHost target, HttpRequest request, HttpContext context) throws IOException, ClientProtocolException {
/*     */     DefaultedHttpContext defaultedHttpContext;
/* 757 */     Args.notNull(request, "HTTP request");
/*     */ 
/*     */ 
/*     */     
/* 761 */     HttpContext execContext = null;
/* 762 */     RequestDirector director = null;
/* 763 */     HttpRoutePlanner routePlanner = null;
/* 764 */     ConnectionBackoffStrategy connectionBackoffStrategy = null;
/* 765 */     BackoffManager backoffManager = null;
/*     */ 
/*     */ 
/*     */     
/* 769 */     synchronized (this) {
/*     */       
/* 771 */       HttpContext defaultContext = createHttpContext();
/* 772 */       if (context == null) {
/* 773 */         execContext = defaultContext;
/*     */       } else {
/* 775 */         defaultedHttpContext = new DefaultedHttpContext(context, defaultContext);
/*     */       } 
/* 777 */       HttpParams params = determineParams(request);
/* 778 */       RequestConfig config = HttpClientParamConfig.getRequestConfig(params);
/* 779 */       defaultedHttpContext.setAttribute("http.request-config", config);
/*     */ 
/*     */       
/* 782 */       director = createClientRequestDirector(getRequestExecutor(), getConnectionManager(), getConnectionReuseStrategy(), getConnectionKeepAliveStrategy(), getRoutePlanner(), getProtocolProcessor(), getHttpRequestRetryHandler(), getRedirectStrategy(), getTargetAuthenticationStrategy(), getProxyAuthenticationStrategy(), getUserTokenHandler(), params);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 795 */       routePlanner = getRoutePlanner();
/* 796 */       connectionBackoffStrategy = getConnectionBackoffStrategy();
/* 797 */       backoffManager = getBackoffManager();
/*     */     } 
/*     */     
/*     */     try {
/* 801 */       if (connectionBackoffStrategy != null && backoffManager != null) {
/* 802 */         CloseableHttpResponse out; HttpHost targetForRoute = (target != null) ? target : (HttpHost)determineParams(request).getParameter("http.default-host");
/*     */ 
/*     */         
/* 805 */         HttpRoute route = routePlanner.determineRoute(targetForRoute, request, (HttpContext)defaultedHttpContext);
/*     */ 
/*     */         
/*     */         try {
/* 809 */           out = CloseableHttpResponseProxy.newProxy(director.execute(target, request, (HttpContext)defaultedHttpContext));
/*     */         }
/* 811 */         catch (RuntimeException re) {
/* 812 */           if (connectionBackoffStrategy.shouldBackoff(re)) {
/* 813 */             backoffManager.backOff(route);
/*     */           }
/* 815 */           throw re;
/* 816 */         } catch (Exception e) {
/* 817 */           if (connectionBackoffStrategy.shouldBackoff(e)) {
/* 818 */             backoffManager.backOff(route);
/*     */           }
/* 820 */           if (e instanceof HttpException) {
/* 821 */             throw (HttpException)e;
/*     */           }
/* 823 */           if (e instanceof IOException) {
/* 824 */             throw (IOException)e;
/*     */           }
/* 826 */           throw new UndeclaredThrowableException(e);
/*     */         } 
/* 828 */         if (connectionBackoffStrategy.shouldBackoff((HttpResponse)out)) {
/* 829 */           backoffManager.backOff(route);
/*     */         } else {
/* 831 */           backoffManager.probe(route);
/*     */         } 
/* 833 */         return out;
/*     */       } 
/* 835 */       return CloseableHttpResponseProxy.newProxy(director.execute(target, request, (HttpContext)defaultedHttpContext));
/*     */     
/*     */     }
/* 838 */     catch (HttpException httpException) {
/* 839 */       throw new ClientProtocolException(httpException);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected RequestDirector createClientRequestDirector(HttpRequestExecutor requestExec, ClientConnectionManager conman, ConnectionReuseStrategy reustrat, ConnectionKeepAliveStrategy kastrat, HttpRoutePlanner rouplan, HttpProcessor httpProcessor, HttpRequestRetryHandler retryHandler, RedirectHandler redirectHandler, AuthenticationHandler targetAuthHandler, AuthenticationHandler proxyAuthHandler, UserTokenHandler userTokenHandler, HttpParams params) {
/* 860 */     return new DefaultRequestDirector(requestExec, conman, reustrat, kastrat, rouplan, httpProcessor, retryHandler, redirectHandler, targetAuthHandler, proxyAuthHandler, userTokenHandler, params);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected RequestDirector createClientRequestDirector(HttpRequestExecutor requestExec, ClientConnectionManager conman, ConnectionReuseStrategy reustrat, ConnectionKeepAliveStrategy kastrat, HttpRoutePlanner rouplan, HttpProcessor httpProcessor, HttpRequestRetryHandler retryHandler, RedirectStrategy redirectStrategy, AuthenticationHandler targetAuthHandler, AuthenticationHandler proxyAuthHandler, UserTokenHandler userTokenHandler, HttpParams params) {
/* 892 */     return new DefaultRequestDirector(this.log, requestExec, conman, reustrat, kastrat, rouplan, httpProcessor, retryHandler, redirectStrategy, targetAuthHandler, proxyAuthHandler, userTokenHandler, params);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected RequestDirector createClientRequestDirector(HttpRequestExecutor requestExec, ClientConnectionManager conman, ConnectionReuseStrategy reustrat, ConnectionKeepAliveStrategy kastrat, HttpRoutePlanner rouplan, HttpProcessor httpProcessor, HttpRequestRetryHandler retryHandler, RedirectStrategy redirectStrategy, AuthenticationStrategy targetAuthStrategy, AuthenticationStrategy proxyAuthStrategy, UserTokenHandler userTokenHandler, HttpParams params) {
/* 925 */     return new DefaultRequestDirector(this.log, requestExec, conman, reustrat, kastrat, rouplan, httpProcessor, retryHandler, redirectStrategy, targetAuthStrategy, proxyAuthStrategy, userTokenHandler, params);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected HttpParams determineParams(HttpRequest req) {
/* 958 */     return (HttpParams)new ClientParamsStack(null, getParams(), req.getParams(), null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {
/* 965 */     getConnectionManager().shutdown();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\client\AbstractHttpClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */