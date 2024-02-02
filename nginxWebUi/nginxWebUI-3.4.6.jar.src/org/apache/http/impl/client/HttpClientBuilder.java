/*      */ package org.apache.http.impl.client;
/*      */ 
/*      */ import java.io.Closeable;
/*      */ import java.io.IOException;
/*      */ import java.net.ProxySelector;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import javax.net.ssl.HostnameVerifier;
/*      */ import javax.net.ssl.SSLContext;
/*      */ import javax.net.ssl.SSLSocketFactory;
/*      */ import org.apache.http.ConnectionReuseStrategy;
/*      */ import org.apache.http.Header;
/*      */ import org.apache.http.HttpHost;
/*      */ import org.apache.http.HttpRequestInterceptor;
/*      */ import org.apache.http.HttpResponseInterceptor;
/*      */ import org.apache.http.auth.AuthSchemeProvider;
/*      */ import org.apache.http.client.AuthenticationStrategy;
/*      */ import org.apache.http.client.BackoffManager;
/*      */ import org.apache.http.client.ConnectionBackoffStrategy;
/*      */ import org.apache.http.client.CookieStore;
/*      */ import org.apache.http.client.CredentialsProvider;
/*      */ import org.apache.http.client.HttpRequestRetryHandler;
/*      */ import org.apache.http.client.RedirectStrategy;
/*      */ import org.apache.http.client.ServiceUnavailableRetryStrategy;
/*      */ import org.apache.http.client.UserTokenHandler;
/*      */ import org.apache.http.client.config.RequestConfig;
/*      */ import org.apache.http.client.entity.InputStreamFactory;
/*      */ import org.apache.http.client.protocol.RequestAcceptEncoding;
/*      */ import org.apache.http.client.protocol.RequestAddCookies;
/*      */ import org.apache.http.client.protocol.RequestAuthCache;
/*      */ import org.apache.http.client.protocol.RequestClientConnControl;
/*      */ import org.apache.http.client.protocol.RequestDefaultHeaders;
/*      */ import org.apache.http.client.protocol.RequestExpectContinue;
/*      */ import org.apache.http.client.protocol.ResponseContentEncoding;
/*      */ import org.apache.http.client.protocol.ResponseProcessCookies;
/*      */ import org.apache.http.config.ConnectionConfig;
/*      */ import org.apache.http.config.Lookup;
/*      */ import org.apache.http.config.Registry;
/*      */ import org.apache.http.config.RegistryBuilder;
/*      */ import org.apache.http.config.SocketConfig;
/*      */ import org.apache.http.conn.ConnectionKeepAliveStrategy;
/*      */ import org.apache.http.conn.DnsResolver;
/*      */ import org.apache.http.conn.HttpClientConnectionManager;
/*      */ import org.apache.http.conn.SchemePortResolver;
/*      */ import org.apache.http.conn.routing.HttpRoutePlanner;
/*      */ import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
/*      */ import org.apache.http.conn.socket.PlainConnectionSocketFactory;
/*      */ import org.apache.http.conn.ssl.DefaultHostnameVerifier;
/*      */ import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
/*      */ import org.apache.http.conn.ssl.X509HostnameVerifier;
/*      */ import org.apache.http.conn.util.PublicSuffixMatcher;
/*      */ import org.apache.http.conn.util.PublicSuffixMatcherLoader;
/*      */ import org.apache.http.cookie.CookieSpecProvider;
/*      */ import org.apache.http.impl.NoConnectionReuseStrategy;
/*      */ import org.apache.http.impl.auth.BasicSchemeFactory;
/*      */ import org.apache.http.impl.auth.DigestSchemeFactory;
/*      */ import org.apache.http.impl.auth.KerberosSchemeFactory;
/*      */ import org.apache.http.impl.auth.NTLMSchemeFactory;
/*      */ import org.apache.http.impl.auth.SPNegoSchemeFactory;
/*      */ import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
/*      */ import org.apache.http.impl.conn.DefaultRoutePlanner;
/*      */ import org.apache.http.impl.conn.DefaultSchemePortResolver;
/*      */ import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
/*      */ import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
/*      */ import org.apache.http.impl.execchain.BackoffStrategyExec;
/*      */ import org.apache.http.impl.execchain.ClientExecChain;
/*      */ import org.apache.http.impl.execchain.MainClientExec;
/*      */ import org.apache.http.impl.execchain.ProtocolExec;
/*      */ import org.apache.http.impl.execchain.RedirectExec;
/*      */ import org.apache.http.impl.execchain.RetryExec;
/*      */ import org.apache.http.impl.execchain.ServiceUnavailableRetryExec;
/*      */ import org.apache.http.protocol.HttpProcessor;
/*      */ import org.apache.http.protocol.HttpProcessorBuilder;
/*      */ import org.apache.http.protocol.HttpRequestExecutor;
/*      */ import org.apache.http.protocol.ImmutableHttpProcessor;
/*      */ import org.apache.http.protocol.RequestContent;
/*      */ import org.apache.http.protocol.RequestTargetHost;
/*      */ import org.apache.http.protocol.RequestUserAgent;
/*      */ import org.apache.http.ssl.SSLContexts;
/*      */ import org.apache.http.util.TextUtils;
/*      */ import org.apache.http.util.VersionInfo;
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
/*      */ public class HttpClientBuilder
/*      */ {
/*      */   private HttpRequestExecutor requestExec;
/*      */   private HostnameVerifier hostnameVerifier;
/*      */   private LayeredConnectionSocketFactory sslSocketFactory;
/*      */   private SSLContext sslContext;
/*      */   private HttpClientConnectionManager connManager;
/*      */   private boolean connManagerShared;
/*      */   private SchemePortResolver schemePortResolver;
/*      */   private ConnectionReuseStrategy reuseStrategy;
/*      */   private ConnectionKeepAliveStrategy keepAliveStrategy;
/*      */   private AuthenticationStrategy targetAuthStrategy;
/*      */   private AuthenticationStrategy proxyAuthStrategy;
/*      */   private UserTokenHandler userTokenHandler;
/*      */   private HttpProcessor httpprocessor;
/*      */   private DnsResolver dnsResolver;
/*      */   private LinkedList<HttpRequestInterceptor> requestFirst;
/*      */   private LinkedList<HttpRequestInterceptor> requestLast;
/*      */   private LinkedList<HttpResponseInterceptor> responseFirst;
/*      */   private LinkedList<HttpResponseInterceptor> responseLast;
/*      */   private HttpRequestRetryHandler retryHandler;
/*      */   private HttpRoutePlanner routePlanner;
/*      */   private RedirectStrategy redirectStrategy;
/*      */   private ConnectionBackoffStrategy connectionBackoffStrategy;
/*      */   private BackoffManager backoffManager;
/*      */   private ServiceUnavailableRetryStrategy serviceUnavailStrategy;
/*      */   private Lookup<AuthSchemeProvider> authSchemeRegistry;
/*      */   private Lookup<CookieSpecProvider> cookieSpecRegistry;
/*      */   private Map<String, InputStreamFactory> contentDecoderMap;
/*      */   private CookieStore cookieStore;
/*      */   private CredentialsProvider credentialsProvider;
/*      */   private String userAgent;
/*      */   private HttpHost proxy;
/*      */   private Collection<? extends Header> defaultHeaders;
/*      */   private SocketConfig defaultSocketConfig;
/*      */   private ConnectionConfig defaultConnectionConfig;
/*      */   private RequestConfig defaultRequestConfig;
/*      */   private boolean evictExpiredConnections;
/*      */   private boolean evictIdleConnections;
/*      */   private long maxIdleTime;
/*      */   private TimeUnit maxIdleTimeUnit;
/*      */   private boolean systemProperties;
/*      */   private boolean redirectHandlingDisabled;
/*      */   private boolean automaticRetriesDisabled;
/*      */   private boolean contentCompressionDisabled;
/*      */   private boolean cookieManagementDisabled;
/*      */   private boolean authCachingDisabled;
/*      */   private boolean connectionStateDisabled;
/*      */   private boolean defaultUserAgentDisabled;
/*  210 */   private int maxConnTotal = 0;
/*  211 */   private int maxConnPerRoute = 0;
/*      */   
/*  213 */   private long connTimeToLive = -1L;
/*  214 */   private TimeUnit connTimeToLiveTimeUnit = TimeUnit.MILLISECONDS;
/*      */   
/*      */   private List<Closeable> closeables;
/*      */   
/*      */   private PublicSuffixMatcher publicSuffixMatcher;
/*      */   
/*      */   public static HttpClientBuilder create() {
/*  221 */     return new HttpClientBuilder();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final HttpClientBuilder setRequestExecutor(HttpRequestExecutor requestExec) {
/*  232 */     this.requestExec = requestExec;
/*  233 */     return this;
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
/*      */   @Deprecated
/*      */   public final HttpClientBuilder setHostnameVerifier(X509HostnameVerifier hostnameVerifier) {
/*  248 */     this.hostnameVerifier = (HostnameVerifier)hostnameVerifier;
/*  249 */     return this;
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
/*      */   public final HttpClientBuilder setSSLHostnameVerifier(HostnameVerifier hostnameVerifier) {
/*  263 */     this.hostnameVerifier = hostnameVerifier;
/*  264 */     return this;
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
/*      */   public final HttpClientBuilder setPublicSuffixMatcher(PublicSuffixMatcher publicSuffixMatcher) {
/*  277 */     this.publicSuffixMatcher = publicSuffixMatcher;
/*  278 */     return this;
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
/*      */   @Deprecated
/*      */   public final HttpClientBuilder setSslcontext(SSLContext sslcontext) {
/*  293 */     return setSSLContext(sslcontext);
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
/*      */   public final HttpClientBuilder setSSLContext(SSLContext sslContext) {
/*  305 */     this.sslContext = sslContext;
/*  306 */     return this;
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
/*      */   public final HttpClientBuilder setSSLSocketFactory(LayeredConnectionSocketFactory sslSocketFactory) {
/*  318 */     this.sslSocketFactory = sslSocketFactory;
/*  319 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final HttpClientBuilder setMaxConnTotal(int maxConnTotal) {
/*  330 */     this.maxConnTotal = maxConnTotal;
/*  331 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final HttpClientBuilder setMaxConnPerRoute(int maxConnPerRoute) {
/*  342 */     this.maxConnPerRoute = maxConnPerRoute;
/*  343 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final HttpClientBuilder setDefaultSocketConfig(SocketConfig config) {
/*  354 */     this.defaultSocketConfig = config;
/*  355 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final HttpClientBuilder setDefaultConnectionConfig(ConnectionConfig config) {
/*  366 */     this.defaultConnectionConfig = config;
/*  367 */     return this;
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
/*      */   public final HttpClientBuilder setConnectionTimeToLive(long connTimeToLive, TimeUnit connTimeToLiveTimeUnit) {
/*  380 */     this.connTimeToLive = connTimeToLive;
/*  381 */     this.connTimeToLiveTimeUnit = connTimeToLiveTimeUnit;
/*  382 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final HttpClientBuilder setConnectionManager(HttpClientConnectionManager connManager) {
/*  390 */     this.connManager = connManager;
/*  391 */     return this;
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
/*      */   public final HttpClientBuilder setConnectionManagerShared(boolean shared) {
/*  410 */     this.connManagerShared = shared;
/*  411 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final HttpClientBuilder setConnectionReuseStrategy(ConnectionReuseStrategy reuseStrategy) {
/*  419 */     this.reuseStrategy = reuseStrategy;
/*  420 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final HttpClientBuilder setKeepAliveStrategy(ConnectionKeepAliveStrategy keepAliveStrategy) {
/*  428 */     this.keepAliveStrategy = keepAliveStrategy;
/*  429 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final HttpClientBuilder setTargetAuthenticationStrategy(AuthenticationStrategy targetAuthStrategy) {
/*  438 */     this.targetAuthStrategy = targetAuthStrategy;
/*  439 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final HttpClientBuilder setProxyAuthenticationStrategy(AuthenticationStrategy proxyAuthStrategy) {
/*  448 */     this.proxyAuthStrategy = proxyAuthStrategy;
/*  449 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final HttpClientBuilder setUserTokenHandler(UserTokenHandler userTokenHandler) {
/*  460 */     this.userTokenHandler = userTokenHandler;
/*  461 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final HttpClientBuilder disableConnectionState() {
/*  468 */     this.connectionStateDisabled = true;
/*  469 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final HttpClientBuilder setSchemePortResolver(SchemePortResolver schemePortResolver) {
/*  477 */     this.schemePortResolver = schemePortResolver;
/*  478 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final HttpClientBuilder setUserAgent(String userAgent) {
/*  489 */     this.userAgent = userAgent;
/*  490 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final HttpClientBuilder setDefaultHeaders(Collection<? extends Header> defaultHeaders) {
/*  501 */     this.defaultHeaders = defaultHeaders;
/*  502 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final HttpClientBuilder addInterceptorFirst(HttpResponseInterceptor itcp) {
/*  513 */     if (itcp == null) {
/*  514 */       return this;
/*      */     }
/*  516 */     if (this.responseFirst == null) {
/*  517 */       this.responseFirst = new LinkedList<HttpResponseInterceptor>();
/*      */     }
/*  519 */     this.responseFirst.addFirst(itcp);
/*  520 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final HttpClientBuilder addInterceptorLast(HttpResponseInterceptor itcp) {
/*  531 */     if (itcp == null) {
/*  532 */       return this;
/*      */     }
/*  534 */     if (this.responseLast == null) {
/*  535 */       this.responseLast = new LinkedList<HttpResponseInterceptor>();
/*      */     }
/*  537 */     this.responseLast.addLast(itcp);
/*  538 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final HttpClientBuilder addInterceptorFirst(HttpRequestInterceptor itcp) {
/*  548 */     if (itcp == null) {
/*  549 */       return this;
/*      */     }
/*  551 */     if (this.requestFirst == null) {
/*  552 */       this.requestFirst = new LinkedList<HttpRequestInterceptor>();
/*      */     }
/*  554 */     this.requestFirst.addFirst(itcp);
/*  555 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final HttpClientBuilder addInterceptorLast(HttpRequestInterceptor itcp) {
/*  565 */     if (itcp == null) {
/*  566 */       return this;
/*      */     }
/*  568 */     if (this.requestLast == null) {
/*  569 */       this.requestLast = new LinkedList<HttpRequestInterceptor>();
/*      */     }
/*  571 */     this.requestLast.addLast(itcp);
/*  572 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final HttpClientBuilder disableCookieManagement() {
/*  582 */     this.cookieManagementDisabled = true;
/*  583 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final HttpClientBuilder disableContentCompression() {
/*  593 */     this.contentCompressionDisabled = true;
/*  594 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final HttpClientBuilder disableAuthCaching() {
/*  604 */     this.authCachingDisabled = true;
/*  605 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final HttpClientBuilder setHttpProcessor(HttpProcessor httpprocessor) {
/*  612 */     this.httpprocessor = httpprocessor;
/*  613 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final HttpClientBuilder setDnsResolver(DnsResolver dnsResolver) {
/*  622 */     this.dnsResolver = dnsResolver;
/*  623 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final HttpClientBuilder setRetryHandler(HttpRequestRetryHandler retryHandler) {
/*  633 */     this.retryHandler = retryHandler;
/*  634 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final HttpClientBuilder disableAutomaticRetries() {
/*  641 */     this.automaticRetriesDisabled = true;
/*  642 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final HttpClientBuilder setProxy(HttpHost proxy) {
/*  652 */     this.proxy = proxy;
/*  653 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final HttpClientBuilder setRoutePlanner(HttpRoutePlanner routePlanner) {
/*  660 */     this.routePlanner = routePlanner;
/*  661 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final HttpClientBuilder setRedirectStrategy(RedirectStrategy redirectStrategy) {
/*  672 */     this.redirectStrategy = redirectStrategy;
/*  673 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final HttpClientBuilder disableRedirectHandling() {
/*  680 */     this.redirectHandlingDisabled = true;
/*  681 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final HttpClientBuilder setConnectionBackoffStrategy(ConnectionBackoffStrategy connectionBackoffStrategy) {
/*  689 */     this.connectionBackoffStrategy = connectionBackoffStrategy;
/*  690 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final HttpClientBuilder setBackoffManager(BackoffManager backoffManager) {
/*  697 */     this.backoffManager = backoffManager;
/*  698 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final HttpClientBuilder setServiceUnavailableRetryStrategy(ServiceUnavailableRetryStrategy serviceUnavailStrategy) {
/*  706 */     this.serviceUnavailStrategy = serviceUnavailStrategy;
/*  707 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final HttpClientBuilder setDefaultCookieStore(CookieStore cookieStore) {
/*  715 */     this.cookieStore = cookieStore;
/*  716 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final HttpClientBuilder setDefaultCredentialsProvider(CredentialsProvider credentialsProvider) {
/*  726 */     this.credentialsProvider = credentialsProvider;
/*  727 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final HttpClientBuilder setDefaultAuthSchemeRegistry(Lookup<AuthSchemeProvider> authSchemeRegistry) {
/*  737 */     this.authSchemeRegistry = authSchemeRegistry;
/*  738 */     return this;
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
/*      */   public final HttpClientBuilder setDefaultCookieSpecRegistry(Lookup<CookieSpecProvider> cookieSpecRegistry) {
/*  751 */     this.cookieSpecRegistry = cookieSpecRegistry;
/*  752 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final HttpClientBuilder setContentDecoderRegistry(Map<String, InputStreamFactory> contentDecoderMap) {
/*  762 */     this.contentDecoderMap = contentDecoderMap;
/*  763 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final HttpClientBuilder setDefaultRequestConfig(RequestConfig config) {
/*  772 */     this.defaultRequestConfig = config;
/*  773 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final HttpClientBuilder useSystemProperties() {
/*  781 */     this.systemProperties = true;
/*  782 */     return this;
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
/*      */   public final HttpClientBuilder evictExpiredConnections() {
/*  804 */     this.evictExpiredConnections = true;
/*  805 */     return this;
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
/*      */   @Deprecated
/*      */   public final HttpClientBuilder evictIdleConnections(Long maxIdleTime, TimeUnit maxIdleTimeUnit) {
/*  835 */     return evictIdleConnections(maxIdleTime.longValue(), maxIdleTimeUnit);
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
/*      */   public final HttpClientBuilder evictIdleConnections(long maxIdleTime, TimeUnit maxIdleTimeUnit) {
/*  862 */     this.evictIdleConnections = true;
/*  863 */     this.maxIdleTime = maxIdleTime;
/*  864 */     this.maxIdleTimeUnit = maxIdleTimeUnit;
/*  865 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final HttpClientBuilder disableDefaultUserAgent() {
/*  874 */     this.defaultUserAgentDisabled = true;
/*  875 */     return this;
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
/*      */   protected ClientExecChain createMainExec(HttpRequestExecutor requestExec, HttpClientConnectionManager connManager, ConnectionReuseStrategy reuseStrategy, ConnectionKeepAliveStrategy keepAliveStrategy, HttpProcessor proxyHttpProcessor, AuthenticationStrategy targetAuthStrategy, AuthenticationStrategy proxyAuthStrategy, UserTokenHandler userTokenHandler) {
/*  899 */     return (ClientExecChain)new MainClientExec(requestExec, connManager, reuseStrategy, keepAliveStrategy, proxyHttpProcessor, targetAuthStrategy, proxyAuthStrategy, userTokenHandler);
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
/*      */   protected ClientExecChain decorateMainExec(ClientExecChain mainExec) {
/*  914 */     return mainExec;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ClientExecChain decorateProtocolExec(ClientExecChain protocolExec) {
/*  921 */     return protocolExec;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void addCloseable(Closeable closeable) {
/*  928 */     if (closeable == null) {
/*      */       return;
/*      */     }
/*  931 */     if (this.closeables == null) {
/*  932 */       this.closeables = new ArrayList<Closeable>();
/*      */     }
/*  934 */     this.closeables.add(closeable);
/*      */   }
/*      */   
/*      */   private static String[] split(String s) {
/*  938 */     if (TextUtils.isBlank(s)) {
/*  939 */       return null;
/*      */     }
/*  941 */     return s.split(" *, *"); } public CloseableHttpClient build() { PoolingHttpClientConnectionManager poolingHttpClientConnectionManager; DefaultClientConnectionReuseStrategy defaultClientConnectionReuseStrategy; RetryExec retryExec;
/*      */     ServiceUnavailableRetryExec serviceUnavailableRetryExec;
/*      */     RedirectExec redirectExec;
/*      */     BackoffStrategyExec backoffStrategyExec;
/*      */     DefaultRoutePlanner defaultRoutePlanner;
/*      */     Registry registry;
/*  947 */     PublicSuffixMatcher publicSuffixMatcherCopy = this.publicSuffixMatcher;
/*  948 */     if (publicSuffixMatcherCopy == null) {
/*  949 */       publicSuffixMatcherCopy = PublicSuffixMatcherLoader.getDefault();
/*      */     }
/*      */     
/*  952 */     HttpRequestExecutor requestExecCopy = this.requestExec;
/*  953 */     if (requestExecCopy == null) {
/*  954 */       requestExecCopy = new HttpRequestExecutor();
/*      */     }
/*  956 */     HttpClientConnectionManager connManagerCopy = this.connManager;
/*  957 */     if (connManagerCopy == null) {
/*  958 */       SSLConnectionSocketFactory sSLConnectionSocketFactory; LayeredConnectionSocketFactory sslSocketFactoryCopy = this.sslSocketFactory;
/*  959 */       if (sslSocketFactoryCopy == null) {
/*  960 */         DefaultHostnameVerifier defaultHostnameVerifier; String[] supportedProtocols = this.systemProperties ? split(System.getProperty("https.protocols")) : null;
/*      */         
/*  962 */         String[] supportedCipherSuites = this.systemProperties ? split(System.getProperty("https.cipherSuites")) : null;
/*      */         
/*  964 */         HostnameVerifier hostnameVerifierCopy = this.hostnameVerifier;
/*  965 */         if (hostnameVerifierCopy == null) {
/*  966 */           defaultHostnameVerifier = new DefaultHostnameVerifier(publicSuffixMatcherCopy);
/*      */         }
/*  968 */         if (this.sslContext != null) {
/*  969 */           sSLConnectionSocketFactory = new SSLConnectionSocketFactory(this.sslContext, supportedProtocols, supportedCipherSuites, (HostnameVerifier)defaultHostnameVerifier);
/*      */         
/*      */         }
/*  972 */         else if (this.systemProperties) {
/*  973 */           sSLConnectionSocketFactory = new SSLConnectionSocketFactory((SSLSocketFactory)SSLSocketFactory.getDefault(), supportedProtocols, supportedCipherSuites, (HostnameVerifier)defaultHostnameVerifier);
/*      */         }
/*      */         else {
/*      */           
/*  977 */           sSLConnectionSocketFactory = new SSLConnectionSocketFactory(SSLContexts.createDefault(), (HostnameVerifier)defaultHostnameVerifier);
/*      */         } 
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  984 */       PoolingHttpClientConnectionManager poolingmgr = new PoolingHttpClientConnectionManager(RegistryBuilder.create().register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", sSLConnectionSocketFactory).build(), null, null, this.dnsResolver, this.connTimeToLive, (this.connTimeToLiveTimeUnit != null) ? this.connTimeToLiveTimeUnit : TimeUnit.MILLISECONDS);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  994 */       if (this.defaultSocketConfig != null) {
/*  995 */         poolingmgr.setDefaultSocketConfig(this.defaultSocketConfig);
/*      */       }
/*  997 */       if (this.defaultConnectionConfig != null) {
/*  998 */         poolingmgr.setDefaultConnectionConfig(this.defaultConnectionConfig);
/*      */       }
/* 1000 */       if (this.systemProperties) {
/* 1001 */         String s = System.getProperty("http.keepAlive", "true");
/* 1002 */         if ("true".equalsIgnoreCase(s)) {
/* 1003 */           s = System.getProperty("http.maxConnections", "5");
/* 1004 */           int max = Integer.parseInt(s);
/* 1005 */           poolingmgr.setDefaultMaxPerRoute(max);
/* 1006 */           poolingmgr.setMaxTotal(2 * max);
/*      */         } 
/*      */       } 
/* 1009 */       if (this.maxConnTotal > 0) {
/* 1010 */         poolingmgr.setMaxTotal(this.maxConnTotal);
/*      */       }
/* 1012 */       if (this.maxConnPerRoute > 0) {
/* 1013 */         poolingmgr.setDefaultMaxPerRoute(this.maxConnPerRoute);
/*      */       }
/* 1015 */       poolingHttpClientConnectionManager = poolingmgr;
/*      */     } 
/* 1017 */     ConnectionReuseStrategy reuseStrategyCopy = this.reuseStrategy;
/* 1018 */     if (reuseStrategyCopy == null) {
/* 1019 */       if (this.systemProperties) {
/* 1020 */         String s = System.getProperty("http.keepAlive", "true");
/* 1021 */         if ("true".equalsIgnoreCase(s)) {
/* 1022 */           defaultClientConnectionReuseStrategy = DefaultClientConnectionReuseStrategy.INSTANCE;
/*      */         } else {
/* 1024 */           NoConnectionReuseStrategy noConnectionReuseStrategy = NoConnectionReuseStrategy.INSTANCE;
/*      */         } 
/*      */       } else {
/* 1027 */         defaultClientConnectionReuseStrategy = DefaultClientConnectionReuseStrategy.INSTANCE;
/*      */       } 
/*      */     }
/* 1030 */     ConnectionKeepAliveStrategy keepAliveStrategyCopy = this.keepAliveStrategy;
/* 1031 */     if (keepAliveStrategyCopy == null) {
/* 1032 */       keepAliveStrategyCopy = DefaultConnectionKeepAliveStrategy.INSTANCE;
/*      */     }
/* 1034 */     AuthenticationStrategy targetAuthStrategyCopy = this.targetAuthStrategy;
/* 1035 */     if (targetAuthStrategyCopy == null) {
/* 1036 */       targetAuthStrategyCopy = TargetAuthenticationStrategy.INSTANCE;
/*      */     }
/* 1038 */     AuthenticationStrategy proxyAuthStrategyCopy = this.proxyAuthStrategy;
/* 1039 */     if (proxyAuthStrategyCopy == null) {
/* 1040 */       proxyAuthStrategyCopy = ProxyAuthenticationStrategy.INSTANCE;
/*      */     }
/* 1042 */     UserTokenHandler userTokenHandlerCopy = this.userTokenHandler;
/* 1043 */     if (userTokenHandlerCopy == null) {
/* 1044 */       if (!this.connectionStateDisabled) {
/* 1045 */         userTokenHandlerCopy = DefaultUserTokenHandler.INSTANCE;
/*      */       } else {
/* 1047 */         userTokenHandlerCopy = NoopUserTokenHandler.INSTANCE;
/*      */       } 
/*      */     }
/*      */     
/* 1051 */     String userAgentCopy = this.userAgent;
/* 1052 */     if (userAgentCopy == null) {
/* 1053 */       if (this.systemProperties) {
/* 1054 */         userAgentCopy = System.getProperty("http.agent");
/*      */       }
/* 1056 */       if (userAgentCopy == null && !this.defaultUserAgentDisabled) {
/* 1057 */         userAgentCopy = VersionInfo.getUserAgent("Apache-HttpClient", "org.apache.http.client", getClass());
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 1062 */     ClientExecChain execChain = createMainExec(requestExecCopy, (HttpClientConnectionManager)poolingHttpClientConnectionManager, (ConnectionReuseStrategy)defaultClientConnectionReuseStrategy, keepAliveStrategyCopy, (HttpProcessor)new ImmutableHttpProcessor(new HttpRequestInterceptor[] { (HttpRequestInterceptor)new RequestTargetHost(), (HttpRequestInterceptor)new RequestUserAgent(userAgentCopy) }, ), targetAuthStrategyCopy, proxyAuthStrategyCopy, userTokenHandlerCopy);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1072 */     execChain = decorateMainExec(execChain);
/*      */     
/* 1074 */     HttpProcessor httpprocessorCopy = this.httpprocessor;
/* 1075 */     if (httpprocessorCopy == null) {
/*      */       
/* 1077 */       HttpProcessorBuilder b = HttpProcessorBuilder.create();
/* 1078 */       if (this.requestFirst != null) {
/* 1079 */         for (HttpRequestInterceptor i : this.requestFirst) {
/* 1080 */           b.addFirst(i);
/*      */         }
/*      */       }
/* 1083 */       if (this.responseFirst != null) {
/* 1084 */         for (HttpResponseInterceptor i : this.responseFirst) {
/* 1085 */           b.addFirst(i);
/*      */         }
/*      */       }
/* 1088 */       b.addAll(new HttpRequestInterceptor[] { (HttpRequestInterceptor)new RequestDefaultHeaders(this.defaultHeaders), (HttpRequestInterceptor)new RequestContent(), (HttpRequestInterceptor)new RequestTargetHost(), (HttpRequestInterceptor)new RequestClientConnControl(), (HttpRequestInterceptor)new RequestUserAgent(userAgentCopy), (HttpRequestInterceptor)new RequestExpectContinue() });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1095 */       if (!this.cookieManagementDisabled) {
/* 1096 */         b.add((HttpRequestInterceptor)new RequestAddCookies());
/*      */       }
/* 1098 */       if (!this.contentCompressionDisabled) {
/* 1099 */         if (this.contentDecoderMap != null) {
/* 1100 */           List<String> encodings = new ArrayList<String>(this.contentDecoderMap.keySet());
/* 1101 */           Collections.sort(encodings);
/* 1102 */           b.add((HttpRequestInterceptor)new RequestAcceptEncoding(encodings));
/*      */         } else {
/* 1104 */           b.add((HttpRequestInterceptor)new RequestAcceptEncoding());
/*      */         } 
/*      */       }
/* 1107 */       if (!this.authCachingDisabled) {
/* 1108 */         b.add((HttpRequestInterceptor)new RequestAuthCache());
/*      */       }
/* 1110 */       if (!this.cookieManagementDisabled) {
/* 1111 */         b.add((HttpResponseInterceptor)new ResponseProcessCookies());
/*      */       }
/* 1113 */       if (!this.contentCompressionDisabled) {
/* 1114 */         if (this.contentDecoderMap != null) {
/* 1115 */           RegistryBuilder<InputStreamFactory> b2 = RegistryBuilder.create();
/* 1116 */           for (Map.Entry<String, InputStreamFactory> entry : this.contentDecoderMap.entrySet()) {
/* 1117 */             b2.register(entry.getKey(), entry.getValue());
/*      */           }
/* 1119 */           b.add((HttpResponseInterceptor)new ResponseContentEncoding((Lookup)b2.build()));
/*      */         } else {
/* 1121 */           b.add((HttpResponseInterceptor)new ResponseContentEncoding());
/*      */         } 
/*      */       }
/* 1124 */       if (this.requestLast != null) {
/* 1125 */         for (HttpRequestInterceptor i : this.requestLast) {
/* 1126 */           b.addLast(i);
/*      */         }
/*      */       }
/* 1129 */       if (this.responseLast != null) {
/* 1130 */         for (HttpResponseInterceptor i : this.responseLast) {
/* 1131 */           b.addLast(i);
/*      */         }
/*      */       }
/* 1134 */       httpprocessorCopy = b.build();
/*      */     } 
/* 1136 */     ProtocolExec protocolExec = new ProtocolExec(execChain, httpprocessorCopy);
/*      */     
/* 1138 */     ClientExecChain clientExecChain1 = decorateProtocolExec((ClientExecChain)protocolExec);
/*      */ 
/*      */     
/* 1141 */     if (!this.automaticRetriesDisabled) {
/* 1142 */       HttpRequestRetryHandler retryHandlerCopy = this.retryHandler;
/* 1143 */       if (retryHandlerCopy == null) {
/* 1144 */         retryHandlerCopy = DefaultHttpRequestRetryHandler.INSTANCE;
/*      */       }
/* 1146 */       retryExec = new RetryExec(clientExecChain1, retryHandlerCopy);
/*      */     } 
/*      */     
/* 1149 */     HttpRoutePlanner routePlannerCopy = this.routePlanner;
/* 1150 */     if (routePlannerCopy == null) {
/* 1151 */       DefaultSchemePortResolver defaultSchemePortResolver; SchemePortResolver schemePortResolverCopy = this.schemePortResolver;
/* 1152 */       if (schemePortResolverCopy == null) {
/* 1153 */         defaultSchemePortResolver = DefaultSchemePortResolver.INSTANCE;
/*      */       }
/* 1155 */       if (this.proxy != null) {
/* 1156 */         DefaultProxyRoutePlanner defaultProxyRoutePlanner = new DefaultProxyRoutePlanner(this.proxy, (SchemePortResolver)defaultSchemePortResolver);
/* 1157 */       } else if (this.systemProperties) {
/* 1158 */         SystemDefaultRoutePlanner systemDefaultRoutePlanner = new SystemDefaultRoutePlanner((SchemePortResolver)defaultSchemePortResolver, ProxySelector.getDefault());
/*      */       } else {
/*      */         
/* 1161 */         defaultRoutePlanner = new DefaultRoutePlanner((SchemePortResolver)defaultSchemePortResolver);
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1166 */     ServiceUnavailableRetryStrategy serviceUnavailStrategyCopy = this.serviceUnavailStrategy;
/* 1167 */     if (serviceUnavailStrategyCopy != null) {
/* 1168 */       serviceUnavailableRetryExec = new ServiceUnavailableRetryExec((ClientExecChain)retryExec, serviceUnavailStrategyCopy);
/*      */     }
/*      */ 
/*      */     
/* 1172 */     if (!this.redirectHandlingDisabled) {
/* 1173 */       RedirectStrategy redirectStrategyCopy = this.redirectStrategy;
/* 1174 */       if (redirectStrategyCopy == null) {
/* 1175 */         redirectStrategyCopy = DefaultRedirectStrategy.INSTANCE;
/*      */       }
/* 1177 */       redirectExec = new RedirectExec((ClientExecChain)serviceUnavailableRetryExec, (HttpRoutePlanner)defaultRoutePlanner, redirectStrategyCopy);
/*      */     } 
/*      */ 
/*      */     
/* 1181 */     if (this.backoffManager != null && this.connectionBackoffStrategy != null) {
/* 1182 */       backoffStrategyExec = new BackoffStrategyExec((ClientExecChain)redirectExec, this.connectionBackoffStrategy, this.backoffManager);
/*      */     }
/*      */     
/* 1185 */     Lookup<AuthSchemeProvider> authSchemeRegistryCopy = this.authSchemeRegistry;
/* 1186 */     if (authSchemeRegistryCopy == null) {
/* 1187 */       registry = RegistryBuilder.create().register("Basic", new BasicSchemeFactory()).register("Digest", new DigestSchemeFactory()).register("NTLM", new NTLMSchemeFactory()).register("Negotiate", new SPNegoSchemeFactory()).register("Kerberos", new KerberosSchemeFactory()).build();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1195 */     Lookup<CookieSpecProvider> cookieSpecRegistryCopy = this.cookieSpecRegistry;
/* 1196 */     if (cookieSpecRegistryCopy == null) {
/* 1197 */       cookieSpecRegistryCopy = CookieSpecRegistries.createDefault(publicSuffixMatcherCopy);
/*      */     }
/*      */     
/* 1200 */     CookieStore defaultCookieStore = this.cookieStore;
/* 1201 */     if (defaultCookieStore == null) {
/* 1202 */       defaultCookieStore = new BasicCookieStore();
/*      */     }
/*      */     
/* 1205 */     CredentialsProvider defaultCredentialsProvider = this.credentialsProvider;
/* 1206 */     if (defaultCredentialsProvider == null) {
/* 1207 */       if (this.systemProperties) {
/* 1208 */         defaultCredentialsProvider = new SystemDefaultCredentialsProvider();
/*      */       } else {
/* 1210 */         defaultCredentialsProvider = new BasicCredentialsProvider();
/*      */       } 
/*      */     }
/*      */     
/* 1214 */     List<Closeable> closeablesCopy = (this.closeables != null) ? new ArrayList<Closeable>(this.closeables) : null;
/* 1215 */     if (!this.connManagerShared) {
/* 1216 */       if (closeablesCopy == null) {
/* 1217 */         closeablesCopy = new ArrayList<Closeable>(1);
/*      */       }
/* 1219 */       final PoolingHttpClientConnectionManager cm = poolingHttpClientConnectionManager;
/*      */       
/* 1221 */       if (this.evictExpiredConnections || this.evictIdleConnections) {
/* 1222 */         final IdleConnectionEvictor connectionEvictor = new IdleConnectionEvictor((HttpClientConnectionManager)poolingHttpClientConnectionManager1, (this.maxIdleTime > 0L) ? this.maxIdleTime : 10L, (this.maxIdleTimeUnit != null) ? this.maxIdleTimeUnit : TimeUnit.SECONDS, this.maxIdleTime, this.maxIdleTimeUnit);
/*      */ 
/*      */         
/* 1225 */         closeablesCopy.add(new Closeable()
/*      */             {
/*      */               public void close() throws IOException
/*      */               {
/* 1229 */                 connectionEvictor.shutdown();
/*      */                 try {
/* 1231 */                   connectionEvictor.awaitTermination(1L, TimeUnit.SECONDS);
/* 1232 */                 } catch (InterruptedException interrupted) {
/* 1233 */                   Thread.currentThread().interrupt();
/*      */                 } 
/*      */               }
/*      */             });
/*      */         
/* 1238 */         connectionEvictor.start();
/*      */       } 
/* 1240 */       closeablesCopy.add(new Closeable()
/*      */           {
/*      */             public void close() throws IOException
/*      */             {
/* 1244 */               cm.shutdown();
/*      */             }
/*      */           });
/*      */     } 
/*      */ 
/*      */     
/* 1250 */     return new InternalHttpClient((ClientExecChain)backoffStrategyExec, (HttpClientConnectionManager)poolingHttpClientConnectionManager, (HttpRoutePlanner)defaultRoutePlanner, cookieSpecRegistryCopy, (Lookup<AuthSchemeProvider>)registry, defaultCookieStore, defaultCredentialsProvider, (this.defaultRequestConfig != null) ? this.defaultRequestConfig : RequestConfig.DEFAULT, closeablesCopy); }
/*      */ 
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\client\HttpClientBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */