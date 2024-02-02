/*     */ package io.undertow;
/*     */ 
/*     */ import io.undertow.attribute.ExchangeAttribute;
/*     */ import io.undertow.predicate.Predicate;
/*     */ import io.undertow.predicate.PredicateParser;
/*     */ import io.undertow.predicate.PredicatesHandler;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.JvmRouteHandler;
/*     */ import io.undertow.server.RoutingHandler;
/*     */ import io.undertow.server.handlers.AccessControlListHandler;
/*     */ import io.undertow.server.handlers.DateHandler;
/*     */ import io.undertow.server.handlers.DisableCacheHandler;
/*     */ import io.undertow.server.handlers.ExceptionHandler;
/*     */ import io.undertow.server.handlers.GracefulShutdownHandler;
/*     */ import io.undertow.server.handlers.HttpContinueAcceptingHandler;
/*     */ import io.undertow.server.handlers.HttpContinueReadHandler;
/*     */ import io.undertow.server.handlers.HttpTraceHandler;
/*     */ import io.undertow.server.handlers.IPAddressAccessControlHandler;
/*     */ import io.undertow.server.handlers.LearningPushHandler;
/*     */ import io.undertow.server.handlers.NameVirtualHostHandler;
/*     */ import io.undertow.server.handlers.PathHandler;
/*     */ import io.undertow.server.handlers.PathTemplateHandler;
/*     */ import io.undertow.server.handlers.PredicateContextHandler;
/*     */ import io.undertow.server.handlers.PredicateHandler;
/*     */ import io.undertow.server.handlers.ProxyPeerAddressHandler;
/*     */ import io.undertow.server.handlers.RedirectHandler;
/*     */ import io.undertow.server.handlers.RequestDumpingHandler;
/*     */ import io.undertow.server.handlers.RequestLimit;
/*     */ import io.undertow.server.handlers.RequestLimitingHandler;
/*     */ import io.undertow.server.handlers.ResponseRateLimitingHandler;
/*     */ import io.undertow.server.handlers.SetAttributeHandler;
/*     */ import io.undertow.server.handlers.SetErrorHandler;
/*     */ import io.undertow.server.handlers.SetHeaderHandler;
/*     */ import io.undertow.server.handlers.URLDecodingHandler;
/*     */ import io.undertow.server.handlers.builder.PredicatedHandler;
/*     */ import io.undertow.server.handlers.proxy.ProxyClient;
/*     */ import io.undertow.server.handlers.proxy.ProxyHandler;
/*     */ import io.undertow.server.handlers.resource.ResourceHandler;
/*     */ import io.undertow.server.handlers.resource.ResourceManager;
/*     */ import io.undertow.server.handlers.sse.ServerSentEventConnectionCallback;
/*     */ import io.undertow.server.handlers.sse.ServerSentEventHandler;
/*     */ import io.undertow.websockets.WebSocketConnectionCallback;
/*     */ import io.undertow.websockets.WebSocketProtocolHandshakeHandler;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Handlers
/*     */ {
/*     */   public static PathHandler path(HttpHandler defaultHandler) {
/*  80 */     return new PathHandler(defaultHandler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static PathHandler path() {
/*  89 */     return new PathHandler();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static PathTemplateHandler pathTemplate() {
/*  97 */     return new PathTemplateHandler();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RoutingHandler routing(boolean rewriteQueryParams) {
/* 106 */     return new RoutingHandler(rewriteQueryParams);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RoutingHandler routing() {
/* 114 */     return new RoutingHandler();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static PathTemplateHandler pathTemplate(boolean rewriteQueryParams) {
/* 123 */     return new PathTemplateHandler(rewriteQueryParams);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static NameVirtualHostHandler virtualHost() {
/* 133 */     return new NameVirtualHostHandler();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static NameVirtualHostHandler virtualHost(HttpHandler defaultHandler) {
/* 142 */     return (new NameVirtualHostHandler()).setDefaultHandler(defaultHandler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static NameVirtualHostHandler virtualHost(HttpHandler hostHandler, String... hostnames) {
/* 153 */     NameVirtualHostHandler handler = new NameVirtualHostHandler();
/* 154 */     for (String host : hostnames) {
/* 155 */       handler.addHost(host, hostHandler);
/*     */     }
/* 157 */     return handler;
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
/*     */   public static NameVirtualHostHandler virtualHost(HttpHandler defaultHandler, HttpHandler hostHandler, String... hostnames) {
/* 169 */     return virtualHost(hostHandler, hostnames).setDefaultHandler(defaultHandler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static WebSocketProtocolHandshakeHandler websocket(WebSocketConnectionCallback sessionHandler) {
/* 177 */     return new WebSocketProtocolHandshakeHandler(sessionHandler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static WebSocketProtocolHandshakeHandler websocket(WebSocketConnectionCallback sessionHandler, HttpHandler next) {
/* 186 */     return new WebSocketProtocolHandshakeHandler(sessionHandler, next);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ServerSentEventHandler serverSentEvents(ServerSentEventConnectionCallback callback) {
/* 197 */     return new ServerSentEventHandler(callback);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ServerSentEventHandler serverSentEvents() {
/* 206 */     return new ServerSentEventHandler();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ResourceHandler resource(ResourceManager resourceManager) {
/* 215 */     return (new ResourceHandler(resourceManager)).setDirectoryListingEnabled(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RedirectHandler redirect(String location) {
/* 225 */     return new RedirectHandler(location);
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
/*     */   public static HttpTraceHandler trace(HttpHandler next) {
/* 239 */     return new HttpTraceHandler(next);
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
/*     */   @Deprecated
/*     */   public static DateHandler date(HttpHandler next) {
/* 252 */     return new DateHandler(next);
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
/*     */   public static PredicateHandler predicate(Predicate predicate, HttpHandler trueHandler, HttpHandler falseHandler) {
/* 267 */     return new PredicateHandler(predicate, trueHandler, falseHandler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static HttpHandler predicateContext(HttpHandler next) {
/* 275 */     return (HttpHandler)new PredicateContextHandler(next);
/*     */   }
/*     */   
/*     */   public static PredicatesHandler predicates(List<PredicatedHandler> handlers, HttpHandler next) {
/* 279 */     PredicatesHandler predicatesHandler = new PredicatesHandler(next);
/* 280 */     for (PredicatedHandler handler : handlers) {
/* 281 */       predicatesHandler.addPredicatedHandler(handler);
/*     */     }
/* 283 */     return predicatesHandler;
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
/*     */   public static SetHeaderHandler header(HttpHandler next, String headerName, String headerValue) {
/* 295 */     return new SetHeaderHandler(next, headerName, headerValue);
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
/*     */   public static SetHeaderHandler header(HttpHandler next, String headerName, ExchangeAttribute headerValue) {
/* 308 */     return new SetHeaderHandler(next, headerName, headerValue);
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
/*     */   public static final IPAddressAccessControlHandler ipAccessControl(HttpHandler next, boolean defaultAllow) {
/* 320 */     return (new IPAddressAccessControlHandler(next)).setDefaultAllow(defaultAllow);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final AccessControlListHandler acl(HttpHandler next, boolean defaultAllow, ExchangeAttribute attribute) {
/* 331 */     return (new AccessControlListHandler(next, attribute)).setDefaultAllow(defaultAllow);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final HttpContinueReadHandler httpContinueRead(HttpHandler next) {
/* 342 */     return new HttpContinueReadHandler(next);
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
/*     */   public static final HttpContinueAcceptingHandler httpContinueAccepting(HttpHandler next, Predicate accept) {
/* 356 */     return new HttpContinueAcceptingHandler(next, accept);
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
/*     */   public static final HttpContinueAcceptingHandler httpContinueAccepting(HttpHandler next) {
/* 369 */     return new HttpContinueAcceptingHandler(next);
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
/*     */   public static final URLDecodingHandler urlDecoding(HttpHandler next, String charset) {
/* 385 */     return new URLDecodingHandler(next, charset);
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
/*     */   public static SetAttributeHandler setAttribute(HttpHandler next, String attribute, String value, ClassLoader classLoader) {
/* 399 */     return new SetAttributeHandler(next, attribute, value, classLoader);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static HttpHandler rewrite(String condition, String target, ClassLoader classLoader, HttpHandler next) {
/* 410 */     return predicateContext((HttpHandler)predicate(PredicateParser.parse(condition, classLoader), (HttpHandler)setAttribute(next, "%R", target, classLoader), next));
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
/*     */   public static HttpHandler urlDecodingHandler(String charset, HttpHandler next) {
/* 423 */     return (HttpHandler)new URLDecodingHandler(next, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static GracefulShutdownHandler gracefulShutdown(HttpHandler next) {
/* 434 */     return new GracefulShutdownHandler(next);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ProxyPeerAddressHandler proxyPeerAddress(HttpHandler next) {
/* 444 */     return new ProxyPeerAddressHandler(next);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JvmRouteHandler jvmRoute(String sessionCookieName, String jvmRoute, HttpHandler next) {
/* 455 */     return new JvmRouteHandler(next, sessionCookieName, jvmRoute);
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
/*     */   public static RequestLimitingHandler requestLimitingHandler(int maxRequest, int queueSize, HttpHandler next) {
/* 467 */     return new RequestLimitingHandler(maxRequest, queueSize, next);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RequestLimitingHandler requestLimitingHandler(RequestLimit requestLimit, HttpHandler next) {
/* 478 */     return new RequestLimitingHandler(requestLimit, next);
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
/*     */   public static ProxyHandler proxyHandler(ProxyClient proxyClient, int maxRequestTime, HttpHandler next) {
/* 490 */     return ProxyHandler.builder().setProxyClient(proxyClient).setNext(next).setMaxRequestTime(maxRequestTime).build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ProxyHandler proxyHandler(ProxyClient proxyClient, HttpHandler next) {
/* 500 */     return ProxyHandler.builder().setProxyClient(proxyClient).setNext(next).build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ProxyHandler proxyHandler(ProxyClient proxyClient) {
/* 510 */     return ProxyHandler.builder().setProxyClient(proxyClient).build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static HttpHandler disableCache(HttpHandler next) {
/* 519 */     return (HttpHandler)new DisableCacheHandler(next);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static HttpHandler requestDump(HttpHandler next) {
/* 529 */     return (HttpHandler)new RequestDumpingHandler(next);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ExceptionHandler exceptionHandler(HttpHandler next) {
/* 538 */     return new ExceptionHandler(next);
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
/*     */   public static ResponseRateLimitingHandler responseRateLimitingHandler(HttpHandler next, int bytes, long time, TimeUnit timeUnit) {
/* 551 */     return new ResponseRateLimitingHandler(next, bytes, time, timeUnit);
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
/*     */   public static LearningPushHandler learningPushHandler(int maxEntries, int maxAge, HttpHandler next) {
/* 563 */     return new LearningPushHandler(maxEntries, maxAge, next);
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
/*     */   public static SetErrorHandler setErrorHandler(int responseCode, HttpHandler next) {
/* 575 */     return new SetErrorHandler(next, responseCode);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static LearningPushHandler learningPushHandler(int maxEntries, HttpHandler next) {
/* 586 */     return new LearningPushHandler(maxEntries, -1, next);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void handlerNotNull(HttpHandler handler) {
/* 594 */     if (handler == null)
/* 595 */       throw UndertowMessages.MESSAGES.handlerCannotBeNull(); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\Handlers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */