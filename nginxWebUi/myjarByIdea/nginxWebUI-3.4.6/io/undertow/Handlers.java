package io.undertow;

import io.undertow.attribute.ExchangeAttribute;
import io.undertow.predicate.Predicate;
import io.undertow.predicate.PredicateParser;
import io.undertow.predicate.PredicatesHandler;
import io.undertow.server.HttpHandler;
import io.undertow.server.JvmRouteHandler;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.AccessControlListHandler;
import io.undertow.server.handlers.DateHandler;
import io.undertow.server.handlers.DisableCacheHandler;
import io.undertow.server.handlers.ExceptionHandler;
import io.undertow.server.handlers.GracefulShutdownHandler;
import io.undertow.server.handlers.HttpContinueAcceptingHandler;
import io.undertow.server.handlers.HttpContinueReadHandler;
import io.undertow.server.handlers.HttpTraceHandler;
import io.undertow.server.handlers.IPAddressAccessControlHandler;
import io.undertow.server.handlers.LearningPushHandler;
import io.undertow.server.handlers.NameVirtualHostHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.PathTemplateHandler;
import io.undertow.server.handlers.PredicateContextHandler;
import io.undertow.server.handlers.PredicateHandler;
import io.undertow.server.handlers.ProxyPeerAddressHandler;
import io.undertow.server.handlers.RedirectHandler;
import io.undertow.server.handlers.RequestDumpingHandler;
import io.undertow.server.handlers.RequestLimit;
import io.undertow.server.handlers.RequestLimitingHandler;
import io.undertow.server.handlers.ResponseRateLimitingHandler;
import io.undertow.server.handlers.SetAttributeHandler;
import io.undertow.server.handlers.SetErrorHandler;
import io.undertow.server.handlers.SetHeaderHandler;
import io.undertow.server.handlers.URLDecodingHandler;
import io.undertow.server.handlers.builder.PredicatedHandler;
import io.undertow.server.handlers.proxy.ProxyClient;
import io.undertow.server.handlers.proxy.ProxyHandler;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.server.handlers.sse.ServerSentEventConnectionCallback;
import io.undertow.server.handlers.sse.ServerSentEventHandler;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.WebSocketProtocolHandshakeHandler;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Handlers {
   public static PathHandler path(HttpHandler defaultHandler) {
      return new PathHandler(defaultHandler);
   }

   public static PathHandler path() {
      return new PathHandler();
   }

   public static PathTemplateHandler pathTemplate() {
      return new PathTemplateHandler();
   }

   public static RoutingHandler routing(boolean rewriteQueryParams) {
      return new RoutingHandler(rewriteQueryParams);
   }

   public static RoutingHandler routing() {
      return new RoutingHandler();
   }

   public static PathTemplateHandler pathTemplate(boolean rewriteQueryParams) {
      return new PathTemplateHandler(rewriteQueryParams);
   }

   public static NameVirtualHostHandler virtualHost() {
      return new NameVirtualHostHandler();
   }

   public static NameVirtualHostHandler virtualHost(HttpHandler defaultHandler) {
      return (new NameVirtualHostHandler()).setDefaultHandler(defaultHandler);
   }

   public static NameVirtualHostHandler virtualHost(HttpHandler hostHandler, String... hostnames) {
      NameVirtualHostHandler handler = new NameVirtualHostHandler();
      String[] var3 = hostnames;
      int var4 = hostnames.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String host = var3[var5];
         handler.addHost(host, hostHandler);
      }

      return handler;
   }

   public static NameVirtualHostHandler virtualHost(HttpHandler defaultHandler, HttpHandler hostHandler, String... hostnames) {
      return virtualHost(hostHandler, hostnames).setDefaultHandler(defaultHandler);
   }

   public static WebSocketProtocolHandshakeHandler websocket(WebSocketConnectionCallback sessionHandler) {
      return new WebSocketProtocolHandshakeHandler(sessionHandler);
   }

   public static WebSocketProtocolHandshakeHandler websocket(WebSocketConnectionCallback sessionHandler, HttpHandler next) {
      return new WebSocketProtocolHandshakeHandler(sessionHandler, next);
   }

   public static ServerSentEventHandler serverSentEvents(ServerSentEventConnectionCallback callback) {
      return new ServerSentEventHandler(callback);
   }

   public static ServerSentEventHandler serverSentEvents() {
      return new ServerSentEventHandler();
   }

   public static ResourceHandler resource(ResourceManager resourceManager) {
      return (new ResourceHandler(resourceManager)).setDirectoryListingEnabled(false);
   }

   public static RedirectHandler redirect(String location) {
      return new RedirectHandler(location);
   }

   public static HttpTraceHandler trace(HttpHandler next) {
      return new HttpTraceHandler(next);
   }

   /** @deprecated */
   @Deprecated
   public static DateHandler date(HttpHandler next) {
      return new DateHandler(next);
   }

   public static PredicateHandler predicate(Predicate predicate, HttpHandler trueHandler, HttpHandler falseHandler) {
      return new PredicateHandler(predicate, trueHandler, falseHandler);
   }

   public static HttpHandler predicateContext(HttpHandler next) {
      return new PredicateContextHandler(next);
   }

   public static PredicatesHandler predicates(List<PredicatedHandler> handlers, HttpHandler next) {
      PredicatesHandler predicatesHandler = new PredicatesHandler(next);
      Iterator var3 = handlers.iterator();

      while(var3.hasNext()) {
         PredicatedHandler handler = (PredicatedHandler)var3.next();
         predicatesHandler.addPredicatedHandler(handler);
      }

      return predicatesHandler;
   }

   public static SetHeaderHandler header(HttpHandler next, String headerName, String headerValue) {
      return new SetHeaderHandler(next, headerName, headerValue);
   }

   public static SetHeaderHandler header(HttpHandler next, String headerName, ExchangeAttribute headerValue) {
      return new SetHeaderHandler(next, headerName, headerValue);
   }

   public static final IPAddressAccessControlHandler ipAccessControl(HttpHandler next, boolean defaultAllow) {
      return (new IPAddressAccessControlHandler(next)).setDefaultAllow(defaultAllow);
   }

   public static final AccessControlListHandler acl(HttpHandler next, boolean defaultAllow, ExchangeAttribute attribute) {
      return (new AccessControlListHandler(next, attribute)).setDefaultAllow(defaultAllow);
   }

   public static final HttpContinueReadHandler httpContinueRead(HttpHandler next) {
      return new HttpContinueReadHandler(next);
   }

   public static final HttpContinueAcceptingHandler httpContinueAccepting(HttpHandler next, Predicate accept) {
      return new HttpContinueAcceptingHandler(next, accept);
   }

   public static final HttpContinueAcceptingHandler httpContinueAccepting(HttpHandler next) {
      return new HttpContinueAcceptingHandler(next);
   }

   public static final URLDecodingHandler urlDecoding(HttpHandler next, String charset) {
      return new URLDecodingHandler(next, charset);
   }

   public static SetAttributeHandler setAttribute(HttpHandler next, String attribute, String value, ClassLoader classLoader) {
      return new SetAttributeHandler(next, attribute, value, classLoader);
   }

   public static HttpHandler rewrite(String condition, String target, ClassLoader classLoader, HttpHandler next) {
      return predicateContext(predicate(PredicateParser.parse(condition, classLoader), setAttribute(next, "%R", target, classLoader), next));
   }

   public static HttpHandler urlDecodingHandler(String charset, HttpHandler next) {
      return new URLDecodingHandler(next, charset);
   }

   public static GracefulShutdownHandler gracefulShutdown(HttpHandler next) {
      return new GracefulShutdownHandler(next);
   }

   public static ProxyPeerAddressHandler proxyPeerAddress(HttpHandler next) {
      return new ProxyPeerAddressHandler(next);
   }

   public static JvmRouteHandler jvmRoute(String sessionCookieName, String jvmRoute, HttpHandler next) {
      return new JvmRouteHandler(next, sessionCookieName, jvmRoute);
   }

   public static RequestLimitingHandler requestLimitingHandler(int maxRequest, int queueSize, HttpHandler next) {
      return new RequestLimitingHandler(maxRequest, queueSize, next);
   }

   public static RequestLimitingHandler requestLimitingHandler(RequestLimit requestLimit, HttpHandler next) {
      return new RequestLimitingHandler(requestLimit, next);
   }

   public static ProxyHandler proxyHandler(ProxyClient proxyClient, int maxRequestTime, HttpHandler next) {
      return ProxyHandler.builder().setProxyClient(proxyClient).setNext(next).setMaxRequestTime(maxRequestTime).build();
   }

   public static ProxyHandler proxyHandler(ProxyClient proxyClient, HttpHandler next) {
      return ProxyHandler.builder().setProxyClient(proxyClient).setNext(next).build();
   }

   public static ProxyHandler proxyHandler(ProxyClient proxyClient) {
      return ProxyHandler.builder().setProxyClient(proxyClient).build();
   }

   public static HttpHandler disableCache(HttpHandler next) {
      return new DisableCacheHandler(next);
   }

   public static HttpHandler requestDump(HttpHandler next) {
      return new RequestDumpingHandler(next);
   }

   public static ExceptionHandler exceptionHandler(HttpHandler next) {
      return new ExceptionHandler(next);
   }

   public static ResponseRateLimitingHandler responseRateLimitingHandler(HttpHandler next, int bytes, long time, TimeUnit timeUnit) {
      return new ResponseRateLimitingHandler(next, bytes, time, timeUnit);
   }

   public static LearningPushHandler learningPushHandler(int maxEntries, int maxAge, HttpHandler next) {
      return new LearningPushHandler(maxEntries, maxAge, next);
   }

   public static SetErrorHandler setErrorHandler(int responseCode, HttpHandler next) {
      return new SetErrorHandler(next, responseCode);
   }

   public static LearningPushHandler learningPushHandler(int maxEntries, HttpHandler next) {
      return new LearningPushHandler(maxEntries, -1, next);
   }

   private Handlers() {
   }

   public static void handlerNotNull(HttpHandler handler) {
      if (handler == null) {
         throw UndertowMessages.MESSAGES.handlerCannotBeNull();
      }
   }
}
