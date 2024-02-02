/*     */ package io.undertow.server.handlers.proxy;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.client.ClientConnection;
/*     */ import io.undertow.client.UndertowClient;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.ServerConnection;
/*     */ import io.undertow.server.handlers.Cookie;
/*     */ import io.undertow.util.AttachmentKey;
/*     */ import io.undertow.util.AttachmentList;
/*     */ import io.undertow.util.CopyOnWriteMap;
/*     */ import java.io.Closeable;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.URI;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.CopyOnWriteArraySet;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.ssl.XnioSsl;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LoadBalancingProxyClient
/*     */   implements ProxyClient
/*     */ {
/*  62 */   private final AttachmentKey<ExclusiveConnectionHolder> exclusiveConnectionKey = AttachmentKey.create(ExclusiveConnectionHolder.class);
/*     */   
/*  64 */   private static final AttachmentKey<AttachmentList<Host>> ATTEMPTED_HOSTS = AttachmentKey.createList(Host.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  69 */   private volatile int problemServerRetry = 10;
/*     */   
/*  71 */   private final Set<String> sessionCookieNames = new CopyOnWriteArraySet<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  76 */   private volatile int connectionsPerThread = 10;
/*  77 */   private volatile int maxQueueSize = 0;
/*  78 */   private volatile int softMaxConnectionsPerThread = 5;
/*  79 */   private volatile int ttl = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  84 */   private volatile Host[] hosts = new Host[0];
/*     */   
/*     */   private final HostSelector hostSelector;
/*     */   
/*     */   private final UndertowClient client;
/*  89 */   private final Map<String, Host> routes = (Map<String, Host>)new CopyOnWriteMap();
/*  90 */   private RouteIteratorFactory routeIteratorFactory = new RouteIteratorFactory(RouteParsingStrategy.SINGLE, RouteIteratorFactory.ParsingCompatibility.MOD_JK);
/*     */   
/*     */   private final ExclusivityChecker exclusivityChecker;
/*     */   
/*  94 */   private static final ProxyClient.ProxyTarget PROXY_TARGET = new ProxyClient.ProxyTarget() {
/*     */     
/*     */     };
/*     */   
/*     */   public List<ProxyClient.ProxyTarget> getAllTargets() {
/*  99 */     List<ProxyClient.ProxyTarget> arr = new ArrayList<>();
/* 100 */     for (Host host : this.hosts) {
/* 101 */       ProxyClient.HostProxyTarget proxyTarget = new ProxyClient.HostProxyTarget() { LoadBalancingProxyClient.Host host;
/*     */           
/*     */           public void setHost(LoadBalancingProxyClient.Host host) {
/* 104 */             this.host = host;
/*     */           }
/*     */           public String toString() {
/* 107 */             return this.host.getUri().toString();
/*     */           } }
/*     */         ;
/* 110 */       proxyTarget.setHost(host);
/* 111 */       arr.add(proxyTarget);
/*     */     } 
/* 113 */     return arr;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public LoadBalancingProxyClient() {
/* 119 */     this(UndertowClient.getInstance());
/*     */   }
/*     */   
/*     */   public LoadBalancingProxyClient(UndertowClient client) {
/* 123 */     this(client, null, null);
/*     */   }
/*     */   
/*     */   public LoadBalancingProxyClient(ExclusivityChecker client) {
/* 127 */     this(UndertowClient.getInstance(), client, null);
/*     */   }
/*     */   
/*     */   public LoadBalancingProxyClient(UndertowClient client, ExclusivityChecker exclusivityChecker) {
/* 131 */     this(client, exclusivityChecker, null);
/*     */   }
/*     */   
/*     */   public LoadBalancingProxyClient(UndertowClient client, ExclusivityChecker exclusivityChecker, HostSelector hostSelector) {
/* 135 */     this.client = client;
/* 136 */     this.exclusivityChecker = exclusivityChecker;
/* 137 */     this.sessionCookieNames.add("JSESSIONID");
/* 138 */     if (hostSelector == null) {
/* 139 */       this.hostSelector = new RoundRobinHostSelector();
/*     */     } else {
/* 141 */       this.hostSelector = hostSelector;
/*     */     } 
/*     */   }
/*     */   
/*     */   public LoadBalancingProxyClient addSessionCookieName(String sessionCookieName) {
/* 146 */     this.sessionCookieNames.add(sessionCookieName);
/* 147 */     return this;
/*     */   }
/*     */   
/*     */   public LoadBalancingProxyClient removeSessionCookieName(String sessionCookieName) {
/* 151 */     this.sessionCookieNames.remove(sessionCookieName);
/* 152 */     return this;
/*     */   }
/*     */   
/*     */   public LoadBalancingProxyClient setProblemServerRetry(int problemServerRetry) {
/* 156 */     this.problemServerRetry = problemServerRetry;
/* 157 */     return this;
/*     */   }
/*     */   
/*     */   public int getProblemServerRetry() {
/* 161 */     return this.problemServerRetry;
/*     */   }
/*     */   
/*     */   public int getConnectionsPerThread() {
/* 165 */     return this.connectionsPerThread;
/*     */   }
/*     */   
/*     */   public LoadBalancingProxyClient setConnectionsPerThread(int connectionsPerThread) {
/* 169 */     this.connectionsPerThread = connectionsPerThread;
/* 170 */     return this;
/*     */   }
/*     */   
/*     */   public int getMaxQueueSize() {
/* 174 */     return this.maxQueueSize;
/*     */   }
/*     */   
/*     */   public LoadBalancingProxyClient setMaxQueueSize(int maxQueueSize) {
/* 178 */     this.maxQueueSize = maxQueueSize;
/* 179 */     return this;
/*     */   }
/*     */   
/*     */   public LoadBalancingProxyClient setTtl(int ttl) {
/* 183 */     this.ttl = ttl;
/* 184 */     return this;
/*     */   }
/*     */   
/*     */   public LoadBalancingProxyClient setSoftMaxConnectionsPerThread(int softMaxConnectionsPerThread) {
/* 188 */     this.softMaxConnectionsPerThread = softMaxConnectionsPerThread;
/* 189 */     return this;
/*     */   }
/*     */   
/*     */   public LoadBalancingProxyClient setRouteParsingStrategy(RouteParsingStrategy routeParsingStrategy) {
/* 193 */     this.routeIteratorFactory = new RouteIteratorFactory(routeParsingStrategy, RouteIteratorFactory.ParsingCompatibility.MOD_JK, null);
/* 194 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LoadBalancingProxyClient setRankedRoutingDelimiter(String rankedRoutingDelimiter) {
/* 201 */     this.routeIteratorFactory = new RouteIteratorFactory(RouteParsingStrategy.RANKED, RouteIteratorFactory.ParsingCompatibility.MOD_JK, rankedRoutingDelimiter);
/* 202 */     return this;
/*     */   }
/*     */   
/*     */   public synchronized LoadBalancingProxyClient addHost(URI host) {
/* 206 */     return addHost(host, null, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized LoadBalancingProxyClient addHost(URI host, XnioSsl ssl) {
/* 211 */     return addHost(host, null, ssl);
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized LoadBalancingProxyClient addHost(URI host, String jvmRoute) {
/* 216 */     return addHost(host, jvmRoute, null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized LoadBalancingProxyClient addHost(URI host, String jvmRoute, XnioSsl ssl) {
/* 222 */     Host h = new Host(jvmRoute, null, host, ssl, OptionMap.EMPTY);
/* 223 */     Host[] existing = this.hosts;
/* 224 */     Host[] newHosts = new Host[existing.length + 1];
/* 225 */     System.arraycopy(existing, 0, newHosts, 0, existing.length);
/* 226 */     newHosts[existing.length] = h;
/* 227 */     this.hosts = newHosts;
/* 228 */     if (jvmRoute != null) {
/* 229 */       this.routes.put(jvmRoute, h);
/*     */     }
/* 231 */     return this;
/*     */   }
/*     */   
/*     */   public synchronized LoadBalancingProxyClient addHost(URI host, String jvmRoute, XnioSsl ssl, OptionMap options) {
/* 235 */     return addHost(null, host, jvmRoute, ssl, options);
/*     */   }
/*     */   
/*     */   public synchronized LoadBalancingProxyClient addHost(InetSocketAddress bindAddress, URI host, String jvmRoute, XnioSsl ssl, OptionMap options) {
/* 239 */     Host h = new Host(jvmRoute, bindAddress, host, ssl, options);
/* 240 */     Host[] existing = this.hosts;
/* 241 */     Host[] newHosts = new Host[existing.length + 1];
/* 242 */     System.arraycopy(existing, 0, newHosts, 0, existing.length);
/* 243 */     newHosts[existing.length] = h;
/* 244 */     this.hosts = newHosts;
/* 245 */     if (jvmRoute != null) {
/* 246 */       this.routes.put(jvmRoute, h);
/*     */     }
/* 248 */     return this;
/*     */   }
/*     */   
/*     */   public synchronized LoadBalancingProxyClient removeHost(URI uri) {
/* 252 */     int found = -1;
/* 253 */     Host[] existing = this.hosts;
/* 254 */     Host removedHost = null;
/* 255 */     for (int i = 0; i < existing.length; i++) {
/* 256 */       if ((existing[i]).uri.equals(uri)) {
/* 257 */         found = i;
/* 258 */         removedHost = existing[i];
/*     */         break;
/*     */       } 
/*     */     } 
/* 262 */     if (found == -1) {
/* 263 */       return this;
/*     */     }
/* 265 */     Host[] newHosts = new Host[existing.length - 1];
/* 266 */     System.arraycopy(existing, 0, newHosts, 0, found);
/* 267 */     System.arraycopy(existing, found + 1, newHosts, found, existing.length - found - 1);
/* 268 */     this.hosts = newHosts;
/* 269 */     removedHost.connectionPool.close();
/* 270 */     if (removedHost.jvmRoute != null) {
/* 271 */       this.routes.remove(removedHost.jvmRoute);
/*     */     }
/* 273 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ProxyClient.ProxyTarget findTarget(HttpServerExchange exchange) {
/* 278 */     return PROXY_TARGET;
/*     */   }
/*     */ 
/*     */   
/*     */   public void getConnection(ProxyClient.ProxyTarget target, HttpServerExchange exchange, final ProxyCallback<ProxyConnection> callback, long timeout, TimeUnit timeUnit) {
/* 283 */     final ExclusiveConnectionHolder holder = (ExclusiveConnectionHolder)exchange.getConnection().getAttachment(this.exclusiveConnectionKey);
/* 284 */     if (holder != null && holder.connection.getConnection().isOpen()) {
/*     */       
/* 286 */       callback.completed(exchange, holder.connection);
/*     */       
/*     */       return;
/*     */     } 
/* 290 */     final Host host = selectHost(exchange);
/* 291 */     if (host == null) {
/* 292 */       callback.couldNotResolveBackend(exchange);
/*     */     } else {
/* 294 */       exchange.addToAttachmentList(ATTEMPTED_HOSTS, host);
/* 295 */       if (holder != null || (this.exclusivityChecker != null && this.exclusivityChecker.isExclusivityRequired(exchange))) {
/*     */ 
/*     */         
/* 298 */         host.connectionPool.connect(target, exchange, new ProxyCallback<ProxyConnection>()
/*     */             {
/*     */               public void completed(HttpServerExchange exchange, ProxyConnection result)
/*     */               {
/* 302 */                 if (holder != null) {
/* 303 */                   holder.connection = result;
/*     */                 } else {
/* 305 */                   final LoadBalancingProxyClient.ExclusiveConnectionHolder newHolder = new LoadBalancingProxyClient.ExclusiveConnectionHolder();
/* 306 */                   newHolder.connection = result;
/* 307 */                   ServerConnection connection = exchange.getConnection();
/* 308 */                   connection.putAttachment(LoadBalancingProxyClient.this.exclusiveConnectionKey, newHolder);
/* 309 */                   connection.addCloseListener(new ServerConnection.CloseListener()
/*     */                       {
/*     */                         public void closed(ServerConnection connection)
/*     */                         {
/* 313 */                           ClientConnection clientConnection = newHolder.connection.getConnection();
/* 314 */                           if (clientConnection.isOpen()) {
/* 315 */                             IoUtils.safeClose((Closeable)clientConnection);
/*     */                           }
/*     */                         }
/*     */                       });
/*     */                 } 
/* 320 */                 callback.completed(exchange, result);
/*     */               }
/*     */ 
/*     */               
/*     */               public void queuedRequestFailed(HttpServerExchange exchange) {
/* 325 */                 callback.queuedRequestFailed(exchange);
/*     */               }
/*     */ 
/*     */               
/*     */               public void failed(HttpServerExchange exchange) {
/* 330 */                 UndertowLogger.PROXY_REQUEST_LOGGER.proxyFailedToConnectToBackend(exchange.getRequestURI(), host.uri);
/* 331 */                 callback.failed(exchange);
/*     */               }
/*     */ 
/*     */               
/*     */               public void couldNotResolveBackend(HttpServerExchange exchange) {
/* 336 */                 callback.couldNotResolveBackend(exchange);
/*     */               }
/*     */             }timeout, timeUnit, true);
/*     */       } else {
/* 340 */         host.connectionPool.connect(target, exchange, callback, timeout, timeUnit, false);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   protected Host selectHost(HttpServerExchange exchange) {
/* 346 */     AttachmentList<Host> attempted = (AttachmentList<Host>)exchange.getAttachment(ATTEMPTED_HOSTS);
/* 347 */     Host[] hosts = this.hosts;
/* 348 */     if (hosts.length == 0) {
/* 349 */       return null;
/*     */     }
/*     */     
/* 352 */     Iterator<CharSequence> parsedRoutes = parseRoutes(exchange);
/* 353 */     while (parsedRoutes.hasNext()) {
/*     */       
/* 355 */       Host host1 = this.routes.get(((CharSequence)parsedRoutes.next()).toString());
/* 356 */       if (host1 != null && (
/* 357 */         attempted == null || !attempted.contains(host1))) {
/* 358 */         return host1;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 363 */     int host = this.hostSelector.selectHost(hosts);
/*     */     
/* 365 */     int startHost = host;
/* 366 */     Host full = null;
/* 367 */     Host problem = null;
/*     */     while (true) {
/* 369 */       Host selected = hosts[host];
/* 370 */       if (attempted == null || !attempted.contains(selected)) {
/* 371 */         ProxyConnectionPool.AvailabilityType available = selected.connectionPool.available();
/* 372 */         if (available == ProxyConnectionPool.AvailabilityType.AVAILABLE)
/* 373 */           return selected; 
/* 374 */         if (available == ProxyConnectionPool.AvailabilityType.FULL && full == null) {
/* 375 */           full = selected;
/* 376 */         } else if ((available == ProxyConnectionPool.AvailabilityType.PROBLEM || available == ProxyConnectionPool.AvailabilityType.FULL_QUEUE) && problem == null) {
/* 377 */           problem = selected;
/*     */         } 
/*     */       } 
/* 380 */       host = (host + 1) % hosts.length;
/* 381 */       if (host == startHost) {
/* 382 */         if (full != null) {
/* 383 */           return full;
/*     */         }
/* 385 */         if (problem != null) {
/* 386 */           return problem;
/*     */         }
/*     */         
/* 389 */         return null;
/*     */       } 
/*     */     } 
/*     */   } protected Iterator<CharSequence> parseRoutes(HttpServerExchange exchange) {
/* 393 */     for (String cookieName : this.sessionCookieNames) {
/* 394 */       for (Cookie cookie : exchange.requestCookies()) {
/* 395 */         if (cookieName.equals(cookie.getName())) {
/* 396 */           return this.routeIteratorFactory.iterator(cookie.getValue());
/*     */         }
/*     */       } 
/*     */     } 
/* 400 */     return this.routeIteratorFactory.iterator(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void closeCurrentConnections() {
/* 411 */     for (Host host : this.hosts)
/* 412 */       host.closeCurrentConnections(); 
/*     */   }
/*     */   
/*     */   public final class Host
/*     */     extends ConnectionPoolErrorHandler.SimpleConnectionPoolErrorHandler implements ConnectionPoolManager {
/*     */     final ProxyConnectionPool connectionPool;
/*     */     final String jvmRoute;
/*     */     final URI uri;
/*     */     final XnioSsl ssl;
/*     */     
/*     */     private Host(String jvmRoute, InetSocketAddress bindAddress, URI uri, XnioSsl ssl, OptionMap options) {
/* 423 */       this.connectionPool = new ProxyConnectionPool(this, bindAddress, uri, ssl, LoadBalancingProxyClient.this.client, options);
/* 424 */       this.jvmRoute = jvmRoute;
/* 425 */       this.uri = uri;
/* 426 */       this.ssl = ssl;
/*     */     }
/*     */ 
/*     */     
/*     */     public int getProblemServerRetry() {
/* 431 */       return LoadBalancingProxyClient.this.problemServerRetry;
/*     */     }
/*     */ 
/*     */     
/*     */     public int getMaxConnections() {
/* 436 */       return LoadBalancingProxyClient.this.connectionsPerThread;
/*     */     }
/*     */ 
/*     */     
/*     */     public int getMaxCachedConnections() {
/* 441 */       return LoadBalancingProxyClient.this.connectionsPerThread;
/*     */     }
/*     */ 
/*     */     
/*     */     public int getSMaxConnections() {
/* 446 */       return LoadBalancingProxyClient.this.softMaxConnectionsPerThread;
/*     */     }
/*     */ 
/*     */     
/*     */     public long getTtl() {
/* 451 */       return LoadBalancingProxyClient.this.ttl;
/*     */     }
/*     */ 
/*     */     
/*     */     public int getMaxQueueSize() {
/* 456 */       return LoadBalancingProxyClient.this.maxQueueSize;
/*     */     }
/*     */     
/*     */     public URI getUri() {
/* 460 */       return this.uri;
/*     */     }
/*     */     
/*     */     void closeCurrentConnections() {
/* 464 */       this.connectionPool.closeCurrentConnections();
/*     */     }
/*     */   }
/*     */   
/*     */   private static class ExclusiveConnectionHolder
/*     */   {
/*     */     private ProxyConnection connection;
/*     */     
/*     */     private ExclusiveConnectionHolder() {}
/*     */   }
/*     */   
/*     */   public static interface HostSelector {
/*     */     int selectHost(LoadBalancingProxyClient.Host[] param1ArrayOfHost);
/*     */   }
/*     */   
/*     */   static class RoundRobinHostSelector
/*     */     implements HostSelector {
/* 481 */     private final AtomicInteger currentHost = new AtomicInteger(0);
/*     */ 
/*     */     
/*     */     public int selectHost(LoadBalancingProxyClient.Host[] availableHosts) {
/* 485 */       return this.currentHost.incrementAndGet() % availableHosts.length;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\proxy\LoadBalancingProxyClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */